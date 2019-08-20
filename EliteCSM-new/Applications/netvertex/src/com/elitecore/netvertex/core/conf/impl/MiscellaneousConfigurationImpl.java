package com.elitecore.netvertex.core.conf.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.lang3.Range;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;
import com.elitecore.netvertex.service.offlinernc.util.RoundingModeTypes;

public class MiscellaneousConfigurationImpl extends BaseConfigurationImpl implements MiscellaneousConfiguration{

	private static final String MODULE = "MISCELLANEOUS-CONFIG";
	private static final String FILE_NAME = "misc-config.properties";
	private static final int MIN_RECORD_RESERVATION_LIMIT = 1000;
	private static final int MAX_RECORD_RESERVATION_LIMIT = 50000;
	private static final int MIN_COA_DELAY_LIMIT = 0;
	private static final int MAX_COA_DELAY_LIMIT = 10;
	private static final int COA_DELAY_DEFAULT = 1;
	private static final boolean RAR_ENABLED_DEFAULT = true;
	private static final boolean SLR_ENABLED_ON_SNR_DEFAULT = false;
	private static final boolean ADDON_START_RAR_ENABLED_DEFAULT = true;	
	private static final boolean ADDON_EXPIRY_RAR_ENABLED_DEFAULT = true;
	private static final boolean ADDON_RESERVATION_ENABLED_DEFAULT = false;
	private static final boolean SESSION_NOWAIT_DEFAULT = true;
	private static final boolean SESSION_BATCH_DEFAULT = true;
	private static final int OVERLOAD_RESULT_CODE_DEFAULT = ResultCode.DIAMETER_TOO_BUSY.code;
	private static final int RECORD_RESERVATION_LIMIT_DEFAULT = 6000;
	private static final boolean SESSION_CACHE_ENABLED_DEFAULT = true;
	private static final boolean SPR_CACHE_ENABLED_DEFAULT = true;
	private static final boolean SERVERINITIATED_DESTINATION_HOST_DEFAULT = true;
	private static final boolean EPS_QOS_ROUNDING_DEFAULT = false;
	private static final String SYSTEM_CURRENCY_ISO_CODE_DEFAULT = "USD";
	private static final String MULTI_VALUE_SEPARATOR_DEFAULT = ":";
	private static final String RATING_KEY_DEFAULT = OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName();
	private static final String TIME_FORMAT_DEFAULT = "dd-MM-yyyy HH:mm:ss";
	private static final int RATING_DECIMAL_POINT_DEFAULT = 6;
	private static final int RATING_DECIMAL_POINT_MIN = 1;
	private static final RoundingModeTypes ROUNDING_MODE_DEFAULT = RoundingModeTypes.TRUNCATE;

	private volatile boolean isRAREnabled;
	private volatile boolean addOnStartRAREnabled;
	private volatile boolean addOnExpiryRAREnabled;
	private volatile boolean addOnReservationEnabled;
	private volatile boolean isSessionNoWait;
	private volatile boolean isSessionBatch;
	private volatile int resultCodeOnOverload;
	private volatile int recordReservationLimit;
	private volatile boolean isSessionCacheEnabled;
	private volatile boolean isSPRCacheEnabled;
	private volatile int coaDelay;
	private volatile boolean sendDestinationHost;
	private volatile boolean isEPSQoSRoundingEnable;
	private volatile String systemCurrencyIsoCode;
	private volatile String multiValueSeparator;
	private volatile String ratingKey;
	private volatile String timeFormat;
	private volatile int ratingDecimalPoints;
	private final Range<Integer> decimalPointRange = Range.between(RATING_DECIMAL_POINT_MIN, RATING_DECIMAL_POINT_DEFAULT);
	private volatile RoundingModeTypes roundingMode;

	private Properties properties;
	private boolean slrEnabledOnSNR;

	public MiscellaneousConfigurationImpl(ServerContext serverContext) {
		super(serverContext);
		properties = new Properties();
	}

	@Override
	public String getParameterValue(String parameterName) {
		return properties.getProperty(parameterName);
	}

	public void readConfiguration(){
		getLogger().info(MODULE,"Read configuration operation for Miscellaneous parameters started");
		
		File propertyFile = new File(getFileLocation());
		try (FileInputStream fileInputStream = new FileInputStream(propertyFile)) {
			Properties tempProperties = new Properties();
			tempProperties.load(fileInputStream);
			this.properties = tempProperties;

			isRAREnabled = stringParamToBoolean(MiscellaneousConfiguration.RAR_ENABLED, RAR_ENABLED_DEFAULT, null);
			slrEnabledOnSNR = stringParamToBoolean(MiscellaneousConfiguration.SLR_ENABLED_ON_SNR, SLR_ENABLED_ON_SNR_DEFAULT, null);
			addOnStartRAREnabled = stringParamToBoolean(MiscellaneousConfiguration.ADDON_START_ENABLED, ADDON_START_RAR_ENABLED_DEFAULT, null);
			addOnExpiryRAREnabled = stringParamToBoolean(MiscellaneousConfiguration.ADDON_EXPIRY_ENABLED, ADDON_EXPIRY_RAR_ENABLED_DEFAULT, null);
			addOnReservationEnabled = stringParamToBoolean(MiscellaneousConfiguration.ADDON_RESERVATION, ADDON_RESERVATION_ENABLED_DEFAULT, null); 
			isSessionNoWait = stringParamToBoolean(MiscellaneousConfiguration.SESSION_NOWAIT, SESSION_NOWAIT_DEFAULT, null);
			isSessionBatch = stringParamToBoolean(MiscellaneousConfiguration.SESSION_BATCH, SESSION_BATCH_DEFAULT, null);
			isSessionCacheEnabled = stringParamToBoolean(MiscellaneousConfiguration.SESSION_CACHE_ENABLED, SESSION_CACHE_ENABLED_DEFAULT, null);
			isSPRCacheEnabled = stringParamToBoolean(MiscellaneousConfiguration.SPR_CACHE_ENABLED, SPR_CACHE_ENABLED_DEFAULT, null);
			resultCodeOnOverload = readResultCodeOnOverload(null);
			recordReservationLimit = stringParamToInteger(MiscellaneousConfiguration.RECORD_RESERVATION_LIMIT, RECORD_RESERVATION_LIMIT_DEFAULT, null, MIN_RECORD_RESERVATION_LIMIT, MAX_RECORD_RESERVATION_LIMIT);
			coaDelay = stringParamToInteger(MiscellaneousConfiguration.COA_DELAY, COA_DELAY_DEFAULT, null, MIN_COA_DELAY_LIMIT, MAX_COA_DELAY_LIMIT);
			sendDestinationHost = stringParamToBoolean(MiscellaneousConfiguration.SERVERINITIATED_DESTINATION_HOST, SERVERINITIATED_DESTINATION_HOST_DEFAULT, null);
			isEPSQoSRoundingEnable = stringParamToBoolean(MiscellaneousConfiguration.EPS_QOS_ROUNDING, EPS_QOS_ROUNDING_DEFAULT, null);
			systemCurrencyIsoCode = readSystemCurrencyIsoCode();
			multiValueSeparator =  readMultiValueSeparator();
			ratingKey = readRatingKey();
			timeFormat = readTimeFormat();
			ratingDecimalPoints = readRatingDecimalPoint();
			roundingMode = readRoundingMode();

			ConfigLogger.getInstance().info(MODULE, this.toString());
			getLogger().debug(MODULE,"Read configuration for Miscellaneous parameters completed");
		} catch(Exception e) {
			getLogger().trace(MODULE,e);
			getLogger().error(MODULE, "Error while reading Miscellaneous parameters configuration. Reason: " + e.getMessage());
		}
	}

	private RoundingModeTypes readRoundingMode() {
		String parameterValue = getParameterValue(MiscellaneousConfiguration.ROUNDING_MODE);
		if (Strings.isNullOrBlank(parameterValue)) {
			getLogger().warn(MODULE, "Considering default value: " + ROUNDING_MODE_DEFAULT + " for " + 
					"parameter: " + MiscellaneousConfiguration.ROUNDING_MODE + ", Reason: parameter value not found");
			return ROUNDING_MODE_DEFAULT;
		}
		
		RoundingModeTypes mode = RoundingModeTypes.getByName(parameterValue);
		if (mode == null) {
			getLogger().warn(MODULE, "Considering default value: " + ROUNDING_MODE_DEFAULT + " for " + 
					"parameter: " + MiscellaneousConfiguration.ROUNDING_MODE + ", Reason: unknown mode: " + parameterValue);
			return ROUNDING_MODE_DEFAULT;
		} else {
			return mode;
		}
	}

	private int readRatingDecimalPoint() {
		String parameterValueString = getParameterValue(MiscellaneousConfiguration.RATING_DECIMAL_POINTS);
		if (Strings.isNullOrBlank(parameterValueString)) {
			getLogger().warn(MODULE, "Considering default value: " + RATING_DECIMAL_POINT_DEFAULT + " for " +
					"parameter: " + MiscellaneousConfiguration.RATING_DECIMAL_POINTS + ", Reason: parameter value not found");
			return RATING_DECIMAL_POINT_DEFAULT;
		}
		
		try {
			int parameterValue = Integer.parseInt(parameterValueString.trim());

			if (decimalPointRange.contains(parameterValue) == false) {
				getLogger().warn(MODULE, "Considering default value: " + RATING_DECIMAL_POINT_DEFAULT + " for " + 
						"parameter: " + MiscellaneousConfiguration.RATING_DECIMAL_POINTS + ", Reason: parameter value - " + parameterValue
						+ " not within range " + decimalPointRange);
				return RATING_DECIMAL_POINT_DEFAULT;
			} else {
				return parameterValue;
			}
		} catch (NumberFormatException ex) {
			getLogger().trace(MODULE, ex);
			getLogger().warn(MODULE, "Considering default value: " + RATING_DECIMAL_POINT_DEFAULT + " for " +
					"parameter: " + RATING_DECIMAL_POINTS + ", Reason: Invalid parameter value: " + parameterValueString);
			return RATING_DECIMAL_POINT_DEFAULT;
		}
	}
	
	private String readRatingKey() {
		String parameterValueString = getParameterValue(MiscellaneousConfiguration.RATING_KEY);

		if (Strings.isNullOrBlank(parameterValueString)) {
			getLogger().warn(MODULE, "Considering default value: " + RATING_KEY_DEFAULT +" for " + 
					"parameter: " + MiscellaneousConfiguration.RATING_KEY + ", Reason: parameter value not found");
			return RATING_KEY_DEFAULT;
		} else {
			return parameterValueString;
		}
	}

	private String readTimeFormat() {
		String parameterValueString = getParameterValue(MiscellaneousConfiguration.TIME_FORMAT);

		if (Strings.isNullOrBlank(parameterValueString)) {
			getLogger().warn(MODULE, "Considering default value: " + TIME_FORMAT_DEFAULT +" for " + 
					"parameter: " + MiscellaneousConfiguration.TIME_FORMAT + ", Reason: parameter value not found");
			return TIME_FORMAT_DEFAULT;
		} else {
			return parameterValueString;
		}
	}

	private String readMultiValueSeparator() {
		String parameterValueString = getParameterValue(MiscellaneousConfiguration.MULTI_VALUE_SEPARATOR);

		if (Strings.isNullOrBlank(parameterValueString)) {
			getLogger().warn(MODULE, "Considering default value: " + MULTI_VALUE_SEPARATOR_DEFAULT +" for " + 
					"parameter: " + MiscellaneousConfiguration.MULTI_VALUE_SEPARATOR + ", Reason: parameter value not found");
			return MULTI_VALUE_SEPARATOR_DEFAULT;
		} else {
			return parameterValueString;
		}
	}

	private String readSystemCurrencyIsoCode() {
		String parameterValueString = getParameterValue(MiscellaneousConfiguration.SYSTEM_CURRENCY_ISO_CODE);

		if (Strings.isNullOrBlank(parameterValueString)) {
			getLogger().warn(MODULE, "Considering default value: " + SYSTEM_CURRENCY_ISO_CODE_DEFAULT+" for " + 
					"parameter: " + MiscellaneousConfiguration.SYSTEM_CURRENCY_ISO_CODE + ", Reason: parameter value not found");
			return SYSTEM_CURRENCY_ISO_CODE_DEFAULT;
		} else {
			return parameterValueString;
		}
	}

	private ILogger getLogger() {
		return LogManager.getLogger();
	}

	private String getFileLocation(){
		return getServerContext().getServerHome() + File.separator + "system" + File.separator + FILE_NAME;
	}

	public void reloadConfiguration(){
		getLogger().info(MODULE,"Reload configuration operation for Miscellaneous parameters started");
		File propertyFile = null;
		FileInputStream fileInputStream = null;
		try {
			Properties tempProperties = new Properties();
			propertyFile = new File(getFileLocation());
			fileInputStream = new FileInputStream(propertyFile);
			tempProperties.load(fileInputStream);
			this.properties = tempProperties;
			reloadParameters();
			getLogger().info(MODULE,"Reload configuration operation for Miscellaneous parameters completed");
		} catch (Exception e) {
			getLogger().trace(MODULE,e);
			getLogger().error(MODULE, "Error while reloading Miscellaneous parameters configuration. Reason: " + e.getMessage());
		}
	}

	private void reloadParameters(){
		boolean isRAREnabledNew = stringParamToBoolean(MiscellaneousConfiguration.RAR_ENABLED, RAR_ENABLED_DEFAULT, isRAREnabled);
		if (isRAREnabledNew != isRAREnabled) {
			isRAREnabled = isRAREnabledNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.RAR_ENABLED  + " parameter changed. New value: " +  //NOSONAR
					isRAREnabled);
		}
		boolean slrEnabledOnSNRNew = stringParamToBoolean(MiscellaneousConfiguration.SLR_ENABLED_ON_SNR, SLR_ENABLED_ON_SNR_DEFAULT, slrEnabledOnSNR);
		if (slrEnabledOnSNRNew != slrEnabledOnSNR) {
			slrEnabledOnSNR = slrEnabledOnSNRNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.SLR_ENABLED_ON_SNR + " parameter changed. New value: " + //NOSONAR
					slrEnabledOnSNR);
		}
		boolean addOnStartRAREnabledNew = stringParamToBoolean(MiscellaneousConfiguration.ADDON_START_ENABLED, ADDON_START_RAR_ENABLED_DEFAULT, addOnStartRAREnabled);
		if (addOnStartRAREnabledNew != addOnStartRAREnabled) {
			addOnStartRAREnabled = addOnStartRAREnabledNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.ADDON_START_ENABLED  + " parameter changed. New value: " +  //NOSONAR
					addOnStartRAREnabled);
		} 

		boolean addOnExpiryRAREnabledNew = stringParamToBoolean(MiscellaneousConfiguration.ADDON_EXPIRY_ENABLED, ADDON_EXPIRY_RAR_ENABLED_DEFAULT, addOnExpiryRAREnabled);
		if (addOnExpiryRAREnabledNew != addOnExpiryRAREnabled) {
			addOnExpiryRAREnabled = addOnExpiryRAREnabledNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.ADDON_EXPIRY_ENABLED  + " parameter changed. New value: " + //NOSONAR
					addOnExpiryRAREnabled);
		} 
		boolean addOnReservationEnabledNew = stringParamToBoolean(MiscellaneousConfiguration.ADDON_RESERVATION, ADDON_RESERVATION_ENABLED_DEFAULT, addOnReservationEnabled);
		if (addOnReservationEnabledNew != addOnReservationEnabled) {
			addOnReservationEnabled = addOnReservationEnabledNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.ADDON_RESERVATION  + " parameter changed. New value: " +  //NOSONAR
					addOnReservationEnabled);
		} 
		boolean isSessionNoWaitNew = stringParamToBoolean(MiscellaneousConfiguration.SESSION_NOWAIT, SESSION_NOWAIT_DEFAULT, isSessionNoWait);
		if (isSessionNoWaitNew != isSessionNoWait) {
			isSessionNoWait = isSessionNoWaitNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.SESSION_NOWAIT  + " parameter changed. New value: " + 
					isSessionNoWait);
		} 
		boolean isSessionBatchNew = stringParamToBoolean(MiscellaneousConfiguration.SESSION_BATCH, SESSION_BATCH_DEFAULT, isSessionBatch);
		if (isSessionBatchNew != isSessionBatch) {
			isSessionBatch = isSessionBatchNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.SESSION_BATCH  + " parameter changed. New value: " + 
					isSessionBatch);
		}
		boolean isSessionCacheEnabledNew = stringParamToBoolean(MiscellaneousConfiguration.SESSION_CACHE_ENABLED, SESSION_CACHE_ENABLED_DEFAULT, isSessionCacheEnabled);
		if (isSessionCacheEnabledNew != isSessionCacheEnabled) {
			isSessionCacheEnabled = isSessionCacheEnabledNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.SESSION_CACHE_ENABLED  + " parameter changed. New value: " + 
					isSessionCacheEnabled);
		} 
		boolean isSPRCacheEnabledNew = stringParamToBoolean(MiscellaneousConfiguration.SPR_CACHE_ENABLED, SPR_CACHE_ENABLED_DEFAULT, isSPRCacheEnabled);
		if (isSPRCacheEnabledNew != isSPRCacheEnabled) {
			isSPRCacheEnabled = isSPRCacheEnabledNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.SPR_CACHE_ENABLED  + " parameter changed. New value: " + 
					isSPRCacheEnabled);
		} 
		int resultCodeOnOverloadNew = readResultCodeOnOverload(resultCodeOnOverload);
		if (resultCodeOnOverloadNew != resultCodeOnOverload) {
			resultCodeOnOverload = resultCodeOnOverloadNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.OVERLOAD_RESULT_CODE  + " parameter changed. New value: " + 
					resultCodeOnOverload);
		} 
		int recordReservationLimitNew = stringParamToInteger(MiscellaneousConfiguration.RECORD_RESERVATION_LIMIT, RECORD_RESERVATION_LIMIT_DEFAULT, recordReservationLimit, MIN_RECORD_RESERVATION_LIMIT, MAX_RECORD_RESERVATION_LIMIT);
		if (recordReservationLimitNew != recordReservationLimit) {
			recordReservationLimit = recordReservationLimitNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.RECORD_RESERVATION_LIMIT + " parameter changed. New value: " + 
					recordReservationLimit);
		} 
		int coaDelayNew = stringParamToInteger(MiscellaneousConfiguration.COA_DELAY, COA_DELAY_DEFAULT, coaDelay, MIN_COA_DELAY_LIMIT, MAX_COA_DELAY_LIMIT);
		if (coaDelayNew != coaDelay) {
			coaDelay = coaDelayNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.COA_DELAY + " parameter changed. New value: " + 
					coaDelay);
		}
		boolean sendDestinationHostNew = stringParamToBoolean(MiscellaneousConfiguration.SERVERINITIATED_DESTINATION_HOST, SERVERINITIATED_DESTINATION_HOST_DEFAULT
				, this.sendDestinationHost);

		if (sendDestinationHostNew != this.sendDestinationHost) {
			this.sendDestinationHost = sendDestinationHostNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.SERVERINITIATED_DESTINATION_HOST  + " parameter changed. New value: " + 
					this.sendDestinationHost);
		}
		boolean isEPSQoSRoundingEnableNew = stringParamToBoolean(MiscellaneousConfiguration.EPS_QOS_ROUNDING, EPS_QOS_ROUNDING_DEFAULT, isEPSQoSRoundingEnable);

		if (isEPSQoSRoundingEnableNew != isEPSQoSRoundingEnable) {
			isEPSQoSRoundingEnable = isEPSQoSRoundingEnableNew;
			getLogger().info(MODULE, MiscellaneousConfiguration.EPS_QOS_ROUNDING + " parameter changed. New value: " + 
					isEPSQoSRoundingEnable);
		} 
	}

	private int readResultCodeOnOverload(Integer previousValue){
		String parameterValueString = getParameterValue(MiscellaneousConfiguration.OVERLOAD_RESULT_CODE);

		if ( Strings.isNullOrBlank(parameterValueString) ) {
			getLogger().warn(MODULE, "Considering default value: " + OVERLOAD_RESULT_CODE_DEFAULT +" for " + //NOSONAR
					"parameter: " + MiscellaneousConfiguration.OVERLOAD_RESULT_CODE + ", Reason: parameter value not found"); //NOSONAR
			return OVERLOAD_RESULT_CODE_DEFAULT;
		}
		try {

			int parameterValueInt = Integer.parseInt(parameterValueString.trim());
			if (previousValue == null){
				if(parameterValueInt == 0 || ResultCode.isValid(parameterValueInt)){
					getLogger().info(MODULE, "Considering value: "+ parameterValueInt + " for parameter: " + MiscellaneousConfiguration.OVERLOAD_RESULT_CODE); //NOSONAR
					return parameterValueInt;
				} else {
					getLogger().warn(MODULE, "Considering default value: " + OVERLOAD_RESULT_CODE_DEFAULT  +" for parameter: " + MiscellaneousConfiguration.OVERLOAD_RESULT_CODE + ", Reason: value: "+ //NOSONAR
							parameterValueInt +" is invalid result-code");//NOSONAR
					return OVERLOAD_RESULT_CODE_DEFAULT;
				}
			} else {
				if(parameterValueInt == 0 || ResultCode.isValid(parameterValueInt)){
					getLogger().info(MODULE, "Considering value: "+ parameterValueInt + " for parameter: " + MiscellaneousConfiguration.OVERLOAD_RESULT_CODE);
					return parameterValueInt;
				} else {
					getLogger().warn(MODULE, "Considering previous value: " + resultCodeOnOverload  +" for parameter: " + MiscellaneousConfiguration.OVERLOAD_RESULT_CODE + ", Reason: value: "+ //NOSONAR
							parameterValueInt +" is invalid result-code");
					return previousValue;
				}
			}
		} catch(Exception e) {
			getLogger().trace(MODULE,e);
			if (previousValue == null) {
				getLogger().warn(MODULE, "Considering default value: " + OVERLOAD_RESULT_CODE_DEFAULT +" for " +
						"parameter: " + MiscellaneousConfiguration.OVERLOAD_RESULT_CODE + ", Reason: Invalid parameter value: " + parameterValueString); //NOSONAR
				return OVERLOAD_RESULT_CODE_DEFAULT;
			} else {
				getLogger().warn(MODULE, "Considering previous value: " + previousValue +" for " + //NOSONAR
						"parameter: " + MiscellaneousConfiguration.OVERLOAD_RESULT_CODE + ", Reason: Invalid parameter value: " + parameterValueString); //NOSONAR
				return previousValue;
			}
		}
	}

	/**
	 * All 3 methods below is used to parse miscellaneous configuration during read and reload.
	 * Here default value cannot be parsed as null, if it is null, there can be NPE while unboxing their values.
	 * 
	 */
	private int stringParamToInteger(String parameterName, Integer defaultValue, Integer previousValue, int minValue, int maxValue){
		String parameterValueString = getParameterValue(parameterName);
		if ( Strings.isNullOrBlank(parameterValueString) ) {
			getLogger().warn(MODULE, "Considering default value: " + defaultValue +" for " +
					"parameter: " + parameterName + ", Reason: parameter value not found");
			return defaultValue;
		}
		try {
			int parameterValueInt = Integer.parseInt(parameterValueString.trim());
			if (previousValue == null){
				if (parameterValueInt > maxValue) {
					getLogger().warn(MODULE, "Considering maximum value: " + maxValue +" for parameter: " + parameterName + ", Reason: value: "+
							parameterValueInt +" is not recommended ");
					return maxValue;
				} else if(parameterValueInt < minValue) {
					getLogger().warn(MODULE, "Considering minimum value: " + minValue +" for parameter: " + parameterName + ", Reason: value: "+
							parameterValueInt +" is not recommended ");
					return minValue;
				} else {
					getLogger().info(MODULE, "Considering value: "+ parameterValueInt + " for parameter: " + parameterName);
					return parameterValueInt;
				}
			} else {
				if (parameterValueInt > maxValue) {
					getLogger().warn(MODULE, "Considering previous value: " + previousValue +" for parameter: " + parameterName + ", Reason: value: "+
							parameterValueInt +" exceeding maximum value: " + maxValue);
					return previousValue;
				} else if (parameterValueInt < minValue) {
					getLogger().warn(MODULE, "Considering previous value: " + previousValue +" for parameter: " + parameterName + ", Reason: value: "+
							parameterValueInt +" is lower then minimum value: " + minValue);
					return previousValue;
				} else {
					getLogger().info(MODULE, "Considering value: "+ parameterValueInt + " for parameter: " + parameterName);
					return parameterValueInt;
				}
			}
		} catch(Exception e) {
			getLogger().trace(MODULE,e);
			if (previousValue == null) {
				getLogger().warn(MODULE, "Considering default value: " + defaultValue +" for " +
						"parameter: " + parameterName + ", Reason: Invalid parameter value: " + parameterValueString);
				return defaultValue;
			} else {
				getLogger().warn(MODULE, "Considering previous value: " + previousValue +" for " +
						"parameter: " + parameterName + ", Reason: Invalid parameter value: " + parameterValueString);
				return previousValue;
			}
		}
	}

	private boolean stringParamToBoolean(String parameterName, Boolean defaultValue, Boolean previousValue) {
		String parameterValueString = getParameterValue(parameterName);
		if( Strings.isNullOrBlank(parameterValueString) ){
			getLogger().warn(MODULE, "Considering default value: " + defaultValue +" for " +
					"parameter: " + parameterName + ", Reason: parameter value not found");
			return defaultValue;
		}
		if ( "true".equalsIgnoreCase(parameterValueString.trim()) ){
			getLogger().info(MODULE,"Considering value: true for parameter: " + parameterName);
			return true;
		} else if ( "false".equalsIgnoreCase(parameterValueString.trim()) ) {
			getLogger().info(MODULE,"Considering value: false for parameter: " + parameterName);
			return false;
		} else {
			if (previousValue == null) {
				getLogger().warn(MODULE, "Considering default value: " + defaultValue +" for " +
						"parameter: " + parameterName + ", Reason: Invalid parameter value: " + parameterValueString);
				return defaultValue;
			} else {
				getLogger().warn(MODULE, "Considering previous value: " + previousValue +" for " +
						"parameter: " + parameterName + ", Reason: Invalid parameter value: " + parameterValueString);
				return previousValue;
			}
		}
	}

	@Override
	public boolean getRAREnabled() {
		return isRAREnabled;
	}

	@Override
	public boolean getAddOnStartRAREnabled() {
		return addOnStartRAREnabled;
	}

	@Override
	public boolean getAddOnExpiryRAREnabled() {
		return addOnExpiryRAREnabled;
	}

	@Override
	public boolean getAddOnReservationEnabled() {
		return addOnReservationEnabled;
	}

	@Override
	public boolean getSessionNoWait() {
		return isSessionNoWait;
	}

	@Override
	public boolean getSessionBatch() {
		return isSessionBatch;
	}

	@Override
	public int getResultCodeOnOverload() {
		return resultCodeOnOverload;
	}

	@Override
	public int getRecordReservationLimit() {
		return recordReservationLimit;
	}

	@Override
	public boolean isSessionCacheEnabled() {
		return isSessionCacheEnabled;
	}

	@Override
	public int getCOADelay() {
		return coaDelay;
	}

	@Override
	public boolean isEPSQoSRoundingEnable() {
		return isEPSQoSRoundingEnable;
	}

	@Override
	public boolean getSLREnabledOnSNR() {
		return slrEnabledOnSNR;
	}

	@Override
	public String getCoresessionCoresessionId() {
		return properties.getProperty(CORESESSION_CORESESSIONID);
	}

	@Override
	public String getSessionRuleCoresessionId() {
		return properties.getProperty(SESSIONRULE_CORESESSIONID);
	}

	@Override
	public String getSessionRuleCoresessionIdAndPccrule() {
		return properties.getProperty(SESSIONRULE_CORESESSIONID_AND_PCCRULE);
	}

	@Override
	public String getCoresessionSubscriberIdentity() {
		return properties.getProperty(CORESESSION_SUBSCRIBERIDENITITY);
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(writer);

		out.println();
		out.println(" --  Miscellaneous parameters -- ");
		out.println();

		out.incrementIndentation();

		out.println(RAR_ENABLED + " = " + isRAREnabled);
		out.println(SLR_ENABLED_ON_SNR + " = " + slrEnabledOnSNR);
		out.println(ADDON_START_ENABLED + " = " + addOnStartRAREnabled);
		out.println(ADDON_EXPIRY_ENABLED + " = " + addOnExpiryRAREnabled);
		out.println(ADDON_RESERVATION + " = " + addOnReservationEnabled);
		out.println(SESSION_BATCH + " = " + isSessionBatch);
		out.println(SESSION_NOWAIT + " = " + isSessionNoWait);
		out.println(OVERLOAD_RESULT_CODE + " = " + resultCodeOnOverload);
		out.println(RECORD_RESERVATION_LIMIT + " = " + recordReservationLimit);
		out.println(SESSION_CACHE_ENABLED + " = " + isSessionCacheEnabled);
		out.println(SPR_CACHE_ENABLED + " = " + isSPRCacheEnabled);
		out.println(COA_DELAY + " = " + coaDelay);
		out.println(SERVERINITIATED_DESTINATION_HOST + " = " + sendDestinationHost);
		out.println(EPS_QOS_ROUNDING + " = " + isEPSQoSRoundingEnable);
		out.println(CORESESSION_CORESESSIONID + " = " + properties.getProperty(CORESESSION_CORESESSIONID, "NIL"));
		out.println(CORESESSION_SUBSCRIBERIDENITITY + " = " + properties.getProperty(CORESESSION_SUBSCRIBERIDENITITY, "NIL"));
		out.println(SESSIONRULE_CORESESSIONID + " = " + properties.getProperty(SESSIONRULE_CORESESSIONID, "NIL"));
		out.println(SESSIONRULE_CORESESSIONID_AND_PCCRULE + " = " + properties.getProperty(SESSIONRULE_CORESESSIONID_AND_PCCRULE, "NIL"));
		out.println(SYSTEM_CURRENCY_ISO_CODE + " = " + systemCurrencyIsoCode);
		out.println(MULTI_VALUE_SEPARATOR + " = " + multiValueSeparator);
		out.println(TIME_FORMAT + " = " + timeFormat);
		out.println(RATING_KEY + " = " + ratingKey);
		out.println(RATING_DECIMAL_POINTS + " = " + ratingDecimalPoints);
		
		out.decrementIndentation();
		out.close();
		return writer.toString();
	}

	@Override
	public boolean getServerInitiatedDestinationHost() {
		return sendDestinationHost;
	}

	@Override
	public boolean isSPRCacheEnabled() {
		return isSPRCacheEnabled;
	}

	@Override
	public int getRatingDecimalPoints() {
		return ratingDecimalPoints;
	}

	@Override
	public RoundingModeTypes getRoundingMode() {
		return roundingMode;
	}
}