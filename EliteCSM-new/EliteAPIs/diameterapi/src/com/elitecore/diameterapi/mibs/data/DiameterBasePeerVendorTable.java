package com.elitecore.diameterapi.mibs.data;

import com.elitecore.diameterapi.mibs.constants.RowStatus;
import com.elitecore.diameterapi.mibs.constants.StorageTypes;

public class DiameterBasePeerVendorTable {

	private int dbpPeerVendorIndex;
	private String dbpPeerVendorId = "";
	private StorageTypes dbpPeerVendorStorageType = StorageTypes.NON_VOLATILE;
	private RowStatus dbpPeerVendorRowStatus= RowStatus.CREATE_AND_GO;
	
	public DiameterBasePeerVendorTable(int dbpPeerVendorIndex, String dbpPeerVendorId, 
			StorageTypes dbpPeerVendorStorageType,RowStatus dbpPeerVendorRowStatus) {

		this.dbpPeerVendorIndex =dbpPeerVendorIndex ;
		if(dbpPeerVendorId != null)
			this.dbpPeerVendorId = dbpPeerVendorId;
		this.dbpPeerVendorStorageType = dbpPeerVendorStorageType;
		this.dbpPeerVendorRowStatus = dbpPeerVendorRowStatus;
	}
	
	public int getDbpPeerVendorIndex() {
		return dbpPeerVendorIndex;
	}

	public String getDbpPeerVendorId() {
		return dbpPeerVendorId;
	}

	public int getDbpPeerVendorStorageType() {
		return dbpPeerVendorStorageType.code;
	}

	public int getDbpPeerVendorRowStatus() {
		return dbpPeerVendorRowStatus.code;
	}

}
