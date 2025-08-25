package com.project.techstore.controllers;

import com.project.techstore.components.JwtTokenProvider;
import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.GoogleCodeRequest;
import com.project.techstore.dtos.UserDTO;
import com.project.techstore.dtos.UserLoginDTO;
import com.project.techstore.models.Token;
import com.project.techstore.models.User;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.user.LoginResponse;
import com.project.techstore.services.auth.GoogleAuthService;
import com.project.techstore.services.user.UserService;
import com.project.techstore.services.token.ITokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    private final ITokenService tokenService;

    private final GoogleAuthService googleAuthService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, String.join(", ", errorMessages)));
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword()))
                return ResponseEntity.badRequest().body(ApiResponse.error("Password doesn't match"));
            // CẦN KIỂM TRA USER ĐÃ XÁC MINH EMAIL CHƯA !!
            userService.createUser(userDTO);
            return ResponseEntity.ok(ApiResponse.ok("Đã nhập thông tin thành công. Cần xác nhận email!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO userLoginDTO, BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, String.join(", ", errorMessages)));
            }
            String token = userService.loginUser(userLoginDTO);
            User user = userService.getUserByToken(token);
            Token jwtToken = tokenService.addToken(user, token);
            LoginResponse loginResponse = LoginResponse.builder()
                    .message("Đăng nhập thành công!")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(user.getFullName())
                    .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .id(user.getId())
                    .build();

            return ResponseEntity.ok(ApiResponse.ok(loginResponse));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/update-phone-user/{idUser}")
    public ResponseEntity<?> updatePhoneUser(@PathVariable("idUser") String idUser,@RequestParam String phoneNumber) {
        try {
            userService.updatePhoneUser(idUser, phoneNumber);
            return ResponseEntity.ok("Update phone successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/update-address-user/{idUser}")
    public ResponseEntity<?> updateAddressUser(@PathVariable("idUser") String idUser, @RequestBody @Valid AddressDTO addressDTO,
                                               BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, String.join(", ", errorMessages)));
            }
            userService.updateAddressUser(idUser, addressDTO);
            return ResponseEntity.ok("Update address successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/update-user/{idUser}")
    public ResponseEntity<?> updateUser(@PathVariable("idUser") String idUser, @RequestBody @Valid UserDTO userDTO,
                                        BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, String.join(", ", errorMessages)));
            }
            userService.updateUser(idUser, userDTO);
            return ResponseEntity.ok("Update profile successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/auth/social-login")
    public void socialAuth(HttpServletResponse response) throws IOException{
        String url = googleAuthService.generateAuthUrl();
        response.sendRedirect(url);
    }

    @PostMapping("/auth/social/callback")
    public ResponseEntity<?> callback(@RequestBody GoogleCodeRequest request){
        try {
            User user = googleAuthService.loginWithGoogle(request);
            String token = jwtTokenProvider.generateToken(user);
            Token jwtToken = tokenService.addToken(user, token);

            LoginResponse loginResponse = LoginResponse.builder()
                    .message("Đăng nhập thành công!")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(user.getFullName())
                    .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .id(user.getId())
                    .build();

            return ResponseEntity.ok(ApiResponse.ok(loginResponse));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
