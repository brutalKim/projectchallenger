package site.challenger.project_challenger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Notice;
import site.challenger.project_challenger.domain.Users;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	@Query("SELECT COUNT(n) FROM Notice n WHERE n.targetusers = :targetUser AND n.readed =false")
	Long countNoticesByTargetUser(@Param("targetUser") Users targetUser);

	@Query("SELECT n FROM Notice n WHERE n.kind = 'notice'")
	List<Notice> getNoticeNotice();

	@Query("SELECT n FROM Notice n WHERE n.targetusers = :targetUser")
	List<Notice> getNotice(@Param("targetUser") Users targetUser);

//	boolean existByKindAndTargetusersAndSentusers()
}
