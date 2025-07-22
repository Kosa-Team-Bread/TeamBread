package model.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBUtil;

public class AdminDAO {
	// 사용자 목록 전체 조회
	public ObservableList<Admin> getAllAdmins() throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM hr.tbl_admin";
		ResultSet rs = DBUtil.dbExecuteQuery(sql);

		ObservableList<Admin> adminList = FXCollections.observableArrayList();

		while (rs.next()) {
			Admin admin = Admin.builder().adminId(rs.getInt("ADMIN_ID")).pw(rs.getString("ADMIN_PW"))
					.adminName(rs.getString("ADMIN_NAME")).grade(rs.getInt("GRADE"))
					.adminRegDate(rs.getDate("REGDATE").toLocalDate()).adminModDate(rs.getDate("MODDATE").toLocalDate())
					.email((rs.getString("EMAIL"))).build();

			adminList.add(admin);
		}

		return adminList;
	}

	// 사용자 ID를 사용한 검색
	public Admin getAdminFromId(int id) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query = "SELECT * FROM TBL_ADMIN WHERE Admin_ID= ?";
		try {
			addList.add(id);
			ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
			Admin admin = getAdmin(rs);
			return admin;
		} catch (SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}
	}

	// 사용자 이름을 사용한 검색
	public Admin getAdminFromName(String name) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query = "SELECT * FROM TBL_ADMIN WHERE Admin_NAME= ?";
		try {
			addList.add(name);
			ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
			Admin admin = getAdmin(rs);
			return admin;
		} catch (SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}
	}

	// 사용자 데이터셋 받기
	public Admin getAdmin(ResultSet rs) throws SQLException, ClassNotFoundException {
		Admin admin = null;
		if (rs.next()) {
			admin = Admin.builder()
		            .adminId(rs.getInt("ADMIN_ID"))
		            .email(rs.getString("EMAIL"))
		            .pw(rs.getString("ADMIN_PW"))
		            .adminName(rs.getString("ADMIN_NAME"))
		            .grade(rs.getInt("GRADE"))
		            .adminRegDate(rs.getDate("REGDATE").toLocalDate())
		            .adminModDate(rs.getDate("MODDATE").toLocalDate())
		            .build();
		}
		return admin;
	}

	// 로그인 사용자 정보 변경
	public void updateAdmin(String adminName, String adminEmail, String pw, int adminId)	throws SQLException, ClassNotFoundException {
		String sql = "BEGIN\n" +
				 "	UPDATE tbl_admin\n" + 
				 "		SET ADMIN_NAME = ?, EMAIL = ? , ADMIN_PW = ?, MODDATE = SYSDATE\n" +
				 "			WHERE ADMIN_ID = ?;\n"+
				 "	COMMIT;\n" +
				 "END;";

		System.out.println(adminName + " " + adminEmail + " " + pw + " " + adminId);

		List<Object> params = new ArrayList<>();
		params.add(adminName);
		params.add(adminEmail);
		params.add(pw);
		params.add(adminId);

		DBUtil.dbExecuteUpdate(sql, params);
	}

	// 사용자 등급 변경
	public void updateAdminGrade(int newGrade, int adminId) throws SQLException, ClassNotFoundException {
		String sql = "BEGIN\n" + 
					 "	UPDATE tbl_admin\n" + 
					 "		SET GRADE = ?, MODDATE = SYSDATE\n"	+ 
					 "			WHERE ADMIN_ID = ?;\n" + 
					 "	COMMIT;\n" + 
					 "END;";

		List<Object> params = new ArrayList<>();
		params.add(newGrade);
		params.add(adminId);

		DBUtil.dbExecuteUpdate(sql, params);
	}

}