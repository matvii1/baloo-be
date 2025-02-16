package com.matt.bezkoderauthlesson.payload.reqeust;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
  @NotBlank
  String username;

  @NotBlank
  String password;

  @NotBlank
  @Email
  String email;
}
