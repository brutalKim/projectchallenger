package site.challenger.project_challenger.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "uid"),
		@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "nickname") })
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;

	@Column(nullable = false, length = 255)
	private String uid;

	@Column(nullable = false, length = 45)
	private String nickname;

	@Column(nullable = false, length = 255)
	private String email;

	@ManyToOne
	@JoinColumn(name = "oauth_ref_no", nullable = false)
	private OauthRef oauthRef;

	@ManyToOne
	@JoinColumn(name = "location_ref_no", nullable = false)
	private LocationRef locationRef;

	@Column(nullable = false)
	private Boolean enable;

	private LocalDateTime latestLoginDate;

	// insert 시 시간 기록
	@CreationTimestamp
	private LocalDateTime signupDate;

	@Builder
	public Users(String uid, String nickname, String email, OauthRef oauthRef, LocationRef locationRef,
			Boolean enable) {
		this.uid = uid;
		this.nickname = nickname;
		this.email = email;
		this.locationRef = locationRef;
		this.enable = enable;
	}
}
