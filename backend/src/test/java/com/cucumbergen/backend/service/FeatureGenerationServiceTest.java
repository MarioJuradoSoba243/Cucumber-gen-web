package com.cucumbergen.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.cucumbergen.backend.dto.generation.GeneratedFeatureDto;
import com.cucumbergen.backend.dto.generation.GenerationRequest;
import com.cucumbergen.backend.dto.generation.MissingPolicy;
import com.cucumbergen.backend.entity.Template;
import com.cucumbergen.backend.entity.TestCase;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link FeatureGenerationService}.
 */
@ExtendWith(MockitoExtension.class)
class FeatureGenerationServiceTest {

    @Mock
    private TemplateService templateService;

    @Mock
    private TestCaseService testCaseService;

    private FeatureGenerationService featureGenerationService;

    @BeforeEach
    void setUp() {
        featureGenerationService = new FeatureGenerationService(templateService, testCaseService);
    }

    @Test
    void preview_shouldReplacePlaceholdersWithValues() {
        Template template = new Template();
        template.setId("tpl-1");
        template.setName("Messaging");
        template.setContent("Feature: Test\nScenario: <name> sends to <destinatario>");

        TestCase testCase = new TestCase();
        testCase.setId("case-1");
        testCase.setName("Envio correo");
        testCase.setAttributes(Map.of("destinatario", "Juan"));

        GenerationRequest request = new GenerationRequest();
        request.setTemplateId("tpl-1");
        request.setTestIds(List.of("case-1"));
        request.setFilenamePattern("{name}");
        request.setMissingPolicy(MissingPolicy.DEFAULT);
        request.setDefaults(Map.of());

        when(templateService.findById("tpl-1")).thenReturn(template);
        when(testCaseService.findByIds(eq(List.of("case-1")))).thenReturn(List.of(testCase));

        List<GeneratedFeatureDto> previews = featureGenerationService.preview(request);

        assertThat(previews).hasSize(1);
        GeneratedFeatureDto preview = previews.get(0);
        assertThat(preview.getFilename()).isEqualTo("Envio correo.feature");
        assertThat(preview.getContent()).contains("Envio correo sends to Juan");
    }

    @Test
    void preview_shouldSkipTestWhenMissingValuesAndPolicySkip() {
        Template template = new Template();
        template.setId("tpl-1");
        template.setName("Messaging");
        template.setContent("Feature: Test\nScenario: <name> sends to <destinatario>");

        TestCase testCase = new TestCase();
        testCase.setId("case-1");
        testCase.setName("Envio correo");
        testCase.setAttributes(Map.of());

        GenerationRequest request = new GenerationRequest();
        request.setTemplateId("tpl-1");
        request.setTestIds(List.of("case-1"));
        request.setFilenamePattern("{name}");
        request.setMissingPolicy(MissingPolicy.SKIP);
        request.setDefaults(Map.of("destinatario", "Por Defecto"));

        when(templateService.findById("tpl-1")).thenReturn(template);
        when(testCaseService.findByIds(anyList())).thenReturn(List.of(testCase));

        List<GeneratedFeatureDto> previews = featureGenerationService.preview(request);

        assertThat(previews).isEmpty();
    }
}
