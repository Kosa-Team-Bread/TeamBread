// DBUtil.java

package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.rowset.CachedRowSetImpl;

public class DBUtil {

	// JDBC driver and connection string for Oracle DB
	private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String connStr = "jdbc:oracle:thin:yb/yb@localhost:1521/xepdb1";
	
	// Shared database connection instance
	private static Connection conn = null;

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
}
