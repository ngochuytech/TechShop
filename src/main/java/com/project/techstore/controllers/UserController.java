package com.project.techstore.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.techstore.components.JwtTokenProvider;
import com.project.techstore.dtos.GoogleCodeRequest;
import com.project.techstore.dtos.UserLoginDTO;
import com.project.techstore.dtos.user.ChangePasswordDTO;
import com.project.techstore.dtos.user.UpdateProfileDTO;
import com.project.techstore.dtos.user.UserDTO;
import com.project.techstore.models.Token;
import com.project.techstore.models.User;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.user.LoginResponse;
import com.project.techstore.responses.user.UserResponse;
import com.project.techstore.services.auth.GoogleAuthService;
import com.project.techstore.services.token.ITokenService;
import com.project.techstore.services.user.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    private final ITokenService tokenService;

    private final GoogleAuthService googleAuthService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserCurrent(@AuthenticationPrincipal User user) {
        try {
            User userCurrent = userService.getUserById(user.getId());
            UserResponse userResponse = UserResponse.fromUser(userCurrent);
            return ResponseEntity.ok(ApiResponse.ok(userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, String.join(", ", errorMessages)));
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword()))
                return ResponseEntity.badRequest().body(ApiResponse.error("Password doesn't match"));
            userService.createUser(userDTO);
            return ResponseEntity.ok(ApiResponse.ok("Đã nhập thông tin thành công. Cần xác nhận email!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO userLoginDTO,
            BindingResult result,
            HttpServletResponse response) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, String.join(", ", errorMessages)));
        }
        String token = userService.loginUser(userLoginDTO);
        User user = userService.getUserByToken(token);
        Token jwtToken = tokenService.addToken(user, token);

        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtToken.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        response.addCookie(refreshTokenCookie);

        LoginResponse loginResponse = LoginResponse.builder()
                .message("Đăng nhập thành công!")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .username(user.getFullName())
                .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(user.getId())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(loginResponse));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Refresh token không tồn tại"));
        }

        Token tokenEntity = tokenService.findByRefreshToken(refreshToken);
        if (tokenEntity == null || tokenEntity.isRevoked()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Refresh token không hợp lệ hoặc đã bị thu hồi"));
        }

        User user = userService.getUserById(tokenEntity.getUser().getId());
        Token newToken = tokenService.refreshToken(refreshToken, user);

        Cookie refreshTokenCookie = new Cookie("refreshToken", newToken.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);

        LoginResponse loginResponse = LoginResponse.builder()
                .message("Làm mới token thành công!")
                .token(newToken.getToken())
                .tokenType(newToken.getTokenType())
                .username(user.getFullName())
                .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(user.getId())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken != null) {
            try {
                tokenService.revokeToken(refreshToken);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
            }
        }

        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(false);
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0);
        response.addCookie(deleteCookie);

        return ResponseEntity.ok(ApiResponse.ok("Đăng xuất thành công"));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal User user,
            @RequestBody @Valid UpdateProfileDTO profileDTO,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, String.join(", ", errorMessages)));
            }
            userService.updateProfile(user.getId(), profileDTO);
            return ResponseEntity.ok("Update profile successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/update-phone-user/{idUser}")
    public ResponseEntity<?> updatePhoneUser(@PathVariable("idUser") String idUser, @RequestParam String phoneNumber) {
        try {
            userService.updatePhoneUser(idUser, phoneNumber);
            return ResponseEntity.ok("Update phone successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/update-user/{idUser}")
    public ResponseEntity<?> updateUser(@PathVariable("idUser") String idUser, @RequestBody @Valid UserDTO userDTO,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, String.join(", ", errorMessages)));
            }
            userService.updateUser(idUser, userDTO);
            return ResponseEntity.ok("Update profile successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestPart("avatar") MultipartFile avatar,
            @AuthenticationPrincipal User user) throws Exception {
        userService.updateAvatar(user, avatar);
        return ResponseEntity.ok("Update avatar successfully");
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal User user,
            @RequestBody @Valid ChangePasswordDTO changePasswordDTO,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, String.join(", ", errorMessages)));
            }
            
            if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Mật khẩu xác nhận không khớp"));
            }
            
            userService.changePassword(user, changePasswordDTO.getCurrentPassword(), 
                    changePasswordDTO.getNewPassword());
            return ResponseEntity.ok(ApiResponse.ok("Đổi mật khẩu thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/auth/social-login")
    public void socialAuth(HttpServletResponse response) throws IOException {
        String url = googleAuthService.generateAuthUrl();
        response.sendRedirect(url);
    }

    @PostMapping("/auth/social/callback")
    public ResponseEntity<?> callback(@RequestBody GoogleCodeRequest request,
            HttpServletResponse response) {
        try {
            User user = googleAuthService.loginWithGoogle(request);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(user);
            Token jwtToken = tokenService.addToken(user, token);

            Cookie refreshTokenCookie = new Cookie("refreshToken", jwtToken.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(refreshTokenCookie);

            LoginResponse loginResponse = LoginResponse.builder()
                    .message("Đăng nhập thành công!")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .username(user.getFullName())
                    .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .id(user.getId())
                    .build();

            return ResponseEntity.ok(ApiResponse.ok(loginResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
