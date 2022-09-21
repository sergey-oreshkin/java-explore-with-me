package ru.practicum.explorewithme.exception;

public class ConflictException extends RuntimeException{
    private String rejectedValue;

    public ConflictException(String message, String rejectedValue) {
        super(message);
        this.rejectedValue = rejectedValue;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }
}
