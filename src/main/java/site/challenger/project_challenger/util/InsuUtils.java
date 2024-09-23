package site.challenger.project_challenger.util;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import site.challenger.project_challenger.constants.Common;

public class InsuUtils {
	public static long getRequestUserNo(Authentication authentication) {
		return Long.parseLong(authentication.getName());
	}

	public static ResponseStatusException throwNewResponseStatusException(HttpStatus httpStatus, String reason) {

		return new ResponseStatusException(httpStatus, reason);
	}

	public static ResponseStatusException throwNewResponseStatusException(String reason) {

		return new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
	}

	public static void insertMapWithPageInfo(Map<String, Object> body, Page<?> pagedList) {

		body.put(Common.TOTAL_PAGE, pagedList.getTotalPages());
		body.put(Common.TOTAL_ELEMENT, pagedList.getTotalElements());
		body.put(Common.HAS_NEXT_PAGE, pagedList.hasNext());
		body.put(Common.HAS_PREVIOUS_PAGE, pagedList.hasPrevious());
		body.put(Common.SIZE_OF_PAGE, pagedList.getSize());
		body.put(Common.CURRENT_PAGE, pagedList.getNumber());

	}

}
