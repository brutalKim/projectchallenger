package site.challenger.project_challenger.domain;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "challenge_log")
public class ChallengeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne
    @JoinColumn(name = "challenge_sub_no", nullable = false)
    private ChallengeSub challengeSub;
    
    @OneToOne
    @JoinColumn(name = "post", nullable = true)
    private Post post;
    
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate date;
    
    public ChallengeLog(ChallengeSub challengeSub,Post post) {
    	this.challengeSub = challengeSub;
    	this.post = post;
    }
}