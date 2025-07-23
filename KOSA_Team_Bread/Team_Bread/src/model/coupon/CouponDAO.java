// CouponDAO.java

package model.coupon;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

// DTO 클래스 임포트
import model.coupon.CouponDTO_Add;
import model.coupon.CouponDTO_Update;

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
        
        // tbl_coupon 테이블의 데이터만 조회
        String sql = "SELECT coupon_id, product_id, category_id, coupon_name, percent, starttime, deadline " +
                     "FROM tbl_coupon " +
                     "ORDER BY coupon_id ASC";

        try {
            ResultSet rs = DBUtil.dbExecuteQuery(sql);

            while (rs.next()) {
                // 새로운 Coupon 모델의 빌더에 맞춰 객체 생성 (categoryName 빌드 부분 없음)
                Coupon coupon = Coupon.builder()
                        .couponId(rs.getInt("coupon_id"))
                        .productId(rs.getInt("product_id"))
                        .categoryId(rs.getInt("category_id"))
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

    /**
     * Stored Procedure 호출하여 기존 쿠폰 정보 수정
     * @param couponDto 수정할 쿠폰 정보를 담은 DTO
     * @throws SQLException DB 접근 오류 or 프로시저 실행 오류 발생 시
     * @throws ClassNotFoundException DB 드라이버를 찾을 수 없을 때
     */
    public void updateCoupon(CouponDTO_Update couponDto) throws SQLException, ClassNotFoundException {
        String sql = "{CALL proc_update_coupon(?, ?, ?, ?, ?, ?, ?)}";
        List<Object> params = new ArrayList<>();
        params.add(couponDto.getCouponId());
        params.add(couponDto.getProductId());
        params.add(couponDto.getCategoryId());
        params.add(couponDto.getCouponName());
        params.add(couponDto.getPercent());
        params.add(java.sql.Date.valueOf(couponDto.getStartTime()));
        params.add(java.sql.Date.valueOf(couponDto.getDeadline()));

        try {
            DBUtil.dbExecuteCall(sql, params);
        } catch (SQLException e) {
            System.err.println("쿠폰 수정_저장 프로시저 실행 중 오류 발생!");
            throw e;
        }
    }

    /**
     * Stored Procedure 호출하여 쿠폰 삭제
     * @param couponId 삭제할 쿠폰의 ID
     * @throws SQLException DB 접근 오류 or 프로시저 실행 오류 발생 시
     * @throws ClassNotFoundException DB 드라이버를 찾을 수 없을 때
     */
    public void deleteCoupon(int couponId) throws SQLException, ClassNotFoundException {
        String sql = "{CALL proc_delete_coupon(?)}";
        List<Object> params = new ArrayList<>();
        params.add(couponId);

        try {
            DBUtil.dbExecuteCall(sql, params);
        } catch (SQLException e) {
            System.err.println("쿠폰 삭제_저장 프로시저 실행 중 오류 발생!");
            throw e;
        }
    }
}