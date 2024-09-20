package site.challenger.project_challenger.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.constants.Common;
import site.challenger.project_challenger.domain.Profile;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.repository.ProfileRepository;
import site.challenger.project_challenger.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class FileSaver {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;

	public String saveFileForProfile(MultipartFile file, HttpServletRequest request, long userNo) {

		Users user = userRepository.findById(userNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당유저없음"));

		Profile profile = profileRepository.findByUser(user).orElse(null);
		String savedFileName = "";

		if (file != null && !file.isEmpty()) {

			String originalFileName = file.getOriginalFilename();
			String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
			savedFileName = UUID.randomUUID().toString() + fileExtension;

			Path filePath = Paths.get(Common.UPLOAD_DIR + savedFileName);

			// 저장할 디렉토리가 존재하지 않으면 생성
			File dir = new File(Common.UPLOAD_DIR);
			if (!dir.exists()) {
				dir.mkdirs(); // 디렉토리 생성
			}

			// 파일 저장
			try {
				Files.write(filePath, file.getBytes());
				if (profile == null) {
					// 프로필이 없을 때
					Profile newProfile = new Profile();
					newProfile.setOriginalName(originalFileName);
					newProfile.setSavedName(savedFileName);
					newProfile.setUser(user);
					profileRepository.save(newProfile);
				} else {
					// 프로필이 있을 때
					profile.setOriginalName(originalFileName);
					profile.setSavedName(savedFileName);
					profileRepository.save(profile);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 저장할 수 없음");
			}

			logger.info("\nIp: {}\nUserNo: {} \nSave File Name : {}", request.getRemoteAddr(), userNo, savedFileName);

		}

		return savedFileName;
	}

}
