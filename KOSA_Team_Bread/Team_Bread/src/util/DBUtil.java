package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.sun.rowset.CachedRowSetImpl;

public class DBUtil {

	// JDBC driver and connection string for Oracle DB
	private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String connStr = "jdbc:oracle:thin:hr/hr@localhost:1521/xepdb1";
	
	// Shared database connection instanced
	private static Connection conn = null;

	// Method to establish a database connection
	public static void dbConnect() throws SQLException, ClassNotFoundException {
		try {
			// Load Oracle JDBC driver
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			System.err.println("Oracle JDBC Driver를 찾을 수 없습니다: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
		System.out.println("Oracle JDBC Driver 등록 완료!");

		try {
			// Create database connection
			conn = DriverManager.getConnection(connStr);
			System.out.println("데이터베이스 연결 성공");
		} catch (SQLException e) {
			System.out.println("데이터베이스 연결 실패: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	// Method to close the database connection
	public static void dbDisconnect() throws SQLException {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				System.out.println("데이터베이스 연결 종료");
			}
		} catch (SQLException e) {
			System.out.println("데이터베이스 연결 종료 실패: " + e.getMessage());
			throw e;
		}
	}

	// Method to execute a SELECT query and return a CachedRowSet
	public static ResultSet dbExecuteQuery(String queryStmt) throws SQLException, ClassNotFoundException {
		Statement stmt = null;
		ResultSet resultSet = null;
		CachedRowSetImpl crs = null;

		try {
			// Connect to the database
			dbConnect();
			System.out.println("Select statement: " + queryStmt + "\n");

			// Execute the SELECT query
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery(queryStmt);

			// Populate and return a CachedRowSet with the results
			crs = new CachedRowSetImpl();
			crs.populate(resultSet);
		} catch (SQLException e) {
			System.out.println("Problem occurred at executeQuery operation : " + e);
			throw e;
		} finally {
			// Close resources and disconnect
			if (resultSet != null) {
				resultSet.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			dbDisconnect();
		}
		return crs;
	}
	
	// 파라미터 2개를 받아야 할때 
	public static ResultSet dbExecuteQuery(String sql, List<Object> params) throws SQLException, ClassNotFoundException {
		
		dbConnect();
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}
		}
		
		return pstmt.executeQuery();
	}
	// ^----------------------- 여기까지 -------------------------------^
	
	// Method to execute an INSERT, UPDATE, or DELETE statement
	public static void dbExecuteUpdate(String sqlStmt) throws SQLException, ClassNotFoundException {
		Statement stmt = null;

		try {
			// Connect to the database
			dbConnect();

			// Execute the update query
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlStmt);
		} catch (SQLException e) {
			System.out.println("Problem occurred at executeUpdate operation : " + e);
			throw e;
		} finally {
			// Close the statement and disconnect
			if (stmt != null) {
				stmt.close();
			}
			dbDisconnect();
		}
	}
	
	// 조건 검색
		public static ResultSet dbCaseExecuteQuery(String queryPstmt, List<Object> addList) throws SQLException, ClassNotFoundException {
			PreparedStatement pstmt = null;
			ResultSet resultSet = null;
			CachedRowSetImpl crs = null;

			try {
				// Connect to the database
				dbConnect();
				System.out.println("Select statement: " + queryPstmt+ "\n");

				// Execute the SELECT query
				pstmt = conn.prepareStatement(queryPstmt);
				// ?의 객체 삽입
				for (int i = 0; i < addList.size(); i++) pstmt.setObject(i + 1, addList.get(i));
				
				resultSet = pstmt.executeQuery();

				// Populate and return a CachedRowSet with the results
				crs = new CachedRowSetImpl();
				crs.populate(resultSet);
			} catch (SQLException e) {
				System.out.println("Problem occurred at executeQuery operation : " + e);
				throw e;
			} finally {
				// Close resources and disconnect
				if (resultSet != null) {
					resultSet.close();
				}
				if (pstmt  != null) {
					pstmt.close();
				}
				dbDisconnect();
			}
			return crs;
		}


		// 삽입, 삭제, 수정
		public static void dbExecuteUpdate(String sqlPstmt, List<Object> addList) throws SQLException, ClassNotFoundException {
			PreparedStatement pstmt = null;

			try {
				// Connect to the database
				dbConnect();

				// Execute the update query
				pstmt = conn.prepareStatement(sqlPstmt);
				// ?의 객체 삽입
				for (int i = 0; i < addList.size(); i++) pstmt.setObject(i + 1, addList.get(i));
				
				pstmt.executeUpdate(sqlPstmt);
			} catch (SQLException e) {
				System.out.println("Problem occurred at executeUpdate operation : " + e);
				throw e;
			} finally {
				// Close the statement and disconnect
				if (pstmt != null) {
					pstmt.close();
				}
				dbDisconnect();
			}
		}
}
