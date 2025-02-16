package com.matt.bezkoderauthlesson.controller;

import com.matt.bezkoderauthlesson.exception.DuplicateResourceException;
import com.matt.bezkoderauthlesson.exception.RefreshTokenException;
import com.matt.bezkoderauthlesson.exception.ResourceNotFoundException;
import com.matt.bezkoderauthlesson.model.ERole;
import com.matt.bezkoderauthlesson.model.RefreshToken;
import com.matt.bezkoderauthlesson.model.Role;
import com.matt.bezkoderauthlesson.model.User;
import com.matt.bezkoderauthlesson.payload.reqeust.LoginRequest;
import com.matt.bezkoderauthlesson.payload.reqeust.RefreshTokenRequest;
import com.matt.bezkoderauthlesson.payload.reqeust.SignupRequest;
import com.matt.bezkoderauthlesson.payload.response.LoginResponse;
import com.matt.bezkoderauthlesson.payload.response.MessageResponse;
import com.matt.bezkoderauthlesson.payload.response.RefreshTokenResponse;
import com.matt.bezkoderauthlesson.security.jwt.JWTUtil;
import com.matt.bezkoderauthlesson.security.service.UserDetailsImpl;
import com.matt.bezkoderauthlesson.service.RefreshTokenService;
import com.matt.bezkoderauthlesson.service.RoleService;
import com.matt.bezkoderauthlesson.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class AuthController {
  private final AuthenticationManager authManager;
  private final JWTUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final RoleService roleService;
  private final UserService userService;
  private final RefreshTokenService refreshTokenService;

  @Autowired
  public AuthController(AuthenticationManager authManager, JWTUtil jwtUtil, PasswordEncoder passwordEncoder, RoleService roleService, UserService userService, RefreshTokenService refreshTokenService) {
    this.authManager = authManager;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = passwordEncoder;
    this.roleService = roleService;
    this.userService = userService;
    this.refreshTokenService = refreshTokenService;
  }

  @PostMapping("/auth/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest user) {
    Authentication authentication = authManager
            .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String accessToken = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities(), userDetails.getEmail());
    RefreshToken refreshToken = refreshTokenService.generateRefreshToken(userDetails.getId());

    return ResponseEntity.ok().body(new LoginResponse(accessToken, refreshToken.getToken()));
  }

  @PostMapping("/auth/signup")
  public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
    if (userService.existsByEmail(signupRequest.getEmail())) {
      throw new DuplicateResourceException("User", "email", signupRequest.getEmail());
    }

    Role userRole = roleService.getRoleByName(ERole.ROLE_USER);
    Role adminRole = roleService.getRoleByName(ERole.ROLE_ADMIN);

    Set<Role> roles = new HashSet<>();
    roles.add(userRole);

    if (signupRequest.getEmail().equals("admin@gmail.com")) {
      roles.add(adminRole);
    }

    User user = new User(
            signupRequest.getUsername(),
            signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword()),
            roles
    );

    userService.save(user);

    return ResponseEntity.ok().body(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/auth/refresh-token")
  public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    String token = refreshTokenRequest.getRefreshToken();
    RefreshToken foundToken = refreshTokenService.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Refresh Token", "Token", token));

    boolean isTokenValid = refreshTokenService.verifyExpiration(foundToken);

    if (!isTokenValid) {
      throw new RefreshTokenException(token);
    }
    User user = foundToken.getUser();
    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .toList();

    String accessToken = jwtUtil.generateToken(user.getUsername(), authorities, user.getEmail());
    RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user.getId());

    return ResponseEntity.ok().body(new RefreshTokenResponse(accessToken, refreshToken.getToken()));
  }

  @PostMapping("/logout")
  public ResponseEntity<MessageResponse> logout() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    refreshTokenService.deleteByUserId(userDetails.getId());
    SecurityContextHolder.clearContext();

    return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/admin")
  public String admin() {
    return "Admin";
  }
}
