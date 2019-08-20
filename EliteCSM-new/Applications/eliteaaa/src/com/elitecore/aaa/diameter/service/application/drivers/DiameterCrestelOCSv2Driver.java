package com.elitecore.aaa.diameter.service.application.drivers;

import java.util.Hashtable;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.crestel.conf.CrestelDriverConf;
import com.elitecore.aaa.core.crestel.conf.impl.CrestelDriverConfImpl;
import com.elitecore.aaa.core.drivers.IEliteCrestelRatingDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.driverx.BaseDriver;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.crestelocs.core.common.packet.ChargingPacketFactory;
import com.elitecore.crestelocs.fw.attribute.IChargingAttribute;
import com.elitecore.crestelocs.fw.packet.IChargingPacket;
import com.elitecore.crestelocs.protocols.rmi.client.ChargingDelegateFactory;
import com.elitecore.crestelocs.protocols.rmi.client.IChargingDelegate;
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

public class DiameterCrestelOCSv2Driver extends BaseDriver implements IEliteCrestelRatingDriver{
	private static String MODULE = "DIA-CRSTL-OCSv2-DRVR";
	
	private CrestelDriverConf driverConfiguration;
	private IChargingDelegate chargingDelegate;
	
	public DiameterCrestelOCSv2Driver(ServerContext serverContext,String driverInstanceId) {
		super(serverContext);
		driverConfiguration = (CrestelDriverConf)((AAAServerContext)serverContext).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverInstanceId);
	}

	@Override
	public int getType() {
		return driverConfiguration.getDriverType().value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.DIAMETER_CRESTEL_OCSv2_DRIVER.name();
	}
	
	// For now all errors are considered as permanent
	@Override
	protected void initInternal() throws DriverInitializationFailedException {		
		TranslatorPolicyData policyData = ((AAAServerContext)getServerContext()).getServerConfiguration().getTranslationMappingConfiguration().getTranslatorPolicyData(driverConfiguration.getTranslationMappingName());
		if(policyData==null){
			throw new DriverInitializationFailedException("TranslatorPolicyData not found for mapping: "+driverConfiguration.getTranslationMappingName());
		}
		 
				Hashtable<String,String> jndiPropertyMap = driverConfiguration.getJndiPropertyMap();
				if (jndiPropertyMap.get(CrestelDriverConfImpl.PROVIDER_URL) == null){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "JNDI property: "+ CrestelDriverConfImpl.PROVIDER_URL+" must be configured for driver: " + getName());
					throw new DriverInitializationFailedException("JNDI property: "+ CrestelDriverConfImpl.PROVIDER_URL+" is not configured for driver: " + getName());
				}
				if (jndiPropertyMap.get(CrestelDriverConfImpl.FACTORY_URL_PKGS) == null){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "JNDI property: "+ CrestelDriverConfImpl.FACTORY_URL_PKGS+" must be configured for driver: " + getName());
					throw new DriverInitializationFailedException("JNDI property: "+ CrestelDriverConfImpl.FACTORY_URL_PKGS+" is not configured for driver: " + getName());
				}
				if (jndiPropertyMap.get(CrestelDriverConfImpl.FACTORY_INITIAL) == null) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "JNDI property: "+CrestelDriverConfImpl.FACTORY_INITIAL+" must be configured for driver: " + getName());
					throw new DriverInitializationFailedException("JNDI property: "+CrestelDriverConfImpl.FACTORY_INITIAL+" is not configured for driver: " +getName() );
				}

			try{
				
				chargingDelegate = ChargingDelegateFactory.getInstance().getChargingDelegate(((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterStackConfiguration().getMaxThreadPoolSize(),jndiPropertyMap,new com.elitecore.crestelocs.fw.logger.ILogger(){

					@Override
					public void debug(String arg0) {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, arg0);
					}

					@Override
					public void error(String arg0) {
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, arg0);
					}

					@Override
					public void fatal(String arg0) {
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, arg0);
					}

					@Override
					public int getLogLevel() {
						return LogManager.getLogger().getCurrentLogLevel();
					}

					@Override
					public void info(String arg0) {
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, arg0);
					}

					@Override
					public boolean isDebugEnabled() {
						return LogManager.getLogger().isLogLevel(LogLevel.DEBUG);
					}

					@Override
					public boolean isErrorEnabled() {
						return LogManager.getLogger().isLogLevel(LogLevel.ERROR);
					}

					@Override
					public boolean isFatalEnabled() {
						return LogManager.getLogger().isLogLevel(LogLevel.ERROR);
					}

					@Override
					public boolean isInfoEnabled() {
						return LogManager.getLogger().isLogLevel(LogLevel.INFO);
					}

					@Override
					public boolean isTraceEnabled() {
						return LogManager.getLogger().isLogLevel(LogLevel.TRACE);
					}

					@Override
					public boolean isWarnEnabled() {
						return LogManager.getLogger().isLogLevel(LogLevel.WARN);
					}

					@Override
					public void trace(String arg0) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, arg0);
					}

					@Override
					public void trace(Throwable arg0) {
						LogManager.getLogger().trace(MODULE, arg0);						
					}

					@Override
					public void warn(String arg0) {
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, arg0);
					}

					@Override
					public void trace(String arg0, Throwable arg1) {
						LogManager.getLogger().trace(arg0, arg1);
					}
					
				});
				if(chargingDelegate==null || !chargingDelegate.isAlive())
					throw new DriverInitializationFailedException("Invalid JNDI properties: "+jndiPropertyMap+" configured, provide proper JNDI properties");
				
			}catch(Throwable e){
				LogManager.getLogger().error(MODULE, "Error while initializing Driver: " + getName()+" , Reason: "+e.getMessage()+" ,Provide valid JNDI properties");
				LogManager.getLogger().trace(MODULE,e);
				throw new DriverInitializationFailedException(e);
			}
			
			LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully: ");
			
		
		}

	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}

	@Override
	public void handleRequest(ApplicationRequest request,ApplicationResponse response) throws TranslationFailedException {

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Handling request using : " + driverConfiguration.getDriverName());
		}		
		
			IChargingPacket ocsV2RequestPacket = ChargingPacketFactory.getInstance().getChargingPacket();
		TranslatorParams params = new TranslatorParamsImpl(request.getDiameterRequest(),ocsV2RequestPacket);
		TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),params, TranslatorConstants.REQUEST_TRANSLATION);
			
		request.setParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING, params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
			
		if(!(Boolean.parseBoolean(String.valueOf(params.getParam(TranslatorConstants.DUMMY_MAPPING))))){
			IChargingPacket ocsV2ResponsePacket=chargingDelegate.onlineChargingService(ocsV2RequestPacket);
			if(ocsV2ResponsePacket != null){
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE,"Response from OCSv2: \n"+ocsV2ResponsePacket.toString());
				
				IChargingAttribute responseCode = ocsV2ResponsePacket.getAttribute(CrestelDriverConfImpl.RESPONCE_CODE_ATTRIBUTE_NAME);
				
				if(responseCode!=null && responseCode.getIntValue()!=CrestelDriverConfImpl.RESPONCE_CODE_SUCCESS){
					IDiameterAVP resultCodeAVP = response.getAVP(DiameterAVPConstants.RESULT_CODE);
					if(resultCodeAVP==null){
						resultCodeAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.RESULT_CODE);
						if(resultCodeAVP!=null){
							resultCodeAVP.setInteger(ResultCode.DIAMETER_RATING_FAILED.code);
							response.addAVP(resultCodeAVP);
						}	
					}else {
						resultCodeAVP.setInteger(ResultCode.DIAMETER_RATING_FAILED.code);
					}
				}
				TranslatorParams param = new TranslatorParamsImpl(ocsV2ResponsePacket,response, request.getDiameterRequest(), ocsV2RequestPacket);
				param.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING,request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
				TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),param, TranslatorConstants.RESPONSE_TRANSLATION);
			}else{
				IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
				avp.setInteger(ResultCode.DIAMETER_TOO_BUSY.code);
				response.addAVP(avp);
				LogManager.getLogger().warn(MODULE,"Response from OCSv2 is null, Sending DIAMETER_TOO_BUSY("+ ResultCode.DIAMETER_TOO_BUSY.code + ")");
			}
		}else{
			TranslatorParams tempParam = new TranslatorParamsImpl(null,response, request.getDiameterRequest(), null);
			tempParam.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING,request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
			tempParam.setParam(TranslatorConstants.DUMMY_MAPPING, true);
			TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),tempParam, TranslatorConstants.RESPONSE_TRANSLATION);
		}	
	}

	@Override
	public void scan() {
		if(!this.chargingDelegate.isAlive())
			markDead();
	}

	@Override
	public String getName() {
		return driverConfiguration.getDriverName();
	}

}
