// CouponDTO_Add.java

package model.coupon;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class CouponDTO_Add {
    private Integer productId;
    private Integer categoryId;
    private String couponName;
    private Integer percent;
    private LocalDate startTime;
    private LocalDate deadline;
}