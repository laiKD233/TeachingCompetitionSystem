package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.dto.ReviewAssignDTO;
import com.teaching.competition.dto.ReviewScoreDTO;
import com.teaching.competition.entity.ReviewTask;
import com.teaching.competition.vo.ScoreVO;

import java.util.List;

public interface ReviewTaskService extends IService<ReviewTask> {
    void assignReviews(ReviewAssignDTO dto);
    void submitScore(ReviewScoreDTO dto, Long reviewerId);
    List<ScoreVO> getWorkScores(Long competitionId);
    List<ReviewTask> getMyReviewTasks(Long reviewerId);
    void autoCalculateScore(Long competitionId);
}
