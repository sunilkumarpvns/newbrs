package com.elitecore.netvertexsm.web.servermgr;

import java.io.Serializable;

public class ChartTypeBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String chartType;
	private String name;
	private String description;
	
	public Long getServerId() {
		return serverId;
	}
	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
	private Long serverId;
	
	public String getChartType() {
		return chartType;
	}
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
