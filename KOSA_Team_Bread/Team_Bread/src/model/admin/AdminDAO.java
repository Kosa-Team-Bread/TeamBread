package model.admin;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBUtil;

public class AdminDAO {
	// 사용자 목록 전체 조회
	public static ObservableList<Admin> getAllAdmins() throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM hr.tbl_admin";
		ResultSet rs = DBUtil.dbExecuteQuery(sql);

		ObservableList<Admin> adminList = FXCollections.observableArrayList();

		while (rs.next()) {
			Admin admin = Admin.builder().adminId(rs.getInt("ADMIN_ID")).pw(rs.getString("ADMIN_PW"))
					.adminName(rs.getString("ADMIN_NAME")).grade(rs.getInt("GRADE"))
					.adminRegDate(toLocalDate(rs.getDate("REGDATE"))).adminModDate(toLocalDate(rs.getDate("MODDATE")))
					.email((rs.getString("EMAIL"))).build();

			adminList.add(admin);
		}

		return adminList;
	}

	// 로그인 사용자 정보 변경
	public static void updateAdmin(String adminName, String adminEmail, String pw, int adminId)
			throws SQLException, ClassNotFoundException {
		String sql = "UPDATE hr.tbl_admin SET ADMIN_NAME = ?, EMAIL = ? , ADMIN_PW = ?, MODDATE = SYSDATE WHERE ADMIN_ID = ?";

		System.out.println(adminName + " " + adminEmail + " " + pw + " ");

		List<Object> params = new ArrayList<>();
		params.add(adminName);
		params.add(adminEmail);
		params.add(pw);
		params.add(adminId);

		DBUtil.dbExecuteUpdate(sql, params);
	}

	// 사용자 등급 변경
	public static void updateAdminGrade(int newGrade, int adminId) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE hr.tbl_admin SET GRADE = ?, MODDATE = SYSDATE WHERE ADMIN_ID = ?";

		List<Object> params = new ArrayList<>();
		params.add(newGrade);
		params.add(adminId);

		DBUtil.dbExecuteUpdate(sql, params);
	}

	private static LocalDate toLocalDate(Date date) {
		return date != null ? date.toLocalDate() : null;
	}
}