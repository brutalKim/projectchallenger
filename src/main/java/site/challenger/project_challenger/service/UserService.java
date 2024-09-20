package site.challenger.project_challenger.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.util.FileSaver;

@Service
@RequiredArgsConstructor
public class UserService {
	private final FileSaver fileSaver;

	public String changeProfileImage(HttpServletRequest request, MultipartFile file, long userNo) {

		return fileSaver.saveFileForProfile(file, request, userNo);
	}

}
