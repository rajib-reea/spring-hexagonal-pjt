package com.csio.hexagonal.infrastructure.store.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "city")
public class CityEntity {

    @Id
    @Column(name = "id", nullable = false)
    public String id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "state", nullable = false)
    public String state;

    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true; // default at persistence level
}
