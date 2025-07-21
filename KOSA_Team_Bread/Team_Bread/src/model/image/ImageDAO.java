package model.image;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import model.category.CategoryDAO;
import model.inout.inoutSelectDto;
import model.product.Product;
import util.DBUtil;

// Made by 정영규
@AllArgsConstructor
public class ImageDAO {
	
	private final CategoryDAO cateDao;
	
	// 이미지 전체조회
		public ObservableList<Image> findAllImage() throws SQLException, ClassNotFoundException {
			String query = "SELECT * FROM tbl_iamge";
			try {
				ResultSet rs = DBUtil.dbExecuteQuery(query);
				ObservableList<Image> imageList =  getImageList(rs);
				return imageList; 
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}

		}
		// 상품 리스트 받기
		public ObservableList<Image> getImageList(ResultSet rs) throws SQLException, ClassNotFoundException {
			ObservableList<Image> imageList = FXCollections.observableArrayList();
			while (rs.next()) {
				Image image = Image.builder()
						.imageId(rs.getInt("IMAGE_ID"))
						.productId(rs.getInt("PRODUCT_ID"))
						.categoryId(rs.getInt("CATEGORY_ID"))
						.imageName(rs.getString("IMAGE_NAME"))
						.imageLocation(rs.getString("IMAGE_LOCATION"))
						.stockRegDate(rs.getDate("IMAGE_REGDATE").toLocalDate())
						.stockModDate(rs.getDate("IMAGE_MODDATE").toLocalDate())
						.build();

				imageList.add(image);
			}
			return imageList;
		}
		//상품 이미지 조회
		public ObservableList<Image> searchProduct(String search) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			String query = "SELECT * FROM tbl_image WHERE image_name = ?";
			try {
				String result = search;
				addList.add(result);
				ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
				ObservableList<Image> imageList =  getImageList(rs);
				return imageList; 
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}
		}
		
	
	// 이미지 삽입
		public void insertCategory(ImageDto image) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			
			String insertStmt = 
				    "BEGIN\n" +
				    "   INSERT INTO tbl_image\n" +
				    "   (IMAGE_ID, PRODUCT_ID, CATEGORY_ID, IMAGE_NAME, IMAGE_LOCATION, IMAGE_REGDATE, IMAGE_MODDATE)\n" +
				    "   VALUES (seq_tbl_image.nextval, ?, ?, ?, ?, ?, ?);\n" +
				    "END;";
			try {;
				addList.add(cateDao.getCategory(image.categoryName).getCategoryId());
				addList.add(image.getProductId());
				addList.add(image.getImageName());
				addList.add(image.getLocation());
				addList.add(java.sql.Date.valueOf(LocalDate.now()));
				addList.add(java.sql.Date.valueOf(LocalDate.now()));
				
				DBUtil.dbExecuteUpdate(insertStmt, addList);	
			} catch (SQLException e) {
				System.out.print("Error occurred while UPDATE Operation: " + e);
				throw e;
			}
			
		}
	
	// 이미지 삭제
		public void deleteImage(Integer imageId) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			String query =  "BEGIN\n" +
			                "   DELETE FROM tbl_image\n" +
			                "         WHERE Image_ID = ?;\n" +
			                "   COMMIT;\n" +
			                "END;";
			try {
				addList.add(imageId);
				DBUtil.dbExecuteUpdate(query, addList);
			} catch (SQLException e) {
				System.out.print("삭제 실패!!! 사유 : " + e);
				throw e;
			}
		}
}