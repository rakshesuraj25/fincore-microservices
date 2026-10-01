package com.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.DTO.UserResponse;
import com.entity.User;
import com.enumuser.KYCStatusenum;
import com.enumuser.UserStatusEnum;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);
	
	Page<User> findByKYCstatus(KYCStatusenum kycStatus, Pageable pageable);
	Page<User> findByUserStatus(UserStatusEnum valueOf, Pageable pageable);
    Page<User> findByUserStatusAndKYCstatus(UserStatusEnum userStatus, KYCStatusenum kycStatus, Pageable pageable);

}
