package com.elitecore.elitesm.web.core.system.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.MDC;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.SecurityConfiguration;
import org.owasp.esapi.codecs.OracleCodec;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.DefaultSecurityConfiguration;

import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;

public class ESAPISecurityPlugin
{
	static final String MODULE = "[ESAPISecurityPlugin] ";
	private String _intrusion_type;
	private String _param;
	public static List<String> blockIpList=new ArrayList<String>();
	public static ConcurrentHashMap<String,Long> elapsedTimeMap=new ConcurrentHashMap<String, Long>();
    private static String esapiPropsFileLocation=EliteUtility.getSMHome()+File.separator+ConfigConstant.ESAPI_PROPERTIES;
	private static String validationPropesFileLocation=EliteUtility.getSMHome()+File.separator+ConfigConstant.VALIDATION_PROPERTIES;
	    
	public void init() {
		//Load ESAPI.Properties file from given Location
		Properties esapiProps=new Properties();
        Logger.logInfo(MODULE, "Loading ESAPI Properties from "+esapiPropsFileLocation);
        File esapiPropsFile =new File(esapiPropsFileLocation);
        try{
        	  FileInputStream fileInputStream = new FileInputStream(esapiPropsFile);
              esapiProps.load(fileInputStream);
              
              File validationPropsFile =new File(validationPropesFileLocation);
              fileInputStream = new FileInputStream(validationPropsFile);
              esapiProps.load(fileInputStream);
              
              SecurityConfiguration defaultSecurityConfiguration=new DefaultSecurityConfiguration(esapiProps);
              ESAPI.override(defaultSecurityConfiguration);
              
        } catch (Exception e) {
        	Logger.logError(MODULE, "Problem in Loading Properties file : "+e);
		}
	
        
	} 
	
	public Object scanVulnerability(Object value, HashMap scanningParameters) throws ServerManagerSecurityException 
	{
		boolean validFlag=true,ipExistInMap=false,isIpRemoved=false;
		
		String rAddress=EliteUtility.getRemoteAddress();
		
		if(elapsedTimeMap.size() > 0 && elapsedTimeMap !=null){
			if(rAddress != null){
				for(String rAdd:elapsedTimeMap.keySet()){
					if(rAdd.equals(rAddress)){
						ipExistInMap=true;
					}
				}	
				if(ipExistInMap == true){
					long blockTime=elapsedTimeMap.get(rAddress);
					for(String rAdd:elapsedTimeMap.keySet()){
						if(rAdd.equals(rAddress)){
							long currentTime=System.currentTimeMillis();
							long diff=currentTime-blockTime;
							long diffSeconds = diff / 1000;
							if(diffSeconds >= Long.parseLong(ConfigManager.get(ConfigConstant.LOGIN_BLOCK_INTERVAL))){
								blockIpList.remove(rAddress);
								isIpRemoved=true;
							}
						}
					}
				}
			
			}
		}
		
		if(isIpRemoved == true){
			elapsedTimeMap.remove(rAddress);
		}
		
		if(blockIpList.size() > 0 && blockIpList != null){
			for(String blockIp:blockIpList){
				String remoteAddr=EliteUtility.getRemoteAddress();
				if(remoteAddr != null){
					if(remoteAddr.equals(blockIp)){
						validFlag=false;
						throw new ServerManagerSecurityException();
					}
				}
			}
		}
		
		
		if(validFlag) 
		{
	    	String input = (value!=null ? value.toString() : null);
	    	String reqInitiation = (scanningParameters!=null ? (scanningParameters.get("LOCATION")!=null ? scanningParameters.get("LOCATION")+"" : "") : "");
			try 
			{				
				if(reqInitiation.equalsIgnoreCase("BaseAction") || reqInitiation.equalsIgnoreCase("BaseWebServlet")) {
					Map map = (Map)value;					
					Set set = map.entrySet();					
					Iterator i = set.iterator();
					String[] strParamArr;
					
					while(i.hasNext()) {
						Map.Entry me = (Map.Entry)i.next();
						strParamArr = (String[])me.getValue();
						
						for(int j=0 ; j<strParamArr.length; j++ ) {
							input = validateInput(strParamArr[j], null);
						}
					}					
				}
				else {					
					input = validateInput(input, scanningParameters);
				}				
			} 
			catch (ValidationException ev) 
			{	
				Logger.logWarn(MODULE, "WARNING: Intrustion has been detected with these parameters: " + value);
				Logger.logError(MODULE, "SecurityException  :"+ev);
				HashMap mpInfoMap=new HashMap();
				mpInfoMap.put("ClientIP", getHostAddress());
				mpInfoMap.put("IntructionValue",_param);
				
				value = null;
				try{
					Logger.logDebug(MODULE, " In reportIntrusion Alert Generated.");
					Logger.logDebug(MODULE, " A Potentially dangerous Request was detected from the client : "+getClientAddress());
					String remoteAddress=EliteUtility.getRemoteAddress();
					boolean chkFlag=false;
					if(remoteAddress != null){
						for(String add:elapsedTimeMap.keySet()){
							if(add.equals(remoteAddress)){
								chkFlag=true;
							}
						}
						if(chkFlag == false){
							blockIpList.add(remoteAddress);
							elapsedTimeMap.put(remoteAddress, System.currentTimeMillis());
						}
					}
					
					Writer writer = new StringWriter();
					PrintWriter printWriter = new PrintWriter(writer);
					ev.printStackTrace(printWriter);
					String s = writer.toString();
					String traceArray[]=s.split("\n");
					StringBuffer st=new StringBuffer();
					for(String str:traceArray){
						if(str.contains("com.elitecore") || !(str.trim().startsWith("at"))){
							st.append(str);
						}
					}
					
					
					reportIntrusion(mpInfoMap,st.toString());
				}
				catch(Exception e){
					Logger.logError(MODULE, " Report or Audit Exception  :"+e);
				}
				throw new ServerManagerSecurityException();
			} 
			catch (IntrusionException ei) 
			{			
				Logger.logWarn(MODULE, "WARNING: Intrustion has been detected with these parameters: " + value);
				Logger.logError(MODULE, "SecurityException  :"+ei);
				HashMap mpInfoMap=new HashMap();
				mpInfoMap.put("ClientIP", getHostAddress());
				mpInfoMap.put("IntructionValue",_param);
				value = null;
				try{
					Logger.logDebug(MODULE, " In reportIntrusion Alert is Generated.");
					Logger.logDebug(MODULE, " A Potentially dangerous Request was detected from the client : "+getClientAddress());
					String remoteAddress=EliteUtility.getRemoteAddress();
					boolean chkFlag=false;
					if(remoteAddress != null){
						for(String add:elapsedTimeMap.keySet()){
							if(add.equals(remoteAddress)){
								chkFlag=true;
							}
						}
						if(chkFlag == false){
							blockIpList.add(remoteAddress);
							elapsedTimeMap.put(remoteAddress, System.currentTimeMillis());
						}
					}
					
					Writer writer = new StringWriter();
					PrintWriter printWriter = new PrintWriter(writer);
					ei.printStackTrace(printWriter);
					String s = writer.toString();
					
					String traceArray[]=s.split("\n");
					StringBuffer st=new StringBuffer();
					for(String str:traceArray){
						if(str.contains("com.elitecore") || !(str.trim().startsWith("at"))){
							st.append(str);
						}
					}
					
					reportIntrusion(mpInfoMap,s);
				}
				catch(Exception e){
					Logger.logError(MODULE, " Report or Audit Exception  :" +e);
				}
				throw new ServerManagerSecurityException();
			}
			catch (Exception e) 
			{
				Logger.logError(MODULE, " Expection  :"+e);
			}
		}
		else 
		{
			Logger.logDebug(MODULE, "Server Manager Security is disabled, going to bypass vulnerability scanning...");
		}
        return value;
	}

	/*private void reportIntrusion(HashMap mpInfoMap) {
		String strValue=ESAPI.encoder().encodeForHTML((String)mpInfoMap.get("IntructionValue"));
		String strEmailMessage="Intrusion Detected From : "+mpInfoMap.get("ClientIP")+" and Value : "+strValue;
		ConfigManager.IntrustionDetectedMsg=strEmailMessage;
	}*/

	private void reportIntrusion(HashMap mpInfoMap,String str) {
		String strValue=ESAPI.encoder().encodeForHTML((String)mpInfoMap.get("IntructionValue"));
		String strEmailMessage="Intrusion Detected From : "+mpInfoMap.get("ClientIP")+" and Value : "+strValue ;
		ConfigManager.IntrustionDetectedMsg=strEmailMessage;
		ConfigManager.stackTraceString=str;
		ConfigManager.clientIp=getHostAddress();
	}

	private String validateInput(String input, HashMap scanningParameters) throws ValidationException, IntrusionException, Exception 
	{
		String safeString = "";
		_param = input;
		if(input!=null) {
	    	if(input.length()>0) {
	    		
	    		if(input.contains(".xml")){
	    			Logger.logDebug(MODULE, "File Vulnerability scanning String: "+input);
	    			safeString=input;
	    			Logger.logDebug(MODULE, "File Vulnerability Safe String: "+ safeString+" :Input:"+input);
	    		}
	    		else{
	    			
					safeString = ESAPI.encoder().encodeForSQL( new OracleCodec() , input);
					input=safeString;
					
		    		_intrusion_type = "XSS";
		    		safeString = ESAPI.validator().getValidInput("SMValidator", input, "SMValidator", input.length(), false);
		    		_intrusion_type = "NONE";
	    		}
	    	}
		}
		return safeString;    	
	}
	private String getHostAddress() {
		InetAddress host = null;
		try {
			host = InetAddress.getLocalHost();
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}		
		return (host != null ? host.getHostAddress() : "");
	}
	
	private static String getClientAddress(){

		String clientAddress = "";
		String remoteAddress = (String)MDC.get("remoteaddress");

		if(remoteAddress!=null){
			clientAddress  = remoteAddress;
		}else{
			clientAddress="0.0.0.0";
			}
		return clientAddress;
	}
	
}