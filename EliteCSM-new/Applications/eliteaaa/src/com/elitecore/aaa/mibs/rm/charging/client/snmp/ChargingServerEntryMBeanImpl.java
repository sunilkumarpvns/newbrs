package com.elitecore.aaa.mibs.rm.charging.client.snmp;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.rm.charging.client.RMChargingClientMIB;
import com.elitecore.aaa.mibs.rm.charging.client.snmp.autogen.ChargingServerEntry;
import com.sun.management.snmp.SnmpStatusException;

public class ChargingServerEntryMBeanImpl extends ChargingServerEntry{

	private int chargingServerIndex;
	private String chargingServerName;
	private String chargingServerAddress; 

	public ChargingServerEntryMBeanImpl(int chargingServerIndex,String chargingServerName,String chargingServerAddress) {
		this.chargingServerIndex = chargingServerIndex;
		this.chargingServerName = chargingServerName;
		this.chargingServerAddress = chargingServerAddress;
	}
	
	@Override
	public Long getChargingAccessAccept(){
		return RMChargingClientMIB.getRmChargingAccessAccept(chargingServerName);
	}

	@Override
	public Long getChargingAccessRequest(){
		return RMChargingClientMIB.getRmChargingAccessRequest(chargingServerName);
	}

	@Override
	public Long getChargingUnknownTypes(){
		return RMChargingClientMIB.getRmChargingUnknownRequestType(chargingServerName);
	}

	@Override
	public Long getChargingAcctUpdateRequest(){
		return RMChargingClientMIB.getRmChargingAcctUpdateRequest(chargingServerName);
	}

	@Override
	public Long getChargingPacketsDropped(){
		return RMChargingClientMIB.getRmChargingRequestDropped(chargingServerName);
	}

	@Override
	public Long getChargingResponses(){
		return RMChargingClientMIB.getRmChargingResponses(chargingServerName);
	}

	@Override
	public Long getChargingAcctStopRequest(){
		return RMChargingClientMIB.getRmChargingAcctStopRequest(chargingServerName);
	}

	@Override
	public Long getChargingRequests(){
		return RMChargingClientMIB.getRmChargingRequest(chargingServerName);
	}

	@Override
	public Long getChargingAcctStartRequest(){
		return RMChargingClientMIB.getRmChargingAcctStartRequest(chargingServerName);
	}

	@Override
	public Long getChargingAcctResponse(){
		return RMChargingClientMIB.getRmChargingAcctResponse(chargingServerName);
	}

	@Override
	public String getChargingServerAddress(){
		return chargingServerAddress;
	}

	@Override
	public Long getChargingAcctRequest(){
		return RMChargingClientMIB.getRmChargingAcctRequest(chargingServerName);
	}

	@Override
	public String getChargingServerName(){
		return chargingServerName;
	}

	@Override
	public Long getChargingAccessReject(){
		return RMChargingClientMIB.getRmChargingAccessReject(chargingServerName);
	}

	@Override
	public Integer getChargingServerIndex(){
		return chargingServerIndex;
	}

	@Override
	public Long getChargingRequestTimeout(){
		return RMChargingClientMIB.getRmChargingRequestTimeout(chargingServerName);
	}
	
	@Override
	public Long getChargingRequestRetransmission() throws SnmpStatusException {
		return RMChargingClientMIB.getRmChargingRequestRetransmission(chargingServerName);
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.CHARGING_SERVER_TABLE + getChargingServerName();
	}
}