package com.elitecore.aaa.diameter.service.application.drivers;

import java.util.Hashtable;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.crestel.conf.CrestelDriverConf;
import com.elitecore.aaa.core.drivers.IEliteCrestelRatingDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.api.rating.ServiceLocatorEnvironment;
import com.elitecore.api.rating.factory.RatingDelegateFactory;
import com.elitecore.api.rating.interfaces.IRatingDelegate;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.driverx.BaseDriver;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.ratingapi.data.RequestPacketFactory;
import com.elitecore.ratingapi.util.IRequestParameters;
import com.elitecore.ratingapi.util.IResponseObject;

public class DiameterCrestelRatingDriver extends BaseDriver implements IEliteCrestelRatingDriver{
	
	private static String MODULE = "DIA-CRSTL-RTNG-DRVR";
	private ServiceLocatorEnvironment serviceLocatorEnvironement;
	private CrestelDriverConf driverConfiguration;
	private IRatingDelegate ratingDelegate ;
	
	public DiameterCrestelRatingDriver(ServerContext serverContext,String driverInstanceId) {
		super(serverContext);
		driverConfiguration = (CrestelDriverConf)((AAAServerContext)serverContext).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverInstanceId);
	}

	// For now all errors are considered as permanent
	@Override
	protected void initInternal() throws DriverInitializationFailedException {		
		TranslatorPolicyData policyData = ((AAAServerContext)getServerContext()).getServerConfiguration().getTranslationMappingConfiguration().getTranslatorPolicyDataMap().get(driverConfiguration.getTranslationMappingName());
		if(policyData==null){
			throw new DriverInitializationFailedException("TranslatorPolicyData not found for mapping: "+driverConfiguration.getTranslationMappingName());
		}
		
			Hashtable<?, ?> jndiPropertyMap = driverConfiguration.getJndiPropertyMap();
			if (jndiPropertyMap.get("java.naming.provider.url") == null){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "JNDI property: java.naming.provider.url must be configured for driver: " + getName());
				throw new DriverInitializationFailedException("JNDI property: java.naming.provider.url is not configured for driver: " + getName());
		}
			if (jndiPropertyMap.get("java.naming.factory.url.pkgs") == null){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "JNDI property: java.naming.factory.url.pkgs must be configured for driver: " + getName());
				throw new DriverInitializationFailedException("JNDI property: java.naming.factory.url.pkgs is not configured for driver: " + getName());
			}
			if (jndiPropertyMap.get("java.naming.factory.initial") == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "JNDI property: java.naming.factory.initial must be configured for driver: " + getName());
				throw new DriverInitializationFailedException("JNDI property: java.naming.factory.initial is not configured for driver: " +getName() );
			}
			try{
			serviceLocatorEnvironement = ServiceLocatorEnvironment.getInstance();
				serviceLocatorEnvironement.setServiceLocatorEnv(jndiPropertyMap);
			ratingDelegate = RatingDelegateFactory.getInstance().getRatingDelegate(((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterStackConfiguration().getMaxThreadPoolSize());
			}catch(Throwable e){
				LogManager.getLogger().error(MODULE, "Error while initializing Driver: " + getName()+" , Reason: "+e.getMessage()+" ,Provide valid JNDI properties");
		}		
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully: ");
		
		}
	
	@Override
	public int getType() {		
		return driverConfiguration.getDriverType().value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.CRESTEL_RATING_DRIVER.name();
	}

	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}

	@Override
	public String getName() {		
		return driverConfiguration.getDriverName();
	}

	@Override
	public void scan() {
		LogManager.getLogger().warn(MODULE, "Scanning Diameter Crestel Rating Driver: "+getName());
	}

	@Override
	public void handleRequest(ApplicationRequest request,ApplicationResponse response) throws TranslationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Handling request using : " + driverConfiguration.getDriverName());
		}		
		
			IRequestParameters requestParameters = RequestPacketFactory.getInstance().getRequestPacket();
			
		TranslatorParams params = new TranslatorParamsImpl(request.getDiameterRequest(), requestParameters);
		TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),params, TranslatorConstants.REQUEST_TRANSLATION);
			
		request.setParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING, params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
		
		if(!(Boolean.parseBoolean(String.valueOf(params.getParam(TranslatorConstants.DUMMY_MAPPING))))){
			IResponseObject responseObject = ratingDelegate.authorizationRequest(requestParameters);
			if(responseObject != null){
				LogManager.getLogger().debug(MODULE,"Response from Rating: \n"+responseObject.toString());
				String responseCode = String.valueOf(responseObject.getResponseCode());
				LogManager.getLogger().debug(MODULE,"Response from rating, Code : "+responseCode);
				responseObject.put(TranslatorConstants.RESULT_CODE, responseCode);
				TranslatorParams tempParams = new TranslatorParamsImpl(responseObject, response, request.getDiameterRequest(), requestParameters);
				tempParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
				TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),tempParams, TranslatorConstants.RESPONSE_TRANSLATION);
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Translated answer from Rating:" + response.getDiameterAnswer().toString());
			}else{
				IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
				avp.setInteger(ResultCode.DIAMETER_TOO_BUSY.code);
				response.addAVP(avp);
				LogManager.getLogger().warn(MODULE,"Response from rating is null, Sending DIAMETER_TOO_BUSY("+ ResultCode.DIAMETER_TOO_BUSY.code + ")");
			}
		}else{
			TranslatorParams tempParams = new TranslatorParamsImpl(null, response, request.getDiameterRequest(), null);
			tempParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
			tempParams.setParam(TranslatorConstants.DUMMY_MAPPING, true);
			TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),tempParams, TranslatorConstants.RESPONSE_TRANSLATION);
			
		}	
	}
}
