package site.challenger.project_challenger.domain;

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

@Getter
@NoArgsConstructor
@Entity
@Table(name = "follow")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
    
    @ManyToOne
    @JoinColumn(name = "users_no", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "follow_users_no", nullable = false)
    private Users followUsers;
    
    public Follow (Users user, Users followUser) {
    	this.users = user;
    	this.followUsers = followUser;
    }
}
