package model.stock;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import model.category.CategoryDAO;
import util.DBUtil;

// Made by 정영규
@AllArgsConstructor
public class StockDAO {
	
		private final CategoryDAO cateDao;

		// 재고 전체조회
		public ObservableList<Stock> findAllStock() throws SQLException, ClassNotFoundException {
			String query = "SELECT * FROM tbl_stock";
			try {
				ResultSet rs = DBUtil.dbExecuteQuery(query);
				ObservableList<Stock> stockList =  getStockList(rs);
				return stockList; 
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}

		}
		// 재고 리스트 받기
		public ObservableList<Stock> getStockList(ResultSet rs) throws SQLException, ClassNotFoundException {
			ObservableList<Stock> stockList = FXCollections.observableArrayList();
			while (rs.next()) {
				Stock stock = Stock.builder()
						.stockId(rs.getInt("STOCK_ID"))
						.adminId(rs.getInt("ADMIN_ID"))
						.productId(rs.getInt("PRODUCT_ID"))
						.categoryId(rs.getInt("CATEGORY_ID"))
						.stockName(rs.getString("STOCK_NAME"))
						.stockQuantity(rs.getInt("QUANTITY"))
						.location(rs.getString("LOCATION"))
						.stockRegDate(rs.getDate("STOCK_REGDATE").toLocalDate())
						.stockModDate(rs.getDate("STOCK_MODDATE").toLocalDate())
						.build();

				stockList.add(stock);
			}
			return stockList;
		}
		
		// 재고 ID으로 상세 조회
		public Stock getStockFromProductId(int id) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			String query = "SELECT * FROM tbl_stock WHERE STOCK_ID= ?" ;
			try {
				addList.add(id);
				ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
				Stock stock = getStock(rs);
				return stock;
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}
		}
		
		// 재고 상품 받기
			public Stock getStock (ResultSet rs) throws SQLException, ClassNotFoundException {
				Stock stock = null;
				if (rs.next()) {
					stock = Stock.builder()
							.stockId(rs.getInt("STOCK_ID"))
							.adminId(rs.getInt("ADMIN_ID"))
							.productId(rs.getInt("PRODUCT_ID"))
							.categoryId(rs.getInt("CATEGORY_ID"))
							.stockName(rs.getString("STOCK_NAME"))
							.stockQuantity(rs.getInt("QUANTITY"))
							.location(rs.getString("LOCATION"))
							.stockRegDate(rs.getDate("STOCK_REGDATE").toLocalDate())
							.stockModDate(rs.getDate("STOCK_MODDATE").toLocalDate())
							.build();
				}
				return stock;
			}
		
		// 재고 검색 -> 대소문자 구분 X
		public ObservableList<Stock> searchStock (String search) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			String query = "SELECT * FROM tbl_stock WHERE LOWER(STOCK_NAME) LIKE LOWER(?)";
			try {
				String result = "%" + search + "%";
				addList.add(result);
				ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
				ObservableList<Stock> stockList =  getStockList(rs);
				return stockList; 
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}
		}
		
		// 카테고리 기준 검색
		public ObservableList<Stock> getStockFromCategory(String cateName) throws SQLException, ClassNotFoundException {
			List<Object> addList = new ArrayList<>();
			String query = "SELECT * FROM tbl_stock WHERE CATEGORY_ID= ?";
			try {
				addList.add(cateDao.getCategory(cateName).getCategoryId());
				ResultSet rs = DBUtil.dbCaseExecuteQuery(query, addList);
				ObservableList<Stock> stockList = getStockList(rs);
				return stockList; 
			} catch(SQLException e) {
				System.out.println("SQL 오류!!! 사유 : " + e);
				throw e;
			}
		}
	
	// 삭제는 저장 프로시저가
	
	// 수정은 입출고 테이블에서
	
	// 삽입은 저장 프로시저가
}