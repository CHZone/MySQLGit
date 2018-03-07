package cai.mysqlgit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.mysql.jdbc.PreparedStatement;

import cai.mysqlgit.MySQLGitConfig;
import cai.mysqlgit.entity.Column;
import cai.mysqlgit.entity.DataBase;
import cai.mysqlgit.entity.DataBaseVersion;
import cai.mysqlgit.entity.Table;

public class MySQLUtils {
	private static Logger logger = Logger.getLogger(MySQLUtils.class);

	private MySQLUtils() {
	}

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

	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<String> getCurrentDatabaseNames() {
		Connection conn = MySQLUtils.getConnection();
		Statement stmt = null;
		ArrayList<String> databases = new ArrayList<String>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select distinct(table_schema) "
					+ " from information_schema.`TABLES` "
					+ "where table_schema <> 'information_schema' ");
			while (rs.next()) {
				databases.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
		return databases;
	}

	public static ArrayList<String> getCurrentTableNames(String databaseName) {
		ArrayList<String> tableList = new ArrayList<String>();
		Connection conn = MySQLUtils.getConnection();
		PreparedStatement stmt = null;
		String sqlStr = "select DISTINCT(TABLE_NAME) "
				+ "from information_schema.`TABLES` "
				+ "where TABLE_SCHEMA = ?";
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sqlStr);
			stmt.setString(1, databaseName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tableList.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
		return tableList;
	}

	public static String getCurrentTableSql(String databaseName, String tableName) {
		Connection conn = MySQLUtils.getConnection();
		Statement stmt = null;
		String sqlStr = "SHOW CREATE TABLE `" + databaseName + "`.`" + tableName + "`";
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
		} finally {
			MySQLUtils.closeConnection(conn);
		}
		return tableSqlStr;
	}

	public static List<Column> getCurrentColmns(String databaseName, String tableName) {
		Connection conn = MySQLUtils.getConnection();
		PreparedStatement stmt = null;
		String sqlStr = "select COLUMN_NAME,COLUMN_TYPE,IS_NULLABLE,COLUMN_DEFAULT,COLUMN_KEY,COLUMN_COMMENT "
				+ "from `information_schema`.`COLUMNS`  "
				+ "where TABLE_SCHEMA = ? AND TABLE_NAME = ?";
		List<Column> columnList = new ArrayList<>();
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sqlStr);
			stmt.setString(1, databaseName);
			stmt.setString(2, tableName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String columnName = rs.getString(1);
				String columnType = rs.getString(2);
				String nullable = rs.getString(3);
				String colunmDefault = rs.getString(4);
				String columnKey = rs.getString(5);
				String columnComment = rs.getString(6);
				Column column = new Column(columnName, columnType, colunmDefault, nullable, columnKey, columnComment);
				columnList.add(column);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(sqlStr);
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
		return columnList;
	}

	public static Table getCurrentTable(String databaseName, String tableName) {
		List<Column> list = getCurrentColmns(databaseName, tableName);
		Table table = new Table(tableName);
		table.addColumns(list);
		return table;
	}

	public static DataBase getCurrentDatabase(String databaseName) {
		List<String> tableNameList = MySQLUtils.getCurrentTableNames(databaseName);
		DataBase database = new DataBase(databaseName);
		for (String tableName : tableNameList) {
			Table table = MySQLUtils.getCurrentTable(databaseName, tableName);
			database.addTable(table);
		}
		return database;
	}

	public static String getMaxVersionId() {
		Connection conn = MySQLUtils.getConnection();
		String versionId = null;
		try {
			String sqlStr = "SELECT count(*) "
					+ "FROM " + MySQLGitConfig.getValue("version.info.database")
					+ ".database_version_info";
			;
			PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sqlStr);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				versionId = rs.getLong(1) + "";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionId;
	}

	public static int saveDatabaseVersion(DataBaseVersion databaseVersion) {
		// 非线程安全
		// 位置也不合理
		databaseVersion.setId(MySQLUtils.getMaxVersionId());
		Connection conn = MySQLUtils.getConnection();
		PreparedStatement pstmt = null;
		int rows = -1;
		String sqlstr = "INSERT INTO "
				+ MySQLGitConfig.getValue("version.info.database")
				+ "."
				+ MySQLGitConfig.getValue("table.versionInfo")
				+ " (version_info, version_create_time, creator, creator_name) "
				+ "values( ? ,? ,? , ?)";
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sqlstr);
			pstmt.setString(1, databaseVersion.getVersionInfo());
			pstmt.setTimestamp(2, databaseVersion.getVersionCreateTime());
			pstmt.setString(3, databaseVersion.getCreator());
			pstmt.setString(4, databaseVersion.getCreatorName());
			rows = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
		return rows;
	}

	/**
	 * 添加参数
	 */
	public static DataBaseVersion createDatabaseVersion(String versionInfo) {
		DataBaseVersion dataBaseVersion = new DataBaseVersion(versionInfo,
				MySQLUtils.getCurrentTimeStamp(), "001", "one");
		return dataBaseVersion;
	}

	public static List<DataBaseVersion> getDatabaseVersions() {
		Connection conn = MySQLUtils.getConnection();
		List<DataBaseVersion> databaseVersionList = new ArrayList<>();
		try {
			Statement stmt = conn.createStatement();
			String sqlStr = "SELECT id, version_info, version_create_time, "
					+ "creator, creator_name "
					+ "from "
					+ MySQLGitConfig.getValue("version.info.database") + "."
					+ MySQLGitConfig.getValue("table.versionInfo");
			ResultSet rs = stmt.executeQuery(sqlStr);
			while (rs.next()) {
				String id = rs.getString(1) + "";
				String versionInfo = rs.getString(1);
				Timestamp versionCreateTime = rs.getTimestamp(3);
				String creator = rs.getString(4);
				String creatorName = rs.getString(5);
				DataBaseVersion dataBaseVersion = new DataBaseVersion(id, versionInfo, versionCreateTime, creator, creatorName);
				databaseVersionList.add(dataBaseVersion);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
		return databaseVersionList;
	}

	public static DataBaseVersion getLatestDatabaseVersion() {
		Connection conn = MySQLUtils.getConnection();
		String sqlStr = "SELECT id, version_info, version_create_time,"
				+ " creator, creator_name from "
				+ MySQLGitConfig.getValue("version.info.database") + "."
				+ MySQLGitConfig.getValue("table.versionInfo")
				+ " ORDER BY version_create_time DESC LIMIT 1 ;";
		DataBaseVersion dataBaseVersion = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStr);
			if (rs.next()) {
				String id = rs.getInt(1) + "";
				String versionInfo = rs.getString(2);
				Timestamp versionCreateTime = rs.getTimestamp(3);
				String creator = rs.getString(4);
				String creatorName = rs.getString(5);
				dataBaseVersion = new DataBaseVersion(id, versionInfo, versionCreateTime, creator, creatorName);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
		return dataBaseVersion;
	}

	public static int saveTableMateInfo(String databaseName, Table table, DataBaseVersion databaseVersion) {
		Connection conn = MySQLUtils.getConnection();
		PreparedStatement pstmt = null;
		String sqlStr = "INSERT INTO "
				+ MySQLGitConfig.getValue("version.info.database") + "."
				+ MySQLGitConfig.getValue("table.tablesHistory")
				+ " ( table_schema, table_name, version_id)"
				+ " values(?, ?,?);";
		int rows = -1;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sqlStr);
			pstmt.setString(1, databaseName);
			pstmt.setString(2, table.getName());
			pstmt.setString(3, databaseVersion.getId());
			rows = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
		return rows;
	}

	public static int saveColumn(String databaseName, String tableName, Column column,
			DataBaseVersion dataBaseVersion) {
		Connection conn = MySQLUtils.getConnection();
		PreparedStatement pstmt = null;
		int rows = -1;
		String sqlStr = "INSERT INTO "
				+ MySQLGitConfig.getValue("version.info.database") + "."
				+ MySQLGitConfig.getValue("table.columnsHistory")
				+ " ( table_schema, table_name, "
				+ "column_name, column_type, column_default, IS_NULLABLE, COLUMN_KEY,COLUMN_COMMENT "
				+ ",version_id)"
				+ "values(?, ?, "
				+ "?, ?, ?, ?, ?, ?, "
				+ "?)";
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sqlStr);
			// db and table info
			pstmt.setString(1, databaseName);
			pstmt.setString(2, tableName);
			// colum info
			pstmt.setString(3, column.getName());
			pstmt.setString(4, column.getColumnType());
			pstmt.setString(5, column.getColunmDefault());
			pstmt.setString(6, column.getNullable());
			pstmt.setString(7, column.getColumnKey());
			pstmt.setString(8, column.getColumnComment());
			// version
			pstmt.setString(9, dataBaseVersion.getId());
			rows = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
		return rows;
	}

	public static void saveTable(String databaseName, Table table, DataBaseVersion dataBaseVersion) {
		MySQLUtils.saveTableMateInfo(databaseName, table, dataBaseVersion);
		for (Column column : table.getColumnMap().values()) {
			MySQLUtils.saveColumn(databaseName, table.getName(), column, dataBaseVersion);
		}
	}

	public static Timestamp getCurrentTimeStamp() {
		Calendar c = Calendar.getInstance();
		return new Timestamp(c.getTimeInMillis());
	}
	
	public static void saveDatabase(DataBase database,DataBaseVersion dataBaseVersion) {
		
		Collection<Table> tables = database.getTableMap().values();
		String databaseName = database.getName();
		// save table 
		for(Table table:tables){
			MySQLUtils.saveTable(databaseName, table, dataBaseVersion);
		}
	}

	/**
	 * @param dataBaseVersion
	 * 获取指定版本的所有数据库名
	 */
	public static void getTablesNameByVersion(DataBaseVersion dataBaseVersion){
		
	}
	
	public static void getTableColumnsByVersion(String databaseName, String tableNamae, DataBaseVersion dataBaseVersion) {
	}

	public static void getTableByVersion(String databaseName, String tableName, DataBaseVersion dataBaseVersion) {
	}

	public static void getDatabaseByVersion(String databaseName, DataBaseVersion dataBaseVersion) {
	}
}
