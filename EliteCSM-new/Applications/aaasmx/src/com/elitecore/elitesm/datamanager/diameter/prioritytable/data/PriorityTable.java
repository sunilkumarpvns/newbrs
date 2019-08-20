package com.elitecore.elitesm.datamanager.diameter.prioritytable.data;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "priority-table")
public class PriorityTable {
	
	@Valid
	@NotNull(message = "Please specify at least one priority entry.")
	private List<PriorityTableData> priorityTables;

	@XmlElement(name = "priority-entry")
	public List<PriorityTableData> getPriorityTables() {
		return priorityTables;
	}

	public void setPriorityTables(List<PriorityTableData> priorityTables) {
		this.priorityTables = priorityTables;
	}

}
