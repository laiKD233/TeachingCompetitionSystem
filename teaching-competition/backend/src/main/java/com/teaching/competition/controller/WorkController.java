package com.teaching.competition.controller;

import com.teaching.competition.common.PageResult;
import com.teaching.competition.common.Result;
import com.teaching.competition.dto.WorkDTO;
import com.teaching.competition.entity.User;
import com.teaching.competition.entity.Work;
import com.teaching.competition.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/work")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    @PostMapping
    public Result<Void> upload(
            @RequestParam("file") MultipartFile file,
            WorkDTO dto,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        try {
            workService.uploadWork(dto, file, user.getId());
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<PageResult<Work>> getMyWorks(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        PageResult<Work> result = workService.getMyWorks(user.getId(), page, size);
        return Result.success(result);
    }

    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            WorkDTO dto,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        try {
            workService.updateWork(id, dto, file, user.getId());
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        try {
            // 先校验作品归属
            Work work = workService.getById(id);
            if (work == null) {
                return Result.error("作品不存在");
            }
            if (!work.getUserId().equals(user.getId())) {
                return Result.error("无权限删除该作品");
            }
            workService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Work> getDetail(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Work work = workService.getWorkDetail(id, user.getId());
        return Result.success(work);
    }

    @GetMapping("/admin/list")
    public Result<PageResult<Work>> getAdminWorks(
            @RequestParam Long competitionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Work> result = workService.getAdminWorks(competitionId, page, size);
        return Result.success(result);
    }
}
