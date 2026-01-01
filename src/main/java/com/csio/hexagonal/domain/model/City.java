package com.csio.hexagonal.domain.model;

import com.csio.hexagonal.domain.exception.InvalidCityNameException;
import com.csio.hexagonal.domain.exception.InvalidStateNameException;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;

import java.util.Objects;

public class City {

    // Entities are defined by identity, not by attributes
    private final CityId id;

    private final String name;
    private final State state;
    private boolean active;

    public City(CityId id, String name, State state) {
        InvalidCityNameException.validate(name);
        InvalidStateNameException.validate(state.value());

        this.id = Objects.requireNonNull(id, "CityId must not be null");
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

    /* ===== Getters ===== */

    public CityId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public State getState() {
        return state;
    }

    public boolean isActive() {
        return active;
    }

    /* ===== Identity-based equality ===== */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return id.equals(city.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /* ===== Debugging support ===== */

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", active=" + active +
                '}';
    }
}
