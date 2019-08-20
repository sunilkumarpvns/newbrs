/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 4th August 2010 by Ezhava Baiju D
 *  
 */


package com.elitecore.aaa.radius.conf;

import com.elitecore.aaa.core.conf.DigestConfiguration;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public interface RadAuthConfiguration  extends BaseRadiusServiceConfiguration {

	public boolean isRrdRejectReasonsEnabled();
	public RadBWListConfiguration getBwListConfiguration();
	public DigestConfiguration getDigestConfiguration(String authPolicyId);
	public DummyRatingConfiguration getDummyRatingConfiguration();
	public boolean isRrdResponseTimeEnabled();
	public boolean isRrdSummaryEnabled();
	public boolean isRrdErrorsEnabled();
}
