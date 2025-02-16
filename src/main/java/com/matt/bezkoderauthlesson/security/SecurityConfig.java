package com.matt.bezkoderauthlesson.security;

import com.matt.bezkoderauthlesson.security.jwt.JWTFilter;
import com.matt.bezkoderauthlesson.security.service.AuthEntryPointJwt;
import com.matt.bezkoderauthlesson.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private final AuthEntryPointJwt unauthorizedHandler;
  private final UserDetailsServiceImpl userDetailsService;
  private final JWTFilter JWTFilter;

  @Autowired
  public SecurityConfig(JWTFilter JWTFilter, UserDetailsServiceImpl userDetailsServiceImpl, AuthEntryPointJwt unauthorizedHandler) {
    this.JWTFilter = JWTFilter;
    this.userDetailsService = userDetailsServiceImpl;
    this.unauthorizedHandler = unauthorizedHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth ->
                    auth
                            .requestMatchers(
                                    "/auth/**",
                                    "/oauth2/**")
                            .permitAll().anyRequest().authenticated()
            );

    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(JWTFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
