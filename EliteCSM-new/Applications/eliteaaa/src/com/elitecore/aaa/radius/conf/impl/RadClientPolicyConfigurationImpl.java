package com.elitecore.aaa.radius.conf.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.elitecore.aaa.radius.conf.RadClientPolicyConfiguration;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.conf.impl.BaseConfigurationImpl;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.configuration.ReadConfigurationFailedException;
import com.elitecore.core.commons.configuration.UpdateConfigurationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;

public class RadClientPolicyConfigurationImpl  extends BaseConfigurationImpl implements RadClientPolicyConfiguration{
	
	private static final String MODULE = "CLIENT_POLICY_CONF_IMPL";
	private final static String KEY = "CLIENT_POLICIES";
	

	public RadClientPolicyConfigurationImpl(ServerContext serverContext) {
		super(serverContext);
	}
	
	public boolean updateConfiguration(List lstConfiguration) throws UpdateConfigurationFailedException {
		boolean bSuccess = true;
		String strReturn = "";
		strReturn = getServerContext().getServerHome() + File.separator + "tempconf";
		Iterator iterator = lstConfiguration.iterator();
		while(iterator.hasNext()) {
			EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData)iterator.next();
			
			String configurationKey = eliteNetConfigurationData.getNetConfigurationKey();
			
			if(configurationKey != null && configurationKey.equalsIgnoreCase(KEY)) {				
				try {
					bSuccess = write(strReturn, "client-policies.xml", eliteNetConfigurationData.getNetConfigurationData());
				} catch (IOException e) {
					bSuccess = false;
					LogManager.getLogger().warn(MODULE, "Error while writing file. Reason:" + e.getMessage());
					LogManager.getLogger().trace(MODULE,e);
					throw new UpdateConfigurationFailedException("Failed to Update Client Policies Configuration . Reason: " + e.getMessage(),e);
				}
			}				
		}
		
		return bSuccess;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		LogManager.getLogger().info(MODULE,"");		
	}
	

	@Override
	public EliteNetConfigurationData getNetConfigurationData(){
		try {
			return read(getFileName(),MODULE, getKey());
		} catch (ReadConfigurationFailedException e) {
			LogManager.getLogger().error(MODULE, e.getMessage());
		}		
		return null;
	}
	
	private String getFileName(){
		return getServerContext().getServerHome() + File.separator + "conf" + File.separator +"client-policies.xml";
	}
	@Override
	public String getKey(){
		return KEY;
	}

}
