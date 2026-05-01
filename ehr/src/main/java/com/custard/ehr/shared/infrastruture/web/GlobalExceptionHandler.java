package com.custard.ehr.shared.infrastruture.web;



import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.shared.exception.NotFoundException;
import com.custard.ehr.shared.exception.UnauthorizedException;
import com.custard.ehr.shared.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ErrorResponse.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldError)
                .toList();

        ErrorResponse response = ErrorResponse.of(
                "Validation failed",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.of(
                "Something went wrong",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message,
            String path
    ) {
        ErrorResponse response = ErrorResponse.of(
                message,
                path,
                status.value()
        );

        return ResponseEntity.status(status).body(response);
    }

    private ErrorResponse.FieldError toFieldError(FieldError error) {
        return new ErrorResponse.FieldError(
                error.getField(),
                error.getDefaultMessage()
        );
    }
}