package com.project.techstore.services;

import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.PasswordResetToken;
import com.project.techstore.models.User;
import com.project.techstore.repositories.PasswordResetTokenRepository;
import com.project.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService implements IPasswordResetTokenService{

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final EmailService emailService;

    @Override
    public void createPasswordResetToken(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("Email này không tồn tại"));

        passwordResetTokenRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();

        // Hết hạn sau 5 min
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(5);

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .build();
        passwordResetTokenRepository.save(passwordResetToken);

        // Truyền link phù hợp vào !
        String resetLink = "${api.prefix}/auth/reset-password?token=" + token;
        emailService.sendSimpleEmail(user.getEmail(), "Khôi phục mật khẩu",
                "Click vào link để đặt lại mật khẩu: " + resetLink);
    }
}
