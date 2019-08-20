package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.transaction.MappingFailedException;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.gateway.diameter.transaction.session.TransactionSession;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;


/**
 * RemoveCoreSessionAction class create PCRFRequest from diameterRequest and submit it to PCRFService
 * @author harsh
 *
 */
public class RemoveCoreSessionAction extends ActionHandler {
	private static final String NAME = "REMOVE-CORE-SESSIONS";
	private static final String MODULE = "REMOVE-CORESESS-ACTN";
	public RemoveCoreSessionAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TransactionState handle() {
		DiameterRequest diameterRequest = getDiameterRequest();

		DiameterGatewayControllerContext controllerContext = getTransactionContext().getControllerContext();
		DiameterGatewayConfiguration configuration = controllerContext.getGatewayConfiguration(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME) ,
				diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

		if(configuration == null){
			configuration = controllerContext.getGatewayConfigurationByHostId(diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		}

		PCRFRequest pcrfRequest;

		try {
			pcrfRequest = createPCRFRequest(configuration.getGxCCRMappings(), configuration);

		}catch(MappingFailedException e){

			sendRejectResponse(e.getErrorCode(), configuration);
			LogManager.getLogger().trace(MODULE, e);
			return TransactionState.COMPLETE;
		}


		TransactionSession session = getTransactionContext().getTransactionSession();			
		String sessionType = session.get(SessionKeys.SESSION_TYPE);
		if(sessionType != null && sessionType.length() > 0){
			pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), sessionType);
			pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + ":" + sessionType);
		}			
		String val = session.get(SessionKeys.GATEWAY_TYPE);
		if(val != null && val.length() > 0){
			pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), val);				
		}			
		pcrfRequest.setAttribute(PCRFKeyConstants.TRANSACTION_ID.getVal(), String.valueOf(session.get(SessionKeys.TRANSACTION_ID)));
		// PCRF Request Created		
		RequestStatus requestStatus = getTransactionContext().submitPCRFRequest(pcrfRequest);
		if(requestStatus != RequestStatus.SUBMISSION_SUCCESSFUL){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "PCRF Request Submission failed. Reason:" +
						"PCRF Request status " + requestStatus.getVal());
			}
			return TransactionState.COMPLETE;
		}
		return TransactionState.WAIT_FOR_AUTH_RES;
	}

}
