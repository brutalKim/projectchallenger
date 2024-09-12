package site.challenger.project_challenger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.UserRoleRef;

public interface UsersRoleRefRepository extends JpaRepository<UserRoleRef,Long>{
	Optional<UserRoleRef> findByRole(String string);
}