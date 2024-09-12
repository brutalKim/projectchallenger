package site.challenger.project_challenger.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.ResDTO;
import site.challenger.project_challenger.dto.post.PostWriteServiceReqDTO;
import site.challenger.project_challenger.repository.PostRepository;
import site.challenger.project_challenger.repository.UserRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class PostManagementService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	public ResDTO writePost(PostWriteServiceReqDTO req) {
		ResDTO res = null;
		Long writerId = req.getWriterId();
		String content = req.getContent();
		try {
			Optional<Users> writer = userRepository.findById(writerId);
			if(writer.isPresent()) {
				Post newPost = new Post(writer.get(),content);
				postRepository.save(newPost);
				res = new ResDTO(HttpStatus.OK,"성공");
			}
		}catch(Exception e) {
			res = new ResDTO(HttpStatus.CONFLICT,"서버 내부오류");
		}finally {
			return res;
		}
	}
	
}
