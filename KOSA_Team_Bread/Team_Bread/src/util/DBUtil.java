package util;

import java.sql.CallableStatement;
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

	// Shared database connection instance
	private static Connection conn = null;
	
	// InoutDAO에서 열어놓은 공유 Connection을 반환하는 메소드
	public static Connection getSharedConnection() {
		return conn;
	}

	// Method to establish a database connection
	public static void dbConnect() throws SQLException, ClassNotFoundException {
		try {
			// Load Oracle JDBC driver
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			throw e;
		}

		System.out.println("Oracle JDBC Driver Registered!");

		try {
			// Create database connection
			conn = DriverManager.getConnection(connStr);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console" + e);
			e.printStackTrace();
			throw e;
		}
	}

	// Method to close the database connection
	public static void dbDisconnect() throws SQLException {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (Exception e) {
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
	public static ResultSet dbCaseExecuteQuery(String queryPstmt, List<Object> addList)	throws SQLException, ClassNotFoundException {
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		CachedRowSetImpl crs = null;

		try {
			// Connect to the database
			dbConnect();
			System.out.println("Select statement: " + queryPstmt + "\n");

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
			if (pstmt != null) {
				pstmt.close();
			}
			dbDisconnect();
		}
		return crs;
	}
	
	// (조건 검색 오버로딩) 이미 열려있는 Connection을 받아서 쿼리만 실행하고 결과를 반환
	public static ResultSet dbCaseExecuteQuery(Connection conn, String queryPstmt, List<Object> addList)	throws SQLException, ClassNotFoundException {
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		CachedRowSetImpl crs = null;

		try {
			System.out.println("Select statement: " + queryPstmt + "\n");
			// 전달받은 conn을 사용하므로 dbConnect()를 호출하지 않음
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
			// ResultSet과 PreparedStatement는 닫아주지만 Connection은 닫지 않음 (dbDisconnect() 호출 안함)
			if (resultSet != null) {
				resultSet.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
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

			pstmt.executeUpdate();
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
	// Stored Procedure 사용
    public static void dbExecuteCall(String sqlCall, List<Object> paramList) throws SQLException, ClassNotFoundException {
		CallableStatement cstmt = null;

		try {
			dbConnect();

			cstmt = conn.prepareCall(sqlCall);

			for (int i = 0; i < paramList.size(); i++) cstmt.setObject(i + 1, paramList.get(i));


			cstmt.execute();

		} catch (SQLException e) {
			System.out.println("Problem occurred at executeCall operation : " + e);
			throw e;
		} finally {
			if (cstmt != null) cstmt.close();
			dbDisconnect();
		}
	}

    
}

