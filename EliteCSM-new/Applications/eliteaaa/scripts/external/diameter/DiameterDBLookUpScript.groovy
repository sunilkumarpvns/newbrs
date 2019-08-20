package external.diameter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.aaa.core.drivers.BaseAuthDriver.AccountDataValueProvider;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.script.DiameterRouterScript;
import com.elitecore.diameterapi.script.manager.DiameterScriptContext;
import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;

public class DiameterDBLookupScript extends DiameterRouterScript {

	/**
	 * tableName  is the Name of the Table, You want to fetch Records from.
	 */
	private String tableName = "tblradiuscustomer";

	/**
	 * profileLookUpColumnName is the Name of Search Column one of the below Table Fields
	 */
	private String profileLookUpColumnName = USER_NAME;
	private Connection connection;

	/**
	 * Specify DB Feilds
	 */
	private static String USER_PASSWORD = "PASSWORD";
	private static String USER_NAME = "USERNAME";
	private static String ENCRYPTION_TYPE = "ENCRYPTIONTYPE";
	private static String CUSTOMER_STATUS = "CUSTOMERSTATUS";
	private static String EXPIRY_DATE = "EXPIRYDATE";
	private static String CUSTOMER_REJECT_ITEMS = "CUSTOMERREJECTITEM";
	private static String CUSTOMER_CHECK_ITEMS = "CUSTOMERCHECKITEM";
	private static String CUSTOMER_REPLY_ITEMS = "CUSTOMERREPLYITEM";
	private static String CREDIT_LIMIT = "CREDITLIMIT";
	private static String LOGIN_POLICY = "CONCURRENTLOGINPOLICY";
	private static String ACCESS_POLICY = "ACCESSPOLICY";
	private static String ADDITIONAL_POLICY = "ADDITIONALPOLICY";
	private static String PARAM1 = "PARAM1";
	private static String PARAM2 = "PARAM2";
	private static String PARAM3 = "PARAM3";
	private static String PARAM4 = "PARAM4";
	private static String PARAM5 = "PARAM5";
	private static String GROUP_NAME = "GROUPNAME";
	private static String EMAIL="CUSTOMERALTEMAILID";
	private static String CUSTOMER_SERVICES="CUSTOMERSERVICES";
	private static String CUSTOMER_TYPE = "CUSTOMERTYPE";
	private static String PASSWORD_CHECK = "PASSWORDCHECK";
	private static String CALLED_STATION_ID = "CALLEDSTATIONID";
	private static String CALLING_STATION_ID = "CALLINGSTATIONID";
	private static String CUI = "CUI";
	private static String HOTLINE_POLICY = "HOTLINEPOLICY";
	private static String GRACE_PERIOD = "GRACEPERIOD";
	private static String CALLBACK_ID = "CALLBACKID";
	private static String MAC_VALIDATION = "MACVALIDATION";
	private static String IMSI = "IMSI";
	private static String MEID = "MEID";
	private static String MSISDN  = "MSISDN";
	private static String MDN  = "MDN";
	private static String IMEI  = "IMEI";
	private static String DEVICE_VENDOR = "DEVICEVENDOR";
	private static String DEVICE_NAME   = "DEVICENAME";
	private static String DEVICE_PORT   = "DEVICEPORT";
	private static String GEO_LOCATION  = "GEOLOCATION";
	private static String DEVICE_VLAN   = "DEVICEVLAN";
	private static String IP_POOL = "IPPOOLNAME";


	//********* SCRIPTS's INTERNAL DATA. DO NOT CHANGE ************** 
	private static final int IDENTITY_CASE_LOWER = 1;
	private static final int IDENTITY_CASE_UPPER = 2;

	public DiameterDBLookupScript(DiameterScriptContext scriptContext) {
		super(scriptContext);
	}

	@Override
	protected String getName() {
		return "DIA-DBLOOKUP-SCRIPT";
	}

	@Override
	public void initGroovy() throws InitializationFailedException {

		try {
			/*
			Creating DB Connection
			Class.forName("oracle.jdbc.driver.OracleDriver");
			DriverManager.setLoginTimeout(1); //In Seconds
			connection = DriverManager.getConnection(
					DB_URL, 
					DB_USERNAME, 
					DB_PASSWORD);
			*/
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			LogManager.getLogger().info(getName(), "Groovy initialized Successfully");
		} catch (DataSourceException e){
				throw new InitializationFailedException("Connection to Database is not available for URL: " + DB_URL + 
						", Reason: " + e.getMessage() + ", Please verify DB Credentials");
		} catch (ClassNotFoundException e) {
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException ex) {
					LogManager.getLogger().trace(getName(),ex);
				}
			throw new InitializationFailedException(e);
		} catch (SQLException e) {
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException ex) {
					LogManager.getLogger().trace(getName(),ex);
				}
			throw new InitializationFailedException(e);
		}

	}

	@Override
	protected void preRequestRouting(String routingTableName,
			DiameterRequest originRequest, String requestOriginatorPeerId) {
		String userIDAVPID = "0:1 , 0:8";
		AccountData accountData;
		try {
			accountData = getAccountData(originRequest,userIDAVPID, true, IDENTITY_CASE_LOWER);
			if(accountData == null){
				LogManager.getLogger().warn(getName(), "Unable to add " + DiameterAVPConstants.EC_PROFILE_IMSI + 
						" AVP for Request with Session-ID="+originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)+
						", Reason: AccountData not found.");
				return;
			}
			String imsiValue = accountData.getImsi();
			if(imsiValue == null){
				LogManager.getLogger().warn(getName(), "IMSI value not found from profile for Identity: " + profileLookUpColumnName + 
						" for Request with Session-ID=" + originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
				return;
			}
			IDiameterAVP diameterAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.EC_PROFILE_IMSI);
			if(diameterAvp == null){
				LogManager.getLogger().warn(getName(), "Unable to add " + DiameterAVPConstants.EC_PROFILE_IMSI + 
						"AVP for Request with Session-ID=" + originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) + 
						", Reason: AVP not found in Dictionary.");
				return;
			}
			diameterAvp.setStringValue(imsiValue);
			originRequest.addInfoAvp(diameterAvp);
			LogManager.getLogger().warn(getName(), "InfoAVP " + DiameterAVPConstants.EC_PROFILE_IMSI + 
					" with value: " + imsiValue + " added to Request with Session-ID=" + 
					originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
		} catch (Exception e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().warn(getName(), "Error Occured while Fetching Account data for Request with Session-ID="+ 
					originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)+", Reason: " + e.getMessage());
		}

	}

	@Override
	protected void postRequestRouting(String routingTableName,
			String routingEntryName, DiameterRequest originRequest,
			String reqOriginatorPeerId, DiameterRequest destinationRequest,
			String destPeerId) {
		// TODO 

	}

	@Override
	protected void preAnswerRouting(String routingTableName,
			String routingEntryName, DiameterRequest originRequest,
			String ansOriginatorPeerId, DiameterRequest destinationRequest,
			DiameterAnswer originAnswer) {
		// TODO 

	}

	@Override
	protected void postAnswerRouting(String routingTableName,
			String routingEntryName, DiameterRequest originRequest,
			String ansOriginitorPeerId, DiameterRequest destinationRequest,
			DiameterAnswer originAnswer, DiameterAnswer destinationAnswer,
			String destAnsPeerId) {
		// TODO 

	}

	private AccountData getAccountData(DiameterPacket packet, 
			String userIDAVPID, boolean btrimUserIdentity, int iCaseSensitivity) throws Exception {

		String userIdentity = packet.getAVPValue(userIDAVPID);
		String[] userIdentities = userIDAVPID.split(",");

		if(userIdentities == null || userIdentities.length == 0){
			LogManager.getLogger().warn(getName(), "Invalid User Identity:" + userIDAVPID + " provided");
			return null;
		}
		for(int i= 0 ; i< userIdentities.length ; i++){
			userIdentity = packet.getAVPValue(userIdentities[0].trim(), true);
			if(userIdentity != null)
				break;
		}
		if ( userIdentity == null){
			LogManager.getLogger().warn(getName(), "Attribute(s): " + userIDAVPID + 
					" not found in Request with Session-ID=" + 
					packet.getAVPValue(DiameterAVPConstants.SESSION_ID));
			return null;
		}

		if(btrimUserIdentity)
			userIdentity = userIdentity.trim();
		userIdentity = checkForCaseSensitivity(userIdentity, iCaseSensitivity);

		LogManager.getLogger().info(getName(),"Trying to get account data for user identity: " + userIdentity);
		AccountData accountData = null;		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{			
			preparedStatement = connection.prepareStatement(getQueryForAccountData());
			if(preparedStatement == null){
				throw new Exception("PreparedStatement is not available");
			}
			preparedStatement.setString(1,userIdentity);
			preparedStatement.setQueryTimeout(100);
			resultSet = preparedStatement.executeQuery();

			if(resultSet.next()){
				DiaDBAccountDataValueProvider valueProvider = new DiaDBAccountDataValueProvider(resultSet);
				accountData = buildAccountData(packet,valueProvider);
				if (accountData != null){
					accountData.setUserIdentity(userIdentity);					
				}
			}			
		}finally{
			if(resultSet != null)
				try {
					resultSet.close();
				}catch (SQLException e) {
					LogManager.getLogger().trace(getName(),e);
				}
			if(preparedStatement != null)
				try {
					preparedStatement.close();
				}catch (SQLException e) {
					LogManager.getLogger().trace(getName(),e);
				}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(getName(), "Account Data: " + accountData.toString());
		}
		return accountData;

	}

	protected String  checkForCaseSensitivity(String strUserIdentity,int iCaseSensitivity){

		switch (iCaseSensitivity) {
		case IDENTITY_CASE_LOWER:
			return strUserIdentity.toLowerCase();
		case IDENTITY_CASE_UPPER:
			return strUserIdentity.toUpperCase();
		default:
			return strUserIdentity;
		}

	}
	private String getQueryForAccountData(){		
		return "SELECT * FROM " + tableName + " WHERE " + profileLookUpColumnName + "=?";
	}

	protected AccountData buildAccountData(DiameterPacket diameterPacket,AccountDataValueProvider valueProvider){

		//Set required Parameters Require, Other can be commented/removed to save processing time.
		AccountData accountData = new AccountData();

		accountData.setUserName(getValue(USER_NAME, valueProvider));
		accountData.setAccessPolicy(getValue(ACCESS_POLICY, valueProvider));
		accountData.setAccountStatus( getValue(CUSTOMER_STATUS, valueProvider));
		accountData.setCallbackId(getValue(CALLBACK_ID, valueProvider));
		accountData.setCalledStationId(getValue(CALLED_STATION_ID, valueProvider));
		accountData.setCallingStationId(getValue(CALLING_STATION_ID, valueProvider));
		accountData.setCheckItem(getValue(CUSTOMER_CHECK_ITEMS, valueProvider));
		accountData.setConcurrentLoginPolicy(getValue(LOGIN_POLICY, valueProvider));
		accountData.setCUI(getValue(CUI, valueProvider));
		accountData.setCustomerServices(getValue(CUSTOMER_SERVICES, valueProvider));
		accountData.setCustomerType(getValue(CUSTOMER_TYPE, valueProvider));
		accountData.setEmailId(getValue(EMAIL, valueProvider));
		accountData.setGracePolicy(getValue(GRACE_PERIOD, valueProvider));
		accountData.setEncryptionType(getValue(ENCRYPTION_TYPE, valueProvider));
		accountData.setHotlinePolicy(getValue(HOTLINE_POLICY, valueProvider));
		accountData.setIPPoolName(getValue(IP_POOL, valueProvider));
		accountData.setParam1(getValue(PARAM1, valueProvider));
		accountData.setParam2(getValue(PARAM2, valueProvider));
		accountData.setParam3(getValue(PARAM3, valueProvider));
		accountData.setParam4(getValue(PARAM4, valueProvider));
		accountData.setParam5(getValue(PARAM5, valueProvider));
		accountData.setPassword(getValue(USER_PASSWORD, valueProvider));
		accountData.setPasswordCheck(getValue(PASSWORD_CHECK, valueProvider));
		accountData.setRejectItem(getValue(CUSTOMER_REJECT_ITEMS, valueProvider));
		accountData.setReplyItem(getValue(CUSTOMER_REPLY_ITEMS, valueProvider));
		accountData.setImsi(getValue(IMSI, valueProvider));
		accountData.setMeid(getValue(MEID, valueProvider));
		accountData.setMsisdn(getValue(MSISDN, valueProvider));
		accountData.setMdn(getValue(MDN, valueProvider));
		accountData.setImei(getValue(IMEI, valueProvider));
		accountData.setDeviceVendor(getValue(DEVICE_VENDOR, valueProvider));
		accountData.setDeviceName(getValue(DEVICE_NAME, valueProvider));
		accountData.setDeviceVLAN(getValue(DEVICE_VLAN, valueProvider));
		accountData.setDevicePort(getValue(DEVICE_PORT, valueProvider));
		accountData.setGeoLocation(getValue(GEO_LOCATION, valueProvider));
		accountData.setGroupName(getValue(GROUP_NAME, valueProvider));
		accountData.setAdditionalPolicy(getValue(ADDITIONAL_POLICY, valueProvider));

		String value = getValue(MAC_VALIDATION, valueProvider);
		if(value!=null && value.length()>0){
			accountData.setMacValidation(Boolean.parseBoolean(value));
		}

		if((CREDIT_LIMIT != null)){
			accountData.setCreditLimitCheckRequired(true);
			value = getValue(CREDIT_LIMIT, valueProvider);
			if(value!=null&& value.length()>0){

				try{
					long creditLimit = Long.parseLong(value);
					accountData.setCreditLimit(creditLimit);

				}catch (NumberFormatException e) {
					LogManager.getLogger().warn(getName(), "Invalid credit limit for user: " + accountData.getUserName() + 
							". Using default value 0.");
				}
			}	
		}
		if((EXPIRY_DATE!=null)){
			Date expiryDate = valueProvider.getDateValue(EXPIRY_DATE);
			accountData.setExpiryDate(expiryDate);
		}else{
			accountData.setExpiryDateCheckRequired(false);
		}
		return accountData;
	}

	private String getValue(String fieldMapping, AccountDataValueProvider valueProvider){
		if(fieldMapping== null || fieldMapping.trim().length() == 0)
			return null;
		return valueProvider.getStringValue(fieldMapping);
	}

	private class DiaDBAccountDataValueProvider implements AccountDataValueProvider{

		ResultSet resultSet;
		public DiaDBAccountDataValueProvider(ResultSet resultSet){
			this.resultSet = resultSet;
		}

		@Override
		public String getStringValue(String fieldName) {
			String strValue = "";
			try {
				strValue = resultSet.getString(fieldName);
			} catch (SQLException e) {
				LogManager.getLogger().warn(getName(), "Could not fetch String value for fieldname: " + fieldName);
			}
			return strValue;
		}

		@Override
		public Date getDateValue(String fieldName) {
			Date date = null;
			try{
				date = resultSet.getTimestamp(fieldName);
			} catch (SQLException e){
				LogManager.getLogger().warn(getName(), "Could not fetch Date value for fieldname: " + fieldName );
			} catch (IllegalArgumentException e){
				LogManager.getLogger().warn(getName(), "Could not fetch Date value for fieldname: " + fieldName );
			}
			return date;
		}

	}

}
