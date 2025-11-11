package com.cucumbergen.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO describing template metadata and content.
 */
public class TemplateDto {

    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String content;

    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
