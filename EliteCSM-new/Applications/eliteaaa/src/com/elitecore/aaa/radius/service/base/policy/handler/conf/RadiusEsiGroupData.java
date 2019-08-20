package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

//TODO geo-redundancy configuration
@XmlRootElement(name = "radius-esi-group")
@XmlType(propOrder = {"id", "name", "description", "esiType", "stateful", "redundancyMode", "switchBackEnable", "primaryEsiList", "failOverEsiList", "activePassiveEsiList"})
public class RadiusEsiGroupData {

	private String id;
	private String name;
	private String description;
	private String esiType;
	private boolean stateful = true;
	private String redundancyMode;
	private boolean switchBackEnable;
	private List<CommunicatorData> primaryEsiList = new ArrayList<>();
	private List<CommunicatorData> failOverEsiList = new ArrayList<>();
	private List<ActivePassiveCommunicatorData> activePassiveEsiList = new ArrayList<>();
	
	@XmlElement(name = "id")
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name = "esi-type")
	public String getEsiType() {
		return esiType;
	}

	public void setEsiType(String esiType) {
		this.esiType = esiType;
	}

	@XmlElement(name = "is-sticky-session-enable")
	public boolean isStateful() {
		return stateful;
	}

	public void setStateful(boolean stateful) {
		this.stateful = stateful;
	}

	@XmlElement(name = "redundancy-mode")
	public String getRedundancyMode() {
		return redundancyMode;
	}

	public void setRedundancyMode(String redundancyMode) {
		this.redundancyMode = redundancyMode;
	}

	@XmlElement(name = "is-switch-back-enable")
	public boolean isSwitchBackEnable() {
		return switchBackEnable;
	}

	public void setSwitchBackEnable(boolean switchBackEnable) {
		this.switchBackEnable = switchBackEnable;
	}

	@XmlElementWrapper(name = "primary-esi-list")
	@XmlElement(name = "esi-entry-detail")
	public List<CommunicatorData> getPrimaryEsiList() {
		return primaryEsiList;
	}

	public void setPrimaryEsiList(List<CommunicatorData> primaryEsiList) {
		this.primaryEsiList = primaryEsiList;
	}

	@XmlElementWrapper(name = "failover-esi-list")
	@XmlElement(name = "esi-entry-detail")
	public List<CommunicatorData> getFailOverEsiList() {
		return failOverEsiList;
	}

	public void setFailOverEsiList(List<CommunicatorData> failOverEsiList) {
		this.failOverEsiList = failOverEsiList;
	}
	
    @XmlElementWrapper(name = "esi-list")
    @XmlElement(name = "active-passive-esi")
    public List<ActivePassiveCommunicatorData> getActivePassiveEsiList() {
        return activePassiveEsiList;
    }

    public void setActivePassiveEsiList(List<ActivePassiveCommunicatorData> activePassiveEsiList) {
        this.activePassiveEsiList = activePassiveEsiList;
    }

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Radius ESI Group Name", this.name));
		out.println(format("%-30s: %s", "Description", this.description));
		out.println(format("%-30s: %s", "Esi Type", esiType));
		out.println(format("%-30s: %s", "Sticky Message", this.stateful));
		out.println(format("%-30s: %s", "Redundancy Mode", redundancyMode));
		out.println(format("%-30s: %s", "Switch Back", this.switchBackEnable));
		out.println(format("%-30s: %s", "Primary ESI List", this.primaryEsiList));
		out.println(format("%-30s: %s", "Fail-Over ESI List", this.failOverEsiList));
		out.println(format("%-30s: %s", "Active-Passive ESI List", this.activePassiveEsiList));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

}
