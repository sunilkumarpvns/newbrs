package com.elitecore.aaa.core.drivers;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.aaa.util.mbean.SubscriberProfileController;
import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Ticker;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.DriverProcessTimeoutException;
import com.elitecore.core.commons.drivers.UpdateAccountDataFailedException;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.driverx.BaseDriver;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.passwordutil.PasswordEncryption;

public abstract class BaseAuthDriver extends BaseDriver implements IEliteAuthDriver {

	private static final String MODULE = "Base Auth Driver";
	private static final String TILDE = "~";
	private final Ticker ticker;

	public BaseAuthDriver(AAAServerContext serverContext){
		this(serverContext, new Ticker() {
			
			@Override
			public long nanoTime() {
				return System.nanoTime();
			}
		});
	}
	
	BaseAuthDriver(AAAServerContext serverContext, Ticker ticker){
		super(serverContext);
		this.ticker = ticker;
	}
	
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		
		((AAAServerContext)getServerContext()).registerMBean(new SubscriberProfileController(MBeanConstants.DRIVER_PROFILE+BaseAuthDriver.this.getName()) {
			
			@Override
			public AccountData getSubscriberProfile(String identity) {
				AccountData accountData=null;
				
				try {
					accountData = getAccountData(new ServiceRequest() {
						
						@Override
						public void setParameter(String key, Object parameterValue) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public long getRequestReceivedNano() {
							// TODO Auto-generated method stub
							return 0;
						}
						
						@Override
						public Object getParameter(String str) {
							// TODO Auto-generated method stub
							return null;
						}
					}, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, identity);
				} catch (DriverProcessFailedException e) {
				}
				return accountData;
			}
		});
	}
	
	//implementing the template pattern for keeping the control to parent class
	@Override
	public final AccountData getAccountData(ServiceRequest serviceRequest, ChangeCaseStrategy caseStrategy, boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String userIdentity)throws DriverProcessFailedException {
		incrementTotalRequests();
		try{
			Stopwatch stopwatch = new Stopwatch(ticker).start();
			AccountData accountData = fetchAccountData(serviceRequest, caseStrategy, btrimUserIdentity, stripStrategy, realmSeparator, userIdentity);
			stopwatch.stop();
			updateAverageResponseTime(stopwatch.elapsedTime(TimeUnit.MILLISECONDS));
			incrementTotalSuccess();
			return accountData;
		}catch(DriverProcessTimeoutException ex){
			incrementTotalTimedoutResponses();
			if(ex.getTimeoutDuration() > 0){
				updateAverageResponseTime(ex.getTimeoutDuration());
			}
			throw ex;
		}catch(DriverProcessFailedException ex){
			incrementTotalErrorResponses();
			throw ex;
		}
	}
	
	protected abstract AccountData fetchAccountData(ServiceRequest serviceRequest,ChangeCaseStrategy caseStrategy, boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String userIdentity)throws DriverProcessFailedException;
	
	@Override
	public final AccountData getAccountData(ServiceRequest serviceRequest, List<String> userIdentities,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator) throws DriverProcessFailedException {
		incrementTotalRequests();
		try{
			Stopwatch stopwatch = new Stopwatch(ticker).start();
			AccountData accountData = fetchAccountData(serviceRequest, userIdentities, caseStrategy, bTrimUserIdentity, stripStrategy, realmSeparator);
			stopwatch.stop();
			updateAverageResponseTime(stopwatch.elapsedTime(TimeUnit.MILLISECONDS));
			incrementTotalSuccess();
			return accountData;
		}catch(DriverProcessTimeoutException ex){
			incrementTotalTimedoutResponses();
			if(ex.getTimeoutDuration() > 0){
				updateAverageResponseTime(ex.getTimeoutDuration());
			}
			throw ex;
		}catch(DriverProcessFailedException ex){
			incrementTotalErrorResponses();
			throw ex;
		}
	}

	protected AccountData fetchAccountData(ServiceRequest serviceRequest, List<String> userIdentities,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator) throws DriverProcessFailedException{
		AccountData accountData = null;
		LogManager.getLogger().debug(MODULE, "Trying to fetch Userprofile using driver: " + getName());
		for(String userIdentity:userIdentities){
			String userIdentityValue = this.getValueForIdentityAttribute(serviceRequest,userIdentity);
			if(userIdentityValue != null){
				serviceRequest.setParameter(AAAServerConstants.USER_IDENTITY, userIdentityValue);
				accountData = this.fetchAccountData(serviceRequest, caseStrategy, bTrimUserIdentity, stripStrategy, realmSeparator, userIdentityValue);
				break;
					
			}
				
		}
		if(accountData == null)
			LogManager.getLogger().info(MODULE, "User Identity not found using driver: " + getName());
		return accountData;
	}
	
	//Invoke this method when passing VendorId:AttributeId as UserIdenity so that it can be retrived from the request packet.
	protected abstract String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity);
	
	public void saveAccountData(AccountData accountData){
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Caching profile is not supported by driver: " + getName());
	}
	
	public void setDynamicCheck(String strDynamicCheck, String strUserIdentity)
			throws UpdateAccountDataFailedException {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Dynamic Check is not support for " + getName() + "LDAP Auth Driver.");		
	}
	
	public void setDynAccountPasswordFromAttrId(AccountData accountData,ServiceRequest serviceRequest){
		if(accountData!=null && accountData.getPassword()!=null && accountData.getPassword().startsWith(TILDE)){
			accountData.setEncryptionType(String.valueOf(PasswordEncryption.NONE));
			String dynPasswordAttrId=accountData.getPassword().substring(1).trim();
			if(dynPasswordAttrId.length()>0){
				if(getValueForIdentityAttribute(serviceRequest, dynPasswordAttrId)!=null){
					accountData.setPassword(getValueForIdentityAttribute(serviceRequest, dynPasswordAttrId));
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Dynamic Password Attributr ["+dynPasswordAttrId+"] is found from Request in Driver : " + getName());
				}else{
					accountData.setPassword((String[])null);
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Dynamic Password Attributr ["+dynPasswordAttrId+"] is not found from Request in Driver : " + getName() +", Using Password Blank.");	
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Configured Dynamic Password Attributr Id ["+accountData.getPassword()+"] is wrong, Using Password Blank.");	
				accountData.setPassword((String[])null);
			}
		}
	}

	protected AccountData buildAccountData(ServiceRequest serviceRequest,AccountDataValueProvider valueProvider, AccountDataFieldMapping accountDataFieldMapping){
		
		AccountData accountData = new AccountData();
		
		accountData.setUserName(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_NAME), valueProvider));
		accountData.setAccessPolicy(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ACCESS_POLICY), valueProvider));
		accountData.setAccountStatus( getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_STATUS), valueProvider));
		accountData.setCallbackId(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLBACK_ID), valueProvider));
		accountData.setCalledStationId(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLED_STATION_ID), valueProvider));
		accountData.setCallingStationId(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CALLING_STATION_ID), valueProvider));
		accountData.setCheckItem(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_CHECK_ITEMS), valueProvider));
		accountData.setConcurrentLoginPolicy(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.LOGIN_POLICY), valueProvider));
		accountData.setCUI(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUI), valueProvider));
		accountData.setCustomerServices(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_SERVICES), valueProvider));
		accountData.setCustomerType(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_TYPE), valueProvider));
		accountData.setEmailId(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EMAIL), valueProvider));
		accountData.setGracePolicy(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GRACE_PERIOD), valueProvider));
		accountData.setEncryptionType(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ENCRYPTION_TYPE), valueProvider));
		accountData.setHotlinePolicy(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.HOTLINE_POLICY), valueProvider));
		accountData.setIPPoolName(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IP_ALLOCATION), valueProvider));
		accountData.setParam1(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM1), valueProvider));
		accountData.setParam2(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM2), valueProvider));
		accountData.setParam3(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM3), valueProvider));
		accountData.setParam4(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM4), valueProvider));
		accountData.setParam5(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PARAM5), valueProvider));
		accountData.setPassword(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.USER_PASSWORD), valueProvider));
		accountData.setPasswordCheck(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.PASSWORD_CHECK), valueProvider));
		accountData.setAuthorizationPolicy(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.AUTHORIZATION_POLICY), valueProvider));
		accountData.setRejectItem(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REJECT_ITEMS), valueProvider));
		accountData.setReplyItem(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS), valueProvider,AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS));
		accountData.setServiceType(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.SERVICE_TYPE), valueProvider));
		accountData.setImsi(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMSI), valueProvider));
		accountData.setMeid(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MEID), valueProvider));
		accountData.setMsisdn(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MSISDN), valueProvider));
		accountData.setMdn(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MDN), valueProvider));
		accountData.setImei(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.IMEI), valueProvider));
		accountData.setDeviceVendor(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VENDOR), valueProvider));
		accountData.setDeviceName(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_NAME), valueProvider));
		accountData.setDeviceVLAN(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_VLAN), valueProvider));
		accountData.setDevicePort(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DEVICE_PORT), valueProvider));
		accountData.setGeoLocation(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GEO_LOCATION), valueProvider));
		accountData.setGroupName(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.GROUP_NAME), valueProvider));
		accountData.setAdditionalPolicy(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.ADDITIONAL_POLICY), valueProvider));
		
		accountData.setFramedIPv4Address(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.FRAMED_IPV4_ADDRESS), valueProvider));
		accountData.setFramedIPv6Prefix(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.FRAMED_IPV6_PREFIX), valueProvider));
		accountData.setFramedPool(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.FRAMED_POOL), valueProvider));
		
		accountData.setAuthorizationPolicyGroup(getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.POLICY_GROUP), valueProvider));
		
		String[] values = null;
		
		values = getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS), valueProvider);
		if(values!=null && values.length>0){
			serviceRequest.setParameter(AAAServerConstants.DYNAMIC_CHECK_ITEM_DRIVER_INSTANCE_ID,getDriverInstanceId());
			accountData.setDynamicCheckItems(values);
		}
		
		values = getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAC_VALIDATION), valueProvider);
		if(values!=null && values.length>0){
			accountData.setMacValidation(Boolean.parseBoolean(values[0]));
		}

		
		values = getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.MAX_SESSION_TIME), valueProvider);
		if(values!=null&& values.length>0){
			
			long maxSessionTime ;
			try{
				maxSessionTime = Long.parseLong(values[0]);
				long tempMaxSessionTime ;
				for(int i=1;i<values.length;i++){
					tempMaxSessionTime = Long.parseLong(values[i]);
					if(tempMaxSessionTime<maxSessionTime)
						maxSessionTime = tempMaxSessionTime;
				}
				accountData.setMaxSessionTime(maxSessionTime);
				
			}catch (NumberFormatException e) {
				LogManager.getLogger().warn(MODULE, "Invalid max session time found from driver " + getName() + " for user: " + accountData.getUserIdentity());
			}
			
		}
		
		if((accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CREDIT_LIMIT)!=null)){
			accountData.setCreditLimitCheckRequired(true);
			values = getValues(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.CREDIT_LIMIT), valueProvider);
			if(values!=null&& values.length>0){
				
				long creditLimit ;
				try{
					creditLimit = Long.parseLong(values[0]);
					long tempCreditLimit ;
					for(int i=1;i<values.length;i++){
						tempCreditLimit = Long.parseLong(values[i]);
						if(tempCreditLimit<creditLimit)
							creditLimit = tempCreditLimit;
					}
					accountData.setCreditLimit(creditLimit);
					
				}catch (NumberFormatException e) {
					LogManager.getLogger().warn(MODULE, "Invalid credit limit found from driver: " + getName() + " for user: " + accountData.getUserName() + ". Using default value 1.");
				}
				
			}	
		}
		
		
		if((accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EXPIRY_DATE)!=null)){
			Date expiryDate = getExpiryDate(accountDataFieldMapping.getFieldMapping(AccountDataFieldMapping.EXPIRY_DATE), valueProvider);
			accountData.setExpiryDate(expiryDate);
			accountData.setExpiryDateCheckRequired(true);
		}
		
		return accountData;
	}
	
	protected Date getExpiryDate(List<DataFieldMapping> fieldMapping,AccountDataValueProvider valueProvider) {
		Date expiryDate=null;
		int listSize = fieldMapping.size();
		for(int i=0;i<listSize;i++){
			DataFieldMapping tempMapping = fieldMapping.get(i);
			expiryDate  = valueProvider.getDateValue(tempMapping.getFieldName());
			if(expiryDate!=null)
				return expiryDate;
		}
		return expiryDate;
	}
	
	protected String[] getValues(List<DataFieldMapping> fieldMapping,AccountDataValueProvider valueProvider){
		if(fieldMapping== null || !(fieldMapping.size()>0))
			return null;
		int listSize = fieldMapping.size();
		String[] values = new String[listSize];
		String tempString;
		int j=0;
		for(int i=0;i<listSize;i++){
			DataFieldMapping tempMapping = fieldMapping.get(i);
			tempString = tempMapping.getValue(valueProvider.getStringValue(tempMapping.getFieldName()));
			if(tempString!=null){
				values[j]=tempString;
				j=j+1;
			}	
		}
		if(j==0){
			values = null;
		}
		return values;
	}
	
	protected String[] getValues(List<DataFieldMapping> fieldMapping,AccountDataValueProvider valueProvider,String logicalName){
		return getValues(fieldMapping, valueProvider);
	}
	
	public interface AccountDataValueProvider{
		public String getStringValue(String fieldName);
		public Date getDateValue(String fieldName);
	}
}
