package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.AwardDTO;
import com.teaching.competition.entity.Award;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.Registration;
import com.teaching.competition.entity.ReviewTask;
import com.teaching.competition.entity.Team;
import com.teaching.competition.entity.TeamMember;
import com.teaching.competition.entity.User;
import com.teaching.competition.entity.Work;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.AwardMapper;
import com.teaching.competition.mapper.RegistrationMapper;
import com.teaching.competition.mapper.ReviewTaskMapper;
import com.teaching.competition.mapper.TeamMapper;
import com.teaching.competition.mapper.TeamMemberMapper;
import com.teaching.competition.mapper.UserMapper;
import com.teaching.competition.service.AwardService;
import com.teaching.competition.service.CompetitionService;
import com.teaching.competition.service.WorkService;
import com.teaching.competition.vo.ScoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AwardServiceImpl extends ServiceImpl<AwardMapper, Award> implements AwardService {

    private final WorkService workService;
    private final CompetitionService competitionService;
    private final RegistrationMapper registrationMapper;
    private final UserMapper userMapper;
    private final ReviewTaskMapper reviewTaskMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamMapper teamMapper;

    @Override
    @Transactional
    public void publishAwards(AwardDTO dto) {
        List<AwardDTO.AwardItemDTO> items = dto.getItems();
        if (items == null || items.isEmpty()) {
            throw new BusinessException("获奖信息不能为空");
        }
        
        Long competitionId = dto.getCompetitionId();
        Competition competition = competitionService.getById(competitionId);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        if (!isReviewStatus(competition.getStatus())) {
            throw new BusinessException("只有评审中的竞赛可以发布奖项");
        }
        
        Set<Long> awardWorkIds = items.stream()
                .map(AwardDTO.AwardItemDTO::getWorkId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (awardWorkIds.isEmpty()) {
            throw new BusinessException("获奖作品不能为空");
        }

        LambdaQueryWrapper<Award> clearWrapper = new LambdaQueryWrapper<>();
        clearWrapper.eq(Award::getCompetitionId, competitionId)
                .in(Award::getWorkId, awardWorkIds);
        remove(clearWrapper);

        List<Award> awards = new ArrayList<>();
        for (AwardDTO.AwardItemDTO item : items) {
            Work work = workService.getById(item.getWorkId());
            if (work == null || !competitionId.equals(work.getCompetitionId())) {
                throw new BusinessException("获奖作品不存在或不属于当前竞赛");
            }
            Award award = new Award();
            award.setCompetitionId(competitionId);
            award.setUserId(work.getUserId());
            award.setWorkId(item.getWorkId());
            award.setAwardLevel(item.getAwardLevel());
            awards.add(award);
        }
        
        saveBatch(awards);
    }

    @Override
    @Transactional
    public void publishAnnouncement(Long competitionId) {
        Competition competition = competitionService.getById(competitionId);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        if (!isReviewStatus(competition.getStatus())) {
            throw new BusinessException("只有评审中的竞赛可以公示成绩");
        }
        
        competition.setStatus("ANNOUNCEMENT");
        competitionService.updateById(competition);
    }

    @Override
    public List<Award> getAwardsByCompetition(Long competitionId) {
        LambdaQueryWrapper<Award> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Award::getCompetitionId, competitionId);
        return list(wrapper);
    }

    @Override
    public List<ScoreVO> getAwardResults(Long competitionId, Long userId) {
        LambdaQueryWrapper<Work> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Work::getCompetitionId, competitionId)
                .orderByDesc(Work::getAvgScore);

        List<Work> works = workService.list(wrapper);
        if (userId != null) {
            Set<Long> visibleTeamIds = teamMemberMapper.selectList(
                    new LambdaQueryWrapper<TeamMember>()
                            .eq(TeamMember::getUserId, userId))
                    .stream()
                    .map(TeamMember::getTeamId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());

            LambdaQueryWrapper<Registration> regWrapper = new LambdaQueryWrapper<>();
            regWrapper.eq(Registration::getCompetitionId, competitionId)
                    .and(w -> {
                        w.eq(Registration::getUserId, userId);
                        if (!visibleTeamIds.isEmpty()) {
                            w.or().in(Registration::getTeamId, visibleTeamIds);
                        }
                    });
            Set<Long> visibleRegistrationIds = registrationMapper.selectList(regWrapper).stream()
                    .map(Registration::getId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());

            works = works.stream()
                    .filter(w -> (w.getUserId() != null && w.getUserId().equals(userId))
                            || (w.getRegistrationId() != null && visibleRegistrationIds.contains(w.getRegistrationId())))
                    .collect(Collectors.toList());
        }

        // 批量查询参赛者信息
        Set<Long> registrationIds = works.stream()
                .map(Work::getRegistrationId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        final Map<Long, String> workIdToParticipantName;
        final Map<Long, String> workIdToTeamName;
        final Map<Long, String> workIdToParticipationType;
        if (!registrationIds.isEmpty()) {
            List<Registration> registrations = registrationMapper.selectBatchIds(registrationIds);
            Set<Long> userIds = registrations.stream()
                    .map(Registration::getUserId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
            Set<Long> teamIds = registrations.stream()
                    .map(Registration::getTeamId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
            Map<Long, User> userMap = userIds.isEmpty() ? Map.of() :
                    userMapper.selectBatchIds(userIds).stream()
                            .collect(Collectors.toMap(User::getId, u -> u));
            Map<Long, Team> teamMap = teamIds.isEmpty() ? Map.of() :
                    teamMapper.selectBatchIds(teamIds).stream()
                            .collect(Collectors.toMap(Team::getId, t -> t));
            Map<Long, Registration> registrationMap = registrations.stream()
                    .collect(Collectors.toMap(Registration::getId, r -> r, (a, b) -> a));

            Map<Long, String> participantNames = new HashMap<>();
            Map<Long, String> teamNames = new HashMap<>();
            Map<Long, String> participationTypes = new HashMap<>();
            for (Work work : works) {
                Registration registration = registrationMap.get(work.getRegistrationId());
                if (registration == null) {
                    continue;
                }
                User user = registration.getUserId() != null ? userMap.get(registration.getUserId()) : null;
                if (user != null) {
                    participantNames.put(work.getId(), user.getName());
                }
                Team team = registration.getTeamId() != null ? teamMap.get(registration.getTeamId()) : null;
                if (team != null) {
                    teamNames.put(work.getId(), team.getName());
                }
                if (registration.getParticipationType() != null) {
                    participationTypes.put(work.getId(), registration.getParticipationType());
                } else if (team != null) {
                    participationTypes.put(work.getId(), "TEAM");
                }
            }

            workIdToParticipantName = participantNames;
            workIdToTeamName = teamNames;
            workIdToParticipationType = participationTypes;
        } else {
            workIdToParticipantName = Map.of();
            workIdToTeamName = Map.of();
            workIdToParticipationType = Map.of();
        }

        // 批量查询奖项
        LambdaQueryWrapper<Award> awardWrapper = new LambdaQueryWrapper<>();
        awardWrapper.eq(Award::getCompetitionId, competitionId);
        Map<Long, String> workIdToAwardLevel = list(awardWrapper).stream()
                .filter(a -> a.getWorkId() != null)
                .collect(Collectors.toMap(
                        Award::getWorkId,
                        Award::getAwardLevel,
                        (a, b) -> a
                ));

        // 批量查询评审评语
        LambdaQueryWrapper<ReviewTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(ReviewTask::getCompetitionId, competitionId)
                .eq(ReviewTask::getStatus, "COMPLETED")
                .isNotNull(ReviewTask::getComment);
        Map<Long, String> workIdToComments = reviewTaskMapper.selectList(taskWrapper).stream()
                .collect(Collectors.groupingBy(
                        ReviewTask::getWorkId,
                        Collectors.mapping(ReviewTask::getComment, Collectors.joining("; "))
                ));
        
        return works.stream()
                .map(work -> {
                    ScoreVO vo = new ScoreVO();
                    vo.setWorkId(work.getId());
                    vo.setWorkTitle(work.getTitle());
                    vo.setAvgScore(work.getAvgScore());
                    vo.setParticipantName(workIdToParticipantName.getOrDefault(work.getId(), null));
                    vo.setTeamName(workIdToTeamName.getOrDefault(work.getId(), null));
                    vo.setParticipationType(workIdToParticipationType.getOrDefault(work.getId(), null));
                    vo.setAwardLevel(workIdToAwardLevel.getOrDefault(work.getId(), null));
                    vo.setReviewComment(workIdToComments.getOrDefault(work.getId(), null));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Award> getFinishedAwards(Long competitionId) {
        LambdaQueryWrapper<Award> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Award::getCompetitionId, competitionId);
        return list(wrapper);
    }

    private boolean isReviewStatus(String status) {
        return "REVIEW".equals(status) || "REVIEWED".equals(status);
    }
}
