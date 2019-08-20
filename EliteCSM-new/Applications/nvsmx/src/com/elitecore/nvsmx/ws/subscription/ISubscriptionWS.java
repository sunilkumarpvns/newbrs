package com.elitecore.nvsmx.ws.subscription;

import com.elitecore.corenetvertex.util.commons.jaxb.adapter.StringToIntegerAdapter;
import com.elitecore.nvsmx.ws.subscription.response.BoDQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.ChangeBillDayResponse;
import com.elitecore.nvsmx.ws.subscription.response.ListPackagesResponse;
import com.elitecore.nvsmx.ws.subscription.response.PackageQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.RnCPackageQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.SubscriptionResponse;
import com.elitecore.nvsmx.ws.subscription.response.TopUpQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.TopUpSubscriptionResponse;
import com.elitecore.nvsmx.ws.util.LongToStringAdapter;
import com.elitecore.nvsmx.ws.util.StringToIntegerAdapterForReauth;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

public interface ISubscriptionWS extends IUsageMonitoringWS{
	String WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION = "wsChangeDataAddOnSubscription";
	String WS_CHANGE_TOP_UP_SUBSCRIPTION = "wsChangeTopUpSubscription";
	String WS_LIST_PRODUCT_OFFERS = "wsListProductOffers";
	String WS_LIST_TOP_UP_PACKAGES = "wsListTopUpPackages";
	String WS_LIST_BOD_PACKAGES = "wsListBoDPackages";
	String WS_LIST_ADD_ON_SUBSCRIPTIONS = "wsListAddOnSubscriptions";
	String WS_LIST_TOP_UP_SUBSCRIPTIONS = "wsListTopUpSubscriptions";
	String WS_SUBSCRIBE_ADD_ON = "wsSubscribeAddOnProductOffer";
	String WS_SUBSCRIBE_TOP_UP = "wsSubscribeTopUp";
	String WS_LIST_DATA_PACKAGES = "wsListDataPackages";
	String WS_LIST_RNC_PACKAGES = "wsListRnCPackages";
	String WS_LIST_PACKAGES = "wsListPackages";
	String WS_CHANGE_RNC_ADD_ON_SUBSCRIPTION = "wsChangeRnCAddOnSubscription";
	String WS_SUBSCRIBE_MONETARY_RECHARGE_PLAN = "wsSubscribeMonetaryRechargePlan";
	String WS_CHANGE_BILL_DAY = "wsChangeBillDay";
	String WS_SUBSCRIBE_BOD = "wsSubscribeBoD";


	@WebMethod( operationName = WS_LIST_PACKAGES )
	ListPackagesResponse wsListPackages(
			@WebParam(name="packageId")String packageId,
			@WebParam(name="packageName")String packageName,
			@WebParam(name="packageType")String packageType,
			@WebParam(name="type")String type,
			@WebParam(name="mode")String mode,
			@WebParam(name="group")String group,
			@WebParam(name="currency")String currency,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_LIST_DATA_PACKAGES )
	PackageQueryResponse wsListDataPackages(
			@WebParam(name="packageId")String packageId,
			@WebParam(name="packageName")String packageName,
			@WebParam(name="packageType")String packageType,
			@WebParam(name="currency") String currency,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);



	@WebMethod( operationName = WS_LIST_ADD_ON_SUBSCRIPTIONS )
	SubscriptionResponse wsListAddOnSubscriptions(
			@WebParam(name="subscriberId")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="subscriptionStatusValue")String subscriptionStatusValue ,
			@WebParam(name="subscriptionStatusName")String subscriptionStatusName ,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_LIST_TOP_UP_SUBSCRIPTIONS )
	TopUpSubscriptionResponse wsListTopUpSubscriptions(
			@WebParam(name="subscriberId")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="subscriptionStatusValue")String subscriptionStatusValue ,
			@WebParam(name="subscriptionStatusName")String subscriptionStatusName ,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_SUBSCRIBE_ADD_ON )
	SubscriptionResponse wsSubscribeAddOnProductOffer(
			@WebParam(name="parentId")String parentId ,
			@WebParam(name="subscriberId")String subscriberId ,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
			@WebParam(name="cui")String cui ,
			@WebParam(name="addOnProductOfferId")String addOnProductOfferId,
			@WebParam(name="addOnProductOfferName")String addOnProductOfferName,
			@WebParam(name="subscriptionStatusValue")String subscriptionStatusValue ,
			@WebParam(name="subscriptionStatusName")String subscriptionStatusName ,
			@WebParam(name="startTime")@XmlJavaTypeAdapter(LongToStringAdapter.class) Long startTime,
			@WebParam(name="endTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long endTime,
			@WebParam(name="priority") @XmlJavaTypeAdapter(StringToIntegerAdapter.class) Integer priority,
			@WebParam(name="fnFGroupName") String fnFGroupName,
			@WebParam(name="fnFMembers") List<String> fnFMembers ,
			@WebParam(name="updateBalanceIndication")boolean updateBalanceIndication,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION)
	SubscriptionResponse wsChangeDataAddOnSubscription(
			@WebParam(name="subscriberId")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
			@WebParam(name="dataAddOnSubscriptionId")String dataAddOnProductOfferId,
			@WebParam(name="subscriptionStatusValue")String subscriptionStatusValue ,
			@WebParam(name="subscriptionStatusName")String subscriptionStatusName ,
			@WebParam(name="dataAddOnProductOfferName") String dataAddOnProductOfferName,
			@WebParam(name="subscriptionOrder") String subscriptionOrder,
			@WebParam(name="startTime")@XmlJavaTypeAdapter(LongToStringAdapter.class)Long startTime ,
			@WebParam(name="endTime")@XmlJavaTypeAdapter(LongToStringAdapter.class)Long endTime ,
			@WebParam(name = "priority") @XmlJavaTypeAdapter(StringToIntegerAdapter.class) Integer priority,
			@WebParam(name="rejectReason")String rejectReason ,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_SUBSCRIBE_TOP_UP )
	TopUpSubscriptionResponse wsSubscribeTopUp(
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
			@WebParam(name="Update Balance Indication")boolean updateBalanceIndication ,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_LIST_TOP_UP_PACKAGES )
	TopUpQueryResponse wsListTopUpPackages(
			@WebParam(name="subscriberId")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="productOfferName")String productOfferName,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod( operationName = WS_LIST_BOD_PACKAGES )
	BoDQueryResponse wsListBoDPackages(
			@WebParam(name="subscriberId")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="productOfferName")String productOfferName,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod(operationName = WS_CHANGE_TOP_UP_SUBSCRIPTION)
	TopUpSubscriptionResponse wsChangeTopUpSubscription(
			@WebParam(name="subscriberId")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
			@WebParam(name = "topUpSubscriptionId") String topUpSubscriptionId,
			@WebParam(name = "subscriptionStatusValue") String subscriptionStatusValue,
			@WebParam(name = "subscriptionStatusName") String subscriptionStatusName,
			@WebParam(name = "topUpName") String topUpName,
			@WebParam(name = "subscriptionOrder") String subscriptionOrder,
			@WebParam(name = "startTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long startTime,
			@WebParam(name = "endTime") @XmlJavaTypeAdapter(LongToStringAdapter.class) Long endTime,
			@WebParam(name = "priority") @XmlJavaTypeAdapter(StringToIntegerAdapter.class) Integer priority,
			@WebParam(name = "rejectReason") String rejectReason, @WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod( operationName = WS_LIST_RNC_PACKAGES )
	RnCPackageQueryResponse wsListRnCPackages(
			@WebParam(name="rncPackageId")String rncPackageId,
			@WebParam(name="rncPackageName")String rncPackageName,
			@WebParam(name="rncPackageType")String rncPackageType,
			@WebParam(name="currency") String currency,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod(operationName = WS_CHANGE_RNC_ADD_ON_SUBSCRIPTION)
	SubscriptionResponse wsChangeRnCAddOnSubscription(@WebParam(name = "subscriberId")String subscriberId,
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
                                                             @WebParam(name = "parameter2") String parameter2);

	@WebMethod( operationName = WS_CHANGE_BILL_DAY )
	ChangeBillDayResponse wsChangeBillDay(
			@WebParam(name="subscriberId")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="nextBillDate")Integer nextBillDate,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);
}

