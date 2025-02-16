package com.matt.bezkoderauthlesson.service;

import com.matt.bezkoderauthlesson.exception.ResourceNotFoundException;
import com.matt.bezkoderauthlesson.model.User;
import com.matt.bezkoderauthlesson.repostiory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public void save(User user) {
    userRepository.save(user);
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
  }

  public User findById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
  }
}
