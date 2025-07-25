package model.inout;
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
import model.product.ProductDAO;
import model.stock.StockDAO;
import util.DBUtil;

// Made by 정영규
@AllArgsConstructor
public class InoutDAO {
	
		private final ProductDAO productDao;
		private final AdminDAO adminDao;
		private final CategoryDAO cateDao;
		private final StockDAO stockDao;
		
		public InoutDAO() {
			this.adminDao = new AdminDAO();
			this.cateDao = new CategoryDAO();
			this.productDao = new ProductDAO(cateDao,adminDao);
			this.stockDao = new StockDAO(cateDao);			
		}
	
		// 입출고 전체조회
		public ObservableList<inoutSelectDto> findAllInout() throws SQLException, ClassNotFoundException {
			String query = "SELECT * FROM TBL_STORAGERETRIEVAL ORDER BY inout_id DESC";
			try {
				ResultSet rs = DBUtil.dbExecuteQuery(query);
				ObservableList<inoutSelectDto> inoutList =  getInoutList(rs);
				return inoutList; 
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}

		}
		// 상품 리스트 받기
		public ObservableList<inoutSelectDto> getInoutList(ResultSet rs) throws SQLException, ClassNotFoundException {
			ObservableList<inoutSelectDto> inoutList = FXCollections.observableArrayList();
			
			try {
				// DB 연결을 한 번만 생성합니다.
				DBUtil.dbConnect();
				Connection sharedConn = DBUtil.getSharedConnection();	// 열린 커넥션을 가져옵니다.
				
				while (rs.next()) {
					String type = null;
					if(rs.getInt("INOUT_TYPE") == 1) type = "입고";
					else type = "출고";
					inoutSelectDto inout = inoutSelectDto.builder()
							.inoutId(rs.getInt("INOUT_ID"))
							.adminName(adminDao.getAdminFromId(rs.getInt("ADMIN_ID"),sharedConn).getAdminName())
							.categoryName(cateDao.getCategoryFromCategoryId(rs.getInt("CATEGORY_ID"),sharedConn).getCategoryName())
							.productName(productDao.getProductNameFromProductId(rs.getInt("PRODUCT_ID"),sharedConn))
							.typeName(type)
							.inoutQuantity(rs.getInt("INOUT_QUANTITY"))
							.inoutContent(rs.getString("INOUT_CONTENT"))
							.inoutRegDate(rs.getDate("INOUT_REGDATE").toLocalDate())
							.build();

					inoutList.add(inout);
				}
			}catch (SQLException e) {
		        System.out.println("입출고 리스트 데이터 처리 중 SQL 오류 발생: " + e);
		        throw e;
		    }finally {
		        // 모든 작업이 끝나면(성공하든 실패하든) DB 연결을 단 한 번만 해제합니다.
		        DBUtil.dbDisconnect(); 
		    }

			return inoutList;
		}
		
		// 입출고 ID으로 상세 조회
		public inoutSelectDto getInoutFromId(int id) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			String query = "SELECT * FROM TBL_STORAGERETRIEVAL WHERE INOUT_ID= ?" ;
			try {
				addList.add(id);
				ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
				inoutSelectDto inout = getInout(rs);
				return inout;
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}
		}
		
		// 입출고 이력 받기
		public inoutSelectDto getInout (ResultSet rs) throws SQLException, ClassNotFoundException {
			inoutSelectDto inout= null;
			if (rs.next()) {
				String type = null;
				if(rs.getInt("INOUT_TYPE") == 1) type = "입고";
				else type = "출고";
				inout = inoutSelectDto.builder()
						.inoutId(rs.getInt("INOUT_ID"))
						.adminName(adminDao.getAdminFromId(rs.getInt("ADMIN_ID")).getAdminName())
						.categoryName(cateDao.getCategoryFromCategoryId(rs.getInt("CATEGORY_ID")).getCategoryName())
						.productName(productDao.getProductFromProductId(rs.getInt("PRODUCT_ID")).getProductName())
						.typeName(type)
						.inoutQuantity(rs.getInt("INOUT_Quantity"))
						.inoutContent(rs.getString("INOUT_CONTENT"))
						.inoutRegDate(rs.getDate("INOUT_REGDATE").toLocalDate())
						.build();
			}
			return inout;
		}
	
		// 입출고 검색
		// 상품 검색 -> 대소문자 구분 X
		public ObservableList<inoutSelectDto> searchProduct(String search) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			String query = "SELECT * FROM TBL_STORAGERETRIEVAL WHERE product_id IN (SELECT product_id FROM TBL_PRODUCT WHERE LOWER(name) LIKE LOWER(?));";
			try {
				String result = "%" + search + "%";
				addList.add(result);
				ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
				ObservableList<inoutSelectDto> inoutList =  getInoutList(rs);
				return inoutList; 
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}
		}
	
	
		// 입출고 삽입, 재고현황 업데이트
		public void insertCategory(InoutInsertDto inout) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			String query = "{CALL proc_insert_inout(?, ?, ?, ?, ?, ?, ?)}";
			Integer type = 1;
			try {
				if(inout.getInoutType().equals("출고")) type=2;
				addList.add(stockDao.searchStock(inout.getProductName()).get(0).getStockId());
				addList.add(adminDao.getAdminFromName(inout.getAdminName()).getAdminId());
				addList.add(productDao.searchProduct(inout.getProductName()).get(0).getProductId());
				addList.add(cateDao.getCategory(inout.getCategoryName()).getCategoryId());
				addList.add(type);
				addList.add(inout.getInoutQuantity());
				addList.add(inout.getContent());
				DBUtil.dbExecuteCall(query, addList);	
			} catch (SQLException e) {
				System.out.print("Error occurred while UPDATE Operation: " + e);
				throw e;
			}
			
		}
}
