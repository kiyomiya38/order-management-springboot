package com.shinesoft.sandbox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GreetingCalculatorTest {
    private final GreetingCalculator calculator = new GreetingCalculator();

    @Test
    void add_returnsSum() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    void greeting_blankName_returnsGuest() {
        assertEquals("Hello, guest", calculator.greeting(" "));
    }
}