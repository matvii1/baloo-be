package com.matt.bezkoderauthlesson.controller;

import com.matt.bezkoderauthlesson.payload.dto.UserDto;
import com.matt.bezkoderauthlesson.security.service.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
  @GetMapping("/user/me")
  public ResponseEntity<UserDto> me() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream().map(
            item -> item.getAuthority()
    ).toList();

    return ResponseEntity.ok().body(new UserDto(userDetails.getUsername(), userDetails.getEmail(), roles));
  }
}
