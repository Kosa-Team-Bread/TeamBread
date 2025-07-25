package util;

import model.admin.Admin;

//Made By 나규태
// 로그인한 사용자 정보를 전역적으로 저장하고 사용할 수 있게 하는 클래스
public class Session {
	// 현재 로그인한 사용자
	private static Admin currentUser;

	// 로그인 시 사용자 정보 저장
	public static void setCurrentUser(Admin user) {
		currentUser = user;
	}

	// 현재 로그인한 사용자 반환
	public static Admin getCurrentUser() {
		return currentUser;
	}

	// 로그아웃 시 사용자 정보 제거
	public static void clear() {
		currentUser = null;
	}
}
