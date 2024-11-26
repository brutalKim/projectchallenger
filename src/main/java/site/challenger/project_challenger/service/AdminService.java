package site.challenger.project_challenger.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import site.challenger.project_challenger.constants.MyRole;
import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.domain.PostComment;
import site.challenger.project_challenger.domain.Report;
import site.challenger.project_challenger.domain.UserRoleRef;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersAuthority;
import site.challenger.project_challenger.domain.UsersAuthorityRef;
import site.challenger.project_challenger.domain.UsersRole;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.admin.RestrictionDto;
import site.challenger.project_challenger.repository.ChallengeHasPostRepository;
import site.challenger.project_challenger.repository.ChallengeRepository;
import site.challenger.project_challenger.repository.PostCommentRepository;
import site.challenger.project_challenger.repository.PostRepository;
import site.challenger.project_challenger.repository.ProfileRepository;
import site.challenger.project_challenger.repository.ReportRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRefRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRepository;
import site.challenger.project_challenger.repository.UsersRoleRefRepository;
import site.challenger.project_challenger.repository.UsersRoleRepository;
import site.challenger.project_challenger.util.InsuUtils;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final ReportRepository reportRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostCommentRepository postCommentRepository;
	private final ChallengeRepository challengeRepository;
	private final ProfileRepository profileRepository;
	private final ChallengeHasPostRepository challengeHasPostRepository;
	private final UsersRoleRepository usersRoleRepository;
	private final UsersRoleRefRepository usersRoleRefRepository;
	private final UsersAuthorityRepository usersAuthorityRepository;
	private final UsersAuthorityRefRepository usersAuthorityRefRepository;

	// 신고된 포스트 가져오기
	public CommonResponseDTO getReportedPosts(int page) {
		Map<String, Object> body = new HashMap<>();
		Pageable pageable = PageRequest.of(page, 10);

		Page<Report> byTargetkind = reportRepository.findByTargetkindAndIsdoneFalse("post", pageable);
		InsuUtils.insertMapWithPageInfo(body, byTargetkind);

		List<Report> ReportList = byTargetkind.getContent();
		List<PostsReportResponseDto> responseList = new ArrayList<>();
		body.put("responseList", responseList);

		for (Report report : ReportList) {
			PostsReportResponseDto dto = new PostsReportResponseDto();
			Post post = null;
			try {
				post = getPostByPostNo(report.getTargetno());

			} catch (Exception e) {
				// 이미 포스트가 지워짐
				List<Report> byTargetkindAndTargetno = reportRepository.findByTargetkindAndTargetno("post",
						report.getTargetno());
				for (Report report2 : byTargetkindAndTargetno) {
					reportRepository.delete(report2);
				}
				continue;
			}
			Users reportedUser = report.getTargetusers();
			dto.setPostno(post.getNo().toString());
			dto.setContent(post.getContent());
			dto.setDate(post.getDate());
			dto.setImages(post.getPostImage().stream().map((item) -> item.getStoredName()).toList());
			dto.setNickname(reportedUser.getNickname());
			dto.setProfileimage(reportedUser.getProfile().getSavedName());
			dto.setUserno(reportedUser.getNo().toString());
			dto.setTaggedchallengeList(challengeHasPostRepository.findByPostNo(post.getNo()).stream()
					.map(item -> new TaggedChallenge(
							getChallengeByChallengeNo(item.getChallengeHasPostPrimaryKey().getChallengeNo()).getTitle(),
							item.getChallengeHasPostPrimaryKey().getChallengeNo().toString()))
					.toList());

			responseList.add(dto);
		}

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	// 신고된 내역 가져오기
	public CommonResponseDTO getReports(String targetkind, long targetno, int page) {
		Map<String, Object> body = new HashMap<>();
		Pageable pageable = PageRequest.of(page, 10);

		Page<Report> byTargetkindAndTargetno = reportRepository.findByTargetkindAndTargetnoAndIsdoneFalse(targetkind,
				targetno, pageable);
		InsuUtils.insertMapWithPageInfo(body, byTargetkindAndTargetno);

		List<Report> reportList = byTargetkindAndTargetno.getContent();
		List<ReportResponseDto> responseList = new ArrayList<>();
		body.put("responseList", responseList);
		for (Report report : reportList) {
			ReportResponseDto dto = new ReportResponseDto();
			dto.setReportcontent(report.getReportcontent());
			dto.setReportkind(report.getReportkind());
			dto.setReportno(report.getNo().toString());
			dto.setReportuserno(report.getReportusers().getNo().toString());
			dto.setTargetno(report.getTargetno().toString());
			dto.setReportusernickname(report.getReportusers().getNickname());
			dto.setDate(report.getDate());

			responseList.add(dto);
		}

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	// 신고된 내역의 숫자 가져오기
	public CommonResponseDTO getReportsCount(String targetkind, long targetno) {
		Map<String, Object> body = new HashMap<>();
		long countByTargetkindAndTargetno = reportRepository.countByTargetkindAndTargetno(targetkind, targetno);
		body.put("allreportcount", countByTargetkindAndTargetno);

		long countByTargetkindAndTargetnoAndIsdoneFalse = reportRepository
				.countByTargetkindAndTargetnoAndIsdoneFalse(targetkind, targetno);
		body.put("readedreportcount", countByTargetkindAndTargetnoAndIsdoneFalse);

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	// 신고내역 읽음 처리
	public CommonResponseDTO readReport(long reportNo) {

		Report reportByReportNo = getReportByReportNo(reportNo);
		reportByReportNo.setIsdone(true);
		reportRepository.save(reportByReportNo);

		return new CommonResponseDTO(HttpStatus.OK, true);
	}

	// 신고된 댓글 가져오기
	public CommonResponseDTO getReportedComments(int page) {
		Map<String, Object> body = new HashMap<>();
		Pageable pageable = PageRequest.of(page, 10);

		Page<Report> byTargetkindAndIsdoneFalse = reportRepository.findByTargetkindAndIsdoneFalse("comment", pageable);
		InsuUtils.insertMapWithPageInfo(body, byTargetkindAndIsdoneFalse);

		List<Report> reportList = byTargetkindAndIsdoneFalse.getContent();
		List<CommentsReportResponseDto> responseList = new ArrayList<>();
		body.put("responseList", responseList);

		for (Report report : reportList) {
			CommentsReportResponseDto dto = new CommentsReportResponseDto();
			PostComment targetComment = null;

			try {
				targetComment = getCommentByCommentNo(report.getTargetno());

			} catch (Exception e) {
				// 이미 댓글이 지워짐
				List<Report> byTargetkindAndTargetno = reportRepository.findByTargetkindAndTargetno("comment",
						report.getTargetno());
				for (Report report2 : byTargetkindAndTargetno) {
					reportRepository.delete(report2);
				}
				continue;
			}

			dto.setCommentno(targetComment.getNo().toString());
			dto.setContent(targetComment.getContent());
			dto.setDate(targetComment.getDate());
			dto.setNickname(targetComment.getUsers().getNickname());
			dto.setProfileimage(targetComment.getUsers().getProfile().getSavedName());
			dto.setUserno(targetComment.getUsers().getNo().toString());

			responseList.add(dto);
		}

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	// 신고된 챌린지들 가져오기
	public CommonResponseDTO getReportedChallenges(int page) {
		Map<String, Object> body = new HashMap<>();
		Pageable pageable = PageRequest.of(page, 10);

		Page<Report> byTargetkindAndIsdoneFalse = reportRepository.findByTargetkindAndIsdoneFalse("challenge",
				pageable);
		InsuUtils.insertMapWithPageInfo(body, byTargetkindAndIsdoneFalse);

		List<Report> reportList = byTargetkindAndIsdoneFalse.getContent();
		List<ChallengesReportResponseDto> responseList = new ArrayList<>();
		body.put("responseList", responseList);

		for (Report report : reportList) {
			ChallengesReportResponseDto dto = new ChallengesReportResponseDto();
			Challenge reportedChallenge = null;

			try {
				reportedChallenge = getChallengeByChallengeNo(report.getTargetno());

			} catch (Exception e) {
				// 이미 댓글이 지워짐
				List<Report> byTargetkindAndTargetno = reportRepository.findByTargetkindAndTargetno("challenge",
						report.getTargetno());
				for (Report report2 : byTargetkindAndTargetno) {
					reportRepository.delete(report2);
				}
				continue;
			}

			dto.setChallengeno(reportedChallenge.getNo().toString());
			dto.setContent(reportedChallenge.getContent());
			dto.setTitle(reportedChallenge.getTitle());
			dto.setUserno(reportedChallenge.getUsers().getNo().toString());

			responseList.add(dto);
		}

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	public CommonResponseDTO getAdminList() {
		Map<String, Object> body = new HashMap<>();
		Optional<UserRoleRef> role = usersRoleRefRepository.findByRole(MyRole.ROLE_ADMIN);
		List<UsersRole> byUserRoleRef = usersRoleRepository.findByUserRoleRef(role.get());

		List<UserProfileDto> responseList = new ArrayList<>();
		body.put("responseList", responseList);

		for (UsersRole usersRole : byUserRoleRef) {
			Users userAdmin = usersRole.getUser();
			UserProfileDto dto = new UserProfileDto();
			dto.setDate(userAdmin.getSignupDate());
			dto.setIsadmin(isSomeoneAdmin(userAdmin));
			dto.setNickname(userAdmin.getNickname());
			dto.setRecentdate(userAdmin.getSignupDate());
			dto.setUserno(userAdmin.getNo().toString());

			responseList.add(dto);
		}

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	public CommonResponseDTO getUserByUserNickName(String nickname) {

		Map<String, Object> body = new HashMap<>();

		Users getUser = getUserByUserNickname(nickname);
		List<UserProfileDto> responseList = new ArrayList<>();
		body.put("responseList", responseList);

		UserProfileDto dto = new UserProfileDto();
		dto.setDate(getUser.getSignupDate());
		dto.setNickname(getUser.getNickname());
		dto.setUserno(getUser.getNo().toString());
		dto.setRecentdate(getUser.getLatestLoginDate());
		dto.setIsadmin(isSomeoneAdmin(getUser));
		responseList.add(dto);

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	public CommonResponseDTO addAdmin(String nickname) {

		Map<String, Object> body = new HashMap<>();
		List<UserProfileDto> responseList = new ArrayList<>();
		body.put("responseList", responseList);

		Users targetUser = getUserByUserNickname(nickname);
		UserRoleRef roleRef = getUserRoleRefByRole(MyRole.ROLE_ADMIN);
		UsersRole userRole = new UsersRole(targetUser, roleRef);

		UsersRole save = usersRoleRepository.save(userRole);

		return getUserByUserNickName(nickname);
	}

	public CommonResponseDTO deleteAdmin(String nickname) {

		Users targetUser = getUserByUserNickname(nickname);
		UserRoleRef roleRef = getUserRoleRefByRole(MyRole.ROLE_ADMIN);

		Optional<UsersRole> byUserAndUserRoleRef = usersRoleRepository.findByUserAndUserRoleRef(targetUser, roleRef);

		if (byUserAndUserRoleRef.isPresent()) {
			UsersRole usersRole = byUserAndUserRoleRef.get();
			usersRoleRepository.delete(usersRole);
		}

		return getUserByUserNickName(nickname);
	}

	@Transactional
	public CommonResponseDTO restrictUser(RestrictionDto restrictionDto) {

		String comment = restrictionDto.getComment();
		String restriction = restrictionDto.getRestriction();
		String targetkind = restrictionDto.getTargetkind();
		long targetno = restrictionDto.getTargetno();
		long targetuserno = restrictionDto.getTargetuserno();

		Users targetUser = getUserbyUserNo(targetuserno);

		if (targetkind.equals("post")) {
			Post postByPostNo = getPostByPostNo(targetno);
			postRepository.delete(postByPostNo);
		} else if (targetkind.equals("comment")) {
			PostComment commentByCommentNo = getCommentByCommentNo(targetno);
			postCommentRepository.delete(commentByCommentNo);
		} else if (targetkind.equals("challenge")) {
			Challenge challengeByChallengeNo = getChallengeByChallengeNo(targetno);
			challengeByChallengeNo.setAbled(false);
			challengeRepository.save(challengeByChallengeNo);
		} else if (targetkind.equals("report")) {
			Report reportByReportNo = getReportByReportNo(targetno);
			reportRepository.delete(reportByReportNo);
			if (restriction.equals("1-day")) {
				banUser(targetUser, MyRole.REPORT, 1, comment);
			} else if (restriction.equals("3-day")) {
				banUser(targetUser, MyRole.REPORT, 3, comment);
			} else if (restriction.equals("7-day")) {
				banUser(targetUser, MyRole.REPORT, 7, comment);
			}
			return new CommonResponseDTO(HttpStatus.OK, true);
		}

		if (restriction.equals("1-day")) {
			banUser(targetUser, MyRole.WRITE, 1, comment);
		} else if (restriction.equals("3-day")) {
			banUser(targetUser, MyRole.WRITE, 3, comment);
		} else if (restriction.equals("7-day")) {
			banUser(targetUser, MyRole.WRITE, 7, comment);
		}
		// 정지 메시지 보내줘야할까 추가기능 구상

		return new CommonResponseDTO(HttpStatus.OK, true);
	}

	public CommonResponseDTO noProblem(String targetkind, long targetno) {

		List<Report> byTargetkindAndTargetno = reportRepository.findByTargetkindAndTargetno(targetkind, targetno);

		for (Report report : byTargetkindAndTargetno) {
			reportRepository.delete(report);
		}

		return new CommonResponseDTO(HttpStatus.OK, true);
	}

	@Setter
	@Getter
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	class UserProfileDto {
		private String userno;
		private String nickname;
		private LocalDateTime date;
		private LocalDateTime recentdate;
		private boolean isadmin;
	}

	@Setter
	@Getter
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	class AdminUserDto {
		private String userno;
		private String usernickname;
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class ReportResponseDto {
		private String reportuserno;
		private String reportkind;
		private String reportcontent;
		private String reportno;
		private String targetno;
		private String reportusernickname;
		private LocalDateTime date;
	}

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	class CommentsReportResponseDto {
		private String commentno;
		private String userno;
		private String profileimage;
		private String nickname;
		private String content;
		private LocalDateTime date;

	}

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	class ChallengesReportResponseDto {
		private String challengeno;
		private String userno;
		private String title;
		private String content;
	}

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	class PostsReportResponseDto {
		private String postno;
		private String userno;
		private String profileimage;
		private String nickname;
		private String content;
		private LocalDateTime date;

		private List<String> images;
		private List<TaggedChallenge> taggedchallengeList;

	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class TaggedChallenge {
		private String challengetitle;
		private String challengeno;
	}

	private UserRoleRef getUserRoleRefByRole(String role) {
		return usersRoleRefRepository.findByRole(role)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 역할"));
	}

	private boolean isSomeoneAdmin(Users user) {
		return usersRoleRepository.existsByUserAndUserRoleRef(user, getUserRoleRefByRole(MyRole.ROLE_ADMIN));
	}

	private Users getUserByUserNickname(String nickname) {
		return userRepository.findByNickname(nickname)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 유저"));
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

	private Report getReportByReportNo(long reportNo) {
		return reportRepository.findById(reportNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 신고내역"));
	}

	private UsersAuthorityRef getAuthorityRefByAuthority(String authority) {
		return usersAuthorityRefRepository.findByAuthority(authority)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 권한"));
	}

	private UsersAuthority getAuthorityByUserAndAuthorityRef(Users user, UsersAuthorityRef usersAuthorityRef) {
		return usersAuthorityRepository.findByUserAndUsersAuthorityRef(user, usersAuthorityRef).orElseThrow(
				() -> InsuUtils.throwNewResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자가 권한을 부여받지 못했음"));
	}

	private void banUser(Users user, String auth, long days, String comment) {
		UsersAuthorityRef authorityRefByAuthority = getAuthorityRefByAuthority(auth);
		UsersAuthority authorityByUserAndAuthorityRef = getAuthorityByUserAndAuthorityRef(user,
				authorityRefByAuthority);
		authorityByUserAndAuthorityRef.setComment(comment);
		authorityByUserAndAuthorityRef.setDate(LocalDateTime.now().plusDays(days));

		usersAuthorityRepository.save(authorityByUserAndAuthorityRef);
	}

}
