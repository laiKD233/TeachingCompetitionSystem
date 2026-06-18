package com.teaching.competition.common.aspect;

import com.teaching.competition.common.annotation.OpLog;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @Around("@annotation(com.teaching.competition.common.annotation.OpLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String result = "SUCCESS";
        Object returnValue = null;

        try {
            returnValue = joinPoint.proceed();
            return returnValue;
        } catch (Throwable e) {
            result = "FAIL: " + e.getMessage();
            throw e;
        } finally {
            try {
                saveLog(joinPoint, result, System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.error("记录操作日志失败", e);
            }
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, String result, long duration) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OpLog annotation = method.getAnnotation(OpLog.class);

        String module = annotation.module();
        String operation = annotation.operation();

        Long userId = null;
        String username = null;
        String role = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            userId = user.getId();
            username = user.getUsername();
            role = user.getRole();
        }

        String ip = getIpAddress();

        operationLogService.log(userId, username, role, module, operation, null, ip, result);
    }

    private String getIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return "unknown";

        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
