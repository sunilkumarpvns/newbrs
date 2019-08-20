package com.elitecore.nvsmx.ws.resetusage;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.nvsmx.ws.resetusage.blmanager.ResetUsageWSBLManager;
import com.elitecore.nvsmx.ws.resetusage.request.ResetBillingCycleWSRequest;
import com.elitecore.nvsmx.ws.resetusage.request.ResetUsageRestRequest;
import com.elitecore.nvsmx.ws.resetusage.response.ResetBillingCycleResponse;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Path("/")
@Api(value = "/restful/resetUsage" ,description = "Web-service to change Billing-Cycle  for post paid subscriber")
public class ResetUsageWS {

	public static final String WS_RESET_BILLING_CYCLE = "wsResetBillingCycle";
	private static final String MODULE = "REST-RESET-USAGE-WS";
	private static final String WEB_SERVICE_NAME = ResetUsageWS.class.getSimpleName();
	private ResetUsageWSBLManager resetUsageWSBLManager;

	public ResetUsageWS() {
		this.resetUsageWSBLManager = new ResetUsageWSBLManager();
	}

	@PUT
	@Path("/resetBillingCycle")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(
			value = "To Reset Billing Cycle Usage",
			notes = "Get operation with Response and @Default value",
			response = ResetBillingCycleResponse.class
	)
    @ApiResponses(value = {
            @ApiResponse(code=401, message = "Subscriber Identity and Alternate Identity is missing")
    })
	public ResetBillingCycleResponse resetUsage(@ApiParam(value = "billing-cycle-reset-request", required = true) ResetUsageRestRequest request,
												@ApiParam(value = "parameter1", required = false) @QueryParam("parameter1") String parameter1,
												@ApiParam(value = "parameter2", required = false) @QueryParam("parameter2") String parameter2,
												@ApiParam(value = "parameter3", required = false) @QueryParam("parameter3") String parameter3) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsResetBillingCycle");
		}

		if (Objects.isNull(request)) {
			getLogger().error(MODULE, "Unable to reset usage. Reason: Reset usage request is NULL");
			return new ResetBillingCycleResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
					"Reset usage details not received", null,
					null, WEB_SERVICE_NAME, WS_RESET_BILLING_CYCLE);
		}

		String subscriberID = StringUtil.trimParameter(request.getSubscriberId());
		String alternateID = StringUtil.trimParameter(request.getAlternateId());
		String productOfferName = StringUtil.trimParameter(request.getProductOfferName());
		String resetReason = StringUtil.trimParameter(request.getResetReason());
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		parameter3 = StringUtil.trimParameter(parameter3);

		if (getLogger().isDebugLogLevel()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Subscriber ID: ");
			stringBuilder.append(subscriberID);
			stringBuilder.append(", Alternate ID: ");
			stringBuilder.append(alternateID);
			stringBuilder.append(", Product Offer Name: ");
			stringBuilder.append(productOfferName);
			stringBuilder.append(", Reset Billing Cycle Date: ");
			stringBuilder.append(request.getResetBillingCycleDate());
			stringBuilder.append(", Reset Reason: ");
			stringBuilder.append(resetReason);
			stringBuilder.append(", Parameter1: ");
			stringBuilder.append(parameter1);
			stringBuilder.append(", Parameter2:");
			stringBuilder.append(parameter2);
			stringBuilder.append(", Parameter3: ");
			stringBuilder.append(parameter3);
			getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
		}
		return resetUsageWSBLManager.resetBillingCycle(new ResetBillingCycleWSRequest(subscriberID, alternateID,
				productOfferName, request.getResetBillingCycleDate(),
				resetReason, parameter1, parameter2, parameter3, WEB_SERVICE_NAME,
				WS_RESET_BILLING_CYCLE));
	}
}
