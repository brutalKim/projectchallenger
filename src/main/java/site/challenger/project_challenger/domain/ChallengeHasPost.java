package site.challenger.project_challenger.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "challenge_has_post")
public class ChallengeHasPost {
	@EmbeddedId
	private ChallengeHasPostPrimaryKey challengeHasPostPrimaryKey;
	
	public ChallengeHasPost(Challenge challenge, Post post) {
		this.challengeHasPostPrimaryKey = new ChallengeHasPostPrimaryKey(challenge,post);
	}
}