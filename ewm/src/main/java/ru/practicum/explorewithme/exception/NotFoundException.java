package ru.practicum.explorewithme.exception;

public class NotFoundException extends AbstractException {
    public NotFoundException(String message, String rejectedValue) {
        super(message, rejectedValue);
    }
}
