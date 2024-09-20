package site.challenger.project_challenger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.Profile;
import site.challenger.project_challenger.domain.Users;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

	Optional<Profile> findByUser(Users user);
}
