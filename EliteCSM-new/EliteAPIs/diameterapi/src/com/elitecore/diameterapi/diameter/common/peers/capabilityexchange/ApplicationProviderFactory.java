package com.elitecore.diameterapi.diameter.common.peers.capabilityexchange;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;

public class ApplicationProviderFactory {

	private static final String MODULE = "APP-PROVIER-FACT";
	private static final String NO_APP_INDICATOR = "0x0";
	private static final String RELAY_APPICATION_ID = "0xFFFFFFFF";
	private IDiameterStackContext stackContext;
	
	private static ApplicationProviderFactory applicationProviderFactory;

	public ApplicationProviderFactory(IDiameterStackContext stackContext) {
		this.stackContext = stackContext;
	}
	
	public static ApplicationProviderFactory getInstance(IDiameterStackContext stackContext){
		if(applicationProviderFactory == null){
			applicationProviderFactory = new ApplicationProviderFactory(stackContext);
		}
		return applicationProviderFactory;
	}

	public ApplicationContainer createApplicationContainer(
			String exclusiveAppIDStr, ServiceTypes serviceType){
		
		ApplicationContainerType applicationContainerType = getApplicationContainerType(exclusiveAppIDStr);
		
		switch (applicationContainerType) {
		case EMPTY:
			return new EmptyApplicationContainer();
		case STACK:
			return new StackApplicationContainer(stackContext, serviceType);
		case EXCLUSIVE:
			return new ExclusiveApplicationContainer(buildExclusiveApplications(exclusiveAppIDStr, serviceType));
		}
		return null;
	}
	
	private ApplicationContainerType getApplicationContainerType(
			String exclusiveAppIDStr) {

		if(exclusiveAppIDStr == null || exclusiveAppIDStr.trim().length() == 0){
			return ApplicationContainerType.STACK;
		}
		if(exclusiveAppIDStr.contains(NO_APP_INDICATOR) 
				|| exclusiveAppIDStr.contains(NO_APP_INDICATOR.toUpperCase())){
			return ApplicationContainerType.EMPTY;
		}
		return ApplicationContainerType.EXCLUSIVE;
	}

	private Set<ApplicationEnum> buildExclusiveApplications(String exclusiveApplicationStr, ServiceTypes serviceType) {

		String [] exclusiveAppIds = ParserUtility.splitString(exclusiveApplicationStr, ',' , ';');
		Set<ApplicationEnum> exclusiveApplicationSet = new HashSet<ApplicationEnum>();
		for(int i = 0; i < exclusiveAppIds.length ; i++){

			String appId = exclusiveAppIds[i].trim();
			if(appId == null || appId.trim().length() == 0){
				continue;
			}
			appId = appId.trim();

			if(RELAY_APPICATION_ID.equalsIgnoreCase(appId)){
				exclusiveApplicationSet.add(DiameterUtility.createApplicationEnumStrictly(
						ApplicationIdentifier.RELAY.getApplicationId(), 
						ApplicationIdentifier.RELAY.getVendorId(), 
						serviceType));
			} else {
				
				String[] application = appId.split(":");
				final long applicationId;
				final long vendorId;
				try{
					if(application.length == 1){

						vendorId = ApplicationIdentifier.BASE.getVendorId();
						applicationId = Long.parseLong(application[0]);
					}else {

						vendorId = Long.parseLong(application[0]);
						applicationId = Long.parseLong(application[1]);
						if(vendorId < 0){
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Skipping " + serviceType + " Application-ID: " + appId + 
										", Reason: Invalid Vendor ID: " + vendorId);
							}
							continue;
						}
					}
					if(applicationId <= 0){
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, "Skipping " + serviceType + " Application-ID: " + appId + 
									", Reason: Invalid Application ID: " + applicationId);
						}
						continue;
					}
					exclusiveApplicationSet.add(DiameterUtility.createApplicationEnumStrictly(applicationId, vendorId, serviceType));
				}catch (NumberFormatException e){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Skipping " + serviceType + " Application-ID: " + appId + 
								", Reason: Unable to parse " + e.getMessage()); 
					}
				}
				
			}
		}
		return exclusiveApplicationSet;
	}
	
	private static class EmptyApplicationContainer implements ApplicationContainer {

		private static final String MODULE = "EMPTY-APP-CONTAINER";

		@Override
		public Set<ApplicationEnum> getApplications() {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "No Applications found."); 
			}
			return Collections.emptySet();
		}

		@Override
		public Set<ApplicationEnum> getCommonApplications(Set<ApplicationEnum> remoteApplications) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "No Applications found."); 
			}
			return Collections.emptySet();
		}

	}
	
	private enum ApplicationContainerType {
		STACK,
		EMPTY, 
		EXCLUSIVE,
		;
	}

}
