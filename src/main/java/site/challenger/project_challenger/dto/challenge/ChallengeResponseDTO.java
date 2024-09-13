package site.challenger.project_challenger.dto.challenge;

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

}
