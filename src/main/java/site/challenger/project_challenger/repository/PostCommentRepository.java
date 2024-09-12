package site.challenger.project_challenger.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment,Long>{
	Long countByPostNo(Long no);
	ArrayList<PostComment> findByPostNo(Long postNo);
}
