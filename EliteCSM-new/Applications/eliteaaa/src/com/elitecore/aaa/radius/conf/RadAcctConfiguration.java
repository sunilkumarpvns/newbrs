/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */

package com.elitecore.aaa.radius.conf;



/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public interface RadAcctConfiguration extends BaseRadiusServiceConfiguration{
		
	public boolean isRrdResponseTimeEnabled();
	public boolean isRrdSummaryEnabled();
	public boolean isRrdErrorsEnabled();
}
