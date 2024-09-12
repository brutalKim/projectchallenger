package site.challenger.project_challenger.dto.post;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDTO {
	private Long no;
	private String content;
    private LocalDateTime date;
    private Long recommend;
	private Long usersNo;
	private boolean isRecommended;
	private Long commentCount;
	public PostDTO(Long no, String content, LocalDateTime date,Long recommend,Long usersNo,Long recommendUsersNo) {
		this.no = no;
		this.content = content;
		this.date = date;
		this.recommend = recommend;
		this.usersNo = usersNo;
		if(recommendUsersNo != null) {
			isRecommended = true;
		}else {
			isRecommended = false;
		}
		commentCount = 0L;
	}
}
