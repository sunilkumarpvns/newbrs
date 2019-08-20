package com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data;



import java.io.Serializable;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.web.core.base.BaseData;

public class MCCMNCCodeGroupRelData extends BaseData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long mccmncGroupId;
	private Long mccMNCID;
	private NetworkData mccmncCodeData;
	
	public long getMccmncGroupId() {
		return mccmncGroupId;
	}
	public void setMccmncGroupId(long mccmncGroupId) {
		this.mccmncGroupId = mccmncGroupId;
	}
	public long getMccMNCID() {
		return mccMNCID;
	}
	public void setMccMNCID(long mccMNCID) {
		this.mccMNCID = mccMNCID;
	}
	public NetworkData getMccmncCodeData() {
		return mccmncCodeData;
	}
	public void setMccmncCodeData(NetworkData mccmncCodeData) {
		this.mccmncCodeData = mccmncCodeData;
	}
	
}
