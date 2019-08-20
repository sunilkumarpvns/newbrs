package com.elitecore.nvsmx.ws.subscription;

import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.DoubleToStringAdapter;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.StringToIntegerAdapter;
import com.elitecore.nvsmx.ws.subscription.blmanager.PackageWSBlManager;
import com.elitecore.nvsmx.ws.subscription.blmanager.SubscriptionWSBLManager;
import com.elitecore.nvsmx.ws.subscription.request.BalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.BoDQueryRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeBillDayWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeDataAddOnSubscriptionWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeRnCAddOnProductOfferWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ChangeTopUpSubscriptionWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListPackageRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListPackagesRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListSubscriptionWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListUsageMonitoringInformationWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.MonetaryBalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.MonetaryOperationRequest;
import com.elitecore.nvsmx.ws.subscription.request.NonMonitoryBalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.RnCBalanceEnquiryRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeAddOnWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.SubscribeTopUpWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.TopUpQueryRequest;
import com.elitecore.nvsmx.ws.subscription.request.UpdateCreditLimitWSRequest;
import com.elitecore.nvsmx.ws.subscription.request.UpdateMonetaryBalanceRequest;
import com.elitecore.nvsmx.ws.subscription.response.BalanceEnquiryResponse;
import com.elitecore.nvsmx.ws.subscription.response.BoDQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.ChangeBillDayResponse;
import com.elitecore.nvsmx.ws.subscription.response.ListPackagesResponse;
import com.elitecore.nvsmx.ws.subscription.response.MonetaryBalanceInquiryResponse;
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
import com.elitecore.nvsmx.ws.util.LongToStringAdapter;
import com.elitecore.nvsmx.ws.util.StringToIntegerAdapterForReauth;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Jay Trivedi
 *
 */
 
public class SubscriptionWS implements ISubscriptionWS, IUsageMonitoringWS {

	private static final String WEBSERVICE_NAME = SubscriptionWS.class.getSimpleName();
	private static final String MODULE = "SUBSCRIPTION-WS";
	private SubscriptionWSBLManager subscriptionWSBLManager;
	private PackageWSBlManager packageWSBlManager;


	public SubscriptionWS() {
		subscriptionWSBLManager = new SubscriptionWSBLManager();
		packageWSBlManager = new PackageWSBlManager();
	}

	@Override
	@WebMethod(operationName = WS_LIST_PACKAGES)
	public ListPackagesResponse wsListPackages(@WebParam(name = "packageId")String packageId,
											   @WebParam(name = "packageName")String packageName,
											   @WebParam(name = "packageType")String packageType,
											   @WebParam(name = "type")String type,
                                               @WebParam(name = "mode")String mode,
											   @WebParam(name = "group")String group,
											   @WebParam(name="currency")String currency,
											   @WebParam(name = "parameter1") String parameter1,
											   @WebParam(name = "parameter2") String parameter2) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called listPackages");
		}
		packageId=StringUtil.trimParameter(packageId);
		packageName=StringUtil.trimParameter(packageName);
		packageType=StringUtil.trimParameter(packageType);
		type=StringUtil.trimParameter(type);
        mode=StringUtil.trimParameter(mode);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		group=StringUtil.trimParameter(group);
		currency=StringUtil.trimParameter(currency);


		if (getLogger().isDebugLogLevel()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(", Package Id: ").append(packageId);
			stringBuilder.append(", Package Name: ").append(packageName);
			stringBuilder.append(", Package Type: ").append(packageType);
			stringBuilder.append(", Type: ").append(type);
            stringBuilder.append(", mode: ").append(mode);
			stringBuilder.append(", Group: ").append(group);
			stringBuilder.append(", Currency: ").append(currency);
			stringBuilder.append(", Parameter1: ").append(parameter1);
			stringBuilder.append(", Parameter2: ").append(parameter2);
			getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
		}
		return packageWSBlManager.getListPackages(new ListPackagesRequest(packageId,packageName,packageType,type,mode,group,currency,parameter1,parameter2, WEBSERVICE_NAME, WS_LIST_PACKAGES));
	}

	@Override
	@WebMethod(operationName = WS_LIST_DATA_PACKAGES)
	public PackageQueryResponse wsListDataPackages(
			@WebParam(name="packageId")String packageId,
			@WebParam(name="packageName")String packageName,
			@WebParam(name="packageType")String packageType,
			@WebParam(name="currency")String currency,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsListPackages");
		}
		packageId=StringUtil.trimParameter(packageId);
		packageName=StringUtil.trimParameter(packageName);
		packageType=StringUtil.trimParameter(packageType);
		currency=StringUtil.trimParameter(currency);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);


		if (getLogger().isDebugLogLevel()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(", Pacakge Id: ").append(packageId);
			stringBuilder.append(", Package Name: ").append(packageName);
			stringBuilder.append(", Package Type: ").append(packageType);
			stringBuilder.append(", Currency: ").append(currency);
			stringBuilder.append(", Parameter1: ").append(parameter1);
			stringBuilder.append(", Parameter2: ").append(parameter2);
			getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
		}
		return packageWSBlManager.getListDataPackages(new ListPackageRequest(packageId,packageName,packageType,currency,parameter1,parameter2, WEBSERVICE_NAME, WS_LIST_DATA_PACKAGES));
	}

	@Override
	@WebMethod(operationName = WS_LIST_ADD_ON_SUBSCRIPTIONS)
	public SubscriptionResponse wsListAddOnSubscriptions(@WebParam(name = "subscriberId") String subscriberId,
			@WebParam(name = "alternateId") String alternateId,
			@WebParam(name = "subscriptionStatusValue") String subscriptionStatusValue,
			@WebParam(name = "subscriptionStatusName") String subscriptionStatusName, @WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {

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

		return subscriptionWSBLManager.getAddOnSubscriptions(new ListSubscriptionWSRequest(subscriberId, alternateId, subscriptionStatusValue, subscriptionStatusName, parameter1, parameter2, WEBSERVICE_NAME, WS_LIST_ADD_ON_SUBSCRIPTIONS));
	}

	@Override
	@WebMethod(operationName = WS_LIST_TOP_UP_SUBSCRIPTIONS)
	public TopUpSubscriptionResponse wsListTopUpSubscriptions(@WebParam(name = "subscriberId") String subscriberId,
															  @WebParam(name = "alternateId") String alternateId,
															  @WebParam(name = "subscriptionStatusValue") String subscriptionStatusValue,
															  @WebParam(name = "subscriptionStatusName") String subscriptionStatusName,
															  @WebParam(name = "parameter1") String parameter1,
															  @WebParam(name = "parameter2") String parameter2) {
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
			getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
		}

		return subscriptionWSBLManager.getTopUpSubscriptions(new ListSubscriptionWSRequest(subscriberId, alternateId, subscriptionStatusValue, subscriptionStatusName, parameter1, parameter2, WEBSERVICE_NAME, WS_LIST_TOP_UP_SUBSCRIPTIONS));

	}

	@Override
	@WebMethod(operationName = WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION)
	public SubscriptionResponse wsChangeDataAddOnSubscription(@WebParam(name="subscriberId")String subscriberId,
															  @WebParam(name="alternateId")String alternateId,
															  @WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
															  @WebParam(name = "dataAddOnSubscriptionId") String dataAddOnSubscriptionId,
															  @WebParam(name = "subscriptionStatusValue") String subscriptionStatusValue,
															  @WebParam(name = "subscriptionStatusName") String subscriptionStatusName,
															  @WebParam(name = "dataAddOnProductOfferName") String dataAddOnProductOfferName,
															  @WebParam(name = "subscriptionOrder") String subscriptionOrder,
															  @WebParam(name = "startTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long startTime,
															  @WebParam(name = "endTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long endTime,
															  @WebParam(name = "priority") @XmlJavaTypeAdapter(StringToIntegerAdapter.class) Integer priority,
															  @WebParam(name = "rejectReason") String rejectReason,
															  @WebParam(name = "parameter1") String parameter1,
															  @WebParam(name = "parameter2") String parameter2) {

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
			stringBuilder.append(", Data AddOn Product Offer Id: ");
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

		return subscriptionWSBLManager.changeDataAddOnSubscription(new ChangeDataAddOnSubscriptionWSRequest(subscriberId, alternateId, updateAction, dataAddOnSubscriptionId,
				subscriptionStatusValue, subscriptionStatusName, dataAddOnProductOfferName, subscriptionOrder, startTime, endTime, priority, rejectReason, parameter1, parameter2, WEBSERVICE_NAME, WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION));
	}

	@Override
	@WebMethod(operationName = WS_SUBSCRIBE_ADD_ON)
	public SubscriptionResponse wsSubscribeAddOnProductOffer(
			@WebParam(name="parentId")String parentId , 
			@WebParam(name="subscriberId")String subscriberId ,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
			@WebParam(name="cui")String cui ,
			@WebParam(name="addOnProductOfferId")String addOnProductOfferId,
			@WebParam(name="addOnProductOfferName")String addOnProductOfferName,
			@WebParam(name="subscriptionStatusValue")String subscriptionStatusValue ,
			@WebParam(name="subscriptionStatusName")String subscriptionStatusName ,
			@WebParam(name="startTime")@XmlJavaTypeAdapter(LongToStringAdapter.class) Long startTime ,
			@WebParam(name="endTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long endTime ,
			@WebParam(name="priority") @XmlJavaTypeAdapter(StringToIntegerAdapter.class) Integer priority ,
			@WebParam(name="fnFGroupName")String fnFGroupName ,
			@WebParam(name="fnFMembers") List<String> fnFMembers ,
			@WebParam(name="updateBalanceIndication")boolean updateBalanceIndication,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2) {

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
				alternateId, updateAction , cui, addOnProductOfferId, addOnProductOfferName, subscriptionStatusValue,
				subscriptionStatusName, startTime, endTime, priority, fnFGroupName,fnFMembers, updateBalanceIndication, parameter1,
				parameter2, WEBSERVICE_NAME, WS_SUBSCRIBE_ADD_ON);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Request Parameters: " + subscribeAddOnWSRequest.toString());
		}

		return subscriptionWSBLManager.subscribeAddOnProductOffer(subscribeAddOnWSRequest);
	}

	@Override
	@WebMethod(operationName = WS_LIST_USAGE_MONITORING_INFORMATION)
	public UMQueryResponse wsListUsageMonitoringInformation(@WebParam(name = "subscriberId") String subscriberId,
			@WebParam(name = "alternateId") String alternateId, @WebParam(name = "packageId") String packageId,
			@WebParam(name = "packageName") String packageName, @WebParam(name = "quotaProfileId") String quotaProfileId,
			@WebParam(name = "quotaProfileName") String quotaProfileName, @WebParam(name = "serviceId") String serviceId,
			@WebParam(name = "serviceName") String serviceName, @WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {

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
		return subscriptionWSBLManager.getUsageMonitoringInformation(new ListUsageMonitoringInformationWSRequest(subscriberId, alternateId, packageId, packageName, quotaProfileId, quotaProfileName, serviceId, serviceName, parameter1, parameter2, WEBSERVICE_NAME, WS_LIST_USAGE_MONITORING_INFORMATION));
	}

	@Override
	@WebMethod(operationName = WS_GET_BALANCE)
	public BalanceEnquiryResponse wsGetBalance(
			@WebParam(name="subscriberId")String subscriberId ,
			@WebParam(name="alternateId")String alternateId, 
			@WebParam(name = "packageId") String packageId,
			@WebParam(name = "packageName") String packageName,
			@WebParam(name="subscriptionId")String subscriptionId,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsGetBalance");
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
		
		return subscriptionWSBLManager.getBalance(new BalanceEnquiryRequest(subscriberId, alternateId, packageId, packageName, subscriptionId, parameter1, parameter2, WEBSERVICE_NAME, WS_GET_BALANCE));
	}

	@Override
	@WebMethod(operationName = WS_GET_NONMONITORY_BALANCE)
	public NonMonitoryBalanceInquiryResponse wsGetNonMonitoryBalance(
			@WebParam(name="subscriberId")String subscriberId ,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name = "packageId") String packageId,
			@WebParam(name = "packageName") String packageName,
			@WebParam(name="subscriptionId")String subscriptionId,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {

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
		return subscriptionWSBLManager.getNonMonitoryBalance(new NonMonitoryBalanceEnquiryRequest(subscriberId, alternateId, packageId, packageName, subscriptionId, parameter1, parameter2, WEBSERVICE_NAME, WS_GET_NONMONITORY_BALANCE));
	}

	//get rnc package balance
	@Override
	@WebMethod(operationName = WS_GET_RNC_BALANCE)
	public RnCBalanceEnquiryResponse wsGetRnCBalance(
			@WebParam(name="subscriberId")String subscriberId ,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name = "packageId") String packageId,
			@WebParam(name = "packageName") String packageName,
			@WebParam(name="subscriptionId")String subscriptionId,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {

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
		return subscriptionWSBLManager.getRnCBalance(new RnCBalanceEnquiryRequest(subscriberId, alternateId, packageId, packageName, subscriptionId, parameter1, parameter2, WEBSERVICE_NAME, WS_GET_RNC_BALANCE));
	}

	@Override
	@WebMethod(operationName = WS_GET_MONETARY_BALANCE)
	public MonetaryBalanceInquiryResponse wsGetMonetaryBalance(
			@WebParam(name="subscriberId")String subscriberId ,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name = "serviceId")String serviceId,
			@WebParam(name = "serviceName")String serviceName,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {

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
		return subscriptionWSBLManager.getMonetaryBalance(new MonetaryBalanceEnquiryRequest(subscriberId, alternateId, serviceId, serviceName, parameter1, parameter2, WEBSERVICE_NAME, WS_GET_MONETARY_BALANCE));
	}

	@Override
	@WebMethod(operationName = WS_ADD_MONETARY_BALANCE)
	public MonetoryOperationResponse wsAddMonetaryBalance(
			@WebParam(name = "subscriberId")String subscriberId,
			@WebParam(name = "alternateId")String alternateId,
			@WebParam(name = "serviceId")String serviceId,
			@WebParam(name = "serviceName")String serviceName,
			@WebParam(name = "totalBalance")@XmlJavaTypeAdapter(DoubleToStringAdapter.class) Double totalBalance,
			@WebParam(name = "validFromDate")@XmlJavaTypeAdapter(LongToStringAdapter.class) Long validFromDate,
			@WebParam(name = "validToDate")@XmlJavaTypeAdapter(LongToStringAdapter.class) Long validToDate,
			@WebParam(name="transactionId")String transactionId,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2) {

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
				alternateId, serviceId, serviceName, totalBalance, validFromDate, validToDate,transactionId, parameter1,
				parameter2, WEBSERVICE_NAME, WS_ADD_MONETARY_BALANCE);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Request Parameters: " + monetaryOperationRequest.toString());
		}
		return subscriptionWSBLManager.addMonetaryBalance(monetaryOperationRequest);
	}

	@Override
	@WebMethod(operationName = WS_UPDATE_MONETARY_BALANCE)
	public UpdateMonetaryBalanceResponse wsUpdateMonetaryBalance(
			@WebParam(name = "subscriberId")String subscriberId,
			@WebParam(name = "alternateId")String alternateId,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
			@WebParam(name="balanceId")String balanceId,
			@WebParam(name = "serviceId")String serviceId,
			@WebParam(name = "serviceName")String serviceName,
			@WebParam(name = "amount")@XmlJavaTypeAdapter(DoubleToStringAdapter.class) Double amount,
			@WebParam(name = "operation")String operation,
			@WebParam(name = "validToDate")@XmlJavaTypeAdapter(LongToStringAdapter.class) Long validToDate,
			@WebParam(name = "reason")String reason,
			@WebParam(name = "transactionId")String transactionId,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsUpdateMonetaryBalance");
		}

		subscriberId = StringUtil.trimParameter(subscriberId);
		alternateId = StringUtil.trimParameter(alternateId);
		serviceId = StringUtil.trimParameter(serviceId);
		serviceName = StringUtil.trimParameter(serviceName);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);

		UpdateMonetaryBalanceRequest updateMonetaryBalanceRequest = new UpdateMonetaryBalanceRequest(subscriberId,
				alternateId, updateAction, balanceId, serviceId, serviceName, amount, operation, validToDate, reason, transactionId, parameter1, parameter2, WEBSERVICE_NAME, WS_UPDATE_MONETARY_BALANCE);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Request Parameters: " + updateMonetaryBalanceRequest.toString());
		}
		return subscriptionWSBLManager.updateMonetaryBalance(updateMonetaryBalanceRequest);
	}

	@Override
	@WebMethod(operationName = WS_SUBSCRIBE_TOP_UP)
	public TopUpSubscriptionResponse wsSubscribeTopUp(
			@WebParam(name="parentId")String parentId ,
			@WebParam(name="subscriberId")String subscriberId ,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
			@WebParam(name="cui")String cui ,
			@WebParam(name="topUpPackageId")String topUpPackageId,
			@WebParam(name="topUpPackageName")String topUpPackageName,
			@WebParam(name="subscriptionStatusValue")String subscriptionStatusValue ,
			@WebParam(name="subscriptionStatusName")String subscriptionStatusName ,
			@WebParam(name="startTime")@XmlJavaTypeAdapter(LongToStringAdapter.class) Long startTime ,
			@WebParam(name="endTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long endTime ,
			@WebParam(name="priority") @XmlJavaTypeAdapter(StringToIntegerAdapter.class) Integer priority ,
			@WebParam(name="updateBalanceIndication")boolean updateBalanceIndication,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2) {

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
			stringBuilder.append(", UpdateBalanceIndication: ");
			stringBuilder.append(priority);
			stringBuilder.append(", Parameter1: ");
			stringBuilder.append(parameter1);
			stringBuilder.append(", Parameter2: ");
			stringBuilder.append(parameter2);
			getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
		}

		return subscriptionWSBLManager.subscribeTopUp(new SubscribeTopUpWSRequest(parentId, subscriberId, alternateId, updateAction
				, cui, topUpPackageId, topUpPackageName, subscriptionStatusValue, subscriptionStatusName, startTime, endTime, priority,updateBalanceIndication, parameter1, parameter2, WEBSERVICE_NAME, WS_SUBSCRIBE_TOP_UP));
	}

	@Override
	@WebMethod(operationName = WS_LIST_TOP_UP_PACKAGES)
	public TopUpQueryResponse wsListTopUpPackages(@WebParam(name="subscriberId")String subscriberId,
												  @WebParam(name="alternateId")String alternateId,
												  @WebParam(name="productOfferName")String productOfferName,
												  @WebParam(name="parameter1")String parameter1,
												  @WebParam(name="parameter2")String parameter2) {
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
		return subscriptionWSBLManager.getTopUpPackages(new TopUpQueryRequest(subscriberId, alternateId, productOfferName, parameter1, parameter2, WEBSERVICE_NAME, WS_LIST_TOP_UP_PACKAGES));
	}

	@Override
	@WebMethod(operationName = WS_LIST_BOD_PACKAGES)
	public BoDQueryResponse wsListBoDPackages(@WebParam(name="subscriberId")String subscriberId,
											  @WebParam(name="alternateId")String alternateId,
											  @WebParam(name="productOfferName")String productOfferName,
											  @WebParam(name="parameter1")String parameter1,
											  @WebParam(name="parameter2")String parameter2) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsListBoDPackages");
		}

		BoDQueryRequest request = new BoDQueryRequest(StringUtil.trimParameter(subscriberId),
				StringUtil.trimParameter(alternateId),
				StringUtil.trimParameter(productOfferName),
				StringUtil.trimParameter(parameter1),
				StringUtil.trimParameter(parameter2),
				WEBSERVICE_NAME, WS_LIST_BOD_PACKAGES);


		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Request Parameters: " + request.toString());
		}
		return subscriptionWSBLManager.getBoDPackages(request);
	}

	@Override
	@WebMethod(operationName = WS_CHANGE_TOP_UP_SUBSCRIPTION)
	public TopUpSubscriptionResponse wsChangeTopUpSubscription(@WebParam(name="subscriberId")String subscriberId,
															   @WebParam(name="alternateId")String alternateId,
															   @WebParam(name="updateAction") @XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
															   @WebParam(name = "topUpSubscriptionId") String topUpSubscriptionId,
															   @WebParam(name = "subscriptionStatusValue") String subscriptionStatusValue,
															   @WebParam(name = "subscriptionStatusName") String subscriptionStatusName,
															   @WebParam(name = "topUpName") String topUpName,
															   @WebParam(name = "subscriptionOrder") String subscriptionOrder,
															   @WebParam(name = "startTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long startTime,
															   @WebParam(name = "endTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long endTime,
															   @WebParam(name = "priority") @XmlJavaTypeAdapter(StringToIntegerAdapter.class) Integer priority,
															   @WebParam(name = "rejectReason") String rejectReason,
															   @WebParam(name = "parameter1") String parameter1,
															   @WebParam(name = "parameter2") String parameter2) {
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

		return subscriptionWSBLManager.changeTopUpSubscription(new ChangeTopUpSubscriptionWSRequest(subscriberId, alternateId, updateAction, topUpSubscriptionId, subscriptionStatusValue, subscriptionStatusName, topUpName, subscriptionOrder, startTime, endTime, rejectReason, parameter1, parameter2, priority, WEBSERVICE_NAME, WS_CHANGE_TOP_UP_SUBSCRIPTION));
	}

	@Override
	@WebMethod(operationName = WS_LIST_RNC_PACKAGES)
	public RnCPackageQueryResponse wsListRnCPackages(
			@WebParam(name="rncPackageId")String rncPackageId,
			@WebParam(name="rncPackageName")String rncPackageName,
			@WebParam(name="rncPackageType")String rncPackageType,
			@WebParam(name="currency")String currency,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsListRnCPackages");
		}
		rncPackageId=StringUtil.trimParameter(rncPackageId);
		rncPackageName=StringUtil.trimParameter(rncPackageName);
		rncPackageType=StringUtil.trimParameter(rncPackageType);
		currency=StringUtil.trimParameter(currency);
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
		return packageWSBlManager.getRnCPackages(new ListPackageRequest(rncPackageId,rncPackageName,rncPackageType,currency,parameter1,parameter2, WEBSERVICE_NAME, WS_LIST_RNC_PACKAGES));
	}

	@Override
	@WebMethod(operationName = WS_CHANGE_RNC_ADD_ON_SUBSCRIPTION)
	public SubscriptionResponse wsChangeRnCAddOnSubscription(@WebParam(name = "subscriberId")String subscriberId,
															 @WebParam(name = "alternateId")String alternateId,
															 @WebParam(name = "updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
															 @WebParam(name = "subscriptionStatusValue") String subscriptionStatusValue,
															 @WebParam(name = "subscriptionStatusName") String subscriptionStatusName,
															 @WebParam(name = "rncAddOnSubscriptionId") String rncAddOnSubscriptionId,
															 @WebParam(name = "rncAddOnProductOfferName")String rncAddOnProductOfferName,
															 @WebParam(name = "subscriptionOrder") String subscriptionOrder,
															 @WebParam(name = "startTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long startTime,
															 @WebParam(name = "endTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long endTime,
															 @WebParam(name = "priority") @XmlJavaTypeAdapter(StringToIntegerAdapter.class) Integer priority,
															 @WebParam(name = "rejectReason") String rejectReason,
															 @WebParam(name = "parameter1") String parameter1,
															 @WebParam(name = "parameter2") String parameter2){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsChangeAddOnSubscription");
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

		return subscriptionWSBLManager.changeRnCAddonSubscription(new ChangeRnCAddOnProductOfferWSRequest(subscriberId, alternateId, updateAction, rncAddOnSubscriptionId, subscriptionStatusValue, subscriptionStatusName, rncAddOnProductOfferName, subscriptionOrder, startTime, endTime, priority, rejectReason, parameter1, parameter2, WEBSERVICE_NAME, WS_CHANGE_RNC_ADD_ON_SUBSCRIPTION));
	}

	@Override
	@WebMethod(operationName = WS_UPDATE_CREDIT_LIMIT)
	public UpdateCreditLimitResponse wsUpdateCreditLimit(@WebParam(name = "subscriberId")String subscriberId,
														 @WebParam(name = "alternateId")String alternateId,
														 @WebParam(name = "creditLimit") String creditLimit,
														 @WebParam(name = "applicableBillingCycle") String applicableBillingCycle,
														 @WebParam(name = "parameter1") String parameter1,
														 @WebParam(name = "parameter2") String parameter2){

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

		return subscriptionWSBLManager.updateCreditLimit(new UpdateCreditLimitWSRequest(subscriberId, alternateId, creditLimit, applicableBillingCycle, parameter1, parameter2, WEBSERVICE_NAME, WS_UPDATE_CREDIT_LIMIT));
	}

	@Override
	@WebMethod(operationName = WS_CHANGE_BILL_DAY)
	public ChangeBillDayResponse wsChangeBillDay(@WebParam(name = "subscriberId") String subscriberId,
												 @WebParam(name = "alternateId") String alternateId,
												 @WebParam(name = "nextBillDate") Integer nextBillDate,
												 @WebParam(name = "parameter1") String parameter1,
												 @WebParam(name = "parameter2") String parameter2) {
		return subscriptionWSBLManager.changeBillDay(new ChangeBillDayWSRequest(subscriberId, alternateId, nextBillDate, parameter1, parameter2, WEBSERVICE_NAME, WS_CHANGE_BILL_DAY));
	}


}
