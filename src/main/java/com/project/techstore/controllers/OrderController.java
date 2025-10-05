package com.project.techstore.controllers;

import com.project.techstore.dtos.CreateOrderRequest;
import com.project.techstore.services.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @GetMapping("/status")
    public ResponseEntity<?> getOrderByStatus(@RequestParam String status){
        try {
            return ResponseEntity.ok(orderService.getOrderByStatus(status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getOrderByUser(@RequestParam String userId){
        try {
            return ResponseEntity.ok(orderService.getOrderByUser(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody @Valid CreateOrderRequest request,
                                         BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            orderService.createOrder(request.getOrderDTO(), request.getAddressDTO());
            return ResponseEntity.ok("Create a new order successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") String id,@RequestBody @Valid CreateOrderRequest request,
                                         BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            orderService.udpateOrder(id, request.getOrderDTO(), request.getAddressDTO());
            return ResponseEntity.ok("Update a order successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatusOrder(@PathVariable("id") String id, @RequestParam String status){
        try{
            orderService.updateStatusOrder(id, status);
            return ResponseEntity.ok("Create a new order successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
