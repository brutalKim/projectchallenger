package site.challenger.project_challenger.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table
public class CommentRecommend {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "post_comment_no", nullable = false)
	PostComment postComment;

	@ManyToOne
	@JoinColumn(name = "users_no", nullable = false, unique = false)
	Users recommendUsers;

	public CommentRecommend(PostComment postcomment, Users recommendUsers) {
		this.postComment = postcomment;
		this.recommendUsers = recommendUsers;
		this.postComment.incrementRecommend();
	}

	@PreRemove
	public void unrecommend() {
		this.postComment.decrementRecommend();
	}
}
