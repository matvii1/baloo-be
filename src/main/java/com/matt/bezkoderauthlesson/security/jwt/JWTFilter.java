package com.matt.bezkoderauthlesson.security.jwt;

import com.matt.bezkoderauthlesson.security.service.UserDetailsImpl;
import com.matt.bezkoderauthlesson.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);
  private final JWTUtil jwtUtils;
  ApplicationContext context;


  @Autowired
  public JWTFilter(ApplicationContext context, JWTUtil jwtUtil) {
    this.context = context;
    this.jwtUtils = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String email = null;
    String jwt = jwtUtils.parseJwt(request);
    System.out.println(jwt);

    if (jwt != null) {
      email = jwtUtils.getEmailFromToken(jwt);
      UserDetailsImpl userDetails = (UserDetailsImpl) context.getBean(UserDetailsServiceImpl.class).loadUserByUsername(email);

      if (jwtUtils.validateToken(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }


    filterChain.doFilter(request, response);
  }
}
