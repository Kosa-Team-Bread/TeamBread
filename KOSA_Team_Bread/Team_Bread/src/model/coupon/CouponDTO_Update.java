// CouponDTO_Update.java

package model.coupon;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class CouponDTO_Update {
    private int couponId; // 수정할 쿠폰을 식별하기 위한 ID (필수)
    private Integer productId;
    private Integer categoryId;
    private String couponName;
    private Integer percent;
    private LocalDate startTime;
    private LocalDate deadline;
}