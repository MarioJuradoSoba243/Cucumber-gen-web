package com.cucumbergen.backend.controller;

import com.cucumbergen.backend.dto.AttributeDefinitionDto;
import com.cucumbergen.backend.service.AttributeDefinitionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes attribute definition endpoints.
 */
@RestController
@RequestMapping("/api/v1/attributes")
public class AttributeDefinitionController {

    private final AttributeDefinitionService attributeDefinitionService;

    public AttributeDefinitionController(AttributeDefinitionService attributeDefinitionService) {
        this.attributeDefinitionService = attributeDefinitionService;
    }

    /**
     * Lists attribute definitions.
     *
     * @return list of definitions
     */
    @GetMapping
    public List<AttributeDefinitionDto> list() {
        return attributeDefinitionService.findAll();
    }

    /**
     * Creates a new attribute definition.
     *
     * @param dto request payload
     * @return persisted definition
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttributeDefinitionDto create(@Valid @RequestBody AttributeDefinitionDto dto) {
        dto.setId(null);
        return attributeDefinitionService.create(dto);
    }
}
