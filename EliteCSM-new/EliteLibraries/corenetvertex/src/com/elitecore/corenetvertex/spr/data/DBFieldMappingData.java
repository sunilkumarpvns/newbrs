package com.elitecore.corenetvertex.spr.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="TBLMDBFIELDMAP")
public class DBFieldMappingData {

	private Long dbFieldMapId;
	private String logicalName;
	private String dbField;
	
	transient private DBSPInterfaceData dbspInterfaceData;

	@Id
	@Column(name="DBFIELDMAPID")
	@GeneratedValue(generator="sequenceGenerator")
	public Long getDbFieldMapId() {
		return dbFieldMapId;
	}

	public void setDbFieldMapId(Long dbFieldMapId) {
		this.dbFieldMapId = dbFieldMapId;
	}

	@Column(name="LOGICALNAME")
	public String getLogicalName() {
		return logicalName;
	}

	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	@Column(name="DBFIELD")
	public String getDbField() {
		return dbField;
	}

	public void setDbField(String dbField) {
		this.dbField = dbField;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="DBSPINTERFACEID")
	public DBSPInterfaceData getDbspInterfaceData() {
		return dbspInterfaceData;
	}

	public void setDbspInterfaceData(DBSPInterfaceData dbspInterfaceData) {
		this.dbspInterfaceData = dbspInterfaceData;
	}
}
