package com.elitecore.aaa.alert.conf;

import com.elitecore.core.serverx.alert.IAlertData;


public interface AAAAlertManagerConfiguration {
	public IAlertData getSystemAlertData(String alertId);
	public boolean isInitialized();
}
