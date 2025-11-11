package com.cucumbergen.backend.repository;

import com.cucumbergen.backend.entity.TestAttributeValue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link TestAttributeValue} entities using direct JPA access.
 */
@Repository
public class TestAttributeValueRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves all attribute values linked to a specific test case.
     *
     * @param testCaseId related test case identifier
     * @return list of attribute values
     */
    public List<TestAttributeValue> findByTestCaseId(String testCaseId) {
        return entityManager.createQuery(
                        "SELECT v FROM TestAttributeValue v WHERE v.testCase.id = :testCaseId",
                        TestAttributeValue.class)
                .setParameter("testCaseId", testCaseId)
                .getResultList();
    }

    /**
     * Persists an attribute value.
     *
     * @param value entity instance
     * @return managed entity
     */
    public TestAttributeValue save(TestAttributeValue value) {
        if (value.getId() == null) {
            entityManager.persist(value);
            return value;
        }
        return entityManager.merge(value);
    }

    /**
     * Finds an attribute value by identifier.
     *
     * @param id identifier
     * @return optional result
     */
    public Optional<TestAttributeValue> findById(String id) {
        return Optional.ofNullable(entityManager.find(TestAttributeValue.class, id));
    }

    /**
     * Deletes an attribute value by identifier when present.
     *
     * @param id identifier
     */
    public void deleteById(String id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
