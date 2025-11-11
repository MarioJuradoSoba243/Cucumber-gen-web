package com.cucumbergen.backend.controller;

import com.cucumbergen.backend.dto.TestCaseDto;
import com.cucumbergen.backend.service.TestCaseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller providing CRUD endpoints for test cases.
 */
@RestController
@RequestMapping("/api/v1/tests")
public class TestCaseController {

    private final TestCaseService testCaseService;

    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    /**
     * Lists all tests.
     *
     * @return list of test case DTOs
     */
    @GetMapping
    public List<TestCaseDto> list() {
        return testCaseService.findAll();
    }

    /**
     * Creates a new test case.
     *
     * @param request payload
     * @return persisted test case
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestCaseDto create(@Valid @RequestBody TestCaseDto request) {
        request.setId(null);
        return testCaseService.save(request);
    }

    /**
     * Updates an existing test case.
     *
     * @param id identifier
     * @param request payload
     * @return updated test case
     */
    @PutMapping("/{id}")
    public TestCaseDto update(@PathVariable String id, @Valid @RequestBody TestCaseDto request) {
        request.setId(id);
        return testCaseService.save(request);
    }

    /**
     * Deletes a test case.
     *
     * @param id identifier
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        testCaseService.delete(id);
    }
}
