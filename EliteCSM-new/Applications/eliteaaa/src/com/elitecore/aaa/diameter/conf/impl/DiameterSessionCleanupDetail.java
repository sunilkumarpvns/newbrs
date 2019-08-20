package com.elitecore.aaa.diameter.conf.impl;

import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.annotations.Reloadable;

@Reloadable(type=DiameterSessionCleanupDetail.class)
@XmlType(propOrder={})
public class DiameterSessionCleanupDetail {
	
	private long sessionCleanupInterval = 3600;
	private long sessionTimeOut = 86400;
	
	public DiameterSessionCleanupDetail() {
	}

	@XmlElement(name="session-cleanup-interval",type=long.class,defaultValue="3600")
	public long getSessionCleanupInterval() {
		return sessionCleanupInterval;
	}

	public void setSessionCleanupInterval(long sessionCleanupInterval) {
		this.sessionCleanupInterval = sessionCleanupInterval;
	}

	@XmlElement(name="session-timeout",type=long.class,defaultValue="86400")
	public long getSessionTimeOut() {
		return sessionTimeOut;
	}

	public void setSessionTimeOut(long sessionTimeOut) {
		this.sessionTimeOut = sessionTimeOut;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Session Clean Up Interval(sec)", 
				getSessionCleanupInterval()));
		out.println(format("%-30s: %s", "Session Timeout(sec)", 
				getSessionTimeOut()));
		out.close();
		return stringBuffer.toString();

	}
	

}
