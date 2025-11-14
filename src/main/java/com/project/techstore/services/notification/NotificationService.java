package com.project.techstore.services.notification;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.techstore.dtos.NotificationDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Notification;
import com.project.techstore.models.Order;
import com.project.techstore.models.User;
import com.project.techstore.repositories.NotificationRepository;
import com.project.techstore.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService{

    private final UserRepository userRepository;

    private final NotificationRepository notificationRepository;

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
    public Page<Notification> getNotificationByUser(String userId, Pageable pageable) throws Exception {
        return notificationRepository.findByUserId(userId, pageable);
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

    @Override
    public void createOrderNotification(User user, Order order, String status) throws Exception {
        String title = "";
        String content = "";
        
        switch (status) {
            case "PENDING":
                title = "Đơn hàng đã được tạo";
                content = String.format("Đơn hàng #%s của bạn đã được tạo thành công và đang chờ xác nhận. Tổng giá trị: %,d VNĐ", 
                    order.getId(), order.getTotalPrice());
                break;
            case "CONFIRMED":
                title = "Đơn hàng đã được xác nhận";
                content = String.format("Đơn hàng #%s của bạn đã được xác nhận và đang được chuẩn bị.", 
                    order.getId());
                break;
            case "SHIPPING":
                title = "Đơn hàng đang được giao";
                content = String.format("Đơn hàng #%s của bạn đang được vận chuyển. Vui lòng chú ý điện thoại để nhận hàng.", 
                    order.getId());
                break;
            case "DELIVERED":
                title = "Đơn hàng đã được giao thành công";
                content = String.format("Đơn hàng #%s của bạn đã được giao thành công. Cảm ơn bạn đã mua hàng!", 
                    order.getId());
                break;
            case "CANCELLED":
                title = "Đơn hàng đã bị hủy";
                content = String.format("Đơn hàng #%s của bạn đã bị hủy. Nếu bạn có thắc mắc, vui lòng liên hệ với chúng tôi.", 
                    order.getId());
                break;
            default:
                return;
        }
        
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .isRead(false)
                .user(user)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public Notification markReadNotification(String id) throws Exception {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Notification doesn't exist"));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
}
