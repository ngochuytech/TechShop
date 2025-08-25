package com.project.techstore.controllers;

import com.project.techstore.services.verification.IVerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/verify")
@RequiredArgsConstructor
public class VerificationController {

    private final IVerificationTokenService verificationTokenService;

    @PostMapping("/verify-email")
    public ResponseEntity<?> sendVerificationEmail(@RequestParam("email") String email){
        try {
            verificationTokenService.createVerificaitonEmailToken(email);
            return ResponseEntity.ok("Mã xác nhận đã được gửi vào email của bạn");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token){
        try {
            verificationTokenService.verifyAccount(token);
            return ResponseEntity.ok("Xác nhận thành công, bạn có thể đăng nhập");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
