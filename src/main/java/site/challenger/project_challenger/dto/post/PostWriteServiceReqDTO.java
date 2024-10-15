package site.challenger.project_challenger.dto.post;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PostWriteServiceReqDTO {
	private Long writerId;
	private String content;
    private List<MultipartFile> images;
    private List<Long> tagChallenges;
    public PostWriteServiceReqDTO() {
    	this.images = null;
    	this.tagChallenges = null;
    }
}
