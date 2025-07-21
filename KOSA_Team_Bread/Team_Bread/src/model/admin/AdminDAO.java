package model.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.inout.inoutSelectDto;
import model.product.Product;
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
	
	// 사용자 ID를 사용한 검색
	public Admin getAdminFromId(int id) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query = "SELECT * FROM TBL_ADMIN WHERE Admin_ID= ?" ;
		try {
			addList.add(id);
			ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
			Admin admin = getAdmin(rs);
			return admin;
		} catch(SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}
	}
	
	// 사용자 이름을 사용한 검색
	public Admin getAdminFromName(String name) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query = "SELECT * FROM TBL_ADMIN WHERE Admin_NAME= ?" ;
		try {
			addList.add(name);
			ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
			Admin admin = getAdmin(rs);
			return admin;
		} catch(SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}
	}

	// 사용자 데이터셋 받기
	public Admin getAdmin(ResultSet rs) throws SQLException, ClassNotFoundException {
		Admin admin = null;
		if (rs.next()) {
			admin = Admin.builder()
					.adminId(rs.getInt("PRODUCT_ID"))
					.email(rs.getString("EMAIL"))
					.pw(rs.getString("ADMIN_PW"))
					.adminName(rs.getString("ADMIN_NAME"))
					.grade(rs.getInt("GRADE"))
					.adminRegDate(rs.getDate("PRODUCT_REGDATE").toLocalDate())
					.adminModDate(rs.getDate("PRODUCT_MODDATE").toLocalDate())
					.build();
		}
		return admin;
	}
	
	private static LocalDate toLocalDate(java.sql.Date date) {
		return date != null ? date.toLocalDate() : null;
	}
}