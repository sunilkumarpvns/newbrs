package com.elitecore.aaa.diameter.service.application.drivers;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.UserFileAccountData;
import com.elitecore.aaa.core.drivers.ChangeCaseStrategy;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy;
import com.elitecore.aaa.core.drivers.UserFileAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterUserFileDriverConfigurationImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class DiameterUserFileDriver extends UserFileAuthDriver implements Cacheable{
	private static final String MODULE = "DIAMETER-USERFILE-DRIVER";
	private DiameterUserFileDriverConfigurationImpl nasUserFileAuthDriverConfiguration = null;
	
	public DiameterUserFileDriver(AAAServerContext serverContext,String instanceId){
		super(serverContext);
		this.nasUserFileAuthDriverConfiguration = (DiameterUserFileDriverConfigurationImpl)((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(instanceId);
	}
	
	// For now all errors are considered as permanent
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		super.initInternal();		
		CacheDetail cacheDetail = readAllFiles(getUserFileLocations());		
		if(cacheDetail.getResultCode() == CacheConstants.FAIL){			
			throw new DriverInitializationFailedException("None userfile read successfully");
		}
		getServerContext().registerCacheable(this);
		
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}
	
	protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity){
		IDiameterAVP identityAttribute = ((ApplicationRequest) serviceRequest).getAVP(userIdentity, true);
		if(identityAttribute != null) {
			serviceRequest.setParameter(AAAServerConstants.USER_IDENTITY, identityAttribute.getStringValue());
			return identityAttribute.getStringValue();
		}
		else
			return null;
	}
	
	@Override
	public String[] getUserFileLocations(){
		return nasUserFileAuthDriverConfiguration.getLocations();
	}
	
	public SimpleDateFormat[] getexpiryPatterns() {
		return nasUserFileAuthDriverConfiguration.getexpiryPatterns();
	} 


	@Override
	public String getName() {
		return nasUserFileAuthDriverConfiguration.getDriverName();
	}


	@Override
	public String toString() {
		return nasUserFileAuthDriverConfiguration.toString();
	}

	@Override
	public String getDriverInstanceId() {
		return this.nasUserFileAuthDriverConfiguration.getDriverInstanceId();
	}

	@Override
	public CacheDetail reloadCache() {
		CacheDetail cacheDetail = new CacheDetailProvider();
		((CacheDetailProvider)cacheDetail).setName(getName());
		try {
			cacheDetail = readAllFiles(getUserFileLocations());			
			if(cacheDetail.getResultCode() == CacheConstants.FAIL){
				LogManager.getLogger().debug(MODULE, "Reloading cache unsuccessfull for NasUserFileAuthDriver instance id: " +getDriverInstanceId());
			}else{			
				LogManager.getLogger().debug(MODULE, "Reloading cache successfull for NasUserFileAuthDriver instance id: " +getDriverInstanceId());
			}			
		} catch (DriverInitializationFailedException e) {			
			LogManager.getLogger().error(MODULE, "Reloading cache failed for NasUserFileAuthDriver instance id: " +getDriverInstanceId() +". reason: " +e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}	
		return cacheDetail;
	}

	@Override
	public int getType() {
		return DriverTypes.DIAMETER_USERFILE_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.DIAMETER_USERFILE_DRIVER.name();
	}

	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String innerIdentity)throws DriverProcessFailedException {
		AccountData accountData =null;
		
		if(innerIdentity!=null && driverUserAccounts!=null && driverUserAccounts.size()>0){
			
			
			String strUnstrippedUserIdentity;
			
			if (bTrimUserIdentity) {
				innerIdentity = innerIdentity.trim();
			}
			
			innerIdentity = caseStrategy.apply(innerIdentity);
			
			strUnstrippedUserIdentity = innerIdentity;
			innerIdentity = stripStrategy.apply(innerIdentity, realmSeparator);
			
			
			int size = driverUserAccounts.size();
			UserFileAccountData userAccountData = null;
			Map<String, String> authAttributeMap;

			for(int i =0;i<size;i++){
				userAccountData = driverUserAccounts.get(i);
				authAttributeMap = userAccountData.getAuthAttributesMap();
				if(authAttributeMap!=null && authAttributeMap.size()>0){
					if(matchAuthAttributeInRequest(serviceRequest, authAttributeMap,innerIdentity)){
						accountData =  userAccountData.getAccountData();
						accountData.setUserIdentity(innerIdentity);
						serviceRequest.setParameter(AAAServerConstants.CUI_KEY, innerIdentity);
						serviceRequest.setParameter(AAAServerConstants.UNSTRIPPED_CUI, strUnstrippedUserIdentity);
						break;
					}
				}	
			}

		}
		return accountData;
	
	}

	private boolean matchAuthAttributeInRequest(ServiceRequest serviceRequest,	Map<String, String> authAttributeMap, String innerIdentity) {
		String attributevalue;
		String strAttributeId;
		
		for(Map.Entry<String, String> keyValuePair :authAttributeMap.entrySet()){
			strAttributeId = keyValuePair.getKey();
			if(!strAttributeId.equalsIgnoreCase(DiameterAVPConstants.USER_NAME)){
				attributevalue = getValueForIdentityAttribute(serviceRequest, strAttributeId);
				if(!(attributevalue!=null && attributevalue.equalsIgnoreCase(keyValuePair.getValue()))){
					return false;
				}
					
			}	
			
		}
		attributevalue = authAttributeMap.get(DiameterAVPConstants.USER_NAME);
		if(attributevalue !=null && !attributevalue.equalsIgnoreCase(innerIdentity)){
			return false;
		}	
		
		return true;
		
	}
	@Override
	public void reInit() throws InitializationFailedException{
		this.nasUserFileAuthDriverConfiguration = (DiameterUserFileDriverConfigurationImpl)((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(getDriverInstanceId());
		super.reInit();
	}
}
