package com.csio.hexagonal.domain.model;

import com.csio.hexagonal.domain.exception.InvalidCityNameException;
import com.csio.hexagonal.domain.exception.InvalidStateNameException;
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
        InvalidCityNameException.validate(name);

        InvalidStateNameException.validate(state.value());

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
