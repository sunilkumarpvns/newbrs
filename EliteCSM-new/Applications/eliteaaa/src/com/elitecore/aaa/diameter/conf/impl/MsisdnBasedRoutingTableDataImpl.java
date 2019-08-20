package com.elitecore.aaa.diameter.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.collections.Trie;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.routerx.msisdn.MsisdnBasedPeerGroupSelector;
import com.elitecore.diameterapi.diameter.common.routerx.msisdn.MsisdnBasedRouteEntryData;
import com.elitecore.diameterapi.diameter.common.routerx.msisdn.MsisdnBasedRoutingTableData;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

@XmlType(propOrder={})
public class MsisdnBasedRoutingTableDataImpl implements MsisdnBasedRoutingTableData {

	public static final String MSISDN_BASED_ROUTING_TABLE = "Msisdn Based Routing Table";
	public static final String MSISDN_IDENTITY_ATTRIBUTE = "Msisdn Identity Attribute";
	public static final String MSISDN_ENTRIES = "Msisdn Entries";
	public static final String MSISDN_LENGTH = "Msisdn Length";
	public static final String MCC = "MCC";

	private static final String EC_MSISDN_AVP_ID = DiameterAVPConstants.EC_SUBSCRIPTION_ID + "." + DiameterAVPConstants.EC_SUBSCRIPTION_ID_E164;
	
	private String[] header = new String[] {MsisdnBasedRouteEntryDataImpl.MSISDN_RANGE,
											MsisdnBasedRouteEntryDataImpl.PRIMARY_PEER,
											MsisdnBasedRouteEntryDataImpl.SECONDARY_PEER};
	private int[] width = new int[] {16, 20, 20};
	private int[] widthTable = new int[] {30, 30};

	private String name;
	private String msisdnIdentityAttributeStr = EC_MSISDN_AVP_ID;
	private List<MsisdnBasedRouteEntryDataImpl> entries;
	private List<String> msisdnIdentityAttributes;
	private int msisdnLength;
	private String mcc;
	private Trie<MsisdnBasedRouteEntryData> msisdnTrie;
	
	
	public MsisdnBasedRoutingTableDataImpl() {
		setMsisdnIdentityAttributes(new ArrayList<String>());
	}

	@Override
	@XmlElement(name = "name", type = String.class)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	@XmlElement(name = "msisdn-identity-attribute", type = String.class, defaultValue = EC_MSISDN_AVP_ID)
	public String getMsisdnIdentityAttributeStr() {
		return msisdnIdentityAttributeStr;
	}
	public void setMsisdnIdentityAttributeStr(String msisdnIdentityAttributeStr) {
		this.msisdnIdentityAttributeStr = msisdnIdentityAttributeStr;
	}
	
	@Override
	@XmlElement(name = "msisdn-length", type = int.class, defaultValue = "10")
	public int getMsisdnLength() {
		return msisdnLength;
	}
	public void setMsisdnLength(int msisdnLength) {
		this.msisdnLength = msisdnLength;
	}

	@Override
	@XmlElement(name = "mcc", type = String.class)
	public String getMcc() {
		return mcc;
	}
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	@Override
	@XmlElementWrapper(name = "msisdn-based-routing-entries")
	@XmlElement(name = "msisdn-based-routing-entry", type = MsisdnBasedRouteEntryDataImpl.class)
	public List<MsisdnBasedRouteEntryDataImpl> getEntries() {
		return entries;
	}
	public void setEntries(List<MsisdnBasedRouteEntryDataImpl> entries) {
		this.entries = entries;
	}

	@Override
	@XmlTransient
	public List<String> getMsisdnIdentityAttributes() {
		return msisdnIdentityAttributes;
	}
	public void setMsisdnIdentityAttributes(List<String> msisdnIdentityAttributes) {
		this.msisdnIdentityAttributes = msisdnIdentityAttributes;
	}

	@Override
	public MsisdnBasedPeerGroupSelector createSelector(
			RouterContext routerContext) {
		return new MsisdnBasedPeerGroupSelector(routerContext, this);
	}
	
	@Override
	public String toString() {
	
		TableFormatter formatter = new TableFormatter(
				new String[] {MSISDN_BASED_ROUTING_TABLE, getName()}, 
				widthTable, TableFormatter.ONLY_HEADER_LINE);
		
		formatter.addRecord(new String[] {MSISDN_IDENTITY_ATTRIBUTE, getMsisdnIdentityAttributeStr()});
		formatter.addRecord(new String[] {MSISDN_LENGTH, String.valueOf(getMsisdnLength())});
		formatter.addRecord(new String[] {MCC, getMcc() != null ? getMcc() : ""});
		formatter.add(MSISDN_ENTRIES, TableFormatter.LEFT);
		
		TableFormatter msisdnEntriesFormatter = new TableFormatter(header, width, 
				TableFormatter.ONLY_HEADER_LINE);
		if (Collectionz.isNullOrEmpty(entries) == false) {
			for(MsisdnBasedRouteEntryDataImpl msisdnBasedRouteEntryData : entries){
				msisdnEntriesFormatter.addRecord(new String[]{msisdnBasedRouteEntryData.getMsisdnRange(),
						msisdnBasedRouteEntryData.getPrimaryPeer(),
						msisdnBasedRouteEntryData.getSecondaryPeer() != null ? msisdnBasedRouteEntryData.getSecondaryPeer() : "" });
			}
		}
		formatter.add(msisdnEntriesFormatter.getFormattedValues());
		return formatter.getFormattedValues();
	}

	@Override
	public Trie<MsisdnBasedRouteEntryData> trie() {

		if (msisdnTrie == null) {
			synchronized (this) { //NOSONAR - Reason: Double-checked locking should not be used
				if (msisdnTrie == null) {
					msisdnTrie = new Trie<MsisdnBasedRouteEntryData>();
					for (MsisdnBasedRouteEntryDataImpl msisdnBasedRouteEntryData : getEntries()) {
						msisdnTrie.put(msisdnBasedRouteEntryData.getMsisdnRange(), msisdnBasedRouteEntryData);
					}
				}
			}
		}
		return msisdnTrie;
	}

}
