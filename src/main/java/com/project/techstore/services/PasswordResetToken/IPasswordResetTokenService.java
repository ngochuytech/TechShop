package com.project.techstore.services.PasswordResetToken;

public interface IPasswordResetTokenService {
    void createPasswordResetToken(String email) throws Exception;
}
