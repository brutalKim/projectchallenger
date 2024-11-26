package site.challenger.project_challenger.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.ChallengeLog;
import site.challenger.project_challenger.domain.ChallengeSub;

@Repository
public interface ChallengeLogRepository  extends JpaRepository<ChallengeLog,Long> {
	@Query("SELECT log FROM ChallengeLog log WHERE log.date =:date AND challengeSub =:challengeSub")
	Optional<ChallengeLog> findByDate(@Param("date")LocalDate date, @Param("challengeSub")ChallengeSub challengeSub);
	
	@Query("SELECT log FROM ChallengeLog log WHERE log.date BETWEEN :startDate AND :endDate AND challengeSub =:challengeSub")
	List<ChallengeLog> findByDateRange(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate, ChallengeSub challengeSub);
}
