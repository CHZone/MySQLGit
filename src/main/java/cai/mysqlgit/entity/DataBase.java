package cai.mysqlgit.entity;

import java.util.HashMap;
import java.util.Map;

public class DataBase {
	private String				name;
	private Map<String, Table>	tableMap;

	public DataBase() {
		super();
		tableMap = new HashMap<>();
	}

	public DataBase(String name) {
		super();
		this.name = name;
		tableMap = new HashMap<>();
	}

	public DataBase(String name, Map<String, Table> tableMap) {
		super();
		this.name = name;
		this.tableMap = tableMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public Map<String, Table> getTableMap() {
		return tableMap;
	}

	public void setTableMap(Map<String, Table> tableMap) {
		this.tableMap = tableMap;
	}

	public void addTable(Table table) {
		this.tableMap.put(table.getName(), table);
	}

	public Table getTable(String tableName) {
		return this.tableMap.get(tableName);
	}

	@Override
	public boolean equals(Object obj) {
		DataBase otherDataBase = (DataBase) obj;
		return name.equals(otherDataBase.getName())
				&& tableMap.equals(otherDataBase.getTableMap());
	}

	@Override
	public String toString() {
		return "DataBase [name=" + name + ", tableMap=" + tableMap + "]";
	}
}
