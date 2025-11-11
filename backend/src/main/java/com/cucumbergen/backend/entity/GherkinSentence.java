package com.cucumbergen.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;

/**
 * Represents a reusable Gherkin sentence that can be inserted into templates.
 */
@Entity
@Table(
        name = "gherkin_sentence",
        uniqueConstraints =
                {@UniqueConstraint(columnNames = {"content", "folder_path"})})
public class GherkinSentence extends AbstractAuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "folder_path")
    private String folderPath;

    /**
     * Generates an identifier before persisting a new sentence.
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
}
