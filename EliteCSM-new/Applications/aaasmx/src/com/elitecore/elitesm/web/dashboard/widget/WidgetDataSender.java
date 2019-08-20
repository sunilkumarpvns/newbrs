package com.elitecore.elitesm.web.dashboard.widget;

/**
 * Interface defining contract for send Widget Data.
 * 
 * @author punit.j.patel
 *
 */
public interface WidgetDataSender {
	
	/**
	 * send Widget Data to Widget Client
	 * 
	 * @param data 
	 */
	public void send(String data);
}
