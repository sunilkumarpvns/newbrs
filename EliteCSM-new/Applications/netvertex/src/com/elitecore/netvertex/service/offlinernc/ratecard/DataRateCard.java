package com.elitecore.netvertex.service.offlinernc.ratecard;

import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public class DataRateCard implements RateCard {
	private static final String MODULE = "RATE-CARD";
	
	private String name;
	private String keyOne;
	private String keyTwo;
	private String accountingEffect;
	private List<RateCardVersion> rateCardVersions;

	public DataRateCard(String name, String accountingEffect, String keyOne,
			String keyTwo, List<RateCardVersion> rateCardVersions) {
		this.name = name;
		this.accountingEffect = accountingEffect;
		this.keyOne = keyOne;
		this.keyTwo = keyTwo;
		this.rateCardVersions = rateCardVersions;
	}
	
	@Override
	public boolean apply(RnCRequest request, RnCResponse response) throws OfflineRnCException {
		request.getTraceWriter().println();
		request.getTraceWriter().incrementIndentation();
		request.getTraceWriter().println();
		request.getTraceWriter().print("[ " + MODULE + " ] : " + name);
		request.getTraceWriter().println();
		
		boolean rateApplied = false;
		if (LogManager.getLogger().isDebugLogLevel()) {
			request.getTraceWriter().println();
			request.getTraceWriter().print(" - Trying to apply rate card: " + name);
		}
		for (RateCardVersion rateCardVersion : rateCardVersions) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter()
						.print(" - Trying to select an entry from version: " + rateCardVersion.getName());
			}
			if(rateCardVersion.apply(request, response, keyOne, keyTwo)) {
				rateApplied = true;
				break;
			}
		}

		if (rateApplied) {
			response.setAttribute(OfflineRnCKeyConstants.ACCOUNTING_EFFECT, accountingEffect);
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print("- Request succesfully rated using Rate card : " + name);
			}
			
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Failed to rate request using Rate card: " + name);
			}
		}
		request.getTraceWriter().decrementIndentation();
		return rateApplied;
	}

	@Override
	public String getName() {
		return name;
	}
}
