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
        
        String sql = "SELECT coupon_id, product_id, category_id, coupon_name, percent, startTime, deadLine\n" + 
                        "FROM tbl_coupon\n" +
                        "ORDER BY coupon_id DESC\n";

        try {
            ResultSet rs = DBUtil.dbExecuteQuery(sql);

            while (rs.next()) {
                // 새로운 Coupon 모델의 빌더에 맞춰 객체 생성
                Coupon coupon = Coupon.builder()
                        .couponId(rs.getInt("coupon_id"))
                        .productId(rs.getInt("product_id"))
                        .categoryId(rs.getInt("category_id"))
                        .couponName(rs.getString("coupon_name"))
                        .percent(rs.getInt("percent"))
                        .startTime(rs.getDate("startTime").toLocalDate())
                        .deadLine(rs.getDate("deadLine").toLocalDate())
                        .build();

                couponList.add(coupon);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("쿠폰 데이터베이스 조회 중 오류 발생");
            e.printStackTrace();
        }
        return couponList;
    }
}
