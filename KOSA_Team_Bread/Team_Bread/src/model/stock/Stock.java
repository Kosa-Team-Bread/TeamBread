package model.stock;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
public class Stock {
	
	// 재고 ID
	private Integer stockId;
	
	// 관지자 ID
	private Integer adminId;
	
	// 제품 ID
	private Integer productId;
	
	// 카테고리 ID
	private Integer categoryId;
	
	// 재고 수량
	private Integer stockQuantity;
	
	// 저장 위치
	private String location;
	
	// 등록날짜
	private LocalDate stockRegDate;
		
	// 수정날짜
	private LocalDate stockModDate;
	
	
}
