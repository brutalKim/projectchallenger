package site.challenger.project_challenger.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "post")
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne
    @JoinColumn(name = "users_no", nullable = false)
    private Users users;
    
    @Lob
    @Column(name ="content")
    private String content;
    
    @Column(name ="recommend")
    private Long recommend;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PostComment> comments;

	@OneToMany(mappedBy = "post",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<PostImage> postImage;
	
	@OneToMany(fetch=FetchType.LAZY)
	private List<ChallengeHasPost> challengeHasPost;
    //insert 시 시간 기록
    @CreationTimestamp
    private LocalDateTime date;
    @Builder
    public Post(Users user,String content) {
    	this.users = user; 
    	this.content = content;
    	this.recommend = (long) 0;
    	this.comments = new ArrayList<PostComment>();
    	this.challengeHasPost = new ArrayList<ChallengeHasPost>();
    }
    public void incrementRecommend() {
    	this.recommend++;
    }
    public void decrementRecommend() {
    	this.recommend--;
    }
    public void addComment(PostComment commnt) {
    	this.comments.add(commnt);
    }
    public void addImgs(List<PostImage> postImage) {
    	this.postImage = postImage;
    }
    public void tagChallenge(Challenge challenge) {
    	this.challengeHasPost.add(new ChallengeHasPost(challenge,this));
    }
}
