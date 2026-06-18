package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.entity.Notification;
import com.teaching.competition.mapper.NotificationMapper;
import com.teaching.competition.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public void sendNotification(Long userId, String title, String content, String type, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setIsRead(false);
        save(notification);
    }

    @Override
    public void sendNotificationToUsers(List<Long> userIds, String title, String content, String type, Long relatedId) {
        for (Long userId : userIds) {
            sendNotification(userId, title, content, type, relatedId);
        }
    }

    @Override
    public List<Notification> getMyNotifications(Long userId, Boolean isRead) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }
        wrapper.orderByDesc(Notification::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return count(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, false));
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getId, notificationId)
                .eq(Notification::getUserId, userId)
                .set(Notification::getIsRead, true);
        update(wrapper);
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, false)
                .set(Notification::getIsRead, true);
        update(wrapper);
    }
}
