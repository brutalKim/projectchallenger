package site.challenger.project_challenger.dto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommonResponseDTO extends ResponseEntity<Map<String, Object>> {
	private String message;
	private String redirectUrl;
	private boolean isSuccess;

	public CommonResponseDTO(Map<String, Object> body, HttpStatusCode status, String message, String redirectUrl,
			Boolean isSuccess) {
		super(body, status);
		if (null != message) {
			body.put("message", message);
		}
		if (null != redirectUrl) {
			body.put("redirectUrl", redirectUrl);
		}
		if (null != isSuccess) {
			body.put("isSuccess", isSuccess);
		}
	}

	public CommonResponseDTO(HttpStatusCode status, Boolean isSuccess) {
		this(new HashMap<>(), status, null, null, isSuccess);
	}

	// 내가 추가한거
	public CommonResponseDTO(HttpStatusCode status) {
		super(new HashMap<>(), status);
	}

	public CommonResponseDTO(HttpStatusCode status, String message) {
		this(new HashMap<>(), status, message, null, null);
	}

	public CommonResponseDTO(Map<String, Object> body, HttpStatusCode status) {
		super(body, status);
	}

	public CommonResponseDTO(Map<String, Object> body, HttpStatusCode status, Boolean isSuccess) {
		this(body, status, null, null, isSuccess);
	}
}
