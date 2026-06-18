package com.teaching.competition.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.competition.common.Result;
import com.teaching.competition.dto.PasswordUpdateDTO;
import com.teaching.competition.dto.UserUpdateDTO;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.UserService;
import com.teaching.competition.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/current")
    public Result<UserVO> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(userService.getCurrentUser(user.getId()));
    }

    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody @Valid UserUpdateDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.updateUserInfo(user.getId(), dto);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody PasswordUpdateDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.updatePassword(user.getId(), dto);
        return Result.success();
    }
}
