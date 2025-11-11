package com.cucumbergen.backend.controller;

import com.cucumbergen.backend.dto.TemplateDto;
import com.cucumbergen.backend.service.TemplateService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for template CRUD operations.
 */
@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Lists templates.
     *
     * @return list of DTOs
     */
    @GetMapping
    public List<TemplateDto> list() {
        return templateService.findAll();
    }

    /**
     * Creates a template.
     *
     * @param request payload
     * @return persisted template
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TemplateDto create(@Valid @RequestBody TemplateDto request) {
        request.setId(null);
        return templateService.save(request);
    }

    /**
     * Updates a template.
     *
     * @param id template id
     * @param request payload
     * @return updated template
     */
    @PutMapping("/{id}")
    public TemplateDto update(@PathVariable String id, @Valid @RequestBody TemplateDto request) {
        request.setId(id);
        return templateService.save(request);
    }
}
