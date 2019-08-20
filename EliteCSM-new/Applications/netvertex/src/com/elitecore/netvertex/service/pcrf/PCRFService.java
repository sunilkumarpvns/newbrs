package com.elitecore.netvertex.service.pcrf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.commons.alert.Events;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.logmonitor.LogMonitorManager;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.base.BaseEliteService;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.core.util.logger.monitor.MonitorLogger;
import com.elitecore.corenetvertex.constants.ExecutionContextKey;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.SystemPropertyReaders;
import com.elitecore.netvertex.cli.ClearStatisticsDetailProvider;
import com.elitecore.netvertex.cli.ClearUsageStatisticsDetailProvider;
import com.elitecore.netvertex.cli.PCRFServiceStatisticsDetailProvider;
import com.elitecore.netvertex.cli.StatisticsDetailProvider;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.driver.cdr.CDRDriverFactory;
import com.elitecore.netvertex.core.driver.cdr.ValueProviderExtImpl;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.LocationInformationConfiguration;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.session.SessionOperation;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.extended.UsageMonitoringStatisticsProvider;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.rnc.ThresholdNotificationProcessor;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.logmonitor.PCRFServiceMonitor;
import com.elitecore.netvertex.service.pcrf.preprocessors.CallTypeStatusOrchestrator;
import com.elitecore.netvertex.service.pcrf.preprocessors.CalledPartyLRNAndPrefixConfigurationOrchestrator;
import com.elitecore.netvertex.service.pcrf.preprocessors.OperatorNetworkInfoOrchestrator;
import com.elitecore.netvertex.service.pcrf.preprocessors.RoamingStatusOrchestrator;
import com.elitecore.netvertex.service.pcrf.preprocessors.SubscriberNetworkInfoOrchestrator;
import com.elitecore.netvertex.service.pcrf.preprocessors.SystemParameterConfigurationOrchestrator;
import com.elitecore.netvertex.service.pcrf.servicepolicy.PCRFServicePolicy;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.ServiceHandlerFactory;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.ServiceHandlerFactoryImpl;
import com.elitecore.netvertex.usagemetering.UMLevel;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;
import com.elitecore.netvertex.usagemetering.UsageMonitoringStatisticsCounter;
import com.elitecore.netvertex.usagemetering.UsageMonitoringStatisticsDetailProvider;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * The <code>PCRFService</code> is used to find package information from various sources like LDAP, DB and Session.
 * It also selects proper policy from package and applies that policy to user.
 *
 * @author Subhash Punani
 */
public class PCRFService extends BaseEliteService {

    private static final String SERVICE_ID = "PCRF";
    private static final String KEY = "PCRF_SERVICE";
    private static final String MODULE = "PCRF-SERV";
    private static final String SYS_PARAMETER_REVALIDATOIN_TIME_DELTA = "revalidationtime.delta";
    private static final long REVALIDATION_TIME_DELTA_DEFAULT = TimeUnit.MINUTES.toSeconds(10);
    private static final String SID = "SID";
    private static final String SESSION_TYPE = "SessionType";

    private LinkedBlockingQueue<Runnable> initialRequestQueue;
    private ThreadPoolExecutor synchronousTaskExecutor;

    private NetVertexServerContext serverContext;

    private PCRFServiceContext pcrfServiceContext;

    private PCRFServiceConfiguration serviceConfiguration;

    private AtomicLong requestCounter;
    private AtomicLong totalReqProcessed;
    private AtomicLong requestProcessingTimeMS;
    private long tpm;
    private List<PCRFServicePolicy> pcrfServicePolicyList;
    private PCCRuleExpiryManager pccRuleExpiryManager;
    private SessionOperation sessionOperation;

    private Object requestListenerLockObject;
    private PCRFServiceMonitor pcrfMonitor;
    private PCRFServiceCounters pcrfServiceCounters;
    private PCRFServiceStatisticsProvider pcrfServiceStatisticsProvider;
    private QuotaNotificationProcessor quotaNotificationProcessor;
    private UsageNotificationProcessor usageNotificationProcessor;
    private ThresholdNotificationProcessor thresholdNotificationProcessor;
    private UsageMonitoringStatisticsCounter usageMonitoringStatistics;
    private long licenseTPS = -1;
    private long pcrfRequestMaxProcessingTimeThreshold;
    private int revalidationTimeDeltaInSeconds;
    private List<PCRFRequestHandlerPreProcessor> requestHandlerPreProcessors;
    private CDRDriverFactory cdrDriverFactory;

    public PCRFService(NetVertexServerContext ctx, SessionOperation sessionOperation, CDRDriverFactory cdrDriverFactory) {
        super(ctx);
        this.serverContext = ctx;
        pcrfServicePolicyList = new ArrayList<PCRFServicePolicy>();
        this.sessionOperation = sessionOperation;
        requestListenerLockObject = new Object();
        requestCounter = new AtomicLong();
        totalReqProcessed = new AtomicLong();
        serviceConfiguration = serverContext.getServerConfiguration().getPCRFServiceConfiguration();
        requestProcessingTimeMS = new AtomicLong();
        usageNotificationProcessor = new UsageNotificationProcessor(serverContext);
        quotaNotificationProcessor = new QuotaNotificationProcessor(serverContext);
        thresholdNotificationProcessor = new ThresholdNotificationProcessor(serverContext);
        this.cdrDriverFactory = cdrDriverFactory;
        ctx.registerLicenseObserver(() -> licenseTPS = ctx.getLicenseTPS());
        pcrfRequestMaxProcessingTimeThreshold = new SystemPropertyReaders.NumberReaderBuilder("pcrf.req.process.threshold")
                .between(0, Integer.MAX_VALUE)
                .onFail(100, "")
                .build().read();
        requestHandlerPreProcessors = new ArrayList<>();

    }


    /**
     * Initializes PCRF Service
     * @throws ServiceInitializationException
     */

    /**
     * Returns Worker Thread Priority
     *
     * @return <code>int</code>
     */
    private int getWorkerThreadPriority() {
        return serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getPcrfServiceConfiguration().getWorkerThreadPriority();
    }

    /**
     * Returns Thread Keep Alive Time
     *
     * @return <code>long</code>
     */
    private long getThreadKeepAliveTime() {
        return (1000 * 60 * 60);
    }

    /**
     * Returns Maximum Thread Pool Size
     *
     * @return <code>int</code>
     */
    private int getMaxThreadPoolSize() {
        return serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getPcrfServiceConfiguration().getMaximumThread();
    }

    /**
     * Returns Minimum Thread Pool Size
     *
     * @return <code>int</code>
     */
    private int getMinThreadPoolSize() {
        return serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getPcrfServiceConfiguration().getMinimumThread();
    }

    /**
     * Returns Maximum Queue Size
     *
     * @return <code>int</code> value indicating Maximum Queue Size
     */
    private int getMaxRequestQueueSize() {
        return serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getPcrfServiceConfiguration().getQueueSize();
    }

    /**
     * Returns Service Identifier
     *
     * @return <code>String</code> containing Service Identifier
     */
    @Override
    public String getServiceIdentifier() {
        return SERVICE_ID;
    }

    /**
     * Starts PCRF Service, initialize thread pool
     *
     * @return <code>true</code> if service starts without any problem, else false
     */
    @Override
    protected boolean startService() {
        LogManager.getLogger().info(MODULE, "PCRF Service started successfully");
        return true;
    }

    /**
     * Stops PCRF Service
     *
     * @return <code>true</code> if service shutdown without any problem, else <code>false</code>
     */
    @Override
    public boolean stopService() {

        usageMonitoringStatistics.serialize();

        getLogger().info(MODULE, "Stop flag set for " + getServiceIdentifier());
        return true;
    }

    @Override
    protected void shutdownService() {
        LogManager.getLogger().info(MODULE, "Final shutdown process started for " + getServiceIdentifier());

        cdrDriverFactory.stop();

        if (pccRuleExpiryManager != null) {
            pccRuleExpiryManager.stop();
        }

        synchronized (requestListenerLockObject) {
            LogManager.getLogger().info(MODULE, getServiceIdentifier() + " - request listener lock obtained, pending request in queue: " + (initialRequestQueue != null ? initialRequestQueue.size() : 0));
            LogManager.getLogger().info(MODULE, getServiceIdentifier() + " - stopping worker threads");

            if (synchronousTaskExecutor != null) {
                synchronousTaskExecutor.shutdown();
                LogManager.getLogger().info(MODULE, getServiceIdentifier() + " - shutdown requested for synchronous task executor ");

                while (!synchronousTaskExecutor.isTerminated()) {
                    LogManager.getLogger().info(MODULE, getServiceIdentifier() + " - waiting for synchronous task executor to complete execution");
                    try {
                        Thread.sleep(500); //NOSONAR
                    } catch (InterruptedException e) { //NOSONAR

                    }
                }
                LogManager.getLogger().info(MODULE, getServiceIdentifier() + " - synchronous task executor terminated");
            }
        }
    }

    /**
     * Submits Event to PCRF Service
     *
     * @param request - Policy Request to be handled
     * @return <code>true</code> if event submitted successfully, else <code>false</code>
     */
    public RequestStatus submitRequest(PCRFRequest request, PCRFResponseListner responseListner, PCCRuleExpiryListener pccRuleExpiryListener) {

        requestCounter.incrementAndGet();
        pcrfServiceCounters.incTotalPCRFReqCntr();

        if (!validatePCRFRequest(request)) {
            LogManager.getLogger().error(MODULE, "PCRF request dropped. Reason: PCRF Request validation failed");
            pcrfServiceCounters.incTotalPCRFReqDroppedCntr();
            return RequestStatus.INVALID_PCRF_REQUEST;
        }

        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "Submitting request to PCRF Request queue");
        }

        PolicyRequestHandler requestHandler = new PolicyRequestHandler(request, responseListner, pccRuleExpiryListener, requestHandlerPreProcessors, ThreadContext.getContext());
        boolean isRequestSubmitted = executeServiceRequest(requestHandler);

        if (!isRequestSubmitted) {

            if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
                LogManager.getLogger().warn(MODULE, "Pausing PCRF request submission. Reason: Request queue is full");
            }

            Thread.yield();
            isRequestSubmitted = executeServiceRequest(requestHandler);
            if (!isRequestSubmitted) {
                if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
                    LogManager.getLogger().error(MODULE, "PCRF request dropped. Reason: Request queue is full");
                }
                pcrfServiceCounters.incTotalPCRFReqDroppedCntr();
                return RequestStatus.QUEUE_FULL;
            }
        }

        return RequestStatus.SUBMISSION_SUCCESSFUL;

    }

    private boolean validatePCRFRequest(PCRFRequest request) {

        String coreSessionId = request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
        if (coreSessionId == null || coreSessionId.trim().length() == 0) {
            LogManager.getLogger().error(MODULE, "Invalid PCRF Request. Reason: Core Session ID not found");
            return false;
        }

        return true;
    }

    public RequestStatus submitRequest(PCRFRequest request, PCRFResponseListner responseListner) {
        return submitRequest(request, responseListner, null);
    }

    public RequestStatus submitRequest(PCRFRequest request) {
        return submitRequest(request, null, null);
    }

    /**
     * A Runnable task to count TPM for pcrf submit request
     *
     * @author Milan Paliwal
     */

    private class TPMCalculator extends BaseIntervalBasedTask {

        private long lastResetTimeMillis;

        public TPMCalculator() {
            lastResetTimeMillis = System.currentTimeMillis();
        }

        @Override
        public long getInitialDelay() {
            return 60;
        }

        @Override
        public long getInterval() {
            return 60;
        }

        @Override
        public void execute(AsyncTaskContext context) {

            long tempReqCount = requestCounter.get();
            requestCounter.set(0);

            double tempTotalReqProcessed = totalReqProcessed.get();
            totalReqProcessed.set(0);

            double tempRequestProcessingTimeMS = requestProcessingTimeMS.get();
            requestProcessingTimeMS.set(0);

            // Average Request Processing time for request received in last one minute
            long avgReqProcessTimeMS = 0;
            if (tempTotalReqProcessed > 0) {
                avgReqProcessTimeMS = (long) Math.ceil(tempRequestProcessingTimeMS / tempTotalReqProcessed);
            }

            long currentTimeMillis = System.currentTimeMillis();
            double timeDIffSec = (currentTimeMillis - (double) lastResetTimeMillis) / 1000;
            double timeDIffMin = timeDIffSec / 60;

            lastResetTimeMillis = currentTimeMillis;

            long tps = (long) Math.ceil(tempReqCount / timeDIffSec);
            if (timeDIffMin > 0) {
                tpm = (long) Math.ceil(tempReqCount / timeDIffMin);
            }

            String tpsStr = String.valueOf(tps);

            boolean isTpsValid = licenseTPS == -1 || tps <= licenseTPS;

            if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
                LogManager.getLogger().warn(MODULE, "Average TPS for last 1 minute = " + tps + ", Valid = " + isTpsValid +
                        ", No. of Pending Requests = " + initialRequestQueue.size() +
                        ", Average request processing time = " + avgReqProcessTimeMS + " ms");
            }

            if (!isTpsValid) {
                getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.LICENSE_EXCEEDED, MODULE,
                        "Netvertex Server TPS Exceeded the license TPS. Average TPS value: " + tpsStr);
            }

            pcrfServiceCounters.updateAvgRequestProcessingTime(avgReqProcessTimeMS);
            pcrfServiceCounters.updateAvgTPS(tps);
        }
    }

    /**
     * A Runnable task to handle {@link #request}
     *
     * @author Subhash Punani
     */
    public class PolicyRequestHandler implements Runnable {
        private PCRFRequest request;
        private PCRFResponse response;
        private PCRFResponseListner responseListner;
        private PCCRuleExpiryListener pccRuleExpiryListener;
        private long reqRcvdTimemili = 0;
        private ExecutionContext executionContext;
        private List<PCRFRequestHandlerPreProcessor> preProcessors;
        private Map<String, String> contextInformation;

        public PolicyRequestHandler(PCRFRequest request,
                                    PCRFResponse pcrfResponse,
                                    ExecutionContext executionContext,
                                    PCRFResponseListner responseListner,
                                    PCCRuleExpiryListener pccRuleExpiryListener,
                                    List<PCRFRequestHandlerPreProcessor> preProcessors,
                                    Map<String, String> contextInformation) {
            this.request = request;
            this.response = pcrfResponse;
            this.responseListner = responseListner;
            this.pccRuleExpiryListener = pccRuleExpiryListener;
            this.executionContext = executionContext;
            this.preProcessors = preProcessors;
            this.contextInformation = contextInformation;
            this.reqRcvdTimemili = System.currentTimeMillis();
        }

        public PolicyRequestHandler(PCRFRequest request,
                                    PCRFResponseListner responseListner,
                                    PCCRuleExpiryListener pccRuleExpiryListener,
                                    List<PCRFRequestHandlerPreProcessor> preProcessors,
                                    Map<String, String> contextInformation) {
            this(request, new PCRFResponseImpl(), null, responseListner, pccRuleExpiryListener, preProcessors, contextInformation);
        }

        private ExecutionContext executeServiceRequest(PCRFRequest request, PCRFResponse response) {
            boolean policyNotSatisfied = true;
            ExecutionContext executionContext = null;
            for (PCRFServicePolicy pcrfServicePolicy : pcrfServicePolicyList) {
                if (pcrfServicePolicy.assignRequest(request, response)) {
                    policyNotSatisfied = false;
                    executionContext = pcrfServicePolicy.handleRequest(request, response);
                    break;
                }
            }

            if (policyNotSatisfied) {
                response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
                LogManager.getLogger().error(MODULE, "No Pcrf Service Policy satisfied for the Request");
                return null;
            }


            if (response.isProcessingCompleted()) {
                finalPostResponseProcess(request, response);
            } else {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Skipping final Post Response Processing. Reason: processing is not completed of response with session-Id: "
                            + response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val));
            }

            return executionContext;

        }

        private ExecutionContext resumeServiceRequest(PCRFRequest request,
                                                      PCRFResponse response, ExecutionContext executionContext) {
            boolean policyNotSatisfied = true;
            for (PCRFServicePolicy pcrfServicePolicy : pcrfServicePolicyList) {
                if (pcrfServicePolicy.assignRequest(request, response)) {
                    policyNotSatisfied = false;
                    pcrfServicePolicy.resume(request, response, executionContext);
                    break;
                }
            }

            if (policyNotSatisfied) {
                response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
                LogManager.getLogger().error(MODULE, "No PCRFf Service Policy satisfied for the Request");
                return null;
            }


            if (response.isProcessingCompleted()) {
                finalPostResponseProcess(request, response);
            } else {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Skipping final Post Response Processing. Reason: processing is not completed of response with session-Id: "
                            + response.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val));
            }


            return executionContext;

        }

        private void finalPostResponseProcess(PCRFRequest request, PCRFResponse response) {
            try {


                if (Objects.equals(SessionTypeConstant.RO.val, request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val))) {
                    if (request.getPCRFEvents().contains(PCRFEvent.SESSION_START) == false) {
                        thresholdNotificationProcessor.process(request, response);
                    }
                } else {

                    if (request.getPCRFEvents().contains(PCRFEvent.USAGE_REPORT)) {
                        usageNotificationProcessor.process(request, response);
                    } else if (request.getPCRFEvents().contains(PCRFEvent.QUOTA_MANAGEMENT)) {
                        quotaNotificationProcessor.process(request, response);
                    }
                }


            } catch (Exception ex) {
                LogManager.getLogger().error(MODULE, "Error while processing notification. Reason: " + ex.getMessage());
                LogManager.getLogger().trace(MODULE, ex);
            }

        }


        private void preProcessRequest() {

            String gatewayName = request.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val);

            if (gatewayName != null) {
                if (SessionTypeConstant.RADIUS.val.equals(request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val))) {
                    RadiusGatewayConfiguration radiusGatewayConfiguration = serverContext.getServerConfiguration().getRadiusGatewayConfigurationByName(gatewayName);
                    request.setAttribute(PCRFKeyConstants.REVALIDATION_MODE.val, radiusGatewayConfiguration.getRevalidationMode().val);
                    if (radiusGatewayConfiguration.isPCCLevelMonitoringSupported()) {
                        request.setAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val, PCRFKeyValueConstants.PCC_LEVEL_MONITORING_SUPPORTED.val);
                    } else {
                        request.setAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val, PCRFKeyValueConstants.PCC_LEVEL_MONITORING_NOT_SUPPORTED.val);
                    }
                } else {
                    DiameterGatewayConfiguration diameterGatewayConfiguration = serverContext.getServerConfiguration().getDiameterGatewayConfByName(gatewayName);
                    request.setAttribute(PCRFKeyConstants.REVALIDATION_MODE.val, diameterGatewayConfiguration.getRevalidationMode().val);
                    if (diameterGatewayConfiguration.isPCCLevelMonitoringSupported()) {
                        request.setAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val, PCRFKeyValueConstants.PCC_LEVEL_MONITORING_SUPPORTED.val);
                    } else {
                        request.setAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val, PCRFKeyValueConstants.PCC_LEVEL_MONITORING_NOT_SUPPORTED.val);
                    }
                }
            } else {
                if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
                    LogManager.getLogger().warn(MODULE, "Gateway name not found from pcrf request");
            }


            try {
                setTACDetails();
            } catch (Exception e) {
                if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
                    LogManager.getLogger().warn(MODULE, "TAC details are not set. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
            try {
                setUserLocationDetails();
            } catch (Exception e) {
                if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
                    LogManager.getLogger().warn(MODULE, "Location details are not set. Reason: " + e.getMessage());
                }
                LogManager.getLogger().trace(MODULE, e);
            }

            for (String policyAttributeKey : request.getKeySet()) {
                response.setAttribute(policyAttributeKey, request.getAttribute(policyAttributeKey));
            }

            preProcessors.forEach(preProcessor -> preProcessor.process(request, response));

            response.setInterfaceQueueTime(request.getInterfaceQueueTimeInMillies());
            response.setInterfacePacketCreateTime(request.getInterfacePacketCreateTime());
            response.setUsageReservations(request.getUsageReservations());
            if(Objects.nonNull(request.getQuotaReservation())){
                response.setQuotaReservation(request.getQuotaReservation().copy());
            } else {
                response.setQuotaReservation(request.getQuotaReservation());
            }

            if(Objects.nonNull(request.getUnAccountedQuota())){
                response.setUnAccountedQuota(request.getUnAccountedQuota().copy());
            } else {
                response.setUnAccountedQuota(request.getUnAccountedQuota());
            }
            response.setSubscriptions(request.getSubscriptions());
            response.setReportedUsageInfoList(request.getReportedUsageInfoList());
            response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);
            response.setSessionStartTime(request.getSessionStartTime());
            response.setMediaComponents(request.getMediaComponents());
            response.setPreviousActiveAFSessions(request.getAFActivePCCRule());
            response.setSessionUsage(request.getSessionUsage());
            response.setSessionLoadTime(request.getSessionLoadTime());
            request.getDiagnosticInformation().forEach(response::addDiagnosticInformation);
        }

        private void updateUsageStatistics(PCRFRequest request) {

            List<UsageMonitoringInfo> reportedUsageInfoList = request.getReportedUsageInfoList();

            if (Collectionz.isNullOrEmpty(reportedUsageInfoList)) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping updating usage statistics. Reason: Reported usage list not found from pcrf Request");
                }
                return;
            }

            if (PCRFKeyValueConstants.PCC_LEVEL_MONITORING_NOT_SUPPORTED.val.equals(request.getAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val))) {
                for (int index = 0; index < reportedUsageInfoList.size(); index++) {
                    UsageMonitoringInfo monitoringInfo = reportedUsageInfoList.get(index);
                    if (monitoringInfo.getUsageMonitoringLevel() == UMLevel.SESSION_LEVEL) {
                        usageMonitoringStatistics.incrementHourlyUsage(monitoringInfo.getUsedServiceUnit().getTotalOctets());
                        break;
                    }
                }
            } else {
                for (int index = 0; index < reportedUsageInfoList.size(); index++) {
                    UsageMonitoringInfo monitoringInfo = reportedUsageInfoList.get(index);
                    if (monitoringInfo.getUsageMonitoringLevel() == UMLevel.PCC_RULE_LEVEL) {
                        usageMonitoringStatistics.incrementHourlyUsage(monitoringInfo.getUsedServiceUnit().getTotalOctets());
                    }
                }
            }
        }

        @Override
        public void run() {
            try {
                //checkForPCRFMonitor put before pre processing to get log of preprocess log monitor
                //NOTE : not to put this code in preprocess
                long startTime = System.currentTimeMillis();

                ThreadContext.putAll(contextInformation);

                if(request.getPCRFEvents().contains(PCRFEvent.REAUTHORIZE)) {
                    cleanContextInformation();
                    String sessionId = request.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val);
                    if(Strings.isNullOrBlank(sessionId) == false){
                        ThreadContext.put(SID, sessionId);
                    }
                    String sessionType = request.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val);
                    if(Strings.isNullOrBlank(sessionType) == false){
                        ThreadContext.put(SESSION_TYPE, sessionType);
                    }
                }

                String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
                if(Objects.nonNull(subscriberIdentity)) {
                    ThreadContext.put(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity);
                }

                try {
                    if (pcrfMonitor != null && pcrfMonitor.evaluate(request, response)) {
                        LogManager.getLogger().addThreadName(Thread.currentThread().getName());
                        request.setParameter(MonitorLogger.MONITORED, true);
                        response.setParameter(MonitorLogger.MONITORED, true);
                    }
                } catch (Exception ex) {
                    if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                        LogManager.getLogger().error(MODULE, "Error in applying PCRFService monitor log. Reason: " + ex.getMessage());
                    LogManager.getLogger().trace(MODULE, ex);
                }

                    /*
                     * If execution context is null
                     * 	 then we assume service processing is for fresh request so do preProcessing.
                     *
                     * else
                     * 		service processing is for resuming service request for which we already have done preProcessing
                     */
                if (executionContext == null) {

                    preProcessRequest();
                }

                if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                    LogManager.getLogger().info(MODULE, "Received PCRF Request : " + request.toString());

                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Enriched PCRF Response: " + response.toString());
                }

                try {
                        /*
                         * If execution context is null
                         * 	 then we assume service processing is for fresh request
                         *
                         * else
                         * 	service processing is for resuming service request for which we already have done preProcessing
                         */
                    if (this.executionContext == null) {
                        long engineQueueTime = startTime - reqRcvdTimemili;
                        response.setEngineQueueime(engineQueueTime);
                        executionContext = executeServiceRequest(request, response);
                    } else {
                        long resumeTime = startTime - reqRcvdTimemili;
                        response.setResumeTime(resumeTime);
                        executionContext = resumeServiceRequest(request, response, executionContext);
                    }


                } catch (Exception e) {
                    if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                        LogManager.getLogger().error(MODULE, "Error in proccessing PCRFRequest. Reason: " + e.getMessage());
                    LogManager.getLogger().trace(MODULE, e);
                }

                if (response.isProcessingCompleted()) {
                    if (responseListner != null) {
                        try {
                            responseListner.responseReceived(response);
                        } catch (Exception e) {
                            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                                LogManager.getLogger().error(MODULE, "Error in proccessing PCRFResponse. Reason: " + e.getMessage());
                            LogManager.getLogger().trace(MODULE, e);
                        }
                    } else {
                        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                            LogManager.getLogger().debug(MODULE, "Skip calling to PCRFResponseListener for PCRFResponse of user " + request.getAttribute(PCRFKeyConstants.CS_USER_IDENTITY.getVal()) + ". Reason: responseListener is not provided");
                    }

                        /*
                         * PostProcessin only called when processing is completed for response
                         */
                    postProcess();


                    incrementResponseCounters(response);

                    totalReqProcessed.incrementAndGet();
                    long engineQueueTime = response.getEngineQueueTime() + (response.getResumeTime() == -1 ? 0 : response.getResumeTime());

                    long totalProcessingTimeMS;
                    long currentTime = System.currentTimeMillis();
                    long serviceExecutionTime = (currentTime - executionContext.getCurrentTime().getTimeInMillis()) + (engineQueueTime > 0 ? engineQueueTime : 0);
                    requestProcessingTimeMS.addAndGet(serviceExecutionTime);

                    if (response.getInterfacePacketCreateTime() > 0) {
                        totalProcessingTimeMS = currentTime - response.getInterfacePacketCreateTime();
                    } else {
                        totalProcessingTimeMS = serviceExecutionTime;
                    }


                    traceProcessingTime(engineQueueTime, totalProcessingTimeMS);

                } else {

                    if (executionContext != null) {
                        executionContext.setParameter(ExecutionContextKey.RESPONSE_LISTENER, responseListner);
                        executionContext.setParameter(ExecutionContextKey.PCCRULE_EXPIRY_LISTENER, pccRuleExpiryListener);
                    } else {
                        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                            LogManager.getLogger().debug(MODULE, "Unable to set ResponseListener and ExpiryListner in ExecutionContext. Reason: ExecutionContext is null");
                    }

                    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                        LogManager.getLogger().debug(MODULE, "Skip calling to PCRFResponseListener for PCRFResponse of user " + request.getAttribute(PCRFKeyConstants.CS_USER_IDENTITY.getVal()) + ". Reason: processing is not completed");
                }

                //below code must be executed
                //NOTE : not to put this code in postProcess
                try {
                    if (request.getParameter(MonitorLogger.MONITORED) != null)
                        LogManager.getLogger().removeThreadName(Thread.currentThread().getName());
                } catch (Exception ex) {
                    if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                        LogManager.getLogger().error(MODULE, "Error in removing PCRFService monitor log with Thread name: " + Thread.currentThread().getName() + ". Reason: " + ex.getMessage());
                    LogManager.getLogger().trace(MODULE, ex);
                }

            } finally {
                cleanContextInformation();
            }

        }

        private void traceProcessingTime(long engineQueueTime, long totalProcessingTimeMS) {
                    if (totalProcessingTimeMS > pcrfRequestMaxProcessingTimeThreshold) {

                        long interfaceQueueTime = response.getInterfaceQueueTime();
                        long syCommunicationTime = response.getSyCommunicationTime();
                        long syPacketQueueTime = response.getSyPacketQueueTime();
                        long sprFetchtime = response.getSPRfetchTime();
                        long sprReadTime = response.getSPRReadTime();
                        long usageFetchtime = response.getUsageFetchTime();
                        long usageReadTime = response.getUsageReadTime();
                        long subscriptionsFetchtime = response.getSubscriptionsFetchTime();
                        long subscriptionsReadTime = response.getSubscriptionsReadTime();
                        long sessionLoadTime = response.getSessionLoadTime();

                        if (getLogger().isWarnLogLevel()) {

                            StringBuilder stringBuilder = new StringBuilder(1000);
                            stringBuilder.append("Request Processing Time(ms): ").append(String.format("%5d", totalProcessingTimeMS))
                                    .append(" Interface Queue: ").append(String.format("%5d", interfaceQueueTime))
                                    .append(" Engine Queue: ").append(String.format("%5d", engineQueueTime))
                                    .append(" SPR-Get: ").append(String.format("%5d", sprFetchtime))
                                    .append(" SPR-Read: ").append(String.format("%5d", sprReadTime))
                                    .append(" Usage-Get: ").append(String.format("%5d", usageFetchtime))
                                    .append(" Usage-Read: ").append(String.format("%5d", usageReadTime))
                                    .append(" Subscriptions-Get: ").append(String.format("%5d", subscriptionsFetchtime))
                                    .append(" Subscriptions-Read: ").append(String.format("%5d", subscriptionsReadTime))
                                    .append(" Sy Communication: ").append(String.format("%5d", syCommunicationTime))
                                    .append(" Sy Packet Queue: ").append(String.format("%5d", syPacketQueueTime))
                                    .append(" Session-Load: ").append(String.format("%5d", sessionLoadTime));

                            if (response.getHandlerNameToProcessingTime().isEmpty() == false) {
                                for (Entry<String, Long> handlerNameToProcessingTimeEntry : response.getHandlerNameToProcessingTime().entrySet()) {
                                    stringBuilder.append(" " + handlerNameToProcessingTimeEntry.getKey() + " Handler Time: " + handlerNameToProcessingTimeEntry.getValue());
                                }
                            }

                            if (MapUtils.isNotEmpty(response.getDiagnosticInformation())) {
                                for (Entry<String, String> diagnosticInfomationEntry : response.getDiagnosticInformation().entrySet()) {
                                    stringBuilder.append(" " + diagnosticInfomationEntry.getKey() + " : " + diagnosticInfomationEntry.getValue());
                                }
                            }
                            stringBuilder.append(" Session-ID: ").append(response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));

                            getLogger().warn(MODULE, stringBuilder.toString());
                        }
                    }
        }

        private void cleanContextInformation() {
            ThreadContext.clearAll();
        }

        private void postProcess() {
            if (serverContext.getServerConfiguration().getMiscellaneousParameterConfiguration().getRAREnabled() == true) {
                if (request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)) {
                    pccRuleExpiryManager.onStop(response);
                } else {
                    if (response.getRevalidationTime() != null &&
                            pccRuleExpiryListener != null &&
								/*
								 * provide negative check, As We need to allow rescheduling when
								 * 	1) Revalidation Mode is client initiated
								 *  2) Revalidation Mode is not found in request
								 */
                            (PCRFKeyValueConstants.REVALIDATION_MODE_CLIENT_INITIATED.val.equals(response.getAttribute(PCRFKeyConstants.REVALIDATION_MODE.getVal())) == false)) {
                        pccRuleExpiryManager.onInitialOrUpdate(response, pccRuleExpiryListener);
                    } else {
                        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                            LogManager.getLogger().debug(MODULE, "Skipping reschedule PCRFRequest operation for PCCRule expiry for user "
                                    + request.getAttribute(PCRFKeyConstants.CS_USER_IDENTITY.getVal())
                                    + ". Reason: revalidation mode is not server initiated, revalidation time or PCC rule expiry listener or session id might be null");
                    }
                }
            } else {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Skipping check for rescheduling PCRFRequest. Reason: RAR is disabled");
            }

            updateUsageStatistics(request);
        }


        private void setTACDetails() {

            String strTAC = request.getAttribute(PCRFKeyConstants.USER_EQUIPMENT_TAC.getVal());
            if (strTAC == null) {
                return;
            }

            TACDetail tacDetails = serverContext.getDeviceManager().getTACDetail(strTAC);
            if (tacDetails == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                    LogManager.getLogger().info(MODULE, "TAC Details not found for TAC : " + strTAC);
                return;
            }

            request.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_MODEL.getVal(), tacDetails.getModel());
            request.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_BRAND.getVal(), tacDetails.getBrand());
            request.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_HWTYPE.getVal(), tacDetails.getHwType());
            request.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_OS.getVal(), tacDetails.getOs());
            request.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_ADDITIONALINFO.getVal(), tacDetails.getAdditionalInfo());
        }

        //finding the geopgraphical information based on location info attributes.
        private void setUserLocationDetails() {
            if (serverContext.isLocationBasedServicesEnabled() == false) {
                if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                    LogManager.getLogger().info(MODULE, "Skipping setting geographical location information, Reason: Location based service is disabled by license");
                return;
            }
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Setting geographical location information into pcrf request");
            }
            String strLocationType = request.getAttribute(PCRFKeyConstants.LOCATION_TYPE.getVal());
            PCRFKeyValueConstants userLocationType = PCRFKeyValueConstants.fromString(PCRFKeyConstants.LOCATION_TYPE, strLocationType);
            if (userLocationType == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "Geographical location information can't be set. Reason: Invalid user location type found");
                }
                return;
            }
            String mcc;
            String mnc;
            String lac;
            LocationInformationConfiguration locationInformationConfiguration = null;

            mcc = request.getAttribute(PCRFKeyConstants.LOCATION_MCC.getVal());
            if (mcc == null || mcc.length() == 0) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "Geographical location information can't be set. Reason: MCC not found");
                }
                return;
            }
            mnc = request.getAttribute(PCRFKeyConstants.LOCATION_MNC.getVal());
            lac = request.getAttribute(PCRFKeyConstants.LOCATION_LAC.getVal());
            if (userLocationType == PCRFKeyValueConstants.LOCATION_TYPE_CGI) {
                String ci = request.getAttribute(PCRFKeyConstants.LOCATION_CI.getVal());
                locationInformationConfiguration = serverContext.getLocationRepository().getLocationInformationByCGI(mcc, mnc, lac, ci);

            } else if (userLocationType == PCRFKeyValueConstants.LOCATION_TYPE_SAI) {
                String sac = request.getAttribute(PCRFKeyConstants.LOCATION_SAC.getVal());
                locationInformationConfiguration = serverContext.getLocationRepository().getLocationInformationBySAI(mcc, mnc, lac, sac);

            } else if (userLocationType == PCRFKeyValueConstants.LOCATION_TYPE_RAI) {
                String rac = request.getAttribute(PCRFKeyConstants.LOCATION_RAC.getVal());
                locationInformationConfiguration = serverContext.getLocationRepository().getLocationInformationByRAI(mcc, mnc, lac, rac);
            }

            if (locationInformationConfiguration == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "Geographical location information can't be set. Reason: No location information found");
                }
                return;
            }


            request.setAttribute(PCRFKeyConstants.LOCATION_ID.getVal(), locationInformationConfiguration.getLocationId());
            if (locationInformationConfiguration.getArea() != null)
                request.setAttribute(PCRFKeyConstants.LOCATION_AREA.getVal(), locationInformationConfiguration.getArea());
            if (locationInformationConfiguration.getCity() != null)
                request.setAttribute(PCRFKeyConstants.LOCATION_CITY.getVal(), locationInformationConfiguration.getCity());
            if (locationInformationConfiguration.getRegion() != null)
                request.setAttribute(PCRFKeyConstants.LOCATION_REGION.getVal(), locationInformationConfiguration.getRegion());
            if (locationInformationConfiguration.getCountry() != null)
                request.setAttribute(PCRFKeyConstants.LOCATION_COUNTRY.getVal(), locationInformationConfiguration.getCountry());
            if(locationInformationConfiguration.getCountry() != null)
                request.setAttribute(PCRFKeyConstants.LOCATION_GEOGRAPHY.getVal(), locationInformationConfiguration.getGeography());
            if (locationInformationConfiguration.getParam1() != null)
                request.setAttribute(PCRFKeyConstants.LOCATION_PARAM1.getVal(), locationInformationConfiguration.getParam1());
            if (locationInformationConfiguration.getParam2() != null)
                request.setAttribute(PCRFKeyConstants.LOCATION_PARAM2.getVal(), locationInformationConfiguration.getParam2());
            if (locationInformationConfiguration.getParam3() != null)
                request.setAttribute(PCRFKeyConstants.LOCATION_PARAM3.getVal(), locationInformationConfiguration.getParam3());
            String oldCongestioStatus = request.getAttribute(PCRFKeyConstants.LOCATION_NEW_CONGESTION_STATUS.getVal());
            request.setAttribute(PCRFKeyConstants.LOCATION_OLD_CONGESTION_STATUS.getVal(), oldCongestioStatus);
            request.setAttribute(PCRFKeyConstants.LOCATION_NEW_CONGESTION_STATUS.getVal(), String.valueOf(locationInformationConfiguration.getCongestionStatus()));

        }
    }

    public void incrementResponseCounters(PCRFResponse response) {
        pcrfServiceCounters.incTotalPCRFRespCntr();
        if (PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(response.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()))) {
            pcrfServiceCounters.incTotalSuccessPCRFRespCntr();
        } else {
            pcrfServiceCounters.incTotalRejectPCRFRespCntr();
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public ServiceDescription getDescription() {
        return new ServiceDescription(getServiceIdentifier(), getStatus(),
                "N.A.", getStartDate(), getRemarks());
    }

    @Override
    protected ServiceContext getServiceContext() {
        return pcrfServiceContext;
    }

    /**
     * Initialise PCRF Service
     *
     * @throws ServiceInitializationException
     */
    @Override
    protected void initService() throws ServiceInitializationException {
        LogManager.getLogger().info(MODULE, "Initializing PCRF Service");
        try {

            initUsageStatistics();

            pcrfServiceContext = createPCRFServiceContext();

            initServicePolicy();

            pcrfServiceCounters = new PCRFServiceCounters(pcrfServiceContext);
            pcrfServiceStatisticsProvider = new PCRFServiceStatisticsProvider(pcrfServiceCounters, pcrfServiceContext);
            PCRFServiceStatisticsDetailProvider pcrfServiceStatisticsDetailProvider = new PCRFServiceStatisticsDetailProvider(pcrfServiceStatisticsProvider);
            StatisticsDetailProvider.getInstance().registerDetailProvider(pcrfServiceStatisticsDetailProvider);

            createPCRFMonitor();
            // Device manager initialization completed
            initialRequestQueue = new LinkedBlockingQueue<Runnable>(getMaxRequestQueueSize());

            synchronousTaskExecutor = new ThreadPoolExecutor(getMinThreadPoolSize(), getMaxThreadPoolSize(), getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, initialRequestQueue);
            synchronousTaskExecutor.setThreadFactory(new EliteThreadFactory(getServiceIdentifier(), getServiceIdentifier(), getWorkerThreadPriority()));
            synchronousTaskExecutor.prestartAllCoreThreads();

            pccRuleExpiryManager = PCCRuleExpiryManager.create(serverContext, sessionOperation.getSessionLocator());

            serverContext.getTaskScheduler().scheduleIntervalBasedTask(new TPMCalculator());
            setRevalidationTimeDelta();

            createRequestPreprocessors();

            getLogger().info(MODULE, "PCRF Service initialization completed");
        } catch (NoClassDefFoundError e) {
            getServerContext().generateSystemAlert("", Alerts.PCRF_STARTUP_FAILED, MODULE, "PCRF Service initialization failed. Reason: " + e.getMessage());
            throw new ServiceInitializationException(e.getMessage(), ServiceRemarks.MISSING_JAR_FILE, e);
        } catch (Exception e) {
            getServerContext().generateSystemAlert("", Alerts.PCRF_STARTUP_FAILED, MODULE, "PCRF Service initialization failed. Reason: " + e.getMessage());
            throw new ServiceInitializationException(e.getMessage(), ServiceRemarks.UNKNOWN_PROBLEM, e);

        }
    }

    private void createRequestPreprocessors() {
        requestHandlerPreProcessors.add(new OperatorNetworkInfoOrchestrator(serverContext));
        requestHandlerPreProcessors.add(new SubscriberNetworkInfoOrchestrator(serverContext));
        requestHandlerPreProcessors.add(new CalledPartyLRNAndPrefixConfigurationOrchestrator(serverContext));
        requestHandlerPreProcessors.add(new SystemParameterConfigurationOrchestrator(serverContext));
        requestHandlerPreProcessors.add(new RoamingStatusOrchestrator(serverContext));
        requestHandlerPreProcessors.add(new CallTypeStatusOrchestrator(serverContext));
    }

    private void createPCRFMonitor() {
        try {
            pcrfMonitor = new PCRFServiceMonitor(pcrfServiceContext);
            LogMonitorManager.getInstance().registerMonitor(pcrfMonitor);
        } catch (RegistrationFailedException e) {
            LogManager.getLogger().error(MODULE, "PCRFs Log Monitor Command registration failed. Reason: " + e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
        }
    }

    private void initUsageStatistics() {

        usageMonitoringStatistics = new UsageMonitoringStatisticsCounter(serverContext);
        usageMonitoringStatistics.init();
        UsageMonitoringStatisticsProvider monitoringStatisticsProvider = new UsageMonitoringStatisticsProvider(usageMonitoringStatistics);
        UsageMonitoringStatisticsDetailProvider usageStatisticsDetailProvider = new UsageMonitoringStatisticsDetailProvider(monitoringStatisticsProvider);
        try {
            StatisticsDetailProvider.getInstance().registerDetailProvider(usageStatisticsDetailProvider);
        } catch (RegistrationFailedException e) {
            getLogger().error(MODULE, "Error in registering usage monitoring statistics detail providers." +
                    " Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        ClearUsageStatisticsDetailProvider clearUsageStatisticsDetailProvider = new ClearUsageStatisticsDetailProvider(usageMonitoringStatistics);

        try {
            ClearStatisticsDetailProvider.getInstance().registerDetailProvider(clearUsageStatisticsDetailProvider);
        } catch (RegistrationFailedException e) {
            getLogger().error(MODULE, "Error in registering clear usage statistic detail provider. " +
                    "	 Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private PCRFServiceContext createPCRFServiceContext() {
        return new PCRFServiceContextImpl();
    }


    public SPRInfo getSPR(String subscriberIdentity) {
        try {
            return CacheAwareDDFTable.getInstance().searchSubscriber(subscriberIdentity);
        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Error while fetching profile for subscriber ID: " + subscriberIdentity + ". Reason: " + e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
        }
        return null;
    }


    private void initServicePolicy() throws InitializationFailedException {
        LogManager.getLogger().info(MODULE, "Initializing PCRF Service Policies");
        Set<String> pcrfServicePolicyConfList = serverContext.getServerConfiguration().getPCRFServicePolicyNames();

        if (pcrfServicePolicyConfList == null || pcrfServicePolicyConfList.isEmpty()) {
            throw new InitializationFailedException("Invalid configuration. Reason: No service policy configured");
        }
        ServiceHandlerFactory serviceHandlerFactory = new ServiceHandlerFactoryImpl(pcrfServiceContext, sessionOperation);

        for (String servicePolicyName : pcrfServicePolicyConfList) {
            try {
                PccServicePolicyConfiguration servicePolicyConf = serverContext.getServerConfiguration().getPCRFServicePolicyConfByName(servicePolicyName);
                PCRFServicePolicy pcrfServicePolicy = new PCRFServicePolicy(servicePolicyConf, serviceHandlerFactory, pcrfServiceContext);
                pcrfServicePolicy.init();
                pcrfServicePolicyList.add(pcrfServicePolicy);
            } catch (InitializationFailedException e) {
                serverContext.generateSystemAlert(AlertSeverity.ERROR, Alerts.SERVICE_POLICY_INIT_FAILS, MODULE, "Error while initializing Service Policy: " + servicePolicyName + ". Reason: " + e.getMessage());
                throw new InitializationFailedException("Error while initializing Service Policy: " + servicePolicyName + ". Reason: " + e.getMessage(), e);
            }
        }
        LogManager.getLogger().info(MODULE, "PCRF Service Policy initialization completed");
    }

    @Override
    public void readConfiguration() throws LoadConfigurationException {
        //NOTING TO DO
    }
    private boolean executeServiceRequest(@Nonnull Runnable runnable) {
        try {
            synchronousTaskExecutor.execute(runnable);
            return true;
        } catch (RejectedExecutionException rejExp) {
            LogManager.getLogger().trace(MODULE, rejExp);
            return false;
        }
    }


    @Override
    public String getServiceName() {
        return "PCRF Service";

    }

    public long getTotalReqPerMin() {
        return tpm;
    }

    public PCRFServiceStatisticsProvider getStatisticsProvider() {
        return pcrfServiceStatisticsProvider;
    }


    private class DBCDRDriverAlertListener implements AlertListener {

        @Override
        public void generateAlert(Events event, String message) {
            if (event == Events.DB_CONNECTION_NOT_AVAILABLE) {
                getServerContext().generateSystemAlert("", Alerts.DB_NO_CONNECTION, MODULE, message);
            }
        }
    }

    public void setRevalidationTimeDelta() throws InitializationFailedException {
        String revalidationTimeStr = System.getProperty(SYS_PARAMETER_REVALIDATOIN_TIME_DELTA);
        int tempRevalidationTimeDeltaInSeconds;
        if (Strings.isNullOrBlank(revalidationTimeStr) == false) {
            tempRevalidationTimeDeltaInSeconds = (int) Numbers.parseLong(revalidationTimeStr, REVALIDATION_TIME_DELTA_DEFAULT);
            if (tempRevalidationTimeDeltaInSeconds < 0) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Negative value: " + tempRevalidationTimeDeltaInSeconds + " configured in System parameter: " + SYS_PARAMETER_REVALIDATOIN_TIME_DELTA);
                }
                tempRevalidationTimeDeltaInSeconds = (int) REVALIDATION_TIME_DELTA_DEFAULT;
            }

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Considering " + tempRevalidationTimeDeltaInSeconds + "(seconds) from System parameter: "
                        + SYS_PARAMETER_REVALIDATOIN_TIME_DELTA);
            }

        } else {
            tempRevalidationTimeDeltaInSeconds = (int) REVALIDATION_TIME_DELTA_DEFAULT;

            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Considering default revalidation time delta: " + tempRevalidationTimeDeltaInSeconds
                        + "(seconds). Reason: System parameter: " + SYS_PARAMETER_REVALIDATOIN_TIME_DELTA + " is not configured");
            }
        }

        this.revalidationTimeDeltaInSeconds = tempRevalidationTimeDeltaInSeconds;
    }

    @Override
    public void reInit() {
        //NOTING TO DO
    }

    public UsageMonitoringStatisticsProvider getUsageMonitoringStatisticsProvider() {
        return new UsageMonitoringStatisticsProvider(usageMonitoringStatistics);
    }

    private class PCRFServiceContextImpl implements PCRFServiceContext {

        @Override
        public CDRDriver<ValueProviderExtImpl> getCDRDriver(DriverConfiguration driverConfiguration) throws InitializationFailedException {
            return cdrDriverFactory.create(driverConfiguration);
        }

        @Override
        public NetVertexServerContext getServerContext() {
            return serverContext;
        }

        @Override
        public int getRevalidationTimeDelta() {
            return revalidationTimeDeltaInSeconds;
        }

        @Override
        public PCRFServiceConfiguration getPCRFServiceConfiguration() {
            return serviceConfiguration;
        }

        @Override
        public void resume(PCRFRequest pcrfRequest,
                           PCRFResponse pcrfResponse,
                           ExecutionContext executionContext) {

            requestCounter.incrementAndGet();
            pcrfServiceCounters.incTotalPCRFReqCntr();
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Submitting request to PCRF Request queue");
            }

            PCRFResponseListner responseListner = (PCRFResponseListner) executionContext.getParameter(ExecutionContextKey.RESPONSE_LISTENER);
            PCCRuleExpiryListener pccRuleExpiryListener = (PCCRuleExpiryListener) executionContext.getParameter(ExecutionContextKey.PCCRULE_EXPIRY_LISTENER);

            PolicyRequestHandler requestHandler = new PolicyRequestHandler(pcrfRequest, pcrfResponse, executionContext, responseListner, pccRuleExpiryListener, requestHandlerPreProcessors, ThreadContext.getContext());
            boolean isRequestSubmitted = executeServiceRequest(requestHandler);

            if (!isRequestSubmitted) {

                if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
                    LogManager.getLogger().warn(MODULE, "Pausing PCRF request submission. Reason: Request queue is full");
                }

                Thread.yield();
                isRequestSubmitted = executeServiceRequest(requestHandler);
                if (!isRequestSubmitted) {
                    if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
                        LogManager.getLogger().error(MODULE, "PCRF request dropped. Reason: Request queue is full");
                    }
                    pcrfServiceCounters.incTotalPCRFReqDroppedCntr();
                }
            }
        }
    }
}

