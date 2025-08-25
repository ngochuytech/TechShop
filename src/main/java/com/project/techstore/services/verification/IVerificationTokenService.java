package com.project.techstore.services.verification;

public interface IVerificationTokenService {
    void createVerificaitonEmailToken(String email) throws Exception;

    void verifyAccount(String token) throws Exception;
}
