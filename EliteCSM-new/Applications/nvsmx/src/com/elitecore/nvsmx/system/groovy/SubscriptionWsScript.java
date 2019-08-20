package com.elitecore.nvsmx.system.groovy;

import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
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
import com.elitecore.nvsmx.ws.subscription.request.UpdateMonetaryBalanceRequest;
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

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class SubscriptionWsScript {

	private static final String MODULE = "SUBSCRIPTION-WS-SCRIPT";

	public void init() throws InitializationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "SubscriptionWS script(" + getName() + ") initialized successfully");
		}
	}

	public abstract String getName();

	public void preListProductOffers(ListProductOfferRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListProductOffers of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postListProductOffers(ListProductOfferRequest request, ListProductOfferResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListProductOffers of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preListAddOnSubscriptions(ListSubscriptionWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListAddOnSubscriptions of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postListAddOnSubscriptions(ListSubscriptionWSRequest request, SubscriptionResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListAddOnSubscriptions of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preSubscribeAddOnProductOffer(SubscribeAddOnWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preSubscribeAddOnProductOffer of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postSubscribeMonetaryRechargePlan(SubscribeMonetaryRechargePlanWSRequest request, MonetaryRechargeSubscriptionResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postSubscribeMonetaryRechargePlan of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preSubscribeMonetaryRechargePlan(SubscribeMonetaryRechargePlanWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preSubscribeMonetaryRechargePlan of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postSubscribeAddOnProductOffer(SubscribeAddOnWSRequest request, SubscriptionResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postSubscribeAddOnProductOffer of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preChangeAddOnSubscription(ChangeDataAddOnSubscriptionWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preChangeDataAddOnSubscription of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postChangeAddOnSubscription(ChangeDataAddOnSubscriptionWSRequest request, SubscriptionResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postChangeDataAddOnSubscription of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preListUsageMonitoringInformation(ListUsageMonitoringInformationWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListUsageMonitoringInformation of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postListUsageMonitoringInformation(ListUsageMonitoringInformationWSRequest request, UMQueryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListUsageMonitoringInformation of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preBalanceEnquiry(BalanceEnquiryRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postBalanceEnquiry(BalanceEnquiryRequest request, BalanceEnquiryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preNonMonitoryBalanceEnquiry(NonMonitoryBalanceEnquiryRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preMonetaryBalanceEnquiry(MonetaryBalanceEnquiryRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preMonetaryBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}


	public void preRnCBalanceEnquiry(RnCBalanceEnquiryRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preRnCBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}


	public void preAddBalanceEnquiry(MonetaryOperationRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preAddBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preUpdateBalanceEnquiry(UpdateMonetaryBalanceRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preUpdateBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postNonMonitoryBalanceEnquiry(NonMonitoryBalanceEnquiryRequest request, NonMonitoryBalanceInquiryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postRnCBalanceEnquiry(RnCBalanceEnquiryRequest request, RnCBalanceEnquiryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postRnCBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postMonetaryBalanceEnquiry(MonetaryBalanceEnquiryRequest request, MonetaryBalanceInquiryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postMonetaryBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postAddBalanceEnquiry(MonetaryOperationRequest request, MonetoryOperationResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postAddBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postUpdateBalanceEnquiry(UpdateMonetaryBalanceRequest request, UpdateMonetaryBalanceResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postUpdateBalanceEnquiry of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preListTopUpPackages(TopUpQueryRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListTopUpPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postListTopUpPackages(TopUpQueryRequest request, TopUpQueryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListTopUpPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preListBoDPackages(BoDQueryRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListBoDPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postListBoDPackages(BoDQueryRequest request, BoDQueryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListBoDackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preChangeTopUpSubscription(ChangeTopUpSubscriptionWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preChangeTopUpSubscription of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postChangeTopUpSubscription(ChangeTopUpSubscriptionWSRequest request, TopUpSubscriptionResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postChangeTopUpSubscription of SubscriptionWsScript(" + getName() + ") is called");
		}
	}


	public void preSubscribeTopUp(SubscribeTopUpWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preSubscribeTopUp of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postSubscribeTopUp(SubscribeTopUpWSRequest request, TopUpSubscriptionResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postSubscribeTopUp of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preListTopUpSubscriptions(ListSubscriptionWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListTopUpSubscriptions of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postListTopUpSubscriptions(ListSubscriptionWSRequest request, TopUpSubscriptionResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListTopUpSubscriptions of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preListDataPackages(ListPackageRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListDataPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postListDataPackages(ListPackageRequest request, PackageQueryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListDataPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preListRnCPackages(ListPackageRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListRnCPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postListRnCPackages(ListPackageRequest request, RnCPackageQueryResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListRncPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preChangeRnCAddonProductOffer(ChangeRnCAddOnProductOfferWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preChangeRnCAddonProductOffer of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postChangeRnCAddonProductOffer(ChangeRnCAddOnProductOfferWSRequest request, SubscriptionResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postChangeRnCAddonProductOffer of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preUpdateCreditLimit(UpdateCreditLimitWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListRnCPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postUpdateCreditLimit(UpdateCreditLimitWSRequest request, UpdateCreditLimitResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListRncPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preChangeBillDay(ChangeBillDayWSRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preChangeBillDay of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postChangeBillDay(ChangeBillDayWSRequest request, ChangeBillDayResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postChangeBillDay of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preListPackages(ListPackagesRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preListPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postListPackages(ListPackagesRequest request, ListPackagesResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postListPackages of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preSubscribeBod(SubscribeBodWsRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preSubscribeBod of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postSubscribeBod(SubscribeBodWsRequest request, BodSubscriptionResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postSubscribeBod of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void preFnFOperation(FnFOperationRequest request) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preFnFOperation of SubscriptionWsScript(" + getName() + ") is called");
		}
	}

	public void postFnFOperation(FnFOperationRequest request, FnFOperationResponse response) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postFnFOperation of SubscriptionWsScript(" + getName() + ") is called");
		}
	}
}