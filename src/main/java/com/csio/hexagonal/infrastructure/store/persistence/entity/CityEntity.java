package com.csio.hexagonal.infrastructure.store.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "city")
@Data
@EqualsAndHashCode(callSuper = true)
public class CityEntity extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "state", nullable = false)
    private String state;

    // Explicit accessors in case Lombok processing is not active during compilation
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getState() { return this.state; }
    public void setState(String state) { this.state = state; }
}
