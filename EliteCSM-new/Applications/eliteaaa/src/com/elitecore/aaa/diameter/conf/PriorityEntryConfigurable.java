package com.elitecore.aaa.diameter.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.core.commons.config.core.CaseInsensitiveEnumAdapter;
import com.elitecore.diameterapi.core.common.transport.priority.Priority;
import com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry.DiameterSessionTypes;

@XmlType(propOrder = {})
public class PriorityEntryConfigurable {
	private String priorityId;
	private String applicationIds;
	private String commandCodes;
	private String ipAddresses;
	private DiameterSessionTypes diameterSessionType;
	private Priority priority;
	
	public PriorityEntryConfigurable() {
		this.diameterSessionType = DiameterSessionTypes.ALL;
		this.priority = Priority.MEDIUM;
	}
	
	@XmlElement(name="priority-id", type=String.class)
	public String getPriorityId() {
		return priorityId;
	}

	public void setPriorityId(String priorityId) {
		this.priorityId = priorityId;
	}
	
	@XmlElement(name="application-ids", type=String.class, defaultValue="")
	public String getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(String applicationIds) {
		this.applicationIds = applicationIds;
	}
	
	@XmlElement(name="command-codes", type=String.class, defaultValue="")
	public String getCommandCodes() {
		return commandCodes;
	}

	public void setCommandCodes(String commandCodes) {
		this.commandCodes = commandCodes;
	}

	@XmlElement(name="ip-addresses", type=String.class, defaultValue="")
	public String getIpAddresses() {
		return ipAddresses;
	}

	public void setIpAddresses(String ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	@XmlElement(name="diameter-session-type")
	@XmlJavaTypeAdapter(type=DiameterSessionTypes.class, value = DiameterSessionTypesAdapter.class)
	public DiameterSessionTypes getDiameterSessionType() {
		return diameterSessionType;
	}

	public void setdiameterSessionType(DiameterSessionTypes diameterSessionType) {
		if(diameterSessionType != null){
			this.diameterSessionType = diameterSessionType;
		}
	}

	@XmlElement(name="priority")
	@XmlJavaTypeAdapter(type = Priority.class, value = PriorityAdapter.class)
	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		if(priority != null){
			this.priority = priority;
		}
	}
	
	
	/**
	 * This adapter is used for xml parsing
	 * 
	 */
	public static class DiameterSessionTypesAdapter extends CaseInsensitiveEnumAdapter<DiameterSessionTypes> {
		public DiameterSessionTypesAdapter() {
			super(DiameterSessionTypes.class, DiameterSessionTypes.ALL);
		}
	}
}
