package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.ReviewAssignDTO;
import com.teaching.competition.dto.ReviewScoreDTO;
import com.teaching.competition.entity.ReviewTask;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.ReviewTaskService;
import com.teaching.competition.vo.ReviewTaskVO;
import com.teaching.competition.vo.ScoreVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewTaskService reviewTaskService;

    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    @PostMapping("/assign")
    public Result<Void> assignReviews(@RequestBody @Valid ReviewAssignDTO dto) {
        reviewTaskService.assignReviews(dto);
        return Result.success();
    }

    @PostMapping("/score")
    public Result<Void> submitScore(
            @RequestBody @Valid ReviewScoreDTO dto,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        reviewTaskService.submitScore(dto, user.getId());
        return Result.success();
    }

    @PostMapping("/admin-score")
    public Result<Void> adminSubmitScore(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Long workId = Long.valueOf(body.get("workId").toString());
        Double score = Double.valueOf(body.get("score").toString());
        String comment = body.get("comment") != null ? body.get("comment").toString() : null;
        reviewTaskService.adminSubmitScore(workId, score, comment, user.getId());
        return Result.success();
    }

    @GetMapping("/my-tasks")
    public Result<List<ReviewTask>> getMyTasks(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        List<ReviewTask> tasks = reviewTaskService.getMyReviewTasks(user.getId());
        return Result.success(tasks);
    }

    @GetMapping("/my-tasks-detail")
    public Result<List<ReviewTaskVO>> getMyTaskDetails(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        List<ReviewTaskVO> tasks = reviewTaskService.getMyReviewTaskDetails(user.getId());
        return Result.success(tasks);
    }

    @GetMapping("/scores/{competitionId}")
    public Result<List<ScoreVO>> getWorkScores(@PathVariable Long competitionId) {
        List<ScoreVO> scores = reviewTaskService.getWorkScores(competitionId);
        return Result.success(scores);
    }

    @GetMapping("/reviewers")
    public Result<List<User>> getEligibleReviewers() {
        List<User> reviewers = reviewTaskService.getEligibleReviewers();
        return Result.success(reviewers);
    }

    @GetMapping("/work-tasks/{workId}")
    public Result<List<ReviewTask>> getWorkReviewTasks(@PathVariable Long workId) {
        List<ReviewTask> tasks = reviewTaskService.getWorkReviewTasks(workId);
        return Result.success(tasks);
    }
}
