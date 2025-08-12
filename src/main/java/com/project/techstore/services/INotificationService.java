package com.project.techstore.services;

import com.project.techstore.dtos.NotificationDTO;
import com.project.techstore.models.Notification;

import java.util.List;

public interface INotificationService {
    Notification createNotification(NotificationDTO notificationDTO) throws Exception;

    List<Notification> getNotificationByUser(String userId) throws Exception;

    Notification updateNotification(String id, NotificationDTO notificationDTO) throws Exception;

    void deleteNotification(String id) throws Exception;
}
