package com.elitecore.aaa.diameter.policies.applicationpolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.policies.AAAServicePolicy;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.servicex.ServiceContext;

public abstract class DiameterServicePolicyContainer implements ReInitializable {
	
	private ServiceContext serviceContext;
	
	private Map<String, DiameterServicePolicyConfiguration> policyConfMap;
	private List<ServicePolicy<ApplicationRequest>> servicePolicyList;
	private static final String MODULE = "DIA-SERV-POLICY-CONTAINER";
	@Nullable private final DiameterSessionManager diameterSessionManager;

	public DiameterServicePolicyContainer(ServiceContext serviceContext,Map<String,DiameterServicePolicyConfiguration> policyConfMap, DiameterSessionManager diameterSessionManager) {
		this.serviceContext = serviceContext;
		this.policyConfMap = policyConfMap;
		this.diameterSessionManager = diameterSessionManager;
	}
	
	public void init() {
		List<ServicePolicy<ApplicationRequest>> tmpServicePolicies= new ArrayList<ServicePolicy<ApplicationRequest>>();	
		ServicePolicy<ApplicationRequest> servicePolicy;
		if(policyConfMap!=null && policyConfMap.size()>0){
			for (Entry<String, DiameterServicePolicyConfiguration> entry : policyConfMap.entrySet()) {
				try {
					servicePolicy = getPolicyObject(serviceContext,entry.getKey(), diameterSessionManager);
					servicePolicy.init();
					tmpServicePolicies.add(servicePolicy);				
				} catch(Exception e) {
					LogManager.getLogger().error(MODULE, "Failed to Initialize the Service-Policy " + entry.getValue().getName() + 
							". Reason: " + e.getMessage());
					LogManager.getLogger().trace(e);
				}
			}
		}
		this.servicePolicyList = tmpServicePolicies;
	}
	
	public ServicePolicy<ApplicationRequest> applyPolicy(ApplicationRequest appRequest) {
		for (ServicePolicy<ApplicationRequest> servicePolicy : this.servicePolicyList) {
			if (servicePolicy.assignRequest(appRequest)) {
				return servicePolicy;
			}
		}
		return null;
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		for (ServicePolicy<ApplicationRequest> servicePolicy: this.servicePolicyList){
			((AAAServicePolicy<ApplicationRequest>)servicePolicy).reInit();
		}
	}
	
	protected abstract ServicePolicy<ApplicationRequest> getPolicyObject(ServiceContext serviceContext,String policyId, DiameterSessionManager diameterSessionManager) throws InitializationFailedException;
	
	public ServiceContext getServiceContext() {
		return serviceContext;
	}
	
	@Nullable public DiameterSessionManager getDiameterSessionManager() {
		return diameterSessionManager;
	}

}
