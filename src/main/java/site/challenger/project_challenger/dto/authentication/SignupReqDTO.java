package site.challenger.project_challenger.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupReqDTO {
	private String nickname;
	private String locationOpt1;
	private String locationOpt2;
}
