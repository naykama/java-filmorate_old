package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getError() {
        return message;
    }
}
