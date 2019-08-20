package com.elitecore.netvertexsm.datamanager.locationconfig.area.data;

import java.util.Set;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;;

public class LacData {
	
	private long lacId;
	private Long lacCode;
	private String strCellIds;
	private String strRacs;
	private String strSacs;
	private long networkId;
	private NetworkData networkData;
	private Set<AreaData> areaDataSet;
	
	public long getLacId() {
		return lacId;
	}
	public void setLacId(long lacId) {
		this.lacId = lacId;
	}
	public Long getLacCode() {
		return lacCode;
	}
	public void setLacCode(Long lacCode) {
		this.lacCode = lacCode;
	}
		public long getNetworkId() {
		return networkId;
	}
	public void setNetworkId(long networkId) {
		this.networkId = networkId;
	}
	public String getStrCellIds() {
		return strCellIds;
	}
	public void setStrCellIds(String strCellIds) {
		this.strCellIds = strCellIds;
	}
	public String getStrRacs() {
		return strRacs;
	}
	public void setStrRacs(String strRacs) {
		this.strRacs = strRacs;
	}
	public String getStrSacs() {
		return strSacs;
	}
	public void setStrSacs(String strSacs) {
		this.strSacs = strSacs;
	}
	public NetworkData getNetworkData() {
		return networkData;
	}
	public void setNetworkData(NetworkData networkData) {
		this.networkData = networkData;
	}
	public Set<AreaData> getAreaDataSet() {
		return areaDataSet;
	}
	public void setAreaDataSet(
			Set<AreaData> areaDataSet) {
		this.areaDataSet = areaDataSet;
	}
	

}
