package cai.mysqlgit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.mysql.jdbc.PreparedStatement;

import cai.mysqlgit.MySQLGitConfig;

public class MySQLUtils {
	private static Logger logger = Logger.getLogger(MySQLUtils.class);

	public static Connection getConnection() {
		String jdbcDriverClass = MySQLGitConfig.getValue("jdbc.driver");
		String url = MySQLGitConfig.getValue("jdbc.information_schema.url");
		String username = MySQLGitConfig.getValue("jdbc.username");
		String password = MySQLGitConfig.getValue("jdbc.password");
		Connection conn = null;
		try {
			Class.forName(jdbcDriverClass);
			conn = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	public static ArrayList<String> getDatabases() {
		Connection conn = MySQLUtils.getConnection();
		Statement stmt = null;
		ArrayList<String> databases = new ArrayList<String>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select distinct(table_schema) "
					+ " from information_schema.`TABLES` "
					+ "where table_schema <> 'information_schema' ");
			// ResultSet rs = stmt.executeQuery("select column_name,column_type, is_nullable, "
			// + "character_set_name, column_key, column_comment "
			// + " from columns where "
			// + "table_schema = 'souche_dfc' "
			// + "and table_name = 'app_splash_screen_inc'");
			while (rs.next()) {
				databases.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(databases);
		return databases;
	}

	public static String getTableSql(String dbName, String tableName) {
		Connection conn = MySQLUtils.getConnection();
		Statement stmt = null;
		String sqlStr = "SHOW CREATE TABLE `" + dbName + "`.`" + tableName + "`";
		String tableSqlStr = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStr);
			if (rs.next()) {
				tableSqlStr = rs.getString(2);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(sqlStr);
			e.printStackTrace();
		}
		return tableSqlStr;
	}

	public static ArrayList<String> getTablesByDatabaseName(String dbName) {
		ArrayList<String> tableList = new ArrayList<String>();
		Connection conn = MySQLUtils.getConnection();
		PreparedStatement stmt = null;
		String sqlStr = "select DISTINCT(TABLE_NAME) "
				+ "from information_schema.`COLUMNS` "
				+ "where TABLE_SCHEMA = ?";
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sqlStr);
			stmt.setString(1, dbName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tableList.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tableList;
	}
}
