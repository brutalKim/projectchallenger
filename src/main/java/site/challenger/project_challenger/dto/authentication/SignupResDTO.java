package site.challenger.project_challenger.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SignupResDTO {
	private boolean status;
	private String msg;
}
