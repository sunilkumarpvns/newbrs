package com.elitecore.aaa.diameter.service;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.plugins.PluginRequestHandler;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.commons.EliteDiameterAppFactory;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.policies.applicationpolicy.DiameterServicePolicyContainer;
import com.elitecore.aaa.diameter.policies.applicationpolicy.DiameterServicePolicyContainerFactory;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.EliteDiameterStack;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAsyncRequestExecutor;
import com.elitecore.aaa.diameter.service.application.listener.AAAServerApplication;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.servicex.UniSocketService;
import com.elitecore.core.servicex.base.BaseEliteService;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.stack.constant.Status;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.stack.DiameterStack;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.DiameterStackContext;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;

public class DiameterService extends BaseEliteService implements UniSocketService<ServiceRequest, ServiceResponse> {
	private static final String MODULE = "DIA-SERVICE";
	private DiameterServiceContext serviceContext;
	private AAAServerContext serverContext;
	private String serviceId;
	private String key;
	private DiameterServiceConfigurationDetail diameterServiceConfigurationDetail;
	private AAAServerApplication applicationListener;
	private EliteDiameterStack diameterStack;
	private DiameterServicePolicyContainer servicePolicyContainer;
	private SynchronousQueue<Runnable> asyncQueue;
	private ThreadPoolExecutor asyncService;
	
	private static final DiameterAsyncRequestExecutor NONE = new DiameterAsyncRequestExecutor() {

		@Override
		public void handleServiceRequest(ApplicationRequest serviceRequest, ApplicationResponse serviceResponse,
				ISession session) {
			// no ops
		}
	};
	
	public DiameterService(ServerContext ctx, String SERVICE_ID, EliteDiameterStack diameterStack,
			DiameterServiceConfigurationDetail diameterServiceConfigurationDetail, String key) {
		super(ctx);
		this.serverContext = (AAAServerContext)ctx;
		this.serviceId = SERVICE_ID;
		this.diameterStack = diameterStack; 
		this.diameterServiceConfigurationDetail = diameterServiceConfigurationDetail;
		this.key = key;
	}

	@Override
	public String getServiceIdentifier() {
		return serviceId;
	}

	@Override
	public String getServiceName() {
		return serviceId;
	}

	@Override
	public ServiceRequest formServiceSpecificRequest(
			InetAddress sourceAddress, int sourcePort, byte[] requestBytes) {
		return null;
	}

	@Override
	public ServiceResponse formServiceSpecificResposne(
			ServiceRequest serviceRequest) {
		return null;
	}

	@Override
	protected ServiceContext getServiceContext() {
		return this.serviceContext;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	protected void initService() throws ServiceInitializationException {
		LogManager.getLogger().info(getKey(), String.valueOf(diameterServiceConfigurationDetail));
		this.serviceContext = new DiameterServiceContext() {
			
			@Override
			public PluginRequestHandler createPluginRequestHandler(
					List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList) {
				return null;
			}
			
			@Override
			public AAAServerContext getServerContext() {
				return serverContext;
			}
			
			@Override
			public ESCommunicator getDriver(String driverInstanceId) {
				return serverContext.getDiameterDriver(driverInstanceId);
			}
			
			@Override
			public DiameterServiceConfigurationDetail getDiameterServiceConfigurationDetail() {
				return diameterServiceConfigurationDetail;
			}

			@Override
			public DriverConfiguration getDriverConfiguration(String driverInstanceId) {
				return DiameterService.this.serverContext.getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverInstanceId);
			}

			@Override
			public ServicePolicy<ApplicationRequest> selectServicePolicy(ApplicationRequest appRequest) {
				return servicePolicyContainer.applyPolicy(appRequest);
			}

			@Override
			public void resumeRequestInAsync(final Session session, final ApplicationRequest request, 
					final ApplicationResponse response, 
					final DiameterAsyncRequestExecutor unitOfWork) {
				
				if (asyncService == null) {
					throw new UnsupportedOperationException("Async Pool is not used");
				}
				
				asyncService.execute(new Runnable() {
					
					@Override
					public void run() {
						applicationListener.resumeApplicationRequest((DiameterSession) session, 
								request, response, unitOfWork);
					}
				});
			}

			@Override
			public DiameterStackContext getStackContext() {
				return diameterStack.getStackContext();
			}

			@Override
			public void resumeRequestInAsync(Session session, ApplicationRequest request,
					ApplicationResponse response) {
				resumeRequestInAsync(session, request, response, NONE);
			}

			@Override
			public void resumeRequestInSync(DiameterSession session, ApplicationRequest request,
					ApplicationResponse response) {
				
				applicationListener.resumeApplicationRequest((DiameterSession) session, 
						request, response, NONE);
			}

			@Override
			public SessionReleaseIndiactor getSessionReleaseIndicator() {
				return applicationListener.getSessionReleaseIndicator();
			}
		};
		
		if (diameterStack.isInitialized() == false) {
			throw new ServiceInitializationException("Diameter Stack is not initialized.", ServiceRemarks.DIAMETER_STACK_ERROR);
		}
		
		this.applicationListener = EliteDiameterAppFactory.getEliteApplicationListener(serviceId, serviceContext, diameterStack.getStackContext(),
				diameterServiceConfigurationDetail.getDiameterServiceConfiguration(), diameterStack.getDiameterSessionManager(),
				diameterStack.getSessionFactoryManager());
		
		if(applicationListener==null){
			throw new ServiceInitializationException("Invalid Service Identifier: "+serviceId, ServiceRemarks.UNKNOWN_PROBLEM);
		}
		
		this.servicePolicyContainer = DiameterServicePolicyContainerFactory.getServicePolicyContainer(serviceId, serviceContext, diameterServiceConfigurationDetail.getDiameterServicePolicyConfiguration(), diameterStack.getDiameterSessionManager());
		this.servicePolicyContainer.init();
		
		if (diameterServiceConfigurationDetail.getDiameterServiceConfiguration().getAsyncCorePoolSize() != DiameterServiceConfigurable.ASYNC_POOL_UNUSED) {

			this.asyncQueue = new SynchronousQueue<Runnable>();
			this.asyncService = new ThreadPoolExecutor(diameterServiceConfigurationDetail.getDiameterServiceConfiguration().getAsyncCorePoolSize(),
					diameterServiceConfigurationDetail.getDiameterServiceConfiguration().getAsyncMaxPoolSize(), 
					DiameterStack.DEFAULT_THREAD_KEEP_ALIVE_TIME, 
					TimeUnit.MILLISECONDS, asyncQueue,new RejectedExecutionHandler() {

				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					if (isStopRequested()) {
						return;
					}
					try {
						getLogger().warn(MODULE, "No Async thread is free, so temporarily pausing listener thread execution.");
						Thread.sleep(150);
						executor.execute(r);
					} catch (InterruptedException e) {
						getLogger().trace(MODULE, e);
					}
				}
			});
			asyncService.setThreadFactory(new EliteThreadFactory(com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants.DIAMETER_STACK_IDENTIFIER,
					getThreadIdentifier() + "-RES", 
					diameterStack.getMainThreadPriority(),
					new UncaughtExceptionHandler() {
						
						@Override
						public void uncaughtException(Thread t, Throwable e) {
							LogManager.getLogger().trace(e);
						}
					}));

			asyncService.prestartAllCoreThreads();
		}
		
	}
	
	@Override
	public void reInit() {
//		try {
//			this.diameterServiceConfigurationDetail = serverContext.getServerConfiguration().getDiameterServiceConfiguration(this.serviceId);
//			servicePolicyContainer.reInit();
//		} catch (InitializationFailedException e) {
//		}
	}

	@Override
	protected boolean startService() {
		try {
			diameterStack.registerApplicationListener(applicationListener);
		} catch (ElementRegistrationFailedException e) {
			return false;
		}
		return true;
	}

	@Override
	public SocketDetail getSocketDetail() {
		return diameterStack.getSocketDetail();
	}

	@Override
	public ServiceDescription getDescription() {
		return new ServiceDescription(getServiceIdentifier(), getStatus(), 
				"N.A.", getStartDate(), getRemarks());
	}

	@Override
	public boolean stopService() {
		return (diameterStack.getStackStatus() == Status.STOPPED 
				|| diameterStack.getStackStatus() == Status.NOT_INITIALIZE);
	}

	@Override
	protected void shutdownService() {
		if (asyncService == null) {
			return;
		}
		
		asyncService.shutdown();
		try {
			asyncService.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LogManager.getLogger().info(MODULE, "Interrupted while waiting for async threads to stop.");
			return;
		}
		asyncService.shutdownNow();
	}
}
