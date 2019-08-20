package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Predicate;

public interface ServicePolicyHandlerData extends Differentiable{
	
	public static final Predicate<ServicePolicyHandlerData> ENABLED = new Predicate<ServicePolicyHandlerData>() {

		@Override
		public boolean apply(ServicePolicyHandlerData input) {
			return input.isEnabled();
		}
	};
	
	public String getPolicyName();
	public List<String> getUserIdentities();
	public ChargeableUserIdentityConfiguration getCuiConfiguration();
	public void setRadiusServicePolicyData(RadiusServicePolicyData data);
	public RadiusServicePolicyData getRadiusServicePolicyData();
	@XmlTransient
	AAAServerContext getServerContext();
	void setServerContext(AAAServerContext aaaServerContext);
	public void postRead();
	public boolean isEnabled();
	public String getHandlerName();
}
