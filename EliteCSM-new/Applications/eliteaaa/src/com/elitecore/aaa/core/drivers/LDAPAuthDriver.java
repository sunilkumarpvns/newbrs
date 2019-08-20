package com.elitecore.aaa.core.drivers;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.TransientFailureException;
import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.diameterapi.core.common.util.MappingParser;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchResults;

public abstract class LDAPAuthDriver extends BaseAuthDriver {
	private static final boolean isSpaceSupportedInReplyItem = Boolean.parseBoolean(System.getProperty(com.elitecore.core.commons.util.constants.CommonConstants.POLICY_OLD_REPLY_ITEM));
	private static String MODULE = "LDAPAuth Driver";	
	private AtomicLong ldapMaxQueryTimeoutCount;
	
	public LDAPAuthDriver(AAAServerContext serverContext){
		super(serverContext);
		ldapMaxQueryTimeoutCount = new AtomicLong(0);
	}
	@Override
	protected void initInternal() throws DriverInitializationFailedException{
		super.initInternal();
		initDataSource(true);	
	}
	
	/*
	 * Configuration fault is treated as permanent and ldap connectivity issue is treated
	 * as temporary
	 */
	private void initDataSource(boolean checkForAllIp) throws TransientFailureException, 
	DriverInitializationFailedException {
		LDAPDataSource ldapDataSource = getLDAPDataSource();
		if (ldapDataSource == null) {
			throw new DriverInitializationFailedException("Failed Intializing LDAP datasource reason: LDAP Data Source is null for DS Id: " + getLdapDsId());
		}
		try {
			LDAPConnectionManager connectionManager = 
				LDAPConnectionManager.getInstance(ldapDataSource.getDataSourceName());
			connectionManager.createLDAPConnectionPool(ldapDataSource, checkForAllIp);				
		} catch(LDAPException e) {
			throw new TransientFailureException("Failed Intializing LDAP datasource: " 
					+ getLDAPDataSource(), e);
		}		
	}

	private String getRFC2254LDAPFilter(String strSearchString) {
		char ch;
		StringBuffer buffer = new StringBuffer(strSearchString.length() + 16);
		for(int cnt = 0; cnt < strSearchString.length() ; cnt++) {
			ch = strSearchString.charAt(cnt);
			if(ch == '\\' || ch == '*' || ch == '(' || ch == ')' || ch == '\0' || !(ch >= 0x20 && ch <= 0x7E)) {
				buffer.append('\\');
				buffer.append(Integer.toHexString(ch));
			}else {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}
	

	@SuppressWarnings("unchecked")
	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest,ChangeCaseStrategy caseStrategy, boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String userIdentity) throws DriverProcessFailedException {
		
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
		
		String strUserDN = "";
		String strSearchString = "";

		LogManager.getLogger().info(MODULE,"Fetch Data for user identity: " + userIdentity);
		LDAPConnection  ldapConnection = null; 
		AccountData accountData = null;				
		ArrayList<String> ldapEntryAttrs = null;
		LDAPSearchResults resultSet = null;
		
		try{			
			ldapConnection = LDAPConnectionManager.getInstance(getDSName()).getConnection();
			if (ldapConnection == null){
				LogManager.getLogger().warn(MODULE, "Connection to LDAP failed for Driver: " + getName() + " while fetching Account Data.");
				throw new DriverProcessFailedException("Connection to LDAP Failed.");
			}
			strUserDN = getUserDN();
			
			String strLDAPFilter;
			if(getSearchFilter() != null){
				strLDAPFilter = getRFC2254LDAPFilter(userIdentity) + ")" + getSearchFilter();
				strSearchString = "(&(" + strUserDN + strLDAPFilter + ")";
			}else{
				 strLDAPFilter = getRFC2254LDAPFilter(userIdentity);
			strSearchString = "(" + strUserDN + strLDAPFilter + ")";  // eg. uid=ambesh
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "UserDN: " + strUserDN);
				LogManager.getLogger().debug(MODULE, "LDAP Search Filter: " + strLDAPFilter);
				LogManager.getLogger().debug(MODULE, "Search String: " + strSearchString);
			}
			ArrayList<String> strDnDetails = getDnDetailList();
			ldapEntryAttrs = getLdapEntryAttributes();			 
			String[] strLdapEntryAttributes = ldapEntryAttrs.toArray(new String[ldapEntryAttrs.size()]);
			int dnCount = strDnDetails.size();			
						
			for(int i = 0; i < dnCount ; i++){
				try{
					resultSet = ldapConnection.search(strDnDetails.get(i), getSearchScope() , strSearchString, strLdapEntryAttributes, false);
					if(resultSet != null && resultSet.hasMoreElements()){
						break;
					}
				} catch (LDAPException e) {
					if(e.getLDAPResultCode() == LDAPException.CONNECT_ERROR){
						markDead();
						getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LDAPDOWN, MODULE, 
								"LDAP with Data Source name "+getDSName()+" is Down, System marked as dead. Reason: " + e.getMessage(), 0,
								"LDAP with Data Source name "+getDSName()+" is Down, System marked as dead");
						LogManager.getLogger().warn(MODULE,"LDAP with Data Source name "+getDSName()+" is Down, System marked as dead. Reason: " + e.getMessage());
						throw new DriverProcessFailedException(e);
					}else if(e.getLDAPResultCode() == LDAPException.LDAP_TIMEOUT){
						ldapMaxQueryTimeoutCount.incrementAndGet();
						if(ldapMaxQueryTimeoutCount.get() >= getMaxQueryTimeoutCount()) {
							LogManager.getLogger().warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts,so System marked as DEAD");
							markDead();
						}
						LogManager.getLogger().warn(MODULE, "LDAP with datasource name " + getDSName()+ " query Timeout. " );
						getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.LDAP_GENERIC, 
								MODULE, "LDAP query TimeOut.", 0, "LDAP query TimeOut.");
						throw new DriverProcessFailedException(e);
					} else if (e.getLDAPResultCode() == LDAPException.PARAM_ERROR) {
						LogManager.getLogger().warn(MODULE, "LDAP param error, Reason: " +e.getMessage()
								+ ". SearchFilter: " + strSearchString);
					}else{
						LogManager.getLogger().warn(MODULE, "UnKnown LDAP error,Reason: " +e.getMessage());
						markDead();
						getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LDAPDOWN, MODULE, 
								"UnKnown LDAP Error : " + e.getLDAPResultCode(),0,
								"UnKnown LDAP Error : " + e.getLDAPResultCode());
						throw new DriverProcessFailedException(e);
					}
				}				
			}
			if(resultSet != null && resultSet.hasMoreElements()){			
				
				serviceRequest.setParameter(AAAServerConstants.CUI_KEY, userIdentity);
				serviceRequest.setParameter(AAAServerConstants.UNSTRIPPED_CUI, strUnstrippedUserIdentityValue);
				
				LDAPEntry myEntry = resultSet.next();
				serviceRequest.setParameter(AAAServerConstants.DN, myEntry.getDN());
				LDAPAttributeSet entryAttrs = myEntry.getAttributeSet();
				Enumeration<LDAPAttribute> attrsInSet = (Enumeration<LDAPAttribute>)entryAttrs.getAttributes();
				accountData = buildAccountData(serviceRequest,getAccountDataValueProvider(attrsInSet),getAccountDataFieldMapping());
				
				if (accountData != null){
					accountData.setUserIdentity(userIdentity);					
				}
				ldapMaxQueryTimeoutCount.set(0);	
			}
		}catch(LDAPException e){
			//LDAPException.LDAP_TIMEOUT
			//LDAPException.SERVER_DOWN
			//LDAPException.TIME_LIMIT_EXCEEDED		
			if(e.getLDAPResultCode() == LDAPException.LDAP_TIMEOUT || 
				e.getLDAPResultCode() == LDAPException.SERVER_DOWN ||
				e.getLDAPResultCode() == LDAPException.TIME_LIMIT_EXCEEDED ||
				e.getLDAPResultCode() == LDAPException.CONNECT_ERROR){
				markDead();
				getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LDAPDOWN,MODULE,
						"No Connection is Available from DataSource : " +getDSName(),0,
						"No Connection is Available from DataSource : " +getDSName());
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "LDAP Driver " + getName() +" marked as DEAD. Reason: " + e.getMessage());
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Unknown Error occured connecting LDAP Driver " + getName() +" marked as DEAD. Reason: " + e.getMessage());
				markDead();
				getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LDAPDOWN,MODULE,
						"Unknown Error occured connecting LDAP Driver " + getName(),0,
						"Unknown Error occured connecting LDAP Driver " + getName());
			}
			throw new DriverProcessFailedException(e);
		} 		
		
		finally{			
			if(ldapConnection != null)								
				LDAPConnectionManager.getInstance(getDSName()).closeConnection(ldapConnection);				
		}
		return accountData;
	}

	private LdapAccountDataValueProvider getAccountDataValueProvider(Enumeration<LDAPAttribute> attrsInSet) {
		
		Map<String, String[]> valueMap = new HashMap<String, String[]>();
		
		LDAPAttribute nextAttr =null;
		String attrName =null;
		String[] valueArray = null;
		
		while (attrsInSet.hasMoreElements()) {
			nextAttr = attrsInSet.nextElement();
			attrName = nextAttr.getName();
			valueArray = nextAttr.getStringValueArray();
			if(valueArray!=null && valueArray.length>0){
				valueMap.put(attrName, valueArray);
			}
			
		}
		return new LdapAccountDataValueProvider(valueMap);
	}
	@Override
	protected int getStatusCheckDuration() {
		return getStatusChkDuration();
	}
	
	@Override
	public void scan() {
		try {			
			initDataSource(false);
			LDAPConnection  ldapConnection = LDAPConnectionManager.getInstance(getDSName()).getConnection();
			if(ldapConnection != null){
				markAlive();
			}else{
				markDead();
			}
		} catch (DriverInitializationFailedException e) {
			markDead();
		} catch (LDAPException e) {
			markDead();
		}
		
	}
	
	public class LdapAccountDataValueProvider implements AccountDataValueProvider{
		public Map<String, String[]> valueMap;
		public LdapAccountDataValueProvider(Map<String, String[]> valueMap){
			this.valueMap = valueMap;
		}

		@Override
		public String getStringValue(String fieldName) {
			return null;
		}

		@Override
		public Date getDateValue(String fieldName) {
			return null;
		}
	}

	@Override
	protected String[] getValues(List<DataFieldMapping> fieldMapping,AccountDataValueProvider valueProvider){
		if(fieldMapping== null || !(fieldMapping.size()>0))
			return null;
		DataFieldMapping tempMapping = fieldMapping.get(0);
		String[] values = ((LdapAccountDataValueProvider)valueProvider).valueMap.get(tempMapping.getFieldName());
		if(values!=null && values.length>0){
			int size = values.length;
			String[] resultArray=new String[size];
			String tempString;
			int j=0;
			for(int i=0;i<size;i++){
				tempString = tempMapping.getValue(values[i]);
				if(tempString!=null){
					resultArray[j] = tempString;
					j = j+1;
				}
			}
			if(j==0)
				resultArray = null;
			return resultArray;
		}else {
			String defaultValue =tempMapping.getDefaultValue();
			if(defaultValue!=null)
				return new String[]{defaultValue};
			else {
				return null;
			}
		}

	}
	
	@Override
	protected String[] getValues(List<DataFieldMapping> fieldMapping,AccountDataValueProvider valueProvider,String logicalName){
		if(AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS.equals(logicalName)){
			if(fieldMapping== null || !(fieldMapping.size()>0))
				return null;
			DataFieldMapping tempMapping = fieldMapping.get(0);
			String[] values = ((LdapAccountDataValueProvider)valueProvider).valueMap.get(tempMapping.getFieldName());
			if(values!=null && values.length>0){
				int size = values.length;
				String[] resultArray=new String[size];
				String tempString;
				int j=0;
				for(int i=0;i<size;i++){
					tempString = tempMapping.getValue(values[i]);
					if(tempString!=null){
						if(isSpaceSupportedInReplyItem  && tempString.trim().length()>0){
							// This change is related to ELITEAAA-1948
							if(tempString.contains(" "))
								tempString = getSpaceSupportedString(tempString);
						}
						resultArray[j] = tempString;
						j = j+1;
					}
				}
				if(j==0)
					resultArray = null;
				return resultArray;
			}else {
				String defaultValue =tempMapping.getDefaultValue();
				if(defaultValue!=null)
					return new String[]{defaultValue};
				else {
					return null;
				}
			}
		}else {
			return getValues(fieldMapping, valueProvider);
		}
		
	}
	
	@Override
	protected Date getExpiryDate(List<DataFieldMapping> fieldMapping,AccountDataValueProvider valueProvider) {
		String[] strValues = getValues(fieldMapping, valueProvider);
		if(strValues!=null && strValues.length>0){
			Date expiryDate = null;
			SimpleDateFormat[] expiryDatePatterns = getExpiryDatePatterns();
			int size = strValues.length;
			String tempValue;
			for(int i=0;i<size;i++) {				
				tempValue = strValues[i];
					for(int j=0;j<expiryDatePatterns.length;j++){
						try{											
							expiryDate = expiryDatePatterns[i].parse(tempValue);
							return expiryDate;
						}catch (ParseException e) {
							LogManager.getLogger().debug(MODULE, "Invalid ExpiryDate Configured in User Account.");
						}								
					}									
			}
		}	
		return null;
	}
	
	private String getSpaceSupportedString(String strReplyItem) {
		StringReader stringReader = new StringReader(strReplyItem);
		StringBuilder finalReplyItem = new StringBuilder();
		int currentChar ;
		try {
			currentChar = stringReader.read();
			while(currentChar != MappingParser.END_OF_STRING){
				finalReplyItem.append((char)currentChar);
				if((char)currentChar=='='){
					finalReplyItem.append(readValue(stringReader));
				}
				currentChar = stringReader.read();
			}
		} catch (IOException e1) {
			return strReplyItem;
		}
		return finalReplyItem.toString();
	}
	private String readValue(StringReader stringReader) throws IOException{
		int currentChar  = stringReader.read();
		while(currentChar!=MappingParser.END_OF_STRING && (char)currentChar==' '){
			currentChar = stringReader.read();
		}
		
		StringBuilder value = new StringBuilder();
		boolean isLiteral = false;
		if(currentChar!=MappingParser.END_OF_STRING){
			if((char)currentChar=='"'){
				isLiteral = true;
			}
			value.append((char)currentChar);
			currentChar = stringReader.read();
			while((char)currentChar != ',' && currentChar != MappingParser.END_OF_STRING){
				if((char)currentChar=='\\')
					currentChar = stringReader.read();
				if(currentChar!=MappingParser.END_OF_STRING){
					value.append((char)currentChar);
					currentChar = stringReader.read();	
				}
			}
		}
		
		String finalValue = value.toString();
		if(!isLiteral){
			finalValue ="\""+finalValue+"\"";
		}
		if((char)currentChar==',')
			finalValue =finalValue+(char)currentChar;
		return finalValue;
	}
	
	@Override
	protected boolean checkForFallback() {
		getTaskScheduler().scheduleSingleExecutionTask(new SingleExecutionAsyncTask() {

			@Override
			public TimeUnit getTimeUnit() {
				return TimeUnit.SECONDS;
			}

			@Override
			public long getInitialDelay() {
				return 0;
			}

			@Override
			public void execute(AsyncTaskContext context) {
				try {
					initDataSource(true);
				} catch (DriverInitializationFailedException e) {
					LogManager.getLogger().error(MODULE, e.getMessage());
					LogManager.getLogger().trace(e);
				}
			}
		});
		return false;
	}
		
	@Override
	public void generateUpAlert() {
		getServerContext().generateSystemAlert(AlertSeverity.CLEAR,Alerts.LDAPUP,MODULE, 
				"Connection is UP for Driver: " + getName() + " (" +getDSName() + ")", 0,
				getName() + " (" +getDSName() + ")");
		
		LogManager.getLogger().debug(MODULE, "LDAP Driver: " + getName()+ " is alive now");
	}
	
	@Override
	public void generateDownAlert() {
		getServerContext().generateSystemAlert(AlertSeverity.CRITICAL,Alerts.LDAPDOWN,MODULE, 
				"Connection is Down for Driver: " + getName() + " (" +getDSName() + ")", 0,
				"Connection is Down for Driver: " + getName() + " (" +getDSName() + ")");
		LogManager.getLogger().warn(MODULE, "LDAP Driver: " + getName()+ " is dead marked");
	}
		
	protected abstract int getStatusChkDuration();
	protected abstract String getDSName();
	protected abstract AccountDataFieldMapping getAccountDataFieldMapping();
	protected abstract ArrayList<String> getLdapEntryAttributes();
	protected abstract String getUserDN();
	protected abstract ArrayList<String> getDnDetailList();
	protected abstract SimpleDateFormat[] getExpiryDatePatterns();
	protected abstract String getLdapDsId();
	protected abstract long getMaxQueryTimeoutCount();
	protected abstract List<String> getUserIdentityAttributes();
	protected abstract int getSearchScope();
	protected abstract String getSearchFilter();
	protected abstract LDAPDataSource getLDAPDataSource();
}
