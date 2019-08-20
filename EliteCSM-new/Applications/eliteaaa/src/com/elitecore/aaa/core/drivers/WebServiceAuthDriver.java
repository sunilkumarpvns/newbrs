package com.elitecore.aaa.core.drivers;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.namespace.QName;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.wipro.sdp.spb.BaseProfile;
import com.wipro.sdp.spb.DeviceProfileByIMSI;
import com.wipro.sdp.spb.spb_service.ErrorResponse;
import com.wipro.sdp.spb.spb_service.SPBInterfaces;
import com.wipro.sdp.spb.spb_service.SPBService;

public abstract class WebServiceAuthDriver extends BaseCacheProfileDriver{
	private static final String MODULE = "WEB-SERVICE-AUTH-DRIVER";
	private SPBInterfaces spbInterfaces;
	private boolean initialized =false;
	private static final int RESULT_CODE_DATA_NOT_FOUND=204;
	private AtomicLong maxQueryTimeoutCount;

	public WebServiceAuthDriver(AAAServerContext serverContext) {
		super(serverContext);
	}

	// For now all errors are considered as permanent
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		super.initInternal();
		initWebServiceCall();
	}
	
	private void initWebServiceCall() throws DriverInitializationFailedException {
		if(!initialized){
			String strWSDLUrl = getServiceAddress();

			try {
				URL wsdlLocationUrl = new URL(strWSDLUrl);
				QName qname = new QName("http://www.wipro.com/sdp/spb/spb_service", "SPBService");
				SPBService spbService = new  SPBService(wsdlLocationUrl,qname);

				spbInterfaces=spbService.getSPBInterfaces();

				if(spbInterfaces==null){
					throw new DriverInitializationFailedException("SPB Interface is not found.");	
				}
				initialized = true;
				LogManager.getLogger().info(MODULE, "Web Service Auth Driver initialized successfully" );	

			} catch (MalformedURLException e) {
				throw new DriverInitializationFailedException(e);
			}
		}
	}


	private AccountData getAccountData(ServiceRequest serviceRequest,String userIdentity) throws DriverProcessFailedException {

		LogManager.getLogger().info(MODULE,"Trying to get account data for user identity: " + userIdentity);
		if(userIdentity == null || userIdentity.length() < 15){
			LogManager.getLogger().info(MODULE, "Invalid IMSI received, Reason: IMSI shoud be 15 digit decimal value" );
			return null;
		}
		if(userIdentity.length() > 15){
			userIdentity = userIdentity.substring(1,16);
		}
		LogManager.getLogger().warn(MODULE, "Requested Identity to SPB service: " + userIdentity);
		try{
			DeviceProfileByIMSI deviceProfileByIMSI = spbInterfaces.getDeviceProfileByIMSI(userIdentity);
			if(deviceProfileByIMSI!=null){
				String strMsisdn =  deviceProfileByIMSI.getMSISDN();
				BaseProfile baseProfile = spbInterfaces.getBaseProfile(strMsisdn);
	
				WebServiceDataValueProvider valueProvider = new WebServiceDataValueProvider(baseProfile, deviceProfileByIMSI, strMsisdn);
				AccountData accountData = buildAccountData(serviceRequest,valueProvider, getAccountDataFieldMapping());
				if(accountData!=null){
					accountData.setImsi(userIdentity);
					accountData.setUserIdentity(userIdentity);
				}
				maxQueryTimeoutCount.set(0);
				return accountData;
			}
			return null  ;

		}catch(ErrorResponse e){
			
			if(e.getFaultInfo()!=null && e.getFaultInfo().getErrorResponse()!=null){
				int responseCode = e.getFaultInfo().getErrorResponse().getResponseCode();
				LogManager.getLogger().warn(MODULE, "Failed to fetch profile Reason - " +responseCode+":"+e.getFaultInfo().getErrorResponse().getErrorText());
				if(responseCode==RESULT_CODE_DATA_NOT_FOUND){
					return null;
				}else{
					this.maxQueryTimeoutCount.incrementAndGet();
					if(maxQueryTimeoutCount.get()>=getMaxQueryTimeoutCount()){
						LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts,so System "+getName()+" marked as DEAD");
						markDead();
					}
					throw new DriverProcessFailedException(e);
				}
			}
			throw new DriverProcessFailedException(e);

		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Failed to fetch profile, Reason: "+e.getMessage());
			throw new DriverProcessFailedException(e);
		}
	}
	private String getWebServiceSubscriberProfileField(String key,String msisdn, BaseProfile baseProfile, DeviceProfileByIMSI deviceProfile) {
		if(baseProfile!=null){
			if(AccountDataFieldMapping.IMEI.equalsIgnoreCase(key)){
				return deviceProfile.getIMEI();
			}else if(AccountDataFieldMapping.IMSI.equalsIgnoreCase(key)){
				return deviceProfile.getIMSI();
			}else if(AccountDataFieldMapping.EMAIL.equalsIgnoreCase(key)){
				return baseProfile.getEmail();
			}else if(AccountDataFieldMapping.CUSTOMER_STATUS.equalsIgnoreCase(key)){
				return baseProfile.getSIStatus();
			}else if(AccountDataFieldMapping.CUSTOMER_TYPE.equalsIgnoreCase(key)){
				return baseProfile.getSubscriberType();
			}else if(AccountDataFieldMapping.MSISDN.equalsIgnoreCase(key)){
				return baseProfile.getMSISDN();
			}else if(AccountDataFieldMapping.CUSTOMER_SERVICES.equalsIgnoreCase(key)){
				return baseProfile.getServiceEnabled();
			}else if(AccountDataFieldMapping.CREDIT_LIMIT.equalsIgnoreCase(key)){
				try {
					return spbInterfaces.getCreditLimit(msisdn);
				} catch (ErrorResponse e) {
				}
			}else{
				return null;
			}
		}
		return null;

	}
	
	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest, List<String> userIdentities, ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy,String realmSeparator) throws DriverProcessFailedException {

		String userIdentityValue = null;
		List<String> driverLevelUIAttr =  getUserIdentityAttributes();
		
		if (driverLevelUIAttr != null){
			for (String userIdAttr : driverLevelUIAttr){
				String userIdValue = getValueForIdentityAttribute(serviceRequest, userIdAttr); 
				if ( userIdValue != null){
					userIdentityValue = userIdValue;
					break;
				}
			}
		} else {
			userIdentityValue= getUserIdentityValue(serviceRequest);
		}
		LogManager.getLogger().info(MODULE, "Getting Account Data For User Name : "+userIdentityValue);

		if(userIdentityValue!=null) {
			serviceRequest.setParameter(AAAServerConstants.USER_IDENTITY, userIdentityValue);
			return getAccountData(serviceRequest,getStrippedAndTrimedUserIdentity(userIdentityValue, caseStrategy, bTrimUserIdentity, stripStrategy, realmSeparator));
		}
		else
			return null;

	}

	private String getStrippedAndTrimedUserIdentity(String userIdentityValue,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator) {
		if (bTrimUserIdentity) {
			userIdentityValue = userIdentityValue.trim();
		}
		
		userIdentityValue = caseStrategy.apply(userIdentityValue);

		userIdentityValue = stripStrategy.apply(userIdentityValue, realmSeparator);
		
		return userIdentityValue;

	}

	public abstract String getUserIdentityValue(ServiceRequest request);
	
	public abstract int getMaxQueryTimeoutCount();

	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String userIdentity)throws DriverProcessFailedException {
		return fetchAccountData(serviceRequest, null, caseStrategy, bTrimUserIdentity, stripStrategy, realmSeparator);
	}

	@Override
	public void scan() {
		try{
			URL url = new URL(getServiceAddress());
			HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.connect();
			markAlive();
		}catch (Exception e) {
			markDead();
			LogManager.getLogger().warn(MODULE, "WebService Auth Driver: " + getName()+ " is still dead.");
		}	
	}

	public abstract AccountDataFieldMapping getAccountDataFieldMapping();

	@Override
	protected int getStatusCheckDuration() {
		return getStatusChkDuration();
	}
	protected abstract SimpleDateFormat[] getExpiryDatePatterns();
	
	protected abstract int getStatusChkDuration();
	
	protected abstract List<String> getUserIdentityAttributes();
	
	public abstract String getServiceAddress();
	
	public class WebServiceDataValueProvider implements AccountDataValueProvider {

		private BaseProfile baseProfile;
		private DeviceProfileByIMSI deviceProfile;
		private String msisdn;
		
		public WebServiceDataValueProvider(BaseProfile baseProfile, DeviceProfileByIMSI deviceProfile,String msisdn){
			this.baseProfile = baseProfile;
			this.deviceProfile = deviceProfile;
			this.msisdn = msisdn;
		}
		@Override
		public String getStringValue(String fieldName) {
			return  getWebServiceSubscriberProfileField(fieldName, msisdn, baseProfile, deviceProfile);
		}

		@Override
		public Date getDateValue(String fieldName) {
			SimpleDateFormat[] expiryDatePatterns = getExpiryDatePatterns();							
			String strValue = getWebServiceSubscriberProfileField(fieldName, msisdn, baseProfile, deviceProfile);
			if(msisdn!=null && msisdn.length()>0){
				for(int i=0;i<expiryDatePatterns.length;i++){
					try{											
						Date expiryDate = expiryDatePatterns[i].parse(strValue);
						return expiryDate;
					}catch (ParseException e) {
						LogManager.getLogger().debug(MODULE, "Invalid ExpiryDate Configured in User Account.");
					}								
				}
			}	
			return null;
		}
		
	}
	
}
