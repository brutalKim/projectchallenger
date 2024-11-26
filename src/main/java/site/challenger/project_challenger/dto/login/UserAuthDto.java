package site.challenger.project_challenger.dto.login;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserAuthDto {
	private String kind; // 종류
	private String comment; // 사유
	private LocalDateTime date; // 정지 해제일

}
