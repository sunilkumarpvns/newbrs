package com.elitecore.aaa.diameter.conf.impl;

import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class SelectedPeerConfDetail {
	
	private List<String> peerList;
	
	public SelectedPeerConfDetail() {
		this.peerList = new ArrayList<String>();
	}
	
	@XmlElement(name="name", type=String.class)
	public List<String> getPeerList() {
		return peerList;
	}

	public void setPeerList(List<String> peerList) {
		this.peerList = peerList;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Selected Peers", 
				getPeerList()));
		out.close();
		return stringBuffer.toString();
	}
	
}
