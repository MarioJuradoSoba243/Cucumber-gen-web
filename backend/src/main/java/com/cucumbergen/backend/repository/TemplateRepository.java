package com.cucumbergen.backend.repository;

import com.cucumbergen.backend.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Template} entities.
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, String> {
}
