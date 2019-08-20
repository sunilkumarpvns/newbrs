/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.fsm.serverstateful.enums;

/**
 * @author pulin
 *
 */
public enum ServerStateFulEvent {
	ServiceSpecificAuthorizationRequestReceivedAndUserIsAuthorized(true),
	ServiceSpecificAuthorizationRequestReceivedAndUserIsNotAuthorized(false),
	ASAReceived(false),
	STRReceived(true),
	HomeServerWantsToTerminateTheService(true),
	AuthorizationLifetimeExpiresOnHomeServer(true),
	SessionTimeoutExpiresOnHomeServer(true),
	FailureToSendASR(false),
	ASRSuccessfullySentAndASAReceivedWithResultCode(true),
	;

	public final boolean isSync;
	
	ServerStateFulEvent(boolean isSync) {
		this.isSync = isSync;
	}
	
	public boolean isSync() {
		return isSync;
	}

}
