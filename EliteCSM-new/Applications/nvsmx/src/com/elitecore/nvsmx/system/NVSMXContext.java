package com.elitecore.nvsmx.system;

import com.elitecore.core.commons.util.voltdb.VoltDBClientManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.license.base.exception.InvalidPublickeyException;
import com.elitecore.nvsmx.ws.util.AlternateIdOperationUtils;

public interface NVSMXContext extends ServerContext{
	PolicyRepository getPolicyRepository();
	VoltDBClientManager getVoltDBClientManager();
	String getPublicKey() throws InvalidPublickeyException;
	void deregisterNetvertexServerLicense(ServerInstanceData netServerInstanceData);
	AlternateIdOperationUtils getAlternateIdOperationUtils();
}
