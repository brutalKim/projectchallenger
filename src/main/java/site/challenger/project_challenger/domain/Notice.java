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
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;

	@ManyToOne
	@JoinColumn(name = "sent_users", nullable = false)
	private Users sentUsers;

	@ManyToOne
	@JoinColumn(name = "target_users", nullable = false)
	private Users targetusers;

	@Pattern(regexp = "follow|post|postComment|comment", message = "알림 유형은 follow, post, postComment, comment 중 하나여야 합니다.")
	@Column(nullable = false)
	private String kind;

	@Column(name = "target_no", nullable = false)
	private Long targetno;

	@Column(name = "targetMaster_no", nullable = true)
	private Long targetmasterno;

	@Column(name = "readed", nullable = false)
	private boolean readed;

	@CreationTimestamp
	private LocalDateTime date;

}
