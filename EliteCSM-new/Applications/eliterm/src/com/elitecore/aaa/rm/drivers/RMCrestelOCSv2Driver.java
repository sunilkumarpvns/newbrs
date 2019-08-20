package com.elitecore.aaa.rm.drivers;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.conf.impl.RMServerConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.crestel.conf.impl.CrestelDriverConfImpl;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.rm.drivers.conf.impl.RMCrestelOCSv2DriverConfImpl;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.BaseDriver;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.crestelocs.core.common.packet.ChargingPacketFactory;
import com.elitecore.crestelocs.fw.attribute.IChargingAttribute;
import com.elitecore.crestelocs.fw.packet.IChargingPacket;
import com.elitecore.crestelocs.protocols.rmi.client.ChargingDelegateFactory;
import com.elitecore.crestelocs.protocols.rmi.client.IChargingDelegate;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;

public class RMCrestelOCSv2Driver extends BaseDriver implements RMChargingDriver {
	
	
	private static String MODULE = "CRESTEL-OCSV2--DRVR";
	private RMCrestelOCSv2DriverConfImpl driverConfiguration;
	private IChargingDelegate chargingDelegate;
	private RMCrestelOCSV2DriverStatistics driverStatistics;

	public RMCrestelOCSv2Driver(AAAServerContext serverContext,RMCrestelOCSv2DriverConfImpl driverConfiguration) {
		super(serverContext);
		this.driverConfiguration = driverConfiguration;
	}
	
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		TranslatorPolicyData policyData = ((AAAServerContext)getServerContext()).getServerConfiguration().getTranslationMappingConfiguration().getTranslatorPolicyData(driverConfiguration.getTranslationMappingName());
		if(policyData==null){
			throw new DriverInitializationFailedException("TranslatorPolicyData not found for mapping: "+driverConfiguration.getTranslationMappingName());
		}
		
				Hashtable<String,String> jndiPropertyMap = driverConfiguration.getJndiPropertyMap();
				if (jndiPropertyMap.get(CrestelDriverConfImpl.PROVIDER_URL) == null){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "JNDI property:"+ CrestelDriverConfImpl.PROVIDER_URL+" must be configured for driver: " + getName());
					throw new DriverInitializationFailedException("JNDI property: java.naming.provider.url is not configured for driver: " + getName());
				}
				if (jndiPropertyMap.get(CrestelDriverConfImpl.FACTORY_URL_PKGS) == null){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "JNDI property: "+ CrestelDriverConfImpl.FACTORY_URL_PKGS+" must be configured for driver: " + getName());
					throw new DriverInitializationFailedException("JNDI property: java.naming.factory.url.pkgs is not configured for driver: " + getName());
				}
				if (jndiPropertyMap.get(CrestelDriverConfImpl.FACTORY_INITIAL) == null) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "JNDI property: "+CrestelDriverConfImpl.FACTORY_INITIAL+" must be configured for driver: " + getName());
					throw new DriverInitializationFailedException("JNDI property: java.naming.factory.initial is not configured for driver: " +getName() );
				}
			try{
				
				chargingDelegate = ChargingDelegateFactory.getInstance().getChargingDelegate(((RMServerConfigurationImpl)((AAAServerContext)getServerContext()).getServerConfiguration()).getRmChargingServiceConfiguration().maxThreadPoolSize(),jndiPropertyMap,new com.elitecore.crestelocs.fw.logger.ILogger(){

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
				LogManager.getLogger().error(MODULE, "Error while initializing Driver: " + getName()+" , Reason: "+e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
				throw new DriverInitializationFailedException(e);
			}
			LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully: ");
		}		

	@Override
	public int getType() {
		return driverConfiguration.getDriverType().value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RM_CRESTEL_OCSV2_DRIVER.name();
	}

	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}

	@Override
	public void handleRequest(ServiceRequest request, ServiceResponse response)
			throws DriverProcessFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Handling request using : " + driverConfiguration.getDriverName());
		}
		incrementTotalRequests();
		try{
				IChargingPacket ocsV2RequestPacket = ChargingPacketFactory.getInstance().getChargingPacket();
				TranslatorParams params = new TranslatorParamsImpl(request, ocsV2RequestPacket);
				TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),params, TranslatorConstants.REQUEST_TRANSLATION);
				request.setParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING, params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
				
				if(!(Boolean.parseBoolean(String.valueOf(params.getParam(TranslatorConstants.DUMMY_MAPPING))))){					
					IChargingPacket ocsV2ResponsePacket=chargingDelegate.onlineChargingService(ocsV2RequestPacket);

					if(ocsV2ResponsePacket!=null){
						IChargingAttribute responseCode = ocsV2ResponsePacket.getAttribute(CrestelDriverConfImpl.RESPONCE_CODE_ATTRIBUTE_NAME);

						if(responseCode!=null && responseCode.getIntValue()!=CrestelDriverConfImpl.RESPONCE_CODE_SUCCESS){
							((RadServiceResponse)response).setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
						}

						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE,"Response from Crestel OCSv2: "+ocsV2ResponsePacket.toString());

						TranslatorParams tempParam = new TranslatorParamsImpl(ocsV2ResponsePacket,response,request, ocsV2RequestPacket);
						tempParam.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
						TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),tempParam, TranslatorConstants.RESPONSE_TRANSLATION);
						incrementTotalSuccess();
					}else{
						LogManager.getLogger().error(MODULE, "Response from rating is null");
						incrementTotalErrorResponses();
						throw new DriverProcessFailedException("Response from OCSv2 is null");		
					}
				}else{
					TranslatorParams tempParams = new TranslatorParamsImpl(null, response, request, null);
					tempParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
					tempParams.setParam(TranslatorConstants.DUMMY_MAPPING, true);
					try {
						TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(), tempParams, TranslatorConstants.RESPONSE_TRANSLATION);
						incrementTotalSuccess();
					} catch (TranslationFailedException e) {
						driverStatistics.incrTranslationFailed();
						throw new UnhandledTransitionException(e.getMessage());
					}
				}
			
		}catch(TranslationFailedException e){
			driverStatistics.incrTranslationFailed();
			LogManager.getLogger().error(MODULE, "Translation failed reason: " + e.getMessage());
			throw new DriverProcessFailedException(MODULE,e);
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

	@Override
	protected ESIStatisticsImpl createESIStatistics() {
		driverStatistics = new RMCrestelOCSV2DriverStatistics();
		return driverStatistics;
	}

	/**
	 * This class will be used to maintain the 
	 * custom counter for CrestelOCSV2 Driver.
	 * 
	 * @author elitecore
	 */
	private class RMCrestelOCSV2DriverStatistics extends ESIStatisticsImpl{
		
		private AtomicLong translationFailed = new AtomicLong(0);
		
		public long getTranslationFailed() {
			return translationFailed.get();
		}
		
		public void incrTranslationFailed() {
			this.translationFailed.incrementAndGet();
		}
		
		@Override
		public String toString() {
			
			int[] width= {35,30};
			String[] header={};
			TableFormatter esiStatsTableFormatter = new TableFormatter(header, width,TableFormatter.CENTER);
			esiStatsTableFormatter.addRecord(new String[]{"Total Translation Failed",":"+String.valueOf(getTranslationFailed())});
			
			return super.toString() + esiStatsTableFormatter.getFormattedValues();
		}
	}
}
