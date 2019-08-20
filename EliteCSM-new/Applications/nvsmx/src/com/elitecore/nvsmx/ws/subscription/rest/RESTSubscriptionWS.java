package com.elitecore.nvsmx.ws.subscription.rest;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.nvsmx.ws.subscription.SubscriptionRestWS;
import com.elitecore.nvsmx.ws.subscription.SubscriptionWS;
import com.elitecore.nvsmx.ws.subscription.blmanager.PackageWSBlManager;
import com.elitecore.nvsmx.ws.subscription.blmanager.SubscriptionWSBLManager;
import com.elitecore.nvsmx.ws.subscription.request.BalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.BoDQueryRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeBillDayWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeDataAddOnSubscriptionWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeRnCAddOnProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeTopUpSubscriptionWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.FnFOperationRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListPackageRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListPackagesRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListProductOfferRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListSubscriptionWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListUsageMonitoringInformationWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.MonetaryBalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.MonetaryOperationRequest;
import com.elitecore.nvsmx.ws.subscription.request.NonMonitoryBalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.RnCBalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeAddOnWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeBodWsRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeMonetaryRechargePlanWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeTopUpWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.TopUpQueryRequest;
import com.elitecore.nvsmx.ws.subscription.request.UpdateCreditLimitWSRequest;
import com.elitecore.nvsmx.ws.subscription.response.BalanceEnquiryResponse;
import com.elitecore.nvsmx.ws.subscription.response.BoDQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.BodSubscriptionResponse;
import com.elitecore.nvsmx.ws.subscription.response.ChangeBillDayResponse;
import com.elitecore.nvsmx.ws.subscription.response.FnFOperationResponse;
import com.elitecore.nvsmx.ws.subscription.response.ListPackagesResponse;
import com.elitecore.nvsmx.ws.subscription.response.ListProductOfferResponse;
import com.elitecore.nvsmx.ws.subscription.response.MonetaryBalanceInquiryResponse;
import com.elitecore.nvsmx.ws.subscription.response.MonetaryRechargeSubscriptionResponse;
import com.elitecore.nvsmx.ws.subscription.response.MonetoryOperationResponse;
import com.elitecore.nvsmx.ws.subscription.response.NonMonitoryBalanceInquiryResponse;
import com.elitecore.nvsmx.ws.subscription.response.PackageQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.RnCBalanceEnquiryResponse;
import com.elitecore.nvsmx.ws.subscription.response.RnCPackageQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.SubscriptionResponse;
import com.elitecore.nvsmx.ws.subscription.response.TopUpQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.TopUpSubscriptionResponse;
import com.elitecore.nvsmx.ws.subscription.response.UMQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.UpdateCreditLimitResponse;
import com.elitecore.nvsmx.ws.subscription.response.UpdateMonetaryBalanceResponse;
import com.elitecore.nvsmx.ws.subscription.rest.request.AddMonetaryBalanceRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.ChangeBillDayRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.ChangeDataAddOnSubscriptionRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.ChangeRnCAddOnSubscriptionRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.ChangeTopUpSubscriptionRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.SubscribeAddOnProductOfferRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.SubscribeBodRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.SubscribeMonetaryRechargePlanRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.SubscribeTopUpRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.UpdateCreditLimitRestRequest;
import com.elitecore.nvsmx.ws.subscription.rest.request.UpdateMonetaryBalanceRestRequest;
import com.elitecore.nvsmx.ws.util.ConvertStringToDigit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.ThreadContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.elitecore.commons.logging.LogManager.getLogger;

@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
@Path("/")
@Api(value="/restful/subscription")
public class RESTSubscriptionWS implements SubscriptionRestWS {
    private static final String MODULE = "REST-SUBSCRIPTION";
    private static final String WEB_SERVICE_NAME = SubscriptionWS.class.getSimpleName();
    private static final String WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN = "wsSubscribeMonetaryRechargePlan" ;
    private SubscriptionWSBLManager subscriptionWSBLManager;
    private PackageWSBlManager packageWSBlManager;
    private static final String REGEX_PATTERN = "^((\\d{0,9}(\\.\\d{0,6})?))$";

    public RESTSubscriptionWS() {
        this.subscriptionWSBLManager = new SubscriptionWSBLManager();
        this.packageWSBlManager = new PackageWSBlManager();
        ThreadContext.put("ModuleName", MODULE);
    }

    @GET
    @Path("/listProductOffers")
    @ApiOperation(
            value = "To fetch list of Product offers",
            notes = "Get operation with Response and @Default value",
            response = ListProductOfferResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code=  400, message = "Invalid Input Parameters received from one of the following <br/>" +
                    "1.Invalid ProductOffer Type received <br/>"+
                    "2.Invalid ProductOffer Mode received <br/>" +
                    "3.Invalid ProductOffer group received<br/>" +
                    "4.Invalid ProductOffer Currency received<br/>"),
            @ApiResponse(code = 404, message = "No Product Offers available"),
    })
    public ListProductOfferResponse wsListProductOffers(@QueryParam("productOfferId")String productOfferId,
                                                        @QueryParam("productOfferName")String productOfferName,
                                                        @QueryParam("type")String type,
                                                        @QueryParam("mode") String mode,
                                                        @QueryParam("group") String group,
                                                        @QueryParam("currency") String currency,
                                                        @QueryParam("parameter1") String parameter1,
                                                        @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListProductOffers");
        }
        productOfferId=StringUtil.trimParameter(productOfferId);
        productOfferName=StringUtil.trimParameter(productOfferName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        type=StringUtil.trimParameter(type);
        mode = StringUtil.trimParameter(mode);
        group = StringUtil.trimParameter(group);
        currency = StringUtil.trimParameter(currency);


        ListProductOfferRequest listProductOfferRequest = new ListProductOfferRequest(productOfferId,productOfferName,type,mode,group,currency,parameter1, parameter2, WEB_SERVICE_NAME, WS_LIST_PRODUCT_OFFERS);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Request Parameters: " + listProductOfferRequest.toString());
        }

        return subscriptionWSBLManager.getAllProductOffers(listProductOfferRequest);
    }

    @Override
    @GET
    @Path("/listPackages")
    public ListPackagesResponse  wsListPackages(
            @QueryParam("packageId")String packageId,
            @QueryParam("packageName")String packageName,
            @QueryParam("packageType")String packageType,
            @QueryParam("type")String type,
            @QueryParam("mode") String mode,
            @QueryParam("group") String group,
            @QueryParam("currency") String currency,
            @QueryParam("parameter1") String parameter1,
            @QueryParam("parameter2") String parameter2) {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListPackages");
        }
        packageId=StringUtil.trimParameter(packageId);
        packageName=StringUtil.trimParameter(packageName);
        packageType=StringUtil.trimParameter(packageType);
        type=StringUtil.trimParameter(type);
        mode = StringUtil.trimParameter(mode);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        group = StringUtil.trimParameter(group);
        currency = StringUtil.trimParameter(currency);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(", Pacakge Id: ").append(packageId);
            stringBuilder.append(", Package Name: ").append(packageName);
            stringBuilder.append(", Package Type: ").append(packageType);
            stringBuilder.append(", Type: ").append(type);
            stringBuilder.append(", mode: ").append(mode);
            stringBuilder.append(", group: ").append(group);
            stringBuilder.append(", currency: ").append(currency);
            stringBuilder.append(", Parameter1: ").append(parameter1);
            stringBuilder.append(", Parameter2: ").append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }
        return packageWSBlManager.getListPackages(new ListPackagesRequest(packageId,packageName,packageType,type,mode,group,currency,parameter1,parameter2, WEB_SERVICE_NAME, WS_LIST_PACKAGES));
    }

    @Override
    @GET
    @Path("/listDataPackages")
    public PackageQueryResponse wsListDataPackages(@QueryParam("packageId")String packageId,
                                                   @QueryParam("packageName")String packageName,
                                                   @QueryParam("packageType")String packageType,
                                                   @QueryParam("currency") String currency,
                                                   @QueryParam("parameter1")String parameter1,
                                                   @QueryParam("parameter2")String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListPackages");
        }
        packageId=StringUtil.trimParameter(packageId);
        packageName=StringUtil.trimParameter(packageName);
        packageType=StringUtil.trimParameter(packageType);
        currency = StringUtil.trimParameter(currency);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);


        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(", Pacakge Id: ").append(packageId);
            stringBuilder.append(", Package Name: ").append(packageName);
            stringBuilder.append(", Package Type: ").append(packageType);
            stringBuilder.append(", currency: ").append(currency);
            stringBuilder.append(", Parameter1: ").append(parameter1);
            stringBuilder.append(", Parameter2: ").append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }
        return packageWSBlManager.getListDataPackages(new ListPackageRequest(packageId,packageName,packageType,currency,parameter1,parameter2, WEB_SERVICE_NAME, WS_LIST_DATA_PACKAGES));
    }

    @GET
    @Path("/listAddOnSubscriptions")
    @Override
    public SubscriptionResponse wsListAddOnSubscriptions(@QueryParam("subscriberId") String subscriberId,
                                                         @QueryParam("alternateId") String alternateId,
                                                         @QueryParam("subscriptionStatusValue") String subscriptionStatusValue,
                                                         @QueryParam("subscriptionStatusName") String subscriptionStatusName,
                                                         @QueryParam("parameter1") String parameter1,
                                                         @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListAddOnSubscriptions");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        subscriptionStatusValue = StringUtil.trimParameter(subscriptionStatusValue);
        subscriptionStatusName = StringUtil.trimParameter(subscriptionStatusName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" Subscriber Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(" Alternate Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", Subscription Status Value: ");
            stringBuilder.append(subscriptionStatusValue);
            stringBuilder.append(", Subscription Status Name: ");
            stringBuilder.append(subscriptionStatusName);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }

        return subscriptionWSBLManager.getAddOnSubscriptions(new ListSubscriptionWSRequest(subscriberId, alternateId,
                subscriptionStatusValue, subscriptionStatusName, parameter1, parameter2, WEB_SERVICE_NAME, WS_LIST_ADD_ON_SUBSCRIPTIONS));
    }


    @GET
    @Path("/listTopUpSubscriptions")
    @Override
    public TopUpSubscriptionResponse wsListTopUpSubscriptions(@QueryParam("subscriberId") String subscriberId,
                                                              @QueryParam("alternateId") String alternateId,
                                                              @QueryParam("subscriptionStatusValue") String subscriptionStatusValue,
                                                              @QueryParam("subscriptionStatusName") String subscriptionStatusName,
                                                              @QueryParam("parameter1") String parameter1,
                                                              @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListTopUpSubscriptions");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        subscriptionStatusValue = StringUtil.trimParameter(subscriptionStatusValue);
        subscriptionStatusName = StringUtil.trimParameter(subscriptionStatusName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" Subscriber Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(" Alternate Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", Subscription Status Value: ");
            stringBuilder.append(subscriptionStatusValue);
            stringBuilder.append(", Subscription Status Name: ");
            stringBuilder.append(subscriptionStatusName);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
            }
        }

        return subscriptionWSBLManager.getTopUpSubscriptions(new ListSubscriptionWSRequest(subscriberId, alternateId,
                subscriptionStatusValue, subscriptionStatusName, parameter1, parameter2, WEB_SERVICE_NAME, WS_LIST_TOP_UP_SUBSCRIPTIONS));
    }

    @POST
    @Path("/subscribeAddOnProductOffer")
        @ApiOperation(
            value = "To subscribe addon product offer",
            notes = "POST operation with compulsory request parameters: <br/>"+
                    "1. Subscriber Id or Alternate Id<br/>"+
                    "2. Product Offer name or Id<br/>" +
                    "FnF members(MSISDNs without country code) are only allowed when offer is of FnF type",
            response = ListProductOfferResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code=  400, message = "Invalid Input Parameters received from one of the following <br/>" +
                    "1. Invalid subscription state received <br/>"+
                    "2. Invalid product offer id and name combination <br/>" +
                    "3. Product offer is not success<br/>"+
                    "4. FnF members provided for non FNF addon offer<br/>"+
                    "5. Invalid start or end time received<br/>"+
                    "6. Subscriber is inactive or expired<br/>"),
            @ApiResponse(code = 404, message = "Not Fuond<br/>" +
                    "1. No Product Offers available<br/>" +
                    "2. Subscriber not found<br/>"),
            @ApiResponse(code = 401, message = "Missing mandatory input parameters <br/>" +
                    "1. Product offer id or name</br>" +
                    "2. Subscriber id or alternate id"),
            @ApiResponse(code = 500, message = "Server Error")
    })
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SubscriptionResponse subscribeAddOnProductOffer(SubscribeAddOnProductOfferRestRequest request,
                                                           @QueryParam("parameter1") String parameter1,
                                                           @QueryParam("parameter2") String parameter2) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to subscriber addOn product offer. Reason: Subscription request is NULL");
            return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    "Subscription details not received", null, null,
                    null, WEB_SERVICE_NAME, WS_SUBSCRIBE_ADD_ON);
        }

        Integer updateAction = convertUpdateAction(request.getUpdateAction());
        Long startTime;
        try {
            startTime = convertLong(request.getStartTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to subscribe addOn product offer. Reason: Invalid start time received: " + request.getStartTime());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid start time received: " + request.getStartTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_ADD_ON);
        }

        Long endTime;
        try {
            endTime = convertLong(request.getEndTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to subscribe addOn product offer. Reason: Invalid end time received: " + request.getEndTime());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received: " + request.getEndTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_ADD_ON);
        }

        Integer priority;
        try {
            priority = convertToInteger(request.getPriority());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to subscribe addOn product offer. Reason: Invalid priority received: " + request.getPriority());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid priority received: " + request.getPriority(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_ADD_ON);
        }

        boolean updateBalanceIndication;

        if (Strings.isNullOrBlank(request.getUpdateBalanceIndication())) {
            updateBalanceIndication = false;
        } else {
            String updateBalanceIndicationStr = request.getUpdateBalanceIndication();

            if((CommonConstants.TRUE.equalsIgnoreCase(updateBalanceIndicationStr) || CommonConstants.FALSE.equalsIgnoreCase(updateBalanceIndicationStr)) == false) {
                getLogger().error(MODULE, "Unable to subscribe addon. Reason: Invalid update balance indication received: " + request.getUpdateBalanceIndication());
                return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid update balance indication received: " + request.getUpdateBalanceIndication(),
                        null, null, null,WEB_SERVICE_NAME, WS_SUBSCRIBE_ADD_ON);
            } else {
                if (CommonConstants.TRUE.equalsIgnoreCase(updateBalanceIndicationStr)) {
                    updateBalanceIndication = true;
                } else {
                    updateBalanceIndication = false;
                }
            }
        }

        return wsSubscribeAddOnProductOffer(request.getParentId(), request.getSubscriberId(), request.getAlternateId(),
                updateAction, request.getCui(), request.getAddOnProductOfferId(), request.getAddOnProductOfferName(),
                request.getSubscriptionStatusValue(), request.getSubscriptionStatusName(), startTime, endTime,
                priority, request.getFnFGroupName(),request.getFnFMembers(),updateBalanceIndication, parameter1, parameter2);
    }

    @POST
    @Path("/subscribeMonetaryRechargePlan")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public MonetaryRechargeSubscriptionResponse subscribeMonetaryRechargePlan(SubscribeMonetaryRechargePlanRestRequest request,
                                                                              @QueryParam("parameter1") String parameter1,
                                                                              @QueryParam("parameter2") String parameter2) throws ParseException, OperationFailedException{
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to subscribe Monetary Recharge Plan. Reason: Subscription request is NULL");
            return new MonetaryRechargeSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    "Subscription details not received", null, null, null, null, null,
                    0, 0, 0, 0, WEB_SERVICE_NAME, WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN);
        }

        Integer updateAction = convertUpdateAction(request.getUpdateAction());

        String priceStr = request.getPrice();

        if (Strings.isNullOrBlank(priceStr) == false) {

            if(Pattern.matches(REGEX_PATTERN, priceStr) == false) {
                getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: Invalid price received: " + priceStr);
                return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid price received: " + request.getPrice(),
                        null, null, null, null, null, 0, 0, 0, 0,  WEB_SERVICE_NAME, WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN);
            }
        }

        BigDecimal price;
        try {
            price = convertToBigDecimal(priceStr);
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: Invalid price received: " + priceStr);
            return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid price received: " + priceStr,
                    null, null, null, null, null, 0, 0, 0, 0,  WEB_SERVICE_NAME, WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN);
        }

        Boolean updateBalanceIndication;

        if (Strings.isNullOrBlank(request.getUpdateBalanceIndication())) {
            updateBalanceIndication = false;
        } else {
            String updateBalanceIndicationStr = request.getUpdateBalanceIndication();
            if((CommonConstants.TRUE.equalsIgnoreCase(updateBalanceIndicationStr) || CommonConstants.FALSE.equalsIgnoreCase(updateBalanceIndicationStr)) == false) {
                getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: Invalid update balance indication received: " + request.getUpdateBalanceIndication());
                return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid update balance indication received: " + request.getUpdateBalanceIndication(),
                        null, null, null, null, null, 0, 0, 0, 0,  WEB_SERVICE_NAME, WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN);
            } else {
                if (CommonConstants.TRUE.equalsIgnoreCase(updateBalanceIndicationStr)) {
                    updateBalanceIndication = true;
                } else {
                    updateBalanceIndication = false;
                }
            }
        }

        return wsSubscribeMonetaryRechargePlan(request.getSubscriberId(), request.getAlternateId(),
                updateAction, request.getCui(), request.getMonetaryRechargePlanId(), request.getMonetaryRechargePlanName(),
                updateBalanceIndication, price,request.getExpiryDate(),
                parameter1, parameter2);
    }

    private Integer convertToInteger(String intStr) {
        if (StringUtils.isBlank(intStr)) {
            return null;
        }

        return Integer.parseInt(intStr);
    }

    private Long convertLong(String longStr) {
        if (longStr == null || StringUtils.isBlank(longStr) || StringUtils.isBlank(longStr.trim())) {
            return null;
        }

        return Long.parseLong(longStr);
    }

    private Double convertToDouble(String str) {
        if (StringUtils.isEmpty(str))
            return null;
        return Double.parseDouble(str);
    }

    private BigDecimal convertToBigDecimal(String str) {
        if (StringUtils.isEmpty(str))
            return null;
        return new BigDecimal(str);
    }

    //TODO make utility to reuse this logic in subscriber and subscription both
    private Integer convertUpdateAction(String updateActionString) {
         if (StringUtils.isEmpty(updateActionString)) {
            return null;
        }

        try {
            return Integer.parseInt(StringUtils.trim(updateActionString));
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Error while parsing update action " + updateActionString + ". Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
        return null;
    }

    @Override
    public SubscriptionResponse wsSubscribeAddOnProductOffer(String parentId, String subscriberId, String alternateId,
                                                             Integer updateAction, String cui, String addOnProductOfferId,
                                                             String addOnProductOfferName, String subscriptionStatusValue,
                                                             String subscriptionStatusName, Long startTime, Long endTime,
                                                             Integer priority,String fnFGroupName, List<String> fnFMembers, boolean updateBalanceIndication, String parameter1, String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsSubscribeAddOnProductOffer");
        }

        parentId = StringUtil.trimParameter(parentId);
        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        cui = StringUtil.trimParameter(cui);
        addOnProductOfferId = StringUtil.trimParameter(addOnProductOfferId);
        addOnProductOfferName = StringUtil.trimParameter(addOnProductOfferName);
        subscriptionStatusValue = StringUtil.trimParameter(subscriptionStatusValue);
        subscriptionStatusName = StringUtil.trimParameter(subscriptionStatusName);
        fnFGroupName=StringUtil.trimParameter(fnFGroupName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        SubscribeAddOnWSRequest subscribeAddOnWSRequest = new SubscribeAddOnWSRequest(parentId, subscriberId,
                alternateId, updateAction, cui, addOnProductOfferId, addOnProductOfferName, subscriptionStatusValue,
                subscriptionStatusName, startTime, endTime, priority, fnFGroupName,fnFMembers, updateBalanceIndication, parameter1, parameter2,
                WEB_SERVICE_NAME, WS_SUBSCRIBE_ADD_ON);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Request Parameters: " + subscribeAddOnWSRequest.toString());
        }

        return subscriptionWSBLManager.subscribeAddOnProductOffer(subscribeAddOnWSRequest);
    }

    public MonetaryRechargeSubscriptionResponse wsSubscribeMonetaryRechargePlan(String subscriberId, String alternateId,
                                                                                Integer updateAction, String cui, String monetaryRechargePlanId,
                                                                                String monetaryRechargePlanName,
                                                                                boolean updateBalanceIndication, BigDecimal price, long expiryDate,
                                                                                String parameter1, String parameter2) throws ParseException, OperationFailedException{
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsSubscribeMonetaryRechargePlan");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        cui = StringUtil.trimParameter(cui);
        monetaryRechargePlanId = StringUtil.trimParameter(monetaryRechargePlanId);
        monetaryRechargePlanName = StringUtil.trimParameter(monetaryRechargePlanName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(", Subscriber Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(", Alternate Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(" Update Action Value: ");
            stringBuilder.append(updateAction);
            stringBuilder.append(", CUI: ");
            stringBuilder.append(cui);
            stringBuilder.append(", Monetary Recharge Plan Id: ");
            stringBuilder.append(monetaryRechargePlanId);
            stringBuilder.append(", Monetary Recharge Plan Name: ");
            stringBuilder.append(monetaryRechargePlanName);
            stringBuilder.append(", Update Balance Indication: ");
            stringBuilder.append(updateBalanceIndication);
            stringBuilder.append(", Price: ");
            stringBuilder.append(price);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }

        return subscriptionWSBLManager.subscribeMonetaryRechargePlan(new SubscribeMonetaryRechargePlanWSRequest(subscriberId, alternateId, updateAction
                , cui, monetaryRechargePlanId, monetaryRechargePlanName, updateBalanceIndication, price,expiryDate, WEB_SERVICE_NAME, WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN, parameter1, parameter2));
    }


    @PUT
    @Path("changeDataAddOnSubscription")
    public SubscriptionResponse changeDataAddOnSubscription(ChangeDataAddOnSubscriptionRestRequest request,
                                                            @QueryParam("parameter1") String parameter1,
                                                            @QueryParam("parameter2") String parameter2) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to change data addon subscription. Reason: Subscription request is NULL");
            return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    "Subscription details not received", null, null,
                    null, WEB_SERVICE_NAME, WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION);
        }

        Integer updateAction = convertUpdateAction(request.getUpdateAction());

        Long startTime;
        try {
            startTime = convertLong(request.getStartTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to change data addon product offer. Reason: Invalid start time received: " + request.getStartTime());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid start time received: " + request.getStartTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION);
        }

        Long endTime;
        try {
            endTime = convertLong(request.getEndTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to change data addon product offer. Reason: Invalid end time received: " + request.getEndTime());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received: " + request.getEndTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION);
        }

        Integer priority;
        try {
            priority = convertToInteger(request.getPriority());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to change data addon product offer. Reason: Invalid priority received: " + request.getPriority());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid priority received: " + request.getPriority(),
                    null, null, null, WEB_SERVICE_NAME, WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION);
        }

        return wsChangeDataAddOnSubscription(request.getSubscriberId(), request.getAlternateId(),
                updateAction, request.getDataAddOnSubscriptionId(),
                request.getSubscriptionStatusValue(), request.getSubscriptionStatusName(),
                request.getDataAddOnProductOfferName(), request.getSubscriptionOrder(),
                startTime, endTime, priority, request.getRejectReason(),
                parameter1, parameter2);
    }

    @Override
    public SubscriptionResponse wsChangeDataAddOnSubscription(String subscriberId, String alternateId,
                                                              Integer updateAction, String dataAddOnSubscriptionId,
                                                              String subscriptionStatusValue, String subscriptionStatusName,
                                                              String dataAddOnProductOfferName, String subscriptionOrder,
                                                              Long startTime, Long endTime, Integer priority, String rejectReason,
                                                              String parameter1, String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsChangeDataAddOnSubscription");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        dataAddOnSubscriptionId = StringUtil.trimParameter(dataAddOnSubscriptionId);
        rejectReason = StringUtil.trimParameter(rejectReason);
        subscriptionStatusValue = StringUtil.trimParameter(subscriptionStatusValue);
        subscriptionStatusName = StringUtil.trimParameter(subscriptionStatusName);
        dataAddOnProductOfferName = StringUtil.trimParameter(dataAddOnProductOfferName);
        subscriptionOrder = StringUtil.trimParameter(subscriptionOrder);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" Subscriber Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(", Update Action Value: ");
            stringBuilder.append(updateAction);
            stringBuilder.append(", Alternate Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", Data AddOn Subscription Id: ");
            stringBuilder.append(dataAddOnSubscriptionId);
            stringBuilder.append(", Data AddOn Product Offer Name: ");
            stringBuilder.append(dataAddOnProductOfferName);
            stringBuilder.append(", Subscription Order: ");
            stringBuilder.append(subscriptionOrder);
            stringBuilder.append(", Subscription Status Value: ");
            stringBuilder.append(subscriptionStatusValue);
            stringBuilder.append(", Subscription Status Name: ");
            stringBuilder.append(subscriptionStatusName);
            stringBuilder.append(", Start Time: ");
            stringBuilder.append(startTime);
            stringBuilder.append(", End Time: ");
            stringBuilder.append(endTime);
            stringBuilder.append(", Priority: ");
            stringBuilder.append(priority);
            stringBuilder.append(", Reject Reason: ");
            stringBuilder.append(rejectReason);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }

        return subscriptionWSBLManager.changeDataAddOnSubscription(new ChangeDataAddOnSubscriptionWSRequest(subscriberId, alternateId,
                updateAction, dataAddOnSubscriptionId, subscriptionStatusValue, subscriptionStatusName, dataAddOnProductOfferName,
                subscriptionOrder, startTime, endTime, priority, rejectReason, parameter1, parameter2,
                WEB_SERVICE_NAME, WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION));
    }

    @POST
    @Path("/subscribeTopUp")
    public TopUpSubscriptionResponse wsSubscribeTopUp(SubscribeTopUpRestRequest request,
                                                      @QueryParam("parameter1") String parameter1,
                                                      @QueryParam("parameter2") String parameter2) {

        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to subscribe topup. Reason: Subscription request is NULL");
            return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    "Subscription details not received", null, null,
                    null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        Integer updateAction = convertUpdateAction(request.getUpdateAction());

        Long startTime;
        try {
            startTime = convertLong(request.getStartTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to subscribe topup. Reason: Invalid start time received: " + request.getStartTime());
            return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid start time received: " + request.getStartTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        Long endTime;
        try {
            endTime = convertLong(request.getEndTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to subscribe topup. Reason: Invalid end time received: " + request.getEndTime());
            return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received: " + request.getEndTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        Integer priority;
        try {
            priority = convertToInteger(request.getPriority());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to subscribe topup. Reason: Invalid priority received: " + request.getPriority());
            return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid priority received: " + request.getPriority(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        boolean updateBalanceIndication ;

        if (Strings.isNullOrBlank(request.getUpdateBalanceIndication())) {
            updateBalanceIndication = false;
        } else {
            String updateBalanceIndicationStr = request.getUpdateBalanceIndication();

            if((CommonConstants.TRUE.equalsIgnoreCase(updateBalanceIndicationStr) || CommonConstants.FALSE.equalsIgnoreCase(updateBalanceIndicationStr)) == false) {
                getLogger().error(MODULE, "Unable to subscribe topup. Reason: Invalid update balance indication received: " + request.getUpdateBalanceIndication());
                return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid update balance indication received: " + request.getUpdateBalanceIndication(),
                        null, null, null,WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
            } else {
                if (CommonConstants.TRUE.equalsIgnoreCase(updateBalanceIndicationStr)) {
                    updateBalanceIndication = true;
                } else {
                    updateBalanceIndication = false;
                }
            }        }

        return wsSubscribeTopUp(request.getParentId(), request.getSubscriberId(), request.getAlternateId(), updateAction, request.getCui(),
                request.getTopUpPackageId(), request.getTopUpPackageName(), request.getSubscriptionStatusValue(), request.getSubscriptionStatusName(),
                startTime, endTime, priority, updateBalanceIndication, parameter1, parameter2);
    }

    @Override
    public TopUpSubscriptionResponse wsSubscribeTopUp(String parentId, String subscriberId, String alternateId, Integer updateAction, String cui,
                                                      String topUpPackageId, String topUpPackageName, String subscriptionStatusValue, String subscriptionStatusName,
                                                      Long startTime, Long endTime, Integer priority, boolean updateBalanceIndication, String parameter1, String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsSubscribeTopUp");
        }

        parentId = StringUtil.trimParameter(parentId);
        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        cui = StringUtil.trimParameter(cui);
        topUpPackageId = StringUtil.trimParameter(topUpPackageId);
        topUpPackageName = StringUtil.trimParameter(topUpPackageName);
        subscriptionStatusValue = StringUtil.trimParameter(subscriptionStatusValue);
        subscriptionStatusName = StringUtil.trimParameter(subscriptionStatusName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" Parent Id: ");
            stringBuilder.append(parentId);
            stringBuilder.append(", Subscriber Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(", Alternate Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(" Update Action Value: ");
            stringBuilder.append(updateAction);
            stringBuilder.append(", CUI: ");
            stringBuilder.append(cui);
            stringBuilder.append(", TopUp Package Name: ");
            stringBuilder.append(topUpPackageName);
            stringBuilder.append(", Subscription Status Value: ");
            stringBuilder.append(subscriptionStatusValue);
            stringBuilder.append(", Subscription Status Name: ");
            stringBuilder.append(subscriptionStatusName);
            stringBuilder.append(", Start Time: ");
            stringBuilder.append(startTime);
            stringBuilder.append(", End Time: ");
            stringBuilder.append(endTime);
            stringBuilder.append(", Priority: ");
            stringBuilder.append(priority);
            stringBuilder.append(", Update Balance Indication: ");
            stringBuilder.append(updateBalanceIndication);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }

        return subscriptionWSBLManager.subscribeTopUp(new SubscribeTopUpWSRequest(parentId, subscriberId, alternateId, updateAction
                , cui, topUpPackageId, topUpPackageName, subscriptionStatusValue, subscriptionStatusName, startTime, endTime, priority, updateBalanceIndication,
                parameter1, parameter2, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP));
    }

    @GET
    @Path("/listTopUpPackages")
    @Override
    @ApiOperation(
            value = "To fetch list of topup packages",
            notes = "Get operation with Response and @Default value",
            response = TopUpQueryResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid Input Parameter with one of following reasons;<br/>" +
                    "1. Base package does not have any quota profile attached <br/>"+
                    "2. Product offer does not have any base package attached"),
            @ApiResponse(code = 404, message = "Not found with one of following reasons;<br/>" +
                    "1. Subscriber not found with alternate identity:<br/>"+
                    "2. No product offer found with name: [productOfferName] for alternate Identity:<br/>" +
                    "3. Reason: Subscriber not found with subscriber identity:<br/>" +
                    "4. No product offer found with name: [productOfferName] for subscriber Identity:<br/>" +
                    "5. Reason: base product offer with name: [productOfferName] doesn't exist<br/>" +
                    "6. No topup found "),
            @ApiResponse(code = 500, message = "Internal Error")
    })

    public TopUpQueryResponse wsListTopUpPackages(@QueryParam("subscriberId")String subscriberId,
                                                  @QueryParam("alternateId")String alternateId,
                                                  @QueryParam("productOfferName")String productOfferName,
                                                  @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListTopUpPackages");
        }
        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        productOfferName = StringUtil.trimParameter(productOfferName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("subscriber Id: ")
                    .append(subscriberId)
                    .append(", alternate Id: ")
                    .append(alternateId)
                    .append(", product Offer Name: ")
                    .append(productOfferName)
                    .append(", Parameter1: ")
                    .append(parameter1)
                    .append(", Parameter2: ")
                    .append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }
        return subscriptionWSBLManager.getTopUpPackages(new TopUpQueryRequest(subscriberId, alternateId, productOfferName, parameter1, parameter2,
                WEB_SERVICE_NAME, WS_LIST_TOP_UP_PACKAGES));
    }

    @GET
    @Path("/listBoDPackages")
    @Override
    @ApiOperation(
            value = "To fetch list of BoD packages",
            notes = "Get operation with Response and @Default value",
            response = BoDQueryResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid Input Parameter with one of following reasons;<br/>" +
                    "1. Base package does not have QoS profile details configured <br/>"+
                    "2. Product offer is not of type Base<br/>"+
                    "3. Product offer does not have any base package attached"),
            @ApiResponse(code = 404, message = "Not found with one of following reasons;<br/>" +
                    "1. Subscriber not found with alternate identity<br/>"+
                    "2. No product offer found with name: [productOfferName] for alternate Identity<br/>" +
                    "3. Subscriber not found with subscriber identity<br/>" +
                    "4. No product offer found with name: [productOfferName] for subscriber Identity<br/>" +
                    "5. Base product offer with name: [productOfferName] doesn't exist<br/>" +
                    "6. No BoD found "),
            @ApiResponse(code = 500, message = "Internal Error")
    })
    public BoDQueryResponse wsListBoDPackages(@QueryParam("subscriberId")String subscriberId,
                                              @QueryParam("alternateId")String alternateId,
                                              @QueryParam("productOfferName")String productOfferName,
                                              @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListBoDPackages");
        }
        BoDQueryRequest request = new BoDQueryRequest(StringUtil.trimParameter(subscriberId),
                StringUtil.trimParameter(alternateId),
                StringUtil.trimParameter(productOfferName),
                StringUtil.trimParameter(parameter1),
                StringUtil.trimParameter(parameter2),
                WEB_SERVICE_NAME, WS_LIST_BOD_PACKAGES);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Request Parameters: " + request.toString());
        }
        return subscriptionWSBLManager.getBoDPackages(request);
    }

    @PUT
    @Path("/changeTopUpSubscription")
    public TopUpSubscriptionResponse changeTopUpSubscription(ChangeTopUpSubscriptionRestRequest request,
                                                             @QueryParam("parameter1") String parameter1,
                                                             @QueryParam("parameter1") String parameter2) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to change topup subscription. Reason: Subscription request is NULL");
            return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    "Subscription details not received", null, null,
                    null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        Integer updateAction = convertUpdateAction(request.getUpdateAction());

        Long startTime;
        try {
            startTime = convertLong(request.getStartTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to change topup subscription. Reason: Invalid start time received: " + request.getStartTime());
            return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid start time received: " + request.getStartTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        Long endTime;
        try {
            endTime = convertLong(request.getEndTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to change topup subscription. Reason: Invalid end time received: " + request.getEndTime());
            return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received: " + request.getEndTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        Integer priority;
        try {
            priority = convertToInteger(request.getPriority());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to subscribe topup. Reason: Invalid priority received: " + request.getPriority());
            return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid priority received: " + request.getPriority(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }
        return wsChangeTopUpSubscription(request.getSubscriberId(), request.getAlternateId(), updateAction, request.getTopUpSubscriptionId(),
                request.getSubscriptionStatusValue(), request.getSubscriptionStatusName(), request.getTopUpName(), request.getSubscriptionOrder(),
                startTime, endTime, priority, request.getRejectReason(), parameter1, parameter2);
    }

    @Override
    public TopUpSubscriptionResponse wsChangeTopUpSubscription(String subscriberId, String alternateId, Integer updateAction, String topUpSubscriptionId,
                                                               String subscriptionStatusValue, String subscriptionStatusName, String topUpName, String subscriptionOrder,
                                                               Long startTime, Long endTime, Integer priority, String rejectReason, String parameter1, String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsChangeTopUpOnSubscription");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        topUpSubscriptionId = StringUtil.trimParameter(topUpSubscriptionId);
        rejectReason = StringUtil.trimParameter(rejectReason);
        subscriptionStatusValue = StringUtil.trimParameter(subscriptionStatusValue);
        subscriptionStatusName = StringUtil.trimParameter(subscriptionStatusName);
        topUpName = StringUtil.trimParameter(topUpName);
        subscriptionOrder = StringUtil.trimParameter(subscriptionOrder);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" Subscriber Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(" Update Action Value: ");
            stringBuilder.append(updateAction);
            stringBuilder.append(", Alternate Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", TopUp Subscription Id: ");
            stringBuilder.append(topUpSubscriptionId);
            stringBuilder.append(", TopUp Name: ");
            stringBuilder.append(topUpName);
            stringBuilder.append(", Subscription Order: ");
            stringBuilder.append(subscriptionOrder);
            stringBuilder.append(", Subscription Status Value: ");
            stringBuilder.append(subscriptionStatusValue);
            stringBuilder.append(", Subscription Status Name: ");
            stringBuilder.append(subscriptionStatusName);
            stringBuilder.append(", Start Time: ");
            stringBuilder.append(startTime);
            stringBuilder.append(", End Time: ");
            stringBuilder.append(endTime);
            stringBuilder.append(", Priority: ");
            stringBuilder.append(priority);
            stringBuilder.append(", Reject Reason: ");
            stringBuilder.append(rejectReason);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }

        return subscriptionWSBLManager.changeTopUpSubscription(new ChangeTopUpSubscriptionWSRequest(subscriberId, alternateId, updateAction,
                topUpSubscriptionId, subscriptionStatusValue, subscriptionStatusName, topUpName, subscriptionOrder, startTime, endTime,
                rejectReason, parameter1, parameter2, priority, WEB_SERVICE_NAME, WS_CHANGE_TOP_UP_SUBSCRIPTION));
    }

    @GET
    @Path("/listRnCPackages")
    @Override
    public RnCPackageQueryResponse wsListRnCPackages(@QueryParam("rncPackageId") String rncPackageId,
                                                     @QueryParam("rncPackageName") String rncPackageName,
                                                     @QueryParam("rncPackageType") String rncPackageType,
                                                     @QueryParam("currency") String currency,
                                                     @QueryParam("parameter1") String parameter1,
                                                     @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListRnCPackages");
        }

        rncPackageId = StringUtil.trimParameter(rncPackageId);
        rncPackageName = StringUtil.trimParameter(rncPackageName);
        rncPackageType = StringUtil.trimParameter(rncPackageType);
        currency = StringUtil.trimParameter(currency);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(", Pacakge Id : ").append(rncPackageId);
            stringBuilder.append(", Package Name : ").append(rncPackageName);
            stringBuilder.append(", Package Type : ").append(rncPackageType);
            stringBuilder.append(", Currency : ").append(currency);
            stringBuilder.append(", Parameter1 : ").append(parameter1);
            stringBuilder.append(", Parameter2 : ").append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }
        return packageWSBlManager.getRnCPackages(new ListPackageRequest(rncPackageId, rncPackageName, rncPackageType,currency,
                parameter1, parameter2, WEB_SERVICE_NAME, WS_LIST_RNC_PACKAGES));
    }


    @PUT
    @Path("/changeRnCAddOnSubscription")
    public SubscriptionResponse changeRnCAddOnSubscription(ChangeRnCAddOnSubscriptionRestRequest request,
                                                           @QueryParam("parameter1") String parameter1,
                                                           @QueryParam("parameter2") String parameter2) {

        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to change rnc addon subscription. Reason: Subscription request is NULL");
            return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    "Subscription details not received", null, null,
                    null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        Integer updateAction = convertUpdateAction(request.getUpdateAction());

        Long startTime;
        try {
            startTime = convertLong(request.getStartTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to change rnc addon subscription. Reason: Invalid start time received: " + request.getStartTime());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid start time received: " + request.getStartTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        Long endTime;
        try {
            endTime = convertLong(request.getEndTime());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to change rnc addon subscription. Reason: Invalid end time received: " + request.getEndTime());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received: " + request.getEndTime(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        Integer priority;
        try {
            priority = convertToInteger(request.getPriority());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to change rnc addon subscription. Reason: Invalid priority received: " + request.getPriority());
            return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid priority received: " + request.getPriority(),
                    null, null, null, WEB_SERVICE_NAME, WS_SUBSCRIBE_TOP_UP);
        }

        return wsChangeRnCAddOnSubscription(request.getSubscriberId(), request.getAlternateId(), updateAction, request.getSubscriptionStatusValue(),
                request.getSubscriptionStatusName(), request.getRncAddOnSubscriptionId(), request.getRncAddOnProductOfferName(),
                request.getSubscriptionOrder(), startTime, endTime, priority, request.getRejectReason(),
                parameter1, parameter2);
    }

    @Override
    public SubscriptionResponse wsChangeRnCAddOnSubscription(String subscriberId, String alternateId, Integer updateAction, String subscriptionStatusValue,
                                                             String subscriptionStatusName, String rncAddOnSubscriptionId, String rncAddOnProductOfferName,
                                                             String subscriptionOrder, Long startTime, Long endTime, Integer priority, String rejectReason,
                                                             String parameter1, String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsChangeRnCAddOnSubscription");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        rejectReason = StringUtil.trimParameter(rejectReason);
        subscriptionStatusValue = StringUtil.trimParameter(subscriptionStatusValue);
        subscriptionStatusName = StringUtil.trimParameter(subscriptionStatusName);
        rncAddOnSubscriptionId = StringUtil.trimParameter(rncAddOnSubscriptionId);
        rncAddOnProductOfferName = StringUtil.trimParameter(rncAddOnProductOfferName);
        subscriptionOrder = StringUtil.trimParameter(subscriptionOrder);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" Subscriber Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(", Update Action Value: ");
            stringBuilder.append(updateAction);
            stringBuilder.append(", Alternate Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", RnC AddOn Subscription Id: ");
            stringBuilder.append(rncAddOnSubscriptionId);
            stringBuilder.append(", RnC AddOn Product Offer Name: ");
            stringBuilder.append(rncAddOnProductOfferName);
            stringBuilder.append(", Subscription Order: ");
            stringBuilder.append(subscriptionOrder);
            stringBuilder.append(", Subscription Status Value: ");
            stringBuilder.append(subscriptionStatusValue);
            stringBuilder.append(", Subscription Status Name: ");
            stringBuilder.append(subscriptionStatusName);
            stringBuilder.append(", Start Time: ");
            stringBuilder.append(startTime);
            stringBuilder.append(", End Time: ");
            stringBuilder.append(endTime);
            stringBuilder.append(", Priority: ");
            stringBuilder.append(priority);
            stringBuilder.append(", Reject Reason: ");
            stringBuilder.append(rejectReason);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }

        return subscriptionWSBLManager.changeRnCAddonSubscription(new ChangeRnCAddOnProductOfferWSRequest(subscriberId, alternateId, updateAction,
                rncAddOnSubscriptionId, subscriptionStatusValue, subscriptionStatusName, rncAddOnProductOfferName, subscriptionOrder, startTime,
                endTime, priority, rejectReason, parameter1, parameter2, WEB_SERVICE_NAME, WS_CHANGE_RNC_ADD_ON_SUBSCRIPTION));
    }


    @GET
    @Path("/getBalance")
    public BalanceEnquiryResponse wsGetBalance(
            @QueryParam(value = "subscriberId") String subscriberId,
            @QueryParam(value = "alternateId") String alternateId,
            @QueryParam(value = "packageId") String packageId,
            @QueryParam(value = "packageName") String packageName,
            @QueryParam(value = "subscriptionId") String subscriptionId,
            @QueryParam(value = "parameter1") String parameter1,
            @QueryParam(value = "parameter2") String parameter2) {
        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        packageId = StringUtil.trimParameter(packageId);
        packageName = StringUtil.trimParameter(packageName);
        subscriptionId = StringUtil.trimParameter(subscriptionId);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsGetBalance with Request Parameters: "
                    + " Subscriber Id: " + subscriberId
                    + " Alternate Id: " + alternateId
                    + " Package Id: " + packageId
                    + " package Name: " + packageName
                    + " Subscription Id: " + subscriberId
                    + " Parameter1: " + parameter1
                    + ", Parameter2: " + parameter2);
        }
        return subscriptionWSBLManager.getBalance(new BalanceEnquiryRequest(subscriberId, alternateId, packageId, packageName,
                subscriptionId, parameter1, parameter2, WEB_SERVICE_NAME, WS_GET_BALANCE));
    }


    @POST
    @Path("/addMonetaryBalance")
    public MonetoryOperationResponse addMonetaryBalance(AddMonetaryBalanceRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to add monetary balance. Reason: request is NULL");
            return new MonetoryOperationResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received", null, parameter1, parameter2, WEB_SERVICE_NAME, WS_ADD_MONETARY_BALANCE);
        }

        Long validFromDate;
        try {
            validFromDate = convertLong(request.getValidFromDate());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to add monetary balance. Reason: Invalid from date received: " + request.getValidFromDate());
            return new MonetoryOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid from date received: " + request.getValidFromDate(),
                    null, null, null, WEB_SERVICE_NAME, WS_ADD_MONETARY_BALANCE);
        }

        Long toDate;
        try {
            toDate = convertLong(request.getValidToDate());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to add monetary balance. Reason: Invalid to date received: " + request.getValidToDate());
            return new MonetoryOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid to date received: " + request.getValidToDate(),
                    null, null, null, WEB_SERVICE_NAME, WS_ADD_MONETARY_BALANCE);
        }

        Double totalBalance;
        try {
            totalBalance = Double.parseDouble(request.getTotalBalance());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to add monetary balance. Reason: Invalid total balance received: " + request.getValidToDate());
            return new MonetoryOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid total balance received: " + request.getTotalBalance(),
                    null, null, null, WEB_SERVICE_NAME, WS_ADD_MONETARY_BALANCE);
        }

        return wsAddMonetaryBalance(request.getSubscriberId(), request.getAlternateId(), request.getServiceId(), request.getServiceName(), totalBalance, validFromDate, toDate,request.getTransactionId(),
                parameter1, parameter2);
    }


    @PUT
    @Path("/updateMonetaryBalance")
    public UpdateMonetaryBalanceResponse updateMonetaryBalance(UpdateMonetaryBalanceRestRequest request,@QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to update monetary balance. Reason: request is NULL");
            return new UpdateMonetaryBalanceResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received", null, parameter1, parameter2, WEB_SERVICE_NAME, WS_UPDATE_MONETARY_BALANCE);
        }
        Long toDate;
        try {
            toDate = convertLong(request.getValidToDate());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to update monetary balance. Reason: Invalid to date received: " + request.getValidToDate());
            return new UpdateMonetaryBalanceResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received: " + request.getValidToDate(),
                    null, null, null, WEB_SERVICE_NAME, WS_ADD_MONETARY_BALANCE);
        }

        Double amount;
        try {
            amount = convertToDouble(request.getAmount());
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Unable to update monetary balance. Reason: Invalid amount received: " + request.getAmount());
            return new UpdateMonetaryBalanceResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid amount received: " + request.getAmount(),
                    null, null, null, WEB_SERVICE_NAME, WS_ADD_MONETARY_BALANCE);
        }

        Integer updateAction = convertUpdateAction(request.getUpdateAction());
        return wsUpdateMonetaryBalance(request.getSubscriberId(), request.getAlternateId(), updateAction, request.getBalanceId(), request.getServiceId(), request.getServiceName(),
                amount, request.getOperation(), toDate, request.getReason(), request.getTransactionId(), parameter1, parameter2);
    }

    @PUT
    @Path("/updateCreditLimit")
    public UpdateCreditLimitResponse updateCreditLimit(UpdateCreditLimitRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to update credit Limit. Reason: no request parameter received");
            return new UpdateCreditLimitResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received", null, parameter1, parameter2, WEB_SERVICE_NAME, WS_UPDATE_CREDIT_LIMIT);
        }

        return wsUpdateCreditLimit(request.getSubscriberId(), request.getAlternateId(), request.getCreditLimit(), request.getApplicableBillingCycle(), parameter1, parameter2);
    }


    @Override
    @GET
    @Path("/listUsageMonitoringInformation")
    public UMQueryResponse wsListUsageMonitoringInformation(@QueryParam("subscriberId") String subscriberId, @QueryParam("alternateId") String alternateId,
                                                            @QueryParam("packageId") String packageId, @QueryParam("packageName") String packageName,
                                                            @QueryParam("quotaProfileId") String quotaProfileId, @QueryParam("quotaProfileName") String quotaProfileName,
                                                            @QueryParam("serviceId") String serviceId, @QueryParam("serviceName") String serviceName,
                                                            @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListUsageMonitoringInformation");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        packageId = StringUtil.trimParameter(packageId);
        packageName = StringUtil.trimParameter(packageName);
        quotaProfileId = StringUtil.trimParameter(quotaProfileId);
        quotaProfileName = StringUtil.trimParameter(quotaProfileName);
        serviceId = StringUtil.trimParameter(serviceId);
        serviceName = StringUtil.trimParameter(serviceName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(", Subscriber Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(", Alternate Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", Package Id: ");
            stringBuilder.append(packageId);
            stringBuilder.append(", Package Name: ");
            stringBuilder.append(packageName);
            stringBuilder.append(", Quota Profile Id: ");
            stringBuilder.append(quotaProfileId);
            stringBuilder.append(", Quota Profile Name: ");
            stringBuilder.append(quotaProfileName);
            stringBuilder.append(", Service Id: ");
            stringBuilder.append(serviceId);
            stringBuilder.append(", Service Name: ");
            stringBuilder.append(serviceName);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }
        return subscriptionWSBLManager.getUsageMonitoringInformation(new ListUsageMonitoringInformationWSRequest(subscriberId, alternateId, packageId, packageName, quotaProfileId, quotaProfileName, serviceId, serviceName, parameter1, parameter2, WEB_SERVICE_NAME, WS_LIST_USAGE_MONITORING_INFORMATION));
    }

    @GET
    @Path("/getNonMonetaryBalance")
    @Override
    public NonMonitoryBalanceInquiryResponse wsGetNonMonitoryBalance(@QueryParam("subscriberId") String subscriberId,
                                                                     @QueryParam("alternateId") String alternateId,
                                                                     @QueryParam("packageId") String packageId,
                                                                     @QueryParam("packageName") String packageName,
                                                                     @QueryParam("subscriptionId") String subscriptionId,
                                                                     @QueryParam("parameter1") String parameter1,
                                                                     @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsGetNonMonitoryBalance");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        packageId = StringUtil.trimParameter(packageId);
        packageName = StringUtil.trimParameter(packageName);
        subscriptionId = StringUtil.trimParameter(subscriptionId);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(", Primary Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(", Secondary Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", Package Id: ");
            stringBuilder.append(packageId);
            stringBuilder.append(", Package Name: ");
            stringBuilder.append(packageName);
            stringBuilder.append(", Subscription Id: ");
            stringBuilder.append(subscriptionId);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }
        return subscriptionWSBLManager.getNonMonitoryBalance(new NonMonitoryBalanceEnquiryRequest(subscriberId, alternateId, packageId, packageName, subscriptionId, parameter1, parameter2, WEB_SERVICE_NAME, WS_GET_NONMONITORY_BALANCE));
    }

    @Override
    @GET
    @Path("/getMonetaryBalance")
    public MonetaryBalanceInquiryResponse wsGetMonetaryBalance(@QueryParam("subscriberId") String subscriberId,
                                                               @QueryParam("alternateId") String alternateId,
                                                               @QueryParam("serviceId") String serviceId,
                                                               @QueryParam("serviceName") String serviceName,
                                                               @QueryParam("parameter1") String parameter1,
                                                               @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsGetMonetaryBalance");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        serviceId = StringUtil.trimParameter(serviceId);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(", Primary Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(", Secondary Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", Service Id: ");
            stringBuilder.append(serviceId);
            stringBuilder.append(", Service Name: ");
            stringBuilder.append(serviceName);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }
        return subscriptionWSBLManager.getMonetaryBalance(new MonetaryBalanceEnquiryRequest(subscriberId, alternateId, serviceId, serviceName, parameter1, parameter2, WEB_SERVICE_NAME, WS_GET_MONETARY_BALANCE));
    }

    @Override
    @GET
    @Path("/getRnCBalance")
    public RnCBalanceEnquiryResponse wsGetRnCBalance(
            @QueryParam("subscriberId") String subscriberId,
            @QueryParam("alternateId") String alternateId,
            @QueryParam("packageId") String packageId,
            @QueryParam("packageName") String packageName,
            @QueryParam("subscriptionId") String subscriptionId,
            @QueryParam("parameter1") String parameter1,
            @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsGetNonMonitoryBalance");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        packageId = StringUtil.trimParameter(packageId);
        packageName = StringUtil.trimParameter(packageName);
        subscriptionId = StringUtil.trimParameter(subscriptionId);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(", Primary Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(", Secondary Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", Package Id: ");
            stringBuilder.append(packageId);
            stringBuilder.append(", Package Name: ");
            stringBuilder.append(packageName);
            stringBuilder.append(", Subscription Id: ");
            stringBuilder.append(subscriptionId);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }
        return subscriptionWSBLManager.getRnCBalance(new RnCBalanceEnquiryRequest(subscriberId, alternateId, packageId, packageName, subscriptionId, parameter1, parameter2, WEB_SERVICE_NAME, WS_GET_RNC_BALANCE));
    }


    public MonetoryOperationResponse wsAddMonetaryBalance(String subscriberId, String alternateId, String serviceId, String serviceName, Double totalBalance, Long validFromDate, Long validToDate,String transactionId, String parameter1, String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsAddMonetaryBalance");
        }
        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        serviceId = StringUtil.trimParameter(serviceId);
        serviceName = StringUtil.trimParameter(serviceName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        MonetaryOperationRequest monetaryOperationRequest = new MonetaryOperationRequest(subscriberId,
                alternateId, serviceId, serviceName, totalBalance, validFromDate, validToDate,transactionId,parameter1,
                parameter2, WEB_SERVICE_NAME, WS_ADD_MONETARY_BALANCE);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Request Parameters: " + monetaryOperationRequest.toString());
        }
        return subscriptionWSBLManager.addMonetaryBalance(monetaryOperationRequest);
    }


    @Override
    public UpdateMonetaryBalanceResponse wsUpdateMonetaryBalance(String subscriberId, String alternateId, Integer updateAction, String balanceId, String serviceId, String serviceName, Double amount, String operation, Long validToDate, String reason, String transactionId, String parameter1, String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsUpdateMonetaryBalance");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        serviceId = StringUtil.trimParameter(serviceId);
        serviceName = StringUtil.trimParameter(serviceName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        com.elitecore.nvsmx.ws.subscription.request.UpdateMonetaryBalanceRequest updateMonetaryBalanceRequest = new com.elitecore.nvsmx.ws.subscription.request.UpdateMonetaryBalanceRequest(subscriberId,
                alternateId, updateAction, balanceId, serviceId, serviceName, amount, operation, validToDate, reason, transactionId, parameter1, parameter2, WEB_SERVICE_NAME, WS_UPDATE_MONETARY_BALANCE);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Request Parameters: " + updateMonetaryBalanceRequest.toString());
        }
        return subscriptionWSBLManager.updateMonetaryBalance(updateMonetaryBalanceRequest);
    }


    @Override
    public UpdateCreditLimitResponse wsUpdateCreditLimit(String subscriberId, String alternateId, String creditLimit, String applicableBillingCycle, String parameter1, String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsUpdateCreditLimit");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        creditLimit = StringUtil.trimParameter(creditLimit);
        applicableBillingCycle = StringUtil.trimParameter(applicableBillingCycle);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" Subscriber Id: ");
            stringBuilder.append(subscriberId);
            stringBuilder.append(", Alternate Id: ");
            stringBuilder.append(alternateId);
            stringBuilder.append(", Credit Limit: ");
            stringBuilder.append(creditLimit);
            stringBuilder.append(", Billing Cycle applicability: ");
            stringBuilder.append(applicableBillingCycle);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }

        return subscriptionWSBLManager.updateCreditLimit(new UpdateCreditLimitWSRequest(subscriberId, alternateId, creditLimit, applicableBillingCycle, parameter1, parameter2, WEB_SERVICE_NAME, WS_UPDATE_CREDIT_LIMIT));
    }

    @PUT
    @Path("/changeBillDay")
    public ChangeBillDayResponse wsChangeBillDay(ChangeBillDayRestRequest request, @QueryParam("parameter1") String parameter1,
                                                 @QueryParam("parameter2") String parameter2) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to change bill day. Reason: no request parameter received");
            return new ChangeBillDayResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received", null, parameter1, parameter2, WEB_SERVICE_NAME, WS_UPDATE_CREDIT_LIMIT);
        }
        Integer nextBillDate = null;
        try {
            nextBillDate = ConvertStringToDigit.convertStringToInt(request.getNextBillDate());
        } catch (OperationFailedException e) {
            getLogger().error(MODULE, "Unable to change bill day. Reason: Invalid Next Bill Date passed");
            getLogger().trace(MODULE,e);
            return new ChangeBillDayResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Next Bill Date passed ", null, parameter1, parameter2, WEB_SERVICE_NAME, WS_UPDATE_CREDIT_LIMIT);

        }
        return wsChangeBillDay(request.getSubscriberId(), request.getAlternateId(), nextBillDate, parameter1, parameter2);


    }

    @Override
    public ChangeBillDayResponse wsChangeBillDay(String subscriberId,
                                                 String alternateId,
                                                 Integer nextBillDate,
                                                 @QueryParam("parameter1") String parameter1,
                                                 @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsListRnCPackages");
        }

        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(", Subscriber Id : ").append(subscriberId);
            stringBuilder.append(", Alternate Id : ").append(alternateId);
            stringBuilder.append(", Next Bill Date : ").append(nextBillDate);
            stringBuilder.append(", Parameter1 : ").append(parameter1);
            stringBuilder.append(", Parameter2 : ").append(parameter2);
            getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
        }

        return subscriptionWSBLManager.changeBillDay(new ChangeBillDayWSRequest(subscriberId, alternateId, nextBillDate, parameter1, parameter2, WEB_SERVICE_NAME, WS_CHANGE_BILL_DAY));
    }

    @POST
    @Path("/subscribeBoD")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(
            value = "To subscribe BoD",
            notes = "Get operation with Response and @Default value",
            response = BodSubscriptionResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid Input Parameter with one of following reasons;<br/>" +
                    "1. Invalid start time received <br/>"+
                    "2. Invalid end time received <br/>"+
                    "3. Invalid priority received<br/>"+
                    "4. Invalid subscription status received<br/>"+
                    "5. ACTIVE BoD not found with id<br/>" +
                    "6. ACTIVE BoD not found with name<br/>" +
                    "7. BoD package id and name are not related<br/>" +
                    "8. Bod plan failed/partial fail<br/>" +
                    "9. Subscriber Status is InActive<br/>" +
                    "10. Subscriber Profile has expired<br/>" +
                    "11. BoD Plan with future Availability Start Date is not be allowed to subscribe.<br/>" +
                    "12. BoD Plan with Past Availability End Date is not be allowed to subscribe."),
            @ApiResponse(code = 401, message = "Identity parameter missing"),
            @ApiResponse(code = 404, message = "Subscriber Not Found"),
            @ApiResponse(code = 500, message = "Internal Error")
    })
    public BodSubscriptionResponse subscribeBoD(SubscribeBodRestRequest request,
                                                @QueryParam("parameter1") String parameter1,
                                                @QueryParam("parameter2") String parameter2) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to subscriber BoD. Reason: Subscription request is NULL");
            return new BodSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    "Subscription details not received", null, null,
                    null, WEB_SERVICE_NAME, WS_SUBSCRIBE_BOD);
        }

        Integer updateAction = convertUpdateAction(request.getUpdateAction());

        boolean updateBalanceIndication;

        if (Strings.isNullOrBlank(request.getUpdateBalanceIndication())) {
            updateBalanceIndication = false;
        } else {
            String updateBalanceIndicationStr = request.getUpdateBalanceIndication();

            if((CommonConstants.TRUE.equalsIgnoreCase(updateBalanceIndicationStr) || CommonConstants.FALSE.equalsIgnoreCase(updateBalanceIndicationStr)) == false) {
                getLogger().error(MODULE, "Unable to subscribe BoD. Reason: Invalid update balance indication received: " + request.getUpdateBalanceIndication());
                return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid update balance indication received: " + request.getUpdateBalanceIndication(),
                        null, null, null,WEB_SERVICE_NAME, WS_SUBSCRIBE_BOD);
            } else {
                if (CommonConstants.TRUE.equalsIgnoreCase(updateBalanceIndicationStr)) {
                    updateBalanceIndication = true;
                } else {
                    updateBalanceIndication = false;
                }
            }
        }

        return wsSubscribeBoD(request.getParentId(), request.getSubscriberId(), request.getAlternateId(),
                updateAction,request.getCui(), request.getBodId(), request.getBodName(),
                request.getSubscriptionStatusValue(), request.getSubscriptionStatusName(), request.getStartTime(), request.getEndTime(),
                request.getPriority(), updateBalanceIndication, parameter1, parameter2);
    }

    private BodSubscriptionResponse wsSubscribeBoD(String parentId, String subscriberId, String alternateId,
                                                  Integer updateAction, String cui, String bodId,
                                                  String bodName, String subscriptionStatusValue,
                                                  String subscriptionStatusName, String startTime, String endTime,
                                                  String priority, boolean updateBalanceIndication, String parameter1, String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsSubscribeBoD");
        }

        parentId = StringUtil.trimParameter(parentId);
        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        cui = StringUtil.trimParameter(cui);
        bodId = StringUtil.trimParameter(bodId);
        bodName = StringUtil.trimParameter(bodName);
        subscriptionStatusValue = StringUtil.trimParameter(subscriptionStatusValue);
        subscriptionStatusName = StringUtil.trimParameter(subscriptionStatusName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        SubscribeBodWsRequest subscribeBodWsRequest = new SubscribeBodWsRequest(parentId,subscriberId,alternateId,
                cui,bodId,bodName,subscriptionStatusValue,subscriptionStatusName,startTime,endTime,updateAction,
                priority, updateBalanceIndication, parameter1,parameter2,WEB_SERVICE_NAME,WS_SUBSCRIBE_BOD);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Request Parameters: " + subscribeBodWsRequest.toString());
        }

        return subscriptionWSBLManager.subscribeBod(subscribeBodWsRequest);
    }

    @PUT
    @Path("/fnfGroupMembers")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(
            value = "To add members to the specified FnF group",
            notes = "PUT operation with Response and @Default value",
            response = BodSubscriptionResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid Input Parameter with one of following reasons;<br/>" +
                    "1. Subscriber status is inactive<br/>" +
                    "2. Subscriber profile has been expired<br/>" +
                    "3. FnF group does not exist<br/>" +
                    "4. Invalid FnF member value<br/>" +
                    "4. Member already part of the group"),
            @ApiResponse(code = 401, message = "Missing mendatory parameters.<br/>" +
                    "1. Subscriber Id or Alternate Id<br/>" +
                    "2. Group name<br/>" +
                    "3. Members list is empty"),
            @ApiResponse(code = 404, message = "Subscriber Not Found"),
            @ApiResponse(code = 500, message = "Internal Error")
    })
    @Override
    public FnFOperationResponse addFnFMembers(FnFOperationRequest request) {
        request.setOperation(FnFOperationRequest.Operation.ADD);
        request.setWebServiceName(WEB_SERVICE_NAME);
        request.setWebServiceMethodName(WS_FNF_GROUP_MEMBERS );
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Request Parameters: " + request.toString());
        }
        return subscriptionWSBLManager.fnFOperation(request);
    }

    @DELETE
    @Path("/fnfGroupMembers")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(
            value = "To add members to the specified FnF group",
            notes = "PUT operation with Response and @Default value",
            response = BodSubscriptionResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid Input Parameter with one of following reasons;<br/>" +
                    "1. Subscriber status is inactive<br/>" +
                    "2. Subscriber profile has been expired<br/>" +
                    "3. FnF group does not exist<br/>" +
                    "4. Invalid FnF member value<br/>" +
                    "4. Member not part of the group"),
            @ApiResponse(code = 401, message = "Missing mendatory parameters.<br/>" +
                    "1. Subscriber Id or Alternate Id<br/>" +
                    "2. Group name<br/>" +
                    "3. Members list is empty"),
            @ApiResponse(code = 404, message = "Subscriber Not Found"),
            @ApiResponse(code = 500, message = "Internal Error")
    })
    @Override
    public FnFOperationResponse removeFnFMembers(FnFOperationRequest request) {
        request.setOperation(FnFOperationRequest.Operation.REMOVE);
        request.setWebServiceName(WEB_SERVICE_NAME);
        request.setWebServiceMethodName(WS_FNF_GROUP_MEMBERS);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Request Parameters: " + request.toString());
        }
        return subscriptionWSBLManager.fnFOperation(request);
    }
}