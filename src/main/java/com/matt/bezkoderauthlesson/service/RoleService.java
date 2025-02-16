package com.matt.bezkoderauthlesson.service;

import com.matt.bezkoderauthlesson.exception.ResourceNotFoundException;
import com.matt.bezkoderauthlesson.model.ERole;
import com.matt.bezkoderauthlesson.model.Role;
import com.matt.bezkoderauthlesson.repostiory.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
  private final RoleRepository roleRepository;

  public RoleService(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  public Role getRoleByName(ERole roleName) {
    return roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
  }
}
