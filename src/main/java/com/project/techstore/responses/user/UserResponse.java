package com.project.techstore.responses.user;

import java.time.LocalDate;
import java.util.List;

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

    private List<AddressResponse> address;

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
        List<AddressResponse> addressResponses = null;
        if (user.getAddresses() != null) {
            addressResponses = user.getAddresses().stream()
                    .map(address -> AddressResponse.builder()
                            .id(address.getId())
                            .province(address.getProvince())
                            .ward(address.getWard())
                            .homeAddress(address.getHomeAddress())
                            .suggestedName(address.getSuggestedName())
                            .build())
                    .toList();
        }

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(addressResponses)
                .avatar(user.getAvatar())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }

}
