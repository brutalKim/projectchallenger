package site.challenger.project_challenger.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SignupServiceReqDTO {
	private String id;
	private String nickname;
	private String locationOpt1;
	private String locationOpt2;
	private int oauthRef;
}
