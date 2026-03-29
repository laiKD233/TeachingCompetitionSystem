package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.LoginDTO;
import com.teaching.competition.dto.RegisterDTO;
import com.teaching.competition.service.UserService;
import com.teaching.competition.vo.LoginVO;
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
    public Result<Void> logout() {
        return Result.success();
    }
}
