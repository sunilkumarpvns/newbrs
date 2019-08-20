package com.elitecore.aaa.rm.service.ippool.snmp;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.rm.ippool.server.RMIPPoolServiceMIBListener;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.IpPoolNASClientEntry;

public class IpPoolNasClientEntryMBeanImpl extends IpPoolNASClientEntry{

	private int nasClientIndex;
	private String nasClientIdentity;
	private RMIPPoolServiceMIBListener ipPoolServiceListener;
	
	public IpPoolNasClientEntryMBeanImpl(int nasClientIndex , String nasClientIdentity , RMIPPoolServiceMIBListener ipPoolServiceListener){
		this.nasClientIndex = nasClientIndex;
		this.nasClientIdentity = nasClientIdentity;
		this.ipPoolServiceListener = ipPoolServiceListener;
	}
	
	@Override
	public Long getNasIPAddressDiscoverRequest(){
		return ipPoolServiceListener.getNASIPPoolServTotalDiscoverRequest(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressInvalidRequest(){
		return ipPoolServiceListener.getNASIPPoolServTotalInvalidRequests(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressUnknownPacket(){
		return ipPoolServiceListener.getNASIPPoolServTotalUnknownTypes(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressRequestDropped(){
		return ipPoolServiceListener.getNASIPPoolServTotalPacketsDropped(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressUpdateRequest(){
		return ipPoolServiceListener.getNASIPPoolServTotalUpdateRequest(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressResponses(){
		return ipPoolServiceListener.getNASIPPoolServTotalResponse(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressRequest(){
		return ipPoolServiceListener.getNASIPPoolServTotalRequest(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressReleaseRequest(){
		return ipPoolServiceListener.getNASIPPoolServTotalReleaseRequest(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressAllocationRequest(){
		return ipPoolServiceListener.getNASIPPoolServTotalAllocationRequest(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressDeclineResponse(){
		return ipPoolServiceListener.getNASIPPoolServTotalDeclineResponse(nasClientIdentity);
	}

	@Override
	public Long getNasIPAddressOfferResponse(){
		return ipPoolServiceListener.getNASIPPoolServTotalOfferResponse(nasClientIdentity);
	}

	@Override
	public Integer getIpPoolNASClientIndex(){
		return nasClientIndex;
	}

	@Override
	public String getNasID(){
		return nasClientIdentity;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.IP_POOL_NAS_CLIENT_TABLE + getNasID();
	}
}
