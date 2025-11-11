package com.cucumbergen.backend.repository;

import com.cucumbergen.backend.entity.TestCase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link TestCase} entities using plain JPA operations.
 */
@Repository
public class TestCaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves all test cases ordered by creation time.
     *
     * @return list of test cases
     */
    public List<TestCase> findAll() {
        return entityManager.createQuery("SELECT t FROM TestCase t ORDER BY t.createdAt", TestCase.class)
                .getResultList();
    }

    /**
     * Persists or merges a test case.
     *
     * @param testCase entity to persist
     * @return managed entity
     */
    public TestCase save(TestCase testCase) {
        if (testCase.getId() == null) {
            entityManager.persist(testCase);
            return testCase;
        }
        return entityManager.merge(testCase);
    }

    /**
     * Deletes a test case by id when present.
     *
     * @param id identifier
     */
    public void deleteById(String id) {
        findById(id).ifPresent(entityManager::remove);
    }

    /**
     * Checks if a test case exists.
     *
     * @param id identifier
     * @return {@code true} when present
     */
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    /**
     * Finds a test case by id.
     *
     * @param id identifier
     * @return optional result
     */
    public Optional<TestCase> findById(String id) {
        return Optional.ofNullable(entityManager.find(TestCase.class, id));
    }

    /**
     * Retrieves all test cases matching the provided ids.
     *
     * @param ids identifiers to search
     * @return list preserving the input order
     */
    public List<TestCase> findAllById(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<TestCase> results = entityManager.createQuery(
                        "SELECT t FROM TestCase t WHERE t.id IN :ids", TestCase.class)
                .setParameter("ids", ids)
                .getResultList();
        Map<String, TestCase> byId = results.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(TestCase::getId, test -> test, (existing, replacement) -> existing,
                        LinkedHashMap::new));
        return ids.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
