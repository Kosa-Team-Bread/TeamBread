package util;

import model.admin.Admin;

// 로그인 성공한 뒤 Session.setCurrentUser 연결해줘서 전역적으로 사용할 수 있게 만들어줘야함
public class Session {
	private static Admin currentUser;

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
