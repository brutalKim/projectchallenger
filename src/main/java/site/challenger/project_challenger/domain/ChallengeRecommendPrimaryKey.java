package site.challenger.project_challenger.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
public class ChallengeRecommendPrimaryKey {

	@Column(name = "user_no", nullable = false)
	private Long userNo;

	@Column(name = "challenge_no", nullable = false)
	private Long challengeNo;

	public ChallengeRecommendPrimaryKey(Users user, Challenge challenge) {
		this.userNo = user.getNo();
		this.challengeNo = challenge.getNo();
	}

}
