// Coupon.java

package model.coupon;
import lombok.*;
import java.time.LocalDate;

// lombok 어노테이션 사용
@Data
@Builder
public class Coupon {
	
	// 쿠폰 ID
	private Integer couponId;
	
	// 제품 ID
	private Integer productId;
		
	// 카테고리 Id
	private Integer categoryId;

	// 쿠폰명
	private String couponName;
	
	// 할인율
	private Integer percent;
	
	// 등록날짜
	private LocalDate startTime;
			
	// 수정날짜
	private LocalDate deadLine;
}
