package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.util.Collection;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NonExclusiveAddOnPolicyHandler extends AddOnPolicyHandler {

	private static final String MODULE = "NON-EX-ADDON-PLC-HDLR";
	
	public NonExclusiveAddOnPolicyHandler(NetVertexServerContext serverContext) {
		super(serverContext);
	}

	@Override
	public void applyPackage(PolicyContext policyContext, Collection<Subscription> nonExclusiveAddOnSubscriptions) {

		if (Collectionz.isNullOrEmpty(nonExclusiveAddOnSubscriptions)) {
			return;
		}


		if(isGyFlow(policyContext) == false && PCRFKeyValueConstants.PCC_LEVEL_MONITORING_NOT_SUPPORTED.val.equals(policyContext.getPCRFResponse().getAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val))) {
			if (getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Skipping non-exclusive addOn execution. Reason: AddOn is not exclusive addOn and gateway does not support pcc level metering");
			return;
		}
		
		super.applyPackage(policyContext, nonExclusiveAddOnSubscriptions);
		
	}

	private boolean isGyFlow(PolicyContext policyContext) {
		PCRFRequest pcrfRequest = policyContext.getPCRFRequest();
		return (SessionTypeConstant.GY.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val))
				|| SessionTypeConstant.RADIUS.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val)))
                && pcrfRequest.getPCRFEvents().contains(PCRFEvent.QUOTA_MANAGEMENT);
	}

}
