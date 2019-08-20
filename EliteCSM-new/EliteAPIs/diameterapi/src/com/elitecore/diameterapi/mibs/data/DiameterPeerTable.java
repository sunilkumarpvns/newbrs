package com.elitecore.diameterapi.mibs.data;

import com.elitecore.diameterapi.mibs.constants.TransportProtocols;

public class DiameterPeerTable extends DiameterBasePeerTable{
	private int dbpPeerPortListen;
	private int dbpPeerTransportProtocol;
	private int dbpPeerSecurity;
	private int dbpPeerPortConnect;
	
	public DiameterPeerTable() {
		// TODO Auto-generated constructor stub
	}
	
	
	public DiameterPeerTable(int dbpPeerIndex,String dbpPeerId, int dbpPeerPortListen, int dbpPeerTransportProtocol, int dbpPeerSecurity, int dbpPeerFirmwareRevision, int dbpPeerStorageType, int dbpPeerRowStatus,int localPort) {
		super(dbpPeerIndex,dbpPeerId, dbpPeerFirmwareRevision,  dbpPeerStorageType, dbpPeerRowStatus);
		
		this.dbpPeerPortListen = dbpPeerPortListen;
		this.dbpPeerTransportProtocol = dbpPeerTransportProtocol;
		this.dbpPeerSecurity =dbpPeerSecurity ;
		this.dbpPeerPortConnect = localPort;
	}
	@Override
	public String toString(){
		
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\nPort to Listen                                        : "+this.dbpPeerPortListen);
		responseBuilder.append("\nTransport Protocol                                    : "+TransportProtocols.getProtocolTypeString(this.dbpPeerTransportProtocol));
		responseBuilder.append("\nSecurity                                              : "+this.dbpPeerSecurity);
		responseBuilder.append("\n----------------------------------------------------------------------------------");
		
		return super.toString()+responseBuilder.toString();
	}
	public int getPeerPortListen(){
		return dbpPeerPortListen;
	}
	public int getPeerSecurity(){
		return dbpPeerSecurity;
	}
	public int getPeerTransportProtocol(){
		return dbpPeerTransportProtocol;
	}
	public int getPeerPortConnect(){
		return dbpPeerPortConnect;
	}
	
}
