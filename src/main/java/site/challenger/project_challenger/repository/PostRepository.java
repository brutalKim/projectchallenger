package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.Post;

public interface PostRepository extends JpaRepository<Post,Long>{

}
