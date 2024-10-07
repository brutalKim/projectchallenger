package site.challenger.project_challenger.repository;

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

}
