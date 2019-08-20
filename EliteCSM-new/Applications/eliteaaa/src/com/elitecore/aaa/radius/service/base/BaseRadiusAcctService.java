/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.radius.service.base;



import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.conf.VSAInClassConfiguration;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.mibs.radius.accounting.client.RadiusAcctClientMIB;
import com.elitecore.aaa.mibs.radius.accounting.client.autogen.RADIUS_ACC_CLIENT_MIB;
import com.elitecore.aaa.mibs.radius.accounting.client.autogen.TableRadiusAccServerTable;
import com.elitecore.aaa.mibs.radius.accounting.client.extended.RADIUS_ACC_CLIENT_MIBImpl;
import com.elitecore.aaa.mibs.radius.accounting.client.extended.RadiusAccServerEntryMBeanImpl;
import com.elitecore.aaa.mibs.radius.accounting.client.extended.TableRadiusAccServerTableImpl;
import com.elitecore.aaa.mibs.radius.accounting.server.RadiusAcctServiceMIBListener;
import com.elitecore.aaa.mibs.radius.accounting.server.autogen.RADIUS_ACC_SERVER_MIB;
import com.elitecore.aaa.mibs.radius.accounting.server.autogen.TableRadiusAccClientTable;
import com.elitecore.aaa.mibs.radius.accounting.server.extended.RADIUS_ACC_SERVER_MIBImpl;
import com.elitecore.aaa.mibs.radius.accounting.server.extended.RadiusAccClientEntryMBeanImpl;
import com.elitecore.aaa.mibs.radius.accounting.server.extended.TableRadiusAccClientTableImpl;
import com.elitecore.aaa.radius.conf.RadAcctConfiguration;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceRequestHash;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceSummaryWriter;
import com.elitecore.aaa.radius.service.acct.RadiusAcctServiceMIBCounters;
import com.elitecore.aaa.radius.service.acct.policy.AcctServicePolicy;
import com.elitecore.aaa.radius.service.acct.policy.AcctServicePolicyFactory;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.util.cli.AcctServCommand;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.configuration.UpdateConfigurationFailedException;
import com.elitecore.core.servicex.IPacketHash;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.sun.management.snmp.SnmpStatusException;



/**
 * 
 * @author baiju
 *
 */
public abstract class BaseRadiusAcctService extends BaseRadiusService<RadAcctRequest, RadAcctResponse> {
	private static final String MODULE = "BS-RAD-ACCT";	
	
	private RadAcctConfiguration radAcctConfiguration ;
	private ArrayList<AcctServicePolicy> acctServicePolicies;
	private RadiusAcctServiceMIBListener acctServiceMIBListener;
	private EliteRollingFileLogger serviceLogger;
	
	//FIXME ELITEAAA-2767 statistic for request related counter was not updated properly when net-mask client is configured
	// so need to add radclient here but remove when proper solution is given.
	private RadClientConfiguration radClientConfiguration;
	
	public BaseRadiusAcctService(AAAServerContext context, 
			DriverManager driverManager,
			RadPluginManager pluginManager,
			RadiusLogMonitor logMonitor) {
		super(context, driverManager, pluginManager, logMonitor);		
		acctServiceMIBListener = new RadiusAcctServiceMIBListener(new RadiusAcctServiceMIBCounters(context));
		this.radAcctConfiguration = context.getServerConfiguration().getAcctConfiguration();
		radClientConfiguration =  context.getServerConfiguration().getRadClientConfiguration();
	}
	
	
	/**
	 * radiusAccServTotalRequests 
	 * DESCRIPTION
	 *     "The number of packets received on the
	 *     accounting port."
	 */
	@Override
	protected void incrementRequestReceivedCounter(String clientAddress){		
		acctServiceMIBListener.listenRadiusAccServTotalRequestsEevnt();
		RadClientData clientData = radClientConfiguration.getClientData(clientAddress);
		if(clientData != null) {
			acctServiceMIBListener.listenRadiusAccServTotalRequestsEvent(clientData.getClientIp());
		}
	}
	/**
	 * radiusAccServTotalResponses 
	 * DESCRIPTION
	 *   "The number of RADIUS Accounting-Response packets sent."
	 */
	@Override
	protected void incrementResponseCounter(ServiceResponse response) {
		RadAcctResponse acctResponse = (RadAcctResponse)response;
		incrementResponseCounter(acctResponse.getClientData().getClientIp(), 
				acctResponse.getPacketType());
	}

	@Override
	protected void incrementResponseCounter(String sourceAddress,
			byte[] responseBytes) {
		RadClientData clientData = ((AAAServerContext)getServerContext()).getServerConfiguration()
			.getRadClientConfiguration().getClientData(sourceAddress);
		
		if (clientData == null) {
			return;
		}
		
		incrementResponseCounter(clientData.getClientIp(), 
				RadiusPacket.parsePacketType(responseBytes));
	}
	
	private void incrementResponseCounter(String clientIp, int packetType) {
		if (packetType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE) {
			acctServiceMIBListener.listenRadiusAccServTotalResponsesEvent(clientIp);		
		}
	}
	
	/**
	 *  radiusAccServTotalUnknownTypes
	 *  DESCRIPTION
	 * "The number of RADIUS packets of unknowntype which
	 *  were received." 
	 */
	private void incrementAcctServTotalUnknownTypes(String clientAddress){
		RadClientData clientData = radClientConfiguration.getClientData(clientAddress);
		if(clientData != null) {
			acctServiceMIBListener.listenRadiusAccServTotalUnknownTypesEvent(clientData.getClientIp());
		}
	}
	
	@Override
	protected void incrementRequestDroppedCounter(ServiceRequest request){
		RadClientData clientData = radClientConfiguration.getClientData(((RadAcctRequest)request).getClientIp());
		if(clientData != null) {
			acctServiceMIBListener.listenRadiusAccServTotalPacketsDroppedEvent(clientData.getClientIp());
		}
	}
	
	@Override
	protected void handleStatusServerMessageRequest(RadAcctRequest serviceRequest, RadAcctResponse serviceResponse) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Status Server message received.");

		serviceResponse.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);

		//as per RFC if request contains Message Authenticator then need to add in response too
		if(serviceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null){
			IRadiusAttribute msgAuthenticatorAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			//as generate packet does add valid bytes
			msgAuthenticatorAttr.setValueBytes(new byte[0]);
			serviceResponse.addAttribute(msgAuthenticatorAttr);
		}
	}
	
	protected void setServicePolicy(RadAcctRequest request){		
		for(AcctServicePolicy acctServicePolicy : acctServicePolicies){
			if(acctServicePolicy.assignRequest(request)){
				((RadiusAcctRequestImpl)request).setServicePolicy(acctServicePolicy);				
				IRadiusAttribute infoAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_SATISFIED_SERVICE_POLICY);
				if(infoAttribute != null){
					infoAttribute.setStringValue(acctServicePolicy.getPolicyName());
					request.addInfoAttribute(infoAttribute);
				}
//				request.setHandlers(((RadiusServicePolicy)acctServicePolicy).getHandlers());
				break;
			}
		}
	}
	public static class RadiusAcctRequestImpl extends RadServiceRequestImpl implements RadAcctRequest {

		AcctServicePolicy servicePolicy;
		private RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> executor;
		private static final int ACCT_DUPLICATE_DETECTION_IDENTIFIER = 0;
		
		public RadiusAcctRequestImpl(byte[] requestBytes, InetAddress sourceAddress, int sourcePort, SocketDetail serverSocketDetail) {
			super(requestBytes, sourceAddress, sourcePort, serverSocketDetail);	
		}
		private void setServicePolicy(AcctServicePolicy servicePolicy){
			this.servicePolicy = servicePolicy;
		}
		@Override
		public AcctServicePolicy getServicePolicy() {
			return servicePolicy;
		}
		
		public void setExecutor(RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> executor) {
			this.executor = executor;
		}
		
		@Override
		public RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> getExecutor() {
			return executor;
		}
		
		@Override
		public RadServiceRequest clone() {
			RadiusAcctRequestImpl clone = (RadiusAcctRequestImpl) super.clone();
			clone.executor = null;
			return clone;
		}
		
		/**
		 * Given compliance of duplicate detection with Acct-Delay-Time AVP [ RFC-2866 ].
		 * This method will detect duplicate accounting request based on Acct-Session-Id and Acct-Status-Type AVP
		 * <br>
		 * <b>NOTE</b> : Now onwards Packet Identifier will not be a part of duplicate detection in Accounting Service. 
		 *        So default identifier will be <code>ACCT_DUPLICATE_DETECTION_IDENTIFIER = 0</code> as request has new 
		 *        identifier when value of the Acct-Delay-Time AVP is changed. 
		 */
		@Override
		public IPacketHash getPacketHash() {
			
			IRadiusAttribute sessionID = getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID);
			IRadiusAttribute statusType = getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
			IRadiusAttribute sessionTime = getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_TIME);
			
			if (sessionID != null && statusType != null) {
				byte[] sessionIDValueBytes = sessionID.getValueBytes();
				byte[] statusTypeValueByte = statusType.getValueBytes();
				byte[] sessionTimeBytes = new byte[0];

				if (sessionTime != null) {
					sessionTimeBytes = sessionTime.getValueBytes();
				}
				
				if (sessionIDValueBytes != null && statusTypeValueByte != null) {
					byte[] resultantValueBytes;				

					resultantValueBytes = new byte[sessionIDValueBytes.length + statusTypeValueByte.length + sessionTimeBytes.length];
					
					System.arraycopy(sessionIDValueBytes, 0, resultantValueBytes, 0, sessionIDValueBytes.length);

					System.arraycopy(statusTypeValueByte, 0, resultantValueBytes, sessionIDValueBytes.length, statusTypeValueByte.length);

					System.arraycopy(sessionTimeBytes, 0, resultantValueBytes, sessionIDValueBytes.length + statusTypeValueByte.length, sessionTimeBytes.length);
					
					return new RadServiceRequestHash(getClientPort(), ACCT_DUPLICATE_DETECTION_IDENTIFIER, resultantValueBytes);
	}
			}
			return super.getPacketHash();
		}
	}

	public static class RadiusAcctResponseImpl extends RadServiceResponseImpl implements RadAcctResponse {

		public RadiusAcctResponseImpl(byte[] authenticator,int identifier) {
			super(authenticator, identifier);
			setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
		}
		
		@Override
		public RadiusPacket generatePacket() {
			RadiusPacket responsePacket = new RadiusPacket();			
			responsePacket.setPacketType(getPacketType());
			responsePacket.setIdentifier(getIdentifier());
			responsePacket.addAttributes(getAttributeList());
			//responsePacket.reencryptAttributes(null,null, getRequestAuthenticator()	, getClientData().getSharedSecret());
			responsePacket.refreshPacketHeader();
	
			IRadiusAttribute msgAuthenticatorAttribute = responsePacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			if (msgAuthenticatorAttribute != null) {
				msgAuthenticatorAttribute.setValueBytes(RadiusUtility.generateMessageAuthenticator(responsePacket.getBytes(), getRequestAuthenticator(), getClientData().getSharedSecret(getPacketType())));
			}
			
			responsePacket.refreshPacketHeader();
			responsePacket.refreshInfoPacketHeader();
			responsePacket.setAuthenticator(RadiusUtility.generateRFC2865ResponseAuthenticator((responsePacket),getRequestAuthenticator(),getClientData().getSharedSecret(getPacketType())));
			return responsePacket;
		}
		
		@Override
		public RadServiceResponse clone() {
			return super.clone();
		}
	}

	@Override
	protected void initService() throws ServiceInitializationException{
		super.initService();
		LogManager.getLogger().info(MODULE, String.valueOf(radAcctConfiguration));
		if (radAcctConfiguration.isServiceLevelLoggerEnabled()) {
			serviceLogger = 
				new EliteRollingFileLogger.Builder(getServerContext().getServerInstanceName(),
						radAcctConfiguration.getLogLocation())
				.rollingType(radAcctConfiguration.logRollingType())
				.rollingUnit(radAcctConfiguration.logRollingUnit())
				.maxRolledUnits(radAcctConfiguration.logMaxRolledUnits())
				.compressRolledUnits(radAcctConfiguration.isCompressLogRolledUnits())
				.sysLogParameters(radAcctConfiguration.getSysLogConfiguration().getHostIp(),
					radAcctConfiguration.getSysLogConfiguration().getFacility())
				.build();
			
				serviceLogger.setLogLevel(radAcctConfiguration.logLevel());
			LogManager.setLogger(getServiceIdentifier(), serviceLogger);
		}
		initServicePolicy();
		LogManager.getLogger().info(MODULE,"Radius Accounting service initilized successfully.");
		LogManager.getLogger().info(MODULE, servicePolicyCacheToString());
		
		
		RadClientConfiguration radClientConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration();
		if(radClientConfiguration != null){
			this.acctServiceMIBListener.init();
		}else{
			LogManager.getLogger().error(MODULE, "Can not initialize MIB counters for clients. Client configuration is null");			
		}
		
		registerServiceSummaryWriter(new RadAcctServiceSummaryWriter((RadAcctServiceContext)getServiceContext()));
		
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		
		try {
			RADIUS_ACC_CLIENT_MIB radius_ACC_CLIENT_MIB = new RADIUS_ACC_CLIENT_MIBImpl();
			radius_ACC_CLIENT_MIB.populate(mbeanServer, null);
			TableRadiusAccServerTable accServerTable = new TableRadiusAccServerTableImpl(radius_ACC_CLIENT_MIB,mbeanServer);
			Map<String, String> serverMap = RadiusAcctClientMIB.getServerMap();
			int serverIndex = 1;
			String acctServerIdentity = "";
			try{
				for (Entry<String, String> entry : serverMap.entrySet()) {
					RadiusAccServerEntryMBeanImpl acctServerEntry = new RadiusAccServerEntryMBeanImpl(serverIndex++, entry.getValue());
					acctServerIdentity = entry.getValue();
					accServerTable.addEntry(acctServerEntry,new ObjectName(acctServerEntry.getObjectName()));
				}
			}catch(SnmpStatusException sse){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Acct Server: "+acctServerIdentity+" in Acct Server table. Reason: "+sse.getMessage());
				}
				LogManager.getLogger().trace(sse);
			}catch (MalformedObjectNameException ex) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Acct Server: "+acctServerIdentity+" in Acct Server table. Reason: "+ex.getMessage());
				}
				LogManager.getLogger().trace(ex);
			}
			RadiusAcctClientMIB.setAcctServerTable(accServerTable);
			((AAAServerContext)getServerContext()).registerSnmpMib(radius_ACC_CLIENT_MIB);
		} catch (Throwable t) {
			LogManager.getLogger().error(MODULE, "RADIUS Accounting clinet MIB registration failed. Reason: " + t.getMessage());
			LogManager.getLogger().trace(MODULE, t);
		}
	
		try {
			RADIUS_ACC_SERVER_MIB radius_ACC_SERVER_MIB = new RADIUS_ACC_SERVER_MIBImpl();
			radius_ACC_SERVER_MIB.populate(mbeanServer, null);
			TableRadiusAccClientTable accClientTable = new TableRadiusAccClientTableImpl(radius_ACC_SERVER_MIB,mbeanServer);
			Map<String, String> clientMap = RadiusAcctServiceMIBListener.getClientMap();
			int clientIndex = 1;
			String acctClientIdentity = "";
			try{
				for (Entry<String, String> entry : clientMap.entrySet()) {
					RadiusAccClientEntryMBeanImpl acctClientEntry = new RadiusAccClientEntryMBeanImpl(clientIndex++, entry.getValue());
					acctClientIdentity = entry.getValue();
					accClientTable.addEntry(acctClientEntry,new ObjectName(acctClientEntry.getObjectName()));
				}
			}catch(SnmpStatusException sse){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Acct Client: "+acctClientIdentity+" in Acct Client table. Reason: "+sse.getMessage());
				}
				LogManager.getLogger().trace(sse);
			}catch (MalformedObjectNameException ex) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Acct Client: "+acctClientIdentity+" in Acct Client table. Reason: "+ex.getMessage());
				}
				LogManager.getLogger().trace(ex);
			}
			
			RadiusAcctServiceMIBListener.setTableRadiusAccClientTable(accClientTable);
			((AAAServerContext)getServerContext()).registerSnmpMib(radius_ACC_SERVER_MIB);
		} catch (Exception t) {
			LogManager.getLogger().error(MODULE, "RADIUS Accounting server MIB registration failed. Reason: " + t.getMessage());
			LogManager.getLogger().trace(MODULE, t);
		}
		
	}

	private void initServicePolicy(){
		ArrayList<AcctServicePolicy> tmpAcctServicePolicies= new ArrayList<AcctServicePolicy>();	
	
		RadiusServicePolicyConfigurable servicePolicyConfigurable = ((AAAServerContext)getServerContext()).getServerConfiguration().getRadiusServicePolicyConfiguration();
		AcctServicePolicyFactory servicePolicyFactory = new AcctServicePolicyFactory((RadAcctServiceContext) getServiceContext());
		for(RadiusServicePolicyData policyData : servicePolicyConfigurable.getAccountingFlowPolicies()) {
			if(policyData.getSupportedMessages().isAccountingMessageEnabled()) {
				try { 
					AcctServicePolicy servicePolicy = servicePolicyFactory.create(policyData.getAccountingPolicyData());
					tmpAcctServicePolicies.add(servicePolicy);
				} catch(Exception e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Failed to Initialize the Service Policy " +  policyData.getName() + ". Reason : " + e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				}
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Accounting message is disabled in radius service policy: " + policyData.getName());
				}
			}
		}
		
		acctServicePolicies = tmpAcctServicePolicies;
	}
	
	private String servicePolicyCacheToString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();		
		out.println();
		out.println(" -- Rad Acct Service Policies -- ");
		out.println();

		if (acctServicePolicies != null && !acctServicePolicies.isEmpty()) {
			for(AcctServicePolicy servicePolicy:acctServicePolicies) {
				out.println(servicePolicy.toString());
			}
		} else {
			out.println("No policy available in the cache");
		}
		out.close();
		return stringBuffer.toString();
	}
	
	public RadAcctConfiguration getRadAcctConfiguration() {
		return radAcctConfiguration;
	}

	@Override
	public final RadAcctRequest formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
		return new RadiusAcctRequestImpl(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
	}

	@Override
	public final RadAcctResponse formServiceSpecificResposne(RadAcctRequest serviceRequest) {
		return new RadiusAcctResponseImpl(serviceRequest.getAuthenticator(),serviceRequest.getIdentifier());
	}
	@Override
	public List<ICommand> getCliCommands() {
		List<ICommand> cmdList = new ArrayList<ICommand>();		
		cmdList.add(new AcctServCommand(getServerContext()) {
			
			@Override
			public String getServicepolicyList() {				
				return getServicePolicyNames();
			}
			
			@Override
			public String getServiceThreadSummary() {				
				return getThreadSummary();
			}
			
			@Override
			public String getServicePolicyDetail(String commandParameters) {		
				StringWriter stringBuffer = new StringWriter();
				PrintWriter out = new PrintWriter(stringBuffer);
				AcctServicePolicy servicePolicy = getServicePolicy(commandParameters);
				if(servicePolicy!=null){
					out.println(servicePolicy);
					out.close();
					return stringBuffer.toString();
				}
				out.println();
				out.println("Service Policy: "+ commandParameters+" is not available in the cache");		
				out.close();
				return stringBuffer.toString();
			
			}
		});
		return cmdList;
	}
	
	
	public boolean updateConfiguration(List lstConfiguration) throws UpdateConfigurationFailedException,LoadConfigurationException {
		return true;
	}

	public RadAcctConfiguration getRadAuthConfiguration() {
		return radAcctConfiguration;
	}
	
	@Override
	protected synchronized boolean startService() {
		return super.startService();
	}
	
	@Override
	protected int getMainThreadPriority() {
		return radAcctConfiguration.mainThreadPriority();
	}

	@Override
	protected int getMaxRequestQueueSize() {
		return radAcctConfiguration.maxRequestQueueSize();
	}

	@Override
	protected int getMaxThreadPoolSize() {
		return radAcctConfiguration.maxThreadPoolSize();
	}

	@Override
	protected int getMinThreadPoolSize() {
		return radAcctConfiguration.minThreadPoolSize();
	}

	@Override
	protected int getSocketReceiveBufferSize() {
		return radAcctConfiguration.socketReceiveBufferSize();
	}

	@Override
	protected int getSocketSendBufferSize() {
		return radAcctConfiguration.socketSendBufferSize();
	}

	@Override
	protected int getThreadKeepAliveTime() {
		return radAcctConfiguration.threadKeepAliveTime();
	}

	@Override
	protected int getWorkerThreadPriority() {
		return radAcctConfiguration.workerThreadPriority();
	}
	
	@Override
	public String getKey() {
		return this.radAcctConfiguration.getKey();
	}
	
	@Override
	public boolean isDuplicateDetectionEnabled() {
		return radAcctConfiguration.isDuplicateRequestDetectionEnabled();
	}

	@Override
	public int getDuplicateDetectionQueuePurgeInterval() {
		return radAcctConfiguration.getDupicateRequestQueuePurgeInterval();
	}
	
	@Override
	protected void incrementServTotalInvalidRequests(ServiceRequest request){
		acctServiceMIBListener.listenRadiusAccServTotalInvalidRequestsEvent();
	}
	@Override
	protected void incrementServTotalBadAuthenticators(String clientAddress){
		RadClientData clientData = radClientConfiguration.getClientData(clientAddress);
		if(clientData != null) {
			acctServiceMIBListener.listenRadiusAccServTotalBadAuthenticatorsEvent(clientData.getClientIp());
		}
	}
	@Override
	protected void incrementServTotalMalformedRequest(RadServiceRequest request) {
		RadClientData clientData = radClientConfiguration.getClientData(request.getClientIp());
		if(clientData != null) {
			acctServiceMIBListener.listenRadiusAccServTotalMalformedRequestsEvent(clientData.getClientIp());
		}
	}
	
	protected void convertClassAttributeToVSA(RadAcctRequest request) {
		List<IRadiusAttribute> radiusAttributesFromClassAttr = getVSAFromClass(request);
		
		if (Collectionz.isNullOrEmpty(radiusAttributesFromClassAttr) == false) {
			addAllAttributes(request, radiusAttributesFromClassAttr);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "No attribute to add from Class attributes in Request Packet");
			}
		}
	}

	private void addAllAttributes(RadAcctRequest request,
			List<IRadiusAttribute> radiusAttributesFromClassAttr) {
		int noOfAttributes = radiusAttributesFromClassAttr.size();
		
		for (int i = 0; i < noOfAttributes; i++) {
			request.addInfoAttribute(radiusAttributesFromClassAttr.get(i));
		}
	}
	
	private @Nullable List<IRadiusAttribute> getVSAFromClass(RadAcctRequest request) {
		VSAInClassConfiguration vsainInClassConfiguration = 
			((AAAServerContext)getServerContext()).getServerConfiguration().getVSAInClassConfiguration();

		ArrayList<IRadiusAttribute> vsaList = null;

		if (vsainInClassConfiguration == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "VSA in class configuration not found, skipping VSA in class processing.");
			}
			return null;
		}

		if (vsainInClassConfiguration.getIsEnabled() == false) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping VSA in class processing as this feature is disabled.");
			}
			return null;
		}

		String[] classAttributeId = vsainInClassConfiguration.getClassAttributeId();
		char strSeparator = vsainInClassConfiguration.getSeparator();
		String strPrefix = vsainInClassConfiguration.getStrPrefix();

		for (int cntr = 0; cntr < classAttributeId.length; cntr++) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Searching for " 
						+ classAttributeId[cntr] + " attribute in request packet for VSA in class processing.");
			}

			ArrayList<IRadiusAttribute> classAttributeList = (ArrayList<IRadiusAttribute>) request.getRadiusAttributes(classAttributeId[cntr]);

			if (Collectionz.isNullOrEmpty(classAttributeList)) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, classAttributeId[cntr] 
				        + " attribute not found in request packet for VSA in class processing.");
				}
				continue;
			}
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Found " + classAttributeList.size() + " occurance(s) of " 
						+ classAttributeId[cntr] + " attribute in request packet for VSA in class processing.");
			}

			final int numberOfAttribute = classAttributeList.size();
			IRadiusAttribute classAttr = null;
			String strStringValue = null;
			StringBuilder newValue;

			for (int i = 0; i < numberOfAttribute; i++) {
				classAttr = classAttributeList.get(i);
				newValue = new StringBuilder();
				strStringValue = classAttr.getStringValue();

				if (strStringValue.startsWith(strPrefix) == false) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Value of attribute " 
								+ classAttributeId[cntr] + "=" + strStringValue + " does not start with prefix "
								+ strPrefix);
					}
					continue;
				}

				String [] classTokens = splitString(strStringValue, strSeparator);

				int totalTokens = classTokens.length;
				String strAttribute;
				int indexOfValueSeparator;
				String strAttributeId;
				String strAttributeValue;

				for (int j = 1; j < totalTokens; j++) {
					strAttribute = classTokens[j];
					indexOfValueSeparator = strAttribute.indexOf('=');

					if (indexOfValueSeparator == -1) {
						if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
							LogManager.getLogger().warn(MODULE, "Invalid syntax format in class attribute for attribute : " + strAttribute);
						}
						continue;
					}

					strAttributeId = strAttribute.substring(0, indexOfValueSeparator).trim();
					IRadiusAttribute radAttribute = Dictionary.getInstance().getKnownAttribute(strAttributeId);

					if (radAttribute != null) {
						strAttributeValue = strAttribute.substring(indexOfValueSeparator + 1);
						radAttribute.setStringValue(strAttributeValue);

						if (vsaList == null) {
							vsaList = new ArrayList<IRadiusAttribute>();
						}
						vsaList.add(radAttribute);

						if (radAttribute.getVendorID() == RadiusConstants.STANDARD_VENDOR_ID) {
							if (LogManager.getLogger().isDebugLogLevel()) {
								LogManager.getLogger().debug(MODULE, "Adding VSA : " + Dictionary.getInstance().getAttributeName(strAttributeId)
										+ radAttribute + " from class Attribute :" + Dictionary.getInstance().getAttributeName(classAttributeId[cntr]));
							}
						} else {
							if (LogManager.getLogger().isDebugLogLevel()) {
								LogManager.getLogger().debug(MODULE, "Adding VSA : " + Dictionary.getInstance().getVendorName(radAttribute.getVendorID()) 
										+ ":" + Dictionary.getInstance().getAttributeName(strAttributeId) + radAttribute
										+ " from class Attribute :" + Dictionary.getInstance().getAttributeName(classAttributeId[cntr]));
							}
						}
					} else {
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Attribute for Attribute-Id : " + 
									strAttributeId + " not added to VSA Attribute List Reason : Attribute not found in Dictionary");
						}

						if (newValue.length() == 0) {
							newValue.append(strAttribute);
						} else {
							newValue.append(strSeparator).append(strAttribute);
						}
					}
				}

				if (newValue.length() > 0) {
					classAttr.setStringValue(newValue.toString());
				} else {
					request.removeAttribute(classAttr, false);
				}
			}
			break;
		}
		return vsaList;
	}
	
	private String[] splitString(String strAttribute, char splitChar) {
		ArrayList<String> splittedExpression = new ArrayList<String>();
		StringBuilder currentExpression = new StringBuilder();
		int pos = 0;
		
		char[] charString = strAttribute.toCharArray();
		final int len  = charString.length;
		while (pos < len) {
			char currentChar = charString[pos];
			if (currentChar == '\\') {
				currentExpression.append(charString[pos + 1]);
				pos = pos + 2;
				continue;
				
			} else if (currentChar == splitChar) {
				if (currentExpression.toString().length() > 0) {
					splittedExpression.add(currentExpression.toString());
				}
				currentExpression = new StringBuilder();
				pos++;
				continue;
			} else {
				currentExpression.append(currentChar);
				pos++;
			}
		}
		
		if (currentExpression.length() != 0) {
			splittedExpression.add(currentExpression.toString());
		}
		return splittedExpression.toArray(new String[splittedExpression.size()]);
	}


	@Override
	protected boolean isValidRequest(RadAcctRequest radServiceRequest, RadAcctResponse radServiceResponse) {

		if(radServiceRequest.getPacketType() != RadiusConstants.ACCOUNTING_REQUEST_MESSAGE && radServiceRequest.getPacketType() != RadiusConstants.STATUS_SERVER_MESSAGE ) {
			incrementAcctServTotalUnknownTypes(radServiceRequest.getClientIp());			
			radServiceResponse.markForDropRequest();
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Unknown request packet (packet type: "+ radServiceRequest.getPacketType() + ") received from " + radServiceRequest.getClientIp() + ":" + radServiceRequest.getClientPort());
			return false;
		}

		if (radServiceRequest.getPacketType() == RadiusConstants.STATUS_SERVER_MESSAGE) {
			return isValidStatusServerMessage(radServiceRequest, radServiceResponse);
		} else {
			return isValidAccountingRequest(radServiceRequest, radServiceResponse);
		}
	}


	private boolean isValidAccountingRequest(RadAcctRequest radServiceRequest,
			RadAcctResponse radServiceResponse) {
		return  isRequestAuthenticatorValid(radServiceRequest, radServiceResponse) && 
				isMessageAuthenticatorValid(radServiceRequest, radServiceResponse);
	}


	private boolean isRequestAuthenticatorValid(RadAcctRequest radServiceRequest, RadAcctResponse radServiceResponse) {
		byte[] requestAuthenticator = null;
		RadiusPacket tmpRadiusPacket = new RadiusPacket();
		tmpRadiusPacket.setBytes(radServiceRequest.getRequestBytes());
		tmpRadiusPacket.setAuthenticator(new byte[16]);

		requestAuthenticator = RadiusUtility.generateRFC2866RequestAuthenticator(tmpRadiusPacket, radServiceResponse.getClientData().getSharedSecret(radServiceRequest.getPacketType()));
		if(!RadiusUtility.isByteArraySame(requestAuthenticator,radServiceRequest.getAuthenticator())) {
			radServiceResponse.markForDropRequest();
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Request Dropped:Invalid Request Authenticator.");
			return false;
		}
		return true;
	}


	private boolean isValidStatusServerMessage(RadAcctRequest radServiceRequest,
			RadAcctResponse radServiceResponse) {
		
		IRadiusAttribute msgAuthenticator = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if(msgAuthenticator != null){
			if(msgAuthenticator.getValueBytes().length == 16 && validateMessageAuthenticatorForStatusServer(radServiceRequest, msgAuthenticator.getValueBytes(),radServiceResponse.getClientData().getSharedSecret(radServiceRequest.getPacketType()))) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Valid message authenticator");	
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Packet Dropped. Invalid Message Authenticator.");

				radServiceResponse.setFurtherProcessingRequired(false);
				radServiceResponse.markForDropRequest();
				incrementServTotalBadAuthenticators(radServiceRequest.getClientIp());
				return false;
			}
		}
		return true;
	}


	private boolean isMessageAuthenticatorValid(RadAcctRequest radServiceRequest, RadAcctResponse radServiceResponse) {
		IRadiusAttribute msgAuthenticator = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if(msgAuthenticator != null){
			if(msgAuthenticator.getValueBytes().length == 16 && validateMessageAuthenticator(radServiceRequest, msgAuthenticator.getValueBytes(),radServiceResponse.getClientData().getSharedSecret(radServiceRequest.getPacketType()))) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Valid message authenticator");	
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Packet Dropped. Invalid Message Authenticator.");

				radServiceResponse.setFurtherProcessingRequired(false);
				radServiceResponse.markForDropRequest();
				incrementServTotalBadAuthenticators(radServiceRequest.getClientIp());
				return false;
			}
		}
		return true;
	}

	public class RadAcctConfigurationSetter implements ConfigurationSetter{
		private ServiceContext serviceContext;
		private static final String REALTIME = "realtime";
		
		public RadAcctConfigurationSetter(ServiceContext serviceContext){
			this.serviceContext = serviceContext;
		}

		@Override
		public String execute(String... parameters) {
			if(parameters[2].equalsIgnoreCase("log")){
				if(parameters.length >= 4){
					if(((RadAcctServiceContext)serviceContext).getAcctConfiguration().isServiceLevelLoggerEnabled()){
						EliteRollingFileLogger logger = (EliteRollingFileLogger)serviceLogger;
						if (logger.isValidLogLevel(parameters[3]) == false) {
							return "Invalid log level: " + parameters[3];
						}
						logger.setLogLevel(parameters[3]);

						return "Configuration Changed Successfully";

					}else{
						return "Error : Accounting log are disabled";
					}
				}			
				
			}
			return getHelpMsg();
		}

		@Override
		public boolean isEligible(String... parameters) {
			if(parameters.length == 0){
				return false;
			}
			if(!parameters[0].equalsIgnoreCase("service")){
				return false;
			}
			if(!parameters[1].equalsIgnoreCase("acct")){
				return false;
			}
			if(parameters[2].equalsIgnoreCase("log")){
				return true;
			}else if(parameters[2].equalsIgnoreCase("-help")){
				return true;
			}
			return false;
		}

		@Override
		public String getHelpMsg() {
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage : set service acct [<options>]");
			out.println();
			out.println("where options include:");		
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the Accounting Service. ");			
			out.close();
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return "'acct':{'log':{'off':{},'error':{},'warn':{},'info':{},'debug':{},'trace':{},'all':{}}}";
		}

		@Override
		public int getConfigurationSetterType() {
			return SERVICE_TYPE;
		}

	}

	private AcctServicePolicy getServicePolicy(String policyName){
		
		if (acctServicePolicies != null && !acctServicePolicies.isEmpty()) {
			for(AcctServicePolicy servicePolicy:acctServicePolicies) {
				if(servicePolicy.getPolicyName().equals(policyName)){
					return servicePolicy;
				}
			}
		}
		return null;	

	}
	private String getServicePolicyNames(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();		
		out.println();
		out.println(" -- Rad Acct Service Policies -- ");
		out.println();
		if (acctServicePolicies != null && !acctServicePolicies.isEmpty()) {
			for(AcctServicePolicy servicePolicy:acctServicePolicies) {
				out.println(servicePolicy.getPolicyName());
			}
		} else {
			out.println("No policy available in the cache");
		}
		out.close();
		return stringBuffer.toString();	
	}
	
	@Override
	public boolean validatePacketAsPerRFC(RadAcctRequest request){
			/*
				As per RFC 2866
				Following attributes MUST NOT be present in an Accounting-Request:
				User-Password, CHAP-Password, Reply-Message, State.
		    */		
		boolean isValidRequest=true;
		if(request.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD)!= null){
			LogManager.getLogger().debug(MODULE, "Dropping Request for request-id :"+request.getIdentifier()+" Reason : User Password attribute present in request packet");
			isValidRequest = false;
		}else if(request.getRadiusAttribute(RadiusAttributeConstants.CHAP_PASSWORD)!= null){
			LogManager.getLogger().debug(MODULE, "Dropping Request for request-id :"+request.getIdentifier()+" Reason : Chap Password attribute present in request packet");
			isValidRequest = false;
		}else if(request.getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE)!= null){
			LogManager.getLogger().debug(MODULE, "Dropping Request for request-id :"+request.getIdentifier()+" Reason : Reply Messege attribute present in request packet");
			isValidRequest = false;
		}else if(request.getRadiusAttribute(RadiusAttributeConstants.STATE)!= null){
			LogManager.getLogger().debug(MODULE, "Dropping Request for request-id :"+request.getIdentifier()+" Reason : State attribute present in request packet");
			isValidRequest = false;
		}else if(request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS) == null && request.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER) == null){
				/*
					As per RFC 2866
					Either NAS-IP-Address or NAS-Identifier MUST be present in a
					RADIUS Accounting-Request.
				*/	
			isValidRequest = false;
			LogManager.getLogger().debug(MODULE, "Dropping Request for request-id :"+request.getIdentifier()+" Reason : NAS-IP-Address or NAS-Identifier or both not present request packet");
		}else if(request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE)==null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Radius Acct-Status-Type Attribute Not Found");
			isValidRequest = false;
		}else if(request.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID)==null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Radius Acct-Session-Id Attribute Not Found");
			isValidRequest = false;

		}
		return isValidRequest;
	}

	@Override
	protected void incrementRequestDroppedCounter(String clientAddress ,ServiceRequest request){
		RadClientData clientData = radClientConfiguration.getClientData(clientAddress);
		if(clientData != null) {
			acctServiceMIBListener.listenRadiusAccServTotalPacketsDroppedEvent(clientData.getClientIp());
		}
	}

	@Override
	protected void incrementDuplicateRequestReceivedCounter(
			ServiceRequest request) {
		RadAcctRequest radAcctRequest = (RadAcctRequest)request;
		RadClientData clientData = radClientConfiguration.getClientData(radAcctRequest.getClientIp());
		if(clientData != null) {
			acctServiceMIBListener.listenRadiusAccServTotalDupRequestsEvent(clientData.getClientIp());
		}
	}
	
	@Override
	public void preRequestProcess(RadAcctRequest request, RadAcctResponse response) {
		super.preRequestProcess(request, response);
		convertClassAttributeToVSA(request);
	}
	
	@Override
	public void reInit() {
		this.radAcctConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getAcctConfiguration();
		this.radClientConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration();
		reInitLogLevel();
//		reInitServicePolicy();
		super.reInit();
	}
	
	private void reInitServicePolicy() {
		//TODO re initialization is temporarily out of service  
	}


	private void reInitLogLevel() {
		if(radAcctConfiguration.isServiceLevelLoggerEnabled()){
			serviceLogger.setLogLevel(radAcctConfiguration.logLevel());
		}
	}
	
	@Override
	protected final void shutdownLogger() {
		Closeables.closeQuietly(serviceLogger);
	}
	
	@Override
	public int getDefaultServicePort() {
		return AAAServerConstants.DEFAULT_RAD_ACCT_PORT;
	}

	@Override
	protected byte[] buildDuplicateResponse(RadAcctRequest request,
			RadAcctResponse response, byte[] cachedResponseBytes) {
		
		RadiusPacket responsePacket = new RadiusPacket();
		responsePacket.setIdentifier(request.getIdentifier());
		responsePacket.setBytes(cachedResponseBytes);

		RadClientConfiguration radClientConfiguration =  ((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration();
		if(radClientConfiguration == null){
			LogManager.getLogger().warn(MODULE, "Returning cached response as duplicate response will not be generated, Reason: RADIUS Client Configuration not found");
			return cachedResponseBytes;
}
		
		RadClientData clientData = radClientConfiguration.getClientData(request.getClientIp());
		if(clientData == null){	
			LogManager.getLogger().warn(MODULE, "Returning cached response as duplicate response will not be generated, Reason: Client Configuration not found for ip: " + request.getClientIp());
			return cachedResponseBytes;
		}	
		
		// is a Valid client - setting the client data in response
		response.setClientData(clientData);
		
		Collection<IRadiusAttribute> radiusAttributes = responsePacket.getRadiusAttributes();
		for (IRadiusAttribute attribute : radiusAttributes) {
			response.addAttribute(attribute);
		}
		return response.getResponseBytes();
	}
}
