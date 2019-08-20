package com.elitecore.netvertex.gateway.diameter.application.handler.tgpp;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.transaction.Transaction;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;

public class TGPPGyAppHandler implements ApplicationHandler  {

	private static final String MODULE = "3GPP-GY-APP-HDLR";
	private DiameterGatewayControllerContext context;
	
	public TGPPGyAppHandler(@Nonnull DiameterGatewayControllerContext context) {
		this.context = context;
	}
	
	@Override
	public void handleReceivedResponse(Session session, DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) {
	}

	@Override
	public void handleTimeoutRequest(Session session,
			DiameterRequest diameterRequest) {
	}

	@Override
	public void handleReceivedRequest(Session session, DiameterRequest request, DiameterAnswerListener diameterAnswerListener) {

		if (CommandCode.CREDIT_CONTROL.code != request.getCommandCode()) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Skipping further processing. Reason: Unsupported Gy request");
			}
			return;
		}

		IDiameterAVP requestType = request.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);

		GyTransactionType transactionType = GyTransactionType.from((int) requestType.getInteger());

		Transaction transaction = context.createTransaction(transactionType);

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Created " + transactionType + " transaction for " + transactionType.eventType + " transaction event");
		}

		transaction.process(transactionType.eventType, request, diameterAnswerListener);
	}

	@Override
	public void handleReceivedRequest(Session session, DiameterRequest diameterRequest) {
		throw new UnsupportedOperationException();
	}
}
