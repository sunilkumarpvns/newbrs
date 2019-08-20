package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import org.hibernate.Hibernate;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;

public class ConfigurationProfileData extends BaseData implements IConfigurationProfileData,Serializable{
	private String configurationProfileId;
	private String name;
	private String alias;
	private String description;
	private String systemGenerated;
	private byte[] xmlData = {};

	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getConfigurationProfileId() {
		return configurationProfileId;
	}
	public void setConfigurationProfileId(String configurationProfileId) {
		this.configurationProfileId = configurationProfileId;
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
	
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	
	public byte[] getXmlData() {
		return xmlData;
	}
	public void setXmlData(byte[] xmlData) {
		this.xmlData = xmlData;
	}
	
	public void setXmlDataBlob(Blob dataBlob){
		this.xmlData = toByteArray(dataBlob);
	}
	public Blob getXmlDataBlob(){
		if(this.xmlData != null){
			return null;
			//return Hibernate.createBlob(this.xmlData);
		}else{
			return null;
		}
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
