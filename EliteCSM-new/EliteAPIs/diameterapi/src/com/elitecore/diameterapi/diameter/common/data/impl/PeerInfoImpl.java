package com.elitecore.diameterapi.diameter.common.data.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.diameterapi.diameter.common.data.PeerInfo;

@XmlType(propOrder={})
public class PeerInfoImpl implements PeerInfo{

	private String peerName;
	private int loadFactor=1;
	
	public PeerInfoImpl() {
	}
	
	public void setPeerName(String peerName){
		 this.peerName = peerName;
	}
	public void setLoadFactor(Integer loadFactor){
		 this.loadFactor = loadFactor;
	}
	@XmlElement(name="peer-name",type=String.class)
	public String getPeerName() {
		return peerName;
	}
	@XmlElement(name="load-factor",type=int.class,defaultValue="1")
	public int getLoadFactor() {
		return loadFactor;
	}

	public String toString(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.print(peerName + " -W- " + loadFactor);
		return stringWriter.toString();
	}
}
