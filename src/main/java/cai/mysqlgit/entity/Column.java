package cai.mysqlgit.entity;

import cai.mysqlgit.utils.EqualCompareUtils;

public class Column {
	/**
	 * COLUMN_NAME
	 */
	private String	name;
	/**
	 * COLUMN_TYPE
	 */
	private String	columnType;
	/**
	 * COLUMN_DEFAULT
	 */
	private String	colunmDefault;
	/**
	 * IS_NULLABLE
	 */
	private String	nullable;
	/**
	 * COLUMN_KEY
	 */
	private String	columnKey;
	/**
	 * COLUMN_COMMENT
	 */
	private String	columnComment;

	public Column() {
		super();
	}

	public Column(String name, String columnType, String colunmDefault, String nullable, String columnKey, String columnComment) {
		super();
		this.name = name;
		this.columnType = columnType;
		this.colunmDefault = colunmDefault;
		this.nullable = nullable;
		this.columnKey = columnKey;
		this.columnComment = columnComment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public String getColumnComment() {
		return columnComment;
	}

	public void setColumnComment(String columnComment) {
		this.columnComment = columnComment;
	}

	public String getColunmDefault() {
		return colunmDefault;
	}

	public void setColunmDefault(String colunmDefault) {
		this.colunmDefault = colunmDefault;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getColumnKey() {
		return columnKey;
	}

	public void setColumnKey(String columnKey) {
		this.columnKey = columnKey;
	}

	@Override
	public boolean equals(Object obj) {
		Column otherColumn = (Column) obj;
		return EqualCompareUtils.isEqual(name, otherColumn.getName())
				&& EqualCompareUtils.isEqual(columnType, otherColumn.getColumnType())
				&& EqualCompareUtils.isEqual(colunmDefault, otherColumn.getColunmDefault())
				&& EqualCompareUtils.isEqual(nullable, otherColumn.getNullable())
				&& EqualCompareUtils.isEqual(columnKey, otherColumn.getColumnKey())
				&& EqualCompareUtils.isEqual(columnComment, otherColumn.getColumnComment());
	}


	@Override
	public String toString() {
		return "Column [name=" + name + ", columnType=" + columnType +
				", ColunmDefault=" + colunmDefault + ", nullable=" +
				nullable + ", columnKey=" + columnKey + ", columnComment="
				+ columnComment + "]";
	}
}
