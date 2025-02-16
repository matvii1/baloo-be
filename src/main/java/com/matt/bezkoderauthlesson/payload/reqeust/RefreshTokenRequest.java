package com.matt.bezkoderauthlesson.payload.reqeust;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenRequest {
  @NotBlank
  private String refreshToken;
}
