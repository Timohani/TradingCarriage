package org.timowa.megabazar.exception;

public class CartLimitExceededException extends Throwable {
    public CartLimitExceededException(String maximumQuantityReached) {
    }
}
