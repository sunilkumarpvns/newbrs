/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.radius.service.acct;

import com.elitecore.aaa.radius.conf.RadAcctConfiguration;
import com.elitecore.aaa.radius.service.RadServiceContext;


/**
 * 
 * @author Elitecore Technlogies Ltd.
 *
 */
public interface RadAcctServiceContext extends RadServiceContext<RadAcctRequest, RadAcctResponse> {

	public RadAcctConfiguration getAcctConfiguration();
	
}
