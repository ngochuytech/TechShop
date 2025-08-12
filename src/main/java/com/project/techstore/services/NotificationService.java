package com.project.techstore.services;

import com.project.techstore.dtos.NotificationDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Notification;
import com.project.techstore.models.User;
import com.project.techstore.repositories.NotificationRepository;
import com.project.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService{

    private final UserRepository userRepository;

    private final NotificationRepository notificationRepository;

    private final ModelMapper modelMapper;

    @Override
    public Notification createNotification(NotificationDTO notificationDTO) throws Exception {
        User user = userRepository.findById(notificationDTO.getUserId()).orElseThrow(() -> new DataNotFoundException("This user doesn't exist"));
        Notification notification = Notification.builder()
                .title(notificationDTO.getTitle())
                .content(notificationDTO.getContent())
                .isRead(notificationDTO.getIsRead())
                .user(user)
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationByUser(String userId) throws Exception {
        if(!userRepository.existsById(userId))
            throw new DataNotFoundException("User doesn't exist");
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public Notification updateNotification(String id, NotificationDTO notificationDTO) throws Exception {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Notification doesn't exist"));
        User user = userRepository.findById(notificationDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User doesn't exist"));
        BeanUtils.copyProperties(notificationDTO, notification, "id");
        notification.setUser(user);
        return notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(String id) throws Exception {
        if(!notificationRepository.existsById(id))
            throw new DataNotFoundException("Notification doesn't exist");
        notificationRepository.deleteById(id);
    }
}
