package model.admin;

//Made By 나규태
public enum Grade {
	ADMIN(1, "관리자"), BOSS(2, "점장");

	private final int code;
	private final String displayName;

	Grade(int code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}

	public int getCode() {
		return code;
	}

	public String getDisplayName() {
		return displayName;
	}

	// 사용자 등급 수정
	public static Grade fromCode(int code) {
		for (Grade grade : Grade.values()) {
			if (grade.code == code) {
				return grade;
			}
		}
		return null; // 또는 throw new IllegalArgumentException(...)
	}
}
