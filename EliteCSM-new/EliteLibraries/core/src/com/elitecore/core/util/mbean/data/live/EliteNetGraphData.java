package com.elitecore.core.util.mbean.data.live;

import java.io.Serializable;

public class EliteNetGraphData implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String moduleName;
	//private HashMap<String, Object> parameterMap;
	byte [] graphData;
	
	public EliteNetGraphData(String moduleName, byte[] grapData) {
		this.moduleName = moduleName;
		this.graphData = grapData;
	}

	public byte[] getGraphData() {
		return graphData;
	}

	public String getModuleName() {
		return moduleName;
	}
	
}
