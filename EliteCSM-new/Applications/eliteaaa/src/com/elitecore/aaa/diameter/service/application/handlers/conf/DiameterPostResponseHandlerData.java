package com.elitecore.aaa.diameter.service.application.handlers.conf;

import static com.elitecore.commons.base.Strings.repeat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterPostResponseHandler;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.commons.base.Collectionz;

@XmlRootElement(name = "post-response-handler")
public class DiameterPostResponseHandlerData extends DiameterApplicationHandlerDataSupport {

	@XmlElementRefs({
			@XmlElementRef(name = "cdr-generation", type = DiameterCDRGenerationHandlerData.class),
			@XmlElementRef(name = "plugin-handler", type = DiameterPluginHandlerData.class)
			}
	)
	private List<DiameterApplicationHandlerData> handlersData = new ArrayList<DiameterApplicationHandlerData>();

	public List<DiameterApplicationHandlerData> getHandlersData() {
		return handlersData;
	}

	@Override
	public void postRead() {
		super.postRead();
		for (DiameterApplicationHandlerData handlerData : getHandlersData()) {
			handlerData.setPolicyData(getPolicyData());
			handlerData.setConfigurationContext(getConfigurationContext());
			handlerData.postRead();
		}
	}
	
	@Override
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(
			DiameterServiceContext context) {
		return new DiameterPostResponseHandler(context, this);
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println("Post response flow handlers");
		for (DiameterApplicationHandlerData handlerData : handlersData) {
			out.println(handlerData);
		}
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		List<String> flowOrderList = new  ArrayList<String>();
		if(Collectionz.isNullOrEmpty(handlersData) == false){
			for(DiameterApplicationHandlerData diameterApplicationHandlerData : handlersData){
				object.put(diameterApplicationHandlerData.getHandlerName(), diameterApplicationHandlerData.toJson());
				flowOrderList.add(diameterApplicationHandlerData.getHandlerName());
			}
		}
		
		object.put("Post Handlers Order", EliteUtility.getServicePolicyOrder(flowOrderList));
		return object;
	}
}
