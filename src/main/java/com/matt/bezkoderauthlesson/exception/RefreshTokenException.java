package com.matt.bezkoderauthlesson.exception;

public class RefreshTokenException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public RefreshTokenException(String token) {
    super(String.format("Invalid or expired refresh token: %s", token));
  }
}
