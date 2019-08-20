package com.elitecore.aaa.diameter.commons;

import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterTGPPServerConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.DiameterCCServiceConfigurable;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.listener.AAAServerApplication;
import com.elitecore.aaa.diameter.service.application.listener.CCApplication;
import com.elitecore.aaa.diameter.service.application.listener.CCApplicationListener;
import com.elitecore.aaa.diameter.service.application.listener.EapApplicationListener;
import com.elitecore.aaa.diameter.service.application.listener.NasApplicationListener;
import com.elitecore.aaa.diameter.service.application.listener.TGPPServerApplicationListener;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.stack.IStackContext;

public class EliteDiameterAppFactory {

	public static AAAServerApplication getEliteApplicationListener(String serviceIdentifier,
			DiameterServiceContext serviceContext, IStackContext stackContext,
			DiameterServiceConfigurable diameterServiceConfiguration,
			@Nullable DiameterSessionManager diameterSessionManager,
			SessionFactoryManager sessionFactoryManager) { 
			

		if (AAAServerConstants.DIA_NAS_SERVICE_ID.equals(serviceIdentifier)) {
			return createNasListener(serviceContext, stackContext, (DiameterNasServiceConfigurable) diameterServiceConfiguration,
					diameterSessionManager, sessionFactoryManager);
		} else if (AAAServerConstants.DIA_EAP_SERVICE_ID.equals(serviceIdentifier)) {
			return createEapListener(serviceContext, stackContext, (DiameterEAPServiceConfigurable) diameterServiceConfiguration,
					diameterSessionManager, sessionFactoryManager);
		} else if (AAAServerConstants.DIA_CC_SERVICE_ID.equals(serviceIdentifier)) {
			return createCCListener(serviceContext, stackContext, (DiameterCCServiceConfigurable) diameterServiceConfiguration,
					diameterSessionManager, sessionFactoryManager);
		} else if (AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID.equals(serviceIdentifier)) {
			return createTGPPServerListener(serviceContext, stackContext, (DiameterTGPPServerConfigurable) diameterServiceConfiguration,
					diameterSessionManager, sessionFactoryManager);
		}
		return null;
	}

	private static AAAServerApplication createTGPPServerListener(DiameterServiceContext serviceContext,
			IStackContext stackContext, final DiameterTGPPServerConfigurable diameterServiceConfiguration,
			DiameterSessionManager diameterSessionManager, SessionFactoryManager sessionFactoryManager) {
		return new TGPPServerApplicationListener(serviceContext, stackContext, 
				diameterServiceConfiguration, diameterSessionManager, sessionFactoryManager);
	}

	private static AAAServerApplication createCCListener(DiameterServiceContext serviceContext,
			IStackContext stackContext, DiameterCCServiceConfigurable diameterServiceConfiguration,
			DiameterSessionManager diameterSessionManager, SessionFactoryManager sessionFactoryManager) {
		
		if (diameterServiceConfiguration.getIsEnabled())
			return new CCApplicationListener(serviceContext, stackContext, 
					diameterServiceConfiguration, diameterSessionManager, sessionFactoryManager);
		else
			return new CCApplication(serviceContext, stackContext, diameterServiceConfiguration,
					diameterSessionManager, sessionFactoryManager);
	}

	private static AAAServerApplication createEapListener(DiameterServiceContext serviceContext,
			IStackContext stackContext, DiameterEAPServiceConfigurable diameterServiceConfiguration,
			DiameterSessionManager diameterSessionManager, SessionFactoryManager sessionFactoryManager) {
		
		return new EapApplicationListener(serviceContext, stackContext, 
				diameterServiceConfiguration, diameterSessionManager, sessionFactoryManager);
	}

	private static AAAServerApplication createNasListener(DiameterServiceContext serviceContext,
			IStackContext stackContext, DiameterNasServiceConfigurable diameterServiceConfiguration,
			DiameterSessionManager diameterSessionManager, SessionFactoryManager sessionFactoryManager) {
		return new NasApplicationListener(serviceContext, stackContext, 
				diameterServiceConfiguration, diameterSessionManager, sessionFactoryManager);
	}

}
