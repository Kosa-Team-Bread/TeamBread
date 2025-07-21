// CouponDAO.java

package model.coupon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBUtil; // DBUtil 클래스 임포트

import java.sql.ResultSet;
import java.sql.SQLException;

/** Coupon 데이터 접근 객체 (DAO) */
public class CouponDAO {

    /**
     * 모든 쿠폰 정보를 DB에서 조회 후 반환
     * DBUtil.dbExecuteQuery 메소드를 사용하여 데이터를 가져옴
     * @return 쿠폰 리스트 (ObservableList)
     */
    public ObservableList<Coupon> getAllCoupons() {
        ObservableList<Coupon> couponList = FXCollections.observableArrayList();
        // SQL 쿼리: 날짜 형식을 'YYYY-MM-DD HH24:MI'로 지정, 문자열로 가져옴
        String sql = "SELECT coupon_id, coupon_name, percent, TO_CHAR(starttime, 'YYYY-MM-DD HH24:MI') as starttime, TO_CHAR(deadline, 'YYYY-MM-DD HH24:MI') as deadline FROM tbl_coupon ORDER BY coupon_id DESC";

        try {
            // DBUtil을 통해 쿼리 실행, 결과를 ResultSet으로 받음
            // DBUtil 내부에서 연결/실행/연결해제 모두 처리
            ResultSet rs = DBUtil.dbExecuteQuery(sql);

            // ResultSet을 순회하며 Coupon 객체 생성, 리스트에 추가
            while (rs.next()) {
                Coupon coupon = new Coupon(
                        rs.getInt("coupon_id"),
                        rs.getString("coupon_name"),
                        rs.getInt("percent"),
                        rs.getString("starttime"),
                        rs.getString("deadline")
                );
                couponList.add(coupon); // 리스트에 추가
            }
        } catch (SQLException | ClassNotFoundException e) {
            // DBUtil.dbExecuteQuery가 던질 수 있는 예외 처리
            System.out.println("쿠폰 데이터베이스 조회 중 오류 발생");
            e.printStackTrace();
        }
        return couponList;
    }

    /*
     * 참고: 만약 쿠폰 '추가/수정/삭제' 기능이 필요하다면,
     * 아래와 같이 DBUtil.dbExecuteUpdate 메소드를 사용하면 됩니다.
     *
     * public void addCoupon(String name, int percent, String startTime, String deadline) throws SQLException, ClassNotFoundException {
     *     String sql = "INSERT INTO tbl_coupon (coupon_name, percent, starttime, deadline) VALUES ('" + name + "', " + percent + ", ...)";
     *     DBUtil.dbExecuteUpdate(sql);
     * }
     *
     * 또는 저장 프로시저나 PreparedStatement를 사용하는
     * dbExecuteUpdate(String sqlPstmt, List<Object> addList)를 사용할 수 있습니다.
     */
}