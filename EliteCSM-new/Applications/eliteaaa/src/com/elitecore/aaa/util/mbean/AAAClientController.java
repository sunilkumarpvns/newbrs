package com.elitecore.aaa.util.mbean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;

public class AAAClientController extends BaseMBeanController implements AAAClientControllerMBean{
	
	private AAAServerContext aaaServerContext;
	public  AAAClientController(AAAServerContext aaaServerContext) {
		this.aaaServerContext = aaaServerContext;
	}
	@Override
	public String getName() {
		return MBeanConstants.CLIENT;
	}

	@Override
	public Map<String, ArrayList<HashMap<String, String>>> clientDetails() {
		return aaaServerContext.getServerConfiguration().getRadClientConfiguration().clientDetails();
	}

}
