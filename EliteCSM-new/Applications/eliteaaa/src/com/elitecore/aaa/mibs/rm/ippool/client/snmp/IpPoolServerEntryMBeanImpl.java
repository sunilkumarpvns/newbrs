package com.elitecore.aaa.mibs.rm.ippool.client.snmp;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.rm.ippool.client.RMIPPoolClientMIB;
import com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.IpPoolServerEntry;
import static com.elitecore.core.util.mbean.SnmpCounterUtil.convertToCounter32;

public class IpPoolServerEntryMBeanImpl extends IpPoolServerEntry{

	private int ipPoolServIndex;
	private String ipPoolServName;
	private String ipPoolServAddress;
	
	public IpPoolServerEntryMBeanImpl(int ipPoolServerIndex,String ipPoolServerName,String ipPoolServerAddress){
		this.ipPoolServIndex = ipPoolServerIndex;
		this.ipPoolServName = ipPoolServerName;
		this.ipPoolServAddress = ipPoolServerAddress;
	}
	
	@Override
	public Long getIpPoolServDeclineResponse() {
		return RMIPPoolClientMIB.getIpAddressDeclineResponse(ipPoolServName);
	}

	@Override
	public Long getIpPoolServOfferResponse() {
		return convertToCounter32(RMIPPoolClientMIB.getIpAddressOfferResponse(ipPoolServName));
	}

	@Override
	public Long getIpPoolServDiscoverRequest() {
		return convertToCounter32(RMIPPoolClientMIB.getIpAddressDiscoverRequest(ipPoolServName));
	}

	@Override
	public Long getIpPoolServRequestRetransmission() {
		return convertToCounter32(RMIPPoolClientMIB.getIpAddressRequestRetransmission(ipPoolServName));
	}

	@Override
	public Long getIpPoolServRequestTimeOut() {
		return convertToCounter32(RMIPPoolClientMIB.getIpAddressRequestTimeout(ipPoolServName));
	}

	@Override
	public Long getIpPoolServUpdateRequest() {
		return convertToCounter32(RMIPPoolClientMIB.getIpAddressUpdateRequest(ipPoolServName));
	}

	@Override
	public Long getIpPoolServResponses() {
		return convertToCounter32(RMIPPoolClientMIB.getIpAddressTotalResponse(ipPoolServName));
	}

	@Override
	public Long getIpPoolServRequest() {
		return convertToCounter32(RMIPPoolClientMIB.getIpAddressTotalRequest(ipPoolServName));
	}

	@Override
	public Long getIpPoolServReleaseRequest() {
		return convertToCounter32(RMIPPoolClientMIB.getIpAddressReleaseRequest(ipPoolServName));
	}

	@Override
	public Long getIpPoolServAllocationResponse() {
		return convertToCounter32(RMIPPoolClientMIB.getIpaddressAllocationResponse(ipPoolServName));
	}

	@Override
	public String getIpPoolServerAddress() {
		return ipPoolServAddress;
	}

	@Override
	public Long getIpPoolServAllocationRequest() {
		return convertToCounter32(RMIPPoolClientMIB.getIpAddressAllocationRequest(ipPoolServName));
	}

	@Override
	public Integer getIpPoolServerIndex() {
		return ipPoolServIndex;
	}

	@Override
	public String getIpPoolServerName(){
		return ipPoolServName;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.IP_POOL_SERVER_TABLE + getIpPoolServerName();
	}
}
