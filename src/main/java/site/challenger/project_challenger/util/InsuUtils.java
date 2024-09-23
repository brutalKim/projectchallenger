package site.challenger.project_challenger.util;

import org.springframework.security.core.Authentication;

public class InsuUtils {
	public static long getRequestUserNo(Authentication authentication) {
		return Long.parseLong(authentication.getName());
	}

}
