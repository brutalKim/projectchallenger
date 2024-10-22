package site.challenger.project_challenger.repository;

import java.util.ArrayList;
import java.util.Optional;

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
	
	@Query("SELECT new site.challenger.project_challenger.dto.user.SearchUsersDTO(users.no, users.profile, users.nickname, follow.users.no) " +
		       "FROM Users users " +
		       "LEFT JOIN Follow follow ON follow.users.no = :userNo " +
		       "WHERE users.nickname LIKE CONCAT('%', :keyWord, '%')")
		ArrayList<SearchUsersDTO> searchUserByKeyWord(@Param("userNo") Long userNo, @Param("keyWord") String keyWord);

}
