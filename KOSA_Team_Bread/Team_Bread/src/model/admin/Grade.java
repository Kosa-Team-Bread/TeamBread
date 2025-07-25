package model.admin;

//Made By 나규태 + CHATGPT

//관리자 등급을 나타내는 열거형(enum)
public enum Grade {
	ADMIN(1, "관리자"), BOSS(2, "점장");

	private final int code; // 등급 코드
	private final String displayName; // 등급 이름

	// 생성자
	Grade(int code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}

	// 등급 코드 반환
	public int getCode() {
		return code;
	}

	// 등급 이름 반환
	public String getDisplayName() {
		return displayName;
	}

	// 등급 코드를 통해 enum 값 찾기
	public static Grade fromCode(int code) {
		for (Grade grade : Grade.values()) {
			if (grade.code == code) {
				return grade;
			}
		}
		return null; // 해당 코드가 없을 경우 null 반환
	}
}
