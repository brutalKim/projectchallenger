package site.challenger.project_challenger.dto.post;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.dto.ResDTO;

@Getter
@Setter
public class PostGetResDTO extends ResDTO{
	private ArrayList<PostDTO> posts;
	public PostGetResDTO(HttpStatus status, String msg,ArrayList<PostDTO> posts) {
		super(status, msg);
		this.posts = posts;
	}
}
