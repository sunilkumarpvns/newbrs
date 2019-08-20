package com.elitecore.aaa.core.util.constant;

import com.elitecore.core.commons.util.constants.BaseConstants;

public class CommonConstants extends BaseConstants {

	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_INACTIVE="INACTIVE";
	public static final String CUSTOMER_TYPE_PREPAID="prepaid";
	public static final String CUSTOMER_TYPE_POSTPAID="postpaid";
	public static final String DATABASE_POLICY_STATUS_ACTIVE ="CST01";
	
	/*
	 *  Constants added for Subscriber Based Routing 
	 */
	public static final String IMSI_MSISDN = "IMSI_MSISDN";
	public static final String MSISDN_IMSI = "MSISDN_IMSI";
	
	public static final String ELITEAAA_HOME = System.getProperty("ELITEAAA_HOME");
	public static final String WEBSNMP_PORT = "websnmp.port";
}
