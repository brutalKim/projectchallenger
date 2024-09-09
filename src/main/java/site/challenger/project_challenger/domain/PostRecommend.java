package site.challenger.project_challenger.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@Entity
@Table(name = "post_recommend", uniqueConstraints = @UniqueConstraint(columnNames = {"users_no", "post_no"}))
public class PostRecommend {
	@EmbeddedId
	private PostRecommendPrimaryKey postRecommendPrimaryKey;
    @ManyToOne
    @MapsId("userNo")  // 복합키에서 userNo와 연결
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private Users user;

    @ManyToOne
    @MapsId("postNo")  // 복합키에서 postNo와 연결
    @JoinColumn(name = "post_no", insertable = false, updatable = false)
    private Post post;

   
    //insert 시 시간 기록
    @CreationTimestamp
    private LocalDateTime date;
    
    @Builder
    public PostRecommend(Users user,Post post) {
        this.user = user;
        this.post = post;
        this.postRecommendPrimaryKey = new PostRecommendPrimaryKey(user,post); // 복합키 설정
    }
}
