package site.challenger.project_challenger.domain;

import jakarta.persistence.Column;
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
@Entity
@Table(name = "post_image")
@NoArgsConstructor
public class PostImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;

	@ManyToOne
	@JoinColumn(name = "post_no", nullable = false)
	private Post post;

	@Column(name = "stored_name", nullable = false)
	private String storedName;

	@Column(name = "file_path", nullable = false)
	private String filePath;

	public PostImage(Post post, String storedName, String filePath) {
		this.post = post;
		this.storedName = storedName;
		this.filePath = filePath;
	}
}