package com.cucumbergen.backend.service;

import com.cucumbergen.backend.dto.TestCaseDto;
import com.cucumbergen.backend.entity.AttributeDefinition;
import com.cucumbergen.backend.entity.TestAttributeValue;
import com.cucumbergen.backend.entity.TestCase;
import com.cucumbergen.backend.exception.BadRequestException;
import com.cucumbergen.backend.exception.NotFoundException;
import com.cucumbergen.backend.repository.AttributeDefinitionRepository;
import com.cucumbergen.backend.repository.TestCaseRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Handles business logic around test cases.
 */
@Service
@Transactional
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final AttributeDefinitionRepository attributeDefinitionRepository;

    public TestCaseService(TestCaseRepository testCaseRepository,
                           AttributeDefinitionRepository attributeDefinitionRepository) {
        this.testCaseRepository = testCaseRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    /**
     * Retrieves all stored test cases.
     *
     * @return list of DTOs
     */
    public List<TestCaseDto> findAll() {
        return testCaseRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Persists a new or existing test case.
     *
     * @param request payload from the client
     * @return persisted DTO
     */
    public TestCaseDto save(TestCaseDto request) {
        TestCase entity = request.getId() == null
                ? new TestCase()
                : testCaseRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Test case not found: " + request.getId()));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setFolderPath(normalizeFolderPath(request.getFolderPath()));
        Map<String, Object> attributes = request.getAttributes() == null
                ? new HashMap<>()
                : new HashMap<>(request.getAttributes());
        entity.setAttributes(attributes);
        syncAttributeValues(entity, attributes);

        TestCase saved = testCaseRepository.save(entity);
        return toDto(saved);
    }

    /**
     * Deletes a test case by id.
     *
     * @param id identifier
     */
    public void delete(String id) {
        if (!testCaseRepository.existsById(id)) {
            throw new NotFoundException("Test case not found: " + id);
        }
        testCaseRepository.deleteById(id);
    }

    /**
     * Finds test cases by id.
     *
     * @param ids identifiers
     * @return list of entities
     */
    public List<TestCase> findByIds(List<String> ids) {
        List<TestCase> testCases = testCaseRepository.findAllById(ids);
        if (testCases.size() != ids.size()) {
            throw new NotFoundException("One or more test cases were not found");
        }
        return testCases;
    }

    private void syncAttributeValues(TestCase testCase, Map<String, Object> attributes) {
        List<AttributeDefinition> definitions = attributeDefinitionRepository.findAll();
        Map<String, AttributeDefinition> byKey = definitions.stream()
                .collect(Collectors.toMap(AttributeDefinition::getKey, def -> def));

        for (AttributeDefinition definition : definitions) {
            if (definition.isRequired() && (attributes == null || !attributes.containsKey(definition.getKey()))) {
                throw new BadRequestException("Missing required attribute: " + definition.getKey());
            }
        }

        Set<TestAttributeValue> currentValues = testCase.getAttributeValues();
        currentValues.clear();

        if (attributes == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            AttributeDefinition definition = byKey.get(entry.getKey());
            if (definition == null) {
                throw new BadRequestException("Unknown attribute key: " + entry.getKey());
            }
            String stringValue = entry.getValue() == null ? null : entry.getValue().toString();
            if (!definition.getAllowedValues().isEmpty() && stringValue != null
                    && !definition.getAllowedValues().contains(stringValue)) {
                throw new BadRequestException("Value '" + stringValue + "' is not allowed for attribute "
                        + definition.getKey());
            }
            TestAttributeValue attributeValue = new TestAttributeValue();
            attributeValue.setAttributeDefinition(definition);
            attributeValue.setValue(stringValue);
            attributeValue.setTestCase(testCase);
            currentValues.add(attributeValue);
        }
    }

    private TestCaseDto toDto(TestCase entity) {
        TestCaseDto dto = new TestCaseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setFolderPath(entity.getFolderPath());
        dto.setAttributes(entity.getAttributes());
        return dto;
    }

    private String normalizeFolderPath(String folderPath) {
        if (folderPath == null) {
            return "";
        }
        String sanitized = folderPath.trim().replace('\\', '/');
        if (sanitized.isEmpty()) {
            return "";
        }

        String[] segments = sanitized.split("/");
        List<String> normalized = new ArrayList<>();
        for (String rawSegment : segments) {
            String segment = rawSegment.trim();
            if (segment.isEmpty() || ".".equals(segment)) {
                continue;
            }
            if ("..".equals(segment)) {
                throw new BadRequestException("The folder path cannot contain parent directory references");
            }
            normalized.add(segment);
        }
        return String.join("/", normalized);
    }
}
