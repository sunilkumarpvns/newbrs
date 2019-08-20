package com.elitecore.aaa.rm.service.chargingservice.snmp;

import com.elitecore.aaa.mibs.rm.chargingservice.server.RMChargingServiceMIBListener;
import com.elitecore.aaa.rm.service.chargingservice.snmp.autogen.ChargingServMIBObjects;
import com.elitecore.aaa.rm.service.chargingservice.snmp.autogen.EnumChargingServiceReset;
import com.elitecore.aaa.rm.service.chargingservice.snmp.autogen.TableChargingClientStatsTable;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.snmp.MIBStates;
import com.sun.management.snmp.SnmpStatusException;
import static com.elitecore.core.util.mbean.SnmpCounterUtil.convertToCounter32;

public class ChargingServMIBMBeanImpl extends ChargingServMIBObjects{

	private static final String MODULE = "RM-CHARGING-SRV-MBEAN-IMPL";
	
	private RMChargingServiceMIBListener chargingListener;
	
	public ChargingServMIBMBeanImpl(RMChargingServiceMIBListener chargingListener){
		this.chargingListener = chargingListener;
	}

	@Override
	public Long getChargingServTotalAcctStopRequest(){
		return convertToCounter32(chargingListener.getRmChargingServTotalAcctStopRequest());
	}

	@Override
	public Long getChargingServTotalAcctStartRequest(){
		return convertToCounter32(chargingListener.getRmChargingServTotalAcctStartRequest());
	}

	@Override
	public Long getChargingServTotalAcctResponse(){
		return convertToCounter32(chargingListener.getRmChargingServTotalAcctResponse());
	}

	@Override
	public Long getChargingServTotalAcctRequest(){
		return convertToCounter32(chargingListener.getRmChargingServTotalAcctRequest());
	}

	@Override
	public Long getChargingServTotalAccessReject(){
		return convertToCounter32(chargingListener.getRMChargingServTotalAccessRejects());
	}

	@Override
	public Long getChargingServTotalAccessAccept(){
		return convertToCounter32(chargingListener.getRmChargingServTotalAccessAccept());
	}

	@Override
	public Long getChargingServTotalAccessRequest(){
		return convertToCounter32(chargingListener.getRmChargingServTotalAccessRequest());
	}

	@Override
	public Long getChargingServTotalUnknownTypes(){
		return convertToCounter32(chargingListener.getRMChargingServTotalUnknownTypes());
	}

	@Override
	public Long getChargingServTotalPacketsDropped(){
		return convertToCounter32(chargingListener.getRMChargingServTotalPacketsDropped());
	}

	@Override
	public Long getChargingServTotalBadAuthenticators(){
		return convertToCounter32(chargingListener.getRMChargingServTotalBadAuthenticators());
	}

	@Override
	public Long getChargingServTotalMalformedRequests(){
		return convertToCounter32(chargingListener.getRMChargingServTotalMalformedRequests());
	}

	@Override
	public Long getChargingServTotalDupRequests(){
		return convertToCounter32(chargingListener.getRMChargingServTotalDupRequests());
	}

	@Override
	public Long getChargingServTotalInvalidRequests(){
		return convertToCounter32(chargingListener.getRMChargingServTotalInvalidRequests());
	}

	@Override
	public Long getChargingServTotalResponses(){
		return convertToCounter32(chargingListener.getRMChargingServTotalResponses());
	}

	@Override
	public Long getChargingServTotalRequests(){
		return convertToCounter32(chargingListener.getRMChargingServTotalRequests());
	}

	@Override
	public Long getChargingServResetTime(){
		return ( System.currentTimeMillis() - chargingListener.getRMChargingServResetTime() )/ 10;
	}

	@Override
	public EnumChargingServiceReset getChargingServiceReset(){
		int stateValue = chargingListener.getMibStates().getStateValue();
		return new EnumChargingServiceReset(stateValue);
	}

	@Override
	public void setChargingServiceReset(EnumChargingServiceReset x){
		if(MIBStates.RESET.getStateValue() == x.intValue()){
			chargingListener.resetCounter();
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Invalid argument is provided for counter reset operation: "+x.intValue());
			}
			throw new IllegalArgumentException("Invalid argument is provided for counter reset operation: "+x.valueStrings());
		}
	}

	@Override
	public Long getChargingServUpTime(){
		return (System.currentTimeMillis() - chargingListener.getRMChargingServUpTime()) / 10;
	}

	@Override
	public Long getChargingServTotalAcctUpdateRequest(){
		return convertToCounter32(chargingListener.getRmChargingServTotalAcctUpdateRequest());
	}

	@Override
	public TableChargingClientStatsTable accessChargingClientStatsTable(){
		return null;
	}

	@Override
	public void checkChargingServiceReset(EnumChargingServiceReset x)
			throws SnmpStatusException {
	}
}
