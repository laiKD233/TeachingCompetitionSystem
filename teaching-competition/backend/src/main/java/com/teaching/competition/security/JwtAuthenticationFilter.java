package com.teaching.competition.security;

import com.teaching.competition.entity.User;
import com.teaching.competition.mapper.UserMapper;
import com.teaching.competition.utils.RedisUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            String cachedToken = redisUtil.getStr("token:" + userId);
            if (cachedToken != null && cachedToken.equals(token)) {
                User user = redisUtil.get("user:" + userId, User.class);
                if (user == null) {
                    user = userMapper.selectById(userId);
                    redisUtil.set("user:" + userId, user, 3600, TimeUnit.SECONDS);
                }
                if (user != null) {
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority(user.getRole())
                    );
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
