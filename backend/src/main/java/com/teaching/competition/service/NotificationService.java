package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.entity.Notification;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    void sendNotification(Long userId, String title, String content, String type, Long relatedId);
    void sendNotificationToUsers(List<Long> userIds, String title, String content, String type, Long relatedId);
    List<Notification> getMyNotifications(Long userId, Boolean isRead);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
}
