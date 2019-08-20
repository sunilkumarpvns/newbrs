package com.elitecore.core.logmonitor;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;

import com.elitecore.core.util.cli.cmd.logmonitorext.Monitor;

public class LogMonitorInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String expression;
	private long startTime;
	private long duration;
	private long expiryTime;
	public LogMonitorInfo(String expression,
			long startTime, long duration, long expiryTime) {
		this.expression = expression;
		this.startTime = startTime;
		this.duration = duration;
		this.expiryTime = expiryTime;
	}
	public String getExpression() {
		return expression;
	}
	public long getStartTime() {
		return startTime;
	}
	public long getDuration() {
		return duration;
	}
	public long getExpiryTime() {
		return expiryTime;
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Expression=");
		out.println(expression);
		out.println(",Start Time=");
		out.println(new Date(startTime));
		out.println(",Duration(minute)=");
		out.println(duration);
		out.println(",Expiry Time=");
		if(expiryTime == Monitor.NO_TIME_LIMIT){
			out.println("NO TIME LIMIT");
		}else{
			out.println(new Date(expiryTime));
		}
		return stringWriter.toString();
	}
}
