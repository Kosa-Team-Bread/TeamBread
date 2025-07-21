// Coupon.java

package model.coupon;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Coupon 모델 클래스 (DTO) - Lombok @Data, @Builder 적용
 * tbl_coupon 테이블의 데이터 표현
 */
@Data // @ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor 자동 생성
public class Coupon {

    // 필드: JavaFX 바인딩을 위해 final Property 객체 사용
    private final IntegerProperty couponId;
    private final StringProperty couponName;
    private final IntegerProperty percent;
    private final StringProperty startTime;
    private final StringProperty deadline;

    /**
     * Lombok @Builder를 위한 생성자
     * 빌더를 통해 전달된 값으로 JavaFX Property를 초기화
     *
     * @param couponId      쿠폰 ID
     * @param couponName    쿠폰명
     * @param percent       할인율
     * @param startTime     쿠폰 시작 시간
     * @param deadline      쿠폰 종료 시간
     */
    @Builder
    public Coupon(int couponId, String couponName, int percent, String startTime, String deadline) {
        this.couponId = new SimpleIntegerProperty(couponId);
        this.couponName = new SimpleStringProperty(couponName);
        this.percent = new SimpleIntegerProperty(percent);
        this.startTime = new SimpleStringProperty(startTime);
        this.deadline = new SimpleStringProperty(deadline);
    }

    // --- JavaFX TableView 바인딩을 위한 커스텀 Getter ---
    // Lombok의 @Getter는 couponId 필드에 대해 `getCouponId()`를 만들 때 반환 타입을 `IntegerProperty`로 만듦
    // 하지만 우리는 실제 값인 `int`를 반환해야 하므로, 수동으로 getter 오버라이드
    public int getCouponId() { return couponId.get(); }
    public String getCouponName() { return couponName.get(); }
    public int getPercent() { return percent.get(); }
    public String getStartTime() { return startTime.get(); }
    public String getDeadline() { return deadline.get(); }

    // --- JavaFX Property 자체를 반환하는 Getter (컨트롤러에서 사용) ---
    // 이 메소드들은 `PropertyValueFactory`가 내부적으로 참조하여 바인딩에 사용
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