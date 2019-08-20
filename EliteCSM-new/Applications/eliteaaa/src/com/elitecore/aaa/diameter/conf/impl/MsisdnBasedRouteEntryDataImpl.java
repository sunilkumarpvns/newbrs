package com.elitecore.aaa.diameter.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.diameterapi.diameter.common.routerx.msisdn.MsisdnBasedRouteEntryData;

@XmlType(propOrder={})
public class MsisdnBasedRouteEntryDataImpl implements MsisdnBasedRouteEntryData {

	public static final String SECONDARY_PEER = "Secondary-Peer";
	public static final String PRIMARY_PEER = "Primary-Peer";
	public static final String MSISDN_RANGE = "Msisdn-Range";
	
	private String msisdnRange;
	private String primaryPeer;
	private String secondaryPeer;
	private String tag;
	
	private String[] header = new String[] {"", ""};
	private int[] width = new int[] {30, 30};
	
	@Override
	@XmlElement(name = "msisdn-range", type = String.class)
	public String getMsisdnRange() {
		return msisdnRange;
	}
	public void setMsisdnRange(String msisdnRange) {
		this.msisdnRange = msisdnRange;
	}

	@Override
	@XmlElement(name = "primary-peer", type = String.class)
	public String getPrimaryPeer() {
		return primaryPeer;
	}
	public void setPrimaryPeer(String primaryPeer) {
		this.primaryPeer = primaryPeer;
	}
	
	@Override
	@XmlElement(name = "secondary-peer", type = String.class)
	public String getSecondaryPeer() {
		return secondaryPeer;
	}
	public void setSecondaryPeer(String secondaryPeer) {
		this.secondaryPeer = secondaryPeer;
	}
	
	@Override
	@XmlElement(name = "tag", type = String.class)
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		
		TableFormatter formatter = new TableFormatter(header, width, TableFormatter.NO_BORDERS);
		formatter.addRecord(new String[] {MSISDN_RANGE, getMsisdnRange()});
		formatter.addRecord(new String[] {PRIMARY_PEER, getPrimaryPeer() != null ? getPrimaryPeer() : ""});
		formatter.addRecord(new String[] {SECONDARY_PEER, getSecondaryPeer() != null ? getSecondaryPeer() : ""});
		return formatter.getFormattedValues();
	}

}
