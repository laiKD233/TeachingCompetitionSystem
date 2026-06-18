package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.common.annotation.OpLog;
import com.teaching.competition.dto.LoginDTO;
import com.teaching.competition.dto.RegisterDTO;
import com.teaching.competition.entity.User;
import com.teaching.competition.security.JwtTokenProvider;
import com.teaching.competition.service.UserService;
import com.teaching.competition.utils.RedisUtil;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @PostMapping("/register")
    @OpLog(module = "认证", operation = "用户注册")
    public Result<Void> register(@RequestBody @Valid RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @GetMapping("/logout")
    @OpLog(module = "认证", operation = "用户登出")
    public Result<Void> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getUserIdFromToken(token);
                redisUtil.delete("token:" + userId);
                redisUtil.delete("user:" + userId);
                redisUtil.setStr("token:blacklist:" + token, "1", 7, java.util.concurrent.TimeUnit.DAYS);
            }
        }
        return Result.success();
    }
}
