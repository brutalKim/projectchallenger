package site.challenger.project_challenger.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
}
