package com.elitecore.netvertex.service.offlinernc.ratecard;

import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public class RateCardVersion {

	private static final String MODULE = "RC-VERSION";
	private final String name; 
	private final List<RatingBehavior> ratingBehaviors;
	private final String ratingKey;
	
	public RateCardVersion(String name, String ratingKey, List<RatingBehavior> ratingBehaviors) {
		this.name = name;
		this.ratingKey = ratingKey;
		this.ratingBehaviors = ratingBehaviors;
	}

	public boolean apply(RnCRequest request, RnCResponse response, String keyOne,
			String keyTwo) throws OfflineRnCException {
		
			request.getTraceWriter().println();
			request.getTraceWriter().println();
			request.getTraceWriter().print("[ " + MODULE + " ] : " + name);
			request.getTraceWriter().incrementIndentation();
			request.getTraceWriter().println();
		
			RatingBehavior ratingBehavior = selectRatingBehavior(request, response, keyOne, keyTwo);
			if (ratingBehavior != null) {
				String[] keys = ratingBehavior.getKey().split(RateCardFactory.KEY_SEPERATOR);
				
				if (LogManager.getLogger().isInfoLogLevel()) {
					request.getTraceWriter().println();
					request.getTraceWriter().print(" - entry with key values " + keys[0]
							+ " and " + keys[1] + " selected. Trying to apply rate.");
				}
				
				RatingRequest ratingRequest = new RatingRequest(request);
				request.getTraceWriter().decrementIndentation();
				return ratingBehavior.apply(ratingRequest, response);
			} else {

				request.getTraceWriter().println();
				request.getTraceWriter().print(" - No entry selected, Reason: ");
				request.getTraceWriter().println();
				request.getTraceWriter().print("   " + keyOne + " = " + response.getAttribute(keyOne) + ",");
				request.getTraceWriter().println();
				request.getTraceWriter().print("   " + keyTwo + " = " + response.getAttribute(keyTwo) + ",");
				request.getTraceWriter().println();
				request.getTraceWriter().print("   " + ratingKey + " = " + response.getAttribute(ratingKey) + ", doesn't satisfy any rate card version entry.");
				request.getTraceWriter().println();
				request.getTraceWriter().decrementIndentation();

				return false;
			}
	}
	
	private RatingBehavior selectRatingBehavior(RnCRequest request, RnCResponse response, String keyOne,
			String keyTwo) throws OfflineRnCException {
		RatingBehavior selectedRatingBehavior = null;
		for (RatingBehavior ratingBehavior : ratingBehaviors) {
			if (ratingBehavior.isApplicable(request, response, keyOne, keyTwo)) {
				selectedRatingBehavior = ratingBehavior;
				break;
			}
		}
		return selectedRatingBehavior;
	}

	public String getName() {
		return name;
	}
}
