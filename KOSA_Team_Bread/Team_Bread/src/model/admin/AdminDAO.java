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

	// 이메일 중복 확인

	public boolean checkEmailDuplicate(String email) throws SQLException, ClassNotFoundException {
		String sql = "SELECT COUNT(*) as count  FROM tbl_admin WHERE email = ?";
		List<Object> params = new ArrayList<>();
		params.add(email);

		ResultSet rs = DBUtil.dbExecuteQuery(sql, params);

		if (rs.next()) {
			int count = rs.getInt("count");
			return count > 0;
		}

		return false;
	}

	// 회원가입 sp 사용시
	public boolean signupAdmin(Admin admin) throws SQLException, ClassNotFoundException {
		String sql = "{CALL sp_insert_admin(?, ?, ?)}";
		List<Object> params = new ArrayList<>();
		params.add(admin.getAdminName());
		params.add(admin.getEmail());
		params.add(admin.getPw());

		try {
			DBUtil.dbExecuteCall(sql, params);
			return true;
		} catch (SQLException e) {
			System.err.println("회원가입 실패: " + e.getMessage());
			return signupAdminWithoutProcedure(admin);
		}
	}

	// 회원가입 sp 사용 안할시
	public boolean signupAdminWithoutProcedure(Admin admin) throws SQLException, ClassNotFoundException {
		String sql = "INSERT INTO tbl_admin (admin_id, admin_pw, admin_name, grade, regDate, modDate, email) "
				+ "VALUES (seq_admin_id.NEXTVAL, ?, ?, 2, SYSDATE, SYSDATE, ?)";
		List<Object> params = new ArrayList<>();
		params.add(admin.getPw());
		params.add(admin.getAdminName());
		params.add(admin.getEmail());

		try {
			DBUtil.dbExecuteUpdate(sql, params);
			return true;

		} catch (SQLException e) {
			System.err.println("회원가입 실패: " + e.getMessage());
			throw e;
		}
	}

	// 로그인 인증
	public Admin loginAdmin(String email, String password) throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM tbl_admin WHERE email = ? AND admin_pw = ?";
		List<Object> params = new ArrayList<>();
		params.add(email);
		params.add(password);

		ResultSet rs = null;

		try {
			rs = DBUtil.dbExecuteQuery(sql, params);
			if (rs.next()) {
				Admin admin = Admin.builder().adminId(rs.getInt("admin_id")).adminName(rs.getString("admin_name"))
						.email(rs.getString("email")).grade(rs.getInt("grade"))
						.adminRegDate(rs.getDate("regDate").toLocalDate())
						.adminModDate(rs.getDate("modDate").toLocalDate()).build();

				System.out.println("로그인 성공: " + admin.getAdminName());
				return admin;
			}
			System.out.println("로그인 실패: 일치하는 사용자 정보가 없습니다.");
	        return null; // 로그인 실패

		} catch (SQLException e) {
			System.err.println("데이터베이스 오류 발생: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC 드라이버를 찾을 수 없습니다: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("예상치 못한 오류 발생: " + e.getMessage());
			e.printStackTrace();
			
			throw new SQLException("로그인 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
		} finally {
			
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
			admin = Admin.builder().adminId(rs.getInt("ADMIN_ID")).email(rs.getString("EMAIL"))
					.pw(rs.getString("ADMIN_PW")).adminName(rs.getString("ADMIN_NAME")).grade(rs.getInt("GRADE"))
					.adminRegDate(rs.getDate("REGDATE").toLocalDate()).adminModDate(rs.getDate("MODDATE").toLocalDate())
					.build();
		}
		return admin;
	}

	// 로그인 사용자 정보 변경
	public void updateAdmin(String adminName, String adminEmail, String pw, int adminId)
			throws SQLException, ClassNotFoundException {
		String sql = "BEGIN\n" + "	UPDATE tbl_admin\n"
				+ "		SET ADMIN_NAME = ?, EMAIL = ? , ADMIN_PW = ?, MODDATE = SYSDATE\n"
				+ "			WHERE ADMIN_ID = ?;\n" + "	COMMIT;\n" + "END;";

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
		String sql = "BEGIN\n" + "	UPDATE tbl_admin\n" + "		SET GRADE = ?, MODDATE = SYSDATE\n"
				+ "			WHERE ADMIN_ID = ?;\n" + "	COMMIT;\n" + "END;";

		List<Object> params = new ArrayList<>();
		params.add(newGrade);
		params.add(adminId);

		DBUtil.dbExecuteUpdate(sql, params);
	}
}