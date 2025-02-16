package com.matt.bezkoderauthlesson.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenResponse {
  private String accessToken;
  private String refreshToken;
}
