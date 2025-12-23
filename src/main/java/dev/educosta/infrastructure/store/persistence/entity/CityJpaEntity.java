
package dev.educosta.infrastructure.store.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "city")
public class CityJpaEntity {
    @Id
    public String id;
    public String name;
    public String state;
}
