package site.challenger.project_challenger.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.dto.post.PostDTO;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	//유저로 조회
	@Query("SELECT new site.challenger.project_challenger.dto.post.PostDTO(p.no, p.content, p.date, p.recommend, p.users.no, pr.user.no) "
			+ "FROM Post p LEFT OUTER JOIN PostRecommend pr ON p.no = pr.post.no AND pr.user.no = :userNo "
			+ "WHERE p.users.no IN :writerNo " + "ORDER BY p.date DESC")
	Page<PostDTO> getPostByWriterAndUser(@Param("writerNo") List<Long> writerNo, @Param("userNo") Long userNo,
			Pageable pageable);

	// 추천 포스트 조회 ~ 일단 전부 가져옴
	@Query("SELECT new site.challenger.project_challenger.dto.post.PostDTO(p.no, p.content, p.date, p.recommend, p.users.no, pr.user.no) "
			+ "FROM Post p LEFT OUTER JOIN PostRecommend pr ON p.no = pr.post.no AND pr.user.no = :userNo " //
			+ "ORDER BY p.date DESC")
	Page<PostDTO> getRecommendPost(@Param("userNo") Long userNo, Pageable pageable);

	// 작성한 포스트 수
	@Query("SELECT COUNT(*) FROM Post p WHERE p.users.no = :userNo")
	Long getPostCount(@Param("userNo") Long userNo);
	
	//챌린지 기반 포스트 조회
	@Query("SELECT p FROM Post p LEFT JOIN ChallengeHasPost chp ON p.no = chp.challengeHasPostPrimaryKey.postNo WHERE chp.challengeHasPostPrimaryKey.challengeNo = :chNo")
	Page<Post> getPostByChallengeNo(@Param("chNo") long chNo, Pageable pageable);
  
   //content포함 검색
   @Query("SELECT new site.challenger.project_challenger.dto.post.PostDTO(p.no, p.content, p.date, p.recommend, p.users.no, pr.user.no) "
           + "FROM Post p LEFT OUTER JOIN PostRecommend pr ON p.no = pr.post.no AND pr.user.no = :userNo "+ //
           "WHERE p.content LIKE concat('%', :keyWord, '%') AND p.users.no <> :userNo")
   Page<PostDTO> getPostByKeyword(@Param("keyWord")String keyWord,@Param("userNo") Long userNo, Pageable pageable);
}