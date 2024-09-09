package site.challenger.project_challenger.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
    @CreationTimestamp
    private LocalDateTime date;
    private String comment;
}
 