package com.csio.hexagonal.domain.model;

import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class City {

    //Entities are defined by identity, not by their attributes
    @EqualsAndHashCode.Include
    private final CityId id;

    private final String name;
    private final State state;
    private boolean active;

    public City(CityId id, String name, State state) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("City name must not be empty");
        }
        if (state == null) {
            throw new IllegalArgumentException("State must not be null");
        }

        this.id = id;
        this.name = name;
        this.state = state;
        this.active = true;
    }

    /* ===== Domain behavior ===== */

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public CityId getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public State getState() {
        return this.state;
    }

    public boolean isActive() {
        return this.active;
    }
}
