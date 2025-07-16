package model.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@Builder
public class Inout {

	
	//입출고 ID
	private Integer inputId;
	
	// 재고 ID
	private Integer stockId;
		
	// 관지자 ID
	private Integer adminId;
		
	// 제품 ID
	private Integer productId;
		
	// 카테고리 ID
	private Integer categoryId;
	
	// 입출고 유형 1-> 입고, 2-> 출고
	private Integer inoutType;
	
	// 입출고 사유
	private String inoutContent;
	
	// 등록날짜
	private LocalDate inoutRegDate;
}
