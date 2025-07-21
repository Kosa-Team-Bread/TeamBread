package model.inout;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class inoutSelectDto {
	
		//입출고 ID
		private Integer inoutId;
			
		// 관지자 이름
		private String adminName;
			
		// 카테고리 이름
		private String categoryName;
		
		// 입출고 상품 이름
		private String productName;
		
		// 입출고 유형 1-> 입고, 2-> 출고
		private String typeName;
		
		// 입출고 수량
		private Integer inoutQuantity;
		
		// 입출고 사유
		private String inoutContent;
		
		// 등록날짜
		private LocalDate inoutRegDate;
}
