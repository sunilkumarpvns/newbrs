package com.elitecore.aaa.core.drivers;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.UserFileAccountData;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public abstract class UserFileAuthDriver extends BaseAuthDriver {
	private static final String MODULE = "USERSFILE AUTHDRIVER";
	protected List<UserFileAccountData> driverUserAccounts = null;
	private static final long defaultCreditLimit = -1;
	public UserFileAuthDriver(AAAServerContext serverContext){
		super(serverContext);
		driverUserAccounts = new ArrayList<UserFileAccountData>();
	}

	@Override
	protected AccountData fetchAccountData(ServiceRequest serviceRequest, List<String> userIdentities,ChangeCaseStrategy caseStrategy, boolean bTrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator) throws DriverProcessFailedException {
		AccountData accountData=null;
		LogManager.getLogger().info(MODULE, "Trying to fetch user profile using driver: " + getName());
		if(driverUserAccounts!=null && driverUserAccounts.size()>0){
			int size = driverUserAccounts.size();
			UserFileAccountData userFileAccountData;
			for(int i=0;i<size;i++){
				userFileAccountData = driverUserAccounts.get(i);
				if(userFileAccountData.getExpression() != null){
					if(matchAuthAttributeInRequest(serviceRequest, userFileAccountData.getExpression())) {
						int userIdentitiesSize = userIdentities.size();
						String identityAttrValue;
						String strUnstrippedUserIdentity;
						for(int j=0;j<userIdentitiesSize;j++){
							identityAttrValue =  getValueForIdentityAttribute(serviceRequest, userIdentities.get(j));
							if(identityAttrValue!=null){
								if (bTrimUserIdentity) {
									identityAttrValue = identityAttrValue.trim();
								}
								
								identityAttrValue = caseStrategy.apply(identityAttrValue);
								strUnstrippedUserIdentity = identityAttrValue;
								identityAttrValue = stripStrategy.apply(identityAttrValue, realmSeparator);
								accountData = userFileAccountData.getAccountData();
								accountData.setUserIdentity(identityAttrValue);
								serviceRequest.setParameter(AAAServerConstants.CUI_KEY, identityAttrValue);
								serviceRequest.setParameter(AAAServerConstants.UNSTRIPPED_CUI, strUnstrippedUserIdentity);
								AccountData newAccountData=accountData;
								try {
									newAccountData=accountData.clone();
									setDynAccountPasswordFromAttrId(newAccountData, serviceRequest);
								} catch (CloneNotSupportedException e) {
									if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
										LogManager.getLogger().error(MODULE, "System Error : Problem in cloning accountData in driver [" + getName() + "] Reason : "+e.getMessage());
								}
								return newAccountData;
							}
						}
					}
				}
			}
		}

		if(accountData == null)
			LogManager.getLogger().debug(MODULE, "User Identity not found using driver: " + getName());
		return accountData;
	}

	private boolean matchAuthAttributeInRequest(final ServiceRequest serviceRequest, LogicalExpression expression) {

		ValueProvider valueProvider = new ValueProvider() {					
			@Override
			public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
				return getValueForIdentityAttribute(serviceRequest, identifier);
			}
			
			@Override
			public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
				return 0;
			}

			@Override
			public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
				String value = getStringValue(identifier);
				
				if(value!=null){
					List<String> stringValues=new ArrayList<String>(1);	
					stringValues.add(value);
					return stringValues;
				}else
					throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			}

			@Override
			public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
				return new ArrayList<Long>(0);
			}

			@Override
			public Object getValue(String key) {
				return null;
			}
		};				
		return expression.evaluate(valueProvider);
	}


	protected abstract String getValueForIdentityAttribute(ServiceRequest serviceRequest,String identityAttribute);

	protected CacheDetail readAllFiles(String[] userFileLocations)throws DriverInitializationFailedException{
		ArrayList<UserFileAccountData> tempDriverUserAccounts  = new ArrayList<UserFileAccountData>();
		boolean isSuccess = false;
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(MODULE);
		cacheDetail.setResultCode(CacheConstants.SUCCESS);
		cacheDetail.setSource(StringUtility.getCommaSeparated(userFileLocations));
		String[] locations = userFileLocations;
		for(int i=0;i<locations.length;i++) {
			File userFile = new File(getServerHome() + File.separator + locations[i]);
			if(userFile.exists()) {
				if(userFile.isDirectory()) {
					String[] userFileList = userFile.list();
					for(int j=0;j<userFileList.length;j++) {
						if(userFileList[j].endsWith("xml")) {
							File file = new File(getServerHome() + File.separator + locations[i] + File.separator + userFileList[j]);			
							try {
								readUserFile(file,tempDriverUserAccounts);
								isSuccess = true;
							} catch (Exception e) {
								cacheDetail.setResultCode(CacheConstants.INTRIM );
							}
						}
					}
				}else
					if(locations[i].endsWith("xml"))
						try {
							readUserFile(userFile,tempDriverUserAccounts);
							isSuccess = true;
						} catch (Exception e) {
							cacheDetail.setResultCode(CacheConstants.INTRIM );
						}
			}else{
				LogManager.getLogger().warn(MODULE, userFile.getName() + ":No Such File or Directory Found");
				cacheDetail.setResultCode(CacheConstants.INTRIM );
			}
		}
		if(!isSuccess){
			cacheDetail.setResultCode(CacheConstants.FAIL);
		}
		this.driverUserAccounts = tempDriverUserAccounts;
		return cacheDetail;	
	}
	private void readUserFile(File userFile,ArrayList<UserFileAccountData>tempDriverUserAccounts) throws Exception{
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document document = docBuilder.parse(userFile);

			NodeList userAccountList = document.getElementsByTagName("user-accounts").item(0).getChildNodes();
			for(int i=0; i<userAccountList.getLength(); i++){
				Node userAccount = userAccountList.item(i);
				AccountData accountData = new AccountData();
				UserFileAccountData userAccountData = new UserFileAccountData();
				if(userAccount.getNodeName().equalsIgnoreCase("user-account")){
					NodeList accountInfo = userAccount.getChildNodes();
					for(int j=0; j<accountInfo.getLength(); j++){
						Node tempNode = accountInfo.item(j);
						if(tempNode.getNodeName().equals("authentication-attributes")){
							NodeList authAttributesList = tempNode.getChildNodes();
							for(int k=0;k<authAttributesList.getLength();k++)
							{
								String authAttributeName = authAttributesList.item(k).getNodeName();
								if(authAttributeName.equals("radius-attribute"))
								{
									NamedNodeMap attrIDValueMap = authAttributesList.item(k).getAttributes();
									String strAttributeId = attrIDValueMap.getNamedItem("id").getTextContent();
									String strAttributeValue = attrIDValueMap.getNamedItem("value").getTextContent();
									try{
										userAccountData.addAuthAttributes(strAttributeId,strAttributeValue);
									}catch(InvalidExpressionException ex){
										LogManager.getLogger().warn(MODULE, "Invalid Authentication Attribute expression in: " + userFile.getName() + 
												". Ignoring this attribute.");
									}
								}
							}
						}else if(tempNode.getNodeName().equals("user-password")){
							NodeList userPassword = tempNode.getChildNodes();
							Map<String,String> userPasswordInfoMap = new HashMap<String,String>();								
							for(int l=0;l<userPassword.getLength();l++){
								Node temNode = userPassword.item(l);
								if(temNode.getNodeName().equals("password-check-enabled") && temNode.getTextContent().trim().length()>0){
									userPasswordInfoMap.put("USER_PASSWORD_CHECK_ENABLED", temNode.getTextContent());
								}else if(temNode.getNodeName().equals("password") && temNode.getTextContent().trim().length()>0){
									userPasswordInfoMap.put("USER_PASSWORD", temNode.getTextContent());
								}else if(temNode.getNodeName().equals("encryption-type")){
									userPasswordInfoMap.put("USER_PASSWORD_ENCRYPTION_TYPE", temNode.getTextContent());
								}
							}
							accountData.setPassword(userPasswordInfoMap.get("USER_PASSWORD"));
							accountData.setPasswordCheck(userPasswordInfoMap.get("USER_PASSWORD_CHECK_ENABLED"));
							accountData.setEncryptionType(userPasswordInfoMap.get("USER_PASSWORD_ENCRYPTION_TYPE"));
						}else if(tempNode.getNodeName().equals("customer-service")){
							accountData.setCustomerServices(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("customer-type")){
							accountData.setCustomerType(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("service-type")){
							accountData.setServiceType(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("calling-station-id")){
							accountData.setCallingStationId(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("called-station-id")){
							accountData.setCalledStationId(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("email-id")){
							accountData.setEmailId(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("user-group")){
							accountData.setGroupName(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("user-check-item")){
							accountData.setCheckItem(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("user-reject-item")){
							accountData.setRejectItem(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("user-reply-item")){
							accountData.setReplyItem(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("access-policy")){
							accountData.setAccessPolicy(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("ip-pool-name")){
							accountData.setIPPoolName(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("CUI")){
							accountData.setCUI(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("param1")){
							accountData.setParam1(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("param2")){
							accountData.setParam2(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("param3")){
							accountData.setParam3(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("param4") && tempNode.getTextContent().trim().length()>0){
							accountData.setParam4(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("param5") && tempNode.getTextContent().trim().length()>0){
							accountData.setParam5(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("hotline-policy")){
							accountData.setHotlinePolicy(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("grace-policy")){
							accountData.setGracePolicy(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("concurrent-login-policy")){
							accountData.setConcurrentLoginPolicy(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("user-name")){
							accountData.setUserName(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("callback-id")){
							accountData.setCallbackId(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("account-status")){
							accountData.setAccountStatus(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("credit-limit") && tempNode.getTextContent().trim().length()>0){
							try {
								accountData.setCreditLimit((long)Double.parseDouble(tempNode.getTextContent()));
							}catch (NumberFormatException e) {
								LogManager.getLogger().warn(MODULE, "Credit Limit is Not Configured Properly.");
								accountData.setCreditLimit(defaultCreditLimit);
							}
							accountData.setCreditLimitCheckRequired(true);
						}else if(tempNode.getNodeName().equals("device-port") && tempNode.getTextContent().trim().length()>0){
							accountData.setDevicePort(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("authorization-policy")){
							accountData.setAuthorizationPolicy(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("additional-policy")){
							accountData.setAdditionalPolicy(tempNode.getTextContent());
						}else if(tempNode.getNodeName().equals("mac-validation")){
							accountData.setMacValidation(Boolean.parseBoolean(tempNode.getTextContent()));
						}else if(tempNode.getNodeName().equals("expiry-date")){
							String expiryDate = tempNode.getTextContent();
							SimpleDateFormat[] expiryPatterns = getexpiryPatterns();
							Date date;
							if(expiryDate != null && expiryDate.length() > 0) {
								for(int k=0;k<expiryPatterns.length;k++) {
									try {
										date = expiryPatterns[k].parse(expiryDate);
										accountData.setExpiryDate(date);
										break;
									} catch (ParseException e) {
										LogManager.getLogger().warn(MODULE, "Invalid ExpiryDate Configured in User Account.");
									}
								}
							}
							else
								LogManager.getLogger().warn(MODULE, "expiry date is not configured");
						}else if("imsi".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setImsi(tempNode.getTextContent());
						}else if("meid".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setMeid(tempNode.getTextContent());
						}else if("msisdn".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setMsisdn(tempNode.getTextContent());
						}else if("mdn".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setMdn(tempNode.getTextContent());
						}else if("imei".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setImei(tempNode.getTextContent());
						}else if("device-vendor".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setDeviceVendor(tempNode.getTextContent());
						}else if("device-name".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setDeviceName(tempNode.getTextContent());
						}else if("device-vlan".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setDeviceVLAN(tempNode.getTextContent());
						}else if("geo-location".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setGeoLocation(tempNode.getTextContent());
						}else if("framedIPV4Address".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setFramedIPv4Address(tempNode.getTextContent());
						}else if("framedIPV6Prefix".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setFramedIPv6Prefix(tempNode.getTextContent());
						}else if("framedPool".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length()>0){
							accountData.setFramedPool(tempNode.getTextContent());
						}else if("authorization-policy-group".equalsIgnoreCase(tempNode.getNodeName()) && tempNode.getTextContent().trim().length() > 0){
							accountData.setAuthorizationPolicyGroup(tempNode.getTextContent());
						}
					}
					userAccountData.setAccountData(accountData);
					tempDriverUserAccounts.add(userAccountData);
				}
			}
		}
		catch(Exception e) {
			LogManager.getLogger().error(MODULE, "Error In Reading UserFile, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
			throw e;
		}
	}

	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}

	@Override
	public void scan() {
		//No need of any implementation
	}
	@Override
	public void reInit() throws InitializationFailedException {
		readAllFiles(getUserFileLocations());		
	}
	
	public abstract SimpleDateFormat[] getexpiryPatterns();
	
	public abstract String[] getUserFileLocations();
}
