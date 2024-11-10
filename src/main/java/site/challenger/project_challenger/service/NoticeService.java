package site.challenger.project_challenger.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.Notice;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.repository.NoticeRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.util.InsuUtils;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final NoticeRepository noticeRepository;
	private final UserRepository userRepository;

	// notice 수 가져오기 except kind = 'notice'
	public CommonResponseDTO getNoticeCount(long requestUserNo) {

		Users requestUser = getUserByUserNo(requestUserNo);
		long countOfNotice = noticeRepository.countNoticesByTargetUser(requestUser);

		Map<String, Object> body = new HashMap<>();
		body.put("countOfNotice", countOfNotice);
		return new CommonResponseDTO(body, HttpStatus.OK);

	}

	public CommonResponseDTO getNotice(long requestUserNo) {
		Map<String, Object> body = new HashMap<>();
		Users requestUser = getUserByUserNo(requestUserNo);

		List<Notice> noticeList = noticeRepository.getNotice(requestUser);

		List<NoticeDTO> otherNoticeList = new ArrayList<>();

		for (Notice notice : noticeList) {
			otherNoticeList.add(getNoticeDTOFromNotice(notice));
		}

		body.put("noticeList", otherNoticeList);

		return new CommonResponseDTO(body, HttpStatus.OK);
	}

	// delete
	public CommonResponseDTO deleteNotice(long requestUserNo, long[] targetArr) {
		Map<String, Object> body = new HashMap<>();
		Users requestUser = getUserByUserNo(requestUserNo);
		List<Long> feedbackList = new ArrayList<Long>();
		body.put("feedbackList", feedbackList);
		for (long targetNo : targetArr) {
			Notice notice = getNoticeByNoticeNo(targetNo);
			if (notice.getTargetusers().equals(requestUser)) {
				noticeRepository.delete(notice);
				feedbackList.add(notice.getNo());
			} else {
				System.out.println("요청 유저와 알림받는 유저가 같지않음");
			}
		}

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	// 읽음 처리
	public CommonResponseDTO readNotice(long requestUserNo, long[] targetArr) {
		Map<String, Object> body = new HashMap<>();
		Users requestUser = getUserByUserNo(requestUserNo);
		List<Long> feedbackList = new ArrayList<Long>();
		body.put("feedbackList", feedbackList);
		for (long targetNo : targetArr) {
			Notice notice = getNoticeByNoticeNo(targetNo);
			if (notice.getTargetusers().equals(requestUser)) {
				notice.setReaded(true);
				Notice a = noticeRepository.save(notice);
				feedbackList.add(a.getNo());
			} else {
				System.out.println("요청 유저와 알림받는 유저가 같지않음");
			}
		}

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	//
	private Users getUserByUserNo(long userNo) {
		return userRepository.findById(userNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("해당 유저는 존재하지 않음: " + userNo));
	}

	private Notice getNoticeByNoticeNo(long noticeNo) {
		return noticeRepository.findById(noticeNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("해당 notice가 존재하지 않음: " + noticeNo));
	}

	private NoticeDTO getNoticeDTOFromNotice(Notice notice) {
		NoticeDTO noticeDTO = new NoticeDTO(notice.getNo(), notice.getSentusers().getNo(),
				notice.getSentusers().getNickname(), notice.getTargetusers().getNo(), notice.getKind(),
				notice.getTargetno(), notice.getTargetmasterno(), notice.getDate(), notice.isReaded());
		return noticeDTO;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	private class NoticeDTO {
		private Long noticeno;
		private Long sentuserno;
		private String sentusernickname;
		private Long targetuserno;
		private String kind;
		private Long targetno;
		private Long targetmasterno;
		private LocalDateTime date;
		private boolean readed;

	}

}
