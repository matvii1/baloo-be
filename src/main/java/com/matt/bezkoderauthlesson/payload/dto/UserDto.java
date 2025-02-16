package com.matt.bezkoderauthlesson.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
  private String username;
  private String email;
  private List<String> roles;
}
