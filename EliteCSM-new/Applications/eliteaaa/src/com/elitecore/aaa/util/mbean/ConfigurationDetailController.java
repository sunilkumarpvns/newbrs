package com.elitecore.aaa.util.mbean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;

public class ConfigurationDetailController  extends BaseMBeanController  implements ConfigurationDetailControllerMBean{

	
	public List<Map<String, String>> retriveReloadedCacheDetails() {
		List<Map<String, String>> detailList = new ArrayList<Map<String,String>>(0);
		return detailList;
	}

	@Override
	public String getName() {
		return MBeanConstants.CONFIGURATION_DETAIL;
	}

}
