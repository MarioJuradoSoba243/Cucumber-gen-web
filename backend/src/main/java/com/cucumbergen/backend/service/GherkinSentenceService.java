package com.cucumbergen.backend.service;

import com.cucumbergen.backend.dto.GherkinSentenceDto;
import com.cucumbergen.backend.entity.GherkinSentence;
import com.cucumbergen.backend.repository.GherkinSentenceRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        Map<String, SentenceDefinition> normalized = new LinkedHashMap<>();
        String currentFolder = null;
        for (String sentence : source) {
            if (sentence == null) {
                continue;
            }
            String trimmed = sentence.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            if (trimmed.startsWith("-")) {
                String folder = trimmed.substring(1).trim();
                currentFolder = folder.isEmpty() ? null : folder;
                continue;
            }

            String key = (currentFolder == null ? "" : currentFolder) + "\u0000" + trimmed;
            normalized.putIfAbsent(key, new SentenceDefinition(currentFolder, trimmed));
        }

        if (normalized.isEmpty()) {
            return List.of();
        }

        List<GherkinSentence> toPersist = normalized.values().stream()
                .map(definition -> {
                    GherkinSentence entity = new GherkinSentence();
                    entity.setContent(definition.content());
                    entity.setFolderPath(definition.folderPath());
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
        dto.setFolderPath(entity.getFolderPath());
        return dto;
    }

    private record SentenceDefinition(String folderPath, String content) {}
}
