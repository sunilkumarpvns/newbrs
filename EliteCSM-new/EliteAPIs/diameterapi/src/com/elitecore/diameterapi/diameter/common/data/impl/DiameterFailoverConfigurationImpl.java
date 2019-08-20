package com.elitecore.diameterapi.diameter.common.data.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.data.DiameterFailoverConfiguration;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;

@XmlType(propOrder={})
public class DiameterFailoverConfigurationImpl implements DiameterFailoverConfiguration {
	
	private static final String MODULE = "DIA-FAILOVER-CONF";
	private String errorCodes;
	private DiameterFailureConstants failoverAction;
	private String failoverArgs;
	private int actionInt;
	
	
	public DiameterFailoverConfigurationImpl() {
		this.failoverAction = DiameterFailureConstants.PASSTHROUGH;
		this.actionInt = DiameterFailureConstants.PASSTHROUGH.failureAction;
	}
	public void setFailoverArguments(String failoverArgs){
		this.failoverArgs= failoverArgs;
	}
	public void setErrorCodes(String errorCodes){
		this.errorCodes= errorCodes;
	}
	
	@Override
	@XmlTransient
	public DiameterFailureConstants getFailoverAction() {
		return failoverAction;
	}

	@Override
	@XmlElement(name="failure-argument",type=String.class)
	public String getFailoverArguments() {
		return failoverArgs;
	}
	
	@XmlElement(name="action",type=int.class)
	public int getAction() {
		return actionInt;
	}
	
	public void setAction(int failoverInt) {
		DiameterFailureConstants failureAction = DiameterFailureConstants.fromDiameterFailureAction(failoverInt);
		if (failureAction != null) {
			actionInt = failoverInt;
			failoverAction = failureAction;
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Invalid Failure Action: " + failoverInt + ". Taking FailoverAction: " + DiameterFailureConstants.PASSTHROUGH);
			}
		}
	}
	
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("    ");
		out.println("  -- Diameter Failover  Configuration -- ");
		out.println(" \t Failover ErroCode      = " + errorCodes);
		out.println(" \t Failover Action        = " + failoverAction);
		out.println(" \t Failover Parameters    = " + failoverArgs);
		out.close();
		return stringBuffer.toString();
	}
	@Override
	@XmlElement(name="error-code",type=String.class)
	public String getErrorCodes() {
		return errorCodes;
	}
}
