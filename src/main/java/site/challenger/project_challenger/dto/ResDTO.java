package site.challenger.project_challenger.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
public class ResDTO {
	private HttpStatus status;
	private String msg;
}