/*
 * 
 */
package com.elitecore.core.servicex;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public interface ServiceResponse {
	
	
	/**
	 * 
	 */
	public void markForDropRequest();
	
	
	/**
	 * 
	 * @return Returns whether the request is to be dropped.
	 */
	public boolean isMarkedForDropRequest();
	
	
	public void setParameter(String key, Object parameterValue);
	
	public Object getParameter(String str);
	
	public boolean isProcessingCompleted();
	public void setProcessingCompleted(boolean value);
	
	public boolean isFurtherProcessingRequired();
	public void setFurtherProcessingRequired(boolean value);
}
