 package com.elitecore.netvertex.gateway.diameter.application;

import java.util.*;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupFactory;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

public class ApplicationListenerFactory {
	
	private static final String MODULE = "APP-LSTR-FCTRY";
	private DiameterGatewayControllerContext diameterGatewayControllerContext;
	private DiameterPeerGroupFactory groupFactory;
	private EnumMap<Application, Map<SupportedStandard, ApplicationHandler>> appHandlers;
	
	public ApplicationListenerFactory(DiameterPeerGroupFactory groupFactory, 
			DiameterGatewayControllerContext diameterGatewayControllerContext,
			EnumMap<Application, Map<SupportedStandard, ApplicationHandler>> appHandlers){
		this.groupFactory = groupFactory;
		this.diameterGatewayControllerContext = diameterGatewayControllerContext;
		this.appHandlers = appHandlers;
	}
	
	public Map<String,NetvertexApplication> createApplicationListenerFromConfiguration(Collection<DiameterGatewayConfiguration> diameterGatewayConfigurations){
		
		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "Creating Application from Diameter gateway configuration");
		
		Map<String, NetvertexApplication> appListeners = new HashMap<String, NetvertexApplication>();
		
		//Standard S9 Interface
		try{
			S9Application stdS9Application = new S9Application(diameterGatewayControllerContext);
			stdS9Application.init();
			registerListener(stdS9Application, appListeners);
		}catch(AppListenerInitializationFaildException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error in initializing Standard S9 interface With " +
						"App Id = " + ApplicationIdentifier.TGPP_S9.getVendorId()  + ":" + ApplicationIdentifier.TGPP_S9.getApplicationId() +
						" . Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}

		
		//Standard SY Interface
		try{
			SyApplication stdSyApplication = new SyApplication(groupFactory,diameterGatewayControllerContext);
			stdSyApplication.init();
			registerListener(stdSyApplication,appListeners);
		}catch(AppListenerInitializationFaildException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error in initializing Standard SY interface With " +
						"App Id = " + ApplicationIdentifier.TGPP_SY.getVendorId()  + ":" + ApplicationIdentifier.TGPP_SY.getApplicationId() +
						" . Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}

		//Standard Gx Interface
		try{
			GxApplication stdGXApplication = new GxApplication(diameterGatewayControllerContext);
			stdGXApplication.init();
			registerListener(stdGXApplication,appListeners);

		}catch(AppListenerInitializationFaildException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error in initializing Standard Gx interface With " +
						"App Id = " + ApplicationIdentifier.TGPP_GX_29_212_18.getVendorId()  + ":" + ApplicationIdentifier.TGPP_GX_29_212_18.getApplicationId() +
						" . Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}


		//Standard Rx Interface
		RxApplication stdRXApplication = null;
		try{
			stdRXApplication = new RxApplication(diameterGatewayControllerContext);
			stdRXApplication.init();
			registerListener(stdRXApplication,appListeners);
		}catch(AppListenerInitializationFaildException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error in initializing Standard Rx interface With " +
						"App Id = " + ApplicationIdentifier.TGPP_RX_29_214_18.getVendorId()  + ":" + ApplicationIdentifier.TGPP_RX_29_214_18.getApplicationId() +
						" . Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}


		// Standard Gy Interface
		registerStandardGyApplication(appListeners);

		
		for(DiameterGatewayConfiguration diameterGatewayConf : diameterGatewayConfigurations){

			//license check
			if(!appListeners.containsKey(diameterGatewayConf.getGxVendorId()+":" +diameterGatewayConf.getGxApplicationId())){
				try{
					GxApplication gxApplication = new GxApplication(diameterGatewayControllerContext,
							diameterGatewayConf.getGxVendorId(),diameterGatewayConf.getGxApplicationId());
					gxApplication.init();
					registerListener(gxApplication, appListeners);
				}catch(AppListenerInitializationFaildException ex){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in initializing Standard Gx interface With " +
								"App Id = " + diameterGatewayConf.getGxVendorId()  + ":" + diameterGatewayConf.getGxApplicationId() +
								" for gateway = "+ diameterGatewayConf.getName() +" . Reason: " + ex.getMessage());
					LogManager.getLogger().trace(ex);
				}
			}

			if(diameterGatewayConf.getSupportedStandard() != SupportedStandard.CISCOSCE) {
				if (!appListeners.containsKey(diameterGatewayConf.getRxVendorId() + ":" + diameterGatewayConf.getRxApplicationId())) {
					try {
						RxApplication rxApplication = new RxApplication(diameterGatewayControllerContext,
								diameterGatewayConf.getRxVendorId(), diameterGatewayConf.getRxApplicationId());
						rxApplication.init();
						registerListener(rxApplication, appListeners);
					} catch (AppListenerInitializationFaildException ex) {
						if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in initializing Standard Rx interface With " +
									"App Id = " + diameterGatewayConf.getRxVendorId() + ":" + diameterGatewayConf.getRxApplicationId() +
									" for gateway = " + diameterGatewayConf.getName() + " . Reason: " + ex.getMessage());
						LogManager.getLogger().trace(ex);
					}
				}
			}

			//Standard SY Interface
			if(!appListeners.containsKey(diameterGatewayConf.getSyVendorId()+":" +diameterGatewayConf.getSyApplicationId())){
				try{
					SyApplication syApplication = new SyApplication(groupFactory,diameterGatewayControllerContext,
							diameterGatewayConf.getSyVendorId(),diameterGatewayConf.getSyApplicationId());
					syApplication.init();
					registerListener(syApplication, appListeners);
				}catch(AppListenerInitializationFaildException ex){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in initializing Standard Sy interface With " +
								"App Id = " + diameterGatewayConf.getGyVendorId()  + ":" + diameterGatewayConf.getGyApplicationId() +
								" for gateway = "+ diameterGatewayConf.getName() +". Reason: " + ex.getMessage());
					LogManager.getLogger().trace(ex);
				}
			}
				
			// Standard Gy Interface
			if (appListeners.containsKey(diameterGatewayConf.getGyVendorId() + CommonConstants.COLON
							+ diameterGatewayConf.getGyApplicationId()) == false) {
				registerCustomGyApplication(appListeners, diameterGatewayConf);
			}
		}
		
		if(!appListeners.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Created Applications : ");
			StringBuilder stringBuilder = new StringBuilder();

			for(String peer : appListeners.keySet()){
				stringBuilder.append(peer + ",");
			}

			stringBuilder.delete(stringBuilder.length() -1, stringBuilder.length());

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, stringBuilder.toString());
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE , "No diameter application created from diameter gateway configuration");
		}
		
		
		return appListeners;
	}

	private void registerStandardGyApplication(Map<String, NetvertexApplication> appListeners) {
		try {
			GyApplication gyApplication = new GyApplication(
					diameterGatewayControllerContext, 
					Arrays.asList(new RequestTypeValidator(), new EventRequestValidator(), new LicenseLimitValidator(diameterGatewayControllerContext)),
					Arrays.asList(new PostReceivedScriptsPreProcessor(diameterGatewayControllerContext)),
					appHandlers.get(Application.CC).get(SupportedStandard.RELEASE_9));
			gyApplication.init();
			registerListener(gyApplication, appListeners);

		} catch (AppListenerInitializationFaildException e) {
			getLogger().error(MODULE, "Error in initializing Standard Gy interface With " +
					"App Id = " + ApplicationIdentifier.CC.getVendorId() + ":" + ApplicationIdentifier.CC.getApplicationId() +
					". Reason: " + e.getMessage());
			getLogger().trace(e);
		}
	}

	private void registerCustomGyApplication(Map<String, NetvertexApplication> appListeners,
			DiameterGatewayConfiguration diameterGatewayConf) {
		try {

			GyApplication gyApplication = new GyApplication(diameterGatewayControllerContext,
					Arrays.asList(new RequestTypeValidator(),
							new EventRequestValidator(),
							new LicenseLimitValidator(diameterGatewayControllerContext)),
					Arrays.asList(new PostReceivedScriptsPreProcessor(diameterGatewayControllerContext)),
					appHandlers.get(Application.CC).get(SupportedStandard.RELEASE_9),
					diameterGatewayConf.getGyVendorId(), diameterGatewayConf.getGyApplicationId());

			gyApplication.init();
			registerListener(gyApplication, appListeners);
		} catch (AppListenerInitializationFaildException ex) {
			getLogger().error(MODULE,
						"Error in initializing Standard Gy interface With " + "App Id = "
								+ diameterGatewayConf.getGyVendorId() + ":"
								+ diameterGatewayConf.getGyApplicationId() + " for gateway = "
								+ diameterGatewayConf.getName() + ". Reason: " + ex.getMessage());
			getLogger().trace(ex);
		}
	}

	private void registerListener(NetvertexBaseServerApplication applicationListener,
			Map<String, NetvertexApplication> applicationListeners) {
		for (ApplicationEnum applicationEnum : applicationListener.getApplicationEnum()) {
			applicationListeners.put(applicationEnum.getVendorId() + ":" +
					applicationEnum.getApplicationId() , applicationListener);
		}
	}
	
	private void registerListener(NetvertexBaseClientApplication applicationListener,
			Map<String, NetvertexApplication> applicationListeners) {
		for (ApplicationEnum applicationEnum : applicationListener.getApplicationEnum()) {
			applicationListeners.put(applicationEnum.getVendorId() + ":" +
					applicationEnum.getApplicationId() , applicationListener);
		}
	}

	private ILogger getLogger() {
		return LogManager.getLogger();
	}
	
}
