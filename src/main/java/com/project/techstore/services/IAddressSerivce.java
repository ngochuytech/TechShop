package com.project.techstore.services;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.models.Address;

public interface IAddressSerivce {
    Address createAddress(AddressDTO addressDTO) throws Exception;

    Address updateAddress(String id, AddressDTO addressDTO) throws Exception;

    void deleteAddress(String id) throws Exception;
}
