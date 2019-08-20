package com.elitecore.diameterapi.script.manager;

import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.DiameterStackContext;

public abstract class DiameterScriptContext {

	public abstract PeerData getPeerData(String hostIdentity);
	public abstract TaskScheduler getTaskSchedular();
	public abstract DiameterStackContext getStackContext();
}
