package site.challenger.project_challenger.domain;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
@Table(name = "member")
public class Member {
	@Id
	@Column(name = "id", unique = true, nullable = false)
	private String id;
	@Column(name = "nickname", nullable = true)
	private String nickname;

	@Builder
	public Member(String id, String nickname) {
		this.id = id;
		this.nickname = nickname;
	}

//	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(name = "authorities", joinColumns = @JoinColumn(name = "member_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
//	private Set<Authority> authorities;
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Authority> authorities;

}
