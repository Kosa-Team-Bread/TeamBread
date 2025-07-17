package model.product;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
public class Product {

	// 제품 ID
	private Integer productId;
	
	// 카테고리 Id
	private Integer categoryId;
	
	// 제품명
	private String productName;
	
	// 판매가격
	private Integer price;
	
	// 원가
	private Integer cost;
	
	// 등록날짜
	private LocalDate productRegDate;
			
	// 수정날짜
	private LocalDate productModDate;
}
