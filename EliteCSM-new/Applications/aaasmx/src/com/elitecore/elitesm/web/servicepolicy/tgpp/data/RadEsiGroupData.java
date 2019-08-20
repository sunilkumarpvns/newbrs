package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;

public class RadEsiGroupData {

	//Retry limit, statefull needed or not ?
	private int id;
	private String name;
	private List<PeerInfoImpl> esis = new ArrayList<PeerInfoImpl>();
	
	@XmlElement(name = "id")
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	public List<PeerInfoImpl> getEsis() {
		return esis;
	}
	
	public void setEsis(List<PeerInfoImpl> esis) {
		this.esis = esis;
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("ESI Group ID		: " +this.id + "\n");
		stringBuffer.append("ESI Group name		: " +this.name +"\n");
		
		for(PeerInfoImpl peerInfoImpl : esis) {
			stringBuffer.append(peerInfoImpl.toString() + "\n");
		}

		return stringBuffer.toString();
	}
	
	
}
