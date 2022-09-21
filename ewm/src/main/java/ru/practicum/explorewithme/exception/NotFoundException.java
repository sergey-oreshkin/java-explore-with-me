package ru.practicum.explorewithme.exception;

public class NotFoundException extends RuntimeException{
    private String rejectedValue;

    public NotFoundException(String message, String rejectedValue) {
        super(message);
        this.rejectedValue = rejectedValue;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }
}
