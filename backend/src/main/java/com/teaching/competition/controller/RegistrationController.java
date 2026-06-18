package com.teaching.competition.controller;

import com.teaching.competition.common.PageResult;
import com.teaching.competition.common.Result;
import com.teaching.competition.common.annotation.OpLog;
import com.teaching.competition.dto.RegistrationDTO;
import com.teaching.competition.dto.ReviewRejectDTO;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.RegistrationService;
import com.teaching.competition.vo.RegistrationVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    @PostMapping
    @OpLog(module = "报名管理", operation = "提交报名")
    public Result<Void> apply(@RequestBody @Valid RegistrationDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        registrationService.applyRegistration(dto, user.getId());
        return Result.success();
    }

    @GetMapping("/my")
    public Result<PageResult<RegistrationVO>> getMyRegistrations(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        PageResult<RegistrationVO> result = registrationService.getMyRegistrations(user.getId(), status, page, size);
        return Result.success(result);
    }

    @PutMapping("/{id}/approve")
    @OpLog(module = "报名管理", operation = "通过报名审核")
    public Result<Void> approve(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        registrationService.approveRegistration(id, user.getId(), user.getRole());
        return Result.success();
    }

    @PutMapping("/reject")
    @OpLog(module = "报名管理", operation = "驳回报名")
    public Result<Void> reject(@RequestBody @Valid ReviewRejectDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        registrationService.rejectRegistration(dto, user.getId(), user.getRole());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OpLog(module = "报名管理", operation = "删除报名")
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        registrationService.deleteRegistration(id, user.getId(), user.getRole());
        return Result.success();
    }

    @GetMapping("/admin/list")
    public Result<PageResult<RegistrationVO>> getAdminList(
            @RequestParam(required = false) Long competitionId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        PageResult<RegistrationVO> result = registrationService.getAdminRegistrations(competitionId, status, keyword, page, size, user.getId(), user.getRole());
        return Result.success(result);
    }
}
