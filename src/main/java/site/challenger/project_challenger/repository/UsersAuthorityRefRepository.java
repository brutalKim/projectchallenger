package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.UsersAuthorityRef;

@Repository
public interface UsersAuthorityRefRepository extends JpaRepository<UsersAuthorityRef, Long> {

}
