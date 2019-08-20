package com.elitecore.netvertex.core.conf;

import com.elitecore.netvertex.service.offlinernc.util.RoundingModeTypes;

public interface MiscellaneousConfiguration{
	
	public static final String RAR_ENABLED = "RAR-Enabled";
	public static final String ADDON_START_ENABLED = "AddOn-Start-RAR-Enabled";
	public static final String ADDON_EXPIRY_ENABLED = "AddOn-Expiry-RAR-Enabled";
	public static final String ADDON_RESERVATION = "addOn.Reservation";
	public static final String SESSION_NOWAIT = "session-nowait"; 
	public static final String SESSION_BATCH = "session-batch";
	
	public static final String OVERLOAD_RESULT_CODE = "Overload-Result-Code";
	public static final String RECORD_RESERVATION_LIMIT = "record-Reservation-Limit";
	
	public static final String CORESESSION_CORESESSIONID = "Coresession-CoresessionID";
	public static final String SESSIONRULE_CORESESSIONID = "SessionRule-CoresessionID";
	public static final String SESSIONRULE_CORESESSIONID_AND_PCCRULE ="SessionRule-CoresessionIDAndPCCRule";
	public static final String CORESESSION_SUBSCRIBERIDENITITY ="Coresession-SubscriberIdentity";
	public static final String SESSION_CACHE_ENABLED ="session.cache-enabled";
	public static final String SPR_CACHE_ENABLED ="spr.cache-enabled";
	public static final String COA_DELAY ="COA-Delay";
	public static final String SERVERINITIATED_DESTINATION_HOST = "serverinitiated.destination-host";
	public static final String EPS_QOS_ROUNDING ="eps.qos.rounding";
	public static final String SLR_ENABLED_ON_SNR = "sy.slronsnr";
	public static final String SYSTEM_CURRENCY_ISO_CODE = "system-currency-iso-code";
	public static final String MULTI_VALUE_SEPARATOR = "multi-value-separator";
	public static final String RATING_KEY = "rating-key";
	public static final String TIME_FORMAT = "time-format";
	public static final String RATING_DECIMAL_POINTS = "rating-decimal-point";
	public static final String ROUNDING_MODE = "rounding-mode";

	boolean getRAREnabled();
	boolean getAddOnStartRAREnabled();
	boolean getAddOnExpiryRAREnabled();
	boolean getAddOnReservationEnabled();
	boolean getSessionNoWait();
	boolean getSessionBatch();
	int getResultCodeOnOverload();
	int getRecordReservationLimit();		
	String getParameterValue(String parameterName);
	boolean isSessionCacheEnabled();
	boolean isSPRCacheEnabled();
	int getCOADelay();
	String getCoresessionCoresessionId();
	String getSessionRuleCoresessionId();
	String getSessionRuleCoresessionIdAndPccrule();
	String getCoresessionSubscriberIdentity();
	boolean getServerInitiatedDestinationHost();
	public abstract boolean isEPSQoSRoundingEnable();

	boolean getSLREnabledOnSNR();
	int getRatingDecimalPoints();
	RoundingModeTypes getRoundingMode();

}