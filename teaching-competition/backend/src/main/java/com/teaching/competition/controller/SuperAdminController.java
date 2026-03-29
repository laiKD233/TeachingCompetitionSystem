package com.teaching.competition.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.common.Result;
import com.teaching.competition.dto.UserManageDTO;
import com.teaching.competition.entity.OperationLog;
import com.teaching.competition.vo.UserVO;
import com.teaching.competition.service.OperationLogService;
import com.teaching.competition.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final UserService userService;
    private final OperationLogService operationLogService;

    @GetMapping("/users")//restful api
    public Result<PageResult<UserVO>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<UserVO> result = userService.getUserList(role, status, keyword, page, size);
        return Result.success(result);
    }

    @PostMapping("/users")//restful api
    public Result<Void> addUser(@RequestBody @Valid UserManageDTO dto) {
        userService.addUser(dto);
        return Result.success();
    }

    @PutMapping("/users/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody @Valid UserManageDTO dto) {
        userService.updateUser(id, dto);
        return Result.success();
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/users/{id}/freeze")
    public Result<Void> freezeUser(@PathVariable Long id) {
        userService.freezeUser(id);
        return Result.success();
    }

    @PutMapping("/users/{id}/unfreeze")
    public Result<Void> unfreezeUser(@PathVariable Long id) {
        userService.unfreezeUser(id);
        return Result.success();
    }

    @PutMapping("/users/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    @GetMapping("/logs")
    public Result<PageResult<OperationLog>> getLogs(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<OperationLog> result = operationLogService.getLogs(module, username, startDate, endDate, page, size);
        return Result.success(result);
    }
}
