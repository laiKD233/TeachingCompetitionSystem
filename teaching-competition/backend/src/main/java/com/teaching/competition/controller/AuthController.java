package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.LoginDTO;
import com.teaching.competition.dto.RegisterDTO;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.UserService;
import com.teaching.competition.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @GetMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        // 前端会在收到响应后清除本地 token
        // 如需服务端 token 失效，可接入 Redis token 黑名单
        return Result.success();
    }
}
