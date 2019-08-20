package com.elitecore.aaa.radius.service.base.policy.handler;

import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.handlers.RadServiceHandler;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

/**
 * 
 * @author narendra.pathai
 *
 * @param <T>
 * @param <V>
 */
public abstract class ResponseAttributeAdditionHandlerSupport<T extends RadServiceRequest, V extends RadServiceResponse> implements RadServiceHandler<T,V> {
	protected static final String MODULE = "RES-ATTR-ADD-HNDLR";
	
	private AdditionalResponseAttributes additionalResponseAttributes;

	@Override
	public void init() throws InitializationFailedException {
		additionalResponseAttributes = new AdditionalResponseAttributes(getConfiguredResponseAttribute());
	}

	public void handleRequest(T request, V response, ISession session) {
		applyResponseAttributes(request, response, new LiteralValueProvider(), additionalResponseAttributes.getHardcodedAttributeMap());
		if(response.isFurtherProcessingRequired()){
			applyResponseAttributes(request, response, new RequestValueProvider(request), additionalResponseAttributes.getAttributeFromRequest());
		}
		if(response.isFurtherProcessingRequired()){
			applyResponseAttributes(request, response, new ResponseValueProvider(response), additionalResponseAttributes.getAttributeFromResponse());
		}
	}
	
	protected abstract void applyResponseAttributes(RadServiceRequest request,RadServiceResponse response,ValueProvider valueProvider,Map<String, List<String>> responseAttributes);
	protected abstract String getConfiguredResponseAttribute();

	class RequestValueProvider implements ValueProvider{

		private RadServiceRequest radServiceRequest;

		public RequestValueProvider(RadServiceRequest request) {
			this.radServiceRequest = request;
		}

		@Override
		public String getStringValue(String identifier) {
			if(identifier==null)
				return null;
			IRadiusAttribute radiusAttribute = radServiceRequest.getRadiusAttribute(identifier,true);
			if(radiusAttribute!=null){
				return radiusAttribute.getStringValue();
			}
			return null;
		}

	}

	class ResponseValueProvider implements ValueProvider {

		private RadServiceResponse radServiceResponse;

		public ResponseValueProvider(RadServiceResponse response) {
			this.radServiceResponse = response;
		}

		@Override
		public String getStringValue(String identifier) {
			if(identifier == null){
				return null;
			}
			IRadiusAttribute radiusAttribute = radServiceResponse.getRadiusAttribute(identifier);
			if(radiusAttribute != null){
				return radiusAttribute.getStringValue();
			}
			return null;
		}

	}

	class LiteralValueProvider implements ValueProvider {

		@Override
		public String getStringValue(String identifier) {
			return identifier;
		}

	}
}
