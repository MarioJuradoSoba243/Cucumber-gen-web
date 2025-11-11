package com.cucumbergen.backend.controller;

import com.cucumbergen.backend.dto.GherkinSentenceDto;
import com.cucumbergen.backend.dto.GherkinSentenceImportRequest;
import com.cucumbergen.backend.service.GherkinSentenceService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes operations to manage reusable Gherkin sentences.
 */
@RestController
@RequestMapping("/api/v1/gherkin-sentences")
public class GherkinSentenceController {

    private final GherkinSentenceService gherkinSentenceService;

    public GherkinSentenceController(GherkinSentenceService gherkinSentenceService) {
        this.gherkinSentenceService = gherkinSentenceService;
    }

    /**
     * Lists the imported sentences.
     *
     * @return ordered list of sentences
     */
    @GetMapping
    public List<GherkinSentenceDto> list() {
        return gherkinSentenceService.findAll();
    }

    /**
     * Imports a new collection of sentences, replacing the existing ones.
     *
     * @param request payload containing the raw sentences
     * @return persisted sentences
     */
    @PostMapping("/import")
    public List<GherkinSentenceDto> importSentences(@Valid @RequestBody GherkinSentenceImportRequest request) {
        return gherkinSentenceService.replaceAll(request.getSentences());
    }
}
