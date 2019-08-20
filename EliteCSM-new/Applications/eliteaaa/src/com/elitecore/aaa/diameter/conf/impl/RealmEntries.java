package com.elitecore.aaa.diameter.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class RealmEntries {
	
	private List<RoutingEntryDataImpl> routingEntryDataList;

	public RealmEntries() {
		this.routingEntryDataList = new ArrayList<RoutingEntryDataImpl>();
	}
	
	@XmlElement(name="routing-entry")
	public List<RoutingEntryDataImpl> getRealmEntryList() {
		return routingEntryDataList;
	}

	public void setRealmEntryList(List<RoutingEntryDataImpl> routingEntryDataList) {
		this.routingEntryDataList = routingEntryDataList;
	}

}
