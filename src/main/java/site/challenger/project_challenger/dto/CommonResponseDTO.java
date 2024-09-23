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
	public CommonResponseDTO(Map<String, Object> body, HttpStatusCode status, String message, String redirectUrl,boolean isSuccess) {
		super(body, status);
		body.put("message", message);
		body.put("redirectUrl", redirectUrl);
		body.put("isSuccess", isSuccess);
	}
	//내가 추가한거
	public CommonResponseDTO(HttpStatusCode status) { 
		super(new HashMap<>(),status);
	}
	public CommonResponseDTO(HttpStatusCode status,String message) {
		super((Map<String, Object>) new HashMap<String,Object>().put("message",message),status);
	}
	public CommonResponseDTO(Map<String, Object> body, HttpStatusCode status) {
		super(body, status);
	}
}
