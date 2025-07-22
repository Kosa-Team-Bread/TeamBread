package model.admin;

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
			Admin admin = Admin.builder().adminId(rs.getInt(1)).pw(rs.getString(2)).adminName(rs.getString(3))
					.grade(rs.getInt(4)).adminRegDate(toLocalDate(rs.getDate(5)))
					.adminModDate(toLocalDate(rs.getDate(6))).email((rs.getString(7))).build();

			adminList.add(admin);
		}

		return adminList;
	}

	private static LocalDate toLocalDate(java.sql.Date date) {
		return date != null ? date.toLocalDate() : null;
	}
	
	// 이메일 중복 확인
	
	public boolean checkEmailDuplicate(String email) throws SQLException, ClassNotFoundException {
		String sql = "SELECT COUNT(*) as count  FROM tbl_admin WHERE email = ?";
		List<Object> params = new ArrayList<>();
		params.add(email);
		
		ResultSet rs = DBUtil.dbExecuteQuery(sql, params);
		
		if (rs.next()) {
			int count  = rs.getInt("count");
			return count > 0;
		}
		
		return false;
	}
}