/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 15, 2008
 *	@author Hardik Shah
 *
 */

package com.elitecore.classicrating.api;

import java.io.Serializable;

/**
 * @author hardikshah
 *
 */
public interface IResponseObject extends Serializable {
	
	public String get(Object key) ;
	
	public String put(String key ,String value) ;
	
	public int getResponseCode() ;
	
	public void setResponseCode(int responseCode) ;
	
	public Object getResponseObject() ;
	
	public void setResponseObject(Object responseObject) ;
	
	public String getResponseMessage();
	
	public void setResponseMessage(String responseMessage) ;
        
        public String toString();

}
