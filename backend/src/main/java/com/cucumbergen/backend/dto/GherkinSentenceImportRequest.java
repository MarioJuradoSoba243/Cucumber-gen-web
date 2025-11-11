package com.cucumbergen.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Request payload used to import a collection of sentences.
 */
public class GherkinSentenceImportRequest {

    @NotNull
    private List<@NotBlank String> sentences = new ArrayList<>();

    public List<String> getSentences() {
        return sentences;
    }

    public void setSentences(List<String> sentences) {
        this.sentences = sentences;
    }
}
