package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.dto.ReviewAssignDTO;
import com.teaching.competition.dto.ReviewScoreDTO;
import com.teaching.competition.entity.ReviewTask;
import com.teaching.competition.entity.User;
import com.teaching.competition.vo.ReviewTaskVO;
import com.teaching.competition.vo.ScoreVO;

import java.util.List;
import java.util.Map;

public interface ReviewTaskService extends IService<ReviewTask> {
    void assignReviews(ReviewAssignDTO dto, Long adminId, String role);
    void submitScore(ReviewScoreDTO dto, Long reviewerId);
    List<ScoreVO> getWorkScores(Long competitionId, Long adminId, String role);
    List<ReviewTask> getMyReviewTasks(Long reviewerId);
    List<ReviewTaskVO> getMyReviewTaskDetails(Long reviewerId);
    void autoCalculateScore(Long competitionId, Long adminId, String role);
    List<Map<String, Object>> getEligibleReviewers();
    void adminSubmitScore(Long workId, Double score, String comment, Long adminId, String role);
    List<ReviewTask> getWorkReviewTasks(Long workId, Long adminId, String role);
}
