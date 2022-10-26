package ru.practicum.explorewithme.ewm.exception;

public abstract class AbstractWithRejectedFieldException extends RuntimeException {
    private String rejectedValue;

    public AbstractWithRejectedFieldException(String message, String rejectedValue) {
        super(message);
        this.rejectedValue = rejectedValue;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }
}
