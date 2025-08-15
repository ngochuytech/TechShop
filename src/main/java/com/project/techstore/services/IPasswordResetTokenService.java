package com.project.techstore.services;

public interface IPasswordResetTokenService {
    void createPasswordResetToken(String email) throws Exception;
}
