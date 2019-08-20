package com.elitecore.aaa.diameter.service.application.handlers.conf;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.diameter.policies.tgppserver.TGPPServerPolicyData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Predicate;

public interface DiameterApplicationHandlerData extends Differentiable{
	
	public static final Predicate<? super DiameterApplicationHandlerData> ENABLED = new Predicate<DiameterApplicationHandlerData>() {

		@Override
		public boolean apply(DiameterApplicationHandlerData input) {
			return input.isEnabled();
		}
	};
	
	public boolean isEnabled();
	
	public TGPPServerPolicyData getPolicyData();
	public void setPolicyData(TGPPServerPolicyData data);
	public AAAConfigurationContext getConfigurationContext();
	public void setConfigurationContext(AAAConfigurationContext context);
	
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(DiameterServiceContext context);
	
	public void postRead();
	public String getHandlerName();
}
