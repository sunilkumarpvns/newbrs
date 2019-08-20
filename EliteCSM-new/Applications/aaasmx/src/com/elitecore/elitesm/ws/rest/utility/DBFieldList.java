package com.elitecore.elitesm.ws.rest.utility;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "data")
public class DBFieldList {

	private List<DBField> dbFieldList;

	@XmlElement(name = "db-field")
	public List<DBField> getDbFieldList() {
		return dbFieldList;
	}

	public void setDbFieldList(List<DBField> dbFieldList) {
		this.dbFieldList = dbFieldList;
	}
}