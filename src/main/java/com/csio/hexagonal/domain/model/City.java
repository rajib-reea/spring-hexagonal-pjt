
package com.csio.hexagonal.domain.model;

import com.csio.hexagonal.domain.specification.CityNameNotEmptySpec;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;

public class City {

    private final CityId id;
    private final String name;
    private final State state;

    public City(CityId id, String name, State state) {
        new CityNameNotEmptySpec().check(name);
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public CityId id() { return id; }
    public String name() { return name; }
    public State state() { return state; }
}
