package com.elitecore.netvertexsm.datamanager.locationconfig.area.data;

import java.io.Serializable;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;

public class AreaNetworkRelationData implements Serializable{

	private static final long serialVersionUID = 1L;
	private long areaId;
	private long networkId;
	private AreaData areaData;
	private NetworkData networkData;
	
	public long getAreaId() {
		return areaId;
	}
	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}
	public long getNetworkId() {
		return networkId;
	}
	public void setNetworkId(long networkId) {
		this.networkId = networkId;
	}
	public AreaData getAreaData() {
		return areaData;
	}
	public void setAreaData(AreaData areaData) {
		this.areaData = areaData;
	}
	public NetworkData getNetworkData() {
		return networkData;
	}
	public void setNetworkData(NetworkData networkData) {
		this.networkData = networkData;
	}
}
