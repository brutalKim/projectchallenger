package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.ChallengeHasPost;
import site.challenger.project_challenger.domain.ChallengeHasPostPrimaryKey;

public interface ChallengeHasPostRepository extends JpaRepository<ChallengeHasPost,ChallengeHasPostPrimaryKey>{

}
