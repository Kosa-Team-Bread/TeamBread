// Coupon.java

package model.coupon;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Coupon 모델 클래스 (DTO)
 * tbl_coupon 테이블의 데이터를 표현합니다.
 * 작성자: 강기범, 김기성, 나규태, 정영규
 */
public class Coupon {

    // 필드: JavaFX 프로퍼티 사용
    private final IntegerProperty couponId;
    private final StringProperty couponName;
    private final IntegerProperty percent;
    private final StringProperty startTime;
    private final StringProperty deadline;

    /**
     * Coupon 생성자
     * @param couponId 쿠폰 ID
     * @param couponName 쿠폰 이름
     * @param percent 할인율
     * @param startTime 쿠폰 시작 시간
     * @param deadline 쿠폰 종료 시간
     */
    public Coupon(int couponId, String couponName, int percent, String startTime, String deadline) {
        this.couponId = new SimpleIntegerProperty(couponId);
        this.couponName = new SimpleStringProperty(couponName);
        this.percent = new SimpleIntegerProperty(percent);
        this.startTime = new SimpleStringProperty(startTime);
        this.deadline = new SimpleStringProperty(deadline);
    }

    // --- 각 필드의 Getter ---
    public int getCouponId() { return couponId.get(); }
    public String getCouponName() { return couponName.get(); }
    public int getPercent() { return percent.get(); }
    public String getStartTime() { return startTime.get(); }
    public String getDeadline() { return deadline.get(); }

    // --- 각 필드의 Property Getter (JavaFX TableView에서 사용) ---
    public IntegerProperty couponIdProperty() { return couponId; }
    public StringProperty couponNameProperty() { return couponName; }
    public IntegerProperty percentProperty() { return percent; }
    public StringProperty startTimeProperty() { return startTime; }
    public StringProperty deadlineProperty() { return deadline; }
}



/*
package model.coupon;
import lombok.*;
import java.time.LocalDate;

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
*/