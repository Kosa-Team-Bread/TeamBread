// CouponDAO.java

package model.coupon;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import model.category.CategoryDAO;
import model.product.ProductDAO;
import util.DBUtil;

@AllArgsConstructor
public class CouponDAO {
	private ProductDAO productDao;
	private CategoryDAO categoryDao;
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
   
    // 쿠폰 삽입
    public void insertCategory(Integer productId, Integer percent, LocalDate startTime, LocalDate DeadLine) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String couponName = productDao.getProductFromProductId(productId).getProductName() + " " + percent + "% 할인쿠폰";
		
		String query = "INSERT INTO tbl_coupon (coupon_id, product_id, category_id, coupon_name, percent, starttime, deadline) "
                + "VALUES (seq_coupon_id.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		try {
			addList.add(productId);
			addList.add(categoryDao.getCategory(productDao.getProductFromProductId(productId).getCategoryName()).getCategoryId());
			addList.add(couponName);
			addList.add(percent);
			addList.add(Date.valueOf(startTime));
			addList.add(Date.valueOf(DeadLine));
			DBUtil.dbExecuteUpdate(query, addList);
		} catch (SQLException e) {
			System.out.print("Error occurred while UPDATE Operation: " + e);
			throw e;
		}
	}
}
