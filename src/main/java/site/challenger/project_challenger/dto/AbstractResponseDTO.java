package site.challenger.project_challenger.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public abstract class AbstractResponseDTO {
	private HttpStatus httpStatus;
	private String errorMessage;
	private String redirectUrl;

}
