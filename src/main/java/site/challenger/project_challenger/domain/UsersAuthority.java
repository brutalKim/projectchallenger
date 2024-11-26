package site.challenger.project_challenger.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_authority")
public class UsersAuthority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;

	@ManyToOne
	@JoinColumn(name = "users_authority_ref_no", nullable = false)
	private UsersAuthorityRef usersAuthorityRef;

	@ManyToOne
	@JoinColumn(name = "users_no", nullable = false)
	private Users user;

	private LocalDateTime date; // 정지일 이 필드가 비어있으면 정지 아님

	private String comment; // 정지 사유
}
