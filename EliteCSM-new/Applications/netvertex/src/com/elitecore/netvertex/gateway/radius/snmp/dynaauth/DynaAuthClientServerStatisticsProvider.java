package com.elitecore.netvertex.gateway.radius.snmp.dynaauth;

import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientCounters.DynAuthServerEntry;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.autogen.EnumRadiusDynAuthServerAddressType;

public class DynaAuthClientServerStatisticsProvider {

	private DynaAuthClientCounters dynaAuthClientCounters;
	public DynaAuthClientServerStatisticsProvider(DynaAuthClientCounters dynaAuthClientCounters){
		this.dynaAuthClientCounters = dynaAuthClientCounters;
	}
	
	public Long getRadiusDynAuthClientCoAPacketsDropped(String serverIp){
		return dynaAuthClientCounters.getCoAPackDropCntr(serverIp);
	}

	
	public Long getRadiusDynAuthClientCoARequests(String serverIp) {
		return dynaAuthClientCounters.getCoAReqCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconPacketsDropped(String serverIp){
		return dynaAuthClientCounters.getDisPackDropCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconTimeouts(String serverIp){
		return dynaAuthClientCounters.getDisTimeoutCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconPendingRequests(String serverIp){
		return dynaAuthClientCounters.getDisPenReqCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconBadAuthenticators(String serverIp){
		return dynaAuthClientCounters.getDisBadAuthenticatorCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconMalformedResponses(String serverIp){
		return dynaAuthClientCounters.getDisMalformedResCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconNakSessNoContext(String serverIp){
		return dynaAuthClientCounters.getDisNakSessNoCtxCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconNakAuthOnlyRequest(String serverIp){
		return dynaAuthClientCounters.getDisNakAuthOnlyReqCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconNaks(String serverIp) {
		return dynaAuthClientCounters.getDisNakCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconAcks(String serverIp) {
		return dynaAuthClientCounters.getDisAckCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconAuthOnlyRequests(String serverIp){
		return dynaAuthClientCounters.getDisAuthOnlyReqCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientCoATimeouts(String serverIp) {
		return dynaAuthClientCounters.getCoATimeoutCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientCoAPendingRequests(String serverIp){
		return dynaAuthClientCounters.getCoAPenReqCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientCoABadAuthenticators(String serverIp){
		return dynaAuthClientCounters.getCoABadAuthenticatorCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientMalformedCoAResponses(String serverIp){
		return dynaAuthClientCounters.getCoAMalformedResCntr(serverIp);
	}
	
	public Long getRadiusDynAuthServerClientPortNumber(String serverIp){
		return dynaAuthClientCounters.getServerPort(serverIp);
	}
	
	public String getRadiusDynAuthServerID(String serverIp) {
		return dynaAuthClientCounters.getServerID(serverIp);
	}
	
	public Long getRadiusDynAuthClientRoundTripTime(String serverIp){
		return dynaAuthClientCounters.getRoundTripTime(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconRequests(String serverIp){
		return dynaAuthClientCounters.getDisReqCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientDisconRetransmissions(String serverIp){
		return dynaAuthClientCounters.getDisRetraCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientCoANakSessNoContext(String serverIp){
		return dynaAuthClientCounters.getCoANakSessNoCtxCntr(serverIp);
	}
	
	public String getRadiusDynAuthServerAddress(String serverIp) {
		return dynaAuthClientCounters.getServerAddress(serverIp);
	}
	
	public Long getRadiusDynAuthClientCoANakAuthOnlyRequest(String serverIp){
		return dynaAuthClientCounters.getCoANakAuthOnlyReqCntr(serverIp);
	}
	
	public EnumRadiusDynAuthServerAddressType getRadiusDynAuthServerAddressType(String serverIp){
		int addressType = dynaAuthClientCounters.getServerAddressType(serverIp);
		return new EnumRadiusDynAuthServerAddressType(addressType); 
	}
	
	public Long getRadiusDynAuthClientCoANaks(String serverIp) {
		return dynaAuthClientCounters.getCoANakCntr(serverIp);
	}

	public Long getRadiusDynAuthClientCoAAcks(String serverIp) {
		return dynaAuthClientCounters.getCoAAckCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientCoARetransmissions(String serverIp){
		return dynaAuthClientCounters.getCoARetraCntr(serverIp);
	}
	
	public Long getRadiusDynAuthClientCoAAuthOnlyRequest(String serverIp){
		return dynaAuthClientCounters.getCoAAuthOnlyReqCntr(serverIp);
	}

	
	public Long getRadiusDynAuthClientCounterDiscontinuity(String serverIp){
		return dynaAuthClientCounters.getCounterDiscontinuity(serverIp);
	}

	public Long getRadiusDynAuthClientUnknownTypes(String serverIp) {
		return dynaAuthClientCounters.getUnknownTypeCntr(serverIp);
	}
	
	public DynAuthServerEntry getClientEntry(String clientIp){
		return dynaAuthClientCounters.getClientEntry(clientIp);
				
	}
}