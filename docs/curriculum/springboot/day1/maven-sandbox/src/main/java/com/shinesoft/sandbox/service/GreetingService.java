package com.shinesoft.sandbox.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    public String createMessage(String name) {
        if (name == null || name.isBlank()) {
            return "Hello, guest";
        }
        return "Hello, " + name.trim();
    }
}