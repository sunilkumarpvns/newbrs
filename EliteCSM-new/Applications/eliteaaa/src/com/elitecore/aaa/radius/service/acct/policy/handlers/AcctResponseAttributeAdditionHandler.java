package com.elitecore.aaa.radius.service.acct.policy.handlers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.ResponseAttributeAdditionHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.ResponseAttributeAdditionHandlerSupport;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AcctResponseAttributeAdditionHandler extends ResponseAttributeAdditionHandlerSupport<RadAcctRequest, RadAcctResponse> implements RadAcctServiceHandler {

	private final ResponseAttributeAdditionHandlerData data;

	public AcctResponseAttributeAdditionHandler(ResponseAttributeAdditionHandlerData data) {
		this.data = data;
	}

	@Override
	public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
		return true;
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}

	@Override
	protected void applyResponseAttributes(RadServiceRequest request,
			RadServiceResponse response, ValueProvider valueProvider,
			Map<String, List<String>> responseAttributes) {

		IRadiusAttribute responseAttribute=null;
		String strAttributeId = null;
		for(Entry<String, List<String>> attribute : responseAttributes.entrySet()){
			strAttributeId = attribute.getKey();
			for (String attributeDefaultValue : attribute.getValue()) {
				String value = valueProvider.getStringValue(attributeDefaultValue);

				if(value != null){
				responseAttribute = Dictionary.getInstance().getKnownAttribute(strAttributeId);

				if(responseAttribute !=null){
						responseAttribute.setStringValue(value);
					response.addAttribute(responseAttribute);

				}else{
						if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Attribute for Attribute-ID: "+strAttributeId+" not added to response packet.Reason : Attribute not found in dictionary");
				}
			}
		}
	}
		}
	}

	@Override
	protected String getConfiguredResponseAttribute() {
		return data.getRadiusServicePolicyData().getAcctResponseAttributes();
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}

