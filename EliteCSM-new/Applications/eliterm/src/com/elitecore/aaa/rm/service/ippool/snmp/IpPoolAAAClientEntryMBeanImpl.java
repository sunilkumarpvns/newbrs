package com.elitecore.aaa.rm.service.ippool.snmp;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.rm.ippool.server.RMIPPoolServiceMIBListener;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.IpPoolAAAClientEntry;

public class IpPoolAAAClientEntryMBeanImpl extends IpPoolAAAClientEntry{

	private String aaaClientAddress;
	private int aaaClientIndex;
	private RMIPPoolServiceMIBListener ipPoolServiceMIBListener;
	
	public IpPoolAAAClientEntryMBeanImpl(int aaaClientindex,String aaaClientAddress,RMIPPoolServiceMIBListener ipPoolServiceMIBListener){
		this.aaaClientIndex = aaaClientindex;
		this.aaaClientAddress = aaaClientAddress;
		this.ipPoolServiceMIBListener = ipPoolServiceMIBListener;
	}
	
	@Override
	public Long getAaaIPAddressInvalidRequest(){
		return ipPoolServiceMIBListener.getRMIPPoolServTotalInvalidRequests(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressUnknownPacket(){
		return ipPoolServiceMIBListener.getRMIPPoolServTotalUnknownTypes(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressDuplicateRequest(){
		return ipPoolServiceMIBListener.getRMIPPoolServTotalDupRequests(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressRequestDropped(){
		return ipPoolServiceMIBListener.getRMIPPoolServTotalPacketsDropped(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressUpdateRequest(){
		return ipPoolServiceMIBListener.getIPAddressTotalUpdateRequest(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressResponses(){
		return ipPoolServiceMIBListener.getRMIPPoolServTotalResponses(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressReleaseRequest() {
		return ipPoolServiceMIBListener.getIPAddressTotalReleaseRequest(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressAllocationRequest() {
		return ipPoolServiceMIBListener.getIPAddressTotalAllocationRequest(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressRequest(){
		return ipPoolServiceMIBListener.getRMIPPoolServTotalRequests(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressDeclineResponse(){
		return ipPoolServiceMIBListener.getIPAddressDeclineTotalResponse(aaaClientAddress);
	}

	@Override
	public String getAaaIPAddress(){
		return aaaClientAddress;
	}

	@Override
	public Long getAaaIPAddressOfferResponse(){
		return ipPoolServiceMIBListener.getIPAddressOfferTotalResponse(aaaClientAddress);
	}

	@Override
	public String getAaaID(){
		return ipPoolServiceMIBListener.getClientId(aaaClientAddress);
	}

	@Override
	public Long getAaaIPAddressDiscoverRequest(){
		return ipPoolServiceMIBListener.getIPAddressDiscoverTotalRequest(aaaClientAddress);
	}

	@Override
	public Integer getIpPoolAAAClientIndex(){
		return aaaClientIndex;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.IP_POOL_AAA_CLIENT_TABLE + getAaaIPAddress();
	}
}
