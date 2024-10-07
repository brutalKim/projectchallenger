package site.challenger.project_challenger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.challenger.project_challenger.domain.ChallengeHasPost;
import site.challenger.project_challenger.domain.ChallengeHasPostPrimaryKey;

public interface ChallengeHasPostRepository extends JpaRepository<ChallengeHasPost, ChallengeHasPostPrimaryKey> {

	@Query("SELECT chp FROM ChallengeHasPost chp WHERE chp.challengeHasPostPrimaryKey.challengeNo = :challengeNo")
	List<ChallengeHasPost> findByChallengeNo(@Param("challengeNo") Long challengeNo);
}
