package com.elitecore.netvertex.gateway.radius.snmp.acct;


public class AcctServerClientStatisticsProvider { 

	private AcctServerCounters acctServerMibCounters;
	public AcctServerClientStatisticsProvider(AcctServerCounters acctServerMibCounters){
		this.acctServerMibCounters = acctServerMibCounters;
	}

	public Long getRadiusAccServMalformedRequests(String clientIp) {
		return acctServerMibCounters.getMalformedReqCntr(clientIp);
	}

	public Long getRadiusAccServBadAuthenticators(String clientIp) {
		return acctServerMibCounters.getBadAuthenticatorsCntr(clientIp);
	}

	public Long getRadiusAccServResponses(String clientIp) {
		return acctServerMibCounters.getResCntr(clientIp);
	}

	public Long getRadiusAccServDupRequests(String clientIp) {
		return acctServerMibCounters.getDupReqCntr(clientIp);
	}

	public Long getRadiusAccServRequests(String clientIp) {
		return acctServerMibCounters.geReqCntr(clientIp);
	}

	public Long getRadiusAccServPacketsDropped(String clientIp) {
		return acctServerMibCounters.getPackDropCntr(clientIp);
	}

	public String getRadiusAccClientID(String clientIp) {
		return acctServerMibCounters.getID(clientIp);
	}

	public Long getRadiusAccServUnknownTypes(String clientIp) {
		return acctServerMibCounters.getUnknownTypeCntr(clientIp);
	}

	public Long getRadiusAccServNoRecords(String clientIp) {
		return acctServerMibCounters.getNoRecordCntr(clientIp);
	}

	public long getAccStartReqCntr(String clientIp) {
		return acctServerMibCounters.getStartReqCntr(clientIp);
	}

	public long getAccStopReqCntr(String clientIp) {
		return acctServerMibCounters.getStopReqCntr(clientIp);
	}

	public long getAccIntrUpdateReqCntr(String clientIp) {
		return acctServerMibCounters.geIntrUdateReqCntr(clientIp);
	}
	
	public AcctServerCounters.RadiusAcctClientEntry getClientEntry(String clientIp){
		return acctServerMibCounters.getAcctClientEntry(clientIp);
				
	}
}
