package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.challenger.project_challenger.domain.PostRecommend;
import site.challenger.project_challenger.domain.PostRecommendPrimaryKey;

public interface PostRecommendRepository extends JpaRepository<PostRecommend,PostRecommendPrimaryKey>{

}
