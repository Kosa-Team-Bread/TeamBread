// Admin.java

package model.admin;

import java.time.LocalDate;

import lombok.*;

@Data
@Builder
public class Admin {
	
	
	// 관리자 ID
	private Integer adminId;
	
	// 관리자 PW
	private String pw;
	
	//관리자 명
	private String adminName;
	
	// 직위 1 -> 최고 관리자 2 -> 중간 관리자
	private Integer grade;
	
	// 등록날짜
	private LocalDate adminRegDate;
	
	// 수정날짜
	private LocalDate adminModDate;

}
