package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.HandlerConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "post-response-handler")
@ValidObject
public class DiameterPostResponseHandlerData extends DiameterApplicationHandlerDataSupport implements Validator {

	@XmlElementRefs({
			@XmlElementRef(name = "cdr-generation", type = DiameterCDRGenerationHandlerData.class),
			@XmlElementRef(name = "plugin-handler", type = DiameterPluginHandlerData.class),
			@XmlElementRef(name = "authentication-handler", type = DiameterAuthenticationHandlerData.class),
			@XmlElementRef(name = "authorization-handler", type = DiameterAuthorizationHandlerData.class),
			@XmlElementRef(name = "user-profile-repository", type = DiameterSubscriberProfileRepositoryDetails.class),
			@XmlElementRef(name = "proxy-handler", type = DiameterSynchronousCommunicationHandlerData.class),
			@XmlElementRef(name = "broadcast-handler", type = DiameterBroadcastCommunicationHandlerData.class),
			@XmlElementRef(name = "dia-concurrency-handler", type = DiameterConcurrencyHandlerData.class)
	})
	
	@Valid
	private List<DiameterApplicationHandlerData> handlersData = new ArrayList<DiameterApplicationHandlerData>();

	@Valid
	public List<DiameterApplicationHandlerData> getHandlersData() {
		return handlersData;
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

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if (Collectionz.isNullOrEmpty(getHandlersData())) {
			return isValid;
		}
		
		for (DiameterApplicationHandlerData handler : getHandlersData()) {
			if (handler instanceof DiameterCDRGenerationHandlerData) {
				boolean isHandlerNameValid = handleValidation(handler, HandlerConstants.CDR_HANDLER, context);
				if(isHandlerNameValid == false){
					isValid = false;
				}
			} else if (handler instanceof DiameterPluginHandlerData) {
				boolean isHandlerNameValid = handleValidation(handler, HandlerConstants.PLUGIN_HANDLER, context);
				if(isHandlerNameValid == false){
					isValid = false;
				}
			} else if (handler instanceof DiameterAuthenticationHandlerData) {
				RestUtitlity.setValidationMessage(context, "Authentication Handler is not supported in post response of command code flow");
				isValid = false;
			} else if (handler instanceof DiameterAuthorizationHandlerData) {
				RestUtitlity.setValidationMessage(context, "Authorization Handler is not supported in post response of command code flow");
				isValid = false;
			} else if (handler instanceof DiameterConcurrencyHandlerData) {
				RestUtitlity.setValidationMessage(context, "Concurrency Handler is not supported in post response of command code flow");
				isValid = false;
			} else if (handler instanceof DiameterSubscriberProfileRepositoryDetails) {
				RestUtitlity.setValidationMessage(context, "Profile Look Up Handler is not supported in post response of command code flow");
				isValid = false;
			} else if (handler instanceof DiameterSynchronousCommunicationHandlerData) {
				RestUtitlity.setValidationMessage(context, "Proxy(Serial) Handler is not supported in post response of command code flow");
				isValid = false;
			} else if (handler instanceof DiameterBroadcastCommunicationHandlerData) {
				RestUtitlity.setValidationMessage(context, "Broadcast(Parellel) Handler is not supported in post response of command code flow");
				isValid = false;
			}
		}
		return isValid;
	}

	private boolean handleValidation(DiameterApplicationHandlerData handler, String handlerType, ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if( Strings.isNullOrBlank(handler.getHandlerName()) ) {
			RestUtitlity.setValidationMessage(context, handlerType + " name must be specified in Post Response Handler Flow.");
			isValid = false;
		}   
		Pattern BOOLEAN_REGEX = Pattern.compile(RestValidationMessages.BOOLEAN_REGEX);

		if (Strings.isNullOrBlank(handler.getEnabled())) {
			RestUtitlity.setValidationMessage(context, "[ " + handlerType + " ] Handler's enabled/disable value must be specified in Post Response Handler Flow.");
			isValid = false;
		}else if(BOOLEAN_REGEX.matcher(handler.getEnabled()).matches() == false){

			RestUtitlity.setValidationMessage(context,
					"Invalid value of enabled/disable field of [ " + handlerType+" ] handler in Post Response Handler Flow. It could be 'true' or 'false'.");
			isValid = false;
		}
		return isValid;
	}
}
