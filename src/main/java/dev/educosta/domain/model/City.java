
package dev.educosta.domain.model;

import dev.educosta.domain.specification.CityNameNotEmptySpec;
import dev.educosta.domain.vo.CityId;
import dev.educosta.domain.vo.State;

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
