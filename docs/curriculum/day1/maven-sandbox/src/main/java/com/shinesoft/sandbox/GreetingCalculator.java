package com.shinesoft.sandbox;

public class GreetingCalculator {
    public int add(int left, int right) {
        return left + right;
    }

    public String greeting(String name) {
        if (name == null || name.isBlank()) {
            return "Hello, guest";
        }
        return "Hello, " + name.trim();
    }
}