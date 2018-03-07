package cai.mysqlgit;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cai.mysqlgit.entity.DataBase;
import cai.mysqlgit.entity.DataBaseVersion;
import cai.mysqlgit.utils.MySQLUtils;

public class App {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String databaseName = "souche_dfc";
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String versionInfo = "app_splash_screen_inc_bak add table app_splash_screen_bak_copy:" + sdf.format(c.getTime());
		DataBase dataBase = MySQLUtils.getCurrentDatabase(databaseName);
		DataBaseVersion dataBaseVersion = MySQLUtils.createDatabaseVersion(versionInfo);
		// sava version info
		MySQLUtils.saveDatabaseVersion(dataBaseVersion);
		dataBaseVersion = MySQLUtils.getLatestDatabaseVersion();
		MySQLUtils.saveDatabase(dataBase, dataBaseVersion);
		long endTime = System.currentTimeMillis();
		System.out.println("运行时间：" + (endTime - startTime) + "ms");
	}
}
