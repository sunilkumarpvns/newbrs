package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class SendFailureAckToAFAction extends ActionHandler {
	private static final String NAME = "SEND-FAILURE-ACK-TO-AF";
	private static final String MODULE = "SEND-FAILURE-ACK-TO-AF";
	
	public SendFailureAckToAFAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {	
		return NAME;
	}

	@Override
	public TransactionState handle() {
		PCRFResponse pcrfResponse = getPCRFResponse();
		
		DiameterRequest diameterRequest = getDiameterRequest();
		if(pcrfResponse == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Sending AAA with REQUESTED_SERVICE_NOT_AUTHORIZED(5063). Reason: No PCRF response found from Transaction");
			
			AvpGrouped experimentalResultAVP = createExperimentalResultCodeForUnAuthorizedRequest();
			DiameterAnswer answer = new DiameterAnswer(diameterRequest);
			answer.addAvp(experimentalResultAVP);
			getTransactionContext().sendAnswer(answer,diameterRequest);
			return TransactionState.COMPLETE;
		}
		
		// when negative result-code received from pcrf service processing
		if(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val)) == false) {
			DiameterAnswer answer = new DiameterAnswer(diameterRequest); 
			getTransactionContext().getControllerContext().buildAAA(pcrfResponse, answer);
			getTransactionContext().sendAnswer(answer,diameterRequest);			
		} else {
			// check negative RAA received or RAR timeout
			DiameterPacket raaPacket = getTransactionContext().getTransactionSession().get(SessionKeys.RAA_PACKET);
			if(raaPacket != null) {
				IDiameterAVP resultCodeAVP = raaPacket.getAVP(DiameterAVPConstants.RESULT_CODE);
				if(resultCodeAVP != null && resultCodeAVP.getInteger() == ResultCode.DIAMETER_UNKNOWN_SESSION_ID.code) {
					AvpGrouped experimentalResultAVP = createExperimentalResultCodeForIpCanSessionNotAvailable();
					DiameterAnswer answer = new DiameterAnswer(diameterRequest);
					answer.addAvp(experimentalResultAVP);
					getTransactionContext().sendAnswer(answer,diameterRequest);
				} else {
					AvpGrouped experimentalResultAVP = createExperimentalResultCodeForUnAuthorizedRequest();
					DiameterAnswer answer = new DiameterAnswer(diameterRequest);
					answer.addAvp(experimentalResultAVP);
					getTransactionContext().sendAnswer(answer,diameterRequest);
				}
			} else {
				pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.val, PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
				DiameterAnswer answer = new DiameterAnswer(diameterRequest);
				getTransactionContext().getControllerContext().buildAAA(pcrfResponse, answer);
				getTransactionContext().sendAnswer(answer,diameterRequest);
			}	
		}
				
		return TransactionState.COMPLETE;
	}


	private AvpGrouped createExperimentalResultCodeForUnAuthorizedRequest() {
		AvpGrouped experimentalResultAVP = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT);
		
		IDiameterAVP vendoerIdAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.VENDOR_ID);
		vendoerIdAVP.setInteger(ResultCode.DIAMETER_REQUESTED_SERVICE_NOT_AUTHORIZED.vendorId);
		experimentalResultAVP.addSubAvp(vendoerIdAVP);
		
		IDiameterAVP experimentalResultCodeAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE);
		experimentalResultCodeAVP.setInteger(ResultCode.DIAMETER_REQUESTED_SERVICE_NOT_AUTHORIZED.code);
		experimentalResultAVP.addSubAvp(experimentalResultCodeAVP);
		return experimentalResultAVP;
	}
	
	
	private AvpGrouped createExperimentalResultCodeForIpCanSessionNotAvailable() {
		AvpGrouped experimentalResultAVP = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT);
		
		IDiameterAVP vendoerIdAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.VENDOR_ID);
		vendoerIdAVP.setInteger(ResultCode.DIAMETER_IP_CAN_SESSION_NOT_AVAILABLE.vendorId);
		experimentalResultAVP.addSubAvp(vendoerIdAVP);
		
		IDiameterAVP experimentalResultCodeAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE);
		experimentalResultCodeAVP.setInteger(ResultCode.DIAMETER_IP_CAN_SESSION_NOT_AVAILABLE.code);
		experimentalResultAVP.addSubAvp(experimentalResultCodeAVP);
		return experimentalResultAVP;
	}
}
