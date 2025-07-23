package model.product;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import model.admin.AdminDAO;
import model.category.CategoryDAO;
import model.image.Image;
import model.image.ImageDAO;
import util.DBUtil;



// Made by 정영규
@AllArgsConstructor
public class ProductDAO {
	
	private static CategoryDAO cateDao;
	private static AdminDAO adminDao;
	private static ImageDAO imageDao;
	
	// ProductDAO.java (맨 위 import 아래 아무데나)
	public static void setCategoryDAO(CategoryDAO dao){ ProductDAO.cateDao = dao; }
	public static void setAdminDAO(AdminDAO dao){ ProductDAO.adminDao = dao; }
	public ProductDAO(CategoryDAO cateDao, AdminDAO adminDao) {
        ProductDAO.cateDao  = cateDao;
        ProductDAO.adminDao = adminDao;
    }

	public static void setImageDAO(ImageDAO dao) {
	    ProductDAO.imageDao = dao;
	}

	
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

	public ProductDetailSelectDto getProductFromProductId(int id) throws SQLException, ClassNotFoundException {
	    // 1) tbl_product 조회
	    List<Object> params = new ArrayList<>();
	    params.add(id);
	    String sql = "SELECT * FROM tbl_product WHERE PRODUCT_ID = ?";
	    ResultSet rs = DBUtil.dbCaseExecuteQuery(sql, params);

	    // 2) Product 객체 빌드
	    Product product = getProduct(rs);
	    if (product == null) {
	        return null; // 해당 ID의 상품이 없으면 null 반환
	    }

	    // 3) 카테고리명 조회
	    String categoryName = cateDao
	        .getCategoryFromCategoryId(product.getCategoryId())
	        .getCategoryName();

	    // 4) 이미지 위치 조회 (없으면 빈 문자열)
	    String imageLocation = "";
	    if (imageDao != null) {
	        try {
	            // ImageDAO#searchProduct가 ObservableList<Image> 반환한다고 가정
	            Image image = imageDao.searchProduct(product.getProductName());
	            if (image != null) {
	                imageLocation = image.getImageLocation();
	            }
	        } catch (Exception ignore) {
	            // 이미지가 없거나 조회 중 에러 나면 그냥 빈 문자열
	        }
	    }

	    // 5) DTO 빌드
	    return ProductDetailSelectDto.builder()
	            .productName(product.getProductName())
	            .categoryName(categoryName)
	            .price(product.getPrice())
	            .cost(product.getCost())
	            .productModDate(product.getProductModDate())
	            .imageLocation(imageLocation)
	            .build();
	}
	
	// 상품 ID으로 상품 이름 조회

	public String getProductNameFromProductId(int id, Connection conn) throws SQLException, ClassNotFoundException {
	    // 1) tbl_product 조회
	    List<Object> params = new ArrayList<>();
	    params.add(id);
	    String sql = "SELECT * FROM tbl_product WHERE PRODUCT_ID = ?";
	    ResultSet rs = DBUtil.dbCaseExecuteQuery(conn, sql, params);

	    // 2) Product 객체 빌드
	    Product product = getProduct(rs);
	    if (product == null) {
	        return null; // 해당 ID의 상품이 없으면 null 반환
	    }

	    // 5) DTO 빌드
	    return product.getProductName();
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
		String query = "SELECT * FROM tbl_product WHERE LOWER(name) LIKE LOWER(?)";
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
		String query = "{CALL proc_add_product_with_stock_and_image("
	               + "?, ?, ?, ?, ?, ?, ?, ?, ?)}";

		try {
			addList.add(cateDao.getCategory(product.getCategoryName()).getCategoryId());
			addList.add(product.getProductName());
			addList.add(product.getPrice());
			addList.add(product.getCost());
			addList.add(adminDao.getAdminFromName(product.getAdminName()).getAdminId());
			addList.add(product.getProductQuantity());
			addList.add(product.getProductName());
			addList.add(product.getStockLocation());
			addList.add(product.getImageLocation());
			DBUtil.dbExecuteCall(query, addList);
		} catch (SQLException e) {
			System.out.print("Error occurred while UPDATE Operation: " + e);
			throw e;
		}
	}
	
	// 상품 수정
	public static void updateProduct(Integer id, Integer newPrice, Integer newCost) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String updateStmt = "BEGIN\n" +
		                    "   UPDATE tbl_product\n" +
		                    "      SET price = ?,\n" +
		                    "		   cost = ?\n" +
		                    "    WHERE PRODUCT_ID = ?; \n" +
		                    "   COMMIT;\n" +
		                    "END;";
		try {
			addList.add(newPrice);
			addList.add(newCost);
			addList.add(id);
			DBUtil.dbExecuteUpdate(updateStmt, addList);
		} catch (SQLException e) {
			System.out.print("Error occurred while UPDATE Operation: " + e);
			throw e;
		}
	}
	
	// 상품 삭제 <- 저장 프로시저 사용
	public void deleteProduct(int productId) throws SQLException, ClassNotFoundException {
		List<Object> addList = new ArrayList<>();
		String query = "{CALL proc_delete_product(?)}";
		try {
			addList.add(productId);
			DBUtil.dbExecuteCall(query, addList);
		} catch (SQLException e) {
			System.out.print("삭제 실패!!! 사유 : " + e);
			throw e;
		}
	}
	
}