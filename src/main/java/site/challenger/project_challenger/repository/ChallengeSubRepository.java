package site.challenger.project_challenger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.ChallengeSub;
import site.challenger.project_challenger.domain.Users;

public interface ChallengeSubRepository extends JpaRepository<ChallengeSub, Long> {

	Optional<ChallengeSub> findByUsersAndChallenge(Users user, Challenge challenge);

	boolean existsByUsersAndChallenge(Users user, Challenge challenge);

	List<ChallengeSub> findByChallenge(Challenge challenge);

	List<ChallengeSub> findByUser(Users user);

	long countByChallenge(Challenge challenge);

}
