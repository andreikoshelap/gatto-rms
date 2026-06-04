package com.gatto.rms.repository;

import com.gatto.rms.entity.Role;
import com.gatto.rms.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
