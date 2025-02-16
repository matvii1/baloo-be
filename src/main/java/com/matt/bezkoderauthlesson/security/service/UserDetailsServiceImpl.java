package com.matt.bezkoderauthlesson.security.service;

import com.matt.bezkoderauthlesson.model.User;
import com.matt.bezkoderauthlesson.repostiory.UserRepository;
import com.matt.bezkoderauthlesson.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserService userService;

  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository, UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userService.findByEmail(email);
    return UserDetailsImpl.build(user);
  }
}
