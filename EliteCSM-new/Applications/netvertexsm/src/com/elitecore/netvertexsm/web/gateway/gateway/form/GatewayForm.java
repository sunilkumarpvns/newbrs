package com.elitecore.netvertexsm.web.gateway.gateway.form;

import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayLocationData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class GatewayForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	private long gatewayId;
	private String gatewayName;
	private String areaName;	
	private int pccProvision;
	
	private String commProtocolId;
	private String connectionUrl;
	private String locationName;
	private String policyEnforcementMethodName;
	private Integer locationId;	
	private String description;	
	
	private int radiusGatewayProfileId;	
	private int diameterGatewayProfileId;	
	private long gatewayProfileId;
	private String action;
	
	List<GatewayProfileData> radiusProfileList;
	List<GatewayProfileData> diameterProfileList;
	private List<GatewayProfileData> gatewayProfileList;
	private List<GatewayLocationData> locationList;
	private Set<DiameterProfileData> diameterProfileSet;
	
	
	public Set<DiameterProfileData> getDiameterProfileSet() {
		return diameterProfileSet;
	}
	public void setDiameterProfileSet(Set<DiameterProfileData> diameterProfileSet) {
		this.diameterProfileSet = diameterProfileSet;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public int getPccProvision() {
		return pccProvision;
	}
	public void setPccProvision(int pccProvision) {
		this.pccProvision = pccProvision;
	}
	public String getCommProtocolId() {
		return commProtocolId;
	}
	public void setCommProtocolId(String commProtocolId) {
		this.commProtocolId = commProtocolId;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<GatewayProfileData> getGatewayProfileList() {
		return gatewayProfileList;
	}
	public void setGatewayProfileList(List<GatewayProfileData> gatewayProfileList) {
		this.gatewayProfileList = gatewayProfileList;
	}
	public List<GatewayLocationData> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<GatewayLocationData> locationList) {
		this.locationList = locationList;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getRadiusGatewayProfileId() {
		return radiusGatewayProfileId;
	}
	public void setRadiusGatewayProfileId(int radiusGatewayProfileId) {
		this.radiusGatewayProfileId = radiusGatewayProfileId;
	}
	public int getDiameterGatewayProfileId() {
		return diameterGatewayProfileId;
	}
	public void setDiameterGatewayProfileId(int diameterGatewayProfileId) {
		this.diameterGatewayProfileId = diameterGatewayProfileId;
	}
	public List<GatewayProfileData> getRadiusProfileList() {
		return radiusProfileList;
	}
	public void setRadiusProfileList(List<GatewayProfileData> radiusProfileList) {
		this.radiusProfileList = radiusProfileList;
	}
	public List<GatewayProfileData> getDiameterProfileList() {
		return diameterProfileList;
	}
	public void setDiameterProfileList(List<GatewayProfileData> diameterProfileList) {
		this.diameterProfileList = diameterProfileList;
	}
	public long getGatewayProfileId() {
		return gatewayProfileId;
	}
	public void setGatewayProfileId(long gatewayProfileId) {
		this.gatewayProfileId = gatewayProfileId;
	}
	public long getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(long gatewayId) {
		this.gatewayId = gatewayId;
	}
	public String getPolicyEnforcementMethodName() {
		return policyEnforcementMethodName;
	}
	public void setPolicyEnforcementMethodName(String policyEnforcementMethodName) {
		this.policyEnforcementMethodName = policyEnforcementMethodName;
	}
	
}
