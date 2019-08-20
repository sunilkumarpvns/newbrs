package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;

public class ServerHelloDone implements ITLSHandshakeMessage{
 
	public ServerHelloDone() {
		
	}

	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return new byte[0];
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public void setBytes(byte[] handshakeMessageBytes) {
		// TODO Auto-generated method stub
		
	}

	public int readFrom(InputStream sourceStream) throws IOException {
		// TODO Auto-generated method stub
		return(0);
	}

	public void writeTo(OutputStream out) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public Object clone()throws CloneNotSupportedException{		
		ServerHelloDone serverHelloDone = null;

		serverHelloDone = (ServerHelloDone)super.clone();
		return(serverHelloDone);
	}

	public int getType() {
		return HandshakeMessageConstants.ServerHelloDone.value;
	}

	public String toString(){
		return "";
	}
} 
