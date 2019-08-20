package com.elitecore.diameterapi.mibs.data;

import com.elitecore.diameterapi.mibs.constants.RowStatus;
import com.elitecore.diameterapi.mibs.constants.StorageTypes;

public class DiameterBasePeerTable {

	private int dbpPeerIndex;
	private String dbpPeerId="";
	//dbpPeerPortConnect

	private int dbpPeerFirmwareRevision;
	private int dbpPeerStorageType;
	private int dbpPeerRowStatus;
	
	public DiameterBasePeerTable() {
		// TODO Auto-generated constructor stub
	}
	
	public DiameterBasePeerTable(int dbpPeerIndex,String dbpPeerId, int dbpPeerFirmwareRevision, int dbpPeerStorageType, int dbpPeerRowStatus) {
		this.dbpPeerIndex =  dbpPeerIndex;
		if(dbpPeerId!=null)
			this.dbpPeerId = dbpPeerId ;
		
		this.dbpPeerFirmwareRevision = dbpPeerFirmwareRevision;
		this.dbpPeerStorageType = dbpPeerStorageType;
		this.dbpPeerRowStatus = dbpPeerRowStatus;
	}
	
	@Override
	public String toString(){
		StringBuilder responseBuilder = new StringBuilder();
		
		responseBuilder.append("\n---------------------------  Basic Configuration  --------------------------------");
		
		responseBuilder.append("\nIndex                                                 : "+this.dbpPeerIndex);
		responseBuilder.append("\nId                                                    : "+this.dbpPeerId);
		responseBuilder.append("\nFirmware Revision                                     : "+this.dbpPeerFirmwareRevision);
		responseBuilder.append("\nStorage Type                                          : "+StorageTypes.getStorageTypeString(this.dbpPeerStorageType));
		responseBuilder.append("\nRow Status                                            : "+RowStatus.getRowStatusTypeString(this.dbpPeerRowStatus));
	
		
		return responseBuilder.toString();
	}
	public String getPeerId(){
		return dbpPeerId;
	}
	public int getPeerIndex(){
		return dbpPeerIndex;
	}
	public int getPeerFirmwareRevison(){
		return dbpPeerFirmwareRevision;
	}
	public int getPeerStorageType(){
		return dbpPeerStorageType;
	}
	public int getPeerRowStatus(){
		return dbpPeerRowStatus;
	}

}
