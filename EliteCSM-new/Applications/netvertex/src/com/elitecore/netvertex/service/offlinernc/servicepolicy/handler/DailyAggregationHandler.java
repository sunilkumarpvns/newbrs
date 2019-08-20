package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import static com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions.checkKeyNotNull;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.AccountingEffect;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.gateway.file.util.OFCSFileHelper;
import com.elitecore.netvertex.service.offlinernc.core.DailyUsageListener;
import com.elitecore.netvertex.service.offlinernc.core.DailyUsageStats;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCEvent;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public class DailyAggregationHandler implements OfflineRnCHandler {

	private static final String MODULE = "DAILY-AGGREGATION-HANDLER";
	
	private String timeFormat;
	private String ratingKey;
	private ConcurrentHashMap<String, AccountWiseUsage> fileWiseDailyAggregationStatistics;
	private DailyUsageListener usageListener;

	public DailyAggregationHandler(String ratingKey, String timeFormat, DailyUsageListener usageListener) {
		this.ratingKey = ratingKey;
		this.timeFormat = timeFormat;
		this.usageListener = usageListener;
		this.fileWiseDailyAggregationStatistics = new ConcurrentHashMap<>();
	}

	@Override
	public void init() throws InitializationFailedException {
		// no-op
	}

	@Override
	public boolean isEligible(RnCRequest request) {
		return OfflineRnCEvent.EOF.equals(request.getEventType()) ||
				OfflineRnCEvent.CDR.equals(request.getEventType());
	}

	@Override
	public void handleRequest(RnCRequest request, RnCResponse response, PacketOutputStream out) throws OfflineRnCException {

		if (LogManager.getLogger().isInfoLogLevel()) {
			request.getTraceWriter().println();
			request.getTraceWriter().print(" - " + MODULE + " - " );
			request.getTraceWriter().incrementIndentation();
		}

		if (Strings.isNullOrBlank(request.getLogicalFileName()) || 
				Strings.isNullOrBlank(request.getAbsoluteFileName())) {
			request.getTraceWriter().println();
			request.getTraceWriter().print("- Failed to aggregate daily usage, Reason: " + "File name is missing from request");

			LogManager.getLogger().warn(MODULE, "Failed to aggregate daily usage, Reason: " + "File name is missing from request");
			request.getTraceWriter().decrementIndentation();
			return;
		}

		if (OfflineRnCEvent.CDR.equals(request.getEventType())) {
			handleCdrEvent(request, response);
		} else if (OfflineRnCEvent.EOF.equals(request.getEventType())) {
			handleEOFEvent(request);
		} else {
			request.getTraceWriter().decrementIndentation();
			throw new AssertionError("Unknown event type: " + request.getEventType());
		}
		request.getTraceWriter().decrementIndentation();
	}

	private void handleEOFEvent(RnCRequest request) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			request.getTraceWriter().println();
			request.getTraceWriter().incrementIndentation();
			request.getTraceWriter().print("EOF event recieved for file: " + request.getLogicalFileName());

		}

		AccountWiseUsage accountWiseDetails = fileWiseDailyAggregationStatistics.remove(request.getAbsoluteFileName());
		if (accountWiseDetails == null) {
			LogManager.getLogger().debug(MODULE, "No aggregation records found for file: " + request.getLogicalFileName());
			request.getTraceWriter().decrementIndentation();
			return;
		}
		accountWiseDetails.report();
	}

	private void handleCdrEvent(RnCRequest request, RnCResponse response) throws OfflineRnCException {
		String absoluteFileName = request.getAbsoluteFileName();
		BigDecimal reportedCost = new BigDecimal(checkKeyNotNull(response, OfflineRnCKeyConstants.ACCOUNTED_COST));
		String accountName = checkKeyNotNull(response, OfflineRnCKeyConstants.ACCOUNT_ID);
		Date usageDate;
		try {
			usageDate = OFCSFileHelper.sqlDateOf(checkKeyNotNull(request, ratingKey),
					timeFormat);
		} catch (ParseException e) {
			request.getTraceWriter().println();
			request.getTraceWriter().print("Invalid date format configured.");
			throw new OfflineRnCException(OfflineRnCErrorCodes.INVALID_EDR, OfflineRnCErrorMessages.INVALID_EDR,
					OfflineRnCErrorMessages.INVALID_DATE_FORMAT + "-" + ratingKey);
		}

		AccountWiseUsage accountWiseUsage = fileWiseDailyAggregationStatistics.computeIfAbsent(absoluteFileName, k -> new AccountWiseUsage());
		if (accountWiseUsage == null) {
			accountWiseUsage = fileWiseDailyAggregationStatistics.get(absoluteFileName);
		}
		accountWiseUsage.update(accountName, reportedCost,
				checkKeyNotNull(response, OfflineRnCKeyConstants.ACCOUNTING_EFFECT), usageDate);
	}
	
	class AccountWiseUsage {

		private Map<String, Map<Date, BigDecimal>> accountNameToUsage = new HashMap<>();

		void update(String accountName, BigDecimal reportedCost, String accountingEffect, Date usageDate) {
			Map<Date, BigDecimal> dateWiseUsage = accountNameToUsage.get(accountName);
			if (AccountingEffect.DR.name().equals(accountingEffect)) {
				if (dateWiseUsage == null) {
					dateWiseUsage = new HashMap<>();
					dateWiseUsage.put(usageDate, reportedCost);
					accountNameToUsage.put(accountName, dateWiseUsage);
				} else {
					BigDecimal previousCost = dateWiseUsage.get(usageDate);
					if (previousCost == null) {
						dateWiseUsage.put(usageDate, reportedCost);
					} else {
						dateWiseUsage.put(usageDate, previousCost.add(reportedCost));
					}
				}
			} else if (AccountingEffect.CR.name().equals(accountingEffect)) {
				if (dateWiseUsage == null) {
					dateWiseUsage = new HashMap<>();
					dateWiseUsage.put(usageDate, new BigDecimal("0").subtract(reportedCost));
					accountNameToUsage.put(accountName, dateWiseUsage);
				} else {
					BigDecimal previousCost = dateWiseUsage.get(usageDate);
					if (previousCost == null) {
						dateWiseUsage.put(usageDate, new BigDecimal("0").subtract(reportedCost));
					} else {
						dateWiseUsage.put(usageDate, previousCost.subtract(reportedCost));
					}
				}
			}
		}

		private void report() {
			for (Entry<String, Map<Date, BigDecimal>> accountNameToUsageEntry : accountNameToUsage.entrySet()) {
				for (Entry<Date, BigDecimal> dateWiseEntry : accountNameToUsageEntry.getValue().entrySet()) {
					usageListener.recordStats(new DailyUsageStats(accountNameToUsageEntry.getKey(),
							dateWiseEntry.getKey(),
							dateWiseEntry.getValue()));
				}
			}
		}
	}
}