package site.challenger.project_challenger.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.domain.PostImage;

@RequiredArgsConstructor
@Component
public class PostImageManager {
	private final ServletContext servletContext;
	//절대 경로
	private static final String rootPath = System.getProperty("user.dir");
	//이미지 저장
	public List<PostImage> saveImage(Post post ,List<MultipartFile> images) throws IllegalStateException, IOException{
		List<PostImage> postImages = new ArrayList<>();
		for(int index = 0; index<images.size(); index++) {
			String type = getFileType(images.get(index).getContentType());
			//파일 확장자 확인
			//C:\Users\wuwan\OneDrive\Documents\GitHub\challenger\challenger\src\main\resources\static
			if(type.equals("png") || type.equals("jpeg") || type.equals("jpg") || type.equals("gif")) {
				String newFilePath = rootPath+"/src/main/resources/static/postimg/";
				//파일 이름 고유 UUID '_' 이후 이미지 순서
				String storedFileName = UUID.randomUUID().toString()+"_"+index+"."+type;
				//파일 경로
				String dir = newFilePath + storedFileName;
				File file = new File(dir);
				//파일 디렉토리가 존재하지 않다면 생성
				if(!file.exists())file.mkdirs();
				//저장
				images.get(index).transferTo(file);
				//postImage list 에 추가
				postImages.add(new PostImage(post,storedFileName,"/postimg/"+storedFileName));
			}
		}
		//PostEntity에 들어갈 List리턴
		return postImages;
	}
	//이미지 url
	public List<String> getImage(Post post){
		List<String> imgUrl = new ArrayList<>();
		List<PostImage> postImages = post.getPostImage();
		for(PostImage image : postImages) {
			String imageUrl = servletContext.getContextPath() + image.getFilePath();
			imgUrl.add(imageUrl);
		}
		return imgUrl;
	}
	//파일 확장자 추출
	private String getFileType(String contentType) {
		int lastSlashIndex = contentType.lastIndexOf('/');
		return contentType.substring(lastSlashIndex + 1);
	}
}
