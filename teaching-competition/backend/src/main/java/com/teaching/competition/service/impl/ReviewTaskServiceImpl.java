package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.ReviewAssignDTO;
import com.teaching.competition.dto.ReviewScoreDTO;
import com.teaching.competition.entity.ReviewTask;
import com.teaching.competition.entity.Work;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.ReviewTaskMapper;
import com.teaching.competition.service.ReviewTaskService;
import com.teaching.competition.service.WorkService;
import com.teaching.competition.vo.ScoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewTaskServiceImpl extends ServiceImpl<ReviewTaskMapper, ReviewTask> implements ReviewTaskService {

    private final WorkService workService;

    @Override
    @Transactional
    public void assignReviews(ReviewAssignDTO dto) {
        List<ReviewAssignDTO.ReviewItemDTO> items = dto.getItems();
        if (items == null || items.isEmpty()) {
            throw new BusinessException("评审分配不能为空");
        }
        
        for (ReviewAssignDTO.ReviewItemDTO item : items) {
            Work work = workService.getById(item.getWorkId());
            if (work == null) {
                throw new BusinessException("作品不存在");
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
        LambdaQueryWrapper<ReviewTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewTask::getCompetitionId, competitionId)
                .eq(ReviewTask::getStatus, "COMPLETED");
        
        List<ReviewTask> tasks = list(wrapper);
        
        return tasks.stream()
                .collect(Collectors.groupingBy(ReviewTask::getWorkId))
                .entrySet().stream()
                .map(entry -> {
                    ScoreVO vo = new ScoreVO();
                    vo.setWorkId(entry.getKey());
                    List<Double> scores = entry.getValue().stream()
                            .map(ReviewTask::getScore)
                            .collect(Collectors.toList());
                    double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    vo.setAvgScore(avg);
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
    @Transactional
    public void autoCalculateScore(Long competitionId) {
        LambdaQueryWrapper<ReviewTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewTask::getCompetitionId, competitionId)
                .eq(ReviewTask::getStatus, "COMPLETED");
        
        List<ReviewTask> tasks = list(wrapper);
        
        tasks.stream()
                .collect(Collectors.groupingBy(ReviewTask::getWorkId))
                .forEach((workId, taskList) -> {
                    double avgScore = taskList.stream()
                            .mapToDouble(ReviewTask::getScore)
                            .average()
                            .orElse(0.0);
                    
                    Work work = workService.getById(workId);
                    if (work != null) {
                        work.setAvgScore(avgScore);
                        workService.updateById(work);
                    }
                });
    }
}
