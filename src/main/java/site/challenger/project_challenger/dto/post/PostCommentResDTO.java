package site.challenger.project_challenger.dto.post;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;

import site.challenger.project_challenger.domain.PostComment;
import site.challenger.project_challenger.dto.ResDTO;

public class PostCommentResDTO extends ResDTO{
	ArrayList<PostComment> comments;
	public PostCommentResDTO(HttpStatus status, String msg ,ArrayList<PostComment> comments) {
		super(status, msg);
		this.comments = comments;
	}
}
