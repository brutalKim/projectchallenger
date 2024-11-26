package site.challenger.project_challenger.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "challenge_sub", uniqueConstraints = @UniqueConstraint(columnNames = { "challenge_no", "Users_no" }))
public class ChallengeSub {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;

	@ManyToOne
	@JoinColumn(name = "users_no", nullable = false)
	private Users users;

	@ManyToOne
	@JoinColumn(name = "challenge_no", nullable = false)
	private Challenge challenge;

	@CreationTimestamp
	private LocalDateTime date;
	
	@OneToMany(mappedBy = "challengeSub", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ChallengeLog> challengeLogs = new ArrayList<>();
	
	@Column(nullable = true)
    private LocalDate latestDate;

	@Column(nullable = false)
	private int sequentialDates;

	@Column(nullable = false)
	private int point;
	
	public ChallengeSub(Users user, Challenge challenge) {
		this.users = user;
		this.challenge = challenge;
		this.point = 0;
		this.latestDate = null;
		this.sequentialDates = 0;
	}
	//로그 기록 포인트 합산
	public void recordLog(LocalDate today) {
		if (this.latestDate == null) {
	        // 최초 기록
	        this.sequentialDates = 0;
	    } else if (this.latestDate.isEqual(today.minusDays(1))) {
	        // 연속 날짜인 경우
	        this.sequentialDates++;
	        this.point += this.sequentialDates; // 추가 포인트
	    } else {
	        // 연속 날짜가 아닌 경우
	        this.sequentialDates = 0;
	    }

	    this.latestDate = today;
	    this.point += 10; // 기본 포인트
	}
	//예상 획득 포인트 조회
	public int getExpectedPoint(LocalDate today) {
		int expectedPoint = 10;
		if(this.latestDate!=null && this.latestDate.isEqual(today.minusDays(1))) {
			return expectedPoint +this.sequentialDates;
		}
		return expectedPoint;
	}
}