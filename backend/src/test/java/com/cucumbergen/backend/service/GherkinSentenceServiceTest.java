package com.cucumbergen.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cucumbergen.backend.dto.GherkinSentenceDto;
import com.cucumbergen.backend.entity.GherkinSentence;
import com.cucumbergen.backend.repository.GherkinSentenceRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link GherkinSentenceService}.
 */
@ExtendWith(MockitoExtension.class)
class GherkinSentenceServiceTest {

    @Mock
    private GherkinSentenceRepository gherkinSentenceRepository;

    private GherkinSentenceService gherkinSentenceService;

    @BeforeEach
    void setUp() {
        gherkinSentenceService = new GherkinSentenceService(gherkinSentenceRepository);
    }

    @Test
    void findAll_shouldReturnDtos() {
        GherkinSentence sentence = new GherkinSentence();
        sentence.setId("id-1");
        sentence.setContent("Given a user");
        when(gherkinSentenceRepository.findAll()).thenReturn(List.of(sentence));

        List<GherkinSentenceDto> result = gherkinSentenceService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Given a user");
        verify(gherkinSentenceRepository).findAll();
    }

    @Test
    void replaceAll_shouldNormalizeAndPersistUniqueSentences() {
        when(gherkinSentenceRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<GherkinSentence> payload = invocation.getArgument(0);
            for (int i = 0; i < payload.size(); i++) {
                payload.get(i).setId("id-" + (i + 1));
            }
            return payload;
        });

        List<GherkinSentenceDto> result = gherkinSentenceService.replaceAll(
                List.of("  Given a user  ", "Given a user", "#comment", "", "When something happens"));

        assertThat(result)
                .extracting(GherkinSentenceDto::getContent)
                .containsExactly("Given a user", "When something happens");
        verify(gherkinSentenceRepository).deleteAll();
        verify(gherkinSentenceRepository)
                .saveAll(argThat(list -> list.size() == 2 && "Given a user".equals(list.get(0).getContent())));
    }

    @Test
    void replaceAll_shouldClearWhenNoValidSentencesExist() {
        List<GherkinSentenceDto> result = gherkinSentenceService.replaceAll(Collections.emptyList());

        assertThat(result).isEmpty();
        verify(gherkinSentenceRepository).deleteAll();
        verify(gherkinSentenceRepository, never()).saveAll(anyList());
    }

    @Test
    void replaceAll_shouldHandleNullPayload() {
        List<GherkinSentenceDto> result = gherkinSentenceService.replaceAll(null);

        assertThat(result).isEmpty();
        verify(gherkinSentenceRepository).deleteAll();
        verify(gherkinSentenceRepository, never()).saveAll(anyList());
    }
}
