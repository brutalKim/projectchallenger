package site.challenger.project_challenger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersAuthority;

public interface UsersAuthorityRepository extends JpaRepository<UsersAuthority, Long> {
	List<UsersAuthority> findByUser(Users user);
}
