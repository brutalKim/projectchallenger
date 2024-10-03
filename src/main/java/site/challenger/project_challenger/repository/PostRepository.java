package site.challenger.project_challenger.repository;

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
	@Query("SELECT new site.challenger.project_challenger.dto.post.PostDTO(p.no, p.content, p.date, p.recommend, p.users.no, pr.user.no) " +
		       "FROM Post p LEFT OUTER JOIN PostRecommend pr ON p.no = pr.post.no AND pr.user.no = :userNo " +
		       "WHERE p.users.no = :writerNo " +
		       "ORDER BY p.date DESC")
		Page<PostDTO> getPostByWriterAndUser(@Param("writerNo") Long writerNo, @Param("userNo") Long userNo, Pageable pageable);


    // 추천 포스트 조회 ~ 일단 전부 가져옴 
    @Query("SELECT new site.challenger.project_challenger.dto.post.PostDTO(p.no, p.content, p.date, p.recommend, p.users.no, pr.user.no) "
            + "FROM Post p LEFT OUTER JOIN PostRecommend pr ON p.no = pr.post.no AND pr.user.no = :userNo " //
            + "ORDER BY p.date DESC")
    Page<PostDTO> getRecommendPost(@Param("userNo") Long userNo, Pageable pageable);
}