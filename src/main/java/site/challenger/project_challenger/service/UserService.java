package site.challenger.project_challenger.service;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.util.FileSaver;
import site.challenger.project_challenger.util.InsuUtils;

@Service
@RequiredArgsConstructor
public class UserService {
	private final FileSaver fileSaver;

	public String changeProfileImage(HttpServletRequest request, MultipartFile file, long requestUserNo) {

		String imageType = file.getContentType();
		if (!(imageType.contains("png") || imageType.contains("jpg") || imageType.contains("jpeg"))) {
			throw InsuUtils.throwNewResponseStatusException(HttpStatus.FORBIDDEN, "허용된 이미지 파일이 아님");
		}

		try (var inputStream = file.getInputStream()) {

			BufferedImage bufferedImage = ImageIO.read(inputStream);
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			if (width != height) {
				throw InsuUtils.throwNewResponseStatusException(HttpStatus.BAD_REQUEST, "가로 세로가 일치하지 않음");
			}

		} catch (Exception e) {
			throw InsuUtils.throwNewResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 이미지가 아님");
		}

		return fileSaver.saveFileForProfile(file, request, requestUserNo);
	}

}
