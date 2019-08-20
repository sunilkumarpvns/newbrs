package com.elitecore.diameterapi.mibs.data;

public class DiameterBasePeerHostIpAddressTable {
	private String ownDiameterIdentity;
	private int ipAddressTye;
	private String inetAddress;

	public DiameterBasePeerHostIpAddressTable(String ownDiameterIdentity,int ipAddressTye,String inetAddress){
		this.ownDiameterIdentity = ownDiameterIdentity;
		this.ipAddressTye = ipAddressTye;
		this.inetAddress = inetAddress;
	}
	public DiameterBasePeerHostIpAddressTable(){
		// TODO:
	}
	public String getDiameterIdentity(){
		return ownDiameterIdentity;
	}
	public int getIpAddressType(){
		return ipAddressTye;	// REF RFC 2851 => 0-Unknown, 1-IPv4, 2-IPv6, 16-DNS
	}
	public String getIpAddressOfHost(){
		return inetAddress;
	}

	public String toString(){
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\n----------------------------------------------------------------------------------");
		responseBuilder.append("\nOwn DiameterIdentity                                      : "+this.ownDiameterIdentity);
		responseBuilder.append("\nIpAdreess Type                                      		: "+this.ipAddressTye);
		responseBuilder.append("\nIpAddress Value                                			: "+this.inetAddress);
		responseBuilder.append("\n----------------------------------------------------------------------------------");
		return responseBuilder.toString();
	}
}
