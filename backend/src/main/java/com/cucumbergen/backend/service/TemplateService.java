package com.cucumbergen.backend.service;

import com.cucumbergen.backend.dto.TemplateDto;
import com.cucumbergen.backend.entity.Template;
import com.cucumbergen.backend.exception.NotFoundException;
import com.cucumbergen.backend.repository.TemplateRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that handles template persistence.
 */
@Service
@Transactional
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    /**
     * Retrieves all templates.
     *
     * @return list of DTOs
     */
    @Transactional(readOnly = true)
    public List<TemplateDto> findAll() {
        return templateRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Persists a template.
     *
     * @param dto payload
     * @return saved template
     */
    public TemplateDto save(TemplateDto dto) {
        Template entity = dto.getId() == null
                ? new Template()
                : templateRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Template not found: " + dto.getId()));
        entity.setName(dto.getName());
        entity.setContent(dto.getContent());
        entity.setDescription(dto.getDescription());
        Template saved = templateRepository.save(entity);
        return toDto(saved);
    }

    /**
     * Finds a template by id.
     *
     * @param id identifier
     * @return entity
     */
    @Transactional(readOnly = true)
    public Template findById(String id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Template not found: " + id));
    }

    private TemplateDto toDto(Template entity) {
        TemplateDto dto = new TemplateDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContent(entity.getContent());
        dto.setDescription(entity.getDescription());
        return dto;
    }
}
