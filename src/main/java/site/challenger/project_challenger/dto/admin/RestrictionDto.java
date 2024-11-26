package site.challenger.project_challenger.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestrictionDto {
	@NotBlank
	@NotNull
	@Pattern(regexp = "challenge|post|comment|report", message = "challenge, post, comment,report 중 하나여야 합니다.")
	private String targetkind;

	@Min(value = 0)
	private long targetno;

	@Min(value = 0)
	private long targetuserno;

	@NotBlank
	@NotNull
	@Pattern(regexp = "delete|1-day|3-day|7-day", message = "delete, 1-day, 3-day,7-day 중 하나여야 합니다.")
	private String restriction;

	@NotBlank
	@NotNull
	private String comment;

}
