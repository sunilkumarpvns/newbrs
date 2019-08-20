package com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

/**
 * @author nayana.rathod
 *
 */

public class GroovyPluginFile extends BaseData implements IGroovyPluginFile,Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;

	private String groovyFileId;
	private String pluginId;
	private byte[] groovyFile;
	private String groovyFileName;
	private Timestamp lastUpdatedTime;
	private Date date;
	private Integer orderNumber ;
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	@XmlTransient
	public String getPluginId() {
		return pluginId;
	}
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	
	@XmlTransient
	public byte[] getGroovyFile() {
		return groovyFile;
	}
	public void setGroovyFile(byte[] groovyFile) {
		this.groovyFile = groovyFile;
	}
	
	@XmlTransient
	public Timestamp getLastUpdatedTime() {
		return lastUpdatedTime;
	}
	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	
	@XmlElement(name  = "groovy-filename")
	public String getGroovyFileName() {
		return groovyFileName;
	}
	public void setGroovyFileName(String groovyFileName) {
		this.groovyFileName = groovyFileName;
	}
	
	@XmlTransient
	public String getGroovyFileId() {
		return groovyFileId;
	}
	public void setGroovyFileId(String groovyFileId) {
		this.groovyFileId = groovyFileId;
	}
	
	@XmlTransient
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		JSONObject innerObject = new JSONObject();
		
		if( groovyFile != null && groovyFile.length > 0)
			jsonObject.put(groovyFileName, new String(groovyFile));
		
		innerObject.put(groovyFileName, jsonObject);
		
		return innerObject;
	}
}
