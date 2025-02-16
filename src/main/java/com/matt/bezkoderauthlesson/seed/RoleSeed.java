package com.matt.bezkoderauthlesson.seed;

import com.matt.bezkoderauthlesson.model.ERole;
import com.matt.bezkoderauthlesson.model.Role;
import com.matt.bezkoderauthlesson.repostiory.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RoleSeed implements CommandLineRunner {
  private final RoleRepository roleRepository;

  @Autowired
  public RoleSeed(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public void run(String... args) {
    if (roleRepository.count() == 0) {
      Arrays.stream(ERole.values()).forEach(role ->
              roleRepository.save(new Role(role))
      );
    }
  }
}
