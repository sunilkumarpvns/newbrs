/**
 * @author vicky.singh
 */
package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import static com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions.checkKeyNotNull;

import java.text.ParseException;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.service.offlinernc.OfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.account.Account;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCEvent;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.partner.Partner;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackage;
import static com.elitecore.commons.logging.LogManager.getLogger;

public class RatingHandler implements OfflineRnCHandler {
	
	private OfflineRnCServiceContext serviceContext;
	private static final String MODULE = "RATING-HANDLER";
	
	public RatingHandler(OfflineRnCServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}
	
	@Override
	public void handleRequest(RnCRequest request, RnCResponse response, PacketOutputStream out) throws OfflineRnCException, ParseException {
		String partnerName = checkKeyNotNull(response, OfflineRnCKeyConstants.PARTNER_NAME);
		String accountId = checkKeyNotNull(response, OfflineRnCKeyConstants.ACCOUNT_ID);

		Partner partner = serviceContext.getPartner(partnerName);
		assert partner != null;
		
		request.getTraceWriter().println();
		request.getTraceWriter().println();
		request.getTraceWriter().print("[ " + MODULE + " ]");
		request.getTraceWriter().incrementIndentation();
		request.getTraceWriter().println();
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Rating process started");
			request.getTraceWriter().println();
			request.getTraceWriter().print(" - Partner Details : " + partner.toString());
		}
		
		Account account = partner.selectAccount(accountId);
		
		if (account == null) {
			request.getTraceWriter().println();
			request.getTraceWriter().print("Account with account id: " + accountId + " not found in partner: " + partnerName);
			throw new OfflineRnCException(OfflineRnCErrorCodes.ACCOUNT_NOT_FOUND, OfflineRnCErrorMessages.ACCOUNT_NOT_FOUND);
		}
		
		if (getLogger().isInfoLogLevel()) {
			request.getTraceWriter().println();
			request.getTraceWriter().print(" - Account Details : " + account.toString());
		}
		
		if (getLogger().isInfoLogLevel()) {
			request.getTraceWriter().println();
			request.getTraceWriter().print(" - Product Specification : " + account.getProductSpec().getName());
		}
		
		if (account.getProductSpec().isEligible(request) == false) {
			throw new OfflineRnCException(OfflineRnCErrorCodes.PRODUCT_SPEC_NOT_FOUND, OfflineRnCErrorMessages.PRODUCT_SPEC_NOT_FOUND);
		}
		RnCPackage rncPackage = account.getProductSpec().selectPackage(request);
		
		request.getTraceWriter().decrementIndentation();
		
		response.setAttribute(OfflineRnCKeyConstants.PRODUCT_SPEC, account.getProductSpec().getName());
		
		rncPackage.apply(request, response);
	}

	@Override
	public void init() throws InitializationFailedException {
		// no-op
	}

	@Override
	public boolean isEligible(RnCRequest request) {
		return (OfflineRnCEvent.CDR == request.getEventType());
	}
}
