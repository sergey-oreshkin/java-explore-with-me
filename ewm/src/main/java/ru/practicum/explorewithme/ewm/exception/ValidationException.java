package ru.practicum.explorewithme.ewm.exception;

public class ValidationException extends AbstractException {

    public ValidationException(String message, String rejectedValue) {
        super(message, rejectedValue);
    }
}
