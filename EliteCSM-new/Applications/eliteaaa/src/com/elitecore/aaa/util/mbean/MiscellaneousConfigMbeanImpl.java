package com.elitecore.aaa.util.mbean;

import java.util.List;

import com.elitecore.aaa.core.data.ParamsDetail;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;

public class MiscellaneousConfigMbeanImpl extends BaseMBeanController implements MiscellaneousConfigMXBean {

	private AAAServerContext serverContext;

	public MiscellaneousConfigMbeanImpl(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	@Override
	public String getName() {
		return MBeanConstants.MISCELLANEOUS;
	}

	@Override
	public List<ParamsDetail> getSystemProperties() {
		return serverContext.getServerConfiguration().getMiscellaneousConfigurable().getParamsList(); 
	}
}
