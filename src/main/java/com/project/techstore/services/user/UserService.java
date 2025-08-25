package com.project.techstore.services.user;

import com.project.techstore.components.JwtTokenProvider;
import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.UserDTO;
import com.project.techstore.dtos.UserLoginDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AddressRepository addressRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public User createUser(UserDTO userDTO) throws Exception{
        String email = userDTO.getEmail();
        if(userRepository.existsByEmail(email)){
            throw new Exception("Email đã tồn tại");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new Exception("Role not found"));

        if(role.getName().toUpperCase().equals(Role.ADMIN)){
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
        return userRepository.save(newUser);
    }

    @Override
    public String loginUser(UserLoginDTO userLoginDTO) throws Exception{
        User user = userRepository.findByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new Exception("Email hoặc mật khẩu không đúng!"));
        return jwtTokenProvider.generateToken(user);
    }


    @Override
    public User updateUser(String id, UserDTO userDTO) throws Exception {
        Optional<User> userList = userRepository.findById(id);
        if(userList.isEmpty())
            throw new DataNotFoundException("Can't found this user");
        User user = userList.get();
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setAvatar(userDTO.getAvatar());
        user.setGoogleId(userDTO.getGoogleId());
        return userRepository.save(user);
    }

    @Override
    public User updatePhoneUser(String idUser, String phoneNumber) throws Exception {
        if(phoneNumber.isBlank())
            throw new InvalidParamException("Phone number is required");
        if(!phoneNumber.matches("\\d+"))
            throw new InvalidParamException("The phone number must be entered as a number, without any other characters.");
        else if(phoneNumber.length()!=10)
            throw new InvalidParamException("The phone number must be exactly 10 digits");

        Optional<User> userList = userRepository.findById(idUser);
        if(userList.isEmpty())
            throw new DataNotFoundException("Can't found this user");
        User user = userList.get();
        user.setPhone(phoneNumber);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateAddressUser(String id, AddressDTO addressDTO) throws Exception {
        Optional<User> userList = userRepository.findById(id);
        if(userList.isEmpty())
            throw new DataNotFoundException("Can't found this user");
        User user = userList.get();
        if(user.getAddress() == null){
            Address address = Address.builder()
                    .province(addressDTO.getProvince())
                    .district(addressDTO.getDistrict())
                    .ward(addressDTO.getWard())
                    .homeAddress(addressDTO.getHomeAddress())
                    .suggestedName(addressDTO.getSuggestedName())
                    .build();
            addressRepository.save(address);
            user.setAddress(address);
        } else {
            Address address = addressRepository.findById(user.getAddress().getId())
                    .orElseThrow(() -> new DataNotFoundException("Can't found address of this user"));
            address.setProvince(addressDTO.getProvince());
            address.setDistrict(addressDTO.getDistrict());
            address.setWard(addressDTO.getWard());
            address.setHomeAddress(addressDTO.getHomeAddress());
            address.setSuggestedName(addressDTO.getSuggestedName());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) throws Exception {
        if(newPassword.isBlank())
            throw new InvalidParamException("Mật khẩu không được để trống");
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidParamException("Token không hợp lệ"));

        if(passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new Exception("Token đã hết hạn");

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Override
    public User getUserByToken(String token) throws Exception {
        if(jwtTokenProvider.isTokenExpired(token)){
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

}
