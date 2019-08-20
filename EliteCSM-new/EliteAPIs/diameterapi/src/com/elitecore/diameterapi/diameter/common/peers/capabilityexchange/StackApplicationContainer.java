package com.elitecore.diameterapi.diameter.common.peers.capabilityexchange;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;

public class StackApplicationContainer implements ApplicationContainer {

	private static final String MODULE = "STACK-APP-CONTAINER";
	private Set<ApplicationEnum> applications;
	private IDiameterStackContext stackContext;
	private ServiceTypes serviceType;
	
	public StackApplicationContainer(IDiameterStackContext stackContext,
			ServiceTypes serviceType) {
				this.stackContext = stackContext;
				this.serviceType = serviceType;
	}

	@Override
	public Set<ApplicationEnum> getApplications() {
		if(applications == null){
			applications = fetchApplicationsOfType(serviceType);
		}
		StringBuilder localAppLst = new StringBuilder("Local " + serviceType + " Applications:[");
		for(ApplicationEnum applicationEnum : applications){
			localAppLst.append(applicationEnum.getApplicationType());
			localAppLst.append('=');
			localAppLst.append(applicationEnum.getVendorId());
			localAppLst.append(':');
			localAppLst.append(applicationEnum.getApplicationId());
			localAppLst.append(',');
		}

		localAppLst.deleteCharAt(localAppLst.length() -1);
		localAppLst.append(']');

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, localAppLst.toString());
		}
		return applications;
	}

	@Override
	public Set<ApplicationEnum> getCommonApplications(Set<ApplicationEnum> remoteApplications) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Finding Common " + serviceType + " Applications");

		if(applications == null){
			applications = fetchApplicationsOfType(serviceType);
		}
		if(applications.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Local " + serviceType + " Applications Not Found");
			}
			return Collections.emptySet();
		}
		HashSet<ApplicationEnum> commonApplications = new HashSet<ApplicationEnum>();
		StringBuilder localAppLst = new StringBuilder("Local " + serviceType + " Applications:[");
		for(ApplicationEnum applicationEnum : applications){
			localAppLst.append(applicationEnum.getApplicationType());
			localAppLst.append('=');
			localAppLst.append(applicationEnum.getVendorId());
			localAppLst.append(':');
			localAppLst.append(applicationEnum.getApplicationId());
			localAppLst.append(',');
		}

		localAppLst.deleteCharAt(localAppLst.length() -1);
		localAppLst.append(']');

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, localAppLst.toString());
		}

		StringBuilder remoteAppLst = new StringBuilder("Remote " + serviceType + " Applications:[");
		StringBuilder commonAppLst = new StringBuilder("Common " + serviceType + " Applications:[");

		for(final ApplicationEnum remoteDiaAppEnum : remoteApplications){
			remoteAppLst.append(remoteDiaAppEnum.getApplicationType());
			remoteAppLst.append('=');
			remoteAppLst.append(remoteDiaAppEnum.getVendorId());
			remoteAppLst.append(':');
			remoteAppLst.append(remoteDiaAppEnum.getApplicationId());
			remoteAppLst.append(',');

			for(ApplicationEnum localDiaAppEnum : applications){	
				if(localDiaAppEnum.getVendorId() == remoteDiaAppEnum.getVendorId() && 
						localDiaAppEnum.getApplicationId() == remoteDiaAppEnum.getApplicationId()
						&& localDiaAppEnum.getApplicationType() == remoteDiaAppEnum.getApplicationType()){
					
					commonAppLst.append(remoteDiaAppEnum.getApplicationType());
					commonAppLst.append('=');
					commonAppLst.append(remoteDiaAppEnum.getVendorId());
					commonAppLst.append(':');
					commonAppLst.append(remoteDiaAppEnum.getApplicationId());
					commonAppLst.append(',');
					commonApplications.add(remoteDiaAppEnum);
					break;
				}
			}
		}
		if(commonApplications.isEmpty() == false){
			remoteAppLst.deleteCharAt(remoteAppLst.length() -1);	
		}
		if(commonApplications.isEmpty() == false){
			commonAppLst.deleteCharAt(commonAppLst.length() -1);	
		}
		remoteAppLst.append(']');
		commonAppLst.append(']');

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, remoteAppLst.toString());
		}

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, commonAppLst.toString());
		}
		return commonApplications;
	}
	
	private Set<ApplicationEnum> fetchApplicationsOfType(final ServiceTypes serviceType){
		
		Set<ApplicationEnum> selectedApplications = new HashSet<ApplicationEnum>();

		for(final ApplicationEnum appEnum : stackContext.getApplicationsIdentifiersList()){
			if(appEnum.getApplicationType() == serviceType){
				selectedApplications.add(appEnum);
			}else if(appEnum.getApplicationType() == ServiceTypes.BOTH){
				selectedApplications.add(new ApplicationEnum() {

					@Override
					public long getVendorId() {
						return appEnum.getVendorId();
					}

					@Override
					public ServiceTypes getApplicationType() {
						return serviceType;
					}

					@Override
					public long getApplicationId() {
						return appEnum.getApplicationId();
					}

					@Override
					public Application getApplication() {
						return appEnum.getApplication();
					}
					
					@Override
					public String toString() {
						return new StringBuilder()
						.append(getVendorId())
						.append(":")
						.append(getApplicationId())
						.append(" [").append(getApplication().getDisplayName()).append("]").toString();
					}
				});
			}
		}
		return selectedApplications;
	}
}
