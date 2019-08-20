package com.elitecore.netvertex.gateway.radius.snmp.auth;

import com.elitecore.netvertex.gateway.radius.snmp.auth.AuthServerCounters.RadiusAuthClientEntry;

public class AuthServerClientStatisticsProvider{ 

	private AuthServerCounters authServerCounters;
	public AuthServerClientStatisticsProvider(AuthServerCounters authServerCounters){
		this.authServerCounters = authServerCounters;
	}

	public Long getRadiusAuthServMalformedAccessRequests(String clientIp) {
		return authServerCounters.getMalformedReqCntr(clientIp);
	}

	public Long getRadiusAuthServAccessChallenges(String clientIp) {
		return authServerCounters.getAccessChallengesCntr(clientIp);
	}

	public Long getRadiusAuthServAccessRejects(String clientIp) {
		return authServerCounters.getAccessRejectCntr(clientIp);
	}

	public Long getRadiusAuthServAccessAccepts(String clientIp) {
		return authServerCounters.getAccessAcceptCntr(clientIp);
	}

	public Long getRadiusAuthServDupAccessRequests(String clientIp) {
		return authServerCounters.getDupReqCntr(clientIp);
	}

	public Long getRadiusAuthServAccessRequests(String clientIp) {
		return authServerCounters.geReqCntr(clientIp);
	}

	public Long getRadiusAuthServUnknownTypes(String clientIp) {
		return authServerCounters.getUnknownTypeCntr(clientIp);
	}

	public String getRadiusAuthClientID(String clientIp) {
		return authServerCounters.getClientId(clientIp);
	}

	public Long getRadiusAuthServPacketsDropped(String clientIp) {
		return authServerCounters.getPackDropCntr(clientIp);
	}

	public Long getRadiusAuthServBadAuthenticators(String clientIp) {
		return authServerCounters.getBadAuthenticatorsCntr(clientIp);
	}
	
	public RadiusAuthClientEntry getClientEntry(String clientIp){
		return authServerCounters.getAuthClientEntry(clientIp);
	}
}