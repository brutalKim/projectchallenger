package site.challenger.project_challenger.dto;

import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Test extends ResponseEntity<Map<String, Object>> {

	private String message;
	private String redirectUrl;
	private boolean isSuccess;

	public Test(Map<String, Object> body, HttpStatusCode status, String message, String redirectUrl,
			boolean isSuccess) {
		super(body, status);
		body.put("message", message);
		body.put("redirectUrl", redirectUrl);
		body.put("isSuccess", isSuccess);

	}

}
