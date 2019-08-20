/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author tejasmudgal
 *  Last Modified October 1, 2008
 */

package com.elitecore.classicrating.commons.request;


import com.elitecore.classicrating.api.IRequestParameters;

/**
 * 
 * Defines the Request Parameters.
 * The caller of the Rating API provides Request Parameters to the Rating API 
 * 
 * @author tejasmudgal
 *
 */
public class RequestParameters extends java.util.HashMap<String, String> implements IRequestParameters {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String get(String key){
        return super.get(key);
    }
}
