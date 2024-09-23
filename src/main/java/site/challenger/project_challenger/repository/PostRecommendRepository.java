package site.challenger.project_challenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.challenger.project_challenger.domain.PostRecommend;
import site.challenger.project_challenger.domain.PostRecommendPrimaryKey;

@Repository
public interface PostRecommendRepository extends JpaRepository<PostRecommend, PostRecommendPrimaryKey> {

}
