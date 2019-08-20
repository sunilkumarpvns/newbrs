package com.elitecore.core.commons.packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RDRPacketFactory extends PacketFactory{	
	
	public byte[] createPacketBytes(InputStream inStream) throws IOException{
		byte []headerBytes = new byte[20];
		byte [] fieldBytes = null;
		byte [] packetBytes = null;
		int totalBytes = 0;
		
		DataInputStream dInStream = new DataInputStream(inStream);
			
		totalBytes = dInStream.read(headerBytes);
			if(totalBytes > 0) {				
				int length=getLength(headerBytes);				
				fieldBytes = new byte[length - 15];				
				dInStream.readFully(fieldBytes);
				packetBytes=new byte[headerBytes.length+fieldBytes.length];
				System.arraycopy(headerBytes, 0, packetBytes, 0, headerBytes.length);
				System.arraycopy(fieldBytes, 0, packetBytes,headerBytes.length, fieldBytes.length);
				return packetBytes;
			}
			return packetBytes;
	}
	public int getLength(byte[] header){
		int intvalue=0,temp=0,bytesread=0;
		for(int i=1;i<5;i++){
			temp=header[i];
			bytesread++;			
			if(48<=temp && temp<=57){				
				temp=temp-48; intvalue=intvalue*10+temp; 				
				temp=0;
			}
		}		
		return intvalue;		
	}
	@Override
	public IPacket createNewPacket() {
		return null;
	}
}
