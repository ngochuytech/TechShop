package com.project.techstore.services.notification;

import com.project.techstore.dtos.NotificationDTO;
import com.project.techstore.models.Notification;
import com.project.techstore.models.Order;
import com.project.techstore.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface INotificationService {
    Notification createNotification(NotificationDTO notificationDTO) throws Exception;

    Page<Notification> getNotificationByUser(String userId, Pageable pageable) throws Exception;

    Notification markReadNotification(String id) throws Exception;

    Notification updateNotification(String id, NotificationDTO notificationDTO) throws Exception;

    void deleteNotification(String id) throws Exception;

    void createOrderNotification(User user, Order order, String status) throws Exception;
}
