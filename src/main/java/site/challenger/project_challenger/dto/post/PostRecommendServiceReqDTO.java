package site.challenger.project_challenger.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostRecommendServiceReqDTO {
	private Long userId;
	private Long postNo;
}
