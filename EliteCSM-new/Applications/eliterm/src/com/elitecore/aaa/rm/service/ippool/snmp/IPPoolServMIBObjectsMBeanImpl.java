package com.elitecore.aaa.rm.service.ippool.snmp;

import com.elitecore.aaa.mibs.rm.ippool.server.RMIPPoolServiceMIBListener;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.EnumIpPoolServiceReset;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.IpPoolServMIBObjects;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.TableIpPoolAAAClientStatisticsTable;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.TableIpPoolNASClientStatisticsTable;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.snmp.MIBStates;
import static com.elitecore.core.util.mbean.SnmpCounterUtil.convertToCounter32;

public class IPPoolServMIBObjectsMBeanImpl extends IpPoolServMIBObjects {

	private final String MODULE = "IPPOOL-SERV-MBEAN-IMPL";
	private RMIPPoolServiceMIBListener ipPoolServiceListener;
	
	public IPPoolServMIBObjectsMBeanImpl(RMIPPoolServiceMIBListener ipPoolServiceListener) {
		this.ipPoolServiceListener = ipPoolServiceListener;
	}
	
	@Override
	public Long getIpPoolServTotalInvalidRequest(){
		return convertToCounter32(ipPoolServiceListener.getRMIPPoolServTotalInvalidRequests());
	}

	@Override
	public Long getIpPoolServTotalUnknownPacket(){
		return convertToCounter32(ipPoolServiceListener.getRMIPPoolServTotalUnknownTypes());
	}

	@Override
	public Long getIpPoolServTotalDuplicateRequest() {
		return convertToCounter32(ipPoolServiceListener.getRMIPPoolServTotalDupRequests());
	}

	@Override
	public Long getIpPoolServTotalUpdateRequest(){
		return convertToCounter32(ipPoolServiceListener.getIPAddressTotalUpdateRequest());
	}

	@Override
	public Long getIpPoolServTotalRequestDropped(){
		return convertToCounter32(ipPoolServiceListener.getRMIPPoolServTotalPacketsDropped());
	}

	@Override
	public Long getIpPoolServTotalReleaseRequest(){
		return convertToCounter32(ipPoolServiceListener.getIPAddressTotalReleaseRequest());
	}

	@Override
	public Long getIpPoolServTotalResponses(){
		return convertToCounter32(ipPoolServiceListener.getRMIPPoolServTotalResponses());
	}

	@Override
	public Long getIpPoolServTotalRequest(){
		return convertToCounter32(ipPoolServiceListener.getRMIPPoolServTotalRequests());
	}

	@Override
	public Long getIpPoolServTotalAllocationRequest(){
		return convertToCounter32(ipPoolServiceListener.getIPAddressTotalAllocationRequest());
	}

	@Override
	public Long getIpPoolServiceResetTime(){
		return ipPoolServiceListener.getRMIPPoolServResetTime();
	}

	@Override
	public TableIpPoolNASClientStatisticsTable accessIpPoolNASClientStatisticsTable(){
		return null;
	}

	@Override
	public Long getIpPoolServTotalDeclineResponse(){
		return convertToCounter32(ipPoolServiceListener.getIPAddressDeclineTotalResponse());
	}

	@Override
	public Long getIpPoolServTotalOfferResponse(){
		return convertToCounter32(ipPoolServiceListener.getIPAddressOfferTotalResponse());
	}

	@Override
	public EnumIpPoolServiceReset getIpPoolServiceReset(){
		int serviceConfigState = ipPoolServiceListener.getState().getStateValue();
		return new EnumIpPoolServiceReset(serviceConfigState);
	}

	@Override
	public void setIpPoolServiceReset(EnumIpPoolServiceReset x){
		if(MIBStates.RESET.getStateValue() == x.intValue()){
			ipPoolServiceListener.resetCounter();
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Invalid argument is provided for counter reset operation: "+x.intValue());
			}
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void checkIpPoolServiceReset(EnumIpPoolServiceReset x){
		
	}

	@Override
	public TableIpPoolAAAClientStatisticsTable accessIpPoolAAAClientStatisticsTable(){
		return null;
	}

	@Override
	public Long getIpPoolServTotalDiscoverRequest(){
		return convertToCounter32(ipPoolServiceListener.getIPAddressDiscoverTotalRequest());
	}

	@Override
	public Long getIpPoolServiceUpTime(){
		return ipPoolServiceListener.getRMIPPoolServUpTime();
	}
}
