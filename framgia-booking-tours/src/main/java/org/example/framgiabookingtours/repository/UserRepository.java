package org.example.framgiabookingtours.repository;

import org.example.framgiabookingtours.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);

	// Đếm user đăng ký sau ngày X
	long countByCreatedAtAfter(LocalDateTime date);
	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
