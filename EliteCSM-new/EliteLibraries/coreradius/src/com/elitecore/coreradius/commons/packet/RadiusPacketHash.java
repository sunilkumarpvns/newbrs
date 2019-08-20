package com.elitecore.coreradius.commons.packet;

import java.util.Arrays;

public class RadiusPacketHash{
	private int clientPort;
	private int identifier;
	private byte[] applicationData;
	private int hash;

	public RadiusPacketHash(int clientPort, int identifier, byte[] applicationData) {
		this.clientPort = clientPort;
		this.identifier = identifier;
		this.applicationData=applicationData;
	}
	public int getClientPort() {
		return clientPort;
	}
	public int getIdentifier() {
		return identifier;
	}
	public void setApplicationData(byte[] applicationData) {
		this.applicationData = applicationData;
	}
	public byte[] getApplicationData() {
		return applicationData;
	}
	
	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0) {
			h = (((getClientPort() << 8) | getIdentifier()) << 8);
			hash = h;
		}
    	return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		try{
			RadiusPacketHash packetHash = (RadiusPacketHash)obj;
			return(getClientPort() == packetHash.getClientPort() && 
					getIdentifier() == packetHash.getIdentifier() &&
					Arrays.equals(getApplicationData(), packetHash.getApplicationData()));
				
		}catch(Exception cce){
			return false;
		}
	}
}
