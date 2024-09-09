package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.Member;

public interface MemberRepository extends JpaRepository<Member,String>{

}
