// CouponDAO.java

package model.coupon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CouponDAO {

    /**
     * 모든 쿠폰 정보를 DB에서 조회 후 반환
     * @return 쿠폰 리스트 (ObservableList)
     */
    public ObservableList<Coupon> getAllCoupons() {
        ObservableList<Coupon> couponList = FXCollections.observableArrayList();
        
        // [수정] tbl_category와 JOIN하여 category_name을 함께 조회합니다.
        // LEFT JOIN을 사용하여 쿠폰에 연결된 카테고리가 없더라도 쿠폰 목록에서 누락되지 않도록 합니다.
        String sql = "SELECT c.coupon_id, c.product_id, c.category_id, cat.category_name, " +
                     "c.coupon_name, c.percent, c.starttime, c.deadline " +
                     "FROM tbl_coupon c " +
                     "LEFT JOIN tbl_category cat ON c.category_id = cat.category_id " +
                     "ORDER BY c.coupon_id DESC";

        try {
            ResultSet rs = DBUtil.dbExecuteQuery(sql);

            while (rs.next()) {
                // [수정] 새로운 Coupon 모델의 빌더에 맞춰 객체 생성
                Coupon coupon = Coupon.builder()
                        .couponId(rs.getInt("coupon_id"))
                        .productId(rs.getInt("product_id"))
                        .categoryId(rs.getInt("category_id"))
                        .categoryName(rs.getString("category_name")) // [추가] 조회한 category_name을 모델에 매핑
                        .couponName(rs.getString("coupon_name"))
                        .percent(rs.getInt("percent"))
                        .startTime(rs.getDate("starttime").toLocalDate())
                        .deadLine(rs.getDate("deadline").toLocalDate())
                        .build();
                couponList.add(coupon);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("쿠폰 DB 조회 중 오류 발생");
            e.printStackTrace();
        }
        return couponList;
    }
}