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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "challenge_sub", uniqueConstraints = @UniqueConstraint(columnNames = {"challenge_no", "Users_no"}))
public class ChallengeSub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne
    @JoinColumn(name = "Users_no", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "challenge_no", nullable = false)
    private Challenge challenge;
    
    @CreationTimestamp
    private LocalDateTime date;

    public ChallengeSub(Users user,Challenge challenge) {
    	this.users = user;
    	this.challenge = challenge;
    }
}