package com.matt.bezkoderauthlesson.security.jwt;

import com.matt.bezkoderauthlesson.security.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTUtil {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.accessTokenExpiration}")
  private int accessTokenExpirationMs;


  public String generateToken(String username, Collection<? extends GrantedAuthority> roles, String email) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", username);
    claims.put("roles", roles);
    claims.put("email", email);

    return Jwts.builder()
            .claims(claims)
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(new Date().getTime() + accessTokenExpirationMs))
            .signWith(getSigningKey())
            .compact();
  }


  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  public boolean isTokenExpired(String jwt) {
    return getExpiration(jwt).before(new Date());
  }

  public String getEmailFromToken(String jwt) {
    return extractClaim(jwt, Claims::getSubject);
  }

  public boolean validateToken(String jwt, UserDetailsImpl userDetails) {
    String email = getEmailFromToken(jwt);
    return !isTokenExpired(jwt) && email.equals(userDetails.getEmail());
  }

  private Date getExpiration(String jwt) {
    return extractClaim(jwt, Claims::getExpiration);
  }

  private <T> T extractClaim(String jwt, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(jwt);
    return claimResolver.apply(claims);
  }

  private Claims extractAllClaims(String jwt) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(jwt).getPayload();
  }

  public String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }

    return null;
  }
}
