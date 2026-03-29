package com.teaching.competition.controller;

import com.teaching.competition.common.PageResult;
import com.teaching.competition.common.Result;
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
    public Result<Void> approve(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        registrationService.approveRegistration(id, user.getId());
        return Result.success();
    }

    @PutMapping("/reject")
    public Result<Void> reject(@RequestBody @Valid ReviewRejectDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        registrationService.rejectRegistration(dto, user.getId());
        return Result.success();
    }

    @GetMapping("/admin/list")
    public Result<PageResult<RegistrationVO>> getAdminList(
            @RequestParam(required = false) Long competitionId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<RegistrationVO> result = registrationService.getAdminRegistrations(competitionId, status, keyword, page, size);
        return Result.success(result);
    }
}
