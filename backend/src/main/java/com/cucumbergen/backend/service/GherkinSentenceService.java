package com.cucumbergen.backend.service;

import com.cucumbergen.backend.dto.GherkinSentenceDto;
import com.cucumbergen.backend.entity.GherkinSentence;
import com.cucumbergen.backend.repository.GherkinSentenceRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that manages available Gherkin sentences.
 */
@Service
@Transactional
public class GherkinSentenceService {

    private final GherkinSentenceRepository gherkinSentenceRepository;

    public GherkinSentenceService(GherkinSentenceRepository gherkinSentenceRepository) {
        this.gherkinSentenceRepository = gherkinSentenceRepository;
    }

    /**
     * Retrieves the imported sentences ordered by insertion time.
     *
     * @return immutable list of DTOs
     */
    @Transactional(readOnly = true)
    public List<GherkinSentenceDto> findAll() {
        return gherkinSentenceRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Replaces the stored sentences with the provided list after normalizing them.
     *
     * @param sentences raw sentences coming from the client
     * @return persisted sentences
     */
    public List<GherkinSentenceDto> replaceAll(List<String> sentences) {
        List<String> source = sentences == null ? List.of() : sentences;
        gherkinSentenceRepository.deleteAll();

        Set<String> normalized = new LinkedHashSet<>();
        for (String sentence : source) {
            if (sentence == null) {
                continue;
            }
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                normalized.add(trimmed);
            }
        }

        if (normalized.isEmpty()) {
            return List.of();
        }

        List<GherkinSentence> toPersist = normalized.stream()
                .map(content -> {
                    GherkinSentence entity = new GherkinSentence();
                    entity.setContent(content);
                    return entity;
                })
                .collect(Collectors.toList());
        List<GherkinSentence> saved = gherkinSentenceRepository.saveAll(toPersist);
        return saved.stream().map(this::toDto).collect(Collectors.toUnmodifiableList());
    }

    private GherkinSentenceDto toDto(GherkinSentence entity) {
        GherkinSentenceDto dto = new GherkinSentenceDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        return dto;
    }
}
