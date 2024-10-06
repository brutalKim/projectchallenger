package site.challenger.project_challenger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorInfo {
	private String message;
	private int status;
	private String timestamp;
	private String url;
	private String method;
	private Object requestData;
	private Object responseData;
	private String userAgent;

	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JsonProperty("status")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@JsonProperty("timestamp")
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty("method")
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@JsonProperty("requestData")
	public Object getRequestData() {
		return requestData;
	}

	public void setRequestData(Object requestData) {
		this.requestData = requestData;
	}

	@JsonProperty("responseData")
	public Object getResponseData() {
		return responseData;
	}

	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}

	@JsonProperty("userAgent")
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public String toString() {
		return "ErrorInfo{" + "message='" + message + '\'' + ", status=" + status + ", timestamp='" + timestamp + '\''
				+ ", url='" + url + '\'' + ", method='" + method + '\'' + ", requestData=" + requestData
				+ ", responseData=" + responseData + ", userAgent='" + userAgent + '\'' + '}';
	}
}