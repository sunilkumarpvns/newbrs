package com.elitecore.aaa.radius.sessionx;

import java.util.concurrent.TimeUnit;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.sessionx.conf.LocalSessionManagerData;
import com.elitecore.aaa.radius.sessionx.snmp.LocalSessionManagerEntryMBeanImpl;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.snmp.service.EliteAAASNMPAgent;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.Session;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.license.base.commons.LicenseNameConstants;

public class CounterAwareConcurrencySessionManager extends ConcurrencySessionManager{

	private final int localSMIndex;
	private UpdateActiveAndInactiveSessionCounterScheduler updateActiveAndInActiveCounters;
	private LocalSessionManagerEntryMBeanImpl localSMEntry;

	public CounterAwareConcurrencySessionManager(int localSMIndex,AAAServerContext serverContext,LocalSessionManagerData localSessionManagerData) {
		super(serverContext, localSessionManagerData);
		this.localSMIndex = localSMIndex;
		updateActiveAndInActiveCounters = new UpdateActiveAndInactiveSessionCounterScheduler();
	}
	
	@Override
	public void init() throws InitializationFailedException {
		boolean isLicenseValid = serverContext.isLicenseValid(LicenseNameConstants.SESSION_MANAGER, String.valueOf(System.currentTimeMillis()));
		if (isLicenseValid == false) {
			throw new InitializationFailedException("Session Manager "+getSmInstanceName()+" will not start, " +
					"Reason: License for "+LicenseNameConstants.SESSION_MANAGER+" is not acquire or invalid.");
		}
		super.init();
		localSMEntry = new LocalSessionManagerEntryMBeanImpl(localSMIndex, getSmInstanceName());
		 ((EliteAAASNMPAgent) serverContext.getSNMPAgent()).expose(localSMEntry);
		serverContext.getTaskScheduler().scheduleIntervalBasedTask(updateActiveAndInActiveCounters);
	}
	
	@Override
	public void handleAuthFlavorAuthenticationRequest(RadServiceRequest request, RadServiceResponse response) {
		localSMEntry.incrNumberOfRequestRx();
		localSMEntry.incrNumberOfAccessRequestRx();
		super.handleAuthFlavorAuthenticationRequest(request, response);
	}
	
	@Override
	public <T extends RadServiceRequest, V extends RadServiceResponse> 
	void handleAuthenticationRequest(T request, V response, RadServiceContext<T, V> serviceContext) {
		localSMEntry.incrNumberOfRequestRx();
		localSMEntry.incrNumberOfAccessRequestRx();
		super.handleAuthenticationRequest(request, response, serviceContext);
	}
	
	@Override
	public <T extends RadServiceRequest, V extends RadServiceResponse>
	void handleAccountingRequest(T request, V response, RadServiceContext<T, V> serviceContext) {
		updateAccountingRequestCounters(request);
		super.handleAccountingRequest(request, response, serviceContext);
		localSMEntry.incrNumberOfAcctResponseTx();
	}

	private final void updateAccountingRequestCounters(RadServiceRequest request) {

		localSMEntry.incrNumberOfRequestRx();
		localSMEntry.incrNumberOfAcctRequestRx();
		
		IRadiusAttribute accoutingRequestType = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
		
		switch (accoutingRequestType.getIntValue()) {
		case RadiusAttributeValuesConstants.START:
			localSMEntry.incrNumberOfAcctStartRequestRx();
			break;

		case RadiusAttributeValuesConstants.STOP:
			localSMEntry.incrNumberOfAcctStopRequestRx();
			break;
			
		case RadiusAttributeValuesConstants.INTERIM_UPDATE:
			localSMEntry.incrNumberOfAcctUpdateRequestRx();
			break;
		}
	}

	@Override
	protected void sendAccessReject(RadServiceRequest request,RadServiceResponse response, String responseMessage,String logMessage) {
		localSMEntry.incrNumberOfAccessRejectTx();
		super.sendAccessReject(request, response, responseMessage, logMessage);
	}
	
	private class UpdateActiveAndInactiveSessionCounterScheduler extends BaseIntervalBasedTask{

		@Override
		public long getInitialDelay() {
			return 1;
		}
		
		@Override
		public void execute(AsyncTaskContext context) {
			updateActiveSessionCount();
			updateInactiveSessionCount();
		}

		private final void updateActiveSessionCount(){
			Session session = getSessionFactory().getSession();
			Criteria criteria = session.createCriteria(getTableName());
			criteria.add(Restrictions.eq(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_ACTIVE));
			localSMEntry.setNumberOfActiveSession(session.count(criteria));
		}

		private final void updateInactiveSessionCount(){
			Session session = getSessionFactory().getSession();
			Criteria criteria = session.createCriteria(getTableName());
			criteria.add(Restrictions.eq(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_INACTIVE));
			localSMEntry.setNumberOfInactiveSession(session.count(criteria));
		}
		
		@Override
		public long getInterval() {
			return 5;
		}
		
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MINUTES;
		}
	}
	
	@Override
	protected void closeSession(SessionData session) {
		localSMEntry.incrNumberOfTimedOutSession();
		super.closeSession(session);
	}
	
	@Override
	protected void onSessionCreationFailure(ServerContext serverContext,String sessionIdValue) {
		LogManager.getLogger().error(MODULE, this.getSmInstanceName() + "- Could not save the session for Session Id: " + sessionIdValue);
		serverContext.generateSystemAlert(AlertSeverity.ERROR ,Alerts.RM_SESSION_GENERIC, 
				MODULE, "Session creation is failed for Session Id:" + sessionIdValue, 0, 
				"Session creation is failed for Session Id:" + sessionIdValue);
		localSMEntry.incrNumberOfSessionCreationFailure();
	}
	
	@Override
	protected void onSessionCreationFailureForAuthBehavior(AAAServerContext serverContext) {
		LogManager.getLogger().error(MODULE, this.getSmInstanceName() + "- Could not save the session for the user.");
		serverContext.generateSystemAlert(AlertSeverity.ERROR , Alerts.RM_SESSION_GENERIC, 
				MODULE, "Session creation is failed", 0,
				"Session creation is failed");
		localSMEntry.incrNumberOfSessionCreationFailure();
	}
	
	@Override
	protected void onSessionUpdationFailure(String sessionIdValue) {
		LogManager.getLogger().error(MODULE, this.getSmInstanceName() + "- Could not update the session for Session Id: " + sessionIdValue + " on Accounting start.");
		localSMEntry.incrNumberOfSessionUpdationFailure();
	}
	
	@Override
	protected void onSessionDeletionFailure(String sessionIdValue) {
		LogManager.getLogger().error(MODULE, this.getSmInstanceName() + "- Error in deleting session for session id: " + sessionIdValue);
		localSMEntry.incrNumberOfSessionDeletionFailure();
	}
	
	@Override
	protected RadResponseListener createAccountingStopResponseListener(SessionData sessionData) {
		localSMEntry.incrNumberOfAcctStopRequestTx();
		return new CounterAwareResponseListenerForAccountingStop(
				super.createAccountingStopResponseListener(sessionData)
		);
	}
	
	class CounterAwareResponseListenerForAccountingStop implements RadResponseListener{
		private final RadResponseListener accountingStopResponseListener;
		
		public CounterAwareResponseListenerForAccountingStop(RadResponseListener listener) {
			accountingStopResponseListener = listener;
		}
		
		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {
			localSMEntry.incrNumberOfAcctStopResponseRx();
			accountingStopResponseListener.responseReceived(radUDPRequest, radUDPResponse, session);
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			localSMEntry.incrNumberOfAcctStopResponseDropped();
			accountingStopResponseListener.requestDropped(radUDPRequest);
		}

		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			localSMEntry.incrNumberOfAcctStopRequestTimeouts();
			accountingStopResponseListener.requestTimeout(radUDPRequest);
		}
	}
	
	@Override
	protected RadResponseListener createDMResponseListener(SessionData sessionData) {
		localSMEntry.incrNumberOfDisconRequest();
		return new CounterAwareResponseListenerForDM(super.createDMResponseListener(sessionData));
	}
	
	class CounterAwareResponseListenerForDM implements RadResponseListener{

		private final RadResponseListener dmResponseListener;
		
		public CounterAwareResponseListenerForDM( RadResponseListener responseListener) {
			dmResponseListener = responseListener;
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {
			if(radUDPResponse.getRadiusPacket().getPacketType() == RadiusConstants.DISCONNECTION_ACK_MESSAGE){
				localSMEntry.incrNumberOfDisconAck();
			}else if(radUDPResponse.getRadiusPacket().getPacketType() == RadiusConstants.DISCONNECTION_NAK_MESSAGE){
				localSMEntry.incrNumberOfDisconNAck();
			}
			dmResponseListener.responseReceived(radUDPRequest, radUDPResponse, session);
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			localSMEntry.incrNumberOfDisconResponseDropped();
			dmResponseListener.requestDropped(radUDPRequest);
		}

		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			localSMEntry.incrNumberOfDisconTimeouts();
			dmResponseListener.requestTimeout(radUDPRequest);
		}
	}
	
	@Override
	protected <T extends RadServiceRequest, V extends RadServiceResponse>
	RadResponseListener createSessionOverrideResponseListenerForForDM(
			SessionData sessionData) {
		return new CounterAwareSessionOverrideResponseListenerForDM(
				super.createSessionOverrideResponseListenerForForDM(sessionData));
	}
	
	@Override
	protected <T extends RadServiceRequest, V extends RadServiceResponse>
	void onSessionOverrideEventForDM(SessionData sessionData) {

		localSMEntry.incrNumberOfOverridedSession();
		localSMEntry.incrNumberOfDisconRequest();
		
		super.onSessionOverrideEventForDM(sessionData);
	}
	
	@Override
	protected <T extends RadServiceRequest, V extends RadServiceResponse> 
	void onSessionOverrideEventForAcctStop(SessionData sessionData) {
		
		localSMEntry.incrNumberOfOverridedSession();
		localSMEntry.incrNumberOfAcctStopRequestTx();
		
		super.onSessionOverrideEventForAcctStop(sessionData);
	}
	
	class CounterAwareSessionOverrideResponseListenerForDM implements RadResponseListener{
		private final RadResponseListener sessionOverrideResponseListenerForDM;

		public CounterAwareSessionOverrideResponseListenerForDM(RadResponseListener responseListener) {
			sessionOverrideResponseListenerForDM = responseListener;
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {
			if(radUDPResponse.getRadiusPacket().getPacketType() == RadiusConstants.DISCONNECTION_ACK_MESSAGE){
				localSMEntry.incrNumberOfDisconAck();
			}else if(radUDPResponse.getRadiusPacket().getPacketType() == RadiusConstants.DISCONNECTION_NAK_MESSAGE){
				localSMEntry.incrNumberOfDisconNAck();
			}

			IRadiusAttribute errorCause = radUDPResponse.getRadiusPacket().getRadiusAttribute(RadiusAttributeConstants.ERROR_CAUSE,true);
			if(errorCause != null){
				maintainErrorCodeWiseStatistics(errorCause);
			}
			sessionOverrideResponseListenerForDM.responseReceived(radUDPRequest, radUDPResponse, session);
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			localSMEntry.incrNumberOfDisconResponseDropped();
			sessionOverrideResponseListenerForDM.requestDropped(radUDPRequest);
		}

		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			localSMEntry.incrNumberOfDisconTimeouts();
			sessionOverrideResponseListenerForDM.requestTimeout(radUDPRequest);
		}

		private void maintainErrorCodeWiseStatistics(IRadiusAttribute errorCause) {
			int errorCauseValue = errorCause.getIntValue();
			
			if(RadiusAttributeValuesConstants.RESIDUAL_CONTEXT_REMOVED == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckResidualSessCtxRemoved();
			}else if(RadiusAttributeValuesConstants.INVALID_EAP_PACKET == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckInvalidEAPPacket();
			}else if(RadiusAttributeValuesConstants. UNSUPPORTED_ATTRIBUTE == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckUnsupporteAttribute();
			}else if(RadiusAttributeValuesConstants.MISSING_ATTRIBUTE == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckMissingAttribute();
			}else if(RadiusAttributeValuesConstants.NAS_IDENTIFICATION_MISMATCH == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckNASIdentificationMismatch();
			}else if(RadiusAttributeValuesConstants.INVALID_REQUEST == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckInvalidRequest();
			}else if(RadiusAttributeValuesConstants.UNSUPPORTED_SERVICE == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckUnsupportedService();
			}else if(RadiusAttributeValuesConstants.UNSUPPORTED_EXTENSION == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckUnsupportedExtension();
			}else if(RadiusAttributeValuesConstants.ADMINISTRATIVELY_PROHIBITED == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckAdministrativelyProhibited();
			}else if(RadiusAttributeValuesConstants.PROXY_REQUEST_NOT_ROUTABLE == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckRequestNotRoutable();
			}else if(RadiusAttributeValuesConstants.SESSION_CONTEXT_NOT_FOUND == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckSessionCtxNotFound();
			}else if(RadiusAttributeValuesConstants.SESSION_CONTEXT_NOT_REMOVABLE == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckSessionCtxNotRemovable();
			}else if(RadiusAttributeValuesConstants.PROXY_PROCESSING_ERROR == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckOtherProxyProcessingError();
			}else if(RadiusAttributeValuesConstants.RESOURCES_UNAVAILABLE == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckResourcesUnavailable();
			}else if(RadiusAttributeValuesConstants.REQUEST_INITIATED == errorCauseValue){
				localSMEntry.incrNumberOfDisconNAckRequestInitiated();
			}
		}
	}
	
	
	@Override
	protected <T extends RadServiceRequest, V extends RadServiceResponse> 
	RadResponseListener createSessionOverrideResponseListenerForAccountingStop(
			SessionData sessionData) {

		return new CounterAwareSessionOverrideResponseListenerForAccountingStop(
				super.createSessionOverrideResponseListenerForAccountingStop(
				sessionData)
		);
	}
	
	class CounterAwareSessionOverrideResponseListenerForAccountingStop implements RadResponseListener{

		private final RadResponseListener sessionOverrideResponseListenerForAccountingStop;

		public CounterAwareSessionOverrideResponseListenerForAccountingStop(RadResponseListener responseListener) {
			this.sessionOverrideResponseListenerForAccountingStop = responseListener;
		}
		
		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {
			localSMEntry.incrNumberOfAcctStopResponseRx();
			sessionOverrideResponseListenerForAccountingStop.responseReceived(radUDPRequest, radUDPResponse, session);
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			localSMEntry.incrNumberOfAcctStopResponseDropped();
			sessionOverrideResponseListenerForAccountingStop.requestDropped(radUDPRequest);
		}

		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			localSMEntry.incrNumberOfAcctStopRequestTimeouts();
			sessionOverrideResponseListenerForAccountingStop.requestTimeout(radUDPRequest);
		}
	}

	@Override
	public String toString() {
		return localSMEntry.toString();
	}
}