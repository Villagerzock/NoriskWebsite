package com.example.application.exceptions;

public class LeaderboardNotFound extends RuntimeException {
    public LeaderboardNotFound(String message) {
        super(message);
    }
}
