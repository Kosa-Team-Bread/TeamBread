package util;

import java.util.regex.Pattern;

public class ValidationUtil {
	
	// 이메일 정규표현식
	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
	        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	
	// 한글 이름 정규표현식
	private static final String KOREAN_NAME_PATTERN = "^[가-힣]{2,10}$";
	
	private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
	private static final Pattern koreanNamePattern = Pattern.compile(KOREAN_NAME_PATTERN);
	
	// 이메일 검증
	public static boolean isValidEmail(String email) {
		if(email == null || email.trim().isEmpty()) {
			return  false;
		}
		return emailPattern.matcher(email.trim()).matches();
	}

}
