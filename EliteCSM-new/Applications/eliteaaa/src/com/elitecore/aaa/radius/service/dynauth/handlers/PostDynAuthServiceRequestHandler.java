package com.elitecore.aaa.radius.service.dynauth.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthRequest;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthResponse;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthServiceContext;
import com.elitecore.aaa.radius.service.handlers.BasePostRequestHandler;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.DynAuthErrorCode;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class PostDynAuthServiceRequestHandler extends BasePostRequestHandler<RadDynAuthRequest, RadDynAuthResponse> implements RadDynAuthServiceHandler {
	
	private static final String MODULE = "POST-DYNAUTH-REQUEST-HANDLER";
	
	public PostDynAuthServiceRequestHandler(ServiceContext serviceContext,
			String policyId) {
		super(serviceContext, policyId);
	}
	
	@Override
	public boolean isEligible(RadDynAuthRequest request,
			RadDynAuthResponse response) {
		return true;
	}

	private void actionOnDefaultValueNotFoundForResponseAttribute(RadServiceRequest request,RadServiceResponse response){
		
	
		response.setFurtherProcessingRequired(false);
		
		if(request.getPacketType() == RadiusConstants.COA_REQUEST_MESSAGE){
        	response.setPacketType(RadiusConstants.COA_NAK_MESSAGE);
		} else {
			response.setPacketType(RadiusConstants.DISCONNECTION_NAK_MESSAGE);
		}
		IRadiusAttribute radiusAttribute;
		radiusAttribute =Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.ERROR_CAUSE);
		 if(radiusAttribute!=null){
			 radiusAttribute.setIntValue(DynAuthErrorCode.MissingAttribute.value);
			 response.addAttribute(radiusAttribute);
		 }else{
			 if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				 LogManager.getLogger().debug(MODULE, "Error-Cause Attribute not added to respnse packet , Reason: Attribute not found");
		 }
		 radiusAttribute = request.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		 if(radiusAttribute!=null)
			 response.addAttribute(radiusAttribute);
		 List<IRadiusAttribute> proxyStateAttributes = (ArrayList<IRadiusAttribute>)request.getRadiusAttributes(RadiusAttributeConstants.PROXY_STATE);
		 if(proxyStateAttributes!=null && proxyStateAttributes.size()>0){
			int size = proxyStateAttributes.size();
			for(int j=0;j<size;j++){
				response.addAttribute(proxyStateAttributes.get(j));
			}
		 }
			
	}
	@Override
	public void reInit() throws InitializationFailedException {
		init();
	}
	@Override
	protected String getConfiguredResponseAttribute() {
		return ((RadDynAuthServiceContext)getServiceContext()).getDynAuthConfiguration().getDynAuthServicePolicyConfiguraion(getPolicyId()).getResponseAttributeStr();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void applyResponseAttributes(RadServiceRequest request,RadServiceResponse response, ValueProvider valueProvider,Map<String, List<String>> responseAttributes) {
		Map<String, List<String>> responseAttributeMap = responseAttributes;
		HashMap<String, List<String>> tempResponseAttrMap = (HashMap<String, List<String>>)((HashMap<String, List<String>>)responseAttributeMap).clone();
		IRadiusAttribute responseAttribute=null;
		String strAttributeId = null;
		for(Entry<String, List<String>> attribute :tempResponseAttrMap.entrySet()){
			strAttributeId = attribute.getKey();
			for (String attributeDefaultValue : attribute.getValue()) {
				String value = valueProvider.getStringValue(attributeDefaultValue);

				if(value!=null){
					responseAttribute = Dictionary.getInstance().getKnownAttribute(strAttributeId);
					if(responseAttribute !=null){
						responseAttribute.setStringValue(value);
						response.addAttribute(responseAttribute);
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Attribute for Attribute-ID: "+strAttributeId+" not added to response packet.Reason : Attribute not found in dictionary");
					}
				}else{
					actionOnDefaultValueNotFoundForResponseAttribute(request, response);
					return;
				}
			}
		}
	}

	@Override
	public void handleRequest(RadDynAuthRequest request,RadDynAuthResponse response, ISession session) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Post dynauth service request handling started");
		}
		super.handleRequest(request, response, session);
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

}
