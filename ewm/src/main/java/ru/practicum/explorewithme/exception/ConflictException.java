package ru.practicum.explorewithme.exception;

public class ConflictException extends AbstractException{

    public ConflictException(String message, String rejectedValue) {
        super(message, rejectedValue);
    }
}
