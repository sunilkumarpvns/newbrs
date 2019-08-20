package com.elitecore.elitesm.web.dashboard.widget;

/**
 * Defines an object to provide widget request information
 * 
 * @author punit.j.patel
 *
 */
public interface WidgetRequest {
	/**
	 * Returns Socket Id of Widget Request  
	 *  
	 * @return the Socket Id of widget Request
	 */
	public String getSocketID();
	
	
	/**
	 * Returns request String body of Widget Request
	 * 
	 * @return a request data of widget request
	 */
	public String getRequestData();
	
	
	/**
	 * Returns an Object of WidgetDataSender for Send data to client
	 * 
	 * @return an object of WidgetDataSender
	 */
	public WidgetDataSender getWidgetDataSender();
}
