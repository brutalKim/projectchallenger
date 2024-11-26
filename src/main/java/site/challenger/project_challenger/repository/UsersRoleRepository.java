package site.challenger.project_challenger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.UserRoleRef;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersRole;

@Repository
public interface UsersRoleRepository extends JpaRepository<UsersRole, Long> {
	List<UsersRole> findByUser(Users user);

	List<UsersRole> findByUserRoleRef(UserRoleRef userRoleRef);

	Optional<UsersRole> findByUserAndUserRoleRef(Users user, UserRoleRef userRoleRef);

	boolean existsByUserAndUserRoleRef(Users user, UserRoleRef userRoleRef);
}
