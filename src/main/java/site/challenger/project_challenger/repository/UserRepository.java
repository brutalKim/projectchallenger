package site.challenger.project_challenger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

	boolean existsByUid(String uid);

	Optional<Users> findByUid(String uid);
}
