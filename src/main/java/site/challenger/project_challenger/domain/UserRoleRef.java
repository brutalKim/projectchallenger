package site.challenger.project_challenger.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "users_role_ref")
public class UserRoleRef {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
	@Column(name="role")
	private String role;
}
