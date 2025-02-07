package site.challenger.project_challenger.dto.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostDTO {
	private Long no;
	@Column(length = 1000)
	private String content;
	private LocalDateTime date;
	private Long recommend;
	private Long usersNo;
	private boolean isRecommended;
	private Long commentCount;
	private String writerNickname;
	private String profileImg;
	private List<String> images;
	private List<TaggedChallenge> taggedChallenges;

	public PostDTO(Long no, String content, LocalDateTime date, Long recommend, Long usersNo, Long recommendUsersNo) {
		this.no = no;
		this.content = content;
		this.date = date;
		this.recommend = recommend;
		this.usersNo = usersNo;
		if (recommendUsersNo != null) {
			isRecommended = true;
		} else {
			isRecommended = false;
		}
		commentCount = 0L;
		taggedChallenges = new ArrayList<>();
	}

	public void setImg(List<String> images) {
		this.images = images;
	}

	public void setWriterNickname(String nickname) {
		this.writerNickname = nickname;
	}

	public void addTaggedChallenge(String title, Long challengeNo) {
		this.taggedChallenges.add(new TaggedChallenge(title, challengeNo));
	}

	@Getter
	@Setter
	private class TaggedChallenge {
		private String title;
		private Long challengeNo;

		public TaggedChallenge(String title, Long challengeNo) {
			this.title = title;
			this.challengeNo = challengeNo;
		}
	}
}
