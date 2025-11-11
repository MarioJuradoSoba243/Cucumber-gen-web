package com.cucumbergen.backend.dto.generation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Request payload for previewing or generating feature files.
 */
public class GenerationRequest {

    @NotBlank
    private String templateId;

    @NotEmpty
    private List<String> testIds;

    @NotBlank
    private String filenamePattern;

    @NotNull
    private MissingPolicy missingPolicy;

    private Map<String, String> defaults;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<String> getTestIds() {
        return testIds;
    }

    public void setTestIds(List<String> testIds) {
        this.testIds = testIds;
    }

    public String getFilenamePattern() {
        return filenamePattern;
    }

    public void setFilenamePattern(String filenamePattern) {
        this.filenamePattern = filenamePattern;
    }

    public MissingPolicy getMissingPolicy() {
        return missingPolicy;
    }

    public void setMissingPolicy(MissingPolicy missingPolicy) {
        this.missingPolicy = missingPolicy;
    }

    public Map<String, String> getDefaults() {
        return defaults;
    }

    public void setDefaults(Map<String, String> defaults) {
        this.defaults = defaults;
    }
}
