package com.matt.bezkoderauthlesson.service;

import com.matt.bezkoderauthlesson.model.RefreshToken;
import com.matt.bezkoderauthlesson.model.User;
import com.matt.bezkoderauthlesson.repostiory.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
  private final UserService userService;
  RefreshTokenRepository refreshTokenRepository;
  @Value("${jwt.refreshTokenExpiration}")
  private Long refreshTokenExpiration;

  @Autowired
  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userService = userService;
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshToken generateRefreshToken(Long userId) {
    User user = userService.findById(userId);

    RefreshToken refreshToken = user.getRefreshToken();

    if (refreshToken == null) {
      refreshToken = new RefreshToken();
      refreshToken.setUser(user);
    }

    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));
    refreshToken.setToken(UUID.randomUUID().toString());

    return refreshTokenRepository.save(refreshToken);
  }

  public boolean verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      return false;
    }

    return true;
  }


  @Transactional
  public void deleteByUserId(Long userId) {
    User user = userService.findById(userId);

    refreshTokenRepository.deleteByUser(user);
  }
}
