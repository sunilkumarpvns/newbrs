package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.Differentiable;

public interface ServicePolicyHandlerData extends Differentiable{
	
	public String getPolicyName();
	public List<String> getUserIdentities();
	public ChargeableUserIdentityConfiguration getCuiConfiguration();
	public void setRadiusServicePolicyData(RadiusServicePolicyData data);
	public RadiusServicePolicyData getRadiusServicePolicyData();
	@XmlTransient
	AAAServerContext getServerContext();
	void setServerContext(AAAServerContext aaaServerContext);
	public void postRead();
	public String getEnabled();
	public String getHandlerName();
}
