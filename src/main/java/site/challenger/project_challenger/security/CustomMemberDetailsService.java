package site.challenger.project_challenger.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import site.challenger.project_challenger.domain.Member;
import site.challenger.project_challenger.repository.MemberRepository;

@Service
@AllArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {

	private MemberRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Member member = repository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return new CustomMemberDetails(member);
	}

}
