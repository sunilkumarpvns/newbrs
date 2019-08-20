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
import com.elitecore.diameterapi.diameter.common.routerx.imsi.ImsiBasedPeerGroupSelector;
import com.elitecore.diameterapi.diameter.common.routerx.imsi.ImsiBasedRouteEntryData;
import com.elitecore.diameterapi.diameter.common.routerx.imsi.ImsiBasedRoutingTableData;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

@XmlType(propOrder={})
public class ImsiBasedRoutingTableDataImpl implements ImsiBasedRoutingTableData {

	public static final String IMSI_BASED_ROUTING_TABLE = "Imsi Based Routing Table";
	public static final String IMSI_IDENTITY_ATTRIBUTE = "Imsi Identity Attribute";
	public static final String IMSI_ENTRIES = "Imsi Entries";

	private static final String EC_IMSI_AVP_ID = DiameterAVPConstants.EC_SUBSCRIPTION_ID + "." + DiameterAVPConstants.EC_SUBSCRIPTION_ID_IMSI;
	
	private String[] header = new String[] {ImsiBasedRouteEntryDataImpl.IMSI_RANGE,
											ImsiBasedRouteEntryDataImpl.PRIMARY_PEER,
											ImsiBasedRouteEntryDataImpl.SECONDARY_PEER};
	private int[] width = new int[] {16, 20, 20};
	private int[] widthTable = new int[] {30, 30};

	private String name;
	private String imsiIdentityAttributeStr = EC_IMSI_AVP_ID;
	private List<ImsiBasedRouteEntryDataImpl> entries;
	private List<String> imsiIdentityAttributes;
	private Trie<ImsiBasedRouteEntryData> imsiTrie;
	
	public ImsiBasedRoutingTableDataImpl() {
		setImsiIdentityAttributes(new ArrayList<String>());
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
	@XmlElement(name = "imsi-identity-attribute", type = String.class, defaultValue = EC_IMSI_AVP_ID)
	public String getImsiIdentityAttributeStr() {
		return imsiIdentityAttributeStr;
	}
	public void setImsiIdentityAttributeStr(String imsiIdentityAttributeStr) {
		this.imsiIdentityAttributeStr = imsiIdentityAttributeStr;
	}

	@Override
	@XmlElementWrapper(name = "imsi-based-routing-entries")
	@XmlElement(name = "imsi-based-routing-entry", type = ImsiBasedRouteEntryDataImpl.class)
	public List<ImsiBasedRouteEntryDataImpl> getEntries() {
		return entries;
	}
	public void setEntries(List<ImsiBasedRouteEntryDataImpl> entries) {
		this.entries = entries;
	}

	@Override
	@XmlTransient
	public List<String> getImsiIdentityAttributes() {
		return imsiIdentityAttributes;
	}
	public void setImsiIdentityAttributes(List<String> imsiIdentityAttributes) {
		this.imsiIdentityAttributes = imsiIdentityAttributes;
	}

	@Override
	public ImsiBasedPeerGroupSelector createSelector(
			RouterContext routerContext) {
		return new ImsiBasedPeerGroupSelector(routerContext, this);
	}
	
	@Override
	public String toString() {
	
		TableFormatter formatter = new TableFormatter(
				new String[] {IMSI_BASED_ROUTING_TABLE, getName()}, 
				widthTable, TableFormatter.ONLY_HEADER_LINE);
		
		formatter.addRecord(new String[] {IMSI_IDENTITY_ATTRIBUTE, getImsiIdentityAttributeStr()});
		formatter.add(IMSI_ENTRIES, TableFormatter.LEFT);
		
		TableFormatter imsiEntriesFormatter = new TableFormatter(header, width, 
				TableFormatter.ONLY_HEADER_LINE);
		if (Collectionz.isNullOrEmpty(entries) == false) {
			for(ImsiBasedRouteEntryDataImpl imsiBasedRouteEntryData : entries){
				imsiEntriesFormatter.addRecord(new String[]{imsiBasedRouteEntryData.getImsiRange(),
						imsiBasedRouteEntryData.getPrimaryPeer(),
						imsiBasedRouteEntryData.getSecondaryPeer() != null ? imsiBasedRouteEntryData.getSecondaryPeer() : "" });
			}
		}
		formatter.add(imsiEntriesFormatter.getFormattedValues());
		return formatter.getFormattedValues();
	}

	@Override
	public Trie<ImsiBasedRouteEntryData> trie() {

		if (imsiTrie == null) {
			synchronized (this) { //NOSONAR - Reason: Double-checked locking should not be used
				if (imsiTrie == null) {
					imsiTrie = new Trie<ImsiBasedRouteEntryData>();
					for (ImsiBasedRouteEntryData imsiBasedRouteEntryData : getEntries()) {
						imsiTrie.put(imsiBasedRouteEntryData.getImsiRange(), imsiBasedRouteEntryData);
					}
				}
			}
		}
		return imsiTrie;
	}
}
