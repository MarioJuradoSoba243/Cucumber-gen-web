package com.cucumbergen.backend.repository;

import com.cucumbergen.backend.entity.GherkinSentence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository that manages {@link GherkinSentence} persistence using the {@link EntityManager}.
 */
@Repository
public class GherkinSentenceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves all stored sentences ordered by creation date.
     *
     * @return list of entities
     */
    public List<GherkinSentence> findAll() {
        TypedQuery<GherkinSentence> query = entityManager.createQuery(
                "SELECT s FROM GherkinSentence s ORDER BY s.createdAt ASC", GherkinSentence.class);
        return query.getResultList();
    }

    /**
     * Persists the provided sentences.
     *
     * @param sentences entities to persist
     * @return managed entities
     */
    public List<GherkinSentence> saveAll(List<GherkinSentence> sentences) {
        for (GherkinSentence sentence : sentences) {
            if (sentence.getId() == null) {
                entityManager.persist(sentence);
            } else {
                entityManager.merge(sentence);
            }
        }
        entityManager.flush();
        return sentences;
    }

    /**
     * Removes all stored sentences.
     */
    public void deleteAll() {
        entityManager.createQuery("DELETE FROM GherkinSentence").executeUpdate();
    }
}
