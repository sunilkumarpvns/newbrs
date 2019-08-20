package com.elitecore.core.util.mbean.data.live;

import java.util.Date;

/**
 * @author aditya.shrivastava
 */

public class EliteNetvertexServerDetails extends EliteNetServerDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date policyReloadTime;
	
	public EliteNetvertexServerDetails() {
	  super();
	}
	
	public Date getPolicyReloadTime() {
		return policyReloadTime;
	}
	public void setPolicyReloadTime(Date policyReloadTime) {
		this.policyReloadTime = policyReloadTime;
	}
	
	
}
