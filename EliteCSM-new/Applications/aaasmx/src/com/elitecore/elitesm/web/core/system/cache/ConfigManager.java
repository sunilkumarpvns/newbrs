package com.elitecore.elitesm.web.core.system.cache;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.elitesm.Version;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.SystemParameterBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_CONSTANTS;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_STATUS;
import com.elitecore.elitesm.web.dashboard.db.DashBoardConnectionPool;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.util.SystemUtil;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;


public class ConfigManager {
    
    private static final String CUSTOMER_NAME = "CUSTOMER_NAME";
	private static final String CUSTOMER_EMAIL = "CUSTOMER_EMAIL";
	public static final String LOWER_CASE = "1";
	public static final String UPPER_CASE = "2";
	
	private static final String CASE_SENSITIVITY_FOR_POLICY = "CASE_SENSITIVITY_FOR_POLICY";
	private static final String CASE_SENSITIVITY_FOR_SUBSCRIBER = "CASE_SENSITIVITY_FOR_SUBSCRIBER";
	// This implementatino is only to support central date format for the system. 
    private static Map<String,String> map = new HashMap<String,String>();
    private static final String MODULE = "CONFIG MANAGER";
    private static final String LICENSE_FILE = "local_node.lic";
    private static OS os = OS.Linux;
    private static final String contactEmail = "support@eliteaaa.com";
    private static DashBoardConnectionPool dashBoardConnectionPool;
    private static boolean isThreatFound=false;
    public static String IntrustionDetectedMsg="";
    public static String clientIp="";
	public static String stackTraceString="";
	public static String requestHeader="";
	private static boolean isInitialize = false;
	private static String dbPropsFileLocation= EliteUtility.getSMHome() +ConfigConstant.DATABASE_CONFIG_FILE_LOCATION;
	private static Properties dbProperties = new Properties();
	private static String dbPropsFileSmHome=  System.getenv("SM_HOME") + "/database.properties";
	public static String policyCaseSensitivity;
	public static String subscriberCaseSensitivity;
    public static Properties getDbProperties() {
		return dbProperties;
	}

	public static void init(){
        try {
            SystemParameterBLManager blManager = new SystemParameterBLManager();
            List<ISystemParameterData> lstParameterList = blManager.getList();	
            
            /* clear the map of ACL */
            if(Maps.isNullOrEmpty(map) == false){
            	map.clear();
            }
            
            for (ISystemParameterData data : lstParameterList) {
                map.put(data.getAlias(),data.getValue());
                if(CASE_SENSITIVITY_FOR_POLICY.equalsIgnoreCase(data.getAlias())){
                	policyCaseSensitivity = data.getValue();
                } else if (CASE_SENSITIVITY_FOR_SUBSCRIBER.equalsIgnoreCase(data.getAlias())){
                	subscriberCaseSensitivity = data.getValue();
                }
            }            
            isInitialize = true;
            EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.SSS, SM_MODULE_STATUS.SUCCESS);
            
            /** storing database.properties file in local cache(reuse purpose) once SM is running with valid 
             *  database credential**/
			File dbPropsFile = new File(dbPropsFileSmHome);
			if (dbPropsFile.exists()) {
				storeDatabasePropertiesInCache(dbPropsFile);
			} else {
				dbPropsFile = new File(dbPropsFileLocation);
				storeDatabasePropertiesInCache(dbPropsFile);
			}
			
        } catch (DataManagerException hExp) {
        	if(ConfigManager.isInitCompleted()){
        		Logger.logError(MODULE, "Error during config manager operation, Reason: "); 
        		Logger.logTrace(MODULE, hExp);
        	}else{
        		Logger.logError(MODULE, "Invalid database connection parameters, Reconfigure database connection parameters");
        	}
        }
    }

	private static void storeDatabasePropertiesInCache(File dbPropsFile) {
		FileInputStream dbPropertiesStream = null;
		try {
			dbPropertiesStream = new FileInputStream(dbPropsFile);
			dbProperties.load(dbPropertiesStream);
			Logger.logInfo(MODULE,"successfully stored database.properties file in local cache");
		} catch (FileNotFoundException e) {
			Logger.logError(MODULE,"Error while storing database.properties file in local cache, Reason: ");
			Logger.logTrace(MODULE, e);
		} catch (IOException e) {
			Logger.logError(MODULE,"Error while storing database.properties file in local cache, Reason: ");
			Logger.logTrace(MODULE, e);
		} finally {
			Closeables.closeQuietly(dbPropertiesStream);
		}
	}

    //default common module
    public static String get(String parameterName) {
        return get(ConfigConstant.COMMON, parameterName);
    }
    
    public static String get(String moduleName, String parameterName) {
    	
    	if(parameterName.equalsIgnoreCase(BaseConstant.ENCRYPTION_MODE)){
    		return Integer.toString(PasswordEncryption.NONE);
    	}
    	
    	String value = (String)map.get(parameterName);
    	if (value == null)
    		value = "";
        return value;
    }
    
    public static void refreshSystemParameter(){
		String alias,value;

		try {
		    SystemParameterBLManager blManager = new SystemParameterBLManager();
    
			List lstParameterList = blManager.getList();	
				for(int i=0;i<lstParameterList.size();i++){
					alias = ((ISystemParameterData)lstParameterList.get(i)).getAlias();
					value = ((ISystemParameterData)lstParameterList.get(i)).getValue();
					map.put(alias,value); 
				}
				
		} catch (DataManagerException hExp) {
			Logger.logError(MODULE, "Error during Config Manager operation , reason : " + hExp.getMessage()); 
		}
    }
    
    public static List readLicense(String contextPath) throws Exception {
        List lstLicenseData = new ArrayList();
        
        String licenseFilePath = contextPath + File.separator + LicenseConstants.LICENSE_DIRECTORY + File.separator + LICENSE_FILE;
        String licenseKey = readLicenseKey(licenseFilePath);
        if (licenseKey != null && !licenseKey.equalsIgnoreCase("")) {
            lstLicenseData = SystemUtil.sequenceingLicense(SystemUtil.populateAdditionalInformations(SystemUtil.getLicenseInformationMap(licenseKey)));
        }
        
        List filteredLicenseData = new ArrayList();
        
         for(int i=0; i<lstLicenseData.size(); i++) {
                LicenseData licenseData = (LicenseData)lstLicenseData.get(i);
              
                if(licenseData.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)) {
                    filteredLicenseData.add(licenseData);
                }
         }
         
      
        return filteredLicenseData;
    }

    public static String readLicenseKey(String licenseFilePath) {
        String licenseKey = null;
        File licenseFile = null;
        InputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        
        try{
                licenseFile = new File(licenseFilePath);
                fileInputStream = new FileInputStream(licenseFile);
                dataInputStream = new DataInputStream(fileInputStream);
                byte[] dateLicenceFiledata = new byte[(int)licenseFile.length()];
                dataInputStream.readFully(dateLicenceFiledata);
                licenseKey = new String(dateLicenceFiledata);
                
        }catch(FileNotFoundException e) {
            Logger.logError(MODULE, "File Not Found : " + e.getMessage());
            return "";
            
        } catch(Exception e){
                Logger.logError(MODULE, "Problem while Reading LicenseKey : "+e.getMessage());
                Logger.logTrace(MODULE,e);
        }finally{
                try{
                    if(fileInputStream!=null)
                        fileInputStream.close();
                    if(dataInputStream!=null)
                        dataInputStream.close();
                }catch(IOException ioExc){
                        Logger.logError(MODULE,"Problem while Reading LicenseKey "+ioExc.getMessage());
                }
        }
        
        return licenseKey;
    }    

    
    public static String getContactMailURI(HttpServletRequest request){
    	StringBuilder contactMail = new StringBuilder();
    	contactMail.append("mailto:"+contactEmail);
    	/*CC Content*/
    	if(map.get(CUSTOMER_EMAIL) != null) {
    		String toCC = map.get(CUSTOMER_EMAIL);
    		setUserOS(request);
    		switch(getOs()){
    		case Windows:
    		case MacOS:
    			toCC = toCC.replaceAll(",", ";");
    			Logger.logInfo(MODULE, "Change separator for Window Or MacOS"+toCC);
    			break;
    		}
    		contactMail.append("?cc="+toCC);
    	}
    	
    	/*Subject Contents*/
    	if(map.get(CUSTOMER_NAME) != null) {
    		contactMail.append(contactMail.indexOf("?")>0?'&':'?'); 
    		try {
				contactMail.append("subject="+URLEncoder.encode(map.get(CUSTOMER_NAME),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Logger.logError(MODULE, "Error occurs in encode body of contact URL:"+e.getMessage());
				contactMail.append("subject="+map.get(CUSTOMER_NAME));
			}
    	}
    	
    	/*Body Contents*/
    	contactMail.append(contactMail.indexOf("?")>0?'&':'?');
    	StringBuilder bodyContents = new StringBuilder();
    	
    	/*Append Version information in mail body*/
    	bodyContents.append(" Version : "+Version.getVersion());
    	bodyContents.append("\n Date : "+Version.getReleaseDate());
    	bodyContents.append("\n Revision : "+Version.getSVNRevision());
    	
    	/*Append User information in mail body*/
    	try {
    		StaffBLManager staffBLManager = new StaffBLManager();
    		SystemLoginForm systemLoginForm = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");
			IStaffData staffData = staffBLManager.getStaffData(systemLoginForm.getUserId());
			if(staffData != null){
				bodyContents.append("\n\n"+staffData.getBasicDetail());
				Logger.logInfo(MODULE, "user Data:"+staffData.getBasicDetail());
			}
			contactMail.append("body="+URLEncoder.encode(bodyContents.toString(), "UTF-8"));
		} catch (DataManagerException e) {
			Logger.logError(MODULE, "Error occurs in getting staff Data:"+e.getMessage());
		}catch (UnsupportedEncodingException e) { 
			Logger.logError(MODULE, "Error occurs in encode body of contact URL:"+e.getMessage());
			contactMail.append("body="+bodyContents);
		}
		Logger.logInfo(MODULE, "Email String:"+contactMail);
    	return contactMail.toString().replaceAll("\\+", " ");
    }
    
    
    public static String getContactMailURIForMalformedUser(HttpServletRequest request){
    	StringBuilder contactMail = new StringBuilder();
    	contactMail.append("mailto:"+contactEmail);
    	/*CC Content*/
    	if(map.get(CUSTOMER_EMAIL) != null) {
    		String toCC = map.get(CUSTOMER_EMAIL);
    		setUserOS(request);
    		switch(getOs()){
    		case Windows:
    		case MacOS:
    			toCC = toCC.replaceAll(",", ";");
    			Logger.logInfo(MODULE, "Change separator for Window Or MacOS"+toCC);
    			break;
    		default :
    			break;
    		}
    		contactMail.append("?cc="+toCC);
    	}
    	
    	/*Subject Contents*/
    	if(map.get(CUSTOMER_NAME) != null) {
    		contactMail.append(contactMail.indexOf("?")>0?'&':'?'); 
    		try {
				contactMail.append("subject="+URLEncoder.encode(map.get(CUSTOMER_NAME),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Logger.logError(MODULE, "Error occurs in encode body of contact URL:"+e.getMessage());
				contactMail.append("subject=111");
			}
    	}else{
    		contactMail.append(contactMail.indexOf("?")>0?'&':'?');
    		contactMail.append("subject="+IntrustionDetectedMsg);
    	}
    	
    	/*Body Contents*/
    	contactMail.append(contactMail.indexOf("?")>0?'&':'?');
    	StringBuilder bodyContents = new StringBuilder();
    	
    	/*Append Version information in mail body*/
    	bodyContents.append(" Hi, ");
    	
    	/*Append User information in mail body*/
    	try {
			
    		bodyContents.append("\n\n Version : "+Version.getVersion());
        	bodyContents.append("\n Date : "+Version.getReleaseDate());
        	bodyContents.append("\n Revision : "+Version.getSVNRevision());
        	
        	if(request.getHeader("referer") != null ){
        		requestHeader=request.getHeader("referer");
        	}
			bodyContents.append("\n\nIntrusion Detected at : "+requestHeader);
			bodyContents.append("\n\nTrace Log :\n");
			bodyContents.append(stackTraceString);
        	
        	contactMail.append("body="+URLEncoder.encode(bodyContents.toString(), "UTF-8"));
        	contactMail.append(URLEncoder.encode("\n\n\nThanks & Regards,", "UTF-8"));
        
        	
		}catch (UnsupportedEncodingException e) { 
			Logger.logError(MODULE, "Error occurs in encode body of contact URL:"+e.getMessage());
			contactMail.append("body="+bodyContents);
		}
    	
		Logger.logInfo(MODULE, "Email String:"+contactMail);
    	return contactMail.toString().replaceAll("\\+", " ");
    }
    

    private static void setUserOS(HttpServletRequest request){
    	String userAgent = request.getHeader("User-Agent");  
    	String user = userAgent.toLowerCase();  
    	Logger.logInfo(MODULE, "UserAgent:"+user);
    	if (user.indexOf("windows ")!=-1) os = OS.Windows;
    	if (user.indexOf("mac")!=-1) os = OS.MacOS;
    	if (user.indexOf("x11")!=-1) os = OS.UNIX;
    }


	public static OS getOs() {
		return os;
	}

	public static boolean isThreatFound() {
		return isThreatFound;
	}

	public static boolean isInitCompleted() {
		return isInitialize;
	}
	
	public static void setInitialized(boolean initialized) {
		isInitialize = initialized;
	}

	public static void setThreatFound(boolean isThreatFound) {
		ConfigManager.isThreatFound = isThreatFound;
	}
	
	public static String getDriverClass(String dbUrl) {
		
		dbUrl = EliteUtility.getLowerCaseString(dbUrl);
		
		if(Strings.isNullOrBlank(dbUrl) == false && dbUrl.contains("oracle")){
			return "oracle.jdbc.driver.OracleDriver";
		}else if(Strings.isNullOrBlank(dbUrl) == false && dbUrl.contains("postgres")){
			return "org.postgresql.Driver";
		}else if(Strings.isNullOrBlank(dbUrl) == false && dbUrl.contains("mariadb")){
			return "org.mariadb.jdbc.Driver";
		}else if(Strings.isNullOrBlank(dbUrl) == false && dbUrl.contains("mysql")){
			return "com.mysql.jdbc.Driver";
		}
		return null;
	}

	public static String getPlainPasswordFromEncrypted(String encryptedPassword) throws NoSuchEncryptionException, Exception {
		String plainPassoword = "";
		
		if (Strings.isNullOrBlank(encryptedPassword) == false) {
			try {
				plainPassoword = PasswordEncryption.getInstance().decrypt(encryptedPassword, PasswordEncryption.ELITE_PASSWORD_CRYPT);
			} catch (DecryptionNotSupportedException e) {
				throw new Exception("Error while decrypting password");
			} catch (DecryptionFailedException e) {
				throw new Exception("Error while decrypting password");
			}
		} else {
			throw new Exception("password is not specified in database.properties file");
		}
		return plainPassoword;
	}
	
	public static boolean chekForCaseSensitivity(){
		
		if(UPPER_CASE.equalsIgnoreCase(policyCaseSensitivity)){
			return true;
		}else if(LOWER_CASE.equalsIgnoreCase(policyCaseSensitivity)){
			return true;
		}else {
			return false;
		}
	}
}


enum OS{
	Windows,Linux,MacOS,UNIX
}

