package site.challenger.project_challenger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.Users;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

	List<Challenge> findByUsers(Users user);

}
