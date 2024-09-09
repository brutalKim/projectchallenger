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
    
    @Column(name ="content")
    private String content;
    
    @Column(name ="recommend")
    private Long recommend;

    @Column(nullable = false)
    //insert 시 시간 기록
    @CreationTimestamp
    private LocalDateTime date;
    @Builder
    public Post(Users user,String content) {
    	this.users = user;
    	this.content = content;
    	this.recommend = (long) 0;
    }
    public void incrementRecommend() {
    	this.recommend++;
    }
    public void decrementRecommend() {
    	this.recommend--;
    }
}
