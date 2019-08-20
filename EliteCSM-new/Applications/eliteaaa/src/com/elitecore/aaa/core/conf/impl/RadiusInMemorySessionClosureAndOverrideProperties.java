package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.base.Differentiable;

import net.sf.json.JSONObject;

@XmlRootElement(name="radius-inmemory-session-closure-properties")
public class RadiusInMemorySessionClosureAndOverrideProperties implements Differentiable {

	private long sessionTimeout = 2;
	private long sessionCloseBatchCount = 1000;
	private long closureInterval = 100;
	private String sessionOverrideField;
	private String action;


	@XmlElement(name="close-batch-count")
	public long getSessionCloseBatchCount() {
		return sessionCloseBatchCount;
	}

	public void setSessionCloseBatchCount(long sessionCloseBatchCount) {
		this.sessionCloseBatchCount = sessionCloseBatchCount;
	}

	@XmlElement(name="session-time-out")
	public long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	@XmlElement(name="close-action")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@XmlElement(name="override-field")
	public String getSessionOverrideField() {
		return sessionOverrideField;
	}

	public void setSessionOverrideField(String sessionOverrideField) {
		this.sessionOverrideField = sessionOverrideField;
	}

	@XmlElement(name="closure-interval")
	public long getClosureInterval() {
		return closureInterval;
	}

	public void setClosureInterval(long closureInterval) {
		this.closureInterval = closureInterval;
	}

	public enum SessionCloseAndOverrideAction {
		NONE("None"),
		GENERATEDMANDSTOP("Generate Disconnect And Stop");
		public final String action;
		private SessionCloseAndOverrideAction(String value) {
			this.action=value;
		}
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("session time out", sessionTimeout);
		jsonObject.put("session close Batch count", sessionCloseBatchCount);
		jsonObject.put("session closer interval", closureInterval);
		jsonObject.put("session override field", sessionOverrideField);
		jsonObject.put("session close action", action);
		return jsonObject;
	}

}
