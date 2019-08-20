package com.elitecore.aaa.rm.service.chargingservice.snmp;

import static com.elitecore.core.util.mbean.SnmpCounterUtil.convertToCounter32;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.rm.chargingservice.server.RMChargingServiceMIBListener;
import com.elitecore.aaa.rm.service.chargingservice.snmp.autogen.ChargingClientEntry;

public class ChargingClientEntryMBeanImpl extends ChargingClientEntry{

	private int clientIndex;
	private String clientAddress;
	private RMChargingServiceMIBListener chargingListner;

	public ChargingClientEntryMBeanImpl(int clientIndex,String clientAddress,RMChargingServiceMIBListener chargingListner) {
		this.clientIndex = clientIndex;
		this.clientAddress = clientAddress;
		this.chargingListner = chargingListner;
	}

	@Override
	public Long getChargingAcctStopRequest(){
		return convertToCounter32(chargingListner.getRmChargingServTotalAcctStopRequest(clientAddress));
	}

	@Override
	public Long getChargingAcctStartRequest(){
		return convertToCounter32(chargingListner.getRmChargingServTotalAcctStartRequest(clientAddress));
	}

	@Override
	public Long getChargingAcctResponse(){
		return convertToCounter32(chargingListner.getRmChargingServTotalAcctResponse(clientAddress));
	}

	@Override
	public Long getChargingAcctRequest(){
		return convertToCounter32(chargingListner.getRmChargingServTotalAcctRequest(clientAddress));
	}

	@Override
	public Long getChargingAccessReject(){
		return convertToCounter32(chargingListner.getRMChargingServTotalAccessRejects(clientAddress));
	}

	@Override
	public Long getChargingAccessAccept(){
		return convertToCounter32(chargingListner.getRmChargingServTotalAccessAccept(clientAddress));
	}

	@Override
	public Long getChargingAccessRequest(){
		return convertToCounter32(chargingListner.getRmChargingServTotalAccessRequest(clientAddress));
	}

	@Override
	public Long getChargingUnknownTypes(){
		return convertToCounter32(chargingListner.getRMChargingServTotalUnknownTypes(clientAddress));
	}

	@Override
	public Long getChargingPacketsDropped(){
		return convertToCounter32(chargingListner.getRMChargingServTotalPacketsDropped(clientAddress));
	}

	@Override
	public Long getChargingBadAuthenticators(){
		return convertToCounter32(chargingListner.getRMChargingServTotalBadAuthenticators(clientAddress));
	}

	@Override
	public Long getChargingMalformedRequests(){
		return convertToCounter32(chargingListner.getRMChargingServTotalMalformedRequests(clientAddress));
	}

	@Override
	public Long getChargingDupRequests(){
		return convertToCounter32(chargingListner.getRMChargingServTotalDupRequests(clientAddress));
	}

	@Override
	public Long getChargingInvalidRequests(){
		return 0L;
	}

	@Override
	public Long getChargingResponses(){
		return convertToCounter32(chargingListner.getRMChargingServTotalResponses(clientAddress));
	}

	@Override
	public Long getChargingRequests(){
		return convertToCounter32(chargingListner.getRMChargingServTotalRequests(clientAddress));
	}

	@Override
	public String getChargingClientAddress(){
		return clientAddress;
	}

	@Override
	public String getChargingClientID(){
		return clientAddress;
	}

	@Override
	public Integer getChargingClientIndex(){
		return clientIndex;
	}

	@Override
	public Long getChargingAcctUpdateRequest(){
		return convertToCounter32(chargingListner.getRmChargingServTotalAcctUpdateRequest(clientAddress));
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.CHARGING_CLIENT_TABLE + getChargingClientAddress();
	}
}
