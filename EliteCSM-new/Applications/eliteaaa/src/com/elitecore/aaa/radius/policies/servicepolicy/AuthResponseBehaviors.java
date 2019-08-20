package com.elitecore.aaa.radius.policies.servicepolicy;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.policy.AuthServicePolicy;
import com.elitecore.aaa.radius.service.auth.policy.conf.AuthenticationPolicyData;
import com.elitecore.aaa.radius.util.HotlineUtility;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CaseInsensitiveEnumAdapter;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlEnum
public enum AuthResponseBehaviors {
	
	@XmlEnumValue(value = "REJECT")
	REJECT(0) {
		@Override
		public void apply(AuthServicePolicy servicePolicy, RadAuthRequest request, RadAuthResponse response) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Applying response behavior: " 
						+ "REJECT for service policy : " + servicePolicy.getPolicyName() + ".");
			}
			
			RadiusProcessHelper.rejectResponse(response, AuthReplyMessageConstant.USER_NOT_FOUND);
		}
	},
	
	@XmlEnumValue(value = "DROP")
	DROP(1) {
		@Override
		public void apply(AuthServicePolicy servicePolicy, RadAuthRequest request, RadAuthResponse response) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Applying response behavior: " +
						"DROP for service policy : " + servicePolicy.getPolicyName() + ".");
			}
			
			RadiusProcessHelper.dropResponse(response);
		}
	},
	
	@XmlEnumValue(value = "HOTLINE")
	HOTLINE(2) {
		@Override
		public void apply(AuthServicePolicy servicePolicy, RadAuthRequest request, RadAuthResponse response) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Applying response behavior: " +
						"HOTLINE for service policy : " + servicePolicy.getPolicyName() + ".");
			}
			
			AuthenticationPolicyData servicePolicyConfiguration = servicePolicy.getConfiguration();
			Optional<String> hotlinePolicy = servicePolicyConfiguration.getHotlinePolicy();
			if (Strings.isAbsentOrBlank(hotlinePolicy) == false) {
				try {
					HotlineUtility.applyHotline(request, response, hotlinePolicy.get(), true, true);
					response.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
					response.setResponseMessage(AuthReplyMessageConstant.HOTLINE_SUCCESS);
					response.setFurtherProcessingRequired(false);
				} catch (ParserException e) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "Error in parsing : "
								+ hotlinePolicy 
								+ " expression. So using default as REJECT behavior.");
					}
					REJECT.apply(servicePolicy, request, response);
				} catch (PolicyFailedException e) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "Failed to apply policy/s: "
								+ hotlinePolicy + ". So using default as REJECT behavior.");
					}
					REJECT.apply(servicePolicy, request, response);
				}
			} else {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Response behavior for service policy : "
							+ servicePolicy.getPolicyName() + 
							" is HOTLINE, but no/blank hotline policy name configured. " +
							"So using Default as REJECT behavior.");
				}
				REJECT.apply(servicePolicy, request, response);
			}
		}
	};
	
	private static final String MODULE = "AUTH-RESPONSE-BEHAVIORS";
	public final int value;
	private static final Map<Integer, AuthResponseBehaviors> map;
	private static final AuthResponseBehaviors[] VALUES = values();
	
	static {
		map = new HashMap<Integer, AuthResponseBehaviors>();
		for (AuthResponseBehaviors type: VALUES) {
			map.put(type.value, type);
		}
	}
	
	private AuthResponseBehaviors(int value) {
		this.value = value;
	}
	
	public abstract void apply(AuthServicePolicy servicePolicy, RadAuthRequest request, RadAuthResponse response);
	
	
	public static class AuthResponseBehaviorsXMLAdapter extends CaseInsensitiveEnumAdapter<AuthResponseBehaviors> {
		public AuthResponseBehaviorsXMLAdapter() {
			super(AuthResponseBehaviors.class, REJECT);
		}
	}
}
