package site.challenger.project_challenger.dto.post;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import site.challenger.project_challenger.domain.PostComment;
import site.challenger.project_challenger.dto.ResDTO;

@Getter
@Setter
public class PostCommentResDTO extends ResDTO{
	private List<Comment> comments;
	public PostCommentResDTO(HttpStatus status, String msg ,List<PostComment> comments) {
		super(status, msg);
		this.comments = new ArrayList<>();
		for(PostComment comment : comments) {
			Comment c = new Comment(comment.getNo(),comment.getUsers().getNickname(),comment.getUsers().getNo(),comment.getContent());
			this.comments.add(c);
		}
	}
	@Getter
	@Setter
	@AllArgsConstructor
	public class Comment{
		private Long CommentNo;
		private String nickname;
		private Long userId;
		private String Content;
	}
}
