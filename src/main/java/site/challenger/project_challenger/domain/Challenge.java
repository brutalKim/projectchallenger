package site.challenger.project_challenger.domain;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "challenge")
public class Challenge {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;

	@ManyToOne
	@JoinColumn(name = "users_no", nullable = false)
	private Users users;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@ManyToOne
	@JoinColumn(name = "location_ref_no", nullable = false)
	private LocationRef locationRef;

	@Column(nullable = false)
	private Long recommend;

	@OneToMany(fetch = FetchType.LAZY)
	private List<ChallengeHasPost> challengeHasPost;

	@Column(nullable = false)
	private boolean abled;

	@Builder
	public Challenge(Users user, LocationRef locationRef, String title, String content) {
		this.users = user;
		this.locationRef = locationRef;
		this.title = title;
		this.content = content;
		this.recommend = (long) 0;
	}

	public void incrementRecommend() {
		this.recommend++;
	}

	public void decrementRecommend() {
		this.recommend--;
	}

	@Override
	public int hashCode() {
		return Objects.hash(no);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Challenge other = (Challenge) obj;
		return Objects.equals(no, other.no);
	}

}
