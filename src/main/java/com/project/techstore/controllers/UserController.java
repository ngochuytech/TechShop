package com.project.techstore.controllers;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.UserDTO;
import com.project.techstore.dtos.UserLoginDTO;
import com.project.techstore.services.IUserService;
import com.project.techstore.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword()))
                return ResponseEntity.badRequest().body("Password doesn't match");
            userService.createUser(userDTO);
            return ResponseEntity.ok("Register successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
                return ResponseEntity.badRequest().body(errorMessages);
            }
            String token = userService.loginUser(userLoginDTO);
            return ResponseEntity.ok(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-phone-user/{idUser}")
    public ResponseEntity<?> updatePhoneUser(@PathVariable("idUser") String idUser,@RequestParam String phoneNumber) {
        try {
            System.out.println(phoneNumber);
            userService.updatePhoneUser(idUser, phoneNumber);
            return ResponseEntity.ok("Update phone successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
                return ResponseEntity.badRequest().body(errorMessages);
            }
            userService.updateAddressUser(idUser, addressDTO);
            return ResponseEntity.ok("Update address successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
                return ResponseEntity.badRequest().body(errorMessages);
            }
            userService.updateUser(idUser, userDTO);
            return ResponseEntity.ok("Update profile successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
