package site.challenger.project_challenger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.user.SearchUsersDTO;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

	boolean existsByUid(String uid);

	Optional<Users> findByUid(String uid);

	boolean existsByNickname(String nickName);

	Optional<Users> findByNickname(String nickname);

	@Query("SELECT new site.challenger.project_challenger.dto.user.SearchUsersDTO(users.no,users.profile, users.nickname, follow.users.no) "
			+ "FROM Users users "
			+ "LEFT OUTER JOIN Follow follow ON follow.users.no = :userNo AND users.no = follow.followUsers.no "
			+ "WHERE users.nickname LIKE CONCAT('%', :keyWord, '%')")
	Page<SearchUsersDTO> searchUserByKeyWord(@Param("userNo") Long userNo, @Param("keyWord") String keyWord,
			Pageable pageable);

	// 유저들 존재 검사
	@Query("SELECT COUNT(u) FROM Users u WHERE u.no IN :userNos")
	int countExistingUser(@Param("userNos") List<Long> userNos);

}
