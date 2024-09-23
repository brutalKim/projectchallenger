package site.challenger.project_challenger.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.PostComment;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
	Long countByPostNo(Long no);

	ArrayList<PostComment> findByPostNo(Long postNo);
}
