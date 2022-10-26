package ru.practicum.explorewithme.ewm.exception;

public class ConflictException extends AbstractWithRejectedFieldException {

    public ConflictException(String message, String rejectedValue) {
        super(message, rejectedValue);
    }
}
