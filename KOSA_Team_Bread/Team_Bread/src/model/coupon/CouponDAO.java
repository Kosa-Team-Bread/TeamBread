package model.coupon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBUtil; // 팀원이 만든 DBUtil 클래스 임포트

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Coupon 데이터 접근 객체 (DAO)
 * 팀원의 DBUtil 구조에 맞게 수정되었습니다.
 * 작성자: 강기범, 김기성, 나규태, 정영규
 */
public class CouponDAO {

    /**
     * 모든 쿠폰 정보를 데이터베이스에서 조회하여 반환합니다.
     * DBUtil.dbExecuteQuery 메소드를 사용하여 데이터를 가져옵니다.
     * @return 쿠폰 리스트 (ObservableList)
     */
    public ObservableList<Coupon> getAllCoupons() {
        ObservableList<Coupon> couponList = FXCollections.observableArrayList();
        // SQL 쿼리: 날짜 형식을 'YYYY-MM-DD HH24:MI'로 지정하여 문자열로 가져옵니다.
        String sql = "SELECT coupon_id, coupon_name, percent, TO_CHAR(starttime, 'YYYY-MM-DD HH24:MI') as starttime, TO_CHAR(deadline, 'YYYY-MM-DD HH24:MI') as deadline FROM tbl_coupon ORDER BY coupon_id DESC";

        try {
            // DBUtil을 통해 쿼리를 실행하고, 결과를 ResultSet으로 받습니다.
            // DBUtil 내부에서 연결, 실행, 연결 해제가 모두 처리됩니다.
            ResultSet rs = DBUtil.dbExecuteQuery(sql);

            // ResultSet을 순회하며 Coupon 객체를 생성하고 리스트에 추가합니다.
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
            // DBUtil.dbExecuteQuery가 던질 수 있는 예외를 처리합니다.
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