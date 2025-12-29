package com.csio.hexagonal.infrastructure.store.persistence.entity;

import com.csio.hexagonal.infrastructure.store.persistence.entity.contract.Activatable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class AuditableEntity implements Serializable, Activatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid", nullable = false, unique = true)
    private String uid;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "removed_by")
    private Long removedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    @Override
    public void setIsActive(boolean active) { this.isActive = active; }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public void setRemovedAt(LocalDateTime removedAt) { this.removedAt = removedAt; }
}