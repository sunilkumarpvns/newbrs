package com.elitecore.aaa.core.conf;

import java.util.List;

import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;

public interface HSSAuthDriverConfiguration extends DriverConfiguration {

	public AccountDataFieldMapping getAccountDataFieldMapping();
	public List<String> getUserIdentityAttributes();
	public String getAdditionalAttributes();
	public int getRequestTimeout();
	public String getApplicationId();
	public int getCommandCode();
	public List<PeerInfoImpl> getPeerList();
	public int getNumberOfTriplets();

}
