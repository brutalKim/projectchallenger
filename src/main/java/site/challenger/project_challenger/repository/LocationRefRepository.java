package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.LocationRef;

@Repository
public interface LocationRefRepository extends JpaRepository<LocationRef, Long> {
	LocationRef findByOpt1AndOpt2(String opt1, String opt2);
}
