package com.cucumbergen.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO that represents an imported Gherkin sentence.
 */
public class GherkinSentenceDto {

    private String id;

    @NotBlank
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
