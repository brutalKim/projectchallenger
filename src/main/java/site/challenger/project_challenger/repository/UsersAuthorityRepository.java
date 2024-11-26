package site.challenger.project_challenger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersAuthority;
import site.challenger.project_challenger.domain.UsersAuthorityRef;

@Repository
public interface UsersAuthorityRepository extends JpaRepository<UsersAuthority, Long> {
	List<UsersAuthority> findByUser(Users user);

	Optional<UsersAuthority> findByUserAndUsersAuthorityRef(Users user, UsersAuthorityRef usersAuthorityRef);
}
