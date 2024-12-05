package site.challenger.project_challenger.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.constants.MyRole;
import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.domain.PostComment;
import site.challenger.project_challenger.domain.Report;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersAuthority;
import site.challenger.project_challenger.domain.UsersAuthorityRef;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.report.ReportRequestDto;
import site.challenger.project_challenger.repository.ChallengeRepository;
import site.challenger.project_challenger.repository.PostCommentRepository;
import site.challenger.project_challenger.repository.PostRepository;
import site.challenger.project_challenger.repository.ReportRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRefRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRepository;
import site.challenger.project_challenger.util.InsuUtils;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final ReportRepository reportRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostCommentRepository postCommentRepository;
	private final ChallengeRepository challengeRepository;
	private final UsersAuthorityRepository usersAuthorityRepository;
	private final UsersAuthorityRefRepository usersAuthorityRefRepository;

	public CommonResponseDTO report(long reportUserNo, ReportRequestDto reportRequestDto) {
		Users reportUser = getUserbyUserNo(reportUserNo);

		boolean someoneHasReportAuth = isSomeoneHasReportAuth(reportUser);
		if (!someoneHasReportAuth) {
			throw InsuUtils.throwNewResponseStatusException(HttpStatus.FORBIDDEN, "신고 권한이 없음");
		}

		Users targetUser = null;
		if (reportRequestDto.getTargetUserNo() != 0) {
			targetUser = getUserbyUserNo(reportRequestDto.getTargetUserNo());
		}

		if (reportRepository.existsByReportusersAndTargetusersAndTargetnoAndTargetkind(reportUser, targetUser,
				reportRequestDto.getTargetNo(), reportRequestDto.getTargetKind())) {
			if (reportRequestDto.getTargetKind().equals("post")) {
				throw InsuUtils.throwNewResponseStatusException(HttpStatus.BAD_REQUEST, "이미 신고한 게시글");
			}
			if (reportRequestDto.getTargetKind().equals("comment")) {
				throw InsuUtils.throwNewResponseStatusException(HttpStatus.BAD_REQUEST, "이미 신고한 댓글");
			}
			if (reportRequestDto.getTargetKind().equals("challenge")) {
				throw InsuUtils.throwNewResponseStatusException(HttpStatus.BAD_REQUEST, "이미 신고한 챌린지");
			}
		}

		Report newReport = new Report();
		newReport.setReportusers(reportUser);
		newReport.setTargetusers(targetUser);

		newReport.setTargetno(reportRequestDto.getTargetNo());
		newReport.setTargetkind(reportRequestDto.getTargetKind());
		newReport.setReportkind(reportRequestDto.getReportKind());
		newReport.setReportcontent(reportRequestDto.getReportContent());
		newReport.setIsdone(false);

		reportRepository.save(newReport);

		return new CommonResponseDTO(HttpStatus.OK, true);
	}

	private PostComment getCommentByCommentNo(long commentNo) {
		return postCommentRepository.findById(commentNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 댓글"));
	}

	private Post getPostByPostNo(long postNo) {
		return postRepository.findById(postNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 포스트"));
	}

	private Challenge getChallengeByChallengeNo(long challengeNo) {
		return challengeRepository.findActiveById(challengeNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않거나 삭제된 챌린지."));
	}

	private Users getUserbyUserNo(long userNo) {
		return userRepository.findById(userNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 유저"));
	}

	private boolean isSomeoneHasReportAuth(Users user) {
		UsersAuthorityRef reportAuthRef = getAuthorityRefByAuthority(MyRole.REPORT);
		UsersAuthority authorityByUserAndAuthorityRef = getAuthorityByUserAndAuthorityRef(user, reportAuthRef);
		if (authorityByUserAndAuthorityRef.getDate() == null) {
			// 권한 문제없음
			return true;
		} else {
			// 권한 문제있음
			boolean overBanned = InsuUtils.isOverBanned(authorityByUserAndAuthorityRef.getDate());
			if (overBanned) {
				authorityByUserAndAuthorityRef.setComment(null);
				authorityByUserAndAuthorityRef.setDate(null);
				usersAuthorityRepository.save(authorityByUserAndAuthorityRef);
				return true;
			}

			return false;
		}

	}

	private UsersAuthorityRef getAuthorityRefByAuthority(String authority) {
		return usersAuthorityRefRepository.findByAuthority(authority)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 권한"));
	}

	private UsersAuthority getAuthorityByUserAndAuthorityRef(Users user, UsersAuthorityRef usersAuthorityRef) {
		return usersAuthorityRepository.findByUserAndUsersAuthorityRef(user, usersAuthorityRef).orElseThrow(
				() -> InsuUtils.throwNewResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자가 권한을 부여받지 못했음"));
	}

}
