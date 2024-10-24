package site.challenger.project_challenger.dto.challenge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

	@NotBlank
	@Size(min = 2, max = 30, message = "2자 이상 30자 이하")
	private String title;
	@NotBlank
	@Size(max = 255, message = "255자 이하")
	private String content;
	@NotBlank
	private String locationOpt1;
	@NotBlank
	private String locationOpt2;

}
