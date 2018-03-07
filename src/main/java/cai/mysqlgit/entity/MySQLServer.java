package cai.mysqlgit.entity;

import java.util.Map;

public class MySQLServer {
	private Map<String, DataBase> databaseMap;

	public Map<String, DataBase> getDatabaseMap() {
		return databaseMap;
	}

	public void setDatabaseMap(Map<String, DataBase> databaseMap) {
		this.databaseMap = databaseMap;
	}
	
}
