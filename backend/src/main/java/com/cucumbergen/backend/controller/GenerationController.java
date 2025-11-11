package com.cucumbergen.backend.controller;

import com.cucumbergen.backend.dto.generation.GeneratedFeatureDto;
import com.cucumbergen.backend.dto.generation.GenerationRequest;
import com.cucumbergen.backend.service.FeatureGenerationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes feature generation endpoints.
 */
@RestController
@RequestMapping("/api/v1/generation")
public class GenerationController {

    private final FeatureGenerationService featureGenerationService;

    public GenerationController(FeatureGenerationService featureGenerationService) {
        this.featureGenerationService = featureGenerationService;
    }

    /**
     * Previews generated feature files.
     *
     * @param request payload describing generation parameters
     * @return list of generated features
     */
    @PostMapping("/preview")
    public List<GeneratedFeatureDto> preview(@Valid @RequestBody GenerationRequest request) {
        return featureGenerationService.preview(request);
    }

    /**
     * Generates a ZIP archive containing feature files.
     *
     * @param request generation parameters
     * @return HTTP response containing the archive
     */
    @PostMapping("/download")
    public ResponseEntity<ByteArrayResource> download(@Valid @RequestBody GenerationRequest request) {
        ByteArrayResource resource = featureGenerationService.generateArchive(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=features.zip")
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
