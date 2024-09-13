package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

}
