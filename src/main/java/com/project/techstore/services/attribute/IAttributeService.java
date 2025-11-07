package com.project.techstore.services.attribute;

import java.util.List;

import com.project.techstore.models.Attribute;

public interface IAttributeService {
    List<Attribute> getAllAttributes() throws Exception;

    Attribute createAttribute(String attributeName) throws Exception;

    void updateAttribute(Long id, String attributeName) throws Exception;
}
