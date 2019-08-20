package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.transaction.MappingFailedException;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

public class GySessionStopAction extends ActionHandler {

	private static final String MODULE = "GY-SESS-STOP-ACTN";

	public GySessionStopAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return MODULE;
	}

	@Override
	public TransactionState handle() {

		DiameterRequest diameterRequest = getDiameterRequest();
		DiameterGatewayControllerContext controllerContext = getTransactionContext().getControllerContext();
		//FIXME NEED TO REFACTOR THIS CODE, AS WE HAVE ALREADY FETCH THE GATWEAYCONFIGURATION WHEN IN xxxHandler() or xxxApplicatin class
		DiameterGatewayConfiguration configuration = controllerContext.getGatewayConfiguration(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME) ,
				diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

		if(configuration == null){
			configuration = controllerContext.getGatewayConfigurationByHostId(diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		}
		
		PCRFRequest pcrfRequest;

		try {
			pcrfRequest =createPCRFRequest(configuration.getGyCCRMappings(), configuration);

		}catch(MappingFailedException e){

			sendRejectResponse(e.getErrorCode(), configuration);
			LogManager.getLogger().trace(MODULE, e);
			return TransactionState.COMPLETE;
		}

		RequestStatus requestStatus = getTransactionContext().submitPCRFRequest(pcrfRequest);
		if (requestStatus != RequestStatus.SUBMISSION_SUCCESSFUL) {
			sendRejectResponse(pcrfRequest);
			return TransactionState.COMPLETE;
		}

		return TransactionState.WAIT_FOR_AUTH_RES;
	}
}
