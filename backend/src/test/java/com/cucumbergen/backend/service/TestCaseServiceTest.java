package com.cucumbergen.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cucumbergen.backend.dto.TestCaseDto;
import com.cucumbergen.backend.entity.TestCase;
import com.cucumbergen.backend.exception.BadRequestException;
import com.cucumbergen.backend.repository.AttributeDefinitionRepository;
import com.cucumbergen.backend.repository.TestCaseRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link TestCaseService}.
 */
@ExtendWith(MockitoExtension.class)
class TestCaseServiceTest {

    @Mock
    private TestCaseRepository testCaseRepository;

    @Mock
    private AttributeDefinitionRepository attributeDefinitionRepository;

    private TestCaseService testCaseService;

    @BeforeEach
    void setUp() {
        testCaseService = new TestCaseService(testCaseRepository, attributeDefinitionRepository);
    }

    @Test
    void save_shouldNormalizeFolderPath() {
        TestCaseDto request = new TestCaseDto();
        request.setName("Login flow");
        request.setFolderPath(" /qa//ui\\flows/ ");

        when(attributeDefinitionRepository.findAll()).thenReturn(Collections.emptyList());
        when(testCaseRepository.save(any(TestCase.class))).thenAnswer(invocation -> {
            TestCase entity = invocation.getArgument(0);
            entity.setId("generated-id");
            return entity;
        });

        TestCaseDto saved = testCaseService.save(request);

        assertThat(saved.getFolderPath()).isEqualTo("qa/ui/flows");
        verify(testCaseRepository).save(argThat(entity -> "qa/ui/flows".equals(entity.getFolderPath())));
    }

    @Test
    void save_shouldRejectParentDirectoryReferences() {
        TestCaseDto request = new TestCaseDto();
        request.setName("Login flow");
        request.setFolderPath("../secrets");

        assertThatThrownBy(() -> testCaseService.save(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("folder path");
        verify(testCaseRepository, never()).save(any());
    }
}
