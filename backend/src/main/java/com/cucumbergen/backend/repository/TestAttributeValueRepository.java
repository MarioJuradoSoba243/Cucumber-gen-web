package com.cucumbergen.backend.repository;

import com.cucumbergen.backend.entity.TestAttributeValue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link TestAttributeValue} entities.
 */
@Repository
public interface TestAttributeValueRepository extends JpaRepository<TestAttributeValue, String> {

    List<TestAttributeValue> findByTestCaseId(String testCaseId);
}
