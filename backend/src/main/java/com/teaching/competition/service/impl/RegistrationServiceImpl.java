package com.teaching.competition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.RegistrationDTO;
import com.teaching.competition.dto.ReviewRejectDTO;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.Registration;
import com.teaching.competition.entity.Team;
import com.teaching.competition.entity.TeamAdvisor;
import com.teaching.competition.entity.TeamMember;
import com.teaching.competition.entity.Track;
import com.teaching.competition.entity.User;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.RegistrationMapper;
import com.teaching.competition.mapper.TeamAdvisorMapper;
import com.teaching.competition.mapper.TeamMapper;
import com.teaching.competition.mapper.TeamMemberMapper;
import com.teaching.competition.service.CompetitionService;
import com.teaching.competition.service.NotificationService;
import com.teaching.competition.service.RegistrationService;
import com.teaching.competition.service.UserService;
import com.teaching.competition.vo.RegistrationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {

    private final CompetitionService competitionService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamAdvisorMapper teamAdvisorMapper;
    private final com.teaching.competition.mapper.TrackMapper trackMapper;
    private final com.teaching.competition.service.CompetitionAdminService competitionAdminService;
    private final com.teaching.competition.service.LocalFileService localFileService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void applyRegistration(RegistrationDTO dto, Long userId) {
        Competition competition = competitionService.getById(dto.getCompetitionId());
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        ensureRegistrationOpen(competition);

        LambdaQueryWrapper<Registration> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Registration::getUserId, userId)
                .eq(Registration::getCompetitionId, dto.getCompetitionId());
        if (count(checkWrapper) > 0) {
            throw new BusinessException("您已报名该竞赛，请勿重复报名");
        }

        String competitionParticipationType = competition.getParticipationType();
        if (StrUtil.isBlank(competitionParticipationType)) {
            competitionParticipationType = "INDIVIDUAL";
        }

        if ("TEAM".equals(competitionParticipationType)) {
            if (dto.getTeamId() == null) {
                throw new BusinessException("团队赛必须关联一个团队");
            }
            Team team = teamMapper.selectById(dto.getTeamId());
            if (team == null || !"ACTIVE".equals(team.getStatus())) {
                throw new BusinessException("团队不存在或已解散");
            }
            if (!team.getCompetitionId().equals(dto.getCompetitionId())) {
                throw new BusinessException("该团队不属于当前竞赛");
            }
            LambdaQueryWrapper<TeamMember> memberCheck = new LambdaQueryWrapper<>();
            memberCheck.eq(TeamMember::getTeamId, dto.getTeamId())
                    .eq(TeamMember::getUserId, userId);
            if (teamMemberMapper.selectCount(memberCheck) == 0) {
                throw new BusinessException("您不是该团队成员，无法报名");
            }
            if (team.getLeaderId().equals(userId) && StrUtil.isBlank(dto.getProjectName())) {
                throw new BusinessException("团长必须填写项目名称");
            }
        } else {
            if (dto.getTeamId() != null) {
                throw new BusinessException("个人赛不能关联团队");
            }
            if (StrUtil.isBlank(dto.getProjectName())) {
                throw new BusinessException("项目名称不能为空");
            }
        }

        User advisor = resolveAdvisor(dto);

        Registration registration = new Registration();
        BeanUtils.copyProperties(dto, registration);
        registration.setUserId(userId);
        registration.setParticipationType(competitionParticipationType);
        if (advisor != null) {
            registration.setAdvisor(advisor.getName());
        }
        if (StrUtil.isBlank(registration.getProjectName())) {
            registration.setProjectName(competition.getName() + " - 团队参赛");
        }
        
        // 如果选择了赛道，验证赛道是否属于该竞赛
        if (dto.getTrackId() != null) {
            Track track = trackMapper.selectById(dto.getTrackId());
            if (track == null) {
                throw new BusinessException("赛道不存在");
            }
            if (!track.getCompetitionId().equals(dto.getCompetitionId())) {
                throw new BusinessException("赛道不属于当前竞赛");
            }
        }
        
        registration.setStatus("PENDING");
        save(registration);
    }

    @Override
    public PageResult<RegistrationVO> getMyRegistrations(Long userId, String status, int page, int size) {
        Page<Registration> registrationPage = new Page<>(page, size);
        Set<Long> teamIds = teamMemberMapper.selectList(
                        new LambdaQueryWrapper<TeamMember>().eq(TeamMember::getUserId, userId))
                .stream()
                .map(TeamMember::getTeamId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> {
            w.eq(Registration::getUserId, userId);
            if (!teamIds.isEmpty()) {
                w.or().in(Registration::getTeamId, teamIds);
            }
        });
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Registration::getStatus, status);
        }
        wrapper.orderByDesc(Registration::getCreatedAt);

        Page<Registration> result = page(registrationPage, wrapper);
        List<RegistrationVO> voList = enrichWithCompetitionAndTeam(result.getRecords());

        return new PageResult<>(voList, result.getTotal(), size, page);
    }

    @Override
    @Transactional
    public void approveRegistration(Long registrationId, Long adminId, String role) {
        Registration registration = getById(registrationId);
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }

        // 检查权限
        if (!canAuditRegistration(adminId, role, registration.getCompetitionId())) {
            throw new BusinessException("无权审核该竞赛的报名");
        }

        registration.setStatus("APPROVED");
        registration.setReviewedBy(adminId);
        registration.setReviewedAt(LocalDateTime.now());
        updateById(registration);

        Competition competition = competitionService.getById(registration.getCompetitionId());
        String compName = competition != null ? competition.getName() : "竞赛";
        notificationService.sendNotification(registration.getUserId(),
                "报名审核通过",
                "您对「" + compName + "」的报名已通过审核",
                "REGISTRATION", registration.getId());
    }

    @Override
    @Transactional
    public void rejectRegistration(ReviewRejectDTO dto, Long adminId, String role) {
        Registration registration = getById(dto.getRegistrationId());
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }

        // 检查权限
        if (!canAuditRegistration(adminId, role, registration.getCompetitionId())) {
            throw new BusinessException("无权审核该竞赛的报名");
        }

        registration.setStatus("REJECTED");
        registration.setRejectReason(dto.getRejectReason());
        registration.setReviewedBy(adminId);
        registration.setReviewedAt(LocalDateTime.now());
        updateById(registration);

        Competition competition = competitionService.getById(registration.getCompetitionId());
        String compName = competition != null ? competition.getName() : "竞赛";
        notificationService.sendNotification(registration.getUserId(),
                "报名审核未通过",
                "您对「" + compName + "」的报名未通过审核，原因：" + dto.getRejectReason(),
                "REGISTRATION", registration.getId());
    }

    @Override
    public PageResult<RegistrationVO> getAdminRegistrations(Long competitionId, String status, String keyword, int page, int size, Long adminId, String role) {
        Page<Registration> registrationPage = new Page<>(page, size);

        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();

        if (competitionId != null) {
            wrapper.eq(Registration::getCompetitionId, competitionId);
        }

        addRegistrationPermissionFilter(wrapper, adminId, role, competitionId);

        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Registration::getStatus, status);
        }
        wrapper.orderByDesc(Registration::getCreatedAt);

        Page<Registration> result = page(registrationPage, wrapper);
        List<RegistrationVO> voList = enrichWithDetails(result.getRecords());

        if (StrUtil.isNotBlank(keyword)) {
            String lowerKeyword = keyword.toLowerCase();
            voList = voList.stream()
                    .filter(vo -> {
                        if (StrUtil.isNotBlank(vo.getProjectName()) && vo.getProjectName().toLowerCase().contains(lowerKeyword)) return true;
                        if (StrUtil.isNotBlank(vo.getDescription()) && vo.getDescription().toLowerCase().contains(lowerKeyword)) return true;
                        if (StrUtil.isNotBlank(vo.getParticipantName()) && vo.getParticipantName().toLowerCase().contains(lowerKeyword)) return true;
                        if (StrUtil.isNotBlank(vo.getStudentId()) && vo.getStudentId().toLowerCase().contains(lowerKeyword)) return true;
                        if (StrUtil.isNotBlank(vo.getTeamName()) && vo.getTeamName().toLowerCase().contains(lowerKeyword)) return true;
                        if (StrUtil.isNotBlank(vo.getAdvisor()) && vo.getAdvisor().toLowerCase().contains(lowerKeyword)) return true;
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        return new PageResult<>(voList, result.getTotal(), size, page);
    }

    @Override
    @Transactional
    public void deleteRegistration(Long registrationId, Long userId, String role) {
        Registration registration = getById(registrationId);
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }

        boolean isAdminDelete = "ADMIN".equals(role)
                || ("TEACHER".equals(role) && canAuditRegistration(userId, role, registration.getCompetitionId()));
        boolean isOwnerDelete = registration.getUserId() != null && registration.getUserId().equals(userId);

        if (!isAdminDelete && !isOwnerDelete) {
            throw new BusinessException("无权删除该报名记录");
        }
        if (!isAdminDelete) {
            if ("APPROVED".equals(registration.getStatus())) {
                throw new BusinessException("已通过的报名不能自行删除，如需删除请联系管理员");
            }
            Integer workCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM work WHERE registration_id = ? AND deleted = 0",
                    Integer.class,
                    registrationId
            );
            if (workCount != null && workCount > 0) {
                throw new BusinessException("该报名已提交作品，不能自行删除");
            }
        }

        Long teamId = registration.getTeamId();
        cleanupRegistrationData(registrationId);

        if (teamId != null && hasNoActiveRegistrationForTeam(teamId)) {
            cleanupTeamData(teamId);
        }
    }

    private Map<Long, String> toNameMap(List<?> items, java.util.function.Function<Object, Long> idExtractor, java.util.function.Function<Object, String> nameExtractor) {
        Map<Long, String> map = new HashMap<>();
        for (Object item : items) {
            Long id = idExtractor.apply(item);
            String name = nameExtractor.apply(item);
            if (id != null) {
                map.put(id, name != null ? name : "");
            }
        }
        return map;
    }

    private List<RegistrationVO> enrichWithCompetitionAndTeam(List<Registration> registrations) {
        Set<Long> competitionIds = registrations.stream()
                .map(Registration::getCompetitionId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Long> teamIds = registrations.stream()
                .map(Registration::getTeamId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Long> trackIds = registrations.stream()
                .map(Registration::getTrackId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<Long, Competition> competitionMap = toCompetitionMap(competitionIds);

        Map<Long, String> teamNameMap = teamIds.isEmpty() ? new HashMap<>() :
                toNameMap(new ArrayList<>(teamMapper.selectBatchIds(teamIds)),
                        o -> ((Team) o).getId(),
                        o -> ((Team) o).getName());

        Map<Long, String> trackNameMap = trackIds.isEmpty() ? new HashMap<>() :
                toNameMap(new ArrayList<>(trackMapper.selectBatchIds(trackIds)),
                        o -> ((Track) o).getId(),
                        o -> ((Track) o).getName());

        return registrations.stream().map(reg -> {
            RegistrationVO vo = new RegistrationVO();
            BeanUtils.copyProperties(reg, vo);
            fillCompetitionInfo(vo, competitionMap.get(reg.getCompetitionId()));
            vo.setTeamName(teamNameMap.get(reg.getTeamId()));
            vo.setTrackName(trackNameMap.get(reg.getTrackId()));
            return vo;
        }).collect(Collectors.toList());
    }

    private List<RegistrationVO> enrichWithDetails(List<Registration> registrations) {
        Set<Long> competitionIds = registrations.stream()
                .map(Registration::getCompetitionId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Long> userIds = registrations.stream()
                .map(Registration::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Long> teamIds = registrations.stream()
                .map(Registration::getTeamId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Long> trackIds = registrations.stream()
                .map(Registration::getTrackId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<Long, Competition> competitionMap = toCompetitionMap(competitionIds);

        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            for (User u : userService.listByIds(userIds)) {
                if (u != null && u.getId() != null) {
                    userMap.put(u.getId(), u);
                }
            }
        }

        Map<Long, String> teamNameMap = teamIds.isEmpty() ? new HashMap<>() :
                toNameMap(new ArrayList<>(teamMapper.selectBatchIds(teamIds)),
                        o -> ((Team) o).getId(),
                        o -> ((Team) o).getName());

        Map<Long, String> trackNameMap = trackIds.isEmpty() ? new HashMap<>() :
                toNameMap(new ArrayList<>(trackMapper.selectBatchIds(trackIds)),
                        o -> ((Track) o).getId(),
                        o -> ((Track) o).getName());

        return registrations.stream().map(reg -> {
            RegistrationVO vo = new RegistrationVO();
            BeanUtils.copyProperties(reg, vo);
            fillCompetitionInfo(vo, competitionMap.get(reg.getCompetitionId()));
            User user = userMap.get(reg.getUserId());
            if (user != null) {
                vo.setParticipantName(user.getName());
                vo.setStudentId(user.getStudentId());
                vo.setCollege(user.getCollege());
            }
            vo.setTeamName(teamNameMap.get(reg.getTeamId()));
            vo.setTrackName(trackNameMap.get(reg.getTrackId()));
            return vo;
        }).collect(Collectors.toList());
    }

    private Map<Long, Competition> toCompetitionMap(Set<Long> competitionIds) {
        Map<Long, Competition> map = new HashMap<>();
        if (!competitionIds.isEmpty()) {
            for (Competition competition : competitionService.listByIds(competitionIds)) {
                if (competition != null && competition.getId() != null) {
                    map.put(competition.getId(), competition);
                }
            }
        }
        return map;
    }

    private void fillCompetitionInfo(RegistrationVO vo, Competition competition) {
        if (competition == null) {
            return;
        }
        vo.setCompetitionName(competition.getName());
        vo.setCompetitionStatus(competition.getStatus());
        vo.setRegistrationStart(competition.getRegistrationStart());
        vo.setRegistrationEnd(competition.getRegistrationEnd());
        vo.setSubmissionDeadline(competition.getSubmissionDeadline());
    }

    private void ensureRegistrationOpen(Competition competition) {
        String status = competition.getStatus();
        if ("DRAFT".equals(status)) {
            throw new BusinessException("竞赛未发布，暂不能报名");
        }
        if ("SUBMISSION".equals(status) || "REVIEW".equals(status)
                || "ANNOUNCEMENT".equals(status) || "FINISHED".equals(status) || "ENDED".equals(status)) {
            throw new BusinessException("竞赛报名已截止");
        }
        LocalDateTime now = LocalDateTime.now();
        if (competition.getRegistrationStart() != null && now.isBefore(competition.getRegistrationStart())) {
            throw new BusinessException("竞赛报名尚未开始");
        }
        if (competition.getRegistrationEnd() != null && now.isAfter(competition.getRegistrationEnd())) {
            throw new BusinessException("竞赛报名已截止");
        }
    }

    private User resolveAdvisor(RegistrationDTO dto) {
        if (dto.getAdvisorId() != null) {
            User advisor = userService.getById(dto.getAdvisorId());
            if (!isValidAdvisor(advisor)) {
                throw new BusinessException("请选择系统中存在且启用的指导老师");
            }
            return advisor;
        }

        if (StrUtil.isBlank(dto.getAdvisor())) {
            return null;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, dto.getAdvisor().trim())
                .in(User::getRole, "TEACHER", "ADVISOR")
                .eq(User::getStatus, 1);
        List<User> matches = userService.list(wrapper);
        if (matches == null || matches.isEmpty()) {
            throw new BusinessException("指导老师不存在，请从系统老师列表中选择");
        }
        if (matches.size() > 1) {
            throw new BusinessException("存在同名指导老师，请从下拉列表中选择具体老师");
        }
        return matches.get(0);
    }

    private boolean isValidAdvisor(User user) {
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            return false;
        }
        return "TEACHER".equals(user.getRole()) || "ADVISOR".equals(user.getRole());
    }

    private boolean canAuditRegistration(Long userId, String role, Long competitionId) {
        if (userId == null || competitionId == null) {
            return false;
        }
        if ("ADMIN".equals(role)) {
            return true;
        }
        if ("TEACHER".equals(role)) {
            return competitionAdminService.hasCompetitionPermission(userId, competitionId);
        }
        return false;
    }

    private void cleanupRegistrationData(Long registrationId) {
        List<String> fileUrls = jdbcTemplate.queryForList(
                "SELECT file_url FROM work WHERE registration_id = ? AND deleted = 0 AND file_url IS NOT NULL AND file_url <> ''",
                String.class,
                registrationId
        );
        for (String fileUrl : fileUrls) {
            localFileService.delete(fileUrl);
        }

        jdbcTemplate.update(
                "UPDATE award SET deleted = 1 WHERE deleted = 0 AND work_id IN " +
                        "(SELECT id FROM work WHERE registration_id = ?)",
                registrationId
        );
        jdbcTemplate.update(
                "UPDATE review_task SET deleted = 1 WHERE deleted = 0 AND work_id IN " +
                        "(SELECT id FROM work WHERE registration_id = ?)",
                registrationId
        );
        jdbcTemplate.update("UPDATE work SET deleted = 1 WHERE deleted = 0 AND registration_id = ?", registrationId);
        jdbcTemplate.update("UPDATE registration SET deleted = 1 WHERE deleted = 0 AND id = ?", registrationId);
    }

    private boolean hasNoActiveRegistrationForTeam(Long teamId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM registration WHERE team_id = ? AND deleted = 0",
                Integer.class,
                teamId
        );
        return count == null || count == 0;
    }

    private void cleanupTeamData(Long teamId) {
        jdbcTemplate.update("UPDATE team_member SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team_advisor SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team_advisor_audit SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team_task SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team_message SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team SET status = 'DISBANDED', deleted = 1 WHERE deleted = 0 AND id = ?", teamId);
    }

    private void addRegistrationPermissionFilter(LambdaQueryWrapper<Registration> wrapper, Long userId, String role, Long requestedCompetitionId) {
        if ("ADMIN".equals(role)) {
            return;
        }

        List<Long> managedCompetitionIds = competitionAdminService.getManagedCompetitionIds(userId);
        if ("TEACHER".equals(role)) {
            if (managedCompetitionIds != null && !managedCompetitionIds.isEmpty()) {
                if (requestedCompetitionId != null && !managedCompetitionIds.contains(requestedCompetitionId)) {
                    throw new BusinessException("无权查看该竞赛的报名记录");
                }
                wrapper.in(Registration::getCompetitionId, managedCompetitionIds);
            }
            return;
        }

        if ("ADVISOR".equals(role)) {
            User advisor = userService.getById(userId);
            String advisorName = advisor != null ? advisor.getName() : null;
            Set<Long> advisedTeamIds = teamAdvisorMapper.selectList(
                            new LambdaQueryWrapper<TeamAdvisor>().eq(TeamAdvisor::getAdvisorId, userId))
                    .stream()
                    .map(TeamAdvisor::getTeamId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());

            boolean hasManagedCompetitions = managedCompetitionIds != null && !managedCompetitionIds.isEmpty();
            if (StrUtil.isBlank(advisorName) && advisedTeamIds.isEmpty() && !hasManagedCompetitions) {
                wrapper.eq(Registration::getId, -1L);
                return;
            }

            wrapper.and(w -> {
                boolean hasCondition = false;
                if (StrUtil.isNotBlank(advisorName)) {
                    w.eq(Registration::getAdvisor, advisorName);
                    hasCondition = true;
                }
                if (!advisedTeamIds.isEmpty()) {
                    if (hasCondition) {
                        w.or();
                    }
                    w.in(Registration::getTeamId, advisedTeamIds);
                    hasCondition = true;
                }
                if (hasManagedCompetitions) {
                    if (hasCondition) {
                        w.or();
                    }
                    w.in(Registration::getCompetitionId, managedCompetitionIds);
                }
            });
            return;
        }

        throw new BusinessException("无权查看报名记录");
    }
}
