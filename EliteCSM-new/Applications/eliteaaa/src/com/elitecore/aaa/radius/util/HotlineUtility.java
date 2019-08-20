package com.elitecore.aaa.radius.util;

import java.util.List;

import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class HotlineUtility {
	private static final String MODULE = "HOTLINE-UTL";

	public static void applyHotline(RadAuthRequest request,RadAuthResponse response,String strHotlinePolicy, boolean bRejectOnCheckItemNotFound,boolean bRejectOnRejectItemNotFound) throws ParserException,PolicyFailedException{
		try {
			
			List<String> satisfiedHotlinePolicies= null;
			satisfiedHotlinePolicies = RadiusPolicyManager.getInstance(RadiusConstants.RADIUS_AUTHORIZATION_POLICY).applyPolicies(request,response,strHotlinePolicy, response.getClientData().getVendorType(), null, null, null, bRejectOnCheckItemNotFound, bRejectOnRejectItemNotFound, false,true);
			if(satisfiedHotlinePolicies != null && satisfiedHotlinePolicies.size() > 0) {
				request.setParameter(AAAServerConstants.SATISFIED_HOTLINE_POLICIES, satisfiedHotlinePolicies);
				List<IRadiusAttribute> 	packetAttributes = (List<IRadiusAttribute>) response.getRadiusAttributes(true);
				response.removeAllAttributes(packetAttributes, true);
				RadiusPolicyManager.getInstance(RadiusConstants.RADIUS_AUTHORIZATION_POLICY).applyReplyItems(request, response,satisfiedHotlinePolicies, null,bRejectOnCheckItemNotFound, bRejectOnRejectItemNotFound, false);
			}
			
			IRadiusAttribute hotlineAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_SATISFIED_HOTLINE_POLICIES);
			if(hotlineAttribute != null) {
				if(satisfiedHotlinePolicies != null && satisfiedHotlinePolicies.size() > 0) {
					String satisfiedPolicyStr = "";
					for(int i=0;i<satisfiedHotlinePolicies.size();i++) {
						if(i == satisfiedHotlinePolicies.size()-1)
							satisfiedPolicyStr = satisfiedPolicyStr + satisfiedHotlinePolicies.get(i);
						else
							satisfiedPolicyStr = satisfiedPolicyStr + satisfiedHotlinePolicies.get(i) + ",";
					}
					hotlineAttribute.setStringValue(satisfiedPolicyStr);
					response.addInfoAttribute(hotlineAttribute);
					response.setParameter(RadiusConstants.HOTLINE_APPLICABLE, RadiusConstants.PROFILE_BASED_HOTLINING);
					LogManager.getLogger().info(MODULE, "Hotline policy attribute added to response packet");
				} else {
					LogManager.getLogger().info(MODULE, "Hotline policy attribute not found in Elitecore dictionary");
				}
			}
		} catch (ParserException e) {
			LogManager.getLogger().info(MODULE, "Hotline policy: " + strHotlinePolicy + " failed during applying");
			throw e;
		} catch (PolicyFailedException e) {
			LogManager.getLogger().info(MODULE, "Hotline policy: " + strHotlinePolicy + " failed during applying");
			throw e;
		}
	}	
}
