package model.product;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDetailSelectDto {
	// 상품이름
	private String productName;
	
	// 카테고리 명
	private String categoryName;
	
	// 판매가격
	private Integer price;
	
	// 원가
	private Integer cost;
	
	// 최종 수정날짜
	private LocalDate productModDate;

	
	// 이미지 저장위치
	private String imageLocation;
}
