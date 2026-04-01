package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.AwardDTO;
import com.teaching.competition.entity.Award;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.Registration;
import com.teaching.competition.entity.ReviewTask;
import com.teaching.competition.entity.User;
import com.teaching.competition.entity.Work;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.AwardMapper;
import com.teaching.competition.mapper.RegistrationMapper;
import com.teaching.competition.mapper.ReviewTaskMapper;
import com.teaching.competition.mapper.UserMapper;
import com.teaching.competition.service.AwardService;
import com.teaching.competition.service.CompetitionService;
import com.teaching.competition.service.WorkService;
import com.teaching.competition.vo.ScoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
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
        
        List<Award> awards = new ArrayList<>();
        for (AwardDTO.AwardItemDTO item : items) {
            Work work = workService.getById(item.getWorkId());
            if (work != null) {
                Award award = new Award();
                award.setCompetitionId(competitionId);
                award.setUserId(work.getUserId());
                award.setWorkId(item.getWorkId());
                award.setAwardLevel(item.getAwardLevel());
                awards.add(award);
            }
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
        
        competition.setStatus("ANNOUNCED");
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

        // 批量查询参赛者信息
        Set<Long> registrationIds = works.stream()
                .map(Work::getRegistrationId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        final Map<Long, String> workIdToParticipantName;
        if (!registrationIds.isEmpty()) {
            List<Registration> registrations = registrationMapper.selectBatchIds(registrationIds);
            Set<Long> userIds = registrations.stream()
                    .map(Registration::getUserId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
            Map<Long, User> userMap = userIds.isEmpty() ? Map.of() :
                    userMapper.selectBatchIds(userIds).stream()
                            .collect(Collectors.toMap(User::getId, u -> u));
            Map<Long, Long> regIdToUserId = registrations.stream()
                    .collect(Collectors.toMap(Registration::getId, Registration::getUserId, (a, b) -> a));

            workIdToParticipantName = works.stream()
                    .filter(w -> w.getRegistrationId() != null)
                    .collect(Collectors.toMap(
                            Work::getId,
                            w -> {
                                Long uid = regIdToUserId.get(w.getRegistrationId());
                                User user = uid != null ? userMap.get(uid) : null;
                                return user != null ? user.getName() : null;
                            },
                            (a, b) -> a
                    ));
        } else {
            workIdToParticipantName = Map.of();
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
}
