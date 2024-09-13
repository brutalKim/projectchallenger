package site.challenger.project_challenger.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "challenge_recommend", uniqueConstraints = @UniqueConstraint(columnNames = { "users_no",
		"challenge_no" }))
public class ChallengeRecommend {

	@EmbeddedId
	private ChallengeRecommendPrimaryKey challengeRecommendPrimaryKey;

	@ManyToOne
	@MapsId("userNo")
	@JoinColumn(name = "user_no", insertable = false, updatable = false)
	private Users user;

	@ManyToOne
	@MapsId("challengeNo")
	@JoinColumn(name = "challenge_no", insertable = false, updatable = false)
	private Challenge challenge;

	@CreationTimestamp
	private LocalDateTime date;

}
