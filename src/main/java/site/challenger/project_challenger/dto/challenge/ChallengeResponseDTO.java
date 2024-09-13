package site.challenger.project_challenger.dto.challenge;

import org.springframework.http.HttpStatus;

import site.challenger.project_challenger.domain.LocationRef;
import site.challenger.project_challenger.dto.AbstractResponseDTO;

public class ChallengeResponseDTO extends AbstractResponseDTO {

	private long no;
	private String title;
	private LocationRef locationRef;
	private long recommend;

	public ChallengeResponseDTO(HttpStatus status, String errorMessage, String redirectUrl, long no, String title,
			LocationRef locationRef, long recommend) {
		super(status, errorMessage, redirectUrl);
		this.no = no;
		this.title = title;
		this.locationRef = locationRef;
		this.recommend = recommend;
	}

	public ChallengeResponseDTO(HttpStatus status, String errorMessage, String redirectUrl) {
		super(status, errorMessage, redirectUrl);
	}

}
