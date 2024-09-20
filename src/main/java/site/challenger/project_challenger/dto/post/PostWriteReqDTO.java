package site.challenger.project_challenger.dto.post;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostWriteReqDTO {
	private String content;
	private List<MultipartFile> images;
}
