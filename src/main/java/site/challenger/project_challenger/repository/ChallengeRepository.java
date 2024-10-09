package site.challenger.project_challenger.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.LocationRef;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

	@Query("SELECT c FROM Challenge c WHERE c.id = :id AND c.abled = true")
	Optional<Challenge> findActiveById(@Param("id") Long id);

	// 검색 시나리오 2
	Page<Challenge> findByLocationRefAndAbledTrue(LocationRef locationRef, Pageable pageable);

	// 검색 시나리오 3
	Page<Challenge> findByTitleContainingAndAbledTrue(String keyword, Pageable pageable);

	// 검색 시나리오 5-1 지역구 전체 1일, 7일 추천수
//	@Query("SELECT c FROM Challenge c " + "JOIN ChallengeRecommend cr ON cr.challenge = c " + "WHERE c.abled = true "
//			+ "AND c.locationRef.opt1 = :opt1 " + "AND cr.date >= :startDate " + "GROUP BY c "
//			+ "ORDER BY COUNT(cr) DESC")
	@Query("SELECT c FROM Challenge c " + "LEFT JOIN ChallengeRecommend cr ON cr.challenge = c "
			+ "WHERE c.abled = true " + "AND c.locationRef.opt1 = :opt1 "
			+ "AND (cr.date >= :startDate OR cr.date IS NULL) " + // 추천이 없는 경우 포함
			"GROUP BY c " + "ORDER BY COUNT(cr) DESC")
	List<Challenge> findMostRecommendedChallengesByOpt1FromStartDate(@Param("opt1") String opt1,
			@Param("startDate") LocalDateTime startDate, Pageable pageable);

	// 검색 시나리오 5-2 전국 1일, 7일 추천수
//	@Query("SELECT c FROM Challenge c " + "JOIN ChallengeRecommend cr ON cr.challenge = c " + "WHERE c.abled = true "
//			+ "AND cr.date >= :startDate " + "GROUP BY c " + "ORDER BY COUNT(cr) DESC")
	@Query("SELECT c FROM Challenge c " + "LEFT JOIN ChallengeRecommend cr ON cr.challenge = c "
			+ "WHERE c.abled = true " + "AND (cr.date >= :startDate OR cr.date IS NULL) " + // cr.date가 NULL인 경우 포함
			"GROUP BY c " + "ORDER BY COUNT(cr) DESC")
	List<Challenge> findMostRecommendedChallengesFromStartDate(@Param("startDate") LocalDateTime startDate,
			Pageable pageable);

	// 검색 시나리로 5-3 지역구 전체 1일, 7일 포스트
//	@Query("SELECT ch FROM Challenge ch "
//			+ "JOIN ChallengeHasPost chp ON chp.challengeHasPostPrimaryKey.challengeNo = ch.no "
//			+ "JOIN Post p ON chp.challengeHasPostPrimaryKey.postNo = p.no " + "WHERE ch.abled = true "
//			+ "AND ch.locationRef.opt1 = :opt1 " + "AND p.date >= :startDate " + "GROUP BY ch "
//			+ "ORDER BY COUNT(p) DESC")
	@Query("SELECT ch FROM Challenge ch "
			+ "LEFT JOIN ChallengeHasPost chp ON chp.challengeHasPostPrimaryKey.challengeNo = ch.no "
			+ "LEFT JOIN Post p ON chp.challengeHasPostPrimaryKey.postNo = p.no " + // LEFT JOIN으로 변경
			"WHERE ch.abled = true " + "AND ch.locationRef.opt1 = :opt1 "
			+ "AND (p.date >= :startDate OR p.date IS NULL) " + // Post가 없는 경우 포함
			"GROUP BY ch " + "ORDER BY COUNT(p) DESC")
	List<Challenge> findTopChallengesByPostCountInByOpt1FromStartDate(@Param("opt1") String opt1,
			@Param("startDate") LocalDateTime startDate, Pageable pageable);

	// 검색 시나리오 5-3 전국 1일, 7일 포스트
//	@Query("SELECT ch FROM Challenge ch "
//			+ "JOIN ChallengeHasPost chp ON chp.challengeHasPostPrimaryKey.challengeNo = ch.no "
//			+ "JOIN Post p ON chp.challengeHasPostPrimaryKey.postNo = p.no " + "WHERE ch.abled = true "
//			+ "AND p.date >= :startDate " + "GROUP BY ch " + "ORDER BY COUNT(p) DESC")
	@Query("SELECT ch FROM Challenge ch "
			+ "LEFT JOIN ChallengeHasPost chp ON chp.challengeHasPostPrimaryKey.challengeNo = ch.no "
			+ "LEFT JOIN Post p ON chp.challengeHasPostPrimaryKey.postNo = p.no " + // LEFT JOIN으로 변경
			"WHERE ch.abled = true " + "AND (p.date >= :startDate OR p.date IS NULL) " + // Post가 없는 경우 포함
			"GROUP BY ch " + "ORDER BY COUNT(p) DESC")
	List<Challenge> findTopChallengesByPostCountInFromStartDate(@Param("startDate") LocalDateTime startDate,
			Pageable pageable);

}
