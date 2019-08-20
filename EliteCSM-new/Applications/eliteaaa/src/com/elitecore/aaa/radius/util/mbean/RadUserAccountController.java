package com.elitecore.aaa.radius.util.mbean;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.coreradius.commons.attributes.AttributeTypeNotFoundException;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.util.mbean.data.MBeanConstants;

public class RadUserAccountController extends BaseMBeanController  implements RadUserAccountControllerMBean{

	private static final String MODULE = "";
	HashMap<String, File> fileInstances = new HashMap<String, File>();
	HashMap<String, Map<String,Map<String,Map<String,String>>>> userAccounts = new HashMap<String, Map<String,Map<String,Map<String,String>>>>();
	private AAAServerContext aaaServerContext;

	public RadUserAccountController(AAAServerContext aaaServerContext){
		this.aaaServerContext = aaaServerContext;
	}
	
	private File[] getUsersFiles(){
		File file = new File(getServerContext().getServerHome() + File.separator + "data" + File.separator + "userfiles");
		if(file.isDirectory()){
			return file.listFiles(new FilenameFilter(){
				public boolean accept(File directory, String fileName){
					return fileName.endsWith("xml");
				}
			});
		}
		return null;
	}

	public void readAllEntities(){

		File[] files = getUsersFiles();
	
		if(files!=null){
			for(int i=0;i<files.length;i++){
				fileInstances.put(files[i].getName(), files[i]);
				try{
					userAccounts.put(files[i].getName(),fetchUserAccountsForManager(files[i]));
				}catch(Exception e){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "No User-Accounts cached from file : "+files[1].getName()+",Reason : "+e.getMessage());
				}			
			}
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "No UserFiles found at location : "+getServerContext().getServerHome() + File.separator + "data" + File.separator + "userfiles"+" , so no User-Accouts will be cached");
			
		}	
		
	}
	public Map<String,Map<String,Map<String,String>>> readEntities(String filename){
		return userAccounts.get(filename);
	}
	
	public Map<String,Map<String,String>> getAccount(String filename,String id){
		
		Map<String,Map<String,Map<String,String>>> filteredUserAccounts = this.userAccounts.get(filename);
		
		return filteredUserAccounts.get(id);
	}
	public int updateUserAccount(String filename,String id, Map userAccount){
		File usersFile = this.fileInstances.get(filename);
		int status=0;
		try {
			status=updateAccount(usersFile,id, userAccount);
		}catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}
	public int addUserAccount(String filename,Map userAccount){
		File usersFile = this.fileInstances.get(filename);
		Map<String,Map<String,Map<String,String>>> filteredUserAccounts = this.userAccounts.get(filename);
		int status=0;
		try{
			status=addAccount(usersFile,userAccount);
		}catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}
	public int deleteUserAccount(String filename,String id){
		File usersFile = this.fileInstances.get(filename);
		Map<String,Map<String,Map<String,String>>> filteredUserAccounts = this.userAccounts.get(filename);
		int status=0;
		try{
			status=deleteAccount(usersFile,id);
		}catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}
	
	public String[] readUserFiles(){
		File[] files = getUsersFiles();
		String[]filesName=new String[files.length];
		
		for(int i=0;i<files.length;i++){
			filesName[i]=files[i].getName();
		}
		return filesName;
	}

	public Map<String,Map<String,Map<String,String>>> fetchUserAccountsForManager(File usersFile){
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Caching user accounts from the file: "+usersFile.getName());
		
		Map<String,Map<String,Map<String,String>>> userAccounts = new LinkedHashMap<String,Map<String,Map<String,String>>>();
		try{	
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document document = docBuilder.parse(usersFile);
			
			NodeList userAccountList = document.getElementsByTagName("user-accounts").item(0).getChildNodes();
			for(int i=0; i<userAccountList.getLength(); i++){
				Node userAccount = userAccountList.item(i);
				if(userAccount.getNodeName().equals("user-account")){
					NamedNodeMap map = userAccount.getAttributes();
					Node id = map.getNamedItem("id");
					String strAccountId = id.getTextContent();
					Map<String,Map<String,String>> authAttrAcctDataMap =null;
					try{
					 authAttrAcctDataMap = getAuthAttributeAccountDataMap(userAccount.getChildNodes());
					 userAccounts.put(strAccountId, authAttrAcctDataMap);
					}catch(Exception e){
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Skipping the account " + strAccountId + ". Reason :" + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
					
				}
			}
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Problem reading users file: " + usersFile.getName(), e);
		}
		return userAccounts;
	}

	private Map<String,Map<String,String>> getAuthAttributeAccountDataMap(NodeList accountInfo) throws AttributeTypeNotFoundException{
		Map<String,Map<String,String>> attrAcctDataMap = new HashMap<String,Map<String,String>>();
		Map<String,String> accountInfoMap = new HashMap<String,String>();
		
		Map<String,String> authAttributesMap = null;
		for(int i=0; i<accountInfo.getLength(); i++){
			Node tempNode = accountInfo.item(i);
			if(tempNode.getNodeName().equals("authentication-attributes")){
				authAttributesMap = readAuthAttributes(tempNode.getChildNodes());
			}else if(tempNode.getNodeName().equals("user-password")){
				Map<String,String> userPasswordInfoMap = readPasswordInfo(tempNode.getChildNodes());
				accountInfoMap.put(MBeanConstants.USER_PASSWORD, String.valueOf(userPasswordInfoMap.get(MBeanConstants.USER_PASSWORD)));
				accountInfoMap.put(MBeanConstants.USER_PASSWORD_CHECK_ENABLED, String.valueOf(userPasswordInfoMap.get(MBeanConstants.USER_PASSWORD_CHECK_ENABLED)));
				accountInfoMap.put(MBeanConstants.USER_PASSWORD_ENCRYPTION_TYPE, String.valueOf(userPasswordInfoMap.get(MBeanConstants.USER_PASSWORD_ENCRYPTION_TYPE)));
			}else if(tempNode.getNodeName().equals("account-status")){
				accountInfoMap.put(MBeanConstants.ACCOUNT_STATUS, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("credit-limit")){
				accountInfoMap.put(MBeanConstants.CREDIT_LIMIT, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("expiry-date")){
				String expiryDate = tempNode.getTextContent();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				accountInfoMap.put(MBeanConstants.EXPIRY_DATE, expiryDate);
			}else if(tempNode.getNodeName().equals("customer-service")){
				accountInfoMap.put(MBeanConstants.CUSTOMER_SERVICE, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("customer-type")){
				accountInfoMap.put(MBeanConstants.CUSTOMER_TYPE, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("service-type")){
				accountInfoMap.put(MBeanConstants.SERVICE_TYPE, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("calling-station-id")){
				accountInfoMap.put(MBeanConstants.CALLING_STATION_ID, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("called-station-id")){
				accountInfoMap.put(MBeanConstants.CALLED_STATION_ID, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("email-id")){
				accountInfoMap.put(MBeanConstants.EMAIL_ID, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("user-group")){
				accountInfoMap.put(MBeanConstants.USER_GROUP, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("user-check-item")){
				accountInfoMap.put(MBeanConstants.USER_CHECK_ITEM, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("user-reject-item")){
				accountInfoMap.put(MBeanConstants.USER_REJECT_ITEM, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("user-reply-item")){
				accountInfoMap.put(MBeanConstants.USER_REPLY_ITEM, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("access-policy")){
				accountInfoMap.put(MBeanConstants.USER_ACCESS_POLICY, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("authorization-policy")){
				accountInfoMap.put(MBeanConstants.USER_RADIUS_POLICY, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("concurrent-login-policy")){
				accountInfoMap.put(MBeanConstants.USER_CONCURRENT_LOGIN_POLICY, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("ip-pool-name")){
				accountInfoMap.put(MBeanConstants.IP_POOL_NAME, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("CUI")){
				accountInfoMap.put(MBeanConstants.CUI, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("param1")){
				accountInfoMap.put(MBeanConstants.PARAM1, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("param2")){
				accountInfoMap.put(MBeanConstants.PARAM2, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("param3")){
				accountInfoMap.put(MBeanConstants.PARAM3, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("param4")){
				accountInfoMap.put(MBeanConstants.PARAM4, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("param5")){
				accountInfoMap.put(MBeanConstants.PARAM5, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("hotline-policy")){
				accountInfoMap.put(MBeanConstants.USER_HOTLINE_POLICY, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("grace-policy")){
				accountInfoMap.put(MBeanConstants.GRACEPOLICY, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("user-name")){
				accountInfoMap.put(MBeanConstants.USERNAME, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("callback-id")){
				accountInfoMap.put(MBeanConstants.CALLBACK_ID, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("mac-validation")){
				accountInfoMap.put(MBeanConstants.MACVALIDATION, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("group-bandwidth")){
				accountInfoMap.put(MBeanConstants.GROUPBANDWIDTH, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("imsi")){
				accountInfoMap.put(MBeanConstants.IMSI, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("meid")){
				accountInfoMap.put(MBeanConstants.MEID, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("msisdn")){
				accountInfoMap.put(MBeanConstants.MSISDN, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("mdn")){
				accountInfoMap.put(MBeanConstants.MDN, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("imei")){
				accountInfoMap.put(MBeanConstants.IMEI, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("device-vendor")){
				accountInfoMap.put(MBeanConstants.DEVICEVENDOR, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("device-name")){
				accountInfoMap.put(MBeanConstants.DEVICENAME, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("device-port")){
				accountInfoMap.put(MBeanConstants.DEVICEPORT, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("device-vlan")){
				accountInfoMap.put(MBeanConstants.DEVICEVLAN, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("geo-location")){
				accountInfoMap.put(MBeanConstants.GEOLOCATION, tempNode.getTextContent());
			} else if("framedIPV4Address".equalsIgnoreCase(tempNode.getNodeName())) {
				accountInfoMap.put(MBeanConstants.FRAMEDIPV4ADDRESS, tempNode.getTextContent());
			} else if("framedIPV6Prefix".equalsIgnoreCase(tempNode.getNodeName())){
			 	accountInfoMap.put(MBeanConstants.FRAMEDIPV6PREFIX, tempNode.getTextContent());
			} else if("framedPool".equalsIgnoreCase(tempNode.getNodeName())){
				accountInfoMap.put(MBeanConstants.FRAMEDPOOL, tempNode.getTextContent());
			} else if("authorization-policy-group".equalsIgnoreCase(tempNode.getNodeName())){
				accountInfoMap.put(MBeanConstants.AUTHORIZATIONPOLICYGROUP, tempNode.getTextContent());
			}
		}
		attrAcctDataMap.put(MBeanConstants.AUTH_ATTRIBUTES_MAP, authAttributesMap);
		attrAcctDataMap.put(MBeanConstants.ACCOUNT_INFO_MAP, accountInfoMap);
		return attrAcctDataMap;
	}

	private Map<String,String> readAuthAttributes(NodeList authAttributes) throws AttributeTypeNotFoundException{
		Map<String,String> authAttributesMap = new LinkedHashMap<String,String>();
		for(int i=0; i< authAttributes.getLength(); i++){
			Node tempNode = authAttributes.item(i);
			if(tempNode.getNodeName().equals("radius-attribute")){
				NamedNodeMap attrIDValueMap = tempNode.getAttributes();
				for(int j=0; j<attrIDValueMap.getLength(); j++){
					long lVendorId = 0;
					int iAttributeId = 0;
					String strAttributeId = attrIDValueMap.getNamedItem("id").getTextContent();
					if(strAttributeId.contains(":")){
						StringTokenizer strTokenizer = new StringTokenizer(strAttributeId, ":");
						lVendorId = Long.parseLong(strTokenizer.nextToken());
						iAttributeId = Integer.parseInt(strTokenizer.nextToken());
					}else{
						iAttributeId = Integer.parseInt(strAttributeId);

					}
					String strVendorAttribute = lVendorId+":"+iAttributeId;
					String strAttributeValue = attrIDValueMap.getNamedItem("value").getTextContent();
					if( Dictionary.getInstance().getKnownAttribute(strAttributeId) == null){
						throw new AttributeTypeNotFoundException("Invalid Standard Radius Attribute Id : "+strAttributeId);
					}
					authAttributesMap.put(strVendorAttribute, strAttributeValue);
				}
			}
		}
		return authAttributesMap;
	}
	
	private Map<String,String> readPasswordInfo(NodeList userPassword){
		Map<String,String> userPasswordInfoMap = new HashMap<String,String>();
		for(int i=0; i<userPassword.getLength(); i++){
			Node tempNode = userPassword.item(i);
			if(tempNode.getNodeName().equals("password-check-enabled")){
				userPasswordInfoMap.put(MBeanConstants.USER_PASSWORD_CHECK_ENABLED, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("password")){
				userPasswordInfoMap.put(MBeanConstants.USER_PASSWORD, tempNode.getTextContent());
			}else if(tempNode.getNodeName().equals("encryption-type")){
				userPasswordInfoMap.put(MBeanConstants.USER_PASSWORD_ENCRYPTION_TYPE, tempNode.getTextContent());
			}
		}
		return userPasswordInfoMap;
	}

	public AAAServerContext getServerContext() {
		return aaaServerContext;
	}
	public void setServerContext(AAAServerContext aaaServerContext) {
		this.aaaServerContext = aaaServerContext;
	}

	public int updateAccount(File usersFile,String id, Map infoMap){
		Map<String,Map<String,Map<String,String>>> filteredUserAccounts = this.userAccounts.get(usersFile.getName()); 
		HashMap tempMap = (HashMap)filteredUserAccounts.get(id);
		HashMap tempMap1 = new HashMap(tempMap);
		if(tempMap != null){
			Map authAttributeMap = (Map)infoMap.get(MBeanConstants.AUTH_ATTRIBUTES_MAP);
			HashMap accountInfoMap = (HashMap)infoMap.get(MBeanConstants.ACCOUNT_INFO_MAP);
			tempMap1.put(MBeanConstants.AUTH_ATTRIBUTES_MAP, authAttributeMap);
			tempMap1.put(MBeanConstants.ACCOUNT_INFO_MAP, accountInfoMap);
			filteredUserAccounts.put(id, tempMap1);
			int j = updateUsersFile(usersFile);
			
/*			if(j != -1){
				return j;
			}
			Logger.logDebug(MODULE, "Account Updation FAILED.");
			managerUserAccounts.put(id, tempMap);
*/		
			if(j < 0){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Update account failed for id=" + id);
				filteredUserAccounts.remove(String.valueOf(id));
			}
			return j;
		}
		return -1;
	}
	
	public int deleteAccount(File usersFile,String id){
		Map<String,Map<String,Map<String,String>>> filteredUserAccounts = this.userAccounts.get(usersFile.getName());
		Map<String,Map<String,String>> obj = filteredUserAccounts.remove(id);
		if(obj != null){
			int j = updateUsersFile(usersFile);
			if(j != -1){
				return j;
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Account deletion failed for id=" + id);
			filteredUserAccounts.put(id, obj);
		}
		return -1;
	}
	public int addAccount(File usersFile,Map infoMap){
		Map<String,Map<String,Map<String,String>>> filteredUserAccounts = this.userAccounts.get(usersFile.getName());
		if(filteredUserAccounts.size()<256){
			int id = generateIdForNewAccount(usersFile);
			Map authAttributeMap = (Map)infoMap.get(MBeanConstants.AUTH_ATTRIBUTES_MAP);
			HashMap accountInfoMap = (HashMap)infoMap.get(MBeanConstants.ACCOUNT_INFO_MAP);
			HashMap tempMap = new HashMap();
			tempMap.put(MBeanConstants.AUTH_ATTRIBUTES_MAP, authAttributeMap);
			tempMap.put(MBeanConstants.ACCOUNT_INFO_MAP, accountInfoMap);
			filteredUserAccounts.put(String.valueOf(id), tempMap);
			int j = updateUsersFile(usersFile);
			if(j < 0){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Add account failed.");
				filteredUserAccounts.remove(String.valueOf(id));
			}
			if(j >= 0){
				return id;
			}else{
				return j;
			}
		}
		return -1;
	}

	private int generateIdForNewAccount(File usersFile){
		Map<String,Map<String,Map<String,String>>> filteredUserAccounts = this.userAccounts.get(usersFile.getName());
		int i = -1;
		Random random = new Random();
		while(true){
			i = random.nextInt(256);
			if(!filteredUserAccounts.keySet().contains(String.valueOf(i))){
				return i;
			}
		}
	}

	public int updateUsersFile(File usersFile){
		try{			
			if(usersFile.canWrite()){
				FileOutputStream fos = new FileOutputStream(usersFile); //NOSONAR - Reason: Resources should be closed
				byte[] fileBytes = getBytesFromMap(usersFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(fileBytes);
				bos.close();
			}else{
				return -2;
			}
		}catch(IOException ioException){
			return -1;
		}
		return 1;
	}

	private byte[] getBytesFromMap(File usersFile){
		Map<String,Map<String,Map<String,String>>> filteredUserAccounts = this.userAccounts.get(usersFile.getName());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element root = document.createElement("user-accounts");
			
			Set<String> set = filteredUserAccounts.keySet();
			Iterator<String> setItr = set.iterator();
			while(setItr.hasNext()){
				
				String strId = (String)setItr.next();
				
				Element userAccountElement = document.createElement("user-account");
				userAccountElement.setAttribute("id", strId);
				
				Map<String, Map<String, String>> userAccountMap = filteredUserAccounts.get(strId);
				Map<String, String> authAttributeMap = userAccountMap.get(MBeanConstants.AUTH_ATTRIBUTES_MAP);
				Map<String, String> accountDataInfoMap = userAccountMap.get(MBeanConstants.ACCOUNT_INFO_MAP);
				
				Element authAttributesElement = document.createElement("authentication-attributes");
				
				Element userPassword = document.createElement("user-password");
				
				Element accountStatus = document.createElement("account-status");
				accountStatus.setTextContent((String)accountDataInfoMap.get(MBeanConstants.ACCOUNT_STATUS));
				
				Element creditLimit = document.createElement("credit-limit");
				creditLimit.setTextContent((String)accountDataInfoMap.get(MBeanConstants.CREDIT_LIMIT));
				
				Element expiryDate = document.createElement("expiry-date");
				expiryDate.setTextContent((String)accountDataInfoMap.get(MBeanConstants.EXPIRY_DATE));
				
				Element customerService = document.createElement("customer-service");
				customerService.setTextContent((String)accountDataInfoMap.get(MBeanConstants.CUSTOMER_SERVICE));
				
				Element customerType = document.createElement("customer-type");
				customerType.setTextContent((String)accountDataInfoMap.get(MBeanConstants.CUSTOMER_TYPE));
				
				Element serviceType = document.createElement("service-type");
				serviceType.setTextContent((String)accountDataInfoMap.get(MBeanConstants.SERVICE_TYPE));
				
				Element callingStationId = document.createElement("calling-station-id");
				callingStationId.setTextContent((String)accountDataInfoMap.get(MBeanConstants.CALLING_STATION_ID));
				
				Element calledStationId = document.createElement("called-station-id");
				calledStationId.setTextContent((String)accountDataInfoMap.get(MBeanConstants.CALLED_STATION_ID));
				
				Element emailId = document.createElement("email-id");
				emailId.setTextContent((String)accountDataInfoMap.get(MBeanConstants.EMAIL_ID));
				
				Element userGroup = document.createElement("user-group");
				userGroup.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_GROUP));
				
				Element userCheckItem = document.createElement("user-check-item");
				userCheckItem.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_CHECK_ITEM));
				
				Element userRejectItem = document.createElement("user-reject-item");
				userRejectItem.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_REJECT_ITEM));
				
				Element userReplyItem = document.createElement("user-reply-item");
				userReplyItem.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_REPLY_ITEM));
				
				Element accessPolicy = document.createElement("access-policy");
				accessPolicy.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_ACCESS_POLICY));
				
				Element radiusPolicy = document.createElement("authorization-policy");
				radiusPolicy.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_RADIUS_POLICY));
				
				Element concurrentLoginPolicy = document.createElement("concurrent-login-policy");
				concurrentLoginPolicy.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_CONCURRENT_LOGIN_POLICY));
				
				Element ipPoolName = document.createElement("ip-pool-name");
				ipPoolName.setTextContent((String)accountDataInfoMap.get(MBeanConstants.IP_POOL_NAME));
				
				Element passwordCheckEnabled = document.createElement("password-check-enabled");
				passwordCheckEnabled.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_PASSWORD_CHECK_ENABLED));
				
				Element password = document.createElement("password");
				password.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_PASSWORD));
				
				Element encryptionType = document.createElement("encryption-type");
				encryptionType.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_PASSWORD_ENCRYPTION_TYPE));
				
				Element cui = document.createElement("CUI");
				cui.setTextContent((String)accountDataInfoMap.get(MBeanConstants.CUI));
				
				Element param1 = document.createElement("param1");
				param1.setTextContent((String)accountDataInfoMap.get(MBeanConstants.PARAM1));
				
				Element param2 = document.createElement("param2");
				param2.setTextContent((String)accountDataInfoMap.get(MBeanConstants.PARAM2));
				
				Element param3 = document.createElement("param3");
				param3.setTextContent((String)accountDataInfoMap.get(MBeanConstants.PARAM3));
				
				Element param4 = document.createElement("param4");
				param4.setTextContent((String)accountDataInfoMap.get(MBeanConstants.PARAM4));
				
				Element param5 = document.createElement("param5");
				param5.setTextContent((String)accountDataInfoMap.get(MBeanConstants.PARAM5));
				
				Element hotlinePolicy = document.createElement("hotline-policy");
				hotlinePolicy.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USER_HOTLINE_POLICY));
				
				Element gracePeriod = document.createElement("grace-policy");
				gracePeriod.setTextContent((String)accountDataInfoMap.get(MBeanConstants.GRACEPOLICY));
				
				Element profileUserName = document.createElement("user-name");
				profileUserName.setTextContent((String)accountDataInfoMap.get(MBeanConstants.USERNAME));

				Element callbackId = document.createElement("callback-id");
				callbackId.setTextContent((String)accountDataInfoMap.get(MBeanConstants.CALLBACK_ID));

				Element macValidation = document.createElement("mac-validation");
				macValidation.setTextContent((String)accountDataInfoMap.get(MBeanConstants.MACVALIDATION));
				
				Element groupBandwidth= document.createElement("group-bandwidth");
				groupBandwidth.setTextContent((String)accountDataInfoMap.get(MBeanConstants.GROUPBANDWIDTH));
				
				Element imsi= document.createElement("imsi");
				imsi.setTextContent((String)accountDataInfoMap.get(MBeanConstants.IMSI));
				
				Element meid= document.createElement("meid");
				meid.setTextContent((String)accountDataInfoMap.get(MBeanConstants.MEID));
				
				Element msIsdn= document.createElement("msisdn");
				msIsdn.setTextContent((String)accountDataInfoMap.get(MBeanConstants.MSISDN));
				
				Element mdn= document.createElement("mdn");
				mdn.setTextContent((String)accountDataInfoMap.get(MBeanConstants.MDN));
				
				Element imei= document.createElement("imei");
				imei.setTextContent((String)accountDataInfoMap.get(MBeanConstants.IMEI));
				
				Element deviceVendor = document.createElement("device-vendor");
				deviceVendor.setTextContent((String)accountDataInfoMap.get(MBeanConstants.DEVICEVENDOR));
				
				Element deviceName = document.createElement("device-name");
				deviceName.setTextContent((String)accountDataInfoMap.get(MBeanConstants.DEVICENAME));
				
				Element devicePort= document.createElement("device-port");
				devicePort.setTextContent((String)accountDataInfoMap.get(MBeanConstants.DEVICEPORT));
				
				Element deviceVLAN = document.createElement("device-vlan");
				deviceVLAN.setTextContent((String)accountDataInfoMap.get(MBeanConstants.DEVICEVLAN));
				
				Element geoLocation = document.createElement("geo-location");
				geoLocation.setTextContent((String)accountDataInfoMap.get(MBeanConstants.GEOLOCATION));
				
				Element framedIPV4Address = document.createElement("framedIPV4Address");
				framedIPV4Address.setTextContent((String)accountDataInfoMap.get(MBeanConstants.FRAMEDIPV4ADDRESS));
				
				Element framedIPV6Prefix = document.createElement("framedIPV6Prefix");
				framedIPV6Prefix.setTextContent((String)accountDataInfoMap.get(MBeanConstants.FRAMEDIPV6PREFIX));
				
				Element framedPool = document.createElement("framedPool");
				framedPool.setTextContent((String)accountDataInfoMap.get(MBeanConstants.FRAMEDPOOL));
				
				Element authorizationPolicyGroup = document.createElement("authorization-policy-group");
				authorizationPolicyGroup.setTextContent((String)accountDataInfoMap.get(MBeanConstants.AUTHORIZATIONPOLICYGROUP));
				
				userPassword.appendChild(passwordCheckEnabled);
				userPassword.appendChild(password);
				userPassword.appendChild(encryptionType);
				
				Set<String> keys = authAttributeMap.keySet();
				Iterator<String> keyItr = keys.iterator();
				while(keyItr.hasNext()){
					String key = (String)keyItr.next();
					String value = (String)authAttributeMap.get(key);
					Element radiusAttribute = document.createElement("radius-attribute");
					radiusAttribute.setAttribute("id", key);
					radiusAttribute.setAttribute("value", value);
					authAttributesElement.appendChild(radiusAttribute);
				}
				userAccountElement.appendChild(authAttributesElement);
				
				userAccountElement.appendChild(userPassword);
				
				userAccountElement.appendChild(accountStatus);
				userAccountElement.appendChild(creditLimit);
				userAccountElement.appendChild(expiryDate);
				userAccountElement.appendChild(customerService);
				userAccountElement.appendChild(customerType);
				userAccountElement.appendChild(serviceType);
				userAccountElement.appendChild(callingStationId);
				userAccountElement.appendChild(calledStationId);
				userAccountElement.appendChild(emailId);
				userAccountElement.appendChild(userGroup);
				
				userAccountElement.appendChild(userCheckItem);
				userAccountElement.appendChild(userRejectItem);
				userAccountElement.appendChild(userReplyItem);
				
				userAccountElement.appendChild(accessPolicy);
				userAccountElement.appendChild(radiusPolicy);
				userAccountElement.appendChild(concurrentLoginPolicy);
				
				userAccountElement.appendChild(ipPoolName);
				
				userAccountElement.appendChild(cui);

				userAccountElement.appendChild(param1);
				userAccountElement.appendChild(param2);
				userAccountElement.appendChild(param3);
				userAccountElement.appendChild(param4);
				userAccountElement.appendChild(param5);
				
				userAccountElement.appendChild(hotlinePolicy);
				userAccountElement.appendChild(gracePeriod);
				userAccountElement.appendChild(profileUserName);
				userAccountElement.appendChild(callbackId);
				userAccountElement.appendChild(groupBandwidth);
				userAccountElement.appendChild(macValidation);
				userAccountElement.appendChild(imsi);
				userAccountElement.appendChild(meid);
				userAccountElement.appendChild(msIsdn);
				userAccountElement.appendChild(mdn);
				userAccountElement.appendChild(imei);
				userAccountElement.appendChild(deviceVendor);
				userAccountElement.appendChild(deviceName);
				userAccountElement.appendChild(devicePort);
				userAccountElement.appendChild(deviceVLAN);
				userAccountElement.appendChild(geoLocation);
				userAccountElement.appendChild(framedIPV4Address);
				userAccountElement.appendChild(framedIPV6Prefix);
				userAccountElement.appendChild(framedPool);
				userAccountElement.appendChild(authorizationPolicyGroup);
				
				root.appendChild(userAccountElement);
			}
			document.appendChild(root);
			
			System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
			TransformerFactory tFactory =TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
            
//          OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
//          of.setIndenting(true);
//			XMLSerializer serializer = new XMLSerializer(fos, of);
//			serializer.asDOMSerializer();
//			serializer.serialize(document.getDocumentElement());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputStream.toByteArray();
	}

	@Override
	public String getName() {
		return com.elitecore.core.commons.utilx.mbean.MBeanConstants.RADIUS_USERFILE;
	}

}
