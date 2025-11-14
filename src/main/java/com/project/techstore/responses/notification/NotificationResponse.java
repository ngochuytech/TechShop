package com.project.techstore.responses.notification;

import com.project.techstore.models.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private String id;
    private String title;
    private String message;
    private String createdAt;
    private boolean isRead;

    public static NotificationResponse fromNotification(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getContent())
                .createdAt(notification.getCreatedAt().toString())
                .isRead(notification.getIsRead() != null ? notification.getIsRead() : false)
                .build();
    }
}
