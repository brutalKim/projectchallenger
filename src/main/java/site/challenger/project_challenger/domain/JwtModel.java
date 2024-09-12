package site.challenger.project_challenger.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtModel {

	private long no;
	private String nickname;

	public JwtModel(long no, String nickname) {
		super();
		if (null == nickname) {
			throw new RuntimeException("There's no nick name on JWT Token plz check");
		}

		this.no = no;
		this.nickname = nickname;
	}

}
