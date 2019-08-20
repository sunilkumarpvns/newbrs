/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author tejasmudgal
 *  Last Modified October 1, 2008
 */

package com.elitecore.classicrating.commons.response;

import java.util.HashMap;

import com.elitecore.classicrating.api.IResponseObject;

/**
 * Defines the Response provided by the Rating API to the caller of API.
 * 
 * @author tejasmudgal
 * 
 */
public class ResponseObject extends HashMap<String, String> implements IResponseObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int responseCode;

	private String responseMessage;

	private Object responseObject;

	public Object getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(Object responseObject) {
		this.responseObject = responseObject;
	}

	public int getResponseCode() {
		return this.responseCode;
	}

	public String getResponseMessage() {
		return this.responseMessage;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public void setResponseMessage(String strResponseMessage) {
		this.responseMessage = strResponseMessage;
	}
        
  }
