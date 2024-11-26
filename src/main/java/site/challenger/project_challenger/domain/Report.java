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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "report")
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;

	@ManyToOne
	@JoinColumn(name = "report_users", nullable = false)
	private Users reportusers;

	@ManyToOne
	@JoinColumn(name = "target_users", nullable = true)
	private Users targetusers;

	@Column(name = "target_no", nullable = false)
	private Long targetno;

	@Pattern(regexp = "challenge|post|comment", message = "challenge, post, comment 중 하나여야 합니다.")
	@Column(nullable = false)
	private String targetkind; //

	@Column(nullable = false)
	private String reportkind; // csv형태로 저장할 예정 ex) "Violent, Spam, Sexual"

	@Column(name = "report_content", nullable = true)
	private String reportcontent;

	@Column(name = "isdone", nullable = false)
	private Boolean isdone;

	@CreationTimestamp
	private LocalDateTime date;

}
