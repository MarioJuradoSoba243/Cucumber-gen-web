package com.cucumbergen.backend.dto.generation;

/**
 * Represents a generated feature file preview.
 */
public class GeneratedFeatureDto {

    private String testId;

    private String filename;

    private String content;

    public GeneratedFeatureDto() {
    }

    public GeneratedFeatureDto(String testId, String filename, String content) {
        this.testId = testId;
        this.filename = filename;
        this.content = content;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
