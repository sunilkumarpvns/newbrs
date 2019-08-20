package com.elitecore.aaa.radius.sessionx.snmp;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.radius.sessionx.snmp.localsm.autogen.LocalSessionManagerEntry;
import static com.elitecore.core.util.mbean.SnmpCounterUtil.convertToCounter32;

public class LocalSessionManagerEntryMBeanImpl extends LocalSessionManagerEntry{

	private static final String MODULE = "LOCAL-SM-ENTRY-MBEAN";
	private int sessionManagerIndex;
	private String sessionManagerName;
	private AtomicLong numberOfSessionCreationFailure = new AtomicLong(0);
	private AtomicLong numberOfSessionUpdationFailure = new AtomicLong(0);
	private AtomicLong numberOfSessionDeletionFailure = new AtomicLong(0);
	private AtomicLong numberOfActiveSession = new AtomicLong(0);
	private AtomicLong numberOfInactiveSession = new AtomicLong(0);
	private AtomicLong numberOfOverridedSession = new AtomicLong(0);
	private AtomicLong numberOfTimedOutSession = new AtomicLong(0);

	private AtomicLong sessionManagerStatsResetTime = new AtomicLong(System.currentTimeMillis());
	private AtomicLong numberOfRequestRx= new AtomicLong(0);

	private AtomicLong numberOfAccessRequestRx = new AtomicLong(0);
	private AtomicLong numberOfAccessRejectTx = new AtomicLong(0);
	private AtomicLong numberOfAcctRequestRx = new AtomicLong(0);
	private AtomicLong numberOfAcctResponseTx = new AtomicLong(0);
	private AtomicLong numberOfAcctStartRequestRx = new AtomicLong(0);
	private AtomicLong numberOfAcctStopRequestRx = new AtomicLong(0);
	private AtomicLong numberOfAcctUpdateRequestRx = new AtomicLong(0);

	private AtomicLong numberOfAcctStopRequestTx = new AtomicLong(0);
	private AtomicLong numberOfAcctStopResponseRx = new AtomicLong(0);
	private AtomicLong numberOfAcctStopRequestTimeouts = new AtomicLong(0);
	private AtomicLong numberOfAcctStopResponseDropped = new AtomicLong(0);

	private AtomicLong numberOfDisconRequest = new AtomicLong(0);
	private AtomicLong numberOfDisconAck = new AtomicLong(0);
	private AtomicLong numberOfDisconNAck = new AtomicLong(0);
	private AtomicLong numberOfDisconTimeouts = new AtomicLong(0);
	private AtomicLong numberOfDisconResponseDropped = new AtomicLong(0);

	private AtomicLong numberOfDisconNAckResidualSessCtxRemoved = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckInvalidEAPPacket = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckUnsupporteAttribute = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckMissingAttribute = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckNASIdentificationMismatch = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckInvalidRequest = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckUnsupportedService = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckUnsupportedExtension = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckAdministrativelyProhibited = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckRequestNotRoutable = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckSessionCtxNotFound = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckSessionCtxNotRemovable = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckOtherProxyProcessingError = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckResourcesUnavailable = new AtomicLong(0);
	private AtomicLong numberOfDisconNAckRequestInitiated = new AtomicLong(0);
	
	public LocalSessionManagerEntryMBeanImpl(int smIndex,String smName) {
		this.sessionManagerIndex = smIndex;
		this.sessionManagerName = smName;
	}

	@Override
	public Long getSmTotalAcctStopResponseRx(){
		return convertToCounter32(numberOfAcctStopResponseRx.get());
	}

	@Override
	public Long getSmTotalAcctStopRequestTx(){
		return convertToCounter32(numberOfAcctStopRequestTx.get());
	}

	@Override
	public Long getSmTotalAccessRequestRx(){
		return convertToCounter32(numberOfAccessRequestRx.get());
	}

	@Override
	public Long getSmTotalRequestRx(){
		return convertToCounter32(numberOfRequestRx.get());
	}

	@Override
	public Long getSmStatsResetTime(){
		return (System.currentTimeMillis() - sessionManagerStatsResetTime.get())/10;
	}

	@Override
	public Long getSmTotalDisconnectResponseDropped(){
		return convertToCounter32(numberOfDisconResponseDropped.get());
	}

	@Override
	public Long getSmTotalDisconnectRequestTimeouts(){
		return convertToCounter32(numberOfDisconTimeouts.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckRequestInitiated(){
		return convertToCounter32(numberOfDisconNAckRequestInitiated.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckResourcesUnavailable(){
		return convertToCounter32(numberOfDisconNAckResourcesUnavailable.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckOtherProxyProcessingError(){
		return convertToCounter32(numberOfDisconNAckOtherProxyProcessingError.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckSessionCtxNotRemovable(){
		return convertToCounter32(numberOfDisconNAckSessionCtxNotRemovable.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckSessionCtxNotFound(){
		return convertToCounter32(numberOfDisconNAckSessionCtxNotFound.get());
	}

	@Override
	public Long getSmTotalTimedOutSession(){
		return convertToCounter32(numberOfTimedOutSession.get());
	}

	@Override
	public Long getSmTotalOverridedSession(){
		return convertToCounter32(numberOfOverridedSession.get());
	}

	@Override
	public Long getSmTotalActiveSession(){
		return convertToCounter32(numberOfActiveSession.get());
	}

	@Override
	public Long getSmTotalSessionDeletionFailure(){
		return convertToCounter32(numberOfSessionDeletionFailure.get());
	}

	@Override
	public Long getSmTotalSessionUpdationFailure(){
		return convertToCounter32(numberOfSessionUpdationFailure.get());
	}

	@Override
	public Long getSmTotalSessionCreationFailure(){
		return convertToCounter32(numberOfSessionCreationFailure.get());
	}

	@Override
	public String getSmName(){
		return sessionManagerName;
	}

	@Override
	public Integer getSmIndex(){
		return sessionManagerIndex;
	}

	@Override
	public Long getSmTotalDisconnectNAckRequestNotRoutable(){
		return convertToCounter32(numberOfDisconNAckRequestNotRoutable.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckAdministrativelyProhibited(){
		return convertToCounter32(numberOfDisconNAckAdministrativelyProhibited.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckUnsupportedExtension(){
		return convertToCounter32(numberOfDisconNAckUnsupportedExtension.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckUnsupportedService(){
		return convertToCounter32(numberOfDisconNAckUnsupportedService.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckInvalidRequest(){
		return convertToCounter32(numberOfDisconNAckInvalidRequest.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckNASIdentificationMismatch(){
		return convertToCounter32(numberOfDisconNAckNASIdentificationMismatch.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckMissingAttribute(){
		return convertToCounter32(numberOfDisconNAckMissingAttribute.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckUnsupporteAttribute(){
		return convertToCounter32(numberOfDisconNAckUnsupporteAttribute.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckInvalidEAPPacket(){
		return convertToCounter32(numberOfDisconNAckInvalidEAPPacket.get());
	}

	@Override
	public Long getSmTotalAcctStopRequestRx(){
		return convertToCounter32(numberOfAcctStopRequestRx.get());
	}

	@Override
	public Long getSmTotalAcctUpdateRequestRx(){
		return convertToCounter32(numberOfAcctUpdateRequestRx.get());
	}

	@Override
	public Long getSmTotalAcctStartRequestRx(){
		return convertToCounter32(numberOfAcctStartRequestRx.get());
	}

	@Override
	public Long getSmTotalAcctRequestRx(){
		return convertToCounter32(numberOfAcctRequestRx.get());
	}

	@Override
	public Long getSmTotalAccessRejectTx(){
		return convertToCounter32(numberOfAccessRejectTx.get());
	}

	@Override
	public Long getSmTotalDisconnectNAckResidualSessCtxRemoved(){
		return convertToCounter32(numberOfDisconNAckResidualSessCtxRemoved.get());
	}

	@Override
	public Long getSmTotalDisconnectNAck(){
		return convertToCounter32(numberOfDisconNAck.get());
	}

	@Override
	public Long getSmTotalDisconnectAck(){
		return convertToCounter32(numberOfDisconAck.get());
	}

	@Override
	public Long getSmTotalDisconnectRequest(){
		return convertToCounter32(numberOfDisconRequest.get());
	}

	@Override
	public Long getSmTotalAcctStopResponseDropped(){
		return convertToCounter32(numberOfAcctStopResponseDropped.get());
	}

	@Override
	public Long getSmTotalAcctStopRequestTimeouts(){
		return convertToCounter32(numberOfAcctStopRequestTimeouts.get());
	}
	
	public void resetCounter(){

		numberOfSessionCreationFailure.set(0);
		numberOfSessionUpdationFailure.set(0); 		
		numberOfSessionDeletionFailure.set(0);		
		numberOfActiveSession.set(0);					      
		numberOfInactiveSession.set(0);				    
		numberOfOverridedSession.set(0);				   
		numberOfTimedOutSession.set(0);				        

		sessionManagerStatsResetTime.set(System.currentTimeMillis());

		numberOfRequestRx.set(0);                
		numberOfAccessRequestRx.set(0);
		numberOfAccessRejectTx.set(0);
		numberOfAcctRequestRx.set(0); 
		numberOfAcctResponseTx.set(0);
		numberOfAcctStartRequestRx.set(0);  
		numberOfAcctUpdateRequestRx.set(0);  
		numberOfAcctStopRequestRx.set(0);   

		numberOfAcctStopRequestTx.set(0);
		numberOfAcctStopResponseRx.set(0);
		numberOfAcctStopRequestTimeouts.set(0);
		numberOfAcctStopResponseDropped.set(0);

		numberOfDisconRequest.set(0);        
		numberOfDisconAck.set(0);            
		numberOfDisconNAck.set(0);           
		numberOfDisconTimeouts.set(0);						  
		numberOfDisconResponseDropped.set(0); 				

		numberOfDisconNAckResidualSessCtxRemoved.set(0); 	
		numberOfDisconNAckInvalidEAPPacket.set(0); 		
		numberOfDisconNAckUnsupporteAttribute.set(0); 		
		numberOfDisconNAckMissingAttribute.set(0); 		
		numberOfDisconNAckNASIdentificationMismatch.set(0);
		numberOfDisconNAckInvalidRequest.set(0); 			
		numberOfDisconNAckUnsupportedService.set(0); 		
		numberOfDisconNAckUnsupportedExtension.set(0); 	
		numberOfDisconNAckAdministrativelyProhibited.set(0);
		numberOfDisconNAckRequestNotRoutable.set(0); 		
		numberOfDisconNAckSessionCtxNotFound.set(0); 	
		numberOfDisconNAckSessionCtxNotRemovable.set(0);
		numberOfDisconNAckOtherProxyProcessingError.set(0);
		numberOfDisconNAckResourcesUnavailable.set(0); 	
		numberOfDisconNAckRequestInitiated.set(0); 		
	}

	public void incrSmIndex(int smIndex) {
		this.sessionManagerIndex = smIndex;
	}

	public void incrSmName(String smName) {
		this.sessionManagerName = smName;
	}

	public void incrNumberOfSessionCreationFailure() {
		this.numberOfSessionCreationFailure.incrementAndGet();
	}

	public void incrNumberOfSessionUpdationFailure() {
		this.numberOfSessionUpdationFailure.incrementAndGet();
	}

	public void incrNumberOfSessionDeletionFailure() {
		this.numberOfSessionDeletionFailure.incrementAndGet();
	}

	public void setNumberOfActiveSession(int activeSessions) {
		this.numberOfActiveSession.set(activeSessions);
	}

	public void setNumberOfInactiveSession(int inActiveSessions) {
		this.numberOfInactiveSession.set(inActiveSessions);
	}

	public void incrNumberOfOverridedSession() {
		this.numberOfOverridedSession.incrementAndGet();
	}

	public void incrNumberOfTimedOutSession() {
		this.numberOfTimedOutSession.incrementAndGet();
	}

	public void incrNumberOfRequestRx() {
		this.numberOfRequestRx.incrementAndGet();
	}

	public void incrNumberOfAccessRequestRx() {
		this.numberOfAccessRequestRx.incrementAndGet();
	}

	public void incrNumberOfAccessRejectTx() {
		this.numberOfAccessRejectTx.incrementAndGet();
	}

	public void incrNumberOfAcctRequestRx() {
		this.numberOfAcctRequestRx.incrementAndGet();
	}

	public void incrNumberOfAcctResponseTx() {
		this.numberOfAcctResponseTx.incrementAndGet();
	}

	public void incrNumberOfAcctStartRequestRx() {
		this.numberOfAcctStartRequestRx.incrementAndGet();
	}

	public void incrNumberOfAcctStopRequestRx() {
		this.numberOfAcctStopRequestRx.incrementAndGet();
	}

	public void incrNumberOfAcctUpdateRequestRx() {
		this.numberOfAcctUpdateRequestRx.incrementAndGet();
	}

	public void incrNumberOfAcctStopRequestTx() {
		this.numberOfAcctStopRequestTx.incrementAndGet();
	}

	public void incrNumberOfAcctStopResponseRx() {
		this.numberOfAcctStopResponseRx.incrementAndGet();
	}

	public void incrNumberOfAcctStopRequestTimeouts() {
		this.numberOfAcctStopRequestTimeouts.incrementAndGet();
	}

	public void incrNumberOfAcctStopResponseDropped() {
		this.numberOfAcctStopResponseDropped.incrementAndGet();
	}

	public void incrNumberOfDisconRequest() {
		this.numberOfDisconRequest.incrementAndGet();
	}

	public void incrNumberOfDisconAck() {
		this.numberOfDisconAck.incrementAndGet();
	}

	public void incrNumberOfDisconNAck() {
		this.numberOfDisconNAck.incrementAndGet();
	}

	public void incrNumberOfDisconTimeouts() {
		this.numberOfDisconTimeouts.incrementAndGet();
	}

	public void incrNumberOfDisconResponseDropped() {
		this.numberOfDisconResponseDropped.incrementAndGet();
	}

	public void incrNumberOfDisconNAckResidualSessCtxRemoved() {
		this.numberOfDisconNAckResidualSessCtxRemoved.incrementAndGet();
	}

	public void incrNumberOfDisconNAckInvalidEAPPacket() {
		this.numberOfDisconNAckInvalidEAPPacket.incrementAndGet();
	}

	public void incrNumberOfDisconNAckUnsupporteAttribute() {
		this.numberOfDisconNAckUnsupporteAttribute.incrementAndGet();
	}

	public void incrNumberOfDisconNAckMissingAttribute() {
		this.numberOfDisconNAckMissingAttribute.incrementAndGet();
	}

	public void incrNumberOfDisconNAckNASIdentificationMismatch() {
		this.numberOfDisconNAckNASIdentificationMismatch.incrementAndGet();
	}

	public void incrNumberOfDisconNAckInvalidRequest() {
		this.numberOfDisconNAckInvalidRequest.incrementAndGet();
	}

	public void incrNumberOfDisconNAckUnsupportedService() {
		this.numberOfDisconNAckUnsupportedService.incrementAndGet();
	}

	public void incrNumberOfDisconNAckUnsupportedExtension() {
		this.numberOfDisconNAckUnsupportedExtension.incrementAndGet();
	}

	public void incrNumberOfDisconNAckAdministrativelyProhibited() {
		this.numberOfDisconNAckAdministrativelyProhibited.incrementAndGet();
	}

	public void incrNumberOfDisconNAckRequestNotRoutable() {
		this.numberOfDisconNAckRequestNotRoutable.incrementAndGet();
	}

	public void incrNumberOfDisconNAckSessionCtxNotFound() {
		this.numberOfDisconNAckSessionCtxNotFound.incrementAndGet();
	}

	public void incrNumberOfDisconNAckSessionCtxNotRemovable() {
		this.numberOfDisconNAckSessionCtxNotRemovable.incrementAndGet();
	}

	public void incrNumberOfDisconNAckOtherProxyProcessingError() {
		this.numberOfDisconNAckOtherProxyProcessingError.incrementAndGet();
	}

	public void incrNumberOfDisconNAckResourcesUnavailable() {
		this.numberOfDisconNAckResourcesUnavailable.incrementAndGet();
	}

	public void incrNumberOfDisconNAckRequestInitiated() {
		this.numberOfDisconNAckRequestInitiated.incrementAndGet();
	}

	@Override
	public Long getSmTotalInActiveSession(){
		return numberOfInactiveSession.get();
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.LOCAL_SM_TABLE + getSmName();
	}
	
	@Override
	public String toString() {
		final String stringFormmat = "%-40s: %s";
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(padStart("Statistics for Session Manager: "+sessionManagerName, 10, ' '));
		out.println(repeat("-", 55));
		out.println(format(stringFormmat, "Session Updation Failure",numberOfSessionUpdationFailure ));
		out.println(format(stringFormmat, "Session Creation Failure", numberOfSessionCreationFailure));
		out.println(format(stringFormmat, "Session Deletion Failure", numberOfSessionDeletionFailure));
		out.println(format(stringFormmat, "Active Sessions", numberOfActiveSession));
		out.println(format(stringFormmat, "Inactive Sessions",numberOfInactiveSession ));
		out.println(format(stringFormmat, "Overrided Sessions",numberOfOverridedSession ));
		out.println(format(stringFormmat, "TimedOut Sessions", numberOfTimedOutSession));
		
		out.println(format(stringFormmat, "Total Request Received", numberOfRequestRx));
		out.println(format(stringFormmat, "Access Requests",numberOfAccessRequestRx ));
		out.println(format(stringFormmat, "Access Reject", numberOfAccessRejectTx));
		out.println(format(stringFormmat, "Acct Requests", numberOfAcctRequestRx));
		out.println(format(stringFormmat, "Acct Responses",numberOfAcctResponseTx));
		out.println(format(stringFormmat, "Acct Start Requests",numberOfAcctStartRequestRx ));
		out.println(format(stringFormmat, "Acct Stop Requests",numberOfAcctStopRequestRx ));
		out.println(format(stringFormmat, "Acct Update Requests", numberOfAcctUpdateRequestRx));
		
		out.println(format(stringFormmat, "Acct Stop Requests send",numberOfAcctStopRequestTx ));
		out.println(format(stringFormmat, "Acct Stop Response received",numberOfAcctStopResponseRx ));
		out.println(format(stringFormmat, "Acct Stop Request Timeouts", numberOfAcctStopRequestTimeouts));
		out.println(format(stringFormmat, "Acct Stop Response Dropped",numberOfAcctStopResponseDropped ));
		
		out.println(format(stringFormmat, "Disconnect Request", numberOfDisconRequest));
		out.println(format(stringFormmat, "Disconnect Ack",numberOfDisconAck ));
		out.println(format(stringFormmat, "Disconnect NAck", numberOfDisconNAck));
		out.println(format(stringFormmat, "Disconnect Timeouts", numberOfDisconTimeouts));
		out.println(format(stringFormmat, "Disconnect Response Dropped",numberOfDisconResponseDropped ));
		
		out.println(format(stringFormmat, "DisconNAckResidualSessCtxRemoved", numberOfDisconNAckResidualSessCtxRemoved));
		out.println(format(stringFormmat, "DisconNAckInvalidEAPPacket", numberOfDisconNAckInvalidEAPPacket));
		out.println(format(stringFormmat, "DisconNAckUnsupporteAttribute", numberOfDisconNAckUnsupporteAttribute));
		out.println(format(stringFormmat, "DisconNAckMissingAttribute", numberOfDisconNAckMissingAttribute));
		out.println(format(stringFormmat, "DisconNAckNASIdentificationMismatch", numberOfDisconNAckNASIdentificationMismatch));
		out.println(format(stringFormmat, "DisconNAckInvalidRequest", numberOfDisconNAckInvalidRequest));
		out.println(format(stringFormmat, "DisconNAckUnsupportedService",numberOfDisconNAckUnsupportedService ));
		out.println(format(stringFormmat, "DisconNAckUnsupportedExtension", numberOfDisconNAckUnsupportedExtension));
		out.println(format(stringFormmat, "DisconNAckAdministrativelyProhibited", numberOfDisconNAckAdministrativelyProhibited));
		out.println(format(stringFormmat, "DisconNAckRequestNotRoutable", numberOfDisconNAckRequestNotRoutable));
		out.println(format(stringFormmat, "DisconNAckSessionCtxNotFound", numberOfDisconNAckSessionCtxNotFound));
		out.println(format(stringFormmat, "DisconNAckSessionCtxNotRemovable", numberOfDisconNAckSessionCtxNotRemovable));
		out.println(format(stringFormmat, "DisconNAckOtherProxyProcessingError", numberOfDisconNAckOtherProxyProcessingError));
		out.println(format(stringFormmat, "DisconNAckResourcesUnavailable", numberOfDisconNAckResourcesUnavailable));
		out.println(format(stringFormmat, "DisconNAckRequestInitiated", numberOfDisconNAckRequestInitiated));
		
		out.println(repeat("-", 55));
		out.close();
		return writer.toString();
	}
}