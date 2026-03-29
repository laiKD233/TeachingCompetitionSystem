package com.teaching.competition.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.common.Result;
import com.teaching.competition.dto.CompetitionDTO;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.CompetitionService;
import com.teaching.competition.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;
    private final UserService userService;

    @GetMapping("/public/list")
    @PreAuthorize("permitAll()") //公共接口，不需要登录
    public Result<PageResult<Competition>> getPublicList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Competition> result = competitionService.getPublicList(keyword, status, type, page, size);
        return Result.success(result);
    }

    @GetMapping("/public/detail/{id}")
    @PreAuthorize("permitAll()") 
    public Result<Competition> getPublicDetail(@PathVariable Long id) {
        Competition competition = competitionService.getPublicDetail(id);
        return Result.success(competition);
    }

    @GetMapping("/admin/list")
    public Result<PageResult<Competition>> getAdminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Competition> result = competitionService.getAdminList(keyword, status, page, size);
        return Result.success(result);
    }

    @GetMapping("/admin/detail/{id}")
    public Result<Competition> getAdminDetail(@PathVariable Long id) {
        Competition competition = competitionService.getAdminDetail(id);
        return Result.success(competition);
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid CompetitionDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        competitionService.createCompetition(dto, user.getId());
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid CompetitionDTO dto) {
        competitionService.updateCompetition(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        competitionService.deleteCompetition(id);
        return Result.success();
    }
}
