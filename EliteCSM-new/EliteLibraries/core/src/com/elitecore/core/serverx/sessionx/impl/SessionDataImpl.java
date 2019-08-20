package com.elitecore.core.serverx.sessionx.impl;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.elitecore.core.serverx.sessionx.SessionData;

public class SessionDataImpl implements SessionData, Serializable {
	private static final long serialVersionUID = 1L;

	private String schemaName;
	private String sessionId;
	private Date creationTime;
	private Date lastUpdateTime;
	private Map<String,String> sessionInfo;
	private long sessionLoadTime = -1;

	public SessionDataImpl(String schemaName,Date creationTime, Date lastUpdaetTime) {
		this.schemaName = schemaName;
		this.creationTime = creationTime;
		this.lastUpdateTime = lastUpdaetTime;
		sessionInfo = new LinkedHashMap<String, String>();
	}

	public SessionDataImpl(String schemaName) {
		this(schemaName,Calendar.getInstance().getTime(),Calendar.getInstance().getTime());
	}
	
	public SessionDataImpl(String schemaName,String sessionId, Date creationTime, Date lastUpdaetTime) {
		this(schemaName,creationTime,lastUpdaetTime);
		this.sessionId = sessionId;
	}
	
	@Override
	public String getSchemaName() {
		return schemaName;
	}
	
	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public String getValue(String key) {
		return sessionInfo.get(key);
	}

	@Override
	public void addValue(String key, String value) {
		sessionInfo.put(key, value);
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.println();
		out.println("Session ID : " + sessionId);
		for(Map.Entry<String, String> entry:sessionInfo.entrySet()){
			out.println(entry.getKey() + " : " + entry.getValue());
		}
		out.println();
		out.flush();
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public Set<String> getKeySet() {
		return sessionInfo.keySet();
	}

	@Override
	public void setSessionLoadTime(long sessionLoadTime) {
		this.sessionLoadTime = sessionLoadTime;
	}

	@Override
	public long getSessionLoadTime() {
		return sessionLoadTime;
	}

	@Override
	public int compareTo(SessionData other) {
		return other.getLastUpdateTime().compareTo(this.getLastUpdateTime());
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean flag = false ;
		if(obj == this )
			return true;
		if(! (obj instanceof SessionDataImpl))
			return false;
		SessionDataImpl sessionObj = (SessionDataImpl) obj;

		flag = this.getCreationTime().equals(sessionObj.getCreationTime()) &&
				this.getLastUpdateTime().equals(sessionObj.getLastUpdateTime()) &&
				this.getSchemaName().equals(sessionObj.getSchemaName()) &&
				this.getSessionId().equals(sessionObj.getSessionId());
		if( flag ) {
			this.sessionInfo.equals(sessionObj.sessionInfo);
		}
		return flag ;
	}
}
