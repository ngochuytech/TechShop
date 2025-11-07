package com.project.techstore.controllers.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.Attribute;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.attribute.IAttributeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/attributes")
@RequiredArgsConstructor
public class AdminAttributeController {
    private final IAttributeService attributeService;
    @GetMapping("/list")
    public ResponseEntity<?> getAllAttributes() throws Exception {
        List<Attribute> attributes = attributeService.getAllAttributes();
        return ResponseEntity.ok(ApiResponse.ok(attributes));
    }
}
