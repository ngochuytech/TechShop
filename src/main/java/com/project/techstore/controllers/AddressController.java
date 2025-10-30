package com.project.techstore.controllers;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.models.Address;
import com.project.techstore.models.User;
import com.project.techstore.services.address.IAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/address")
@RequiredArgsConstructor
public class AddressController {
    private final IAddressService addressService;

    @PostMapping("")
    public ResponseEntity<?> createAddress(@RequestBody @Valid AddressDTO addressDTO,
        @AuthenticationPrincipal User user,
        BindingResult result) throws Exception{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Address address = addressService.createAddress(user, addressDTO);
            return ResponseEntity.ok(address);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable String id, @RequestBody @Valid AddressDTO addressDTO,
                                           BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Address address = addressService.updateAddress(id, addressDTO);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

}
