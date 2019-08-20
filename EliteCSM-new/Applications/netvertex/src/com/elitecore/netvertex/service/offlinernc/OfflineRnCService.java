package com.elitecore.netvertex.service.offlinernc;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.base.BaseEliteService;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyValueConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.core.*;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchange;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchangeFactory;
import com.elitecore.netvertex.service.offlinernc.partner.Partner;
import com.elitecore.netvertex.service.offlinernc.productspec.ProductSpecFactory;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroupFactory;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackageFactory;
import com.elitecore.netvertex.service.offlinernc.servicepolicy.OfflineRnCServicePolicy;
import com.elitecore.netvertex.service.offlinernc.servicepolicy.handler.*;
import com.elitecore.netvertex.service.offlinernc.util.BigDecimalFormatter;
import com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class OfflineRnCService extends BaseEliteService {

	private static final String MODULE = "OFFLINE-RNC-SERVICE";
	private OfflineRnCServiceContext serviceContext;
	private PartnerFactory partnerFactory;
	private CurrencyExchangeFactory currencyExchangeFactory;
	private CurrencyExchange currencyExchange;
	private OfflineRnCHandler partnerGuideHandler;
	private OfflineRnCHandler prefixEnrichmentHandler;
	private RatingHandler ratingHandler;
	private BigDecimalFormatter bigDecimalFormatter;
	private DailyAggregationHandler dailyAggregationHandler;

	private List<OfflineRnCServicePolicy> servicePolicies;
	private SystemParameterConfiguration systemConfiguration;
	
	public OfflineRnCService(ServerContext serverContext, SessionFactory sessionFactory) {
		super(serverContext);
		this.servicePolicies = new ArrayList<>();
		serviceContext = new OfflineRnCServiceContext() {

			@Override
			public ServerContext getServerContext() {
				return serverContext;
			}

			@Override
			public Partner getPartner(String partnerName) {
				return partnerFactory.getPartner(partnerName);
			}

			@Override
			public CurrencyExchange getCurrencyExchange() {
				return currencyExchange;
			}

			@Override
			public BigDecimalFormatter getBigDecimalFormatter() {
				return bigDecimalFormatter;
			}
		};

		currencyExchangeFactory = new CurrencyExchangeFactory(sessionFactory);
		systemConfiguration = ((NetVertexServerContext)serviceContext.getServerContext()).getServerConfiguration().getSystemParameterConfiguration();
		bigDecimalFormatter = new BigDecimalFormatter(systemConfiguration.getNumberOfDecimalPointsInTransaction(), systemConfiguration.getRoundingCurrencyToSpecifiedDecimalPoint());
		partnerFactory = new PartnerFactory(sessionFactory, new ProductSpecFactory(new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(serviceContext, systemConfiguration), systemConfiguration)), systemConfiguration));
	}

	@Override
	public String getServiceIdentifier() {
		return "OFFLINE-RNC";
	}

	@Override
	public String getServiceName() {
		return "OfflineRnC";
	}

	@Override
	public ServiceDescription getDescription() {
		return new ServiceDescription(getServiceName(), getStatus(), "-NA-", getStartDate(), null);
	}

	@Override
	protected ServiceContext getServiceContext() {
		return serviceContext;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		// no-op
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	protected void initService() throws ServiceInitializationException {


		try {
			currencyExchange = currencyExchangeFactory.create();

			partnerFactory.createPartners();
			partnerGuideHandler = new PartnerGuideHandler(((NetVertexServerContext)serviceContext.getServerContext()).getServerConfiguration().getOfflineRnCServiceGuidingConfiguration(), systemConfiguration);
			partnerGuideHandler.init();

			prefixEnrichmentHandler = new PrefixEnrichmentHandler(((NetVertexServerContext)serviceContext.getServerContext()).getServerConfiguration().getOfflineRnCServicePrefixConfiguration(),serviceContext);
			prefixEnrichmentHandler.init();

			ratingHandler = new RatingHandler(serviceContext);
			ratingHandler.init();
			
			String dataSourceName = ((NetVertexServerContext) serviceContext.getServerContext())
				.getServerConfiguration()
				.getNetvertexServerGroupConfiguration()
				.getSessionDS().getDataSourceName();
			
			DailyUsageListener dailyUsageListener = new DailyUsageDbListener("TBLT_UNBILLED_USAGE",
					DBConnectionManager.getInstance(dataSourceName));
			dailyAggregationHandler = new DailyAggregationHandler(systemConfiguration.getRateSelectionWhenDateChange(),
					systemConfiguration.getEdrDateTimeStampFormat(),
					dailyUsageListener);
			dailyAggregationHandler.init();
			
		} catch (InitializationFailedException ex) {
			throw new ServiceInitializationException(ServiceRemarks.UNKNOWN_PROBLEM, ex);
		}

		initServicePolicies();
	}

	private void initServicePolicies() {
		OfflineRnCServicePolicy defaultPolicy = new OfflineRnCServicePolicy("");

		try {
			defaultPolicy.addHandler(ratingHandler);
			defaultPolicy.addHandler(dailyAggregationHandler);
			defaultPolicy.addHandler(new WriteHandler());
			defaultPolicy.init();

			servicePolicies.add(defaultPolicy);

		} catch (InitializationFailedException e) {
			LogManager.getLogger().warn(MODULE, "Error in creating default service policy. Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}



	public void handleRequest(RnCRequest request, RnCResponse response, final PacketOutputStream originalOut) throws Exception {

		try {
			if (OfflineRnCKeyValueConstants.RATING_STREAM_TYPE_CARRIER_INTERCONNECT.val.equals(
					request.getAttribute(OfflineRnCKeyConstants.RATING_STREAM))) {
				
				request.getTraceWriter().println();
				request.getTraceWriter().incrementIndentation();
				request.getTraceWriter().println("[ " + MODULE + " ]");
				
				request.getTraceWriter().incrementIndentation();
				request.getTraceWriter().println("EDR of transit scenario");
				request.getTraceWriter().decrementIndentation();
				
				final List<RnCRequest> subRequests = splitRequest(request);

				AggregatingPacketOutputStream aggregatingPacketOutputStream = new AggregatingPacketOutputStream();
				for (RnCRequest subRequest : subRequests) {
					handleWithPolicy(subRequest, RnCResponse.of(subRequest), aggregatingPacketOutputStream);
				}
				
				for (int i = 0; i < subRequests.size(); i++) {
					originalOut.writeSuccessful(subRequests.get(i), aggregatingPacketOutputStream.responses.get(i));
				}

			} else {
				handleWithPolicy(request, response, originalOut);
			}
		} catch (OfflineRnCException ex) {
			request.getTraceWriter().println();
			request.getTraceWriter().print("Error in handling EDR, SerialNumber: " 
					+ request.getSerialNumber() + ", Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);

			request.getTraceWriter().println();
			request.getTraceWriter().println();
			getLogger().info(MODULE, request.getTrace());

			response.setError(ex);
			originalOut.writeError(request, response);
		}
	}

	private void handleWithPolicy(RnCRequest request, RnCResponse response, final PacketOutputStream originalOut) throws Exception {
		if (partnerGuideHandler.isEligible(request)) {
			partnerGuideHandler.handleRequest(request, response, originalOut);
		}
		if (prefixEnrichmentHandler.isEligible(request)) {
			prefixEnrichmentHandler.handleRequest(request, response, originalOut);
		}

		OfflineRnCServicePolicy policy = selectServicePolicy(request);
		
		if (policy == null) {
			request.getTraceWriter().println();
			request.getTraceWriter().print("No service policy selected for packet " + request.getSerialNumber());
			throw new OfflineRnCException(OfflineRnCErrorCodes.NO_SERVICE_POLICY_SELECTED, OfflineRnCErrorMessages.NO_SERVICE_POLICY_SELECTED);
		}

		policy.handle(request, response, originalOut);

		request.getTraceWriter().println();
		request.getTraceWriter().println();
		
		getLogger().info(MODULE, request.getTrace());
	}

	private List<RnCRequest> splitRequest(RnCRequest request) throws OfflineRnCException {
		List<RnCRequest> rncRequests = new ArrayList<>(2);

		RnCRequest ingressRnCRequest = RnCRequest.copy(request);
		RnCRequest egressRnCRequest = RnCRequest.copy(request);
		
		String ingressTrunkGroupName = RnCPreConditions.checkKeyNotNull(ingressRnCRequest, OfflineRnCKeyConstants.INGRESS_TRUNK_GROUP_NAME);
		String egressTrunkGroupName = RnCPreConditions.checkKeyNotNull(egressRnCRequest, OfflineRnCKeyConstants.EGRESS_TRUNK_GROUP_NAME);

		ingressRnCRequest.setAttribute(OfflineRnCKeyConstants.ACCOUNT_IDENTIFIER.getName(), ingressTrunkGroupName);
		egressRnCRequest.setAttribute(OfflineRnCKeyConstants.ACCOUNT_IDENTIFIER.getName(), egressTrunkGroupName);

		rncRequests.add(ingressRnCRequest);
		rncRequests.add(egressRnCRequest);

		return rncRequests;
	}

	private OfflineRnCServicePolicy selectServicePolicy(RnCRequest packet) {
		for (OfflineRnCServicePolicy servicePolicy : servicePolicies) {
			if (servicePolicy.assignRequest(packet)) {
				return servicePolicy;
			}
		}
		return null;
	}

	@Override
	protected boolean startService() {
		return true;
	}

	@Override
	protected boolean stopService() {
		return true;
	}

	@Override
	protected void shutdownService() {
		// no-op
	}
	
	private class AggregatingPacketOutputStream implements PacketOutputStream {

		private List<RnCResponse> responses;
		
		public AggregatingPacketOutputStream() {
			responses = new ArrayList<>(2);
		}
		
		@Override
		public void writeSuccessful(RnCRequest request, RnCResponse response) throws Exception {
			responses.add(response);
		}

		@Override
		public void writeError(RnCRequest request, RnCResponse response) throws Exception {
			throw new OfflineRnCException(response.getErrorCode(), response.getErrorMessage());
		}
	}
}
