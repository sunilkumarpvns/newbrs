package com.elitecore.aaa.rm.drivers;


import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.conf.impl.RMServerConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.crestel.conf.CrestelDriverConf;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.api.rating.ServiceLocatorEnvironment;
import com.elitecore.api.rating.factory.RatingDelegateFactory;
import com.elitecore.api.rating.interfaces.IRatingDelegate;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.BaseDriver;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.ratingapi.data.RequestPacketFactory;
import com.elitecore.ratingapi.util.IRequestParameters;
import com.elitecore.ratingapi.util.IResponseObject;


/***
 * 
 * This class is used for RADIUS (RM) to Crestel Rating call.
 * 
 * @author jatin
 *
 */
public class RMCrestelChargingDriver extends BaseDriver implements
		RMChargingDriver {
	private static String MODULE = "RM-CRSTL-CHRGN-DRVR";
	private ServiceLocatorEnvironment serviceLocatorEnvironement;
	private CrestelDriverConf driverConfiguration;
	private IRatingDelegate ratingDelegate ;
	
	public static final String AUTH_METHOD="AUTH_METHOD";
	public static final String REAUTH_METHOD="REAUTH_METHOD";
	public static final String ACCT_METHOD="ACCT_METHOD";
	
	private RMCrestelChargingDriverStatistics driverStatistics;
	
	public RMCrestelChargingDriver(AAAServerContext serverContext,CrestelDriverConf rmCrestelChargingDriverConf) {
		super(serverContext);
		driverConfiguration = rmCrestelChargingDriverConf;
	}
	
	@Override
	protected void initInternal() throws DriverInitializationFailedException {		
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
				ratingDelegate = RatingDelegateFactory.getInstance().getRatingDelegate(((RMServerConfigurationImpl)((AAAServerContext)getServerContext()).getServerConfiguration()).getRmChargingServiceConfiguration().maxThreadPoolSize());
				LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully: ");
			}catch(Throwable e){
				LogManager.getLogger().error(MODULE, "Error while initializing Driver: " + getName()+" , Reason: "+e.getMessage()+" ,Provide valid JNDI properties");
				LogManager.getLogger().trace(MODULE,e);
				throw new DriverInitializationFailedException(e);
			}
		}		
	
	@Override
	public void handleRequest(ServiceRequest request, ServiceResponse response)
			throws DriverProcessFailedException {
		incrementTotalRequests();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Handling request using : " + driverConfiguration.getDriverName());
		}		
		try{
				
				double startTime = 0;
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					startTime = System.nanoTime();
				}
				
				IRequestParameters requestParameters = RequestPacketFactory.getInstance().getRequestPacket();
				TranslatorParams params = new TranslatorParamsImpl(request, requestParameters);
				
				TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),params, TranslatorConstants.REQUEST_TRANSLATION);
				request.setParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING, params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
				
				if(!(Boolean.parseBoolean(String.valueOf(params.getParam(TranslatorConstants.DUMMY_MAPPING))))){
					String methodName = (String)params.getParam(AAATranslatorConstants.RATING_METHOD_NAME);

					IResponseObject responseObject = null;

					if(AUTH_METHOD.equalsIgnoreCase(methodName)){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Calling authorizationRequest in Rating");
						responseObject = ratingDelegate.authorizationRequest(requestParameters);						
					}else if(ACCT_METHOD.equalsIgnoreCase(methodName)){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Calling accountingRequest in Rating");
						responseObject = ratingDelegate.accountingRequest(requestParameters);						
					}else if(REAUTH_METHOD.equalsIgnoreCase(methodName)){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Calling reauthorizationRequest in Rating");
						responseObject = ratingDelegate.reAuthorizationRequest(requestParameters);								
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "No Appropriate Calling Method Selected: (Method name: " + methodName + ")");
					}				

					driverStatistics.incrementMethodSpecificCounters(methodName);

					if(responseObject != null){
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							double endTime = (System.nanoTime() - startTime);
							LogManager.getLogger().trace(MODULE,"Rating Response Time : " + endTime + " nenoseconds.");
						}	

						LogManager.getLogger().debug(MODULE,"Response from Rating: \n"+responseObject.toString());
						String responseCode = String.valueOf(responseObject.getResponseCode());
						LogManager.getLogger().debug(MODULE,"Response from rating, Code : "+responseCode);
						responseObject.put(AAATranslatorConstants.RESULT_CODE, responseCode);

						TranslatorParams tempParams = new TranslatorParamsImpl(responseObject, response, request, requestParameters);
						tempParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));

						TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),tempParams, TranslatorConstants.RESPONSE_TRANSLATION);

						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Translated answer from Rating:" + response.toString());
						incrementTotalSuccess();
						driverStatistics.incrementMethodSpecificSuccessCounters(methodName, responseObject);
					}else{
						LogManager.getLogger().error(MODULE, "Response from rating is null");
						incrementTotalErrorResponses();
						driverStatistics.incrementMethodSpecificErrorCounters(methodName);
						throw new DriverProcessFailedException("Response from rating is null.");		
					}

				}else{
					TranslatorParams tempParam = new TranslatorParamsImpl(null, response, request, null);
					tempParam.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, request.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
					tempParam.setParam(TranslatorConstants.DUMMY_MAPPING, true);
					TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(),tempParam, TranslatorConstants.RESPONSE_TRANSLATION);
					incrementTotalSuccess();
				}
		}catch(TranslationFailedException e){
			driverStatistics.incrTranslationFailed();
			LogManager.getLogger().error(MODULE, "Translation failed reason: " + e.getMessage());
			throw new DriverProcessFailedException(MODULE,e);
		}
			
	}	
		
	@Override
	public int getType() {		
		return driverConfiguration.getDriverType().value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RM_CRESTEL_CHARGING_DRIVER.name();
	}

	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}

	@Override
	public void scan() {
		LogManager.getLogger().warn(MODULE, "Scanning Crestel Rating Driver :"+getName());
	}

	@Override
	public String getName() {
		return driverConfiguration.getDriverName();
	}

	@Override
	protected ESIStatisticsImpl createESIStatistics() {
		driverStatistics = new RMCrestelChargingDriverStatistics();
		return driverStatistics;
	}

	/**
	 * This class will be used to maintain the 
	 * custom counter for CrestelCharging Driver.
	 * 
	 * @author elitecore
	 */
	private class RMCrestelChargingDriverStatistics extends ESIStatisticsImpl{
		
		private AtomicLong authMethodRequestRx = new AtomicLong(0);
		private AtomicLong acctMethodRequestRx = new AtomicLong(0);
		private AtomicLong reAuthMethodRequestRx = new AtomicLong(0);
		
		private AtomicLong authMethodSuccessResponseTx = new AtomicLong(0);
		private AtomicLong acctMethodSuccessResponseTx = new AtomicLong(0);
		private AtomicLong reAuthMethodSuccessResponseTx = new AtomicLong(0);
		
		private AtomicLong authMethodErrorResponseTx = new AtomicLong(0);
		private AtomicLong acctMethodErrorResponseTx = new AtomicLong(0);
		private AtomicLong reAuthMethodErrorResponseTx = new AtomicLong(0);
		
		private AtomicLong translationFailed = new AtomicLong(0);
		public void incrementMethodSpecificCounters(String methodName){
			if(AUTH_METHOD.equalsIgnoreCase(methodName)){
				authMethodRequestRx.incrementAndGet();
			}else if(ACCT_METHOD.equalsIgnoreCase(methodName)){
				acctMethodRequestRx.incrementAndGet();
			}else if(REAUTH_METHOD.equalsIgnoreCase(methodName)){
				reAuthMethodRequestRx.incrementAndGet();
			}
		}

		public void incrementMethodSpecificSuccessCounters(String methodName,IResponseObject responseObject){
			if(AUTH_METHOD.equalsIgnoreCase(methodName)){
				authMethodSuccessResponseTx.incrementAndGet();
			}else if(ACCT_METHOD.equalsIgnoreCase(methodName)){
				acctMethodSuccessResponseTx.incrementAndGet();
			}else if(REAUTH_METHOD.equalsIgnoreCase(methodName)){
				reAuthMethodSuccessResponseTx.incrementAndGet();
			}
		}
		
		public void incrementMethodSpecificErrorCounters(String methodName){
			if(AUTH_METHOD.equalsIgnoreCase(methodName)){
				authMethodErrorResponseTx.incrementAndGet();
			}else if(ACCT_METHOD.equalsIgnoreCase(methodName)){
				acctMethodErrorResponseTx.incrementAndGet();
			}else if(REAUTH_METHOD.equalsIgnoreCase(methodName)){
				reAuthMethodErrorResponseTx.incrementAndGet();
			}
		}
		
		public long getAuthMethodRequestRx() {
			return authMethodRequestRx.get();
		}
		
		public long getAcctMethodRequestRx() {
			return acctMethodRequestRx.get();
		}
		
		public long getReAuthMethodRequestRx() {
			return reAuthMethodRequestRx.get();
		}

		public long getAuthMethodErrorResponseTx() {
			return authMethodErrorResponseTx.get();
		}
		
		public long getAcctMethodErrorResponseTx() {
			return acctMethodErrorResponseTx.get();
		}
		
		public long getReAuthMethodErrorResponseTx() {
			return reAuthMethodErrorResponseTx.get();
		}
		
		public long getAuthMethodSuccessResponseTx() {
			return authMethodSuccessResponseTx.get();
		}
		
		public long getAcctMethodSuccessResponseTx() {
			return acctMethodSuccessResponseTx.get();
		}
		
		public long getReAuthMethodSuccessResponseTx() {
			return reAuthMethodSuccessResponseTx.get();
		}

		public long getTranslationFailed() {
			return translationFailed.get();
		}
		
		public void incrTranslationFailed() {
			translationFailed.incrementAndGet();
		}
		
		@Override
		public String toString() {
			
			final int[] alignment  = new int[]{TableFormatter.LEFT,TableFormatter.RIGHT,TableFormatter.RIGHT,TableFormatter.RIGHT};
			TableFormatter esiStatsTableFormatter = new TableFormatter(new String[]{}, new int[]{35,30},TableFormatter.NO_BORDERS);
			esiStatsTableFormatter.addRecord(new String[]{"Total Translation Failed",":"+String.valueOf(getTranslationFailed())});
			esiStatsTableFormatter.addNewLine();
			
			int[] width= {13,16,16,16};
			
			String[] header={"TYPE"
					,"Request Received"
					,"Success Response"
					,"Error Response"};
			
			TableFormatter ratingMethodsStatsTableFormatter = new TableFormatter(header, width,TableFormatter.ONLY_HEADER_LINE);
			
			ratingMethodsStatsTableFormatter.addRecord(new String[]{"Auth-Method",String.valueOf(getAuthMethodRequestRx()),
										String.valueOf(getAcctMethodRequestRx()),String.valueOf(getReAuthMethodRequestRx())},alignment);
			
			ratingMethodsStatsTableFormatter.addRecord(new String[]{"Acct-Method",String.valueOf(getAuthMethodSuccessResponseTx()),
										String.valueOf(getAcctMethodSuccessResponseTx()),String.valueOf(getReAuthMethodSuccessResponseTx())},alignment);
			
			ratingMethodsStatsTableFormatter.addRecord(new String[]{"ReAuth-Method",String.valueOf(getAuthMethodErrorResponseTx()),
										String.valueOf(getAcctMethodErrorResponseTx()),String.valueOf(getReAuthMethodErrorResponseTx())},alignment);
			
			return super.toString() + esiStatsTableFormatter.getFormattedValues() + ratingMethodsStatsTableFormatter.getFormattedValues();
		}
	}
}
