package com.csio.hexagonal.infrastructure.store.persistence.entity.contract;

import java.time.LocalDateTime;

public interface Activatable {
    void setIsActive(boolean active);
    void setCreatedAt(LocalDateTime createdAt);
    void setUpdatedAt(LocalDateTime updatedAt);
    void setRemovedAt(LocalDateTime removedAt);
}