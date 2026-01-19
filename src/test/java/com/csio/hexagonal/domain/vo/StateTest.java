package com.csio.hexagonal.domain.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateTest {

    @Test
    void shouldCreateValidState() {
        // Arrange
        String value = "NY";

        // Act
        State state = new State(value);

        // Assert
        assertNotNull(state);
        assertEquals(value, state.value());
    }

    @Test
    void shouldThrowExceptionWhenStateIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new State(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenStateIsBlank() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new State("   ");
        });
    }

    @Test
    void shouldThrowExceptionWhenStateIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new State("");
        });
    }

    @Test
    void shouldHaveToStringMethod() {
        // Arrange
        String value = "CA";
        State state = new State(value);

        // Act
        String result = state.toString();

        // Assert
        assertEquals(value, result);
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        // Arrange
        State state1 = new State("TX");
        State state2 = new State("TX");

        // Act & Assert
        assertEquals(state1, state2);
        assertEquals(state1.hashCode(), state2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValue() {
        // Arrange
        State state1 = new State("NY");
        State state2 = new State("CA");

        // Act & Assert
        assertNotEquals(state1, state2);
    }
}
