package com.project.techstore.services.address;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Address;
import com.project.techstore.models.User;
import com.project.techstore.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService{
    private final AddressRepository addressRepository;


    @Override
    public Address createAddress(User user, AddressDTO addressDTO) throws Exception {
        Address address = Address.builder()
                .province(addressDTO.getProvince())
                .ward(addressDTO.getWard())
                .homeAddress(addressDTO.getHomeAddress())
                .suggestedName(addressDTO.getSuggestedName())
                .user(user)
                .phone(addressDTO.getPhoneNumber())
                .isDeleted(false)
                .build();
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(String id, AddressDTO addressDTO) throws Exception {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Address not found"));
        address.setProvince(addressDTO.getProvince());
        address.setWard(addressDTO.getWard());
        address.setHomeAddress(addressDTO.getHomeAddress());
        address.setSuggestedName(addressDTO.getSuggestedName());
        address.setPhone(addressDTO.getPhoneNumber());
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(String id) throws Exception {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Address not found"));
        address.setIsDeleted(true);
        addressRepository.save(address);
    }
}
