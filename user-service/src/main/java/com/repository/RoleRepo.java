package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Role;
import com.entity.User;
import com.enumuser.RolesEnum;


@Repository
public interface RoleRepo extends JpaRepository<Role, Long>{

	
	Boolean existsByName(RolesEnum name);
	
	Optional<Role> findByName(RolesEnum name);
}
