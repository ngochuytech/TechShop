package com.project.techstore.services.address;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.models.Address;
import com.project.techstore.models.User;

public interface IAddressService {
    Address createAddress(User user, AddressDTO addressDTO) throws Exception;

    Address updateAddress(String addressId, AddressDTO addressDTO) throws Exception;

    void deleteAddress(String id) throws Exception;
}
