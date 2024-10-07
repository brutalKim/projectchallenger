package site.challenger.project_challenger.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class ChallengeHasPostPrimaryKey implements Serializable {
	@Column(name = "challenge_no", nullable = false)
	private Long challengeNo;
	@Column(name = "post_no", nullable = false)
	private Long postNo;

	public ChallengeHasPostPrimaryKey(Challenge challenge, Post post) {
		this.challengeNo = challenge.getNo();
		this.postNo = post.getNo();
	}
}