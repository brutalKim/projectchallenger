package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.LocationRef;

public interface LocationRefRepository extends JpaRepository<LocationRef,Long>{
	LocationRef findAllByop1Andop2(String op1,String op2);
}
