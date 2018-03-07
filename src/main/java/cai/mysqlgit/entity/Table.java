package cai.mysqlgit.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
	private String				name;
	private Map<String, Column>	columnMap;

	public Table(String name) {
		super();
		this.name = name;
		columnMap = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Column> getColumnMap() {
		return columnMap;
	}

	public void setColumnMap(Map<String, Column> columnMap) {
		this.columnMap = columnMap;
	}

	public void addColumns(List<Column> columnList) {
		for (Column c : columnList) {
			columnMap.put(c.getName(), c);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		Table otherTable = (Table) obj;
		return name.equals(otherTable.getName())
				&& columnMap.equals(otherTable.getColumnMap());
	}

	@Override
	public String toString() {
		return "Table [name=" + name + ", columnMap=" + columnMap + "]";
	}
}
