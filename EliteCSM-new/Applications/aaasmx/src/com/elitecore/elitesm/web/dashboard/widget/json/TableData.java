package com.elitecore.elitesm.web.dashboard.widget.json;

import java.util.ArrayList;
import java.util.List;

public class TableData {
	private List<String> name = new ArrayList<String>();

	public List<String> getName() {
		return name;
	}

	public void setName(List<String> name) {
		this.name = name;
	}
	
	public TableData addTableData(String data) {
		this.name.add(data);
		return this;
	}
	
	
}
