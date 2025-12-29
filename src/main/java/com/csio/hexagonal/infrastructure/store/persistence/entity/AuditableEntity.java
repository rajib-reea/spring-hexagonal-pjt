package com.csio.hexagonal.infrastructure.store.persistence.entity;

import com.csio.hexagonal.infrastructure.store.persistence.entity.contract.Activatable;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity implements Serializable, Activatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid", nullable = false, unique = true, updatable = false)
    private String uid;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "removed_by")
    private Long removedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
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
    
    // Explicit accessors in case Lombok processing is not active during compilation
    public String getUid() { return this.uid; }
    public void setUid(String uid) { this.uid = uid; }

    public Boolean getIsActive() { return this.isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Long getCreatedBy() { return this.createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getUpdatedBy() { return this.updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Long getRemovedBy() { return this.removedBy; }
    public void setRemovedBy(Long removedBy) { this.removedBy = removedBy; }

    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public LocalDateTime getUpdatedAt() { return this.updatedAt; }
    public LocalDateTime getRemovedAt() { return this.removedAt; }
    
    public Long getId() { return this.id; }
}