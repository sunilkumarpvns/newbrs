package com.elitecore.aaa.core.config;

import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.annotations.Reloadable;

@Reloadable(type=RadiusSessionCleanupDetail.class)
@XmlType(propOrder={})
public class RadiusSessionCleanupDetail {
	
	public static final long MAX_SESSION_TIMEOUT = 86400;
	public static final long MAX_SESSION_CLEANUP_INTERVAL = 43200;
	public static final long MIN_SESSION_CLEANUP_INTERVAL = 300;
	
	private long sessionCleanupInterval = 120;
	private long sessionTimeOut = 120;
	
	public RadiusSessionCleanupDetail() {
		//required for JAXB
	}

	@XmlElement(name="session-cleanup-interval",type=long.class,defaultValue="120")
	public long getSessionCleanupInterval() {
		return sessionCleanupInterval;
	}

	public void setSessionCleanupInterval(long sessionCleanupInterval) {
		this.sessionCleanupInterval = sessionCleanupInterval;
	}

	@XmlElement(name="session-timeout",type=long.class,defaultValue="120")
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
