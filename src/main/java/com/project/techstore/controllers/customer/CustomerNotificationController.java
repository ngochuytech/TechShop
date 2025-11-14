package com.project.techstore.controllers.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.Notification;
import com.project.techstore.models.User;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.notification.NotificationResponse;
import com.project.techstore.services.notification.INotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/customer/notifications")
@RequiredArgsConstructor
public class CustomerNotificationController {
    private final INotificationService notificationService;

    @GetMapping("/user")
    public ResponseEntity<?> getNotificationByUser(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal User user) throws Exception {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Notification> notificationList = notificationService.getNotificationByUser(user.getId(), pageable);
        Page<NotificationResponse> responsePage = notificationList.map(NotificationResponse::fromNotification);
        return ResponseEntity.ok(ApiResponse.ok(responsePage));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable("notificationId") String notificationId) throws Exception {
        Notification notification = notificationService.markReadNotification(notificationId);
        return ResponseEntity.ok(NotificationResponse.fromNotification(notification));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable("id") String id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok("Delete a notification successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
