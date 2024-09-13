package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.ChallengeRecommend;
import site.challenger.project_challenger.domain.ChallengeRecommendPrimaryKey;

public interface ChallengeRecommendRepository extends JpaRepository<ChallengeRecommend, ChallengeRecommendPrimaryKey> {

}
