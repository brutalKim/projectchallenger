package site.challenger.project_challenger.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "profile")
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long no;

	@OneToOne
	@JoinColumn(name = "users_no", unique = true)
	private Users user;

	// 자기 설명 등 보충될예정
	@Column(name = "description", nullable = true)
	private String description;

	@Column(name = "original_name", nullable = false)
	private String originalName;

	@Column(name = "saved_name", nullable = false)
	private String savedName;

}
