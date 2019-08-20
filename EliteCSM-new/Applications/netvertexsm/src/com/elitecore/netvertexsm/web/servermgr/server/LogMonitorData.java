package com.elitecore.netvertexsm.web.servermgr.server;

/**
 * @author nitul.kukadia
 *
 */
public class LogMonitorData {
	private String logMonitorType;
	private String expression;
	private String startTime;
	private long duration;
	private String expiryTime;
	public String getLogMonitorType() {
		return logMonitorType;
	}
	public void setLogMonitorType(String logMonitorType) {
		this.logMonitorType = logMonitorType;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getExpiryTime() {
		return expiryTime;
	}
	public void setExpiryTime(String expiryTime) {
		this.expiryTime = expiryTime;
	}
	@Override
	public String toString() {
		return "LogMonitorData [logMonitorType=" + logMonitorType
				+ ", expression=" + expression + ", startTime=" + startTime
				+ ", duration=" + duration + ", expiryTime=" + expiryTime + "]";
	}
}