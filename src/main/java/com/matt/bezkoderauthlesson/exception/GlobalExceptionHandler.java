package com.matt.bezkoderauthlesson.exception;

import com.matt.bezkoderauthlesson.payload.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Map<String, String>>> handleValidationExceptions(
          MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    Map<String, Map<String, String>> response = new HashMap<>();
    response.put("errors", errors);

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleRoleNotFoundException(ResourceNotFoundException ex) {
    return ErrorResponse.of(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
    return ErrorResponse.of(ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(RefreshTokenException.class)
  public ResponseEntity<ErrorResponse> handleRefreshTokenException(RefreshTokenException ex) {
    return ErrorResponse.of(ex.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) {
    return ErrorResponse.of(
            "An unexpected error occurred: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

