package com.elitecore.aaa.radius.service.base.policy.handler;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler.ProcessingStrategy;

/**
 * This Strategy is used to check whether further execution for request is required or not. 
 * If any CoA-DM filter is selected, then it will skip further CoA-DM filters.
 * 
 * @author kuldeep.panchal
 * @author narendra.pathai
 *
 */
public class FirstCoADMFilterSelectedStrategy implements ProcessingStrategy {
	static final String CoA_DM_FILTER_SELECTED = "CoA_DM_FILTER_SELECTED";
	
	@Override
	public boolean shouldContinue(RadServiceRequest request,
			RadServiceResponse response) {
		Boolean isCoADMFilterSelected = (Boolean)request.getParameter(CoA_DM_FILTER_SELECTED);
		return isCoADMFilterSelected == null;
	}
}
