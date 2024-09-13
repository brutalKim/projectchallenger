package site.challenger.project_challenger.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CommonResponseDTO<T> {
	private HttpStatus httpStatus;
	private String errorMessage;
	private String redirectUrl;
	private boolean isSuccess;
	private List<T> responseList;

}
