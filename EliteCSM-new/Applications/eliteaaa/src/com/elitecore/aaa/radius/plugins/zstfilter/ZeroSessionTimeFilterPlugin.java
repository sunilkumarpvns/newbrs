package com.elitecore.aaa.radius.plugins.zstfilter;

import com.elitecore.aaa.radius.plugins.core.BaseRadPlugin;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class ZeroSessionTimeFilterPlugin extends BaseRadPlugin<RadServiceRequest, RadServiceResponse> {

	private static final String MODULE = "ZERO-SES-TM-FIL-PLG";
	public ZeroSessionTimeFilterPlugin(PluginContext pluginContext,
			PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
	}

	@Override
	public void handlePostRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		return;
	}

	@Override
	public void handlePreRequest(RadServiceRequest radServReq,
			RadServiceResponse radServRes, String argument, PluginCallerIdentity callerID, ISession session) {
		
			if(radServReq.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
			
				IRadiusAttribute acctStatusTypeAttrib = radServReq.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
				if(acctStatusTypeAttrib != null && acctStatusTypeAttrib.getIntValue() == RadiusAttributeValuesConstants.STOP){
				
					IRadiusAttribute sessionTimeAttrib = radServReq.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_TIME);
					if(sessionTimeAttrib != null && sessionTimeAttrib.getIntValue() <=0 ){
						LogManager.getLogger().debug(MODULE,"Acct-Session-Time with zero length received. Skipping further processing.");
						if(radServReq.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID) != null){
							radServRes.addAttribute(radServReq.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID));
						}
						radServRes.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
						radServRes.setFurtherProcessingRequired(false);
						radServRes.setProcessingCompleted(true);
						return;
					}
				}
			}
		
	}

	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Zero Session Time Filter Plugin Initialized successfuly.");		
	}

}
