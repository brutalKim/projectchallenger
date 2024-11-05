package site.challenger.project_challenger.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.challenger.project_challenger.domain.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    //유저가 팔로우 조회
    @Query("SELECT f FROM Follow f WHERE f.users.no = :userNo")
    ArrayList<Follow> getFollow(@Param("userNo") Long userNo);
    
    //유저 팔로워 조회
    @Query("SELECT f FROM Follow f WHERE f.followUsers.no = :targetUserNo")
    ArrayList<Follow> getFollower(@Param("targetUserNo") Long targetUserNo);
    
    //팔로우 했는지 안했는지
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Follow f WHERE f.users.no = :userNo AND f.followUsers.no = :targetUserNo")
    boolean existsByUserNoAndTargetUserNo(@Param("userNo") Long userNo, @Param("targetUserNo") Long targetUserNo);
    
    //유저와 유저사이의 팔로우 관계
    @Query("SELECT f FROM Follow f WHERE f.users.no = :userNo AND f.followUsers.no = :targetUserNo")
    Follow getFollow(@Param("userNo")Long userNo, @Param("targetUserNo")Long targetUserNo);
}