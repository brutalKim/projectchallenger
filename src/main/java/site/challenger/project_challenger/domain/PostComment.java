package site.challenger.project_challenger.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "post_comment")
@ToString
public class PostComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;

	@ManyToOne
	@JoinColumn(name = "users_no", nullable = false)
	private Users users;

	@ManyToOne
	@JoinColumn(name = "post_no", nullable = false)
	private Post post;

	@Column(nullable = false)
	private String content;

	@OneToMany(mappedBy = "postComment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommentRecommend> CommentRecommendList;

	private Long recommend;
	// insert 시 시간 기록
	@CreationTimestamp
	private LocalDateTime date;

	@Builder
	public PostComment(Users user, Post post, String content) {
		this.users = user;
		this.post = post;
		this.content = content;
		this.recommend = 0L;
	}

	public void incrementRecommend() {
		this.recommend++;
	}

	public void decrementRecommend() {
		this.recommend--;
	}
}
