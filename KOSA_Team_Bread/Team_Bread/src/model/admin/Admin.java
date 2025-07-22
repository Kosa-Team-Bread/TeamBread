package model.admin;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Admin {

	// 관리자 ID
	private Integer adminId;

	// 관리자 이메일
	private String email;

	// 관리자 PW
	private String pw;

	// 관리자 명
	private String adminName;

	// 직위 1 -> 최고 관리자 2 -> 중간 관리자
	private Integer grade;

	// 등록날짜
	private LocalDate adminRegDate;

	// 수정날짜
	private LocalDate adminModDate;
	
	// 기본 생성자
	public Admin() {}
	
	// 회원가입용 생성자
	public Admin(String adminName, String email, String pw) {
		this.adminName = adminName;
		this.email = email;
		this.pw = pw; 
	}
	

	public Admin(Integer adminId, String email, String pw, String adminName, Integer grade, LocalDate adminRegDate, LocalDate adminModDate) {
		this.adminId = adminId;
		this.email = email;
		this.pw = pw;
		this.adminName = adminName;
		this.grade = grade;
		this.adminRegDate = adminRegDate;
		this.adminModDate = adminModDate;
	}
	
	
}
