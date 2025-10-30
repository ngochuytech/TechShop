package com.project.techstore.services.user;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.UserLoginDTO;
import com.project.techstore.dtos.user.UpdateProfileDTO;
import com.project.techstore.dtos.user.UserDTO;
import com.project.techstore.models.User;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface IUserService{
    User createUser(UserDTO userDTO) throws Exception;

    User getUserById(String id) throws Exception;

    String loginUser(UserLoginDTO userLoginDTO) throws Exception;

    User updateUser(String id, UserDTO userDTO) throws Exception;

    void updateProfile(String userId, UpdateProfileDTO profileDTO) throws Exception;

    void updateAvatar(User user, MultipartFile avatar) throws Exception;

    User updatePhoneUser(String idUser, String phoneNumber) throws Exception;

    void updateAddressUser(String addressId, AddressDTO addressDTO) throws Exception;

    void resetPassword(String token, String newPassword) throws Exception;

    User getUserByToken(String token) throws Exception;
    // Admin
    List<User> getUserList();

    User setActiveUser(String id, Boolean isActive) throws Exception;
}
