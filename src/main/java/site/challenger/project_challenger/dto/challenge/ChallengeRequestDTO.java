package site.challenger.project_challenger.dto.challenge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChallengeRequestDTO {

	private String title;
	private String content;
	private String locationOpt1;
	private String locationOpt2;

}
