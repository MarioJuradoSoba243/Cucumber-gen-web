package com.cucumbergen.backend.repository;

import com.cucumbergen.backend.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link TestCase} entities.
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, String> {
}
