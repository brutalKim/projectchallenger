package site.challenger.project_challenger.dto.challenge;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import site.challenger.project_challenger.domain.JwtModel;
import site.challenger.project_challenger.dto.CommonRequestDTO;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ChallengeRequestDTO extends CommonRequestDTO {

	private String title;
	private String content;
	private String locationOpt1;
	private String locationOpt2;

	public ChallengeRequestDTO(JwtModel jwtModel, String title, String content, String locationOpt1,
			String locationOpt2) {
		super(jwtModel);
		this.title = title;
		this.content = content;
		this.locationOpt1 = locationOpt1;
		this.locationOpt2 = locationOpt2;
	}

}
