package com.elitecore.aaa.radius.service.auth.policy.handlers.conf;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthPostResponseHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMGenerationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadPluginHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerDataSupport;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.commons.base.Collectionz;


@XmlRootElement(name = "post-response-handler")
public class AuthPostResponseHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData {
	
	@XmlElementRefs({
		@XmlElementRef(name = "cdr-generation", type = CdrGenerationHandlerData.class),
		@XmlElementRef(name = "coa-dm-generation-handler", type = CoADMGenerationHandlerData.class),
		@XmlElementRef(name = "plugin-handler", type = RadPluginHandlerData.class)
	})
	private List<AuthServicePolicyHandlerData> handlersData = new ArrayList<AuthServicePolicyHandlerData>(3);

	public List<AuthServicePolicyHandlerData> getHandlersData() {
		return handlersData;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthPostResponseHandler(serviceContext, this);
	}
	
	@Override
	public void postRead() {
		super.postRead();
		for (AuthServicePolicyHandlerData handlerData : getHandlersData()) {
			handlerData.setServerContext(getServerContext());
			handlerData.setRadiusServicePolicyData(getRadiusServicePolicyData());
			handlerData.postRead();
		}
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
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		for(AuthServicePolicyHandlerData authServicePolicyHandlerData : handlersData){
			out.println(authServicePolicyHandlerData);
		}
		return writer.toString();
	}
}
