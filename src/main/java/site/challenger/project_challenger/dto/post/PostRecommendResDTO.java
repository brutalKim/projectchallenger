package site.challenger.project_challenger.dto.post;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;
import site.challenger.project_challenger.dto.ResDTO;

@Getter
@Setter
public class PostRecommendResDTO extends ResDTO{
	private String type;
	private Long recommendCount;
	public PostRecommendResDTO(HttpStatus status, String msg ,boolean type,Long count) {
		super(status, msg);
		// TODO Auto-generated constructor stub
		if(type) {
			this.type = "recommend";
		}else {
			this.type = "unrecommend";
		}
		this.recommendCount = count;
	}

}
