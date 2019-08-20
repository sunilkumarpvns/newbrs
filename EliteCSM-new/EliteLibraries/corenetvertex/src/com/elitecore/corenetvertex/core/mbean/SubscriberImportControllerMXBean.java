package com.elitecore.corenetvertex.core.mbean;

import com.elitecore.corenetvertex.subscriberimport.SubscriberImportParameters;

public interface SubscriberImportControllerMXBean {
	public String start(SubscriberImportParameters importParameters);
	public String stop();
}
