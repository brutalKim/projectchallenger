package site.challenger.project_challenger.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.ChallengeSub;
import site.challenger.project_challenger.domain.Users;

@Repository
public interface ChallengeSubRepository extends JpaRepository<ChallengeSub, Long> {

	@Query("SELECT cs FROM ChallengeSub cs WHERE cs.users = :user AND cs.challenge = :challenge AND cs.challenge.abled = true")
	Optional<ChallengeSub> findByUsersAndChallengeAbledTrue(@Param("user") Users user,
			@Param("challenge") Challenge challenge);

	@Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END FROM ChallengeSub cs WHERE cs.users = :user AND cs.challenge = :challenge AND cs.challenge.abled = true")
	boolean existsByUsersAndChallengeAbledTrue(Users user, Challenge challenge);

	@Query(nativeQuery = true, value = "SELECT cs.* FROM challenge_sub cs JOIN challenge c ON cs.challenge_no = c.no WHERE cs.users_no = :userNo AND c.abled = true ORDER BY c.recommend DESC")
	Page<ChallengeSub> findByUsersSortedByRecommend(@Param("userNo") long userNO, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM challenge_sub cs JOIN challenge c ON cs.challenge_no = c.no WHERE cs.users_no = :userNo AND c.abled = true", nativeQuery = true)
	long countByUserNo(@Param("userNo") long userNo);

	@Query("SELECT COUNT(cs) FROM ChallengeSub cs WHERE cs.challenge = :challenge AND cs.challenge.abled = true")
	long countByChallenge(@Param("challenge") Challenge challenge);

}
