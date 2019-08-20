package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.commons.base.Collectionz;

@XmlRootElement(name = "post-response-handler")
public class AcctPostResponseHandlerData extends ServicePolicyHandlerDataSupport implements AcctServicePolicyHandlerData {
	
	@XmlElementRefs({
		@XmlElementRef(name = "proxy-handler", type = SynchronousCommunicationHandlerData.class),
		@XmlElementRef(name = "broadcast-handler", type = BroadcastCommunicationHandlerData.class),
		@XmlElementRef(name = "user-profile-repository", type = RadiusSubscriberProfileRepositoryDetails.class),
		@XmlElementRef(name = "plugin-handler", type = RadPluginHandlerData.class),
		@XmlElementRef(name = "cdr-generation", type = CdrGenerationHandlerData.class),
		@XmlElementRef(name = "coa-dm-generation-handler", type = CoADMGenerationHandlerData.class),
		@XmlElementRef(name = "authentication-handler", type = AuthenticationHandlerData.class),
		@XmlElementRef(name = "authorization-handler", type = AuthorizationHandlerData.class),
		@XmlElementRef(name = "concurrency-handler", type = ConcurrencyHandlerData.class)
	})
	
	@Valid
	private List<AcctServicePolicyHandlerData> handlersData = new ArrayList<AcctServicePolicyHandlerData>();

	public List<AcctServicePolicyHandlerData> getHandlersData() {
		return handlersData;
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
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
			for(AcctServicePolicyHandlerData acctServicePolicyHandlerData : handlersData){
				object.put(acctServicePolicyHandlerData.getHandlerName(), acctServicePolicyHandlerData.toJson());
				flowOrderList.add(acctServicePolicyHandlerData.getHandlerName());
			}
		}
		object.put("Handlers Order", EliteUtility.getServicePolicyOrder(flowOrderList));
		return object;
	}
}
