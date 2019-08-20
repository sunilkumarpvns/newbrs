package com.elitecore.netvertex.core.groovy;


import com.elitecore.commons.logging.ILogger;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;

/*
 * Author: Raj Kirpalsinh
 * 
 */

public interface GroovyContext {
	
	public NetVertexServerContext getServerContext();
	
	public ILogger getLogger();
	public void doForcefullyReAuthorization(PCRFKeyConstants pcrfKey, String value);
	public void doReAuthorization(PCRFKeyConstants pcrfKey, String value);
}
