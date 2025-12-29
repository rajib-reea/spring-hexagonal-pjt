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
}
