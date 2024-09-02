package site.challenger.project_challenger.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class MemberVO {
	@Id
	@Column(name = "id",unique =true,nullable = false)
	private String id;
	@Column(name = "nickname",nullable = false)
	private String nickname;
	@Builder
	public MemberVO(String id, String nickname) {
		this.id = id;
		this.nickname = nickname;
	}
}
