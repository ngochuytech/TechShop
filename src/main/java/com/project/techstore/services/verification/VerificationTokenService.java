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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService implements IVerificationTokenService {
    @Value("${api.prefix}")
    private String apiPrefix;

    @Value("${app.backend-url:}")
    private String configuredBackendUrl;

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

        String backendUrl = configuredBackendUrl.isEmpty() 
            ? ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()
            : configuredBackendUrl;
        String verifyLink = backendUrl + "/" + apiPrefix + "/verify/verify-account?token=" + token;
        String htmlContent = buildVerificationEmail(user.getFullName(), verifyLink);
        emailService.sendHtmlEmail(user.getEmail(), "Xác nhận tài khoản TechStore", htmlContent);
    }
    
    private String buildVerificationEmail(String userName, String verifyLink) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"vi\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Xác nhận tài khoản</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;\">\n" +
                "    <div style=\"background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; text-align: center;\">\n" +
                "        <h1 style=\"color: white; margin: 0; font-size: 28px;\">TechStore</h1>\n" +
                "    </div>\n" +
                "    \n" +
                "    <div style=\"max-width: 600px; margin: 30px auto; padding: 20px; background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">\n" +
                "        <h2 style=\"color: #333; margin-top: 0;\">Xin chào " + userName + "!</h2>\n" +
                "        \n" +
                "        <p style=\"color: #555; font-size: 14px; margin: 15px 0;\">\n" +
                "            Cảm ơn bạn đã đăng ký tài khoản tại TechStore! \n" +
                "            Để hoàn tất quá trình đăng ký, vui lòng xác nhận địa chỉ email của bạn.\n" +
                "        </p>\n" +
                "        \n" +
                "        <p style=\"color: #555; font-size: 14px; margin: 15px 0;\">\n" +
                "            Nhấp vào nút bên dưới để xác nhận tài khoản của bạn. Link sẽ hết hạn sau 5 phút:\n" +
                "        </p>\n" +
                "        \n" +
                "        <div style=\"text-align: center; margin: 30px 0;\">\n" +
                "            <a href=\"" + verifyLink + "\" style=\"background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 12px 40px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold; font-size: 16px;\">\n" +
                "                Xác nhận tài khoản\n" +
                "            </a>\n" +
                "        </div>\n" +
                "        \n" +
                "        <p style=\"color: #999; font-size: 12px; margin: 20px 0; border-top: 1px solid #eee; padding-top: 20px;\">\n" +
                "            Nếu nút bên trên không hoạt động, sao chép và dán đường link này vào trình duyệt của bạn:\n" +
                "        </p>\n" +
                "        <p style=\"color: #667eea; font-size: 12px; word-break: break-all; background: #f5f5f5; padding: 10px; border-radius: 5px;\">\n" +
                "            " + verifyLink + "\n" +
                "        </p>\n" +
                "        \n" +
                "        <p style=\"color: #999; font-size: 12px; margin-top: 20px;\">\n" +
                "            Nếu bạn không tạo tài khoản này, vui lòng bỏ qua email này.\n" +
                "        </p>\n" +
                "        \n" +
                "        <p style=\"color: #999; font-size: 12px;\">\n" +
                "            Đây là email tự động, vui lòng không trả lời email này.\n" +
                "        </p>\n" +
                "    </div>\n" +
                "    \n" +
                "    <div style=\"text-align: center; padding: 20px; color: #999; font-size: 12px; background: #f9f9f9;\">\n" +
                "        <p>© 2024 TechStore. All rights reserved.</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
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
