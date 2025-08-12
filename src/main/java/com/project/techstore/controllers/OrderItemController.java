package com.project.techstore.controllers;

import com.project.techstore.dtos.OrderItemDTO;
import com.project.techstore.services.IOrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    private final IOrderItemService orderItemService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderItemsByOrder(@PathVariable("orderId") String orderId){
        try {
            return ResponseEntity.ok(orderItemService.getOrderItemsByOrder(orderId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrderItem(@RequestBody @Valid OrderItemDTO orderItemDTO, BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            orderItemService.createOrderItem(orderItemDTO);
            return ResponseEntity.ok("Create a order item successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
