package site.challenger.project_challenger.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PostWriteServiceReqDTO {
	private Long writerId;
	private String content;
}
