package site.challenger.project_challenger.dto.login;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.LocationRef;

@NoArgsConstructor
@Getter
@Setter
public class AfterLoginInfoDTO {
	private long userNo;
	private LocalDateTime userCreateTime;
	private String userProfileDescription;
	private String userProfileImage;
	private String userNickName;
	private LocationRef userLocationRef;

}
