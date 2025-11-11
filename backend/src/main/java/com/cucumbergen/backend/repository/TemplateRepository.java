package com.cucumbergen.backend.repository;

import com.cucumbergen.backend.entity.Template;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Template} entities backed by the JPA {@link EntityManager}.
 */
@Repository
public class TemplateRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves all templates ordered by name.
     *
     * @return list of templates
     */
    public List<Template> findAll() {
        TypedQuery<Template> query = entityManager.createQuery(
                "SELECT t FROM Template t ORDER BY t.name", Template.class);
        return query.getResultList();
    }

    /**
     * Finds a template by identifier.
     *
     * @param id identifier
     * @return optional result
     */
    public Optional<Template> findById(String id) {
        return Optional.ofNullable(entityManager.find(Template.class, id));
    }

    /**
     * Persists or updates a template.
     *
     * @param template entity instance
     * @return managed entity
     */
    public Template save(Template template) {
        if (template.getId() == null) {
            entityManager.persist(template);
            return template;
        }
        return entityManager.merge(template);
    }

    /**
     * Verifies if a template exists.
     *
     * @param id identifier
     * @return {@code true} when present
     */
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    /**
     * Deletes a template by identifier when it exists.
     *
     * @param id identifier
     */
    public void deleteById(String id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
