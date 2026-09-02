package com.mindata.hotelavailability.domain.exception;

public class SearchNotFoundException extends RuntimeException {

    public SearchNotFoundException(String searchId) {
        super("No search found for searchId '%s'".formatted(searchId));
    }
}
