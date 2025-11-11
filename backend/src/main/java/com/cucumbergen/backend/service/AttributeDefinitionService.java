package com.cucumbergen.backend.service;

import com.cucumbergen.backend.dto.AttributeDefinitionDto;
import com.cucumbergen.backend.entity.AttributeDefinition;
import com.cucumbergen.backend.exception.BadRequestException;
import com.cucumbergen.backend.repository.AttributeDefinitionRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for managing attribute definitions.
 */
@Service
@Transactional
public class AttributeDefinitionService {

    private final AttributeDefinitionRepository repository;

    public AttributeDefinitionService(AttributeDefinitionRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new attribute definition.
     *
     * @param request payload from the client
     * @return persisted DTO
     */
    public AttributeDefinitionDto create(AttributeDefinitionDto request) {
        repository.findByKey(request.getKey()).ifPresent(existing -> {
            throw new BadRequestException("Attribute with key '" + existing.getKey() + "' already exists");
        });
        AttributeDefinition definition = new AttributeDefinition();
        definition.setKey(request.getKey());
        definition.setType(request.getType());
        definition.setAllowedValues(request.getAllowedValues());
        definition.setRequired(request.isRequired());
        AttributeDefinition saved = repository.save(definition);
        return toDto(saved);
    }

    /**
     * Retrieves all attribute definitions.
     *
     * @return list of DTOs
     */
    @Transactional(readOnly = true)
    public List<AttributeDefinitionDto> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AttributeDefinitionDto toDto(AttributeDefinition definition) {
        AttributeDefinitionDto dto = new AttributeDefinitionDto();
        dto.setId(definition.getId());
        dto.setKey(definition.getKey());
        dto.setType(definition.getType());
        dto.setAllowedValues(definition.getAllowedValues());
        dto.setRequired(definition.isRequired());
        return dto;
    }
}
