package com.elitecore.core.util.cli.cmd.logmonitorext;

import com.elitecore.core.logmonitor.LogMonitorInfo;

/**
 * 
 * @author Harsh Patel
 *
 */
public class MonitorExpression<T,V> {
	
	private LogMonitorInfo logMonitorInfo;
	private Expression<T,V> expression;
	
	public MonitorExpression(Expression<T,V> expression,
			LogMonitorInfo logMonitorInfo) {
			this.logMonitorInfo = logMonitorInfo;
			this.expression = expression;
	}

	public Expression<T,V> getExpression() {
		return expression;
	}
	
	public String getExpressionStr() {
		return logMonitorInfo.getExpression();
	}

	public long getStartTime() {
		return logMonitorInfo.getStartTime();
	}

	public long getDuration() {
		return logMonitorInfo.getDuration();
	}
	
	public long getExpiryTime() {
		return logMonitorInfo.getExpiryTime();
	}
	
	public LogMonitorInfo getLogMonitorInfo() {
		return logMonitorInfo;
	}
	
	public boolean evaluate(T request, V response){
		return expression.evaluate(request, response);
	}

}
