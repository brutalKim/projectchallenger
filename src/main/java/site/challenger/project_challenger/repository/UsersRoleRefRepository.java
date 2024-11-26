package site.challenger.project_challenger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.UserRoleRef;

@Repository
public interface UsersRoleRefRepository extends JpaRepository<UserRoleRef, Long> {
	Optional<UserRoleRef> findByRole(String string);

}