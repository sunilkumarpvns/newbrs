package com.elitecore.netvertex.service.pcrf.servicepolicy.conf;


import com.elitecore.corenetvertex.constants.RequestAction;
import com.elitecore.corenetvertex.constants.ServicePolicyStatus;
import com.elitecore.corenetvertex.constants.SyMode;
import com.elitecore.corenetvertex.constants.UnknownUserAction;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;


/**
 * 
 * @author Manjil Purohit
 *
 */
public interface PccServicePolicyConfiguration  extends ToStringable {
	public String getPcrfPolicyId();
	public String getName();
	public String getDescription();
	public String getRuleset();
	public RequestAction getAction();
	public String getSubscriberLookupOn();
	public String getIdentityAttribute();
	public UnknownUserAction getUnknownUserAction();
	public String getUnknownUserPkgId();
	public String getUnknownUserPkgName();
	public String getSyGateway();
	public SyMode getSyMode();
	public DriverConfiguration getPolicyCdrDriver();
	public DriverConfiguration getChargingCdrDriver();
	public Integer getOrderNumber();
	public ServicePolicyStatus getStatus();

}
