package com.project.techstore.services.user;

import com.project.techstore.components.JwtTokenProvider;
import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.UserLoginDTO;
import com.project.techstore.dtos.user.UpdateProfileDTO;
import com.project.techstore.dtos.user.UserDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.exceptions.ExpiredTokenException;
import com.project.techstore.exceptions.InvalidParamException;
import com.project.techstore.models.Address;
import com.project.techstore.models.PasswordResetToken;
import com.project.techstore.models.Role;
import com.project.techstore.models.User;
import com.project.techstore.repositories.AddressRepository;
import com.project.techstore.repositories.PasswordResetTokenRepository;
import com.project.techstore.repositories.RoleRepository;
import com.project.techstore.repositories.UserRepository;
import com.project.techstore.services.CloudinaryService;
import com.project.techstore.services.verification.IVerificationTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AddressRepository addressRepository;

    private final CloudinaryService cloudinaryService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;
    
    private final IVerificationTokenService verificationTokenService;

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        String email = userDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new Exception("Email đã tồn tại");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new Exception("Role not found"));

        if (role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new Exception("You can't register an admin account");
        }

        User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .fullName(userDTO.getFullName())
                .role(role)
                .isActive(true)
                .enable(false)
                .build();
        User savedUser = userRepository.save(newUser);
        
        try {
            verificationTokenService.createVerificaitonEmailToken(email);
        } catch (Exception e) {
            System.err.println("Không thể gửi email xác thực: " + e.getMessage());
        }
        
        return savedUser;
    }

    @Override
    public String loginUser(UserLoginDTO userLoginDTO) throws Exception {
        User user = userRepository.findByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new Exception("Email hoặc mật khẩu không đúng!"));
        if(user.getEnable() == false){
            throw new Exception("Tài khoản của bạn chưa được kích hoạt. Vui lòng kiểm tra email để kích hoạt tài khoản.");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLoginDTO.getEmail(),
                userLoginDTO.getPassword(),
                user.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(user);
    }

    @Override
    public User updateUser(String id, UserDTO userDTO) throws Exception {
        Optional<User> userList = userRepository.findById(id);
        if (userList.isEmpty())
            throw new DataNotFoundException("Can't found this user");
        User user = userList.get();
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setAvatar(userDTO.getAvatar());
        user.setGoogleId(userDTO.getGoogleId());
        return userRepository.save(user);
    }

    @Override
    public User updatePhoneUser(String idUser, String phoneNumber) throws Exception {
        if (phoneNumber.isBlank())
            throw new InvalidParamException("Phone number is required");
        if (!phoneNumber.matches("\\d+"))
            throw new InvalidParamException(
                    "The phone number must be entered as a number, without any other characters.");
        else if (phoneNumber.length() != 10)
            throw new InvalidParamException("The phone number must be exactly 10 digits");

        Optional<User> userList = userRepository.findById(idUser);
        if (userList.isEmpty())
            throw new DataNotFoundException("Can't found this user");
        User user = userList.get();
        user.setPhone(phoneNumber);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateAddressUser(String addressId, AddressDTO addressDTO) throws Exception {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new DataNotFoundException("Can't found this address"));
        address.setProvince(addressDTO.getProvince());
        address.setWard(addressDTO.getWard());
        address.setHomeAddress(addressDTO.getHomeAddress());
        address.setSuggestedName(addressDTO.getSuggestedName());
        address.setPhone(addressDTO.getPhoneNumber());
        addressRepository.save(address);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) throws Exception {
        if (newPassword.isBlank())
            throw new InvalidParamException("Mật khẩu không được để trống");
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidParamException("Token không hợp lệ"));

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new Exception("Token đã hết hạn. Vui lòng gửi lại yêu cầu đặt lại mật khẩu.");

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Override
    public User getUserByToken(String token) throws Exception {
        if (jwtTokenProvider.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token đã hết hạn");
        }
        String email = jwtTokenProvider.getUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy user với token này"));
    }

    @Override
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @Override
    public User setActiveUser(String id, Boolean isActive) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User doesn't exist"));
        user.setIsActive(isActive);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User doesn't exist"));
    }

    @Override
    public void updateProfile(String userId, UpdateProfileDTO profileDTO) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User doesn't exist"));
        user.setFullName(profileDTO.getFullName());
        user.setDateOfBirth(profileDTO.getDateOfBirth());
        user.setPhone(profileDTO.getPhone());
        userRepository.save(user);
    }

    @Override
    public void updateAvatar(User user, MultipartFile avatar) throws Exception {
        if (avatar == null || avatar.isEmpty()) {
            throw new InvalidParamException("Avatar file is required");
        }
        String avatarUrl = cloudinaryService.uploadUserImage(avatar, user.getId());
        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(User user, String currentPassword, String newPassword) throws Exception {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidParamException("Mật khẩu hiện tại không đúng");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidParamException("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
