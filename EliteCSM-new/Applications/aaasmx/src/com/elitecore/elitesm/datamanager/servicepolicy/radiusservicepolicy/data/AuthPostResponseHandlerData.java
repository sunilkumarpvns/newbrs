package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.commons.base.Collectionz;


@XmlRootElement(name = "post-response-handler")
public class AuthPostResponseHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData {
	
	@XmlElementRefs({
		@XmlElementRef(name = "proxy-handler", type = SynchronousCommunicationHandlerData.class),
		@XmlElementRef(name = "broadcast-handler", type = BroadcastCommunicationHandlerData.class),
		@XmlElementRef(name = "authentication-handler", type = AuthenticationHandlerData.class),
		@XmlElementRef(name = "authorization-handler", type = AuthorizationHandlerData.class),
		@XmlElementRef(name = "user-profile-repository", type = RadiusSubscriberProfileRepositoryDetails.class),
		@XmlElementRef(name = "plugin-handler", type = RadPluginHandlerData.class),
		@XmlElementRef(name = "concurrency-handler", type = ConcurrencyHandlerData.class),
		@XmlElementRef(name = "cdr-generation", type = CdrGenerationHandlerData.class),
		@XmlElementRef(name = "coa-dm-generation-handler", type = CoADMGenerationHandlerData.class)
	})
	
	@Valid
	private List<AuthServicePolicyHandlerData> handlersData = new ArrayList<AuthServicePolicyHandlerData>();

	public List<AuthServicePolicyHandlerData> getHandlersData() {
		return handlersData;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return null;
	}
	
	@Override
	public void postRead() {
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		List<String> flowOrderList = new  ArrayList<String>();
		if(Collectionz.isNullOrEmpty(handlersData) == false){
			for(AuthServicePolicyHandlerData authServicePolicyHandlerData : handlersData){
				object.put(authServicePolicyHandlerData.getHandlerName(), authServicePolicyHandlerData.toJson());
				flowOrderList.add(authServicePolicyHandlerData.getHandlerName());
			}
		}
		object.put("Handlers Order", EliteUtility.getServicePolicyOrder(flowOrderList));
		return object;
	}
}