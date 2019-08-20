package com.elitecore.diameterapi.mibs.data;

import com.elitecore.diameterapi.mibs.constants.ServiceTypes;


public class DiameterBasePeerAppAdvTable {

	private long dbpAppAdvFromPeerVendorId;
	private long dbpAppId;
	private ServiceTypes dbpAppAdvFromPeerType;
	
	public DiameterBasePeerAppAdvTable(long dbpAppAdvFromPeerVendorId,long dbpAppId, ServiceTypes dbpAppAdvFromPeerType) {
		this.dbpAppAdvFromPeerVendorId = dbpAppAdvFromPeerVendorId ;
		this.dbpAppId = dbpAppId;
		this.dbpAppAdvFromPeerType = dbpAppAdvFromPeerType;
	}

	public long getDbpAppAdvFromPeerVendorId() {
		return dbpAppAdvFromPeerVendorId;
	}

	public long getDbpAppId() {
		return dbpAppId;
	}

	public int getDbpAppAdvFromPeerType() {
		return dbpAppAdvFromPeerType.code;
	}
	
	public void setDbpAppAdvFromPeerType(ServiceTypes dbpAppAdvFromPeerType) {
		this.dbpAppAdvFromPeerType = dbpAppAdvFromPeerType;
	}

	public String getAppAdvName() {
		return dbpAppAdvFromPeerVendorId + "-" + dbpAppId + "(" + dbpAppAdvFromPeerType + ")";
	}

}
