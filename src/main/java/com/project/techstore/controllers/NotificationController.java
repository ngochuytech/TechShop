package com.project.techstore.controllers;

import com.project.techstore.dtos.NotificationDTO;
import com.project.techstore.models.Notification;
import com.project.techstore.services.INotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationService notificationService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getNotificationByUser(@PathVariable("id") String userId){
        try {
            List<Notification> notificationList = notificationService.getNotificationByUser(userId);
            return ResponseEntity.ok(notificationList);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNotification(@RequestBody @Valid NotificationDTO notificationDTO, BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            notificationService.createNotification(notificationDTO);
            return ResponseEntity.ok("Create a new notification successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable("id") String id,@RequestBody @Valid NotificationDTO notificationDTO,
                                                BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            notificationService.updateNotification(id, notificationDTO);
            return ResponseEntity.ok("Update a notification successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable("id") String id){
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok("Delete a notification successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
