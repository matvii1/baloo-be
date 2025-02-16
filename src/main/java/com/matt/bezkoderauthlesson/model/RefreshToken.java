package com.matt.bezkoderauthlesson.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private Instant expiryDate;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;
}
