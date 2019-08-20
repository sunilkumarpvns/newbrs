package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.session.SessionSort;
import com.elitecore.netvertex.core.session.SessionSortOrder;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.transaction.MappingFailedException;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.service.pcrf.DiagnosticKey;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class GyAuthorizeAction extends ActionHandler {
	private static final String MODULE = "GY-AUTHORIZE-ACTN";
	private static final Predicate<SessionData> sessionGxDataPredicate = data -> SessionTypeConstant.GX.getVal().equals(data.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()));
	private static final Predicate<SessionData> sessionRadiusDataPredicate = data -> SessionTypeConstant.RADIUS.getVal().equals(data.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()));
	private static final Predicate<SessionData> sessionPredicate = sessionGxDataPredicate.or(sessionRadiusDataPredicate);

	public GyAuthorizeAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return MODULE;
	}

	private void setSessionTypeAndService(PCRFRequest pcrfRequest, DiameterGatewayConfiguration configuration ){

		String service = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SERVICE.val);

		if(Strings.isNullOrBlank(service) == true) {

			if (GatewayComponent.APPLICATION_FUNCTION == configuration.getGatewayType()) {
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.RO.val);
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, CommonConstants.VOICE_SERVICE_ALIAS);
			} else {
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, CommonConstants.DATA_SERVICE_ALIAS);
			}
		}
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
			pcrfRequest = createPCRFRequest(configuration.getGyCCRMappings(), configuration);

		}catch(MappingFailedException e){
			LogManager.getLogger().trace(MODULE, e);
			sendRejectResponse(e.getErrorCode(), configuration);

			return TransactionState.COMPLETE;
		}

		setSessionTypeAndService(pcrfRequest, configuration);

		locateGXSession(controllerContext, pcrfRequest);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "PCRF Request : " + pcrfRequest);
		}

		RequestStatus requestStatus = getTransactionContext().submitPCRFRequest(pcrfRequest);

		if (requestStatus != RequestStatus.SUBMISSION_SUCCESSFUL) {
			sendRejectResponse(pcrfRequest);
			return TransactionState.COMPLETE;
		}

		return TransactionState.WAIT_FOR_AUTH_RES;
	}

	private void locateGXSession(DiameterGatewayControllerContext controllerContext, PCRFRequest pcrfRequest){


		String ipV4 = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.getVal());

		SessionSort sessionSort = new SessionSort(sessionPredicate, SessionSortOrder.DESCENDING);
		if (Objects.nonNull(ipV4)) {
			long lookupStartTime = getTransactionContext().getTimeSource().currentTimeInMillis();
			controllerContext.getSessionLocator().getCoreSessionBySessionIPv4(ipV4, sessionSort);
			long loadtime = getTransactionContext().getTimeSource().currentTimeInMillis() - lookupStartTime;
			pcrfRequest.addDiagnosticInformation(DiagnosticKey.CO_RELATED_SESSION_IPV4_LOAD, String.valueOf(loadtime));
		}

		if(CollectionUtils.isEmpty(sessionSort.getList())){
			long lookupStartTimeOnIpV6 = getTransactionContext().getTimeSource().currentTimeInMillis();
			String ipV6 = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_IPV6.getVal());
			if (Objects.nonNull(ipV6)) {
				controllerContext.getSessionLocator().getCoreSessionBySessionIPv6(ipV6, sessionSort);
			}
			long loadtime = getTransactionContext().getTimeSource().currentTimeInMillis() - lookupStartTimeOnIpV6;
			pcrfRequest.addDiagnosticInformation(DiagnosticKey.CO_RELATED_SESSION_IPV6_LOAD, String.valueOf(loadtime));
		}


		if (CollectionUtils.isEmpty(sessionSort.getList())) {
			return;
		}


		if(Objects.nonNull(sessionSort.getList().get(0))) {
			SessionData sessionData = sessionSort.getList().get(0);
			String json = sessionData.getValue(PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.getVal());
			RatingGroupSelectionState pccProfileSelectionState = RatingGroupSelectionState.fromJson(controllerContext.getServerContext().getPolicyRepository(), json);
			pcrfRequest.setPCCProfileSelectionState(pccProfileSelectionState);
			if(Objects.isNull(ipV4)) {
				pcrfRequest.addDiagnosticInformation(DiagnosticKey.CO_RELATED_SESSION_IPV6_LOAD, String.valueOf(sessionData.getSessionLoadTime()));
			} else {
				pcrfRequest.addDiagnosticInformation(DiagnosticKey.CO_RELATED_SESSION_IPV4_LOAD, String.valueOf(sessionData.getSessionLoadTime()));
			}

			if (SessionTypeConstant.RADIUS.getVal().equals(sessionData.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))) {
				pcrfRequest.setAttribute(PCRFKeyConstants.RADIUS_SESSION_ID.val, sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val));
			} else {
				pcrfRequest.setAttribute(PCRFKeyConstants.GX_SESSION_ID.val, sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val));
			}

		}
	}
}
