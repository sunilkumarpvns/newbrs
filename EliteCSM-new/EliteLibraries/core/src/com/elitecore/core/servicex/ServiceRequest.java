package com.elitecore.core.servicex;


/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public interface ServiceRequest {
	
	public void setParameter(String key, Object parameterValue);
	
	public Object getParameter(String str);
	
	public long getRequestReceivedNano();

}
