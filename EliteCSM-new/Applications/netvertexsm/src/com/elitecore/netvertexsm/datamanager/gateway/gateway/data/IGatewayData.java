package com.elitecore.netvertexsm.datamanager.gateway.gateway.data;

import java.sql.Timestamp;
import java.util.Set;

public interface IGatewayData {
	public long getGatewayId();
	public void setGatewayId(long gatewayId);
	public void setGatewayName(String gatewayName);
	public String getGatewayName();
	public String getCommProtocol();
	public void setCommProtocol(String commProtocol);
	public String getConnectionUrl();
	public void setConnectionUrl(String connectionUrl);
	public Integer getLocationId();
	public void setLocationId(Integer locationId);
	public String getDescription();
	public void setDescription(String description);
	public long getProfileId();
	public void setProfileId(long profileId);
	public String getAreaName();
	public void setAreaName(String areaName);
	
	public String getPolicyEnforcementMethodName();		
	public void setPolicyEnforcementMethodName(String policyEnforcementMethodName);	
	public long getTotalPages();
	public void setTotalPages(long totalPages);
	public long getTotalRecords();
	public void setTotalRecords(long totalRecords);
	public String getStatus();
	public void setStatus(String status);
	
	
	public String getSharedSecret();
	public void setSharedSecret(String sharedSecret);
	public Integer getTimeout();
	public void setTimeout(Integer timeout);
	public Integer getMaxRequestTimeout();
	public void setMaxRequestTimeout(Integer maxRequestTimeout);
	public String getHostId();
	public void setHostId(String hostId);
	public String getRealm();
	public void setRealm(String realm);
	public String getTlsEnable();
	public void setTlsEnable(String tlsEnable);
	public Integer getRetransmissionCnt();
	public void setRetransmissionCnt(Integer retransmissionCnt);
	public String toString();	
	
	public RadiusGatewayData getRadiusGatewayData();
	public void setRadiusGatewayData(RadiusGatewayData radiusGatewayData);
	public DiameterGatewayData getDiameterGatewayData();
	public void setDiameterGatewayData(DiameterGatewayData diameterGatewayData);
	public void setModifiedDate(Timestamp modifiedDate);
	public void setModifiedByStaffId(Long modifiedByStaffId);
	public Timestamp getCreatedDate();
	public void setCreatedDate(Timestamp createdDate);
	public Timestamp getModifiedDate();
	public Long getCreatedByStaffId();
	public void setCreatedByStaffId(Long createdByStaffId);
	public Long getModifiedByStaffId();
	public Set<GatewayData> getGatewaySet();
	public void setGatewaySet(Set<GatewayData> gatewaySet);
}