package ru.practicum.explorewithme.exception;

public abstract class AbstractException extends RuntimeException{
    private String rejectedValue;

    public AbstractException(String message, String rejectedValue) {
        super(message);
        this.rejectedValue = rejectedValue;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }
}
