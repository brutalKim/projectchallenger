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

	Optional<ChallengeSub> findByUsersAndChallenge(Users user, Challenge challenge);

	boolean existsByUsersAndChallenge(Users user, Challenge challenge);

	// 사용중 인 곳이 없어서 주석해놈
//	List<ChallengeSub> findByChallenge(Challenge challenge);
//	List<ChallengeSub> findByUsers(Users user);

	@Query(nativeQuery = true, value = "SELECT cs.* FROM challenge_sub cs JOIN challenge c ON cs.challenge_no = c.no WHERE cs.users_no = :userNo ORDER BY c.recommend DESC ")
	Page<ChallengeSub> findByUsersSortedByRecommend(@Param("userNo") long userNO, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM challenge_sub cs WHERE cs.users_no = :userNo", nativeQuery = true)
	long countByUserNo(@Param("userNo") long userNo);

	long countByChallenge(Challenge challenge);

}
