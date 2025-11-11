package com.cucumbergen.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cucumbergen.backend.dto.CloneTestCaseRequest;
import com.cucumbergen.backend.dto.TestCaseDto;
import com.cucumbergen.backend.entity.AttributeDefinition;
import com.cucumbergen.backend.entity.TestCase;
import com.cucumbergen.backend.repository.AttributeDefinitionRepository;
import com.cucumbergen.backend.repository.TestCaseRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link TestCaseService} behaviour.
 */
@ExtendWith(MockitoExtension.class)
class TestCaseServiceTest {

    @Mock
    private TestCaseRepository testCaseRepository;

    @Mock
    private AttributeDefinitionRepository attributeDefinitionRepository;

    private TestCaseService service;

    @BeforeEach
    void setUp() {
        service = new TestCaseService(testCaseRepository, attributeDefinitionRepository);
    }

    @Test
    void cloneAppliesProvidedOverrides() {
        TestCase original = new TestCase();
        original.setId("original-id");
        original.setName("Login");
        original.setDescription("Initial test");
        original.setFolderPath("auth/tests");
        Map<String, Object> originalAttributes = new HashMap<>();
        originalAttributes.put("priority", "Alta");
        original.setAttributes(originalAttributes);

        AttributeDefinition priorityDefinition = new AttributeDefinition();
        priorityDefinition.setId("attr-1");
        priorityDefinition.setKey("priority");
        priorityDefinition.setType("TEXT");
        priorityDefinition.setAllowedValues(List.of("Alta", "Media", "Baja"));
        priorityDefinition.setRequired(false);

        when(testCaseRepository.findById("original-id")).thenReturn(Optional.of(original));
        when(attributeDefinitionRepository.findAll()).thenReturn(List.of(priorityDefinition));
        when(testCaseRepository.save(any(TestCase.class))).thenAnswer(invocation -> {
            TestCase persisted = invocation.getArgument(0);
            persisted.setId("generated-id");
            return persisted;
        });

        CloneTestCaseRequest request = new CloneTestCaseRequest();
        request.setName("Login QA");
        request.setDescription("Escenario validado para QA");
        request.setFolderPath(" qa / flows ");
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("priority", "Media");
        request.setAttributes(overrides);

        TestCaseDto clone = service.clone("original-id", request);

        assertThat(clone.getId()).isEqualTo("generated-id");
        assertThat(clone.getName()).isEqualTo("Login QA");
        assertThat(clone.getDescription()).isEqualTo("Escenario validado para QA");
        assertThat(clone.getFolderPath()).isEqualTo("qa/flows");
        assertThat(clone.getAttributes()).containsEntry("priority", "Media");

        verify(testCaseRepository).save(any(TestCase.class));
    }

    @Test
    void cloneGeneratesDefaultNameWhenMissing() {
        TestCase original = new TestCase();
        original.setId("original-id");
        original.setName("Checkout");
        original.setDescription("Flow de compra");
        original.setFolderPath("commerce");
        original.setAttributes(new HashMap<>());

        when(testCaseRepository.findById("original-id")).thenReturn(Optional.of(original));
        when(attributeDefinitionRepository.findAll()).thenReturn(List.of());
        when(testCaseRepository.save(any(TestCase.class))).thenAnswer(invocation -> {
            TestCase persisted = invocation.getArgument(0);
            persisted.setId("copy-id");
            return persisted;
        });

        CloneTestCaseRequest request = new CloneTestCaseRequest();
        request.setName("   ");

        TestCaseDto clone = service.clone("original-id", request);

        assertThat(clone.getId()).isEqualTo("copy-id");
        assertThat(clone.getName()).isEqualTo("Checkout (copia)");
        assertThat(clone.getFolderPath()).isEqualTo("commerce");
    }
}
