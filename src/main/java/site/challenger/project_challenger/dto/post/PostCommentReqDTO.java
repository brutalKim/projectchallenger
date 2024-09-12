package site.challenger.project_challenger.dto.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentReqDTO {
	private Long postNo;
	private String content;
}
