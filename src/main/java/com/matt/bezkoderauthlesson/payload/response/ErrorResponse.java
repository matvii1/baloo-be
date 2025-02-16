package com.matt.bezkoderauthlesson.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class ErrorResponse {
  private String error;

  public static ResponseEntity<ErrorResponse> of(String message, HttpStatus status) {
    return ResponseEntity
            .status(status)
            .body(new ErrorResponse(message));
  }
}

