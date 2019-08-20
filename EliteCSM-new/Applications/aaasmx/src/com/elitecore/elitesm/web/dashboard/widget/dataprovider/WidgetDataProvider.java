package com.elitecore.elitesm.web.dashboard.widget.dataprovider;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.WidgetListener;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;


/**
 * Interface defining contract for provide Widget Data 
 * 
 * @author punit.j.patel
 *
 */
public interface WidgetDataProvider {
	/**
	 * Returns Initial <code>List</code> of <code>WidgetJSON</code>,
	 * Normally, this method use when first time send Widget JSON Data to Client
	 * 
	 * @return list of WidgetJSON
	 */
	public List<WidgetJSON> provideInitialData(String serverKey);
	
	/**
	 * Returns <code>List</code> of <code>WidgetJSON</code>
	 * Normally, this method invoke by <code>WidgetListener</code>
	 * 
	 * @return list of WidgetJSON
	 * @see WidgetListener
	 */
	public List<WidgetJSON> provideData();
	
	/**
	 * Returns name of Widget Provider
	 * 
	 * @return name Of Widget Provider
	 */
	public String getName();
}
