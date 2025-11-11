package com.cucumbergen.backend.repository;

import com.cucumbergen.backend.entity.AttributeDefinition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Repository backed by {@link jakarta.persistence.EntityManager} for {@link AttributeDefinition}.
 */
@Repository
public class AttributeDefinitionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves every attribute definition ordered by key.
     *
     * @return all attribute definitions
     */
    public List<AttributeDefinition> findAll() {
        return entityManager.createQuery("SELECT a FROM AttributeDefinition a ORDER BY a.key", AttributeDefinition.class)
                .getResultList();
    }

    /**
     * Searches for an attribute definition by identifier.
     *
     * @param id identifier
     * @return optional result
     */
    public Optional<AttributeDefinition> findById(String id) {
        return Optional.ofNullable(entityManager.find(AttributeDefinition.class, id));
    }

    /**
     * Searches for an attribute definition by its unique key.
     *
     * @param key attribute key
     * @return optional result
     */
    public Optional<AttributeDefinition> findByKey(String key) {
        TypedQuery<AttributeDefinition> query = entityManager.createQuery(
                "SELECT a FROM AttributeDefinition a WHERE a.key = :key", AttributeDefinition.class);
        query.setParameter("key", key);
        List<AttributeDefinition> results = query.setMaxResults(1).getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Persists the provided definition.
     *
     * @param definition entity to persist
     * @return managed entity instance
     */
    public AttributeDefinition save(AttributeDefinition definition) {
        if (definition.getId() == null) {
            entityManager.persist(definition);
            return definition;
        }
        return entityManager.merge(definition);
    }

    /**
     * Checks if an entity exists by id.
     *
     * @param id identifier
     * @return {@code true} when found
     */
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    /**
     * Deletes an entity by id when present.
     *
     * @param id identifier
     */
    public void deleteById(String id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
