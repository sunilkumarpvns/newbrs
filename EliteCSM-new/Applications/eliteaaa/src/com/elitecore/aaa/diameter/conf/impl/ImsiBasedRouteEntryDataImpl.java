package com.elitecore.aaa.diameter.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.diameterapi.diameter.common.routerx.imsi.ImsiBasedRouteEntryData;

@XmlType(propOrder={})
public class ImsiBasedRouteEntryDataImpl implements ImsiBasedRouteEntryData {

	public static final String SECONDARY_PEER = "Secondary-Peer";
	public static final String PRIMARY_PEER = "Primary-Peer";
	public static final String IMSI_RANGE = "Imsi-Range";
	
	private String imsiRange;
	private String primaryPeer;
	private String secondaryPeer;
	private String tag;
	
	private String[] header = new String[] {"", ""};
	private int[] width = new int[] {30, 30};
	
	@Override
	@XmlElement(name = "imsi-range", type = String.class)
	public String getImsiRange() {
		return imsiRange;
	}
	public void setImsiRange(String imsiRange) {
		this.imsiRange = imsiRange;
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
		formatter.addRecord(new String[] {IMSI_RANGE, getImsiRange()});
		formatter.addRecord(new String[] {PRIMARY_PEER, getPrimaryPeer() != null ? getPrimaryPeer() : ""});
		formatter.addRecord(new String[] {SECONDARY_PEER, getSecondaryPeer() != null ? getSecondaryPeer() : ""});
		return formatter.getFormattedValues();
	}

}
