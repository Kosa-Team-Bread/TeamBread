package model.category;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBUtil;

// Made by 정영규
public class CategoryDAO {

	
	// 카테고리 전체조회 
	public ObservableList<Category> findAllCategory() throws SQLException, ClassNotFoundException {
		String query = "SELECT * FROM tbl_category";
		try {
			ResultSet rs = DBUtil.dbExecuteQuery(query);
			ObservableList<Category> categoryList =  getCategoryList(rs);
			return categoryList; 
		} catch(SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}

	}
	
	// 카테고리 리스트 받기
	public ObservableList<Category> getCategoryList(ResultSet rs) throws SQLException, ClassNotFoundException {
		ObservableList<Category> categoryList = FXCollections.observableArrayList();
		while (rs.next()) {
			Category category = Category.builder()
				.categoryId(rs.getInt("CATEGORY_ID"))
				.parentId(rs.getInt("CATEGORY_PARENTID"))
				.categoryName(rs.getString("CATEGORY_NAME"))
				.level(rs.getInt("CATEGORY_LEVEL"))
				.build();
			categoryList.add(category);
		}
		return categoryList;
	}
	
	// 카테고리 이름으로 상세 조회
	public Category getCategory(String cateName) throws SQLException, ClassNotFoundException {
		String query = "SELECT * FROM tbl_category WHERE CATEGORY_NAME=" + cateName;
		try {
			ResultSet rs = DBUtil.dbExecuteQuery(query);
			Category category = getCategory(rs);
			return category;
		} catch(SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}
	}
	
	// 카테고리 ID으로 상세 조회
		public Category getCategoryFromCategoryId(int id) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			String query = "SELECT * FROM tbl_category WHERE CATEGORY_ID= ?" ;
			try {
				addList.add(id);
				ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
				Category category = getCategory(rs);
				return category;
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}
		}
	
	// 단일 카테고리 받기
	public Category getCategory(ResultSet rs) throws SQLException, ClassNotFoundException {
		Category category = null;
		if (rs.next()) {
				category = Category.builder()
				.categoryId(rs.getInt("CATEGORY_ID"))
				.parentId(rs.getInt("CATEGORY_PARENTID"))
				.categoryName(rs.getString("CATEGORY_NAME"))
				.level(rs.getInt("CATEGORY_LEVEL"))
				.build();
		}
		return category;
	}
	
	// 카테고리 삽입
	public void insertCategory(Category category, String cateName) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		int level = 1;
		Integer id = getCategory(cateName).getCategoryId();
		
		String updateStmt = "BEGIN\n" +
			                "   INSERT INTO tbl_category\n" +
			                "   (CATEGORY_ID, CATEGORY_PARENTID, CATEGORY_NAME, CATEGORY_LEVEL)\n" +
			                "   VALUES (sequence_employee.nextval, ?, ?, ?, ?);\n" +
			                "END;";
		try {
			// addList.add(category.getCategoryId());
			
			if(cateName != null || cateName == "없음") addList.add(id);
			else addList.add(null);
			
			addList.add(category.getCategoryName());
			
	
			while(true) {
				if(id != null) {
					level++;
					Category levelcate =getCategoryFromCategoryId(id);
					id = levelcate.getParentId();
				}
				else break;
			}
			addList.add(level);
			DBUtil.dbExecuteUpdate(updateStmt, addList);	
		} catch (SQLException e) {
			System.out.print("Error occurred while UPDATE Operation: " + e);
			throw e;
		}
		
	}
	
	// 카테고리 이름 수정
	public static void updateCategoryName(String cateName, String newCateName) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String updateStmt = "BEGIN\n" +
		                    "   UPDATE tbl_category\n" +
		                    "      SET CATEGORY_NAME = ? \n" +
		                    "    WHERE CATEGORY_ID = ?; \n" +
		                    "   COMMIT;\n" +
		                    "END;";
		try {
			addList.add(cateName);
			addList.add(newCateName);
			DBUtil.dbExecuteUpdate(updateStmt, addList);
		} catch (SQLException e) {
			System.out.print("Error occurred while UPDATE Operation: " + e);
			throw e;
		}
	}
	
	// 카테고리 삭제
	public void deleteCategory(String cateName) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query =  "BEGIN\n" +
		                "   DELETE FROM tbl_category\n" +
		                "         WHERE CATEGORY_NAME = ?;\n" +
		                "   COMMIT;\n" +
		                "END;";
		try {
			addList.add(cateName);
			DBUtil.dbExecuteUpdate(query, addList);
		} catch (SQLException e) {
			System.out.print("삭제 실패!!! 사유 : " + e);
			throw e;
		}
	}
	
	
}
