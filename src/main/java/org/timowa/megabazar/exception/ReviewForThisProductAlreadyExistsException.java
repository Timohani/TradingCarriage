package org.timowa.megabazar.exception;

public class ReviewForThisProductAlreadyExistsException extends RuntimeException {
    public ReviewForThisProductAlreadyExistsException(String message) {
        super(message);
    }
}
