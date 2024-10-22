package site.challenger.project_challenger.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.Profile;

@Getter
@Setter
@NoArgsConstructor
public class SearchUsersDTO {
	private Long userNo;
	private String profileImg;
	private String description;
	private String nickname;
	private boolean isFollowed;
	
	public SearchUsersDTO(Long userNo,Profile profile, String nickname, Long followed) {
		this.userNo = userNo;
		this.nickname = nickname;
		this.profileImg = profile.getSavedName();
		this.description = profile.getDescription();
		if(followed != null) {
			this.isFollowed = true;
		}else {
			this.isFollowed = false;
		}
	}
}
