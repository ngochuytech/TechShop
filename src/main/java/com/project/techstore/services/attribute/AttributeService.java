package com.project.techstore.services.attribute;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Attribute;
import com.project.techstore.repositories.AttributeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttributeService implements IAttributeService {
    private final AttributeRepository attributeRepository;

    @Override
    public List<Attribute> getAllAttributes() throws Exception {
        return attributeRepository.findAll();
    }

    @Override
    public Attribute createAttribute(String attributeName) throws Exception {
        Attribute attribute = attributeRepository.findByName(attributeName)
            .orElse(Attribute.builder().name(attributeName).build());
        return attributeRepository.save(attribute);
    }

    @Override
    public void updateAttribute(Long id, String attributeName) throws Exception {
        Attribute attribute = attributeRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thuộc tính sản phẩm cần chỉnh sửa"));
        attribute.setName(attributeName);
        attributeRepository.save(attribute);
    }

}
