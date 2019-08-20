package com.elitecore.aaa.core.drivers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.TransientFailureException;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.servicex.ServiceRequest;

public abstract class HttpAuthDriver extends BaseAuthDriver{

	private static String MODULE = "HTTP-AUTH-DRV";

	private AtomicLong maxQueryTimeoutCount;


	public HttpAuthDriver(AAAServerContext serverContext) {
		super(serverContext);
		maxQueryTimeoutCount = new AtomicLong(0);
	}

	@Override
	protected void initInternal() throws TransientFailureException, 
	DriverInitializationFailedException {
		super.initInternal();
		try{
			URL url = new URL(getHttpUrl());
			HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.connect();
		}catch (IOException e) {
			throw new TransientFailureException("Could not connect to url: " 
					+ getHttpUrl() + " due to reason: " + e.getMessage(), e);
		}	
	}

	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest,
			ChangeCaseStrategy caseStrategy, boolean btrimUserIdentity,
			StripUserIdentityStrategy stripStrategy, String realmSeparator,
			String userIdentity)
	throws DriverProcessFailedException {
		
		
		List<String> driverLevelUIAttr =  getUserIdentityAttributes();
		
		if (driverLevelUIAttr != null){
			for (String userIdAttr : driverLevelUIAttr){
				String userIdValue = getValueForIdentityAttribute(serviceRequest, userIdAttr); 
				if ( userIdValue != null){
					userIdentity = userIdValue;
					break;
				}
			}
		}

		if (btrimUserIdentity) {
			userIdentity = userIdentity.trim();
		}
		
		userIdentity = caseStrategy.apply(userIdentity);

		String strUnstrippedUserIdentityValue = userIdentity;

		userIdentity = stripStrategy.apply(userIdentity, realmSeparator);

		String urlString = getHttpUrl()+userIdentity;

		AccountData accountData=null;
		BufferedReader in= null ;

		try{

			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			connection.connect();
			HttpURLConnection httpURLConnection = (HttpURLConnection)connection ;

			int responseCode = httpURLConnection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK){
				InputStream inputStream = httpURLConnection.getInputStream();
				in = new BufferedReader( new InputStreamReader(inputStream));
				String responseString="";
				String tmpString="";

				while ((tmpString = in.readLine()) != null && tmpString.length()>0) 
					responseString = responseString+tmpString;
				in.close();

				if(responseString!=null){
					String[] responseArray = ParserUtility.splitString(responseString, '|',';');
					if(responseArray !=null && responseArray.length>0){

						Map<String,String> responseMap = new HashMap<String, String>();
						for (int i=0 ; i<responseArray.length ; i++){
							responseMap.put(String.valueOf(i), responseArray[i]);
						}
						
						serviceRequest.setParameter(AAAServerConstants.CUI_KEY, userIdentity);
						serviceRequest.setParameter(AAAServerConstants.UNSTRIPPED_CUI, strUnstrippedUserIdentityValue);

						HttpAccountDataValueProvider valueProvider = new HttpAccountDataValueProvider(responseMap);
						accountData = buildAccountData(serviceRequest,valueProvider, getAccountDataFieldMapping());
						if (accountData != null){
							accountData.setUserIdentity(userIdentity);
						}
						maxQueryTimeoutCount.set(0);
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Invalid response received from "+getHttpUrl());
					}

				}	

			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Unexpected response : "+HTTPResponseCodes.getIpAddressTypeString(responseCode)+" received from "+getHttpUrl());
			}


		}catch (Exception e) {
			this.maxQueryTimeoutCount.incrementAndGet();
			if(maxQueryTimeoutCount.get()>=getMaxQueryTimeoutCount()){
				LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts,so System "+getName()+" marked as DEAD");
				markDead();
			}	

			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Problem during getting account data from driver :"+getName()+" , Reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}finally{
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Problem during closing input stream :"+e.getMessage());
				}
		}

		return accountData;
	}

	@Override
	public void scan() {
		try{
			URL url = new URL(getHttpUrl());
			HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.connect();
			markAlive();
		}catch (Exception e) {
			markDead();
			LogManager.getLogger().warn(MODULE, "HTTP Auth Driver: " + getName()+ " is still dead.");
		}	
	}

	public abstract String getHttpUrl();

	public abstract AccountDataFieldMapping getAccountDataFieldMapping();

	public abstract int getMaxQueryTimeoutCount();

	public abstract SimpleDateFormat[] getExpiryPatterns();

	public abstract List<String> getUserIdentityAttributes();


	private enum HTTPResponseCodes {

		HTTP_ACCEPTED(HttpURLConnection.HTTP_ACCEPTED, "Accepted"),
		HTTP_BAD_GATEWAY(HttpURLConnection.HTTP_BAD_GATEWAY, "Bad Gateway"),
		HTTP_BAD_METHOD(HttpURLConnection.HTTP_BAD_METHOD, "Bad Method"),
		HTTP_BAD_REQUEST(HttpURLConnection.HTTP_BAD_REQUEST,"Bad Request" ),
		HTTP_CLIENT_TIMEOUT(HttpURLConnection.HTTP_CLIENT_TIMEOUT,"Client Timeout" ),
		HTTP_CONFLICT(HttpURLConnection.HTTP_CONFLICT, "Conflict"),
		HTTP_CREATED(HttpURLConnection.HTTP_CREATED, "Created"),
		HTTP_ENTITY_TOO_LARGE(HttpURLConnection.HTTP_ENTITY_TOO_LARGE, "Entity too large"),
		HTTP_FORBIDDEN(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden"),
		HTTP_GATEWAY_TIMEOUT(HttpURLConnection.HTTP_GATEWAY_TIMEOUT,"Gateway timeout" ),
		HTTP_GONE(HttpURLConnection.HTTP_GONE, "Gone"),
		HTTP_INTERNAL_ERROR(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal error"),
		HTTP_LENGTH_REQUIRED(HttpURLConnection.HTTP_LENGTH_REQUIRED,"Length required" ),
		HTTP_MOVED_PERM(HttpURLConnection.HTTP_MOVED_PERM,"Moved permanently" ),
		HTTP_MOVED_TEMP(HttpURLConnection.HTTP_MOVED_TEMP,"Moved temporarily" ),
		HTTP_MULT_CHOICE(HttpURLConnection.HTTP_MULT_CHOICE, "Multiple choices"),
		HTTP_NO_CONTENT(HttpURLConnection.HTTP_NO_CONTENT,"No content" ),
		HTTP_NOT_AUTHORITATIVE(HttpURLConnection.HTTP_NOT_AUTHORITATIVE, "Not authoritative"),
		HTTP_NOT_FOUND(HttpURLConnection.HTTP_NOT_FOUND, "Not found"),
		HTTP_NOT_MODIFIED(HttpURLConnection.HTTP_NOT_MODIFIED,"Not modified"),
		HTTP_OK(HttpURLConnection.HTTP_OK,"OK"),
		HTTP_PARTIAL(HttpURLConnection.HTTP_PARTIAL,"Partial"),
		HTTP_PAYMENT_REQUIRED(HttpURLConnection.HTTP_PAYMENT_REQUIRED,"Payment required"),
		HTTP_PRECON_FAILED(HttpURLConnection.HTTP_PRECON_FAILED,"Precondition failed"),
		HTTP_PROXY_AUTH(HttpURLConnection.HTTP_PROXY_AUTH,"Proxy authentication required"),
		HTTP_RESET(HttpURLConnection.HTTP_RESET,"Reset"),
		HTTP_SEE_OTHER(HttpURLConnection.HTTP_SEE_OTHER,"See other"),
		HTTP_UNAUTHORIZED(HttpURLConnection.HTTP_UNAUTHORIZED,"Unauthorized"),
		HTTP_UNAVAILABLE(HttpURLConnection.HTTP_UNAVAILABLE,"Unavailable"),
		HTTP_UNSUPPORTED_TYPE(HttpURLConnection.HTTP_UNSUPPORTED_TYPE,"Unsupported type"),
		HTTP_USE_PROXY(HttpURLConnection.HTTP_USE_PROXY,"Use proxy"),
		HTTP_VERSION(HttpURLConnection.HTTP_VERSION,"Version not supported"),;

		public final int intResponseCode;
		public final String strResponseMsg;
		private static final Map<Integer,HTTPResponseCodes> map;

		public static final HTTPResponseCodes[] VALUES = values();

		static {
			map = new HashMap<Integer,HTTPResponseCodes>();
			for (HTTPResponseCodes type : VALUES) {
				map.put(type.intResponseCode, type);
			}


		}
		HTTPResponseCodes(int code,String strResponseMsg) {
			this.intResponseCode = code;
			this.strResponseMsg = strResponseMsg;
		}

		public static String getIpAddressTypeString(int ipAddressTypeCode) {
			HTTPResponseCodes httpResponseCode = map.get(ipAddressTypeCode);  
			if(httpResponseCode != null){
				return httpResponseCode.strResponseMsg;
			}

			return "INVALID RESPONSE CODE";
		}



	}

	public class HttpAccountDataValueProvider implements AccountDataValueProvider {

		Map<String,String> responseMap;
		public HttpAccountDataValueProvider(Map<String, String> responseMap){
			this.responseMap = responseMap;
		}
		@Override
		public String getStringValue(String fieldName) {
			return responseMap.get(fieldName);
		}

		@Override
		public Date getDateValue(String fieldName) {
			Date date = null;
			String strValue = responseMap.get(fieldName);
			if (strValue != null){
					SimpleDateFormat[] expiryDatePatterns = getExpiryPatterns();
					for(int k=0;k<expiryDatePatterns.length;k++) {
						try {
							if (strValue != null){
								date = expiryDatePatterns[k].parse(strValue);							
								break;
							}
						} catch (ParseException e) {
							LogManager.getLogger().warn(MODULE, "Date could not be parsed using pattern : "+expiryDatePatterns[k]+" Date: " + strValue + " Reason: " + e.getMessage());
						}
					}
			}
			return date;
		}
		
	}
}
