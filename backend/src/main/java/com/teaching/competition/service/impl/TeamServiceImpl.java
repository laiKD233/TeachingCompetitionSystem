package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.TeamDTO;
import com.teaching.competition.dto.TeamMessageDTO;
import com.teaching.competition.dto.TeamTaskDTO;
import com.teaching.competition.entity.*;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.*;
import com.teaching.competition.service.TeamAdvisorAuditService;
import com.teaching.competition.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamTaskMapper teamTaskMapper;
    private final TeamMessageMapper teamMessageMapper;
    private final TeamAdvisorMapper teamAdvisorMapper;
    private final TeamAdvisorAuditService auditService;
    private final UserMapper userMapper;
    private final CompetitionMapper competitionMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Team createTeam(TeamDTO dto, Long userId) {
        User creator = userMapper.selectById(userId);
        if (creator == null) {
            throw new BusinessException("用户不存在");
        }
        if (dto.getCompetitionId() == null) {
            throw new BusinessException("创建团队时必须选择所属竞赛");
        }
        Competition competition = competitionMapper.selectById(dto.getCompetitionId());
        boolean competitionDeleted = competition != null
                && competition.getDeleted() != null
                && competition.getDeleted() == 1;
        if (competition == null || competitionDeleted) {
            throw new BusinessException("所属竞赛不存在");
        }
        if (!"TEAM".equals(competition.getParticipationType())) {
            throw new BusinessException("仅团队赛支持创建团队");
        }
        ensureTeamFormationOpen(competition);
        if ("STUDENT".equals(creator.getRole()) && hasActiveTeamMembership(userId)) {
            throw new BusinessException(buildExistingTeamMessage(userId));
        }

        Team team = new Team();
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());
        team.setCompetitionId(dto.getCompetitionId());
        team.setLeaderId(userId);
        team.setMaxMembers(dto.getMaxMembers() != null ? dto.getMaxMembers() : 5);
        team.setStatus("ACTIVE");
        team.setInviteCode(generateUniqueCode());
        save(team);

        TeamMember leader = new TeamMember();
        leader.setTeamId(team.getId());
        leader.setUserId(userId);
        leader.setRole("LEADER");
        leader.setJoinTime(LocalDateTime.now());
        teamMemberMapper.insert(leader);

        // 创建指导老师审核申请（不再直接添加指导老师）
        if (dto.getAdvisorId() != null && !dto.getAdvisorId().isEmpty()) {
            Set<Long> uniqueAdvisorIds = new HashSet<>(dto.getAdvisorId());
            for (Long advisorId : uniqueAdvisorIds) {
                // 检查是否已存在审核申请或已加入
                if (!auditService.hasPendingAudit(team.getId(), advisorId)) {
                    LambdaQueryWrapper<TeamAdvisor> checkWrapper = new LambdaQueryWrapper<>();
                    checkWrapper.eq(TeamAdvisor::getTeamId, team.getId());
                    checkWrapper.eq(TeamAdvisor::getAdvisorId, advisorId);
                    TeamAdvisor existing = teamAdvisorMapper.selectOne(checkWrapper);
                    if (existing == null) {
                        // 创建审核申请
                        auditService.createAudit(team.getId(), advisorId, userId, "申请成为团队指导老师");
                    }
                }
            }
        }

        return getTeamDetail(team.getId());
    }

    @Override
    @Transactional
    public void updateTeam(Long id, TeamDTO dto, Long userId) {
        Team team = getById(id);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }
        if (!team.getLeaderId().equals(userId)) {
            throw new BusinessException("只有队长可以修改团队信息");
        }
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());
        if (dto.getMaxMembers() != null) {
            team.setMaxMembers(dto.getMaxMembers());
        }
        updateById(team);

        if (dto.getAdvisorId() != null && !dto.getAdvisorId().isEmpty()) {
            Set<Long> uniqueAdvisorIds = new HashSet<>(dto.getAdvisorId());

            LambdaQueryWrapper<TeamAdvisor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TeamAdvisor::getTeamId, id);
            List<TeamAdvisor> existingAdvisors = teamAdvisorMapper.selectList(wrapper);
            Set<Long> existingAdvisorIds = new HashSet<>();
            for (TeamAdvisor ta : existingAdvisors) {
                existingAdvisorIds.add(ta.getAdvisorId());
            }

            // 添加新的指导老师
            for (Long advisorId : uniqueAdvisorIds) {
                if (!existingAdvisorIds.contains(advisorId)) {
                    if (!auditService.hasPendingAudit(id, advisorId)) {
                        auditService.createAudit(id, advisorId, userId, "申请您成为团队指导老师");
                    }
                }
            }

            // 移除不在新列表中的指导老师
            for (TeamAdvisor existingAdvisor : existingAdvisors) {
                if (!uniqueAdvisorIds.contains(existingAdvisor.getAdvisorId())) {
                    teamAdvisorMapper.deleteById(existingAdvisor.getId());
                }
            }
        } else {
            // 如果没有指定指导老师，则移除所有已有的指导老师
            LambdaQueryWrapper<TeamAdvisor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TeamAdvisor::getTeamId, id);
            teamAdvisorMapper.delete(wrapper);
        }
    }

    @Override
    @Transactional
    public void deleteTeam(Long id, Long userId) {
        Team team = getById(id);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }
        if (!team.getLeaderId().equals(userId)) {
            throw new BusinessException("只有队长可以解散团队");
        }
        // 使用 UpdateWrapper 强制更新所有字段，包括 deleted
        LambdaUpdateWrapper<Team> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Team::getId, id)
                     .set(Team::getStatus, "DISBANDED")
                     .set(Team::getDeleted, 1);
        teamMapper.update(null, updateWrapper);
    }

    @Override
    public Team getTeamDetail(Long id) {
        Team team = teamMapper.selectById(id);
        if (team == null) {
            return null;
        }
        fillTeamDisplayInfo(team);
        return team;
    }

    @Override
    public Team getTeamDetail(Long id, Long userId) {
        assertCanViewTeam(id, userId);
        return getTeamDetail(id);
    }

    private void fillTeamDisplayInfo(Team team) {
        if (team == null) {
            return;
        }
        if (team.getLeaderId() != null) {
            User leader = userMapper.selectById(team.getLeaderId());
            if (leader != null) {
                team.setLeaderName(leader.getName());
            }
        }
        LambdaQueryWrapper<TeamAdvisor> advisorWrapper = new LambdaQueryWrapper<>();
        advisorWrapper.eq(TeamAdvisor::getTeamId, team.getId());
        List<TeamAdvisor> teamAdvisors = teamAdvisorMapper.selectList(advisorWrapper);
        if (teamAdvisors != null && !teamAdvisors.isEmpty()) {
            List<String> advisorNames = new ArrayList<>();
            List<Long> advisorIds = new ArrayList<>();
            for (TeamAdvisor teamAdvisor : teamAdvisors) {
                advisorIds.add(teamAdvisor.getAdvisorId());
                User advisor = userMapper.selectById(teamAdvisor.getAdvisorId());
                if (advisor != null) {
                    advisorNames.add(advisor.getName());
                }
            }
            team.setAdvisorNames(advisorNames);
            team.setAdvisorIds(advisorIds);
            if (!advisorNames.isEmpty()) {
                team.setAdvisorName(String.join("、", advisorNames));
            }
        }
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, team.getId());
        wrapper.eq(TeamMember::getDeleted, 0);
        Long count = teamMemberMapper.selectCount(wrapper);
        team.setMemberCount(count != null ? count.intValue() : 0);
    }

    @Override
    public List<Team> getMyTeams(Long userId) {
        List<Team> teams = teamMapper.selectByMemberId(userId);
        for (Team team : teams) {
            fillTeamDisplayInfo(team);
        }
        return teams;
    }

    @Override
    public List<Team> getAdvisedTeams(Long advisorId) {
        LambdaQueryWrapper<TeamAdvisor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamAdvisor::getAdvisorId, advisorId);
        List<TeamAdvisor> teamAdvisors = teamAdvisorMapper.selectList(wrapper);

        List<Team> teams = new ArrayList<>();
        if (!teamAdvisors.isEmpty()) {
            List<Long> teamIds = new ArrayList<>();
            for (TeamAdvisor ta : teamAdvisors) {
                teamIds.add(ta.getTeamId());
            }

            if (!teamIds.isEmpty()) {
                LambdaQueryWrapper<Team> teamWrapper = new LambdaQueryWrapper<>();
                teamWrapper.in(Team::getId, teamIds);
                teamWrapper.eq(Team::getDeleted, 0);
                teamWrapper.orderByDesc(Team::getCreatedAt);
                teams = list(teamWrapper);

                teams.forEach(this::fillTeamDisplayInfo);
            }
        }
        return teams;
    }

    @Override
    public PageResult<Team> getAllTeams(int page, int size) {
        Page<Team> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getDeleted, 0);
        wrapper.orderByDesc(Team::getCreatedAt);
        Page<Team> result = page(pageParam, wrapper);
        result.getRecords().forEach(this::fillTeamDisplayInfo);
        return new PageResult<>(result.getRecords(), (int) result.getTotal(), page, size);
    }

    @Override
    @Transactional
    public void joinTeam(Long teamId, String inviteCode, Long userId) {
        Team team = getById(teamId);
        if (team == null || !"ACTIVE".equals(team.getStatus())) {
            throw new BusinessException("团队不存在或已解散");
        }
        Competition competition = competitionMapper.selectById(team.getCompetitionId());
        ensureTeamFormationOpen(competition);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("STUDENT".equals(user.getRole()) && hasActiveTeamMembership(userId)) {
            throw new BusinessException(buildExistingTeamMessage(userId));
        }
        if (!team.getInviteCode().equals(inviteCode)) {
            throw new BusinessException("邀请码错误");
        }
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId).eq(TeamMember::getUserId, userId);
        if (teamMemberMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("你已经是团队成员");
        }
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId);
        Long countLong = teamMemberMapper.selectCount(wrapper);
        int currentCount = countLong != null ? countLong.intValue() : 0;
        if (currentCount >= team.getMaxMembers()) {
            throw new BusinessException("团队人数已满");
        }
        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setRole("MEMBER");
        member.setJoinTime(LocalDateTime.now());
        teamMemberMapper.insert(member);
    }

    @Override
    @Transactional
    public void leaveTeam(Long teamId, Long userId) {
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }
        if (team.getLeaderId().equals(userId)) {
            throw new BusinessException("队长不能退出团队，请先转让队长或解散团队");
        }
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId).eq(TeamMember::getUserId, userId);
        teamMemberMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void removeMember(Long teamId, Long memberId, Long operatorId) {
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }
        if (!team.getLeaderId().equals(operatorId)) {
            throw new BusinessException("只有队长可以移除成员");
        }
        if (team.getLeaderId().equals(memberId)) {
            throw new BusinessException("不能移除队长");
        }
        LambdaQueryWrapper<TeamMember> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(TeamMember::getTeamId, teamId).eq(TeamMember::getUserId, memberId);
        TeamMember member = teamMemberMapper.selectOne(checkWrapper);
        if (member == null) {
            throw new BusinessException("该成员不存在");
        }
        teamMemberMapper.deleteById(member.getId());
    }

    @Override
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberMapper.selectByTeamId(teamId);
    }

    @Override
    public List<TeamMember> getTeamMembers(Long teamId, Long userId) {
        assertCanViewTeam(teamId, userId);
        return getTeamMembers(teamId);
    }

    @Override
    public String generateInviteCode(Long teamId, Long userId) {
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }
        if (!team.getLeaderId().equals(userId)) {
            throw new BusinessException("只有队长可以生成邀请码");
        }
        return team.getInviteCode();
    }

    @Override
    @Transactional
    public TeamTask createTask(Long teamId, TeamTaskDTO dto, Long userId) {
        if (!isTeamMemberOrAdvisor(teamId, userId)) {
            throw new BusinessException("只有团队成员或指导老师可以创建任务");
        }
        TeamTask task = new TeamTask();
        task.setTeamId(teamId);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setAssigneeId(dto.getAssigneeId());
        task.setStatus("PENDING");
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM");
        task.setDueDate(dto.getDueDate());
        task.setCreatedBy(userId);
        teamTaskMapper.insert(task);
        return task;
    }

    @Override
    @Transactional
    public void updateTask(Long taskId, TeamTaskDTO dto, Long userId) {
        TeamTask task = teamTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!isTeamMemberOrAdvisor(task.getTeamId(), userId)) {
            throw new BusinessException("只有团队成员或指导老师可以修改任务");
        }
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setAssigneeId(dto.getAssigneeId());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        teamTaskMapper.updateById(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        TeamTask task = teamTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!isTeamMemberOrAdvisor(task.getTeamId(), userId)) {
            throw new BusinessException("只有团队成员或指导老师可以删除任务");
        }
        teamTaskMapper.deleteById(taskId);
    }

    @Override
    @Transactional
    public void updateTaskStatus(Long taskId, String status, Long userId) {
        TeamTask task = teamTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!isTeamMemberOrAdvisor(task.getTeamId(), userId)) {
            throw new BusinessException("只有团队成员或指导老师可以更新任务状态");
        }
        task.setStatus(status);
        teamTaskMapper.updateById(task);
    }

    @Override
    public List<TeamTask> getTeamTasks(Long teamId) {
        return teamTaskMapper.selectByTeamId(teamId);
    }

    @Override
    public List<TeamTask> getTeamTasks(Long teamId, Long userId) {
        assertCanViewTeam(teamId, userId);
        return getTeamTasks(teamId);
    }

    @Override
    @Transactional
    public TeamMessage sendMessage(Long teamId, TeamMessageDTO dto, Long userId) {
        if (!isTeamMemberOrAdvisor(teamId, userId)) {
            throw new BusinessException("只有团队成员或指导老师可以发送消息");
        }
        TeamMessage message = new TeamMessage();
        message.setTeamId(teamId);
        message.setUserId(userId);
        message.setContent(dto.getContent());
        message.setType("TEXT");
        teamMessageMapper.insert(message);

        User user = userMapper.selectById(userId);
        if (user != null) {
            message.setUserName(user.getName());
            message.setUserAvatar(user.getAvatar());
        }

        return message;
    }

    @Override
    public List<TeamMessage> getTeamMessages(Long teamId, int limit) {
        List<TeamMessage> messages = teamMessageMapper.selectByTeamId(teamId, limit);
        for (TeamMessage message : messages) {
            User user = userMapper.selectById(message.getUserId());
            if (user != null) {
                message.setUserName(user.getName());
                message.setUserAvatar(user.getAvatar());
            }
        }
        return messages;
    }

    @Override
    public List<TeamMessage> getTeamMessages(Long teamId, int limit, Long userId) {
        assertCanViewTeam(teamId, userId);
        return getTeamMessages(teamId, limit);
    }

    @Override
    public List<?> getAvailableAdvisors() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getRole, "TEACHER", "ADVISOR");
        wrapper.eq(User::getStatus, 1);
        return userMapper.selectList(wrapper).stream()
                .map(user -> {
                    Map<String, Object> advisor = new LinkedHashMap<>();
                    advisor.put("id", user.getId());
                    advisor.put("username", user.getUsername());
                    advisor.put("name", user.getName());
                    advisor.put("role", user.getRole());
                    advisor.put("college", user.getCollege());
                    advisor.put("email", user.getEmail());
                    advisor.put("phone", user.getPhone());
                    return advisor;
                })
                .toList();
    }

    private boolean isTeamMember(Long teamId, Long userId) {
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId).eq(TeamMember::getUserId, userId);
        return teamMemberMapper.selectCount(wrapper) > 0;
    }

    private boolean isTeamAdvisor(Long teamId, Long userId) {
        LambdaQueryWrapper<TeamAdvisor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamAdvisor::getTeamId, teamId).eq(TeamAdvisor::getAdvisorId, userId);
        return teamAdvisorMapper.selectCount(wrapper) > 0;
    }

    private boolean isTeamMemberOrAdvisor(Long teamId, Long userId) {
        return isTeamMember(teamId, userId) || isTeamAdvisor(teamId, userId);
    }

    private void assertCanViewTeam(Long teamId, Long userId) {
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException("团队不存在");
        }
        User user = userMapper.selectById(userId);
        if (user != null && "ADMIN".equals(user.getRole())) {
            return;
        }
        if (!isTeamMemberOrAdvisor(teamId, userId)) {
            throw new BusinessException("无权限查看该团队信息");
        }
    }

    private boolean hasActiveTeamMembership(Long userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) " +
                        "FROM team_member tm " +
                        "JOIN team t ON t.id = tm.team_id " +
                        "WHERE tm.user_id = ? AND tm.deleted = 0 AND t.deleted = 0 AND t.status = 'ACTIVE'",
                Integer.class,
                userId
        );
        return count != null && count > 0;
    }

    private String buildExistingTeamMessage(Long userId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT t.name AS team_name, c.name AS competition_name " +
                        "FROM team_member tm " +
                        "JOIN team t ON t.id = tm.team_id " +
                        "LEFT JOIN competition c ON c.id = t.competition_id " +
                        "WHERE tm.user_id = ? AND tm.deleted = 0 AND t.deleted = 0 AND t.status = 'ACTIVE' " +
                        "ORDER BY tm.join_time DESC LIMIT 1",
                userId
        );
        if (rows.isEmpty()) {
            return "当前学生已加入团队，每位参赛者只能加入或创建一个团队";
        }
        Map<String, Object> row = rows.get(0);
        String teamName = row.get("team_name") != null ? String.valueOf(row.get("team_name")) : "已有团队";
        String competitionName = row.get("competition_name") != null ? String.valueOf(row.get("competition_name")) : "其他竞赛";
        return "当前学生已加入团队「" + teamName + "」（" + competitionName + "），每位参赛者只能加入或创建一个团队";
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void ensureTeamFormationOpen(Competition competition) {
        if (competition == null || (competition.getDeleted() != null && competition.getDeleted() == 1)) {
            throw new BusinessException("所属竞赛不存在");
        }
        String status = competition.getStatus();
        if ("DRAFT".equals(status)) {
            throw new BusinessException("竞赛未发布，暂不能创建或加入团队");
        }
        if ("SUBMISSION".equals(status) || "REVIEW".equals(status)
                || "ANNOUNCEMENT".equals(status) || "FINISHED".equals(status) || "ENDED".equals(status)) {
            throw new BusinessException("竞赛报名已截止，不能创建或加入团队");
        }
        LocalDateTime now = LocalDateTime.now();
        if (competition.getRegistrationStart() != null && now.isBefore(competition.getRegistrationStart())) {
            throw new BusinessException("竞赛报名尚未开始，暂不能创建或加入团队");
        }
        if (competition.getRegistrationEnd() != null && now.isAfter(competition.getRegistrationEnd())) {
            throw new BusinessException("竞赛报名已截止，不能创建或加入团队");
        }
    }
}
