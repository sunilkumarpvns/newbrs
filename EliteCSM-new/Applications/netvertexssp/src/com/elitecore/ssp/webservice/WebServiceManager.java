package com.elitecore.ssp.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import com.elitecore.netvertex.ws.parentalws.ParentalService;
import com.elitecore.netvertex.ws.subscriberprovisioningws.SubscriberProvisioningService;
import com.elitecore.netvertex.ws.subscriptionws.SubscriptionService;
import com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProvisioningWS;
import com.elitecore.ssp.util.EliteUtility;
import com.elitecore.ssp.util.constants.BaseConstant;
import com.elitecore.ssp.util.logger.Logger;



public class WebServiceManager {
	private static String MODULE = WebServiceManager.class.getSimpleName();
	private static String webservicePropsFileLocation = EliteUtility.getSSPHome() + File.separator +BaseConstant.WEBSERVICE_CONFIG_FILE_LOCATION;
	private static String SUBSCRIBEPROPERTYFILE = EliteUtility.getSSPHome() + File.separator +BaseConstant.SUBSCRIBE_PROFILER_FILE_LOCATION;
	
	private static WebServiceManager webServiceManager;
	
	private URL subscriptionWsLocation_URL;
	private URL parentalWsLocation_URL;
	private URL subscriberProvisiongWsLocation_URL;
	
	private String telenorConnectionURL = "";
	private String telenorDriverClass = "";
	private String telenorDatabaseUsername = "";
	private String telenorDatabasePassword = "";
	

	private boolean initialized =false;
	
	private SubscriptionService subscriptionWs;
	private ParentalService parentalWs;
    private SubscriberProvisioningService subscriberProvisioningService;
	private Properties propertiesForSubscriber;
	private WebServiceManager(){
		
	}
	
	public synchronized static WebServiceManager getInstance(){
		if(webServiceManager==null){
			webServiceManager = new WebServiceManager();
			webServiceManager.init();
		}
		return webServiceManager;
	}

	/**
	 * @return the subscriptionWs_Port
	 */
	public com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.SubscriptionWS getSubscriptionWs_Port() {
		return subscriptionWs.getSubscriptionWS();
	}

	/**
	 * @return the parentalWs_Port
	 */
	public com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalWS getParentalWs_Port() {
		return parentalWs.getParentalWS();
	}
	
	 public SubscriberProvisioningWS getSubscriberProvisioningWS() {
	        return subscriberProvisioningService.getSubscriberProvisioningWS();
	 }
	
	
	
	private void init(){
		if(!initialized){
			Logger.logInfo(MODULE, "Initializing web service manager...");
			FileInputStream fileInputStream = null;
			try {
				Properties properties = new Properties();
				System.out.println("webservice location-------> "+webservicePropsFileLocation);
				File webServiceConfigFile = new File(webservicePropsFileLocation);
				fileInputStream = new FileInputStream(webServiceConfigFile);
				properties.load(fileInputStream);
				String parentalWsLocation=properties.getProperty("parentalsspwebservice.url");
				String subscriptionWsLocation=properties.getProperty("subscriptionwebservice.url");
				String subcriberProvisioningWsLocation=properties.getProperty("subscriberprovisiong.url");
				if(subscriptionWsLocation!=null)
					subscriptionWsLocation_URL=new URL(subscriptionWsLocation);
				if(parentalWsLocation!=null)
					parentalWsLocation_URL=new URL(parentalWsLocation);
				if(subcriberProvisioningWsLocation!=null)
				   subscriberProvisiongWsLocation_URL=new URL(subcriberProvisioningWsLocation); 
				Logger.logInfo(MODULE,"Subscription Webservice location: "+subscriptionWsLocation);
				Logger.logInfo(MODULE,"Parental Webservice location: "+parentalWsLocation);

				parentalWs=new ParentalService(parentalWsLocation_URL);
				subscriptionWs=new SubscriptionService(subscriptionWsLocation_URL);
				subscriberProvisioningService=new SubscriberProvisioningService(subscriberProvisiongWsLocation_URL);

				readSubscriberPropeties();
				
				telenorConnectionURL = properties.getProperty("connection.url");
				telenorDatabasePassword = properties.getProperty("connection.password");
				telenorDatabaseUsername = properties.getProperty("connection.username");
				telenorDriverClass = properties.getProperty("connection.driver_class");
				
				initialized = true;
				Logger.logDebug(MODULE, "Web Servie Initialization completed...");
			}catch (Exception e) {
				Logger.logError(MODULE,"Error while reading webservice-config properties, reason:"+e.getMessage());
				Logger.logTrace(MODULE, e);
			}finally{
				try{
					if(fileInputStream!=null){
						fileInputStream.close();
					}
				}catch(Exception e){

				}
			}
		}
	}
	

	
	public void readSubscriberPropeties(){
		FileInputStream fileInputStream = null;
		try{
			Properties properties = new Properties();
			File dbPropsFile = new File(SUBSCRIBEPROPERTYFILE);
			fileInputStream = new FileInputStream(dbPropsFile);
			properties.load(fileInputStream);
			this.propertiesForSubscriber=properties;
		}catch (Exception e) {
			Logger.logError(MODULE,"Error while reading subscribeProfiler properties, reason:"+e.getMessage());
		}finally{
			try{
				if(fileInputStream!=null){
					fileInputStream.close();
				}
			}catch(Exception e){
				Logger.logError(MODULE,"Error while closing Stream, reason:"+e.getMessage());	
			}
		}
	}

	public String getParameterValue(String parameterName) {
		return propertiesForSubscriber.getProperty(parameterName);
	}

	public String getTelenorConnectionURL() {
		return telenorConnectionURL;
	}

	public String getTelenorDriverClass() {
		return telenorDriverClass;
	}

	public String getTelenorDatabaseUsername() {
		return telenorDatabaseUsername;
	}

	public String getTelenorDatabasePassword() {
		return telenorDatabasePassword;
	}
	

	
	
}
