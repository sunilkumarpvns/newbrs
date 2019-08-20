package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.policy.PackageSelectionState;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.policy.ServiceSelectionState;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class GySendCCAAction extends ActionHandler {

	private static final String MODULE = "GY-SND-CCA-ACTN";

	public GySendCCAAction(DiameterTransactionContext transactionContext) {
		super(transactionContext);
	}

	@Override
	public String getName() {
		return MODULE;
	}

	@Override
	public TransactionState handle() {
		PCRFResponse pcrfResponse = getPCRFResponse();
		PCRFRequest pcrfRequest = getPCRFRequest();
		
		DiameterRequest diameterRequest = getDiameterRequest();
		DiameterAnswer answer = new DiameterAnswer(diameterRequest);
		getTransactionContext().getControllerContext().buildCCA(pcrfResponse, answer);


		initiateGxReAuthorizationIfRequired(pcrfRequest, pcrfResponse);

		String resultCode = pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.val);

		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_START) == false || PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(resultCode)) {
			handleSession(pcrfRequest, pcrfResponse);
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Session handling skipped for Session-ID = "
						+ diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) + ". Reason: CCA Result code is not success");
			}
		}

		try {
			getDiameterAnswerListener().answerReceived(answer, diameterRequest);
		} catch (CommunicationException e) {
			getLogger().error(MODULE, "Unable to send Diameter request with Session-ID = "
					+ diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) + ". Reason :" + e.getMessage());
			getLogger().trace(e);
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Session handling skipped for Session-ID = "
						+ diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) + ". Reason: CCA Sending failed");
			}
			return TransactionState.COMPLETE;
		}

		return TransactionState.COMPLETE;
	}

	private void initiateGxReAuthorizationIfRequired(PCRFRequest pcrfRequest, PCRFResponse response) {

		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)) {
			return;
		}


		String resultCode = response.getAttribute(PCRFKeyConstants.RESULT_CODE.val);
		if (Objects.nonNull(resultCode) && Objects.equals(resultCode, PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val) == false) {
			return;
		}

		///FIXME FETCH FROM RESPONSE
		RatingGroupSelectionState pccProfileSelectionState = pcrfRequest.getPCCProfileSelectionState();

		if(Objects.isNull(pccProfileSelectionState)) {
			return;
		}

		String sessionId = pcrfRequest.getAttribute(PCRFKeyConstants.GX_SESSION_ID.val);
		if (Objects.isNull(sessionId)) {
			sessionId = pcrfRequest.getAttribute(PCRFKeyConstants.RADIUS_SESSION_ID.val);
		}

		if(Objects.isNull(sessionId)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Unable to re-authorize Gx or RADIUS session. Reason: "
						+ PCRFKeyConstants.GX_SESSION_ID.val + ", "
						+ PCRFKeyConstants.RADIUS_SESSION_ID.val + " not found" );
			}
			return;
		}


		String reAuthCause = null;
		if (isAnyQuotaReservationFail(response) == false) {
			if(isAnyReservationDifferFromPccSelectionState(pccProfileSelectionState, response.getQuotaReservation())) {
				reAuthCause = PCRFKeyValueConstants.RE_AUTH_CAUSE_GY_RESERVATION_CHANGE.val;
			}
		} else {
			reAuthCause = PCRFKeyValueConstants.RE_AUTH_CAUSE_GY_RESRVATION_FAIL.val;
		}


		if (Objects.isNull(reAuthCause)) {
			return;
		}


		SendRARTask sendRARTask = new SendRARTask(sessionId, reAuthCause, getTransactionContext().getControllerContext());
		getTransactionContext().getControllerContext().getServerContext().getTaskScheduler().scheduleSingleExecutionTask(sendRARTask);

	}

	private boolean isAnyReservationDifferFromPccSelectionState(RatingGroupSelectionState pccProfileSelectionState, QuotaReservation quotaReservation) {


		if(Objects.isNull(quotaReservation)) {
			/*if(pccProfileSelectionState.hasRnCQuotaProfilePackage()) {
				if(Objects.isNull(pcrfRequest.getReportedMSCCs())) {
					return  false;
				}

				for(MSCC mscc : pcrfRequest.getReportedMSCCs()) {
					if(ReportingReason.FINAL == mscc.getReportingReason()) {
						return true;
					}
				}

			}*/

			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping diff between reservation. Reason: Quota reservation not found");
			}

			return false;
		}

		for (Entry<Long, MSCC> quotaReservationEntry : quotaReservation.get()) {

			Long ratingGroupId = quotaReservationEntry.getKey();
			ServiceSelectionState serviceSelectionState = pccProfileSelectionState.getServiceSelectionState(ratingGroupId);

			if(Objects.isNull(serviceSelectionState)) {
				if(getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Selection state not found for RG:" + ratingGroupId + " but quota reserved");
				}
				continue;
			}

			MSCC mscc = quotaReservationEntry.getValue();
			for(PackageSelectionState packageSelectionState : serviceSelectionState.getAll().values()) {

				if(packageSelectionState.getLevel() != mscc.getGrantedServiceUnits().getFupLevel()) {
					return true;
				}

				if(Objects.equals(packageSelectionState.getPackage().getId(), mscc.getGrantedServiceUnits().getPackageId()) == false) {
					return true;
				}

				if(Objects.isNull(packageSelectionState.getQoSProfile().getQuotaProfile()) && Objects.isNull(packageSelectionState.getQoSProfile().getDataRateCard())) {
					return true;
				}

				if(Objects.nonNull(packageSelectionState.getQoSProfile().getQuotaProfile()) && Objects.equals(packageSelectionState.getQoSProfile().getQuotaProfile().getId(), mscc.getGrantedServiceUnits().getQuotaProfileIdOrRateCardId()) == false) {
					return true;
				}

				if(Objects.equals(packageSelectionState.getSubscriptionId(), mscc.getGrantedServiceUnits().getSubscriptionId()) == false) {
					return true;
				}

			}

		}

		return false;

	}

	private boolean isAnyQuotaReservationFail(PCRFResponse response) {
		QuotaReservation quotaReservation = response.getQuotaReservation();
		if (Objects.isNull(quotaReservation)) {
			return false;
		}

		for (Entry<Long, MSCC> entry : quotaReservation.get()) {
			if (entry.getValue().getResultCode() != ResultCode.SUCCESS) {
				return true;
			}
		}

		return false;
	}

	private static class SendRARTask implements SingleExecutionAsyncTask {

		private final String sessionId;
		private final String reAuthCause;
		private DiameterGatewayControllerContext controllerContext;

		public SendRARTask(String sessionId, String reAuthCause, DiameterGatewayControllerContext controllerContext) {

			this.sessionId = sessionId;
			this.reAuthCause = reAuthCause;
			this.controllerContext = controllerContext;
		}

		@Override
		public long getInitialDelay() {
			return 1;
		}

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.SECONDS;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			controllerContext.reauthorizeSesion(PCRFKeyConstants.CS_CORESESSION_ID,
					sessionId,
					reAuthCause,
					true,
					Collections.emptyMap());
		}
	}
}
