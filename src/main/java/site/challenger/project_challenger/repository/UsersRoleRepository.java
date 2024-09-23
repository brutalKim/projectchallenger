package site.challenger.project_challenger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersRole;

@Repository
public interface UsersRoleRepository extends JpaRepository<UsersRole, Long> {
	List<UsersRole> findByUser(Users user);
}
