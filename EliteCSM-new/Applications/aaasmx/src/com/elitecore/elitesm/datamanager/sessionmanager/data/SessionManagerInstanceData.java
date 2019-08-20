package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.sql.Timestamp;

import javax.validation.Valid;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class SessionManagerInstanceData extends BaseData implements ISessionManagerInstanceData,Differentiable{
	
	private String smInstanceId;
	private String name;
	private String description;
	private String status;
	private String createdbystaffid;
	private String lastmodifiedbystaffid;
	private Timestamp lastmodifieddate; 
	private Timestamp createdate;
	@Valid
	private SMConfigInstanceData smConfigInstanceData ;
	private String auditUId;
	
	public SMConfigInstanceData getSmConfigInstanceData() {
		return smConfigInstanceData;
	}
	public void setSmConfigInstanceData(SMConfigInstanceData smConfigInstanceData) {
		this.smConfigInstanceData = smConfigInstanceData;
	}
	
	public String getSmInstanceId() {
		return smInstanceId;
	}
	public void setSmInstanceId(String smInstanceId) {
		this.smInstanceId = smInstanceId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCreatedbystaffid() {
		return createdbystaffid;
	}
	public void setCreatedbystaffid(String createdbystaffid) {
		this.createdbystaffid = createdbystaffid;
	}
	
	public String getLastmodifiedbystaffid() {
		return lastmodifiedbystaffid;
	}
	public void setLastmodifiedbystaffid(String lastmodifiedbystaffid) {
		this.lastmodifiedbystaffid = lastmodifiedbystaffid;
	}

	public Timestamp getLastmodifieddate() {
		return lastmodifieddate;
	}
	public void setLastmodifieddate(Timestamp lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}
	
	public Timestamp getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Timestamp createdate) {
		this.createdate = createdate;
	}
	
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("status", status);
		object.put("Description", description);
		if(smConfigInstanceData != null){
			object.put("SMConfigInstanceData", smConfigInstanceData.toJson());
		}
		return object;
	}
}
