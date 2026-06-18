package com.teaching.competition.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teaching.competition.common.Result;
import com.teaching.competition.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/api/competition/public/**",
                    "/api/award/finished/**",
                    "/api/award/public/**",
                    "/api/upload",
                    "/uploads/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/file/download/**").permitAll()
                .requestMatchers("/api/super-admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/competition-admin/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/dashboard/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/file/delete").hasAuthority("ADMIN")
                .requestMatchers("/api/participant/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR", "STUDENT")
                .requestMatchers("/api/competition/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/team/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR", "STUDENT")
                .requestMatchers("/api/schedule/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/appointment/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR", "STUDENT")
                .requestMatchers("/api/todo/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR", "STUDENT")
                .requestMatchers("/api/work/admin/**").hasAnyAuthority("ADMIN", "TEACHER")
                .requestMatchers("/api/review/assign").hasAnyAuthority("ADMIN", "TEACHER")
                .requestMatchers("/api/review/admin-score").hasAnyAuthority("ADMIN", "TEACHER")
                .requestMatchers("/api/review/scores/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/review/reviewers").hasAnyAuthority("ADMIN", "TEACHER")
                .requestMatchers("/api/review/work-tasks/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/review/my-tasks").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/review/my-tasks-detail").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/review/score").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/award/publish").hasAnyAuthority("ADMIN", "TEACHER")
                .requestMatchers("/api/award/announcement/**").hasAnyAuthority("ADMIN", "TEACHER")
                .requestMatchers("/api/award/list/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/award/results/**").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR", "STUDENT")
                .requestMatchers("/api/registration/admin/list").hasAnyAuthority("ADMIN", "TEACHER", "ADVISOR")
                .requestMatchers("/api/registration/*/approve").hasAnyAuthority("ADMIN", "TEACHER")
                .requestMatchers("/api/registration/reject").hasAnyAuthority("ADMIN", "TEACHER")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    new ObjectMapper().writeValue(response.getOutputStream(),
                            Result.error(401, "未登录或token已过期"));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    new ObjectMapper().writeValue(response.getOutputStream(),
                            Result.error(403, "权限不足"));
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
