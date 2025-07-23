// CouponDAO.java

package model.coupon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.coupon.CouponDTO_Add;
import model.coupon.CouponDTO_Update;

public class CouponDAO {
    /**
     * 모든 쿠폰 정보를 DB에서 조회 후 반환
     * @return 쿠폰 리스트 (ObservableList)
     */
    public ObservableList<Coupon> getAllCoupons() {
        ObservableList<Coupon> couponList = FXCollections.observableArrayList();
        
        // tbl_category와 JOIN, category_name 함께 조회
        // LEFT JOIN 사용, 쿠폰에 연결된 카테고리가 없더라도 쿠폰 목록에서 누락되지 않도록 함
        String sql = "SELECT c.coupon_id, c.product_id, c.category_id, c.coupon_name, c.percent, c.starttime, c.deadline " +
                     "FROM tbl_coupon c " +
                     "LEFT JOIN tbl_category cat ON c.category_id = cat.category_id " +
                     "ORDER BY c.coupon_id ASC";

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

    
    /**
     * Stored Procedure 호출하여 새 쿠폰을 DB에 추가
     * @param couponDto 추가할 쿠폰 정보를 담은 DTO
     * @throws SQLException DB 접근 오류 or 프로시저 실행 오류 발생 시
     * @throws ClassNotFoundException DB 드라이버를 찾을 수 없을 때
     */
    public void addCoupon(CouponDTO_Add couponDto) throws SQLException, ClassNotFoundException {
        String sql = "{CALL proc_add_coupon(?, ?, ?, ?, ?, ?)}";
        List<Object> params = new ArrayList<>();
        params.add(couponDto.getProductId());
        params.add(couponDto.getCategoryId());
        params.add(couponDto.getCouponName());
        params.add(couponDto.getPercent());
        params.add(java.sql.Date.valueOf(couponDto.getStartTime()));
        params.add(java.sql.Date.valueOf(couponDto.getDeadline()));

        try {
            DBUtil.dbExecuteCall(sql, params);
        } catch (SQLException e) {
            System.err.println("쿠폰 추가_저장 프로시저 실행 중 오류 발생!");
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