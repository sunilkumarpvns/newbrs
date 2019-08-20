package com.elitecore.aaa.radius.service.acct.policy.handlers.conf;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.AcctPostResponseHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.CdrGenerationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMGenerationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadPluginHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerDataSupport;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.commons.base.Collectionz;

@XmlRootElement(name = "post-response-handler")
public class AcctPostResponseHandlerData extends ServicePolicyHandlerDataSupport implements AcctServicePolicyHandlerData {
	
	@XmlElementRefs({
		@XmlElementRef(name = "cdr-generation", type = CdrGenerationHandlerData.class),
		@XmlElementRef(name = "coa-dm-generation-handler", type = CoADMGenerationHandlerData.class),
		@XmlElementRef(name = "plugin-handler", type = RadPluginHandlerData.class)
	})
	private List<AcctServicePolicyHandlerData> handlersData = new ArrayList<AcctServicePolicyHandlerData>(3);

	public List<AcctServicePolicyHandlerData> getHandlersData() {
		return handlersData;
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return new AcctPostResponseHandler(serviceContext, this);
	}
	
	@Override
	public void postRead() {
		super.postRead();
		for (AcctServicePolicyHandlerData handlerData : getHandlersData()) {
			handlerData.setServerContext(getRadiusServicePolicyData().getServerContext());
			handlerData.setRadiusServicePolicyData(getRadiusServicePolicyData());
			handlerData.postRead();
		}
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
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		for(AcctServicePolicyHandlerData acctServicePolicyHandlerData : handlersData){
			out.println(acctServicePolicyHandlerData);
		}
		return writer.toString();
	}
}
