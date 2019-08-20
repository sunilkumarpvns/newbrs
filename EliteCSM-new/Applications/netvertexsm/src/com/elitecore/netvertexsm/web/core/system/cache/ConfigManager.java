package com.elitecore.netvertexsm.web.core.system.cache;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.SystemParameterBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.util.SystemUtil;
import com.elitecore.passwordutil.PasswordEncryption;


public class ConfigManager {
    
    // This implementation is only to support central date format for the system. 
    private static Map<String,String> map = new HashMap<String,String>();
    private static final String MODULE = "CONFIG MANAGER";
    private static final String LICENSE_FILE = "local_node.lic";

    public static void init(){
        try {
            SystemParameterBLManager blManager = new SystemParameterBLManager();
            List<ISystemParameterData> lstParameterList = blManager.getList();	

            for (ISystemParameterData data : lstParameterList) {
                map.put(data.getAlias(),data.getValue()); 
            }

        } catch (DataManagerException hExp) {
            Logger.logError(MODULE, "Error during Config Manager operation , reason : " + hExp.getMessage()); 
            Logger.logTrace(MODULE, hExp);
        }
    }

    //default common module
    public static String get(String parameterName) {
        return get(ConfigConstant.COMMON, parameterName);
    }
    
    public static String get(String moduleName, String parameterName) {
        /*if (ConfigConstant.COMMON.equals(moduleName) && ConfigConstant.DATE_FORMAT.equals(parameterName)){
            return "dd/MMM/yyyy HH:mm:ss";
        }
        if (ConfigConstant.COMMON.equals(moduleName) && ConfigConstant.SHORT_DATE_FORMAT.equals(parameterName)){
            return "dd/MMM/yyyy";
        }*/
    	
    	if(parameterName.equalsIgnoreCase(BaseConstant.ENCRYPTION_MODE)){
    		return PasswordEncryption.NONE+"";
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

    
}
