package com.project.techstore.responses.user;

import java.time.LocalDate;

import com.project.techstore.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private LocalDate dateOfBirth;

    private AddressResponse address;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class AddressResponse {
        private String id;
        private String province;
        private String ward;
        private String homeAddress;
        private String suggestedName;
    }

    public static UserResponse fromUser(User user) {
        AddressResponse addressResponse = null;
        if (user.getAddress() != null) {
            addressResponse = AddressResponse.builder()
                    .id(user.getAddress().getId())
                    .province(user.getAddress().getProvince())
                    .ward(user.getAddress().getWard())
                    .homeAddress(user.getAddress().getHomeAddress())
                    .suggestedName(user.getAddress().getSuggestedName())
                    .build();
        }

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(addressResponse)
                .avatar(user.getAvatar())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }

}
