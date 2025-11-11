package com.cucumbergen.backend.repository;

import com.cucumbergen.backend.entity.AttributeDefinition;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link AttributeDefinition} entities.
 */
@Repository
public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, String> {

    Optional<AttributeDefinition> findByKey(String key);
}
