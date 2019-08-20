package com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.logging.LogManager;

public class DropBehavior extends DefaultResponseBehavior {

	private static final String MODULE = "DROP-RESPONSE-BEHAVIOR";

	@Override
	public void apply(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Applying default response behavior DROP for policy " + applicationRequest.getApplicationPolicy().getPolicyName());
		}
		DiameterProcessHelper.dropResponse(applicationResponse);
	}

}
