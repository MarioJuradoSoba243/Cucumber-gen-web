package com.cucumbergen.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * DTO representing a test case payload.
 */
public class TestCaseDto {

    private String id;

    @NotBlank
    private String name;

    private String description;

    private String folderPath = "";

    private Map<String, Object> attributes;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
