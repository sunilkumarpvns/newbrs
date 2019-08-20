package com.elitecore.nvsmx.policydesigner.model.session;

import com.elitecore.nvsmx.policydesigner.controller.session.SessionSearchAttributes;

import java.io.Serializable;

/**
 * Used to search active sessions
 * @author Dhyani.Raval
 *
 */
public class SessionSearchField implements Serializable {
	
	private SessionSearchAttributes sessionAttribute;
	private String attributeValue;
	
	public SessionSearchAttributes getSessionAttribute() {
		return sessionAttribute;
	}
	public void setSessionAttribute(SessionSearchAttributes sessionAttribute) {
		this.sessionAttribute = sessionAttribute;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	
	

}
