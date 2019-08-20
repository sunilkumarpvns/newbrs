package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import static com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions.checkKeyNotNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.commons.base.TimeRange;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCEvent;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.guiding.conf.GuidingConfiguration;

public class PartnerGuideHandler implements OfflineRnCHandler {

	private static final String MODULE = "PARTNER-GUIDE-HANDLER" ;

	private Map<String, List<PartnerGuidingData>> guidingInformationsMap;
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;
	private List<GuidingConfiguration> guidingConfigurations;
	private SystemParameterConfiguration systemParameterConfiguration;

	public PartnerGuideHandler(List<GuidingConfiguration> guidingConfigurations, SystemParameterConfiguration systemParameterConfiguration) {
		this.guidingConfigurations = guidingConfigurations;
		this.guidingInformationsMap = new HashMap<>();
		this.systemParameterConfiguration = systemParameterConfiguration;
	}

	@Override
	public void init() throws InitializationFailedException{

		simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(systemParameterConfiguration.getEdrDateTimeStampFormat());
		
		for (GuidingConfiguration guidingConfiguration : guidingConfigurations) {
			PartnerGuidingData partnerGuidingData = new PartnerGuidingData();
			String lob = guidingConfiguration.getLob().getAlias();
			partnerGuidingData.setLob(lob);
			String service = guidingConfiguration.getService().getAlias();
			partnerGuidingData.setService(service);
			String trafficType = guidingConfiguration.getTrafficType();
			partnerGuidingData.setTrafficType(trafficType);
			String accountIdentifierType = guidingConfiguration.getAccountIdentifierType();
			partnerGuidingData.setAccountIdentifierType(accountIdentifierType);
			String accountIdentifierValue = guidingConfiguration.getAccountIdentifierValue();
			partnerGuidingData.setAccountIdentifier(accountIdentifierValue);
			partnerGuidingData.setAccountId(guidingConfiguration.getAccountNumber());
			partnerGuidingData.setPartnerName(guidingConfiguration.getPartnerName());

			try {
				if(guidingConfiguration.getEndDate() != null) {
					partnerGuidingData.setTimeRange(TimeRange.closed(systemParameterConfiguration.getEdrDateTimeStampFormat(), simpleDateFormatThreadLocal.get().format(guidingConfiguration.getStartDate()),
							simpleDateFormatThreadLocal.get().format(guidingConfiguration.getEndDate())));
				} else {
					partnerGuidingData.setTimeRange(TimeRange.closed(systemParameterConfiguration.getEdrDateTimeStampFormat(), simpleDateFormatThreadLocal.get().format(guidingConfiguration.getStartDate())));
				}
			} catch (ParseException e) {
				LogManager.getLogger().warn(MODULE, "Unable to parse date in dd/MM/yyyy format");
			}
			String key = keyFrom(lob, service, trafficType, accountIdentifierValue);
			List<PartnerGuidingData> guidingInformations = getPartnerGuideDetails(key);
			guidingInformations.add(partnerGuidingData);
		}
	}

	public List<PartnerGuidingData> getPartnerGuideDetails(String key) {
		return guidingInformationsMap.computeIfAbsent(key, k -> new ArrayList<>());
	}

	@Override
	public boolean isEligible(RnCRequest request) {
		return (OfflineRnCEvent.CDR == request.getEventType());
	}

	@Override
	public void handleRequest(RnCRequest request, RnCResponse response, PacketOutputStream out) throws OfflineRnCException {
		String lob = checkKeyNotNull(request, OfflineRnCKeyConstants.LINE_OF_BUSINESS);
		String service = checkKeyNotNull(request, OfflineRnCKeyConstants.SERVICE_NAME);
		String trafficType = checkKeyNotNull(request, OfflineRnCKeyConstants.TRAFFIC_TYPE);
		String accountIdentifier = checkKeyNotNull(request, OfflineRnCKeyConstants.ACCOUNT_IDENTIFIER);
		String ratingKeyValue = checkKeyNotNull(request, systemParameterConfiguration.getRateSelectionWhenDateChange());

		request.getTraceWriter().println();
		request.getTraceWriter().println();
		request.getTraceWriter().incrementIndentation();
		request.getTraceWriter().print("[ " + MODULE + " ]");
		request.getTraceWriter().incrementIndentation();
		request.getTraceWriter().println();

		String key = keyFrom(lob, service, trafficType, accountIdentifier);

		List<PartnerGuidingData> partnerGuideDetails = guidingInformationsMap.get(key);
		if (Collectionz.isNullOrEmpty(partnerGuideDetails)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print("LOB: " + lob + ", Service Name: " + service
						+ ", Traffic Type: " + trafficType + ", Account Identifier: " + accountIdentifier 
						+ " does not match with any guiding configuration");
			}
			request.getTraceWriter().decrementIndentation();
			throw new OfflineRnCException(OfflineRnCErrorCodes.PARTNER_NOT_FOUND, OfflineRnCErrorMessages.PARTNER_NOT_FOUND);

		}

		Timestamp ratingKeyTime = parseSessionConnectTime(ratingKeyValue);

		PartnerGuidingData selectedPartnerData = null;
		for (PartnerGuidingData partnerGuideDetail : partnerGuideDetails) {
			TimeRange timeRange = partnerGuideDetail.getTimeRange();
			if (timeRange.contains(ratingKeyTime)) {
				selectedPartnerData = partnerGuideDetail;
				break;
			}
		}

		if (selectedPartnerData == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print("Guiding date does not match any guiding configuration for Partner: " + partnerGuideDetails.get(0).partnerName 
						+ ", " + systemParameterConfiguration.getRateSelectionWhenDateChange() + ": " + ratingKeyValue);
			}
			request.getTraceWriter().decrementIndentation();
			throw new OfflineRnCException(OfflineRnCErrorCodes.PARTNER_NOT_FOUND, OfflineRnCErrorMessages.PARTNER_NOT_FOUND);
		}

		response.setAttribute(OfflineRnCKeyConstants.PARTNER_NAME, selectedPartnerData.getPartnerName());
		response.setAttribute(OfflineRnCKeyConstants.ACCOUNT_ID, selectedPartnerData.getAccountId());
		response.setAttribute(OfflineRnCKeyConstants.ACCOUNT_IDENTIFIER_TYPE, selectedPartnerData.getAccountIdentifierType());

		if (LogManager.getLogger().isInfoLogLevel()) {
			request.getTraceWriter().println();
			request.getTraceWriter().print("Partner account located with Account-ID: " + response.getAttribute(OfflineRnCKeyConstants.ACCOUNT_ID));
			request.getTraceWriter().println();
			request.getTraceWriter().print("Partner: " + response.getAttribute(OfflineRnCKeyConstants.PARTNER_NAME));
		}
		request.getTraceWriter().decrementIndentation();
	}

	private Timestamp parseSessionConnectTime(String sessionStartDateValue) throws OfflineRnCException {
		try {
			return new Timestamp(simpleDateFormatThreadLocal.get().parse(sessionStartDateValue).getTime());
		} catch (ParseException ex) {
			throw new OfflineRnCException(OfflineRnCErrorCodes.INVALID_EDR, OfflineRnCErrorMessages.INVALID_EDR, 
					OfflineRnCErrorMessages.INVALID_DATE_FORMAT + "-" + systemParameterConfiguration.getRateSelectionWhenDateChange());
		}
	}

	private String keyFrom(String lob, String service, String trafficType, String accountIdentifierValue) {
		return lob + " " + service + " " + trafficType + " " + accountIdentifierValue;
	}

	class PartnerGuidingData {

		private String lob;
		private String service;
		private String trafficType;
		private String accountIdentifierType;
		private String accountIdentifier;
		private String accountId;
		private TimeRange timeRange;
		private String partnerName;

		public String getLob() {
			return lob;
		}
		public void setLob(String lob) {
			this.lob = lob;
		}
		public String getService() {
			return service;
		}
		public void setService(String service) {
			this.service = service;
		}
		public String getTrafficType() {
			return trafficType;
		}
		public void setTrafficType(String trafficType) {
			this.trafficType = trafficType;
		}
		public String getAccountIdentifierType() {
			return accountIdentifierType;
		}
		public void setAccountIdentifierType(String accountIdentifierType) {
			this.accountIdentifierType = accountIdentifierType;
		}
		public String getAccountIdentifier() {
			return accountIdentifier;
		}
		public void setAccountIdentifier(String accountIdentifier) {
			this.accountIdentifier = accountIdentifier;
		}
		public String getAccountId() {
			return accountId;
		}
		public void setAccountId(String accountId) {
			this.accountId = accountId;
		}
		public TimeRange getTimeRange() {
			return timeRange;
		}
		public void setTimeRange(TimeRange timeRange) {
			this.timeRange = timeRange;
		}
		public String getPartnerName() {
			return partnerName;
		}
		public void setPartnerName(String partnerName) {
			this.partnerName = partnerName;
		}	
	}
}
