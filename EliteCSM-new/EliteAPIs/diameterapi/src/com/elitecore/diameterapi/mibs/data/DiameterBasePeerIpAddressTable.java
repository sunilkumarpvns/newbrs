package com.elitecore.diameterapi.mibs.data;

import com.elitecore.diameterapi.mibs.constants.IpAddressTypes;

public class DiameterBasePeerIpAddressTable {

	private int dbpPeerIpAddressIndex;
	private IpAddressTypes dbpPeerIpAddressType;
	private String dbpPeerIpAddress;
	
	
	public DiameterBasePeerIpAddressTable(int dbpPeerIpAddressIndex,IpAddressTypes dbpPeerIpAddressType,String dbpPeerIpAddress) {
		this.dbpPeerIpAddressIndex = dbpPeerIpAddressIndex;
		this.dbpPeerIpAddressType = dbpPeerIpAddressType ;
		this.dbpPeerIpAddress = dbpPeerIpAddress;
	}
	
	public int getPeerIpAddressIndex(){
		return dbpPeerIpAddressIndex;
	}
	public int getdbpPeerIpAddressType(){
		return dbpPeerIpAddressType.code;
	}
	public String getPeerIpAddress(){
		return dbpPeerIpAddress;
	}


}
