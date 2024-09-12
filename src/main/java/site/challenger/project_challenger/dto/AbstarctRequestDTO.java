package site.challenger.project_challenger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.JwtModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstarctRequestDTO {

	private JwtModel jwtModel;

}
