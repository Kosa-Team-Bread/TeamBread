package util;

import java.time.LocalDate;

import lombok.Builder;
import model.admin.Admin;

// 로그인 성공한 뒤 Session.setCurrentUser 연결해줘서 전역적으로 사용할 수 있게 만들어줘야함
@Builder
public class Session {
	private static Admin currentUser = Admin.builder().adminId(1).pw("1234").adminName("kosa").grade(1)
			.adminRegDate(LocalDate.now().minusDays(1)).adminModDate(LocalDate.now()).email("a1@naver.com").build();
	// private static Admin currentUser;

	public static void setCurrentUser(Admin user) {
		currentUser = user;
	}

	public static Admin getCurrentUser() {
		return currentUser;
	}

	public static void clear() {
		currentUser = null;
	}
}
