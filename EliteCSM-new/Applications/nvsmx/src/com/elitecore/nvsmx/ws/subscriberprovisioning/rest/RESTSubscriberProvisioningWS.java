package com.elitecore.nvsmx.ws.subscriberprovisioning.rest;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.nvsmx.ws.subscriberprovisioning.ISubscriberProvisioningWS;
import com.elitecore.nvsmx.ws.subscriberprovisioning.SubscriberProvisioningWS;
import com.elitecore.nvsmx.ws.subscriberprovisioning.blmanager.SubscriberProfileWSBLManager;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfileData;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileBulkWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.AddSubscriberProfileWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeBaseProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.ChangeImsPackageWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.DeleteSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.GetSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.MigrateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.PurgeSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.RestoreSubscribersWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.SubscriberProvisioningWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateAlternateIdWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.UpdateSubscriberWSRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.AlternateIdProvisioningResponse;
import com.elitecore.nvsmx.ws.subscriberprovisioning.response.SubscriberProvisioningResponse;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.AddAlternateIdRestRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.ChangeAlternateIdStatusRestRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.ChangeBaseProductOfferRestRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.ChangeIMSPackageRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.DeleteSubscriberRestRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.PurgeSubscribersRestRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.RestoreSubscribersRestRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.SubscriberBulkAddRestRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.SubscriberUpdateRestRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.rest.request.UpdateAlternateIdRestRequest;
import com.elitecore.nvsmx.ws.subscription.response.ListProductOfferResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Path("/")
@Api(value="/restful/subscriberProvisioning")
public class RESTSubscriberProvisioningWS implements ISubscriberProvisioningWS {

    private static final String MODULE = "REST-SUBSCRIBER-PROVISION";
    private static final String WEB_SERVICE_NAME = SubscriberProvisioningWS.class.getSimpleName();
    public static final String WS_ADD_ALTERNATEID = "wsAddAlternateId";
	public static final String WS_UPDATE_ALTERNATEID = "wsUpdateAlternateId";
	public static final String WS_CHANGE_ALTERNATEID_STATUS = "wsChangeAlternateIdStatus";
	public static final String WS_DELETE_ALTERNATE_ID = "wsDeleteAlternateId";
	public static final String WS_GET_ALTERNATE_ID = "wsGetAlternateId";
    private SubscriberProfileWSBLManager subscriberProfileWSBLManager;

    public RESTSubscriberProvisioningWS() {
        this.subscriberProfileWSBLManager = new SubscriberProfileWSBLManager();
    }

    @GET
    @Path("/getSubscriber")
    @Override
    public SubscriberProvisioningResponse wsGetSubscriberProfileByID(@QueryParam("subscriberId") String subscriberId,
                                                                     @QueryParam("alternateId") String alternateId,
                                                                     @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called get SubscriberProfileByID with Request Parameters: "
                    + " Subscriber ID: " + subscriberId
                    + " Alternate ID: " + alternateId
                    + " Parameter1: " + parameter1
                    + ", Parameter2: " + parameter2);
        }
        return subscriberProfileWSBLManager.getSubscriberByID(new GetSubscriberWSRequest(subscriberId, alternateId, parameter1, parameter2, WEB_SERVICE_NAME, WS_GET_SUBSCRIBER_PROFILE_BY_ID));
    }


    @POST
    @Path("/addSubscriber")
    public SubscriberProvisioningResponse addSubscriber(SubscriberProfileData subscriberProfile, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        return wsAddSubscriber(subscriberProfile, parameter1, parameter2);
    }

    @Override
    @DELETE
    @Path("/deleteSubscriber")
    public SubscriberProvisioningResponse wsDeleteSubscriberProfile(@QueryParam("subscriberId") String subscriberID, @QueryParam("alternateId") String alternateId,
                                                                    @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        return subscriberProfileWSBLManager.deleteSubscriberProfile(new DeleteSubscriberWSRequest(subscriberID, alternateId, parameter1, parameter2, WEB_SERVICE_NAME, WS_DELETE_SUBSCRIBER_PROFILE));
    }


    @PUT
    @Path("/updateSubscriberProfile")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SubscriberProvisioningResponse updateSubscriberProfile(SubscriberUpdateRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {

        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to update subscriber profile. Reason: subscriber profile is NULL");
            return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Subscriber profile not received", null, null, null, WEB_SERVICE_NAME, WS_UPDATE_SUBSCRIBER_PROFILE);
        }
        String  updateActionString = request.getUpdateAction();
        Integer updateAction = convertUpdateAction(updateActionString);

        return wsUpdateSubscriberProfile(request.getSubscriberProfile(), request.getSubscriberId(), request.getAlternateId(), updateAction, parameter1, parameter2);
    }

    private Integer convertUpdateAction(String updateActionString) {
        if (StringUtils.isEmpty(updateActionString)) {
            return null;
        }

        try {
            return Integer.parseInt(StringUtils.trim(updateActionString));
        } catch (NumberFormatException e) {
            getLogger().error(MODULE, "Error while parsing update action "+updateActionString+". Reason: "+e.getMessage());
            getLogger().trace(MODULE, e);
        }
        return null;
    }

    @DELETE
    @Path("/deleteSubscribers")
    public SubscriberProvisioningResponse deleteSubscriberProfiles(DeleteSubscriberRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {

        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to delete subscriber profile. Reason: no request received");
            return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, WEB_SERVICE_NAME, WS_DELETE_SUBSCRIBER_PROFILES);
        }

        return wsDeleteSubscriberProfiles(request.getSubscriberIds(), request.getAlternateIds(), parameter1, parameter2);
    }

    @Override
    @DELETE
    @Path("/purgeSubscriber")
    public SubscriberProvisioningResponse wsPurgeSubscriber(@QueryParam("subscriberId") String subscriberID, @QueryParam("alternateId") String alternateId,
                                                            @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        return subscriberProfileWSBLManager.purgeSubscriber(new PurgeSubscriberWSRequest(subscriberID, alternateId, parameter1, parameter2, WEB_SERVICE_NAME, WS_PURGE_SUBSCRIBER));
    }

    @DELETE
    @Path("/purgeSubscribers")
    public SubscriberProvisioningResponse purgeSubscribers(PurgeSubscribersRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        if (Objects.isNull(request)) {
            LogManager.getLogger().error(MODULE, "Unable to purge Subscriber Profiles. Reason: Identity parameter missing");
            return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, WEB_SERVICE_NAME, WS_PURGE_SUBSCRIBERS);
        }
        return wsPurgeSubscribers(request.getSubscriberIds(), request.getAlternateIds(), parameter1, parameter2);
    }

    @Override
    @DELETE
    @Path("/purgeAllSubscribers")
    public SubscriberProvisioningResponse wsPurgeAllSubscriber(@QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsPurgeAllSubscriber with Request Parameters: "
                    + " Parameter1: " + parameter1
                    + ", Parameter2: " + parameter2);
        }
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        return subscriberProfileWSBLManager.purgeAllSubscribers(new SubscriberProvisioningWSRequest(parameter1, parameter2, WEB_SERVICE_NAME, WS_PURGE_ALL_SUBSCRIBER));
    }

    @Override
    @PUT
    @Path("/restoreSubscriber")
    public SubscriberProvisioningResponse wsRestoreSubscriber(@QueryParam("subscriberId") String subscriberId, @QueryParam("alternateId") String alternateId,
                                                              @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        return subscriberProfileWSBLManager.restoreSubscriber(new RestoreSubscriberWSRequest(subscriberId, alternateId, parameter1, parameter2, WEB_SERVICE_NAME, WS_RESTORE_SUBSCRIBER));
    }

    @PUT
    @Path("/restoreSubscribers")
    public SubscriberProvisioningResponse restoreSubscribers(RestoreSubscribersRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {

        if (Objects.isNull(request)) {
            LogManager.getLogger().error(MODULE, "Unable to restore Subscriber Profiles. Reason: Identity parameter missing");
            return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, WEB_SERVICE_NAME, WS_RESTORE_SUBSCRIBERS);
        }

        return wsRestoreSubscribers(request.getSubscriberIds(), request.getAlternateIds(), parameter1, parameter2);
    }

    @Override
    @PUT
    @Path("/migrateSubscriber")
    public SubscriberProvisioningResponse wsMigrateSubscriber(@QueryParam("currentSubscriberIdentity") String currentSubscriberIdentity, @QueryParam("newSubscriberIdentity") String newSubscriberIdentity,
                                                              @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        return subscriberProfileWSBLManager.migrateSubscriber(new MigrateSubscriberWSRequest(currentSubscriberIdentity, newSubscriberIdentity, parameter1, parameter2, WEB_SERVICE_NAME, WS_MIGRATE_SUBSCRIBER));
    }

    @POST
    @Path("/addSubscribers")
    public SubscriberProvisioningResponse addSubscribers(SubscriberBulkAddRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {

        if (Objects.isNull(request)) {
            LogManager.getLogger().error(MODULE, "Unable to add Subscriber Profiles. Reason: subscriber profiles is NULL");
            return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Subscriber profiles not received", null, null, null, WEB_SERVICE_NAME, WS_ADD_SUBSCRIBERS);
        }

        return wsAddSubscribers(request.getSubscriberProfile(), parameter1, parameter2);
    }

    @PUT
    @Path("/changeImsPackage")
    public SubscriberProvisioningResponse changeImsPackage(ChangeIMSPackageRequest request,
                                                           @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2, @QueryParam("parameter3") String parameter3) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to change ims package. Reason: no request parameter received");
            return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received"
                    , null, null, null, WEB_SERVICE_NAME, WS_CHANGE_IMS_PACKAGE);

        }
        Integer updateAction = convertUpdateAction(request.getUpdateAction());

        return wsChangeImsPackage(request.getSubscriberID(), request.getAlternateID(), request.getCurrentPackageName(), request.getNewPackageName(),
                request.getPackageType(), updateAction, parameter1, parameter2, parameter3);
    }

    @PUT
    @Path("/changeBaseProductOffer")
    @ApiOperation(
            value = "To change subscriber's base product offer",
            notes = "POST operation with compulsory request parameters: <br/>"+
                    "1. Subscriber Id or Alternate Id<br/>"+
                    "2. New Product Offer name<br/>",
            response = SubscriberProvisioningResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code=  400, message = "Invalid Input Parameters received from one of the following <br/>" +
                    "1.Invalid Base product offer policy status <br/>"+
                    "2.Invalid Base ProductOffer Status <br/>"),
            @ApiResponse(code = 401, message = "Input parameters are missing from one of the following <br/>"+
                    "1. Subscriber Id or Alternate Id"+
                    "2. New Product Offer Name"),
            @ApiResponse(code = 404, message = "Not Found from one of the following <br/>"+
                    "1. Subscriber not found from repository"+
                    "2. Subscriber base product offer can not set to empty"),
            @ApiResponse(code = 412, message = "PRECONDITION FAILED for following <br/>"+
                    "1. Subscriber's current product offer and current product offer parameter are not same<br/>" +
                    "2. New Base product offer found with different currency"),
            @ApiResponse(code = 500, message = "INTERNAL ERROR")
    })
    public SubscriberProvisioningResponse changeBaseProductOffer(ChangeBaseProductOfferRestRequest request,
                                                                 @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2, @QueryParam("parameter3") String parameter3) {
        if (Objects.isNull(request)) {
            getLogger().error(MODULE, "Unable to change base product offer . Reason: no request parameter received");
            return new SubscriberProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received"
                    , null, null, null, WEB_SERVICE_NAME, WS_CHANGE_BASE_PRODUCT_OFFER);
        }
        Integer updateAction = convertUpdateAction(request.getUpdateAction());
        return wsChangeBaseProductOffer(request.getSubscriberId(), request.getAlternateId(), request.getCurrentProductOfferName(),
                request.getNewProductOfferName(), updateAction, parameter1, parameter2, parameter3);
    }



    //FIXME provide WEB SERVICE NAME
    @POST
    @Path("/addAlternateId")
    public AlternateIdProvisioningResponse addAlternateId(AddAlternateIdRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2) {
        if (Objects.isNull(request)){
            getLogger().error(MODULE, "Unable to add Alternate Identity. Reason: no request parameter received");
            return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received"
                    , null, null, null,null, WEB_SERVICE_NAME, WS_CHANGE_BASE_PRODUCT_OFFER);
        }

        String subscriberId = StringUtils.trim(request.getSubscriberId());
        String alternateId = StringUtils.trim(request.getAlternateId());
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Calling add alternate Id with parameters: subscriber id "+subscriberId+" and alternate Id "+alternateId);
        }

        return subscriberProfileWSBLManager.addAlternateId(new AddAlternateIdWSRequest(subscriberId,alternateId,parameter1,parameter2,WEB_SERVICE_NAME,WS_ADD_ALTERNATEID));
    }


    @PUT
    @Path("/updateAlternateId")
    public AlternateIdProvisioningResponse updateAlternateId(UpdateAlternateIdRestRequest request,@QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2){
        if (Objects.isNull(request)){
            getLogger().error(MODULE, "Unable to update alternate identity. Reason: no request parameter received");
            return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received"
                    , null, null, null,null, WEB_SERVICE_NAME, WS_CHANGE_BASE_PRODUCT_OFFER);
        }
        String subscriberId =StringUtils.trim(request.getSubscriberId());
        String currentAlternateId = StringUtils.trim(request.getCurrentAlternateId());
        String newAlternateId = StringUtils.trim(request.getNewAlternateId());
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Calling update alternate id with parameters: subscriber Id "+subscriberId+" current alternate Id "+currentAlternateId+
                                  " new alternate Id "+newAlternateId);
        }
        return subscriberProfileWSBLManager.updateAlternateId(new UpdateAlternateIdWSRequest(subscriberId,currentAlternateId,
                                                                  newAlternateId, null,parameter1,parameter2,WEB_SERVICE_NAME,WS_UPDATE_ALTERNATEID));
    }

    @PUT
    @Path("/changeAlternateIdStatus")
    public AlternateIdProvisioningResponse changeAlternateIdStatus(ChangeAlternateIdStatusRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2){
        if (Objects.isNull(request)){
            getLogger().error(MODULE, "Unable to change alternate identity status. Reason: no request parameter received");
            return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received"
                    , null, null, null,null, WEB_SERVICE_NAME, WS_CHANGE_BASE_PRODUCT_OFFER);
        }
        String subscriberId = StringUtils.trim(request.getSubscriberId());
        String alternateId = StringUtils.trim(request.getAlternateId());
        String status = StringUtils.trim(request.getStatus());
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Calling change alternate id status with parameters: subscriber Id "+subscriberId+" alternate Id "+alternateId);
        }

        return subscriberProfileWSBLManager.changeAlternateIdStatus(new UpdateAlternateIdWSRequest(subscriberId,alternateId,null,status,parameter1,parameter2,WEB_SERVICE_NAME,WS_CHANGE_ALTERNATEID_STATUS));
    }

    @DELETE
    @Path("/deleteAlternateId")
    public AlternateIdProvisioningResponse removeAlternateId(AddAlternateIdRestRequest request, @QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2){
        if (Objects.isNull(request)){
            getLogger().error(MODULE, "Unable to add Alternate Identity. Reason: no request parameter received");
            return new AlternateIdProvisioningResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
                    ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: no request parameter received"
                    , null, null, null,null, WEB_SERVICE_NAME, WS_CHANGE_BASE_PRODUCT_OFFER);
        }
        String subscriberId = StringUtils.trim(request.getSubscriberId());
        String alternateId = StringUtils.trim(request.getAlternateId());
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Calling remove alternate id with parameters: subscriber Id "+subscriberId+" alternate Id: "+alternateId);
        }
        return subscriberProfileWSBLManager.deleteAlternateId(new AddAlternateIdWSRequest(subscriberId,alternateId,parameter1,parameter2,WEB_SERVICE_NAME,WS_DELETE_ALTERNATE_ID));
    }


    @GET
    @Path("/getAlternateId")
    public AlternateIdProvisioningResponse getAlternateId(@QueryParam("subscriberId")String subscriberId,@QueryParam("parameter1") String parameter1, @QueryParam("parameter2") String parameter2){
        String subscriberIdentity = StringUtils.trim(subscriberId);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Calling get alternate ids for subscriber: "+subscriberIdentity);
        }
        return subscriberProfileWSBLManager.getAlternateIds(new GetAlternateIdWSRequest(subscriberIdentity,parameter1,parameter2,WEB_SERVICE_NAME,WS_GET_ALTERNATE_ID));
    }


    @Override
    public SubscriberProvisioningResponse wsAddSubscriber(SubscriberProfileData subscriberProfile, String parameter1, String parameter2) {
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called addSubscriber with Request Parameters: Subscriber Profile: " + subscriberProfile
                    + ", Parameter1: " + parameter1 +
                    ", Parameter2: " + parameter2);
        }
        return subscriberProfileWSBLManager.addSubscriber(new AddSubscriberProfileWSRequest(subscriberProfile, parameter1, parameter2, WEB_SERVICE_NAME, WS_ADD_SUBSCRIBER));
    }


    @Override
    public SubscriberProvisioningResponse wsUpdateSubscriberProfile(SubscriberProfile subscriberProfile, String subscriberId, String alternateId, Integer updateAction, String parameter1, String parameter2) {
        subscriberId = StringUtil.trimParameter(subscriberId);
        alternateId = StringUtil.trimParameter(alternateId);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called updateSubscriber with Request Parameters: Subscriber Profile: " + subscriberProfile
                    + " Subscriber ID: " + subscriberId
                    + ", Alternate ID: " + alternateId
                    + ", Update Action: " + updateAction
                    + ", Parameter1: " + parameter1
                    + ", Parameter2: " + parameter2);
        }
        return subscriberProfileWSBLManager.updateSubscriberProfile(new UpdateSubscriberWSRequest(subscriberProfile, subscriberId, alternateId, updateAction, parameter1, parameter2, WEB_SERVICE_NAME, WS_UPDATE_SUBSCRIBER_PROFILE));
    }


    @Override
    public SubscriberProvisioningResponse wsDeleteSubscriberProfiles(List<String> subscriberIDs, List<String> alternateIds, String parameter1, String parameter2) {
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsDeleteSubscriberProfiles with Request Parameters: "
                    + " Subscriber Identities: " + subscriberIDs
                    + " Alternate Ids: " + alternateIds
                    + " Parameter1: " + parameter1
                    + ", Parameter2: " + parameter2);
        }
        return subscriberProfileWSBLManager.deleteSubscriberProfiles(new DeleteSubscribersWSRequest(subscriberIDs, alternateIds, parameter1, parameter2, WEB_SERVICE_NAME, WS_DELETE_SUBSCRIBER_PROFILES));
    }


    @Override
    public SubscriberProvisioningResponse wsPurgeSubscribers(List<String> subscriberIDs, List<String> alternateIds, String parameter1, String parameter2) {
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsPurgeSubscribers with Request Parameters: "
                    + " Subscriber Identities: " + subscriberIDs
                    + " Alternate Ids: " + alternateIds
                    + " Parameter1: " + parameter1
                    + ", Parameter2: " + parameter2);
        }
        return subscriberProfileWSBLManager.purgeSubscribers(new PurgeSubscribersWSRequest(subscriberIDs, alternateIds, parameter1, parameter2, WEB_SERVICE_NAME, WS_PURGE_SUBSCRIBERS));
    }


    @Override
    public SubscriberProvisioningResponse wsRestoreSubscribers(List<String> subscriberIdentities, List<String> alternateIds, String parameter1, String parameter2) {
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsRestoreSubscribers with Request Parameters: "
                    + " Subscriber Identities: " + subscriberIdentities
                    + " Alternate Ids: " + alternateIds
                    + " Parameter1: " + parameter1
                    + ", Parameter2: " + parameter2);
        }
        return subscriberProfileWSBLManager.restoreSubscribers(new RestoreSubscribersWSRequest(subscriberIdentities, alternateIds, parameter1, parameter2, WEB_SERVICE_NAME, WS_RESTORE_SUBSCRIBERS));
    }

    @Override
    public SubscriberProvisioningResponse wsAddSubscribers(List<SubscriberProfileData> subscriberProfile, String parameter1, String parameter2) {
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        if (getLogger().isDebugLogLevel()) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Total Subscriber Profiles: ");
            int noOfSubscriberProfiles = 0;
            if (subscriberProfile != null) {
                noOfSubscriberProfiles = subscriberProfile.size();
            }
            stringBuilder.append(noOfSubscriberProfiles);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);

            getLogger().debug(MODULE, "Called wsAddSubscribers with Request Parameters: " + stringBuilder.toString());
        }
        return subscriberProfileWSBLManager.addSubscribers(new AddSubscriberProfileBulkWSRequest(subscriberProfile, parameter1, parameter2, WEB_SERVICE_NAME, WS_ADD_SUBSCRIBERS));
    }

    @Override
    public SubscriberProvisioningResponse wsChangeImsPackage(String subscriberID, String alternateID, String currentPackageName, String newPackageName, String packageType, Integer updateAction, String parameter1, String parameter2, String parameter3) {
        subscriberID = StringUtil.trimParameter(subscriberID);
        alternateID = StringUtil.trimParameter(alternateID);
        currentPackageName = StringUtil.trimParameter(currentPackageName);
        newPackageName = StringUtil.trimParameter(newPackageName);
        packageType = StringUtil.trimParameter(packageType);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        parameter3 = StringUtil.trimParameter(parameter3);
        if (getLogger().isDebugLogLevel()) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Subscriber Identity: ");
            stringBuilder.append(subscriberID);
            stringBuilder.append(", Alternate Identity: ");
            stringBuilder.append(alternateID);
            stringBuilder.append(", Current Package Name: ");
            stringBuilder.append(currentPackageName);
            stringBuilder.append(", New Package Name: ");
            stringBuilder.append(newPackageName);
            stringBuilder.append(", Package Type: ");
            stringBuilder.append(packageType);
            stringBuilder.append(", Update Action: ");
            stringBuilder.append(updateAction);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            stringBuilder.append(", Parameter3: ");
            stringBuilder.append(parameter3);
            getLogger().debug(MODULE, "Called changeIMSPackage with Request Parameters: " + stringBuilder.toString());
        }

        return subscriberProfileWSBLManager.changeImsPackage(new ChangeImsPackageWSRequest(currentPackageName, newPackageName, packageType, subscriberID, alternateID, updateAction, WEB_SERVICE_NAME, WS_CHANGE_IMS_PACKAGE, parameter1, parameter2, parameter3));
    }


    @Override
    public SubscriberProvisioningResponse wsChangeBaseProductOffer(String subscriberID, String alternateID, String currentProductOfferName, String newProductOfferName, Integer updateAction, String parameter1, String parameter2, String parameter3) {
        subscriberID = StringUtil.trimParameter(subscriberID);
        alternateID = StringUtil.trimParameter(alternateID);
        currentProductOfferName = StringUtil.trimParameter(currentProductOfferName);
        newProductOfferName = StringUtil.trimParameter(newProductOfferName);
        parameter1 = StringUtil.trimParameter(parameter1);
        parameter2 = StringUtil.trimParameter(parameter2);
        parameter3 = StringUtil.trimParameter(parameter3);

        if (getLogger().isDebugLogLevel()) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Subscriber Identity: ");
            stringBuilder.append(subscriberID);
            stringBuilder.append(", Alternate Identity: ");
            stringBuilder.append(alternateID);
            stringBuilder.append(", Current Product Offer: ");
            stringBuilder.append(currentProductOfferName);
            stringBuilder.append(", New Product Offer: ");
            stringBuilder.append(newProductOfferName);
            stringBuilder.append(", Update Action: ");
            stringBuilder.append(updateAction);
            stringBuilder.append(", Parameter1: ");
            stringBuilder.append(parameter1);
            stringBuilder.append(", Parameter2: ");
            stringBuilder.append(parameter2);
            stringBuilder.append(", Parameter3: ");
            stringBuilder.append(parameter3);

            getLogger().debug(MODULE, "Called wsChangePackage with Request Parameters: " + stringBuilder.toString());
        }

        return subscriberProfileWSBLManager.changeBaseProductOffer(new ChangeBaseProductOfferWSRequest(currentProductOfferName, newProductOfferName,
                subscriberID, alternateID, updateAction, WEB_SERVICE_NAME, WS_CHANGE_BASE_PRODUCT_OFFER, parameter1, parameter2, parameter3));
    }

    //TODO remove these deprecated methods once SOAP removed
    @Override
    public SubscriberProvisioningResponse wsAddSubscriberProfile(SubscriberProfile subscriberProfile, String parameter1, String parameter2) {
        return null;
    }


    //TODO REMOVE Deprecated method once SOAP removed
    @Override
    public SubscriberProvisioningResponse wsAddSubscriberBulk(List<SubscriberProfile> subscriberProfile, String parameter1, String parameter2) {
        return null;
    }

    //TODO remove unsupported operations once SOAP removed
    @Override
    public SubscriberProvisioningResponse wsListSubscriberProfiles(Map<String, String> subscriberProfileCriteria, String parameter1, String parameter2) {
        return null;
    }

}
