package site.challenger.project_challenger.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.dto.post.PostDTO;

public interface PostRepository extends JpaRepository<Post,Long>{
	//recommend entity post entity outer join 유저기준 추천을 했는지 안했는지
	@Query("SELECT new site.challenger.project_challenger.dto.post.PostDTO(p.no, p.content, p.date, p.recommend, p.users.no, pr.user.no) " +
		       "FROM Post p LEFT OUTER JOIN PostRecommend pr ON p.no = pr.post.no AND pr.user.no = :userNo " +
		       "WHERE p.users.no = :writerNo")
		ArrayList<PostDTO> getPostByWriterAndUser(@Param("writerNo") Long writerNo, @Param("userNo") Long userNo);
}
