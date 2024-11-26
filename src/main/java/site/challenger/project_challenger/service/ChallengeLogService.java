package site.challenger.project_challenger.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.ChallengeLog;
import site.challenger.project_challenger.domain.ChallengeSub;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.repository.ChallengeLogRepository;
import site.challenger.project_challenger.repository.ChallengeRepository;
import site.challenger.project_challenger.repository.ChallengeSubRepository;
import site.challenger.project_challenger.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChallengeLogService {
	private final ChallengeSubRepository challengeSubRepository;
	private final ChallengeRepository challengeRepository;
	private final UserRepository userRepository;
	private final ChallengeLogRepository challengeLogRepository;
	//챌린지 로그생성
	@Transactional
	public CommonResponseDTO createLog(Long userNo, Long challengeNo) {
		Optional<Challenge> optionalTargetChallenge = challengeRepository.findActiveById(challengeNo);
		Optional<Users> optionalUsers = userRepository.findById(userNo);
		//유저와 챌린지가 존재할경우
		if(optionalTargetChallenge.isPresent() && optionalUsers.isPresent()) {
			Challenge challenge = optionalTargetChallenge.get();
			Users users = optionalUsers.get();
			
			//챌린지 구독여부 충족
			Optional<ChallengeSub> optionalChallengeSub = challengeSubRepository.findByUsersAndChallengeAbledTrue(users, challenge);
			
			//챌린지가 구독하고 있을경우
			if(optionalChallengeSub.isPresent()) {
				ChallengeSub challengeSub = optionalChallengeSub.get();
				LocalDate nowDate = LocalDate.now();
				Optional<ChallengeLog> optionalChallengeLog = challengeLogRepository.findByDate(nowDate, challengeSub);
				//오늘자 챌린지 로그가 이미 존재할 경우
				if(optionalChallengeLog.isPresent()) return new CommonResponseDTO(HttpStatus.CONFLICT);
				//점수 추가
				challengeSub.recordLog(LocalDate.now());
				challengeSubRepository.save(challengeSub);
				//챌린지 로그 기록
				ChallengeLog newChallengeLog = new ChallengeLog(challengeSub,null);
				challengeLogRepository.save(newChallengeLog);
				
				LocalDate thisWeekMonday = getThisWeekMondayDate();
				List<ChallengeLog> challengeLoglist = getChallengelogByDateRange(challengeSub,thisWeekMonday,LocalDate.now());
				List<LogDTO> dtoList = preprocessingLog(challengeLoglist);

				Map map = new HashMap<>();
				map.put("logs",dtoList);
				map.put("todaySuccess",true);
				map.put("point", challengeSub.getPoint());
				return new CommonResponseDTO(map,HttpStatus.CREATED);
			}
			//챌린지 구독하고 있지 않을 경우
			return new CommonResponseDTO(HttpStatus.FORBIDDEN);
		}
		//유저나 챌린지가 존재하지 않을경우
		return new CommonResponseDTO(HttpStatus.BAD_REQUEST);
	}
	//로그삭제
	@Transactional
	public void deleteLog() {
		
	}
	
	//로그 조회
	@Transactional(readOnly = true)
	public CommonResponseDTO getLog(Long userNo, Long challengeNo) {
		Optional<Challenge> optionalTargetChallenge = challengeRepository.findActiveById(challengeNo);
		Optional<Users> optionalUsers = userRepository.findById(userNo);
		//챌린지와 유저가 실재로 존재할시
		if(optionalTargetChallenge.isPresent() && optionalUsers.isPresent()) {
			Challenge challenge = optionalTargetChallenge.get();
			Users users = optionalUsers.get();
			
			//챌린지 구독 확인
			Optional<ChallengeSub> optionalChallengeSub = challengeSubRepository.findByUsersAndChallengeAbledTrue(users, challenge);
			//챌린지 구독 여부 충족 확인
			if(optionalChallengeSub.isPresent()) {
				ChallengeSub challengeSub = optionalChallengeSub.get();
				LocalDate thisWeekMonday = getThisWeekMondayDate();
				List<ChallengeLog> challengeLoglist = getChallengelogByDateRange(challengeSub,thisWeekMonday,LocalDate.now());
				
				LocalDate nowDate = LocalDate.now();
				Optional<ChallengeLog> optionalChallengeLog = challengeLogRepository.findByDate(nowDate, challengeSub);
				
				
				List<LogDTO> dtoList = preprocessingLog(challengeLoglist);
				Map map = new HashMap<>();
				map.put("logs",dtoList);
				
				if(optionalChallengeLog.isPresent()) {
					map.put("todaySuccess",true);
				}else {
					map.put("todaySuccess",false);
					//예상 포인트 보상
					int expectedPoint = challengeSub.getExpectedPoint(LocalDate.now());
					map.put("expectedPoint",expectedPoint);
				}
				map.put("sequentialDates", challengeSub.getSequentialDates());
				
				return new CommonResponseDTO(map,HttpStatus.OK);
			}
			//챌린지 구독 없을시
			return new CommonResponseDTO(HttpStatus.NOT_FOUND);
		}
		//유저와 챌린지가 존재 하지 않을 시
		return new CommonResponseDTO(HttpStatus.BAD_REQUEST);
	}
	//유저 챌린지 구독 정보 가져오기
	@Transactional(readOnly = true)
	public CommonResponseDTO getSub(Long userNo) {
		Optional<Users> optionalUsers = userRepository.findById(userNo);
		if(optionalUsers.isPresent()) {
			Users targetUser = optionalUsers.get();
			List<ChallengeSub> challengeSubs = challengeSubRepository.findByUsers(targetUser);
			List<SubDTO> subDTO = preprocessingSub(challengeSubs);
			Map map = new HashMap <>();
			map.put("subs", subDTO);
			return new CommonResponseDTO(map,HttpStatus.ACCEPTED);
		}
		return new CommonResponseDTO(HttpStatus.NOT_FOUND);
	}
	//연도별 로그 조회
	@Transactional(readOnly = true)
	public CommonResponseDTO getLogByYear(Long subNo, int year) {
		Optional<ChallengeSub> optionalChallengeSub = challengeSubRepository.findById(subNo);
		if(optionalChallengeSub.isPresent()) {
			LocalDate startDate = LocalDate.of(year, 1, 1);
			LocalDate endDate = LocalDate.of(year, 12, 31);
			List<ChallengeLog> logs =  getChallengelogByDateRange(optionalChallengeSub.get(),startDate,endDate);
			List<LogDTO> logDTOs = preprocessingLog(logs);
			Map map = new HashMap<>();
			map.put("logs", logDTOs);
			return new CommonResponseDTO(map,HttpStatus.OK);
		}
		return new CommonResponseDTO(HttpStatus.NOT_FOUND);
	}
	//챌린지 순위 구하기
	@Transactional(readOnly = true)
	public CommonResponseDTO getRank(long ChallengeNo) {
		Optional<Challenge> optionalChallenge = challengeRepository.findActiveById(ChallengeNo);
		if(optionalChallenge.isPresent()) {
			Challenge challenge = optionalChallenge.get();

			
			Pageable pageable = PageRequest.of(0, 20);
			Page<ChallengeSub> pageChallengeSub = challengeSubRepository.getChallengeSubByPointOrderByPoint(challenge, pageable);
			
			List<ChallengeSub> challengeSubs = pageChallengeSub.toList();
			
			List<RankDTO> rankList = new ArrayList<>();
			for(ChallengeSub subs :challengeSubs) {
				Users user = subs.getUsers();
				int point = subs.getPoint();
				RankDTO rankDTO = new RankDTO(user, point);
				rankList.add(rankDTO);
			}
			HashMap<String, Object> map = new HashMap<>();
			map.put("ranks", rankList);
			return new CommonResponseDTO(map,HttpStatus.OK);
		}
		return new CommonResponseDTO(HttpStatus.BAD_REQUEST);
	}
	
	//이번주 월요일 날짜 구하기
	private LocalDate getThisWeekMondayDate() {
		LocalDate today = LocalDate.now();
		int dayOfWeek = today.getDayOfWeek().getValue();
		return today.minusDays(dayOfWeek);
	}
	
	//범위로 챌린지 로그조회
	private List<ChallengeLog> getChallengelogByDateRange(ChallengeSub challengeSub, LocalDate startDate, LocalDate endDate){
		return challengeLogRepository.findByDateRange(startDate, endDate, challengeSub);
	}
	
	private List<LogDTO> preprocessingLog (List<ChallengeLog> entityList){
		List<LogDTO> dtolist = new ArrayList<>();
		for(ChallengeLog entity : entityList) {
			LogDTO dto = new LogDTO();
			dto.setDate(entity.getDate());
			dtolist.add(dto);
		}
		return dtolist;
	}
	private List<SubDTO> preprocessingSub (List<ChallengeSub> entityList){
		List<SubDTO> dtolist = new ArrayList<>();
		for(ChallengeSub entity :entityList) {
			Challenge challenge = entity.getChallenge();
			SubDTO dto = new SubDTO(entity.getNo(),challenge.getTitle(),challenge.getContent(),entity.getDate(),entity.getPoint());
			dtolist.add(dto);
		}
		return dtolist;
	}
	//예상 획득 포인트
	private int getEstimatedPointsEarnings(ChallengeSub challengeSub) {
		List <ChallengeLog>logs = challengeSub.getChallengeLogs();
		LocalDate referDate = LocalDate.now();
		int earningPoint = 0;
		for(ChallengeLog log :logs) {
			if(referDate.minusDays(1) == log.getDate()) {
				earningPoint += 1;
				referDate = referDate.minusDays(1);
			}else {
				break;
			}
		}
		earningPoint += 10;
		return earningPoint;
	}
}
@Getter
@Setter
class LogDTO{
	private LocalDate date;
}

@Getter
@Setter
@AllArgsConstructor
class SubDTO{
	private Long subNo;
	private String title;
	private String description;
	private LocalDateTime startedDate;
	private int point;
}

@Getter
@Setter
class RankDTO{
	private Long userNo;
	private String userNickname;
	private String userProfileImg;
	private int point;
	public RankDTO(Users user, int point) {
		this.userNo = user.getNo();
		this.userNickname = user.getNickname();
		this.userProfileImg = user.getProfile().getSavedName();
		this.point = point;
	}
}




