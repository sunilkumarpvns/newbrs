package com.elitecore.aaa.mibs.rm.ippool.client.snmp;

import com.elitecore.aaa.mibs.rm.ippool.client.RMIPPoolClientMIB;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.IpPoolClientMIBObjects;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.IpPoolClientMIBObjectsMBean;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.TableIpPoolServerStatisticsTable;

public class IpPoolClientMIBObjectsMBeanImpl extends IpPoolClientMIBObjects {

	@Override
	public Long getIpPoolTotalAllocationResponse(){
		return RMIPPoolClientMIB.getIpaddressAllocationResponse();
	}
	
	@Override
	public Long getIpPoolTotalAllocationRequest() {
		return RMIPPoolClientMIB.getIpAddressAllocationRequest();
	}
	
	@Override
	public Long getIpPoolTotalDeclineResponse() {
		return RMIPPoolClientMIB.getIpAddressDeclineResponse();
	}

	@Override
	public Long getIpPoolTotalOfferResponse() {
		return RMIPPoolClientMIB.getIpAddressOfferResponse();
	}

	@Override
	public Long getIpPoolTotalDiscoverRequest() {
		return RMIPPoolClientMIB.getIpAddressDiscoverRequest();
	}

	@Override
	public Long getIpPoolTotalRequestRetransmission(){
		return RMIPPoolClientMIB.getIpAddressRequestRetransmission();
	}

	@Override
	public Long getIpPoolTotalRequestTimeOut() {
		return RMIPPoolClientMIB.getIpAddressRequestTimeout();
	}

	@Override
	public Long getIpPoolTotalResponses() {
		return RMIPPoolClientMIB.getIpAddressTotalResponse();
	}

	@Override
	public TableIpPoolServerStatisticsTable accessIpPoolServerStatisticsTable(){
		return null;
	}

	@Override
	public Long getIpPoolTotalUpdateRequest() {
		return RMIPPoolClientMIB.getIpAddressUpdateRequest();
	}

	@Override
	public Long getIpPoolTotalRequest() {
		return RMIPPoolClientMIB.getIpAddressTotalRequest();
	}

	@Override
	public Long getIpPoolTotalReleaseRequest() {
		return RMIPPoolClientMIB.getIpAddressReleaseRequest();
	}
}
