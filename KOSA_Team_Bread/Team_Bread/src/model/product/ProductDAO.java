package model.product;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import model.admin.AdminDAO;
import model.category.CategoryDAO;
import util.DBUtil;

// Made by 정영규
@AllArgsConstructor
public class ProductDAO {
	
	private static CategoryDAO cateDao;
	private static AdminDAO adminDao;
	
	// 상품 전체조회
	public ObservableList<Product> findAllProduct() throws SQLException, ClassNotFoundException {
		String query = "SELECT * FROM tbl_product";
		try {
			ResultSet rs = DBUtil.dbExecuteQuery(query);
			ObservableList<Product> productList =  getProductList(rs);
			return productList; 
		} catch(SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}

	}
	// 상품 리스트 받기
	public ObservableList<Product> getProductList(ResultSet rs) throws SQLException, ClassNotFoundException {
		ObservableList<Product> productList = FXCollections.observableArrayList();
		while (rs.next()) {
			Product product = Product.builder()
					.productId(rs.getInt("PRODUCT_ID"))
					.categoryId(rs.getInt("CATEGORY_ID"))
					.productName(rs.getString("NAME"))
					.price(rs.getInt("PRICE"))
					.cost(rs.getInt("COST"))
					.productRegDate(rs.getDate("PRODUCT_REGDATE").toLocalDate())
					.productModDate(rs.getDate("PRODUCT_MODDATE").toLocalDate())
					.build();

			productList.add(product);
		}
		return productList;
	}
	
	// 상품 ID으로 상세 조회
	public Product getProductFromProductId(int id) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query = "SELECT * FROM tbl_product WHERE PRODUCT_ID= ?" ;
		try {
			addList.add(id);
			ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
			Product product = getProduct(rs);
			return product;
		} catch(SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}
	}
	
	// 단일 상품 받기
		public Product getProduct(ResultSet rs) throws SQLException, ClassNotFoundException {
			Product product = null;
			if (rs.next()) {
				product = Product.builder()
						.productId(rs.getInt("PRODUCT_ID"))
						.categoryId(rs.getInt("CATEGORY_ID"))
						.productName(rs.getString("NAME"))
						.price(rs.getInt("PRICE"))
						.cost(rs.getInt("COST"))
						.productRegDate(rs.getDate("PRODUCT_REGDATE").toLocalDate())
						.productModDate(rs.getDate("PRODUCT_MODDATE").toLocalDate())
						.build();
			}
			return product;
		}
	
	// 상품 검색 -> 대소문자 구분 X
	public ObservableList<Product> searchProduct(String search) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query = "SELECT * FROM tbl_product WHERE LOWER(name) LIKE LOWER('?')";
		try {
			String result = "%" + search + "%";
			addList.add(result);
			ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
			ObservableList<Product> productList =  getProductList(rs);
			return productList; 
		} catch(SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}
	}
	
	// 카테고리 기준 검색
	public ObservableList<Product> getProductFromCategory(String cateName) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query = "SELECT * FROM tbl_product WHERE CATEGORY_ID= ?";
		try {
			addList.add(cateDao.getCategory(cateName).getCategoryId());
			ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
			ObservableList<Product> productList =  getProductList(rs);
			return productList; 
		} catch(SQLException e) {
			System.out.println("SQL 오류!!! 사유 : " + e);
			throw e;
		}
	}
	
	// 상품 삽입 <- 재고도 같이 삽입
	public static void insertProduct(ProductInsertDto product) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String sql = "{CALL proc_add_product_with_stock(?, ?, ?, ?, ?, ?, ?, ?)}";

		try {
			addList.add(cateDao.getCategory(product.getCategoryName()).getCategoryId());
			addList.add(product.getProductName());
			addList.add(product.getPrice());
			addList.add(product.getCost());
			addList.add(adminDao.getAdminFromName(product.getAdminName()).getAdminId());
			addList.add(product.getProductQuantity());
			addList.add(product.getProductName());
			addList.add(product.getStockLocation());
			DBUtil.dbExecuteCall(sql, addList);
		} catch (SQLException e) {
			System.out.print("Error occurred while UPDATE Operation: " + e);
			throw e;
		}
	}
	
	// 상품 수정
	public static void updateProduct(String newName, Integer newPrice, Integer newCost) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String updateStmt = "BEGIN\n" +
		                    "   UPDATE tbl_product\n" +
		                    "      SET name = ?,\n" +
		                    "		   price = ?,\n" +
		                    "		   cost = ?\n" +
		                    "    WHERE PRODUCT_ID = ?; \n" +
		                    "   COMMIT;\n" +
		                    "END;";
		try {
			addList.add(newName);
			addList.add(newPrice);
			addList.add(newCost);
			DBUtil.dbExecuteUpdate(updateStmt, addList);
		} catch (SQLException e) {
			System.out.print("Error occurred while UPDATE Operation: " + e);
			throw e;
		}
	}
	
	// 상품 삭제 <- 저장 프로시저 사용
	public void deleteProduct(String productName) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query = "{CALL proc_delete_product(?)}";
		try {
			addList.add(searchProduct(productName).get(0).getProductId());
			DBUtil.dbExecuteCall(query, addList);
		} catch (SQLException e) {
			System.out.print("삭제 실패!!! 사유 : " + e);
			throw e;
		}
	}
	
}