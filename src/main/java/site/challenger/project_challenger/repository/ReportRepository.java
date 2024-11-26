package site.challenger.project_challenger.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Report;
import site.challenger.project_challenger.domain.Users;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
	boolean existsByReportusersAndTargetusersAndTargetnoAndTargetkind(Users reportusers, Users targetuser,
			long targetno, String targetkind);

	@Query("SELECT r FROM Report r " + "WHERE r.isdone = false AND r.targetkind = :targetkind AND r.no = "
			+ "(SELECT MIN(r2.no) FROM Report r2 WHERE r2.targetkind = r.targetkind AND r2.targetno = r.targetno) "
			+ "ORDER BY r.targetkind ASC, r.targetno ASC")
	Page<Report> findByTargetkindAndIsdoneFalse(@Param("targetkind") String targetkind, Pageable pageable);

	Page<Report> findByTargetkindAndTargetnoAndIsdoneFalse(String targetkind, long targetno, Pageable pageable);

	List<Report> findByTargetkindAndTargetno(String targetkind, long targetno);

	long countByTargetkindAndTargetno(String targetkind, long targetno);

	long countByTargetkindAndTargetnoAndIsdoneFalse(String targetkind, long targetno);
}
