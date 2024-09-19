package site.challenger.project_challenger.dto.post;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;
import site.challenger.project_challenger.dto.ResDTO;

@Getter
@Setter
public class CommentWriteResDTO extends ResDTO{
	private Long commentCount = 0L;
	public CommentWriteResDTO(HttpStatus status, String msg,Long commentCount) {
		super(status, msg);
		this.commentCount = commentCount;
	}

}
