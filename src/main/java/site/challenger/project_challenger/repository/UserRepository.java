package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.Users;

public interface UserRepository extends JpaRepository<Users,Long>{
	boolean existsByUid(String Id);
}
