package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class ResponseAttributeAdditionHandler<T extends ApplicationRequest, V extends ApplicationResponse>
implements DiameterApplicationHandler<T, V>, ReInitializable {
	
	private static final String MODULE = "RES-ATTR-ADD-HNDLR";
	private DiameterServicePolicyConfiguration policyConfiguration;
	private DiameterServiceContext serviceContext;
	private String policyId;
	
	public ResponseAttributeAdditionHandler(DiameterServiceContext serviceContext, DiameterServicePolicyConfiguration policyConfiguration) {
		this.serviceContext = serviceContext;
		this.policyConfiguration = policyConfiguration;
	}
	
	public ResponseAttributeAdditionHandler(DiameterServiceContext serviceContext, String policyId) {		
		this(serviceContext, serviceContext.getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(policyId));
		this.policyId = policyId;
	}

	@Override
	public void init() throws InitializationFailedException {

	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		this.policyConfiguration = serviceContext.getDiameterServiceConfigurationDetail().getDiameterServicePolicyConfiguration(policyId);
		init();
	}
	
	public boolean isEligible(T request, V response) {
		return true;
	}

	private ArrayList<IDiameterAVP> addResponseAvps (ApplicationRequest request, 
			ApplicationResponse response, AdditionalResponseAttributes additionalResponseAttributes) throws AdditionalAttributeFailed {

		ArrayList<IDiameterAVP> avpContainer = new ArrayList<IDiameterAVP>();
		addResponseAvps(additionalResponseAttributes.getHardcodedAttributeMap(), new HardCodedValueProvider(), avpContainer);
		addResponseAvps(additionalResponseAttributes.getAttributeFromRequest(), new RequestValueProvider(request), avpContainer);
		addResponseAvps(additionalResponseAttributes.getAttributeFromResponse(), new ResponseValueProvider(response), avpContainer);
		addResponseAvps(additionalResponseAttributes.getGroupAttributes(), response, request, avpContainer);
		return avpContainer;
	}

	private void addResponseAvps(Map<String, List<String>> responseAttributeMap, 
			ValueProvider valueProvider, List<IDiameterAVP> avpsContainer) throws AdditionalAttributeFailed {

		if(responseAttributeMap!=null){
			String attributeId;
			String attributeValue;
			for(Map.Entry<String, List<String>> entry:responseAttributeMap.entrySet()){
				attributeId  = entry.getKey();

				for (String attributeDefaultValue : entry.getValue()) {
					attributeValue = valueProvider.getStringValue(attributeDefaultValue);
				if(attributeValue == null){
					throw new AdditionalAttributeFailed(attributeId +", as mapped attribute : "+entry.getValue() + 
							" not found in request or response");
				}
				IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(attributeId);

				if(diameterAVP == null) {
					throw new AdditionalAttributeFailed("Attribute: " + attributeId + 
							" does not exist in dictionary");
				}
				try {
					diameterAVP.setStringValue(attributeValue);
					avpsContainer.add(diameterAVP);
				} catch (Throwable e) {
					throw new AdditionalAttributeFailed("Error in assigning value: " + attributeValue + 
							" to AVP: " + diameterAVP.getAVPId() + ", Reason: " + e.getMessage());
				}
			}
		}
	}
	}

	private void addResponseAvps(Map<String, AdditionalResponseAttributes> responseAttributeMap, 
			ApplicationResponse response, ApplicationRequest request, List<IDiameterAVP> avpsContainer) throws AdditionalAttributeFailed {

		if(responseAttributeMap == null || responseAttributeMap.size() == 0){
			return;
		}
		for(Map.Entry<String, AdditionalResponseAttributes> entry:responseAttributeMap.entrySet()){
			AvpGrouped groupedAvp = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(entry.getKey());
			if(groupedAvp == null){
				throw new AdditionalAttributeFailed("Attribute: " + entry.getKey() + 
						" does not exist in dictionary");
			}
			ArrayList<IDiameterAVP> subAVPs = addResponseAvps(request, response, entry.getValue());
			if(Collectionz.isNullOrEmpty(subAVPs)){
				throw new AdditionalAttributeFailed("Value for Attribute: " + entry.getKey() + 
						" not found");
			}
			groupedAvp.setGroupedAvp(subAVPs);
			avpsContainer.add(groupedAvp);
		}
	}

	@Override
	public void handleRequest(T request, V response, ISession session){
		AdditionalResponseAttributes additionalResponseAttributes = policyConfiguration.getCommandCodeResponseAttributesMap()
				.get(response.getDiameterAnswer().getCommandCode());
		if(additionalResponseAttributes == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "No Response Attributes configured for Command-Code: " + 
						response.getDiameterAnswer().getCommandCode());
			}
			return;
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Applying Response Attributes for Command-Code: " + 
					response.getDiameterAnswer().getCommandCode());
		}
		try {
			List<IDiameterAVP> avps = addResponseAvps(request, response, additionalResponseAttributes);
			for (IDiameterAVP avp : avps) {
				if(response.getAVP(avp.getAVPId()) == null) {
					response.addAVP(avp);
				}
			}
		} catch (AdditionalAttributeFailed e) {
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Sending: " + ResultCode.DIAMETER_UNABLE_TO_COMPLY + 
						", Reason: Could not add Response Attributes " + e.getMessage());
			} 
			IDiameterAVP resultCodeAvp = response.getAVP(DiameterAVPConstants.RESULT_CODE);						
			if(resultCodeAvp == null) {
				resultCodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
				resultCodeAvp.setInteger(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code);
				response.addAVP(resultCodeAvp);
			} else {
				resultCodeAvp.setInteger(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code);
			}
			response.setFurtherProcessingRequired(false);
		}
	}

	static class RequestValueProvider implements ValueProvider {

		private ApplicationRequest applicationRequest;

		public RequestValueProvider(ApplicationRequest applicationRequest) {
			this.applicationRequest = applicationRequest;
		}

		@Override
		public String getStringValue(String identifier) {
			if(identifier==null)
				return null;
			IDiameterAVP diameterAVP = applicationRequest.getAVP(identifier,true);
			if(diameterAVP!=null){
				return diameterAVP.getStringValue();
			}
			return null;
		}

	}

	static class ResponseValueProvider implements ValueProvider {

		private ApplicationResponse applicationResponse;

		public ResponseValueProvider(ApplicationResponse applicationResponse) {
			this.applicationResponse = applicationResponse;
		}

		@Override
		public String getStringValue(String identifier) {
			if(identifier==null)
				return null;
			IDiameterAVP diameterAVP = applicationResponse.getAVP(identifier,true);
			if(diameterAVP!=null){
				return diameterAVP.getStringValue();
			}
			return null;
		}

	}

	static class HardCodedValueProvider implements ValueProvider {

		@Override
		public String getStringValue(String identifier) {
			return identifier;
		}

	}

	static class AdditionalAttributeFailed extends Exception {

		public AdditionalAttributeFailed(String message) {
			super(message);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}
