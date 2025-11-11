package com.cucumbergen.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Represents a reusable Gherkin template.
 */
@Entity
@Table(name = "template")
public class Template extends AbstractAuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Ensures an identifier is generated for new templates.
     */
    @PrePersist
    protected void assignIdentifier() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
