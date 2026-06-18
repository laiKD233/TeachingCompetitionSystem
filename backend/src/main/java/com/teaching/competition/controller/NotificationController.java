package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.entity.Notification;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @GetMapping("/my")
    public Result<List<Notification>> getMyNotifications(
            @RequestParam(required = false) Boolean isRead,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Notification> notifications = notificationService.getMyNotifications(user.getId(), isRead);
        return Result.success(notifications);
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(Authentication authentication) {
        User user = getCurrentUser(authentication);
        long count = notificationService.getUnreadCount(user.getId());
        return Result.success(count);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        notificationService.markAsRead(id, user.getId());
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(Authentication authentication) {
        User user = getCurrentUser(authentication);
        notificationService.markAllAsRead(user.getId());
        return Result.success();
    }
}
