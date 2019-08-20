package com.elitecore.elitesm.web.plugins;

import java.sql.Timestamp;

import net.sf.json.JSONObject;


/**
 * @author nayana.rathod
 *
 */

public class GroovyDetails {
	
	private String groovyFileName;
	private byte[] groovyFileData;
	private Timestamp lastModifiedDate;
	private JSONObject jsonObject;
	private Long date;
	private String groovyFileId;
	private String groovyData;
	private String groovyFileInstanceId;
	
	public String getGroovyFileName() {
		return groovyFileName;
	}
	public void setGroovyFileName(String groovyFileName) {
		this.groovyFileName = groovyFileName;
	}
	public byte[]  getGroovyFileData() {
		return groovyFileData;
	}
	public void setGroovyFileData(byte[] groovyFileData) {
		this.groovyFileData = groovyFileData;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public JSONObject getJsonObject() {
		return jsonObject;
	}
	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public String getGroovyFileId() {
		return groovyFileId;
	}
	public void setGroovyFileId(String groovyFileId) {
		this.groovyFileId = groovyFileId;
	}
	public String getGroovyData() {
		return groovyData;
	}
	public void setGroovyData(String groovyData) {
		this.groovyData = groovyData;
	}
	public String getGroovyFileInstanceId() {
		return groovyFileInstanceId;
	}
	public void setGroovyFileInstanceId(String groovyFileInstanceId) {
		this.groovyFileInstanceId = groovyFileInstanceId;
	}
	
	
}
