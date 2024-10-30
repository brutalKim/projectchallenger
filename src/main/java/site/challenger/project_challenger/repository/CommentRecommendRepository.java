package site.challenger.project_challenger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.challenger.project_challenger.domain.CommentRecommend;
import site.challenger.project_challenger.domain.PostComment;
import site.challenger.project_challenger.domain.Users;

public interface CommentRecommendRepository extends JpaRepository<CommentRecommend,Long> {
	Optional<CommentRecommend> findByPostCommentAndRecommendUsers(PostComment postComment, Users recommendUsers);

	@Query("SELECT COUNT(cr)>0 FROM CommentRecommend cr WHERE cr.postComment.no = :postCommentNo AND cr.recommendUsers.no = :recommendUsersNo")
	boolean existsByPostCommentAndRecommendUsers(@Param("postCommentNo") Long postComment, @Param("recommendUsersNo") Long recommendUsers);
}
 