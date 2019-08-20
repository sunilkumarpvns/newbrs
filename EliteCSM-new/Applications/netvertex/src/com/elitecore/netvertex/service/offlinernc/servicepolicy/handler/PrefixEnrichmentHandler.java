package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import java.util.List;

import com.elitecore.commons.collections.Trie;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.service.offlinernc.OfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.account.Account;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCEvent;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.partner.Partner;
import com.elitecore.netvertex.service.offlinernc.prefix.conf.PrefixConfiguration;
import static com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions.checkKeyNotNull;

public class PrefixEnrichmentHandler implements OfflineRnCHandler {
	
	private static final String MODULE = "PREFIX-ENRCHMENT-HNDLR";
	
	private Trie<PrefixConfiguration> prefixTree;
	private List<PrefixConfiguration> prefixConfigurations;
	private OfflineRnCServiceContext serviceContext;
	
	public PrefixEnrichmentHandler(List<PrefixConfiguration> prefixConfigurations, OfflineRnCServiceContext serviceContext) {
		this.prefixConfigurations = prefixConfigurations;
		this.prefixTree = new Trie<>();
		this.serviceContext = serviceContext;
	}

	@Override
	public void init() throws InitializationFailedException {
		for (PrefixConfiguration prefixConfiguration : prefixConfigurations) {
			prefixTree.put(prefixConfiguration.getPrefixCode(), prefixConfiguration);
		}
	}

	@Override
	public boolean isEligible(RnCRequest request) {
		return (OfflineRnCEvent.CDR == request.getEventType());
	}

	@Override
	public void handleRequest(RnCRequest request, RnCResponse response, PacketOutputStream out) throws OfflineRnCException {
		String callingPartyNumber = checkKeyNotNull(request, OfflineRnCKeyConstants.CALLING_STATION_ID);
		String calledPartyNumber = checkKeyNotNull(request, OfflineRnCKeyConstants.CALLED_STATION_ID);
		String partnerName = checkKeyNotNull(response, OfflineRnCKeyConstants.PARTNER_NAME);
		String accountId = checkKeyNotNull(response, OfflineRnCKeyConstants.ACCOUNT_ID);
		
		Partner partner = serviceContext.getPartner(partnerName);
		Account account = partner.selectAccount(accountId);
		
		request.getTraceWriter().println();
		request.getTraceWriter().println();
		request.getTraceWriter().print("[ " + MODULE + " ]");
		request.getTraceWriter().incrementIndentation();
		request.getTraceWriter().println();
		
		PrefixConfiguration callingPrefixConfiguration = account.selectPrefix(callingPartyNumber);
		if (callingPrefixConfiguration == null) {
			callingPrefixConfiguration = prefixTree.longestPrefixKeyMatch(callingPartyNumber);
		}
		
		if (callingPrefixConfiguration != null) {
			response.setAttribute(OfflineRnCKeyConstants.CALLING_PREFIX , callingPrefixConfiguration.getPrefixCode());
			response.setAttribute(OfflineRnCKeyConstants.CALLING_COUNTRY_CODE , String.valueOf(callingPrefixConfiguration.getCountryCode()));
			response.setAttribute(OfflineRnCKeyConstants.CALLING_AREA_CODE , String.valueOf(callingPrefixConfiguration.getAreaCode()));
			response.setAttribute(OfflineRnCKeyConstants.CALLING_NAME, callingPrefixConfiguration.getPrefixName());
			
			request.getTraceWriter().println();
			request.getTraceWriter().print("Calling prefix enrichment keys :- ");
			request.getTraceWriter().println();
			request.getTraceWriter().println();
			request.getTraceWriter().print("- " + OfflineRnCKeyConstants.CALLING_STATION_ID.getName() + " : " + callingPartyNumber);
			request.getTraceWriter().println();
			request.getTraceWriter().print("- Prefix Name : "+ callingPrefixConfiguration.getPrefixName());
			request.getTraceWriter().println();
			request.getTraceWriter().print("- Prefix : "+ callingPrefixConfiguration.getPrefixCode());
			request.getTraceWriter().println();
			request.getTraceWriter().print("- Country Code: "+ callingPrefixConfiguration.getCountryCode());
			request.getTraceWriter().println();
			request.getTraceWriter().print("- Area Code: "+ callingPrefixConfiguration.getAreaCode());
			request.getTraceWriter().println();
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print("- Calling prefix configuration not found");
				request.getTraceWriter().println();
				request.getTraceWriter().print("- " + OfflineRnCKeyConstants.CALLING_STATION_ID.getName() + " : " + callingPartyNumber);
			}
		}
		
		PrefixConfiguration calledPrefixConfiguration = account.selectPrefix(calledPartyNumber);
		if (calledPrefixConfiguration == null) {
			calledPrefixConfiguration = prefixTree.longestPrefixKeyMatch(calledPartyNumber);
		}
		
		if (calledPrefixConfiguration != null) {
			response.setAttribute(OfflineRnCKeyConstants.CALLED_PREFIX, calledPrefixConfiguration.getPrefixCode());
			response.setAttribute(OfflineRnCKeyConstants.CALLED_COUNTRY_CODE , String.valueOf(calledPrefixConfiguration.getCountryCode()));
			response.setAttribute(OfflineRnCKeyConstants.CALLED_AREA_CODE , String.valueOf(calledPrefixConfiguration.getAreaCode()));
			response.setAttribute(OfflineRnCKeyConstants.CALLED_NAME, calledPrefixConfiguration.getPrefixName());
			
			request.getTraceWriter().println();
			request.getTraceWriter().print("Called prefix enrichment keys :- ");
			request.getTraceWriter().println();
			request.getTraceWriter().println();
			request.getTraceWriter().print("- " + OfflineRnCKeyConstants.CALLED_STATION_ID.getName() + " : "+ calledPartyNumber);
			request.getTraceWriter().println();
			request.getTraceWriter().print("- Prefix Name : "+ calledPrefixConfiguration.getPrefixName());
			request.getTraceWriter().println();
			request.getTraceWriter().print("- Prefix : "+ calledPrefixConfiguration.getPrefixCode());
			request.getTraceWriter().println();
			request.getTraceWriter().print("- Country Code: "+ calledPrefixConfiguration.getCountryCode());
			request.getTraceWriter().println();
			request.getTraceWriter().print("- Area Code: "+ calledPrefixConfiguration.getAreaCode());
			request.getTraceWriter().println();
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print("- Called prefix configuration not found");
				request.getTraceWriter().println();
				request.getTraceWriter().print("- " + OfflineRnCKeyConstants.CALLED_STATION_ID.getName() + " : " + calledPartyNumber);
			}
		}
		
		request.getTraceWriter().decrementIndentation();
	}
}
