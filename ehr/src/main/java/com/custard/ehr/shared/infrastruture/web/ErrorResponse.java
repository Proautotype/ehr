package com.custard.ehr.shared.infrastruture.web;


import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        boolean success,
        String message,
        String path,
        int status,
        Instant timestamp,
        List<FieldError> errors
) {

    public static ErrorResponse of(
            String message,
            String path,
            int status
    ) {
        return new ErrorResponse(
                false,
                message,
                path,
                status,
                Instant.now(),
                List.of()
        );
    }

    public static ErrorResponse of(
            String message,
            String path,
            int status,
            List<FieldError> errors
    ) {
        return new ErrorResponse(
                false,
                message,
                path,
                status,
                Instant.now(),
                errors
        );
    }

    public record FieldError(
            String field,
            String message
    ) {
    }
}