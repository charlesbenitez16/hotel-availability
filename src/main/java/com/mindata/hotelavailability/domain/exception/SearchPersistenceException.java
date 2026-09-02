package com.mindata.hotelavailability.domain.exception;

public class SearchPersistenceException extends RuntimeException {

    public SearchPersistenceException(String searchId, Throwable cause) {
        super("Failed to persist search '%s'".formatted(searchId), cause);
    }
}
