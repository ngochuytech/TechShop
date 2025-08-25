package com.project.techstore.services.verification;

import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.exceptions.InvalidParamException;
import com.project.techstore.models.User;
import com.project.techstore.models.VerificationToken;
import com.project.techstore.repositories.UserRepository;
import com.project.techstore.repositories.VerificationTokenRepository;
import com.project.techstore.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService implements IVerificationTokenService {
    @Value("${api.prefix}")
    private String apiPrefix;

    private final VerificationTokenRepository verificationTokenRepository;

    private final UserRepository userRepository;

    private final EmailService emailService;

    @Override
    @Transactional
    public void createVerificaitonEmailToken(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("Email này không tồn tại"));

        // Xoá token cũ nếu có
        verificationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        // Hết hạn sau 5 min
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(5);

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .build();
        verificationTokenRepository.save(verificationToken);

        // Truyền link phù hợp vào !
        String resetLink = apiPrefix + "/verify/verify-email?token=" + token;
        emailService.sendSimpleEmail(user.getEmail(), "Xác nhận tài khoản của bạn",
                "Vui lòng click vào link để xác nhận: " + resetLink);
    }

    @Override
    @Transactional
    public void verifyAccount(String token) throws Exception {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new DataNotFoundException("Token không tồn tại"));
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidParamException("Token hết hạn !");
        }

        User user = verificationToken.getUser();
        user.setEnable(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }
}
