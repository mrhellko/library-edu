package ru.mrhellko.library.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(long id) {
        String message = "Row with " + id + " Not Found";
        super(message);
    }
}
