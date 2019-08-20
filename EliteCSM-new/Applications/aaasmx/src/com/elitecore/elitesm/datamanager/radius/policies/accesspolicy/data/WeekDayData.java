package com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class WeekDayData extends BaseData implements IWeekDayData{
	private String weekDayId;
	private String alias;
	private String name;
	private String description;
	private String systemGenerated;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public String getWeekDayId() {
		return weekDayId;
	}
	public void setWeekDayId(String weekDayId) {
		this.weekDayId = weekDayId;
	}
}
