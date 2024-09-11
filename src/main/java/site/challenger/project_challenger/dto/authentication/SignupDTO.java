package site.challenger.project_challenger.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class SignupDTO {
	@Getter
	@Setter
	public class SignupReqDTO{
		private String nickname;
		private String locationOpt1;
		private String locationOpt2;
	}
	@AllArgsConstructor
	@Getter
	@Setter
	public class SignupResDTO{
		private boolean status;
		private String msg;
	}
	@Getter
	@Setter
	@AllArgsConstructor 
	public class SignupServiceReqDTO{
		private String id;
		private String nickname;
		private String locationOpt1;
		private String locationOpt2;
	}
}
