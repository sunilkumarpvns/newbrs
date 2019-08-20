package com.elitecore.commons.kpi.data;

import java.util.List;

public class Entry {
	private List<Value> values;
	
	public Entry(List<Value> values) {
		this.values = values;
	}
	
	public List<Value> getValues() {
		return values;
	}
}
