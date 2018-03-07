package cai.mysqlgit.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.mysql.jdbc.PreparedStatement;

import cai.mysqlgit.MySQLGitConfig;
import cai.mysqlgit.entity.Column;
import cai.mysqlgit.entity.DataBase;
import cai.mysqlgit.entity.DataBaseVersion;
import cai.mysqlgit.entity.Table;

public class TestMySQLUtils {
	@Test
	public void test_getCurrentDatabaseNames() {
		ArrayList<String> databaseNameList = MySQLUtils.getCurrentDatabaseNames();
		Connection conn = MySQLUtils.getConnection();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(DISTINCT(table_schema)) "
					+ "from information_schema.`TABLES`");
			rs.next();
			// 排除 数据库information_schema
			int dbs = rs.getInt(1) - 1;
			assertEquals(databaseNameList.size(), dbs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
	}

	@Test
	public void test_getCurrentTableNames() {
		String dbName = MySQLGitConfig.getValue("version.info.database");
		ArrayList<String> tableNameList = MySQLUtils.getCurrentTableNames(dbName);
		Connection conn = MySQLUtils.getConnection();
		try {
			PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement("select count(DISTINCT(TABLE_NAME)) "
					+ "from information_schema.`TABLES`" +
					" where TABLE_SCHEMA = ?");
			pstmt.setString(1, dbName);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			int tables = rs.getInt(1);
			assertEquals(tableNameList.size(), tables);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MySQLUtils.closeConnection(conn);
		}
	}

	@Test
	public void test_getTableSql() {
		String dbName = "souche_dfc";
		String tableName = "app_splash_screen_inc";
		String tableSQL = MySQLUtils.getCurrentTableSql(dbName, tableName);
		assertTrue(tableSQL.length() > 0);
	}

	@Test
	public void test_getCurrentColmns() {
		String databaseName = "souche_dfc";
		String tableName = "app_splash_screen_inc";
		List<Column> columList = MySQLUtils.getCurrentColmns(databaseName, tableName);
		assertEquals(columList.size(), 16);
	}

	@Test
	public void test_getCurrentTable() {
		String databaseName = "souche_dfc";
		String tableName = "app_splash_screen_inc";
		Table table = MySQLUtils.getCurrentTable(databaseName, tableName);
		assertEquals(table.getName(), tableName);
		assertEquals(table.getColumnMap().keySet().size(), 16);
	}
	@Test
	public void test_getCurrentDatabase() {
		String databaseName = "souche_dfc";
		String tableName = "app_splash_screen_inc";
		DataBase dataBase = MySQLUtils.getCurrentDatabase(databaseName);
		assertEquals(dataBase.getName(), databaseName);
		// SELECT COUNT(DISTINCT table_name) FROM
		// information_schema.`TABLES` WHERE table_schema = 'souche_dfc'
		assertEquals(dataBase.getTableMap().keySet().size(), 82);
		assertEquals(dataBase.getTable(tableName).getColumnMap().values().size(), 16);
	}

	@Test
	public void test_saveDatabaseVersion() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		assertEquals(MySQLUtils.saveDatabaseVersion(MySQLUtils.createDatabaseVersion(sdf.format(c.getTime()))), 1);
	}

	@Test
	public void test_getDatabaseVersions() {
		List<DataBaseVersion> list = MySQLUtils.getDatabaseVersions();
		for (DataBaseVersion dataBaseVersion : list) {
			System.out.println(dataBaseVersion);
		}
		assertTrue(list.size() > 0);
	}

	@Test
	public void test_getLatestDatabaseVersion() {
		DataBaseVersion dataBaseVersion = MySQLUtils.getLatestDatabaseVersion();
		List<DataBaseVersion> list = MySQLUtils.getDatabaseVersions();
		assertEquals(dataBaseVersion, list.get(list.size() - 1));
	}

	@Test
	public void test_saveTableMateInfo() {
		String databaseName = "souche_dfc";
		String tableName = "app_splash_screen_inc_bak";
		Calendar c = Calendar.getInstance();
		c.set(2018, 3 - 1, 6);
		DataBaseVersion dbVersion = new DataBaseVersion("1", "",
				new Timestamp(c.getTimeInMillis()),
				"007", "chzone");
		Table table = MySQLUtils.getCurrentTable(databaseName, tableName);
		assertEquals(MySQLUtils.saveTableMateInfo(databaseName, table, dbVersion), 1);
	}

	@Test
	public void test_saveColumn() {
		String databaseName = "souche_dfc";
		String tableName = "app_splash_screen_inc_bak";
		List<Column> columnList = MySQLUtils.getCurrentColmns(databaseName, tableName);
		Calendar c = Calendar.getInstance();
		c.set(2018, 3 - 1, 5);
		DataBaseVersion dataBaseVersion = new DataBaseVersion("1",
				"first func", new Timestamp(c.getTimeInMillis()),
				"007", "chzone");
		assertEquals(
				MySQLUtils.saveColumn(databaseName, tableName, columnList.get(0), dataBaseVersion), 1);
	}

	@Test
	public void test_saveTable() {
		String databaseName = "souche_dfc";
		String tableName = "app_splash_screen_inc_bak";
		Table table = MySQLUtils.getCurrentTable(databaseName, tableName);
		DataBaseVersion dataBaseVersion = MySQLUtils.getLatestDatabaseVersion();
		MySQLUtils.saveTable(databaseName, table, dataBaseVersion);
	}
	
	@Test
	public void test_saveDatabase(){
		String databaseName = "souche_dfc";
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String versionInfo = "by unit test[test_saveDatabase]:"+sdf.format(c.getTime());
		DataBase dataBase = MySQLUtils.getCurrentDatabase(databaseName);
		DataBaseVersion dataBaseVersion = MySQLUtils.createDatabaseVersion(versionInfo);
		// sava version info
		MySQLUtils.saveDatabaseVersion(dataBaseVersion);
		dataBaseVersion = MySQLUtils.getLatestDatabaseVersion();
		MySQLUtils.saveDatabase(dataBase,dataBaseVersion);
	}
}
