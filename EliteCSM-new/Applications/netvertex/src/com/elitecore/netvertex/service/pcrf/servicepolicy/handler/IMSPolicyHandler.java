package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.pm.IMSPackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;

import java.util.ArrayList;

public class IMSPolicyHandler extends ServiceHandler{
	
	private static final String MODULE = "IMS-PLC-HDLR";
	
	public IMSPolicyHandler(PCRFServiceContext pcrfServiceContext) {
		super(pcrfServiceContext);
	}

	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		
		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;
		
		String imsPackageName = pcrfRequest.getSPRInfo().getImsPackage();
		
		if (Strings.isNullOrBlank(imsPackageName)) {
			if (getLogger().isErrorLogLevel())
				getLogger().error(MODULE, "Rejecting request. Reason: package information not found in subscriber profile");

			pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
			pcrfResponse.setFurtherProcessingRequired(false);
			return;
		}
		
		IMSPackage imsPackage = (IMSPackage) getServerContext().getPolicyRepository().getIMSPkgByName(imsPackageName);
		
		if(imsPackage == null) {
			if (getLogger().isErrorLogLevel())
				getLogger().error(MODULE, "Rejecting request. Reason: subscriber package:" + imsPackageName +" not found in policy repository");

			pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
			pcrfResponse.setFurtherProcessingRequired(false);
			return;
		}
		
		if (imsPackage.getStatus() == PolicyStatus.FAILURE) {
			if (getLogger().isErrorLogLevel())
				getLogger().error(MODULE, "Rejecting request. Reason: subscriber package:" + imsPackageName +" is fail. Reason:" + imsPackage.getFailReason());

			pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
			pcrfResponse.setFurtherProcessingRequired(false);
			return;
		}
		
		
		imsPackage.apply(pcrfRequest, pcrfResponse, new ArrayList<MediaComponent>(pcrfResponse.getMediaComponents()));
		pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);
		
		if (getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "PCRFResponse after applying IMS package" + pcrfResponse);
	}

	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;

		return SessionTypeConstant.RX.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val)) && (pcrfRequest.getPCRFEvents().contains(PCRFEvent.AUTHORIZE)
				|| pcrfRequest.getPCRFEvents().contains(PCRFEvent.REAUTHORIZE));
	}

	@Override
	public String getName() {
		return MODULE;
	}

}
