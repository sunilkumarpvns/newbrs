package com.elitecore.aaa.radius.drivers;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.UserFileAccountData;
import com.elitecore.aaa.core.drivers.ChangeCaseStrategy;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy;
import com.elitecore.aaa.core.drivers.UserFileAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.drivers.conf.impl.RadUserFileAuthDriverConfigurationImpl;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

public class RadUserFileAuthDriver extends UserFileAuthDriver implements Cacheable {
	private static final String MODULE = "RAD USERFILE AUTH DRIVER";
	private RadUserFileAuthDriverConfigurationImpl radUserFileAuthDriverConfiguration = null;
	private final AAAServerContext serverContext;
	
	public RadUserFileAuthDriver(AAAServerContext serverContext, String driverInstanceId) {
		super(serverContext);
		this.serverContext = serverContext;
		radUserFileAuthDriverConfiguration = (RadUserFileAuthDriverConfigurationImpl)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId);		
	}

	// For now all errors are considered as permanent
	@Override
	protected void initInternal() throws DriverInitializationFailedException{
		super.initInternal();
		CacheDetail cacheDetail = readAllFiles(getUserFileLocations());		
		if(cacheDetail.getResultCode() == CacheConstants.FAIL){			
			throw new DriverInitializationFailedException("None userfile read successfully");
		}		
		serverContext.registerCacheable(this);
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}
	
	protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity){
		IRadiusAttribute identityAttribute = ((RadServiceRequest) serviceRequest).getRadiusAttribute(userIdentity, true);
		if(identityAttribute != null) {
			serviceRequest.setParameter(AAAServerConstants.USER_IDENTITY, identityAttribute.getStringValue());
			return identityAttribute.getStringValue();
		}
		else
			return null;
	}
	
	@Override
	public String[] getUserFileLocations(){
		return radUserFileAuthDriverConfiguration.getLocations();
	}
	
	public SimpleDateFormat[] getexpiryPatterns() {
		return radUserFileAuthDriverConfiguration.getexpiryPatterns();
	} 


	@Override
	public String getName() {
		return radUserFileAuthDriverConfiguration.getDriverName();
	}


	@Override
	public String toString() {
		return radUserFileAuthDriverConfiguration.toString();
	}

	@Override
	public String getDriverInstanceId() {
		return this.radUserFileAuthDriverConfiguration.getDriverInstanceId();
	}

	@Override
	public CacheDetail reloadCache() {
		CacheDetail cacheDetail = new CacheDetailProvider();
		((CacheDetailProvider)cacheDetail).setName(getName());
		try {
			cacheDetail = readAllFiles(getUserFileLocations());			
			if(cacheDetail.getResultCode() == CacheConstants.FAIL){
				LogManager.getLogger().debug(MODULE, "Reloading cache unsuccessfull for RadUserFileAuthDriver instance id: " +getDriverInstanceId());
			}else{			
				LogManager.getLogger().debug(MODULE, "Reloading cache successfull for RadUserFileAuthDriver instance id: " +getDriverInstanceId());
			}			
		} catch (DriverInitializationFailedException e) {			
			LogManager.getLogger().error(MODULE, "Reloading cache failed for RadUserFileAuthDriver instance id: " +getDriverInstanceId() +". reason: " +e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}	
		return cacheDetail;
	}


	@Override
	public int getType() {
		return DriverTypes.RAD_USERFILE_AUTH_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RAD_USERFILE_AUTH_DRIVER.name();
	}
	
	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String innerIdentity)throws DriverProcessFailedException {
		AccountData accountData =null;
		AccountData newAccountData=null;
		
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
						newAccountData=accountData;
						try {
							newAccountData=accountData.clone();
							setDynAccountPasswordFromAttrId(newAccountData, serviceRequest);
						} catch (CloneNotSupportedException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
								LogManager.getLogger().error(MODULE, "System Error : Problem in cloning accountData in driver [" + getName() + "] Reason : "+e.getMessage());
						}
						break;
					}
				}	
			}

		}
		return newAccountData;
	}

	private boolean matchAuthAttributeInRequest(ServiceRequest serviceRequest,	Map<String, String> authAttributeMap, String innerIdentity) {

		AttributeId userNameAttributeIDUserfile=null;
		boolean result=false;
		try {
			userNameAttributeIDUserfile=Dictionary.getInstance().getAttributeId(RadiusAttributeConstants.USER_NAME_STR);
			if(innerIdentity.equals(authAttributeMap.get(userNameAttributeIDUserfile.getStringID()))){
				result=true;
			}else if(innerIdentity.equals(authAttributeMap.get(String.valueOf(RadiusAttributeConstants.USER_NAME)))){
				result=true;
			}
		}catch (InvalidAttributeIdException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, RadiusAttributeConstants.USER_NAME_STR +" Attribute is not found in Radius Dictionary.");
			}
		}
		return result;
	}
	@Override
	public void reInit() throws InitializationFailedException{
		this.radUserFileAuthDriverConfiguration = (RadUserFileAuthDriverConfigurationImpl)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(getDriverInstanceId());
		super.reInit();
	}
}
