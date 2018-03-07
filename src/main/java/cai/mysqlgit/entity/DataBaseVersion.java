package cai.mysqlgit.entity;

import java.sql.Timestamp;

import cai.mysqlgit.utils.EqualCompareUtils;

public class DataBaseVersion {
	private String id;
	private String versionInfo;
	private Timestamp versionCreateTime;
	private String creator;
	private String creatorName;
	
	public DataBaseVersion() {
		super();
	}

	
	public DataBaseVersion(String versionInfo, Timestamp versionCreateTime, String creator, String creatorName) {
		super();
		this.versionInfo = versionInfo;
		this.versionCreateTime = versionCreateTime;
		this.creator = creator;
		this.creatorName = creatorName;
	}


	public DataBaseVersion(String id, String versionInfo, Timestamp versionCreateTime, String creator, String creatorName) {
		super();
		this.id = id;
		this.versionInfo = versionInfo;
		this.versionCreateTime = versionCreateTime;
		this.creator = creator;
		this.creatorName = creatorName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersionInfo() {
		return versionInfo;
	}

	public void setVersionInfo(String versionInfo) {
		this.versionInfo = versionInfo;
	}

	public Timestamp getVersionCreateTime() {
		return versionCreateTime;
	}

	public void setVersionCreateTime(Timestamp versionCreateTime) {
		this.versionCreateTime = versionCreateTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}


	@Override
	public String toString() {
		return "DataBaseVersion [id=" + id + ", versionInfo=" + versionInfo + ", versionCreateTime=" + versionCreateTime + ", creator=" + creator + ", creatorName=" + creatorName + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		DataBaseVersion other = (DataBaseVersion) obj;
		return EqualCompareUtils.isEqual(this.id, other.getId());
	}
	
	
}
