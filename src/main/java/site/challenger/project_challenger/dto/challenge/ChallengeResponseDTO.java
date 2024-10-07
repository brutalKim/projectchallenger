package site.challenger.project_challenger.dto.challenge;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.LocationRef;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeResponseDTO {

	private long no;
	private String title;
	private String content;
	private LocationRef locationRef;
	private long recommend;
	// 추천 했나
	private boolean recommended;
	// 구독 했나
	private boolean subscribed;
	private LocalDateTime subDateTime;
	// 포스트가 몇개인가
	private long postNum;
	// 구독자가 몇 명인가
	private long follower;

}
