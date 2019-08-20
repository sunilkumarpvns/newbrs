package com.elitecore.ssp.web.bod.forms;

import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class SubscribeBodForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;

	private Long bodPackageId;

	private long duration;
	private String startTime="2/5/2014";
	private String durationUnit;
	
	public Long getBodPackageId() {
		return bodPackageId;
	}
	public void setBodPackageId(Long bodPackageId) {
		this.bodPackageId = bodPackageId;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getDurationUnit() {
		return durationUnit;
	}
	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}
	
}
