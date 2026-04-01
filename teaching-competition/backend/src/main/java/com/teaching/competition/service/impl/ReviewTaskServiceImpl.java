package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.ReviewAssignDTO;
import com.teaching.competition.dto.ReviewScoreDTO;
import com.teaching.competition.entity.Registration;
import com.teaching.competition.entity.ReviewTask;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.User;
import com.teaching.competition.entity.Work;
import com.teaching.competition.vo.ReviewTaskVO;
import com.teaching.competition.entity.Work;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.RegistrationMapper;
import com.teaching.competition.mapper.ReviewTaskMapper;
import com.teaching.competition.mapper.UserMapper;
import com.teaching.competition.service.ReviewTaskService;
import com.teaching.competition.service.WorkService;
import com.teaching.competition.vo.ScoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewTaskServiceImpl extends ServiceImpl<ReviewTaskMapper, ReviewTask> implements ReviewTaskService {

    private final WorkService workService;
    private final RegistrationMapper registrationMapper;
    private final UserMapper userMapper;
    private final com.teaching.competition.mapper.CompetitionMapper competitionMapper;

    @Override
    @Transactional
    public void assignReviews(ReviewAssignDTO dto) {
        List<ReviewAssignDTO.ReviewItemDTO> items = dto.getItems();

        // 兼容前端当前的简化调用：仅传单个 workId 和 reviewerId
        if ((items == null || items.isEmpty()) && dto.getWorkId() != null && dto.getReviewerId() != null) {
            ReviewAssignDTO.ReviewItemDTO item = new ReviewAssignDTO.ReviewItemDTO();
            item.setWorkId(dto.getWorkId());
            item.setReviewerId(dto.getReviewerId());
            items = List.of(item);
        }

        if (items == null || items.isEmpty()) {
            throw new BusinessException("评审分配不能为空");
        }

        for (ReviewAssignDTO.ReviewItemDTO item : items) {
            Work work = workService.getById(item.getWorkId());
            if (work == null) {
                throw new BusinessException("作品不存在");
            }

            // 检查是否重复分配
            LambdaQueryWrapper<ReviewTask> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(ReviewTask::getWorkId, item.getWorkId())
                    .eq(ReviewTask::getReviewerId, item.getReviewerId());
            if (count(checkWrapper) > 0) {
                throw new BusinessException("该评审人已被分配到此作品，请勿重复分配");
            }

            ReviewTask task = new ReviewTask();
            task.setCompetitionId(work.getCompetitionId());
            task.setWorkId(item.getWorkId());
            task.setReviewerId(item.getReviewerId());
            task.setStatus("PENDING");
            save(task);
        }
    }

    @Override
    @Transactional
    public void submitScore(ReviewScoreDTO dto, Long reviewerId) {
        ReviewTask task = getById(dto.getTaskId());
        if (task == null) {
            throw new BusinessException("评审任务不存在");
        }
        if (!task.getReviewerId().equals(reviewerId)) {
            throw new BusinessException("无权限提交该评审");
        }
        
        task.setScore(dto.getScore());
        task.setComment(dto.getComment());
        task.setStatus("COMPLETED");
        updateById(task);
        
        autoCalculateScore(task.getCompetitionId());
    }

    @Override
    public List<ScoreVO> getWorkScores(Long competitionId) {
        // 1. 查询该竞赛下所有已提交作品
        LambdaQueryWrapper<Work> workWrapper = new LambdaQueryWrapper<>();
        workWrapper.eq(Work::getCompetitionId, competitionId);
        List<Work> works = workService.list(workWrapper);

        if (works.isEmpty()) {
            return List.of();
        }

        // 2. 查询该竞赛下所有已完成的评审任务
        LambdaQueryWrapper<ReviewTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(ReviewTask::getCompetitionId, competitionId)
                .eq(ReviewTask::getStatus, "COMPLETED");
        List<ReviewTask> tasks = list(taskWrapper);

        Map<Long, DoubleSummaryStatistics> scoreStatsByWork = tasks.stream()
                .collect(Collectors.groupingBy(
                        ReviewTask::getWorkId,
                        Collectors.summarizingDouble(t -> t.getScore() != null ? t.getScore() : 0.0)
                ));

        // 3. 组装每个作品的评分视图对象
        return works.stream()
                .map(work -> {
                    ScoreVO vo = new ScoreVO();
                    vo.setWorkId(work.getId());
                    vo.setWorkTitle(work.getTitle());
                    vo.setFileUrl(work.getFileUrl());
                    vo.setFileName(work.getFileName());

                    // 参赛者姓名（通过报名记录和用户表反查）
                    if (work.getRegistrationId() != null) {
                        Registration registration = registrationMapper.selectById(work.getRegistrationId());
                        if (registration != null && registration.getUserId() != null) {
                            User user = userMapper.selectById(registration.getUserId());
                            if (user != null) {
                                vo.setParticipantName(user.getName());
                            }
                        }
                    }

                    DoubleSummaryStatistics stats = scoreStatsByWork.get(work.getId());
                    if (stats != null && stats.getCount() > 0) {
                        vo.setAvgScore(stats.getAverage());
                    } else {
                        vo.setAvgScore(null);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewTask> getMyReviewTasks(Long reviewerId) {
        LambdaQueryWrapper<ReviewTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewTask::getReviewerId, reviewerId)
                .orderByDesc(ReviewTask::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public List<ReviewTaskVO> getMyReviewTaskDetails(Long reviewerId) {
        List<ReviewTask> tasks = getMyReviewTasks(reviewerId);
        return tasks.stream().map(task -> {
            ReviewTaskVO vo = new ReviewTaskVO();
            vo.setId(task.getId());
            vo.setCompetitionId(task.getCompetitionId());
            vo.setWorkId(task.getWorkId());
            vo.setReviewerId(task.getReviewerId());
            vo.setScore(task.getScore());
            vo.setComment(task.getComment());
            vo.setStatus(task.getStatus());
            vo.setCreatedAt(task.getCreatedAt());

            Work work = workService.getById(task.getWorkId());
            if (work != null) {
                vo.setWorkTitle(work.getTitle());
                vo.setWorkFileUrl(work.getFileUrl());
                vo.setWorkFileName(work.getFileName());

                Competition comp = competitionMapper.selectById(task.getCompetitionId());
                if (comp != null) {
                    vo.setCompetitionName(comp.getName());
                }

                if (work.getRegistrationId() != null) {
                    Registration reg = registrationMapper.selectById(work.getRegistrationId());
                    if (reg != null && reg.getUserId() != null) {
                        User participant = userMapper.selectById(reg.getUserId());
                        if (participant != null) {
                            vo.setParticipantName(participant.getName());
                        }
                    }
                }
            }

            User reviewer = userMapper.selectById(task.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getName());
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void autoCalculateScore(Long competitionId) {
        LambdaQueryWrapper<ReviewTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewTask::getCompetitionId, competitionId)
                .eq(ReviewTask::getStatus, "COMPLETED")
                .isNotNull(ReviewTask::getScore);
        
        List<ReviewTask> tasks = list(wrapper);
        
        tasks.stream()
                .collect(Collectors.groupingBy(ReviewTask::getWorkId))
                .forEach((workId, taskList) -> {
                    double avgScore = taskList.stream()
                            .mapToDouble(t -> t.getScore() != null ? t.getScore() : 0.0)
                            .average()
                            .orElse(0.0);
                    
                    Work work = workService.getById(workId);
                    if (work != null) {
                        work.setAvgScore(avgScore);
                        workService.updateById(work);
                    }
                });
    }

    @Override
    public List<User> getEligibleReviewers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getRole, "TEACHER", "ADMIN")
                .eq(User::getStatus, 1);
        return userMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void adminSubmitScore(Long workId, Double score, String comment, Long adminId) {
        Work work = workService.getById(workId);
        if (work == null) {
            throw new BusinessException("作品不存在");
        }

        // 查找该管理员对此作品是否已有评审任务
        LambdaQueryWrapper<ReviewTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewTask::getWorkId, workId)
                .eq(ReviewTask::getReviewerId, adminId);
        ReviewTask task = getOne(wrapper);

        if (task == null) {
            // 自动创建一条评审任务
            task = new ReviewTask();
            task.setCompetitionId(work.getCompetitionId());
            task.setWorkId(workId);
            task.setReviewerId(adminId);
            task.setStatus("COMPLETED");
            task.setScore(score);
            task.setComment(comment);
            save(task);
        } else {
            task.setScore(score);
            task.setComment(comment);
            task.setStatus("COMPLETED");
            updateById(task);
        }

        autoCalculateScore(work.getCompetitionId());
    }

    @Override
    public List<ReviewTask> getWorkReviewTasks(Long workId) {
        LambdaQueryWrapper<ReviewTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewTask::getWorkId, workId)
                .orderByDesc(ReviewTask::getCreatedAt);
        return list(wrapper);
    }
}
