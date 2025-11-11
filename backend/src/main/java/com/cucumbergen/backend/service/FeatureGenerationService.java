package com.cucumbergen.backend.service;

import com.cucumbergen.backend.dto.generation.GeneratedFeatureDto;
import com.cucumbergen.backend.dto.generation.GenerationRequest;
import com.cucumbergen.backend.dto.generation.MissingPolicy;
import com.cucumbergen.backend.entity.Template;
import com.cucumbergen.backend.entity.TestCase;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

/**
 * Generates feature files by applying templates to test cases.
 */
@Service
public class FeatureGenerationService {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("<([a-zA-Z0-9_-]+)>");
    private static final Pattern FILENAME_TOKEN_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_-]+)\\}");

    private final TemplateService templateService;
    private final TestCaseService testCaseService;

    public FeatureGenerationService(TemplateService templateService, TestCaseService testCaseService) {
        this.templateService = templateService;
        this.testCaseService = testCaseService;
    }

    /**
     * Generates previews for the requested tests.
     *
     * @param request request payload
     * @return generated feature previews
     */
    public List<GeneratedFeatureDto> preview(GenerationRequest request) {
        Template template = templateService.findById(request.getTemplateId());
        List<TestCase> testCases = testCaseService.findByIds(request.getTestIds());
        List<GeneratedFeatureDto> generated = new ArrayList<>();
        for (TestCase testCase : testCases) {
            Map<String, String> values = buildValueMap(testCase);
            GeneratedFeatureDto dto = generateForTest(template, testCase, values, request);
            if (dto != null) {
                generated.add(dto);
            }
        }
        return generated;
    }

    /**
     * Generates a zip archive containing the generated feature files.
     *
     * @param request generation parameters
     * @return resource representing the archive
     */
    public ByteArrayResource generateArchive(GenerationRequest request) {
        List<GeneratedFeatureDto> features = preview(request);
        byte[] data = createZip(features);
        return new ByteArrayResource(data);
    }

    private GeneratedFeatureDto generateForTest(Template template,
                                                TestCase testCase,
                                                Map<String, String> values,
                                                GenerationRequest request) {
        String content = applyTemplate(template.getContent(), values, request);
        if (content == null) {
            return null;
        }
        String filename = applyFilenamePattern(request.getFilenamePattern(), values, request);
        if (!filename.toLowerCase(Locale.ROOT).endsWith(".feature")) {
            filename = filename + ".feature";
        }
        return new GeneratedFeatureDto(testCase.getId(), filename, content);
    }

    private String applyTemplate(String template, Map<String, String> values, GenerationRequest request) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            PlaceholderResolution resolution = resolvePlaceholder(key, values, request);
            if (resolution.skip) {
                return null;
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(resolution.value));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String applyFilenamePattern(String pattern, Map<String, String> values, GenerationRequest request) {
        Matcher matcher = FILENAME_TOKEN_PATTERN.matcher(pattern);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            PlaceholderResolution resolution = resolvePlaceholder(key, values, request);
            if (resolution.skip) {
                return values.getOrDefault("name", "test") + ".feature";
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(resolution.value));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private PlaceholderResolution resolvePlaceholder(String key, Map<String, String> values, GenerationRequest request) {
        String value = values.get(key);
        if (value != null) {
            return new PlaceholderResolution(false, value);
        }
        Map<String, String> defaults = request.getDefaults() == null ? Map.of() : request.getDefaults();
        switch (request.getMissingPolicy()) {
            case SKIP:
                return new PlaceholderResolution(true, "");
            case DEFAULT:
                return new PlaceholderResolution(false, defaults.getOrDefault(key, ""));
            case KEEP:
            default:
                return new PlaceholderResolution(false, "<" + key + ">");
        }
    }

    private Map<String, String> buildValueMap(TestCase testCase) {
        Map<String, String> values = new HashMap<>();
        values.put("id", testCase.getId());
        values.put("name", testCase.getName());
        values.put("description", testCase.getDescription() == null ? "" : testCase.getDescription());
        testCase.getAttributes().forEach((key, value) -> values.put(key, value == null ? "" : value.toString()));
        return values;
    }

    private byte[] createZip(List<GeneratedFeatureDto> features) {
        try (java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            for (GeneratedFeatureDto feature : features) {
                ZipEntry entry = new ZipEntry(feature.getFilename());
                zipOutputStream.putNextEntry(entry);
                zipOutputStream.write(feature.getContent().getBytes(StandardCharsets.UTF_8));
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
            return outputStream.toByteArray();
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Unable to create zip archive", e);
        }
    }

    private static class PlaceholderResolution {
        private final boolean skip;
        private final String value;

        PlaceholderResolution(boolean skip, String value) {
            this.skip = skip;
            this.value = value;
        }
    }
}
