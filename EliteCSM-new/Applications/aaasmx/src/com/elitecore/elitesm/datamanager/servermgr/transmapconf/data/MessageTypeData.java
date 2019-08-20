package com.elitecore.elitesm.datamanager.servermgr.transmapconf.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class MessageTypeData extends BaseData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String messageTypeId;
	private String name;
	private String alias;
	private String translatorTypeId;
	private String inType;
	private String outType;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getMessageTypeId() {
		return messageTypeId;
	}
	public void setMessageTypeId(String messageTypeId) {
		this.messageTypeId = messageTypeId;
	}
	public String getTranslatorTypeId() {
		return translatorTypeId;
	}
	public void setTranslatorTypeId(String translatorTypeId) {
		this.translatorTypeId = translatorTypeId;
	}
	public String getInType() {
		return inType;
	}
	public void setInType(String inType) {
		this.inType = inType;
	}
	public String getOutType() {
		return outType;
	}
	public void setOutType(String outType) {
		this.outType = outType;
	}
	
}

