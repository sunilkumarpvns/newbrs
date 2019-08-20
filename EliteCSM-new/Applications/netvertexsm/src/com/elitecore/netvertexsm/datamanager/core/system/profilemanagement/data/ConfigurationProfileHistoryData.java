package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.hibernate.Hibernate;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;

public class ConfigurationProfileHistoryData extends BaseData implements IConfigurationProfileHistoryData,Serializable {
	private String configProfileHistoryId;
	private String name;
	private String description;
	private Timestamp createDate;
	private String configurationProfileId;
	private byte[] xmlData;
	private String version;
	private String createdBy;
	
	public String getConfigProfileHistoryId() {
		return configProfileHistoryId;
	}
	public void setConfigProfileHistoryId(String configProfileHistoryId) {
		this.configProfileHistoryId = configProfileHistoryId;
	}
	
	public String getConfigurationProfileId() {
		return configurationProfileId;
	}
	public void setConfigurationProfileId(String configurationProfileId) {
		this.configurationProfileId = configurationProfileId;
	}
	
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public byte[] getXmlData() {
		return xmlData;
	}
	public void setXmlData(byte[] xmlData) {
		this.xmlData = xmlData;
	}
	
	private void setXmlDataBlob(Blob dataBlob){
		this.xmlData = toByteArray(dataBlob);
	}
	private Blob getXmlDataBlob(){
		return null;
		//return Hibernate.createBlob(this.xmlData);
	}
	
	private byte[] toByteArray(Blob fromDataBlob){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			return toByteArrayImpl(fromDataBlob,byteArrayOutputStream);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	private byte[] toByteArrayImpl(Blob fromDataBlob,ByteArrayOutputStream byteArrayOutputStream) throws SQLException,IOException{
		byte buf[] = new byte[4000];
		int datasize;
		InputStream inputStream = fromDataBlob.getBinaryStream();
		try {
			while((datasize = inputStream.read(buf)) != -1){
				byteArrayOutputStream .write(buf,0,datasize);
			}
		}finally{
			if(inputStream != null){
				inputStream.close();
			}
		}
		return byteArrayOutputStream.toByteArray();
	}
}
