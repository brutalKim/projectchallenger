package site.challenger.project_challenger.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.LocationRef;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

	// 검색 시나리오 2
	Page<Challenge> findByLocationRef(LocationRef locationRef, Pageable pageable);

	// 검색 시나리오 3
	Page<Challenge> findByTitleContaining(String keyword, Pageable pageable);

}
