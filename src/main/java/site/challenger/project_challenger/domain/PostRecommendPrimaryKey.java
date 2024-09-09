package site.challenger.project_challenger.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Embeddable
public class PostRecommendPrimaryKey implements Serializable{
    @Column(name = "user_no", nullable = false)
    private Long userNo;
    @Column(name = "post_no", nullable = false)
    private Long postNo;
    
    public PostRecommendPrimaryKey(Users user,Post post) {
    	this.userNo = user.getNo();
    	this.postNo = post.getNo();
    }
}
