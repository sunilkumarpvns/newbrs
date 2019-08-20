package com.elitecore.diameterapi.mibs.cc.extended.hostcfgs;

import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaHostCfgs;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaHostCfgsMBean;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaHostIpAddrTable;

public class DccaHostCfgsImpl extends DccaHostCfgs {

	@Override
	public TableDccaHostIpAddrTable accessDccaHostIpAddrTable(){
		return null;
	}

	@Override
	public String getDccaHostID(){
		return Parameter.getInstance().getOwnDiameterIdentity();
	}

}
