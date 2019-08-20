package com.elitecore.aaa.core.subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.impl.UpdateIdentityParameters;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.AuthCommunicatorGroup;
import com.elitecore.aaa.core.drivers.AuthCommunicatorGroupImpl;
import com.elitecore.aaa.core.drivers.CacheCommunicatorGroup;
import com.elitecore.aaa.core.drivers.CacheCommunicatorGroupImpl;
import com.elitecore.aaa.core.drivers.ChangeCaseStrategy;
import com.elitecore.aaa.core.drivers.IEliteAuthDriver;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy;
import com.elitecore.aaa.core.subscriber.conf.SubscriberProfileRepositoryDetails;
import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.commons.plugins.script.NullDriverScript;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.LoadBalancerType;

/**
 * 
 * @author narendra.pathai
 *
 * @param <T>
 * @param <V>
 */
public abstract class SubscriberProfileRepository<T extends ServiceRequest, V extends ServiceResponse>
implements IAccountInfoProvider<T, V> {

	private final SubscriberProfileRepositoryDetails data;

	private AuthCommunicatorGroup primaryGroup;
	private Optional<? extends AuthCommunicatorGroup> optionalSecondaryGroup;
	private List<AuthCommunicatorGroup> additionalDriverGroupList;
	private Map<String, CacheCommunicatorGroupImpl> cacheDriverMap;
	private final ServerContext serverContext;
	private StripUserIdentityStrategy stripStrategy;
	private ChangeCaseStrategy caseStrategy;
	private DriverScript driverScript;
	
	public SubscriberProfileRepository(final ServerContext serverContext, SubscriberProfileRepositoryDetails data){
		this.data = data;
		this.serverContext = serverContext;
		primaryGroup = new AuthCommunicatorGroupImpl(LoadBalancerType.ROUND_ROBIN);
		optionalSecondaryGroup = Optional.absent();
		cacheDriverMap = new HashMap<String,CacheCommunicatorGroupImpl>();
		additionalDriverGroupList = new ArrayList<AuthCommunicatorGroup>();
		driverScript = new NullDriverScript(new ScriptContext() {
			@Override
			public ServerContext getServerContext() {
				return serverContext;
			}
		});
	}

	public void init() throws InitializationFailedException {
		stripStrategy = StripUserIdentityStrategy.get(data.getUpdateIdentity().getStripIdentity());
		caseStrategy = ChangeCaseStrategy.get(data.getUpdateIdentity().getCase());
		initializeCommunicatorGroup();
		initDriverScript();
	}

	private void initDriverScript() {
		String scriptName = data.getDriverDetails().getDriverScript();
		if (Strings.isNullOrBlank(scriptName) == false) {
			try {
				driverScript = serverContext.getExternalScriptsManager().getScript(scriptName, DriverScript.class);
			} catch (IllegalArgumentException ex) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(getModuleName(), "Cannot use driver script for repository. Reason: " + ex.getMessage());
				}

				LogManager.getLogger().trace(ex);
			}
		}
	}

	private void initializeCommunicatorGroup() throws InitializationFailedException {
		IEliteAuthDriver mainDriver = null;
		IEliteAuthDriver secondaryDriver = null;
		IEliteAuthDriver cacheDriver = null;
		IEliteAuthDriver additionalDriver = null;

		int failedDriverCount = 0;
		List<PrimaryDriverDetail> primaryDriverDetails = data.getDriverDetails().getPrimaryDriverGroup();
		for(PrimaryDriverDetail driverDetail : primaryDriverDetails){

			mainDriver = getDriver(driverDetail.getDriverInstanceId());
			if (mainDriver != null) {
				addPrimaryCommunicator(mainDriver,driverDetail.getWeightage());
			} else {
				failedDriverCount++;
				LogManager.getLogger().warn(getModuleName(),"Problem in initializing Driver For Instance Id :"+driverDetail.getDriverInstanceId()+" Reason :Driver Not Found");
			}				
		}

		List<SecondaryAndCacheDriverDetail> secondaryDriverDetails = data.getDriverDetails().getSecondaryDriverGroup();

		for(SecondaryAndCacheDriverDetail secondaryDriverDetail : secondaryDriverDetails) {
			String secondaryDriverId  = secondaryDriverDetail.getSecondaryDriverId();
			String cacheDriverId = secondaryDriverDetail.getCacheDriverId();

			secondaryDriver = getDriver(secondaryDriverId);
			if (secondaryDriver != null) {
				addSecondaryCommunicator(secondaryDriver);
			} else {
				failedDriverCount++;
				LogManager.getLogger().warn(getModuleName(),"Problem in initializing Secondary Driver For Instance Id :"+secondaryDriverId+" Reason :Driver Not Found");
			}				

			if(cacheDriverId!=null){

				cacheDriver = getDriver(cacheDriverId);
				if (cacheDriver != null) {

					addCacheCommunicator(secondaryDriverId,cacheDriver);
				} else {
					LogManager.getLogger().warn(getModuleName(),"Problem in initializing Cache Driver For Instance Id :"+cacheDriverId+" Reason :Driver Not Found");
				}				
			}
		}
		
		// A sanity check to ensure that at least one primary or secondary driver is added in repository
		if(failedDriverCount == primaryDriverDetails.size() 
				+ secondaryDriverDetails.size()) {
			throw new InitializationFailedException("Problem in initializing Subscriber Profile Repository, Reason: No primary or secondary driver found");
		}

		List<AdditionalDriverDetail> additionalDriverDetails = data.getDriverDetails().getAdditionalDriverList();
		for(AdditionalDriverDetail additionalDriverDetail : additionalDriverDetails) {
			additionalDriver = getDriver(additionalDriverDetail.getDriverId());
			if (additionalDriver != null) {
				addAdditionalCommunicator(additionalDriver);
			} else {
				LogManager.getLogger().warn(getModuleName(),"Problem in initializing Additional Driver For Instance Id :"+ additionalDriverDetail.getDriverId()+" Reason :Driver Not Found");
			}	
		}
	}

	protected abstract IEliteAuthDriver getDriver(String driverInstanceId);
	protected abstract AccountData getAccountDataFromRequest(T request);
	protected abstract void setAccountDataIntoRequest(T request, AccountData accountData);
	protected abstract List<String> getUserIdentities(T request);

	@Override
	public AccountData getAccountData(T request, V response) {
		AccountData accountData = null;			

		//calling the pre driver script
		preDriverProcessingScript(request, response);

		AccountData accountDataFromScript = getAccountDataFromRequest(request);

		accountData = getAccountData(request ,response, getUserIdentities(request));

		/* If service request contains account data after pre script processing
		 * then using the primary driver as the additional driver,
		 * If pre script execution has not set the account data then setting the 
		 * account data for the post script to use it
		 */

		if(accountDataFromScript != null){
			accountDataFromScript.append(accountData);
			setAccountDataIntoRequest(request, accountDataFromScript);
		}else{
			setAccountDataIntoRequest(request, accountData);
		}

		//calling the post driver script
		postDriverProcessingScript(request,response);

		//fetching  the account data so that it gets refreshed if was set in script
		accountData = getAccountDataFromRequest(request);

		if (accountData != null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(getModuleName(), "User Profile for Identity: "+ accountData.getUserIdentity() + accountData);
		}else{
			if (request.getParameter(AAAServerConstants.USER_IDENTITY) != null){
				serverContext.generateSystemAlert(AlertSeverity.INFO,
						Alerts.UNKNOWN_USER, 
						getModuleName(), 
						"Unknown User: " + 
						request.getParameter(AAAServerConstants.USER_IDENTITY),
						0,
						(String) request.getParameter(AAAServerConstants.USER_IDENTITY));
			} else {
				serverContext.generateSystemAlert(AlertSeverity.INFO,
						Alerts.UNKNOWN_USER, 
						getModuleName(), 
						"Unknown User",
						0,
				"");
			}
		}
		return accountData;
	}


	private void preDriverProcessingScript(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		try {
			driverScript.preDriverProcessing(serviceRequest, serviceResponse);
		} catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(getModuleName(), "Error in executing \"pre\" method of driver script" + driverScript.getName() + ". Reason: " + e.getMessage());

			LogManager.getLogger().trace(e);
		}
	}

	private void postDriverProcessingScript(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {

		try {
			driverScript.postDriverProcessing(serviceRequest, serviceResponse);
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(getModuleName(), "Error in executing \"post\" method of driver script" + driverScript.getName() + ". Reason: " + e.getMessage());

			LogManager.getLogger().trace(e);
		}
	}

	@Override
	public AccountData getAccountData(T serviceRequest, V serviceResponse, String userIdentity) {
		AccountData accountData = null;	

		//calling the pre driver script
		preDriverProcessingScript(serviceRequest, serviceResponse);

		//saving the account data for further processing
		AccountData accountDataFromScript = getAccountDataFromRequest(serviceRequest);

		accountData = getAccountData(serviceRequest, serviceResponse, data.getUpdateIdentity().getCase(), data.getUpdateIdentity().getIsTrimIdentity(), stripStrategy, data.getUpdateIdentity().getSeparator(), userIdentity);

		/* If service request contains account data after pre script processing
		 * then using the primary driver as the additional driver,
		 * If pre script execution has not set the account data then setting the 
		 * account data for the post script to use it
		 */

		if(accountDataFromScript != null){
			accountDataFromScript.append(accountData);
			setAccountDataIntoRequest(serviceRequest, accountDataFromScript);
		}else{
			setAccountDataIntoRequest(serviceRequest, accountData);
		}

		//calling the post driver script
		postDriverProcessingScript(serviceRequest,serviceResponse);

		//fetching  the account data so that it gets refreshed if was set in script
		accountData = getAccountDataFromRequest(serviceRequest);

		if(accountData == null){
			serverContext.generateSystemAlert(AlertSeverity.INFO,
					Alerts.UNKNOWN_USER,
					getModuleName(), 
					"Unknown User: " + userIdentity,
					0,
					userIdentity);
		}
		return accountData;
	}


	private AccountData getAccountData(T serviceRequest, V serviceResponse, List<String> userIdentities){
		UpdateIdentityParameters updateIdentityParams = data.getUpdateIdentity();
		AccountData accountData = null;

		/*
		 * BEHAVIOUR
		 * if account data not found or primary driver not found --> fetch using secondary group primary driver
		 * if secondary group primary driver is not found then return NULL
		 * if account data is not found using the secondary group primary driver --> fetch using all the secondary group other drivers
		 * if still not found then return NULL
		 *  
		 */
		try{

			IEliteAuthDriver primaryCommunicator = primaryGroup.getCommunicator();
			if(primaryCommunicator != null){
				accountData = primaryCommunicator.getAccountData(serviceRequest, userIdentities, caseStrategy, updateIdentityParams.getIsTrimIdentity(), stripStrategy, updateIdentityParams.getSeparator());
				serviceRequest.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, primaryCommunicator.getDriverInstanceId());


			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(getModuleName(), "No Alive Auth Communicator found in Primary group.");
				}
			}

			if(accountData != null){
				return mergeAccountDataUsingAdditionalDriver(accountData, serviceRequest, userIdentities, updateIdentityParams.getCase(), updateIdentityParams.getIsTrimIdentity(), stripStrategy, updateIdentityParams.getSeparator());
			}

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModuleName(), "User profile not found using Primary Driver.");
			}

		}catch(DriverProcessFailedException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(getModuleName(), "Primary Driver processing failed. Reason: " + ex.getMessage());
		}

		if (optionalSecondaryGroup.isPresent()) {
			accountData = getAccountDataUsingSecondaryGroup(serviceRequest, userIdentities, updateIdentityParams);
		}
		return accountData;

	}

	private AccountData getAccountDataUsingSecondaryGroup(T serviceRequest, List<String> userIdentities,
			UpdateIdentityParameters updateIdentityParams) {
		
		assert optionalSecondaryGroup.isPresent();
		
		AccountData accountData = null;
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(getModuleName(), "Trying to fetch User profile using Secondary Group.");
		}

		IEliteAuthDriver secondaryCommunicator = null;
		try{

			secondaryCommunicator = optionalSecondaryGroup.get().getCommunicator();
			if(secondaryCommunicator != null){
				accountData = secondaryCommunicator.getAccountData(serviceRequest, userIdentities, caseStrategy, updateIdentityParams.getIsTrimIdentity(), stripStrategy, updateIdentityParams.getSeparator());
				serviceRequest.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, secondaryCommunicator.getDriverInstanceId());

			}else{
				//this means that there is no secondary driver configured in the service policy
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(getModuleName(), "No Alive Auth Communicator found in Secondary group.");
					return mergeAccountDataUsingAdditionalDriver(accountData, serviceRequest, userIdentities, updateIdentityParams.getCase(), updateIdentityParams.getIsTrimIdentity(), stripStrategy, updateIdentityParams.getSeparator());
				}
			}

			if(accountData != null){


				final String driverInstanceId= secondaryCommunicator.getDriverInstanceId(); 
				serviceRequest.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, secondaryCommunicator.getDriverInstanceId());
				final AccountData tempAccountData = accountData;
				final CacheCommunicatorGroup cacheCommunicatorGroup = getCacheCommunicatorGroup(driverInstanceId);

				if(getCacheCommunicatorGroup(driverInstanceId)!=null){

					try{
						serverContext.getTaskScheduler().scheduleSingleExecutionTask(new SingleExecutionAsyncTask()  {
							@Override
							public long getInitialDelay() {
								return 0;
							}

							@Override
							public TimeUnit getTimeUnit() {
								return TimeUnit.SECONDS;
							}

							@Override
							public void execute(AsyncTaskContext context) {
								cacheCommunicatorGroup.saveAccountData(tempAccountData);		
							}
						});
					}catch(RejectedExecutionException ex){
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(getModuleName(), "Async Task Executor is busy right now. Cannot cache profile for User Identity : " + accountData.getUserIdentity());
						}
					}				
				}
				return mergeAccountDataUsingAdditionalDriver(accountData, serviceRequest, userIdentities, updateIdentityParams.getCase(), updateIdentityParams.getIsTrimIdentity(), stripStrategy, updateIdentityParams.getSeparator());
			}

		}catch(DriverProcessFailedException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(getModuleName(), "Secondary Driver processing failed. Reason: " + ex.getMessage());
		}


		if(accountData == null){
			LinkedList<IEliteAuthDriver> alreadyTriedCommunicators = new LinkedList<IEliteAuthDriver>();
			alreadyTriedCommunicators.add(secondaryCommunicator);
			do{
				try{

					secondaryCommunicator = (IEliteAuthDriver) optionalSecondaryGroup.get().getSecondaryCommunicator(toArray(alreadyTriedCommunicators));
					if(secondaryCommunicator==null){
						return mergeAccountDataUsingAdditionalDriver(accountData, serviceRequest, userIdentities, updateIdentityParams.getCase(), updateIdentityParams.getIsTrimIdentity(), stripStrategy, updateIdentityParams.getSeparator());
					}

					alreadyTriedCommunicators.add(secondaryCommunicator);
					accountData = secondaryCommunicator.getAccountData(serviceRequest, userIdentities, caseStrategy, updateIdentityParams.getIsTrimIdentity(), stripStrategy, updateIdentityParams.getSeparator());

					if(accountData!=null  ){

						final String driverInstanceId= secondaryCommunicator.getDriverInstanceId(); 
						serviceRequest.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, secondaryCommunicator.getDriverInstanceId());
						final AccountData tempAccountData = accountData;
						final CacheCommunicatorGroup cacheCommunicatorGroup = getCacheCommunicatorGroup(driverInstanceId);

						if(getCacheCommunicatorGroup(driverInstanceId)!=null){
							try{
								serverContext.getTaskScheduler().scheduleSingleExecutionTask(new SingleExecutionAsyncTask()  {
									@Override
									public long getInitialDelay() {
										return 0;
									}

									@Override
									public TimeUnit getTimeUnit() {
										return TimeUnit.SECONDS;
									}

									@Override
									public void execute(AsyncTaskContext context) {
										cacheCommunicatorGroup.saveAccountData(tempAccountData);		
									}
								});
							}catch(RejectedExecutionException ex){
								if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
									LogManager.getLogger().info(getModuleName(), "Async task Executor is busy right now. Cannot cache profile for User Identity : " + accountData.getUserIdentity());
								}
							}
						}
						break;
					}
				}catch(DriverProcessFailedException e){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(getModuleName(), "Secondary Driver processing failed. Reason: " + e.getMessage());
					continue;
				}
			}while(optionalSecondaryGroup.get().getSecondaryCommunicator(toArray(alreadyTriedCommunicators)) != null);
		}
		return accountData;
	}
	
	private AccountData getAccountData(T serviceRequest, V serviceResponse,  int iCaseSensitivity,
			boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy,
			String realmSeparator,String userIdentity) {
		
		AccountData accountData = null;

		try{

			IEliteAuthDriver primaryCommunicator = (IEliteAuthDriver) primaryGroup.getCommunicator();
			if(primaryCommunicator != null){
				accountData = primaryCommunicator.getAccountData(serviceRequest, caseStrategy, btrimUserIdentity, stripStrategy, realmSeparator, userIdentity);
				serviceRequest.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, primaryCommunicator.getDriverInstanceId());

			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(getModuleName(), "No Alive Auth Communicator found in Primary group.");
				}
			}

			if(accountData != null){
				return getAccountDataUsingAdditionalDriver(accountData, serviceRequest, iCaseSensitivity, btrimUserIdentity, stripStrategy, realmSeparator, userIdentity);
			}

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(getModuleName(), "User profile not found using Primary Driver.");
			}

		}catch(DriverProcessFailedException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(getModuleName(), "Primary Driver processing failed. Reason: " + ex.getMessage());
		}

		if (optionalSecondaryGroup.isPresent()) {
			accountData = getAccountDataUsingSecondaryGroup(serviceRequest, iCaseSensitivity, btrimUserIdentity,
					stripStrategy, realmSeparator, userIdentity);
		}
		
		return accountData;

	}

	private AccountData getAccountDataUsingSecondaryGroup(T serviceRequest, int iCaseSensitivity,
			boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator,
			String userIdentity) {

		assert optionalSecondaryGroup.isPresent();
		
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(getModuleName(), "Trying to fetch User profile using Secondary Group.");
		}

		AccountData accountData = null;
		IEliteAuthDriver secondaryCommunicator = null;
		try{

			secondaryCommunicator = (IEliteAuthDriver) optionalSecondaryGroup.get().getCommunicator();
			if(secondaryCommunicator != null){
				accountData = secondaryCommunicator.getAccountData(serviceRequest, caseStrategy, btrimUserIdentity, stripStrategy, realmSeparator, userIdentity);
				serviceRequest.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, secondaryCommunicator.getDriverInstanceId());

			}else{
				//this means that there is no secondary driver configured in the service policy
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(getModuleName(), "No Alive Auth Communicator found in Secondary group.");
					return getAccountDataUsingAdditionalDriver(accountData, serviceRequest, iCaseSensitivity, btrimUserIdentity, stripStrategy, realmSeparator, userIdentity);
				}

			}

			if(accountData != null){


				final String driverInstanceId= secondaryCommunicator.getDriverInstanceId(); 
				serviceRequest.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, secondaryCommunicator.getDriverInstanceId());
				final AccountData tempAccountData = accountData;
				final CacheCommunicatorGroup cacheCommunicatorGroup = getCacheCommunicatorGroup(driverInstanceId);

				if(getCacheCommunicatorGroup(driverInstanceId)!=null){
					try{
						serverContext.getTaskScheduler().scheduleSingleExecutionTask(new SingleExecutionAsyncTask()  {
							@Override
							public long getInitialDelay() {
								return 0;
							}

							@Override
							public TimeUnit getTimeUnit() {
								return TimeUnit.SECONDS;
							}

							@Override
							public void execute(AsyncTaskContext context) {
								cacheCommunicatorGroup.saveAccountData(tempAccountData);		
							}
						});
					}catch(RejectedExecutionException ex){
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(getModuleName(), "Async task Executor is busy right now. Cannot cache profile for User Identity : " + accountData.getUserIdentity());
						}
					}
				}
				return getAccountDataUsingAdditionalDriver(accountData, serviceRequest, iCaseSensitivity, btrimUserIdentity, stripStrategy, realmSeparator, userIdentity);
			}

		}catch(DriverProcessFailedException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(getModuleName(), "Secondary Driver processing failed. Reason: " + ex.getMessage());
		}


		if(accountData == null){
			LinkedList<IEliteAuthDriver> alreadyTriedCommunicators = new LinkedList<IEliteAuthDriver>();
			alreadyTriedCommunicators.add(secondaryCommunicator);
			do{
				try{
					
					secondaryCommunicator = (IEliteAuthDriver) optionalSecondaryGroup.get().getSecondaryCommunicator(toArray(alreadyTriedCommunicators));
					if(secondaryCommunicator==null){
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(getModuleName(), "No Alive Auth Communicator found in secondary group.");
						}
						return getAccountDataUsingAdditionalDriver(accountData, serviceRequest, iCaseSensitivity, btrimUserIdentity, stripStrategy, realmSeparator, userIdentity);
					}

					alreadyTriedCommunicators.add(secondaryCommunicator);
					accountData = secondaryCommunicator.getAccountData(serviceRequest, caseStrategy, btrimUserIdentity, stripStrategy, realmSeparator, userIdentity);

					if(accountData!=null  ){

						final String driverInstanceId= secondaryCommunicator.getDriverInstanceId(); 
						serviceRequest.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, secondaryCommunicator.getDriverInstanceId());
						final AccountData tempAccountData = accountData;
						final CacheCommunicatorGroup cacheCommunicatorGroup = getCacheCommunicatorGroup(driverInstanceId);

						if(getCacheCommunicatorGroup(driverInstanceId)!=null){

							try{
								serverContext.getTaskScheduler().scheduleSingleExecutionTask(new SingleExecutionAsyncTask()  {
									@Override
									public long getInitialDelay() {
										return 0;
									}

									@Override
									public TimeUnit getTimeUnit() {
										return TimeUnit.SECONDS;
									}

									@Override
									public void execute(AsyncTaskContext context) {
										cacheCommunicatorGroup.saveAccountData(tempAccountData);		
									}
								});
							}catch(RejectedExecutionException ex){
								if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
									LogManager.getLogger().info(getModuleName(), "Async task Executor is busy right now. Cannot cache profile for User Identity : " + accountData.getUserIdentity());
								}
							}
						}
						break;
					}
				}catch(DriverProcessFailedException e){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(getModuleName(), "Secondary Driver processing failed. Reason: " + e.getMessage());
					continue;
				}
			}while(optionalSecondaryGroup.get().getSecondaryCommunicator(toArray(alreadyTriedCommunicators)) != null);
		}
		return accountData;
	}

	public void addPrimaryCommunicator(IEliteAuthDriver mainDriver,Integer value) {
		primaryGroup.addCommunicator(mainDriver, value);
	}

	public void addSecondaryCommunicator(IEliteAuthDriver secondary) {
		if (optionalSecondaryGroup.isPresent() == false) {
			optionalSecondaryGroup = Optional.of(new AuthCommunicatorGroupImpl(LoadBalancerType.FAIL_OVER));
		}
		optionalSecondaryGroup.get().addCommunicator(secondary);
	}

	public void addAdditionalCommunicator(IEliteAuthDriver additionalDriver){
		AuthCommunicatorGroup group = new AuthCommunicatorGroupImpl(LoadBalancerType.ROUND_ROBIN);
		group.addCommunicator(additionalDriver);
		additionalDriverGroupList.add(group);
	}

	public void addCacheCommunicator(String driverInstanceId, IEliteAuthDriver authDriver){
		CacheCommunicatorGroupImpl cacheCommunicatorGroup = new CacheCommunicatorGroupImpl();
		cacheCommunicatorGroup.addCommunicator(authDriver);
		cacheDriverMap.put(driverInstanceId, cacheCommunicatorGroup);
	}

	private CacheCommunicatorGroup getCacheCommunicatorGroup(String driverInstanceId){
		return cacheDriverMap.get(driverInstanceId);
	}

	private AccountData mergeAccountDataUsingAdditionalDriver(AccountData accountData, T serviceRequest, 
			List<String> userIdentities, int iCaseSensitivity,boolean btrimUserIdentity, 
			StripUserIdentityStrategy stripStrategy, String realmSeparator){

		if (accountData == null){
			return accountData;
		}

		AccountData tempAccountData = null;
		addSubscriberProfileVSA(serviceRequest, accountData);
		for (AuthCommunicatorGroup authCommunicatorGroup : additionalDriverGroupList){
			IEliteAuthDriver additionalCommunicator = authCommunicatorGroup.getCommunicator();

			if(additionalCommunicator != null){
				try {

					tempAccountData = additionalCommunicator.getAccountData(serviceRequest,userIdentities, caseStrategy,btrimUserIdentity, stripStrategy, realmSeparator);
					accountData.append(tempAccountData);
					addSubscriberProfileVSA(serviceRequest, accountData);

				} catch (DriverProcessFailedException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) 
						LogManager.getLogger().warn(getModuleName(), "Additional Driver processing Failed. Reason: " + e.getMessage());
				}
			}
		}
		return accountData;

	}

	private AccountData getAccountDataUsingAdditionalDriver(AccountData accountData, T serviceRequest, 
			int iCaseSensitivity,boolean btrimUserIdentity,StripUserIdentityStrategy stripStrategy,
			String realmSeparator,String userIdentity){

		if (accountData == null){
			return accountData;
		}

		AccountData tempAccountData = null;
		addSubscriberProfileVSA(serviceRequest, accountData);
		for (AuthCommunicatorGroup authCommunicatorGroup : additionalDriverGroupList){
			IEliteAuthDriver additionalCommunicator = (IEliteAuthDriver) authCommunicatorGroup.getCommunicator();

			if(additionalCommunicator != null){
				try {

					tempAccountData = additionalCommunicator.getAccountData(serviceRequest, caseStrategy, btrimUserIdentity, stripStrategy, realmSeparator, userIdentity);
					accountData.append(tempAccountData);
					addSubscriberProfileVSA(serviceRequest, accountData);

				} catch (DriverProcessFailedException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) 
						LogManager.getLogger().warn(getModuleName(), "Additional Driver processing Failed. Reason: " + e.getMessage());
				}
			}
		}
		return accountData;

	}

	protected abstract String getModuleName();
	protected abstract void addSubscriberProfileVSA(T request, AccountData accountData);

	public boolean isAlive(){
		return primaryGroup.isAlive() 
				|| (optionalSecondaryGroup.isPresent() ? optionalSecondaryGroup.get().isAlive() : false);
	}
	
	private IEliteAuthDriver[] toArray(LinkedList<IEliteAuthDriver> drivers) {
		IEliteAuthDriver[] driversArray = new IEliteAuthDriver[drivers.size()];
		return drivers.toArray(driversArray);
	}
}


