package com.project.techstore.services.token;

import com.project.techstore.models.Token;
import com.project.techstore.models.User;

public interface ITokenService {
    Token addToken(User user, String token);

    Token refreshToken(String refreshToken, User user) throws Exception;
}
