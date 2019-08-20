package com.elitecore.netvertex.gateway.radius.snmp.dynaauth;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.autogen.EnumRadiusDynAuthServerAddressType;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.autogen.RadiusDynAuthServerEntry;
import com.sun.management.snmp.SnmpStatusException;

public class RadiusDynAuthServerEntryImpl extends RadiusDynAuthServerEntry{

	private int serverIndex; 
	private String ipAddress;
	transient private DynaAuthClientServerStatisticsProvider dynaAuthClientServerStatisticsProvider;

	public RadiusDynAuthServerEntryImpl(String ipAddress,
			DynaAuthClientServerStatisticsProvider dynaAuthClientServerStatisticsProvider, int serverIndex){
		this.serverIndex = serverIndex;
		this.ipAddress = ipAddress;
		this.dynaAuthClientServerStatisticsProvider = dynaAuthClientServerStatisticsProvider;
	}

	@Override
	public Long getRadiusDynAuthClientCounterDiscontinuity()
			throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCounterDiscontinuity(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientUnknownTypes(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientUnknownTypes(ipAddress));	
	}

	@Override
	public Long getRadiusDynAuthClientCoAPacketsDropped()
			throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoAPacketsDropped(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientCoARequests(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoARequests(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconPacketsDropped()
			throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconPacketsDropped(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconTimeouts()
			throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconTimeouts(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconPendingRequests()
			throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconPendingRequests(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconBadAuthenticators(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconBadAuthenticators(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientMalformedDisconResponses(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconMalformedResponses(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconNakSessNoContext(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconNakSessNoContext(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconNakAuthOnlyRequest(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconNakAuthOnlyRequest(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconNaks(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconNaks(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconAcks(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconAcks(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconRetransmissions(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconRetransmissions(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconAuthOnlyRequests(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconAuthOnlyRequests(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientCoATimeouts(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoATimeouts(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientDisconRequests(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconRequests(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientCoAPendingRequests(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoAPendingRequests(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientRoundTripTime(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientRoundTripTime(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientCoABadAuthenticators(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoABadAuthenticators(ipAddress));
	}

	@Override
	public String getRadiusDynAuthServerID(){
		return dynaAuthClientServerStatisticsProvider.getRadiusDynAuthServerID(ipAddress);
	}

	@Override
	public Long getRadiusDynAuthClientMalformedCoAResponses(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientMalformedCoAResponses(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthServerClientPortNumber(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthServerClientPortNumber(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientCoANakSessNoContext(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoANakSessNoContext(ipAddress));
	}

	@Override
	public Byte[] getRadiusDynAuthServerAddress(){
		byte[] bs = ipAddress.getBytes();
		Byte[] bytes = new Byte[bs.length];
		for(int i=0; i< bs.length ; i++){
			bytes[i] = bs[i];
		}
		return bytes; 
	}

	@Override
	public Long getRadiusDynAuthClientCoANakAuthOnlyRequest(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoANakAuthOnlyRequest(ipAddress));
	}

	@Override
	public EnumRadiusDynAuthServerAddressType getRadiusDynAuthServerAddressType(){
		return dynaAuthClientServerStatisticsProvider.getRadiusDynAuthServerAddressType(ipAddress);
	}

	@Override
	public Long getRadiusDynAuthClientCoANaks(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoANaks(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientCoAAcks(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoAAcks(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientCoARetransmissions(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoARetransmissions(ipAddress));
	}

	@Override
	public Long getRadiusDynAuthClientCoAAuthOnlyRequest(){
		return SnmpCounterUtil.convertToCounter32(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoAAuthOnlyRequest(ipAddress));
	}

	@Override
	public Integer getRadiusDynAuthServerIndex(){
		return serverIndex;
	}

	public String getObjectName() {
		return MBeanConstants.STATISTICS_PROTOCOL_RADIUS +  "dynaauth,table=RadiusDynaAuthServerTable,entry=" + "RadiusDynaAuthServerTableEntry-" + ipAddress;
	}

	public String getRadiusDynAuthServerIPAddress() {
		return ipAddress;
	}

}