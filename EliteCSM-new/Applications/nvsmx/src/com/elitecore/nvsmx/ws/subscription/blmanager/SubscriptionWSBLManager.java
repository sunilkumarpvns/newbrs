package com.elitecore.nvsmx.ws.subscription.blmanager;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferAutoSubscription;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.*;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCard;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.corenetvertex.spr.*;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.data.*;
import com.elitecore.corenetvertex.spr.ddf.CurrencyValidatorPredicate;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.corenetvertex.spr.util.RenewalIntervalUtility;
import com.elitecore.corenetvertex.spr.util.SubscriberMonetaryBalanceUtil;
import com.elitecore.corenetvertex.util.util.Predicates;
import com.elitecore.nvsmx.policydesigner.controller.util.ApplicableBoDByBasePackagePredicate;
import com.elitecore.nvsmx.policydesigner.controller.util.ApplicableQuotaTopUpByBasePackagePredicate;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.groovy.GroovyManager;
import com.elitecore.nvsmx.system.groovy.SubscriptionWsScript;
import com.elitecore.nvsmx.system.hibernate.SessionProviderImpl;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.ws.interceptor.*;
import com.elitecore.nvsmx.ws.subscription.data.QuotaProfileBalance;
import com.elitecore.nvsmx.ws.subscription.data.*;
import com.elitecore.nvsmx.ws.subscription.exception.ValidationFailedException;
import com.elitecore.nvsmx.ws.subscription.request.*;
import com.elitecore.nvsmx.ws.subscription.response.*;
import com.elitecore.nvsmx.ws.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.*;
import static com.elitecore.nvsmx.ws.util.WebServiceTopUpPkgResponseMapper.QUOTA_TOP_UP_TO_WS_TOP_UP_MAPPER;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 *
 * @author Jay Trivedi
 *
 */
public class SubscriptionWSBLManager {

	private static final String MODULE = "SUB-WSBL-MNGR";
	private static final String SUCCESS = "SUCCESS";
	private static final long ALL_SERVICE_ID = 1;
	private static final long DEFAULT_RATING_GROUP = 0;
	private static final Long BIG_DATE_IN_MILLIS = Timestamp.valueOf("2037-12-31 00:00:00").getTime();
	private StaffData adminStaff;
	private List<WebServiceInterceptor> interceptors = new ArrayList<>();
	private ChangeRnCAddonSubscriptionProcessor changeRnCAddonSubscriptionProcessor;
	private static ImportExportUtil importExportUtil = new ImportExportUtil(new SessionProviderImpl());

	public SubscriptionWSBLManager() {
		adminStaff = new StaffData();
		adminStaff.setUserName("admin");
		interceptors.add(WebServiceStatisticsManager.getInstance());
		interceptors.add(WebServiceAuditInterceptor.getInstance());
		interceptors.add(DiagnosticContextWebServiceInterceptor.getInstance());

		this.changeRnCAddonSubscriptionProcessor = new ChangeRnCAddonSubscriptionProcessor(interceptors, GroovyManager.getInstance().getSubscriptionGroovyScripts());
	}

	public ListProductOfferResponse getAllProductOffers(ListProductOfferRequest request) {

		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();
		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preListProductOffers(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		ListProductOfferResponse response = doGetAllProductOffers(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postListProductOffers(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private ListProductOfferResponse doGetAllProductOffers(ListProductOfferRequest request) {
		List<ProductOfferData> productOfferList =Collectionz.newArrayList();
		PkgType offerType = null;
		PkgMode mode = null;
		Map<String,String> idWiseGroupNameMap=null;
		Predicate productOfferPredicate;

		if (isNullOrBlank(request.getType()) == false){
			offerType = PkgType.fromName(request.getType());
			if (offerType == null || offerType.name().equalsIgnoreCase(PkgType.EMERGENCY.name()) || offerType.name().equalsIgnoreCase(PkgType.PROMOTIONAL.name())) {
				getLogger().error(MODULE, "Invalid Product-Offer-Type received " + request.getType());
				return new ListProductOfferResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while listing Product Offers.Reason: Invalid ProductOffer Type received :" + request.getType(), productOfferList, null, null,
						request.getWebServiceName(), request.getWebServiceMethodName());
			}

		}

		if (isNullOrBlank(request.getMode()) == false) {
			mode = PkgMode.getMode(request.getMode());
			if (mode == null || (PkgMode.TEST != mode && PkgMode.LIVE != mode && PkgMode.LIVE2 != mode)) {
				getLogger().error(MODULE, "Invalid Mode received " + request.getMode());
				return new ListProductOfferResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while listing Product Offers.Reason: Invalid ProductOffer Mode received :" + request.getMode(), productOfferList, null, null,
						request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if(isNullOrBlank(request.getGroup()) == false){
			List<String> groupNameList = CommonConstants.COMMA_SPLITTER.split(request.getGroup());
			idWiseGroupNameMap = getGroupMapByName(groupNameList);
			if(idWiseGroupNameMap.isEmpty()){
				getLogger().error(MODULE, "Invalid group received " + request.getGroup());
				return new ListProductOfferResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while listing Product Offers.Reason: Invalid ProductOffer group received :" + request.getGroup(), productOfferList, null, null,
						request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if(StringUtils.isNotBlank(request.getCurrency())){
			if ((SystemParameterDAO.isMultiCurrencyEnable() == false) && (SystemParameterDAO.getCurrency().equals(request.getCurrency()) == false)) {
				getLogger().error(MODULE, "Currency other than system currency is not supported");
				return new ListProductOfferResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while fetching data packages.Reason: Currency other than system currency is not supported",productOfferList, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}else if (SystemParameter.CURRENCY.validate(request.getCurrency()) == false) {
				getLogger().error(MODULE, "Invalid Currency received " + request.getCurrency());
				return new ListProductOfferResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while fetching data packages.Reason: Invalid Currency received :" + request.getCurrency(),productOfferList, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		productOfferPredicate=ProductOfferModePredicate.create(mode).and(ProductOfferGroupPredicate.create(idWiseGroupNameMap)).and(ProductOfferCurrencyPredicate.create(request.getCurrency()));

		if (isNullOrBlank(request.getProductOfferId()) == false) {
			productOfferList = createProductOfferList(getProductOfferListById(request.getProductOfferId()),productOfferPredicate,offerType);
		}else if(isNullOrBlank(request.getProductOfferName()) == false){
			productOfferList = createProductOfferList(getProductOfferListByName(request.getProductOfferName()),productOfferPredicate,offerType);
		}else{
			productOfferList = createProductOfferList(getProductOfferDataFromStore(),productOfferPredicate,offerType);
		}
		if (Collectionz.isNullOrEmpty(productOfferList)) {
			return new ListProductOfferResponse(ResultCode.NOT_FOUND.code, "No Product Offers available", productOfferList, null, null,
					request.getWebServiceName(), request.getWebServiceMethodName());
		}

		return new ListProductOfferResponse(ResultCode.SUCCESS.code, SUCCESS, productOfferList, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	private List<ProductOffer> getProductOfferListById(String productOfferId) {

		List<ProductOffer> productOfferList = Collectionz.newArrayList();

		ProductOffer productOffer = PolicyManager.getInstance().getProductOffer().byId(productOfferId);
		if (Objects.isNull(productOffer)) {
			return productOfferList;
		}

		if(productOffer.getPolicyStatus() == PolicyStatus.FAILURE){
			return productOfferList;
		}
		productOfferList.add(productOffer);
		return productOfferList;

	}

	private List<ProductOffer> getProductOfferListByName(String productOfferName) {
		List<ProductOffer> productOfferList = Collectionz.newArrayList();

		ProductOffer productOffer = PolicyManager.getInstance().getProductOffer().byName(productOfferName);
		if (Objects.isNull(productOffer)) {
			return productOfferList;
		}

		if(productOffer.getPolicyStatus() == PolicyStatus.FAILURE){
			return productOfferList;
		}
		productOfferList.add(productOffer);
		return productOfferList;

	}

	public List<ProductOfferData> createProductOfferList(List<ProductOffer> productOfferStoreList,Predicate productOfferPredicate,PkgType offerType) {

		List<ProductOfferData> productOfferDataList = Collectionz.newArrayList();

		if (Collectionz.isNullOrEmpty(productOfferStoreList)) {
			return null;
		}

		for(ProductOffer productOffer:productOfferStoreList){

			if (productOfferPredicate.test(productOffer) == false) {
				continue;
			}

			if(offerType == null || productOffer.getType().name().equalsIgnoreCase(offerType.name())) {

				List<ProductOfferServicePkgRelData> productOfferServicePkgRelDataList = createServicePkgRelList(productOffer.getProductOfferServicePkgRelDataList());
				List<ProductOfferAutoSubscriptionData> productOfferAutoSubscriptionDataList = createAutoSubscriptionList(productOffer.getProductOfferAutoSubscriptions());

				productOfferDataList.add(new ProductOfferData(productOffer.getId(), productOffer.getName(), productOffer.getDescription(),
						productOffer.getType(), productOffer.getPackageMode(), productOffer.getCurrency(), productOffer.getValidityPeriod(), productOffer.getValidityPeriodUnit(),
						productOffer.getSubscriptionPrice(), productOffer.getCreditBalance(), productOffer.getAvailabilityStartDate(),
						productOffer.getAvailabilityEndDate(), productOffer.isFnFOffer(), productOffer.getParam1(), productOffer.getParam2(), productOffer.getStatus(),
						productOfferServicePkgRelDataList, productOfferAutoSubscriptionDataList, productOffer.getDataServicePkgId(),
						productOffer.getDataServicePkgData()!=null?productOffer.getDataServicePkgData().getName():null, productOffer.getGroups(),
						productOffer.getPolicyStatus()));
			}
		}

		return productOfferDataList;
	}

	private List<ProductOfferServicePkgRelData> createServicePkgRelList(List<ProductOfferServicePkgRel> productOfferServicePkgRels) {
		List<ProductOfferServicePkgRelData> productOfferServicePkgRelDataList = Collectionz.newArrayList();

		for(ProductOfferServicePkgRel productOfferAutoSubscriptionData:productOfferServicePkgRels){
            ProductOfferServicePkgRelData productOfferServicePkgRelData = new ProductOfferServicePkgRelData(
                    productOfferAutoSubscriptionData.getId(),productOfferAutoSubscriptionData.getServiceData().getId(),
                    productOfferAutoSubscriptionData.getServiceData().getName(),
                    productOfferAutoSubscriptionData.getRncPackageId(),productOfferAutoSubscriptionData.getRncPackageData().getName());
            productOfferServicePkgRelDataList.add(productOfferServicePkgRelData);
        }
		return productOfferServicePkgRelDataList;
	}

	private List<ProductOfferAutoSubscriptionData> createAutoSubscriptionList(List<ProductOfferAutoSubscription> productOfferAutoSubscriptions) {
		List<ProductOfferAutoSubscriptionData> productOfferAutoSubscriptionDataList = Collectionz.newArrayList();
		for(ProductOfferAutoSubscription productOfferAutoSubscription:productOfferAutoSubscriptions) {
                ProductOfferAutoSubscriptionData productOfferAutoSubscriptionData = new ProductOfferAutoSubscriptionData(
                        productOfferAutoSubscription.getId(),
                        productOfferAutoSubscription.getAdvancedCondition(),productOfferAutoSubscription.getAdvanceConditonStr(),
                        productOfferAutoSubscription.getAddOnProductOfferId(),productOfferAutoSubscription.getAddOnProductOfferData().getName());
            productOfferAutoSubscriptionDataList.add(productOfferAutoSubscriptionData);
        }
        return productOfferAutoSubscriptionDataList;
	}

	private List<ProductOffer> getProductOfferDataFromStore(){
		return getNVSMXPolicyRepository().
				getProductOffer().all().
				stream().
				filter(offerData -> offerData.getPolicyStatus() != PolicyStatus.FAILURE).
				collect(Collectors.toList());
	}

	public TopUpQueryResponse getTopUpPackages(TopUpQueryRequest request) {

		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();
		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName() );
			}

			try {
				groovyScript.preListTopUpPackages(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() +" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		TopUpQueryResponse response = doGetTopUpPackages(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName() );
			}

			try {
				groovyScript.postListTopUpPackages(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private TopUpQueryResponse doGetTopUpPackages(TopUpQueryRequest request) {
		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();
		String baseProductOfferName = request.getPackageName();

		if (StringUtils.isBlank(subscriberIdentity) && StringUtils.isBlank(alternateIdentity) && StringUtils.isBlank(baseProductOfferName)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "No filteration parameters provided so fetching all top up packages");
			}

			return getTopUpResponse(getAllTopUps(), request);
		}

		SPRInfo sprInfo = null;
		String subscriberProductOfferName ;
		if (StringUtils.isEmpty(subscriberIdentity)) {
			if (StringUtils.isNotEmpty(alternateIdentity)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Subscriber ID not received");
				}
				try {
					sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateIdentity, adminStaff);
					if (sprInfo == null) {
						getLogger().error(MODULE, "Unable to list topups for alternate Id: " + alternateIdentity + ". Reason: Subscriber not found");
						return new TopUpQueryResponse(ResultCode.NOT_FOUND.code,
								ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with alternate identity:" + alternateIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					subscriberProductOfferName = sprInfo.getProductOffer();
					if (StringUtils.isNotBlank(baseProductOfferName) && baseProductOfferName.equals(subscriberProductOfferName) == false) {
						getLogger().error(MODULE, "Unable to list topups for alternate Id: " + alternateIdentity + ". Reason: No product offer found with name: " + baseProductOfferName + " for alternate ID: " + alternateIdentity);
						return new TopUpQueryResponse(ResultCode.NOT_FOUND.code,
								ResultCode.NOT_FOUND.name + ". Reason: No product offer found with name: " + baseProductOfferName + " for alternate Identity: " + alternateIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					} else {
						baseProductOfferName = subscriberProductOfferName;
					}
				} catch (OperationFailedException e) {
					getLogger().error(MODULE, "Error while list topups with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
					if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
						getLogger().trace(MODULE, e);
					}
					return new TopUpQueryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				} catch (UnauthorizedActionException e) {
					getLogger().error(MODULE, "Error while list topups with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
					getLogger().trace(MODULE, e);
					return new TopUpQueryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

		} else {
			try {
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);
				if (sprInfo == null) {
					getLogger().error(MODULE, "Unable to list topups for subscriber Id: " + subscriberIdentity + ". Reason: Subscriber not found");
					return new TopUpQueryResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with subscriber identity:" + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscriberProductOfferName = sprInfo.getProductOffer();
				if (StringUtils.isNotBlank(baseProductOfferName) && baseProductOfferName.equals(subscriberProductOfferName) == false) {
					getLogger().error(MODULE, "Unable to list topup for subscriber Id: " + subscriberIdentity + ". Reason: No product offer found with name: " + baseProductOfferName + " for subscriber ID: " + subscriberIdentity);
					return new TopUpQueryResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: No package found with Package Name: " + baseProductOfferName + " for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				} else {
					baseProductOfferName = subscriberProductOfferName;
				}
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while listing topups with subscriber Id: " + subscriberIdentity + ". Reason: " + e.getMessage());
				if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE, e);
				}
				return new TopUpQueryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			} catch (UnauthorizedActionException e) {
				getLogger().error(MODULE, "Error while listing topup with subscriber Id: " + subscriberIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				return new TopUpQueryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		ProductOffer productOffer = getNVSMXPolicyRepository().getProductOffer().byName(baseProductOfferName);

		if (Objects.isNull(productOffer)) {
			getLogger().error(MODULE, "Unable to list topups. Reason: base product offer with name: " + baseProductOfferName + " doesn't exist");
			return new TopUpQueryResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: base product offer with name: " + baseProductOfferName + " doesn't exist", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		UserPackage dataServicePkg = productOffer.getDataServicePkgData();

		if (Objects.isNull(dataServicePkg)) {
			String message = " Reason: Product offer " + baseProductOfferName + " does not have Base package attached";
			getLogger().error(MODULE, "Unable to list topups. " + message);
			return new TopUpQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + message, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (dataServicePkg.isQuotaProfileExist() == false) {
			getLogger().error(MODULE, "Unable to find applicable Top Ups . Reason: Base package does not have any quota profile attached");
			String message = " Reason: Base package does not have any quota profile attached";
			getLogger().error(MODULE, "Unable to list topups. " + message);
			return new TopUpQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + message, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		List<String> pccProfileNames = dataServicePkg.getQoSProfiles().stream().map(QoSProfile::getName).collect(Collectors.toList());

		List<QuotaTopUp> topUps = getTopUps(pccProfileNames, isTestMode(sprInfo,productOffer));

		if (CollectionUtils.isEmpty(topUps)) {
			return new TopUpQueryResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: No topup found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		List<TopUpPackageData> availableTopUpList = convertTopUpsToWSResponse(topUps);
		return getTopUpResponse(availableTopUpList, request);
	}

	private boolean isTestMode(SPRInfo sprInfo, ProductOffer productOffer) {

		if (Objects.isNull(sprInfo) && Objects.isNull(productOffer)) {
			return false;
		}
		if(Objects.isNull(sprInfo)){
		    return productOffer.getPackageMode() == PkgMode.TEST;
        }
		return SPRInfo.SubscriberMode.TEST == sprInfo.getSubscriberMode() ? true : false;

	}

	private List<TopUpPackageData> convertTopUpsToWSResponse(List<QuotaTopUp> topUps) {
		return topUps.stream().map(QUOTA_TOP_UP_TO_WS_TOP_UP_MAPPER).collect(Collectors.toList());
	}

	private List<QuotaTopUp> getTopUps(List<String> pccProfileNames, boolean isTestSubscriber) {
		List<QuotaTopUp> topUps;
		if (isTestSubscriber) {
			topUps = getNVSMXPolicyRepository().getActiveAllQuotaTopUpDatas();
		} else {
			topUps = getNVSMXPolicyRepository().getActiveLiveQuotaTopUpDatas();
		}
		ApplicableQuotaTopUpByBasePackagePredicate applicableQuotaTopUpPredicate = ApplicableQuotaTopUpByBasePackagePredicate.create(pccProfileNames);

		return topUps.stream()
				.filter(topUp -> PolicyStatus.FAILURE != topUp.getStatus())
				.filter(applicableQuotaTopUpPredicate)
				.collect(Collectors.toList());
	}

	private TopUpQueryResponse getTopUpResponse(List<TopUpPackageData> topUpPkgs, TopUpQueryRequest request) {
		if (Collectionz.isNullOrEmpty(topUpPkgs)) {
			return new TopUpQueryResponse(ResultCode.SUCCESS.code, "No topups available", topUpPkgs, null, null,
					request.getWebServiceName(), request.getWebServiceMethodName());
		}
		return new TopUpQueryResponse(ResultCode.SUCCESS.code, SUCCESS, topUpPkgs, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	public BoDQueryResponse getBoDPackages(BoDQueryRequest request) {

		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();
		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName() );
			}

			try {
				groovyScript.preListBoDPackages(request);
			} catch (Throwable e) {//NOSONAR
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() +" . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		BoDQueryResponse response = doGetBoDPackages(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName() );
			}

			try {
				groovyScript.postListBoDPackages(request, response);
			} catch (Throwable e) {//NOSONAR
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private BoDQueryResponse doGetBoDPackages(BoDQueryRequest request) {
		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();
		String baseProductOfferName = request.getOfferName();

		if (StringUtils.isBlank(subscriberIdentity) && StringUtils.isBlank(alternateIdentity) && StringUtils.isBlank(baseProductOfferName)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "No filtering parameters provided so fetching all BoD packages");
			}

			return getBoDResponse(getAllBoDs(), request);
		}

		SPRInfo sprInfo = null;
		String subscriberProductOfferName ;
		if (StringUtils.isEmpty(subscriberIdentity)) {
			if (StringUtils.isNotEmpty(alternateIdentity)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Subscriber ID not received");
				}
				try {
					sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateIdentity, adminStaff);
					if (sprInfo == null) {
						getLogger().error(MODULE, "Unable to list BoD for alternate Id: " + alternateIdentity + ". Reason: Subscriber not found");
						return new BoDQueryResponse(ResultCode.NOT_FOUND.code,
								ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with alternate identity:" + alternateIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					subscriberProductOfferName = sprInfo.getProductOffer();
					if (StringUtils.isNotBlank(baseProductOfferName) && baseProductOfferName.equals(subscriberProductOfferName) == false) {
						getLogger().error(MODULE, "Unable to list BoD for alternate Id: " + alternateIdentity + ". Reason: No product offer found with name: " + baseProductOfferName + " for alternate ID: " + alternateIdentity);
						return new BoDQueryResponse(ResultCode.NOT_FOUND.code,
								ResultCode.NOT_FOUND.name + ". Reason: No product offer found with name: " + baseProductOfferName + " for alternate Identity: " + alternateIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					} else {
						baseProductOfferName = subscriberProductOfferName;
					}
				} catch (OperationFailedException e) {
					getLogger().error(MODULE, "Error while list BoD with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
					if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
						getLogger().trace(MODULE, e);
					}
					return new BoDQueryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				} catch (UnauthorizedActionException e) {
					getLogger().error(MODULE, "Error while list BoD with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
					getLogger().trace(MODULE, e);
					return new BoDQueryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

		} else {
			try {
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);
				if (sprInfo == null) {
					getLogger().error(MODULE, "Unable to list BoD for subscriber Id: " + subscriberIdentity + ". Reason: Subscriber not found");
					return new BoDQueryResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with subscriber identity:" + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscriberProductOfferName = sprInfo.getProductOffer();
				if (StringUtils.isNotBlank(baseProductOfferName) && baseProductOfferName.equals(subscriberProductOfferName) == false) {
					getLogger().error(MODULE, "Unable to list BoD for subscriber Id: " + subscriberIdentity + ". Reason: No product offer found with name: " + baseProductOfferName + " for subscriber ID: " + subscriberIdentity);
					return new BoDQueryResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: No package found with Package Name: " + baseProductOfferName + " for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				} else {
					baseProductOfferName = subscriberProductOfferName;
				}
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while listing BoD with subscriber Id: " + subscriberIdentity + ". Reason: " + e.getMessage());
				if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE, e);
				}
				return new BoDQueryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			} catch (UnauthorizedActionException e) {
				getLogger().error(MODULE, "Error while listing BoD with subscriber Id: " + subscriberIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				return new BoDQueryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		ProductOffer productOffer = getNVSMXPolicyRepository().getProductOffer().byName(baseProductOfferName);

		if (Objects.isNull(productOffer)) {
			getLogger().error(MODULE, "Unable to list BoD. Reason: base product offer with name: " + baseProductOfferName + " doesn't exist");
			return new BoDQueryResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: base product offer with name: " + baseProductOfferName + " doesn't exist", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		UserPackage dataServicePkg = productOffer.getDataServicePkgData();

		if (Objects.isNull(dataServicePkg)) {
			String message = " Reason: Product offer " + baseProductOfferName + " does not have Base package attached";
			getLogger().error(MODULE, "Unable to list BoD. " + message);
			return new BoDQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + message, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (dataServicePkg.getType().equals(PkgType.BASE.name())==false) {
			getLogger().error(MODULE, "Unable to find applicable BoDs . Reason: Product offer is not of type Base");
			String message = " Reason: Product offer is not of type Base";
			getLogger().error(MODULE, "Unable to list BoD. " + message);
			return new BoDQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + message, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (dataServicePkg.isQoSProfileDetailsExist()==false) {
			getLogger().error(MODULE, "Unable to find applicable BoDs . Reason: Base data package in offer has no QoS configured");
			String message = " Reason: Base data package in offer has no QoS configured";
			getLogger().error(MODULE, "Unable to list BoD. " + message);
			return new BoDQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + message, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		List<String> pccProfileNames = dataServicePkg.getQoSProfiles().stream().map(QoSProfile::getName).collect(Collectors.toList());

		List<BoDPackage> topUps = getBoDs(pccProfileNames, isTestMode(sprInfo,productOffer));

		if (CollectionUtils.isEmpty(topUps)) {
			return new BoDQueryResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: No BoD found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		List<BoDPackageData> availableBoDs = convertBoDsToWSResponse(topUps);
		return getBoDResponse(availableBoDs, request);
	}

	private BoDQueryResponse getBoDResponse(List<BoDPackageData> bodPackages, BoDQueryRequest request) {
		if (Collectionz.isNullOrEmpty(bodPackages)) {
			return new BoDQueryResponse(ResultCode.SUCCESS.code, "No BoDs available", bodPackages, null, null,
					request.getWebServiceName(), request.getWebServiceMethodName());
		}
		return new BoDQueryResponse(ResultCode.SUCCESS.code, SUCCESS, bodPackages, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	private List<BoDPackageData> getAllBoDs() {
		List<BoDPackageData> bodPackages = Collectionz.newArrayList();
		List<BoDPackage> boDPackages = getNVSMXPolicyRepository().getBoDPackage().all();
		Timestamp currentTime = new Timestamp(TimeSource.systemTimeSource().currentTimeInMillis());
		if (Collectionz.isNullOrEmpty(boDPackages) == false) {
			for (BoDPackage bod : boDPackages) {
				if (bod.getPolicyStatus() != PolicyStatus.FAILURE) {
					if(nonNull(bod.getAvailabilityStartDate()) && bod.getAvailabilityStartDate().before(currentTime) == false) {
						continue;
					}
					else if(nonNull(bod.getAvailabilityEndDate()) && bod.getAvailabilityEndDate().after(currentTime) == false ){
						continue;
					}
					bodPackages.add(WSBoDDataFactory.createBoDPackageData(bod));
				}

			}
		}
		return bodPackages;
	}

	private List<BoDPackage> getBoDs(List<String> pccProfileNames, boolean isTestSubscriber) {
		List<BoDPackage> boDs;
		if (isTestSubscriber) {
			boDs = getNVSMXPolicyRepository().getBoDPackage().all();
		} else {
			boDs = getNVSMXPolicyRepository().getBoDPackage().all().stream().filter(pkg->pkg.getPackageMode()!=PkgMode.TEST).collect(Collectors.toList());
		}
		ApplicableBoDByBasePackagePredicate applicableBoDPredicate = ApplicableBoDByBasePackagePredicate.create(pccProfileNames);
		Timestamp currentTime = new Timestamp(TimeSource.systemTimeSource().currentTimeInMillis());

		return boDs.stream()
				.filter(bod -> PolicyStatus.FAILURE != bod.getPolicyStatus())
				.filter(applicableBoDPredicate)
				.filter(boDPackage -> ((boDPackage.getAvailabilityStartDate()!=null?boDPackage.getAvailabilityStartDate().before(currentTime):true)
						&& (boDPackage.getAvailabilityEndDate()!=null?boDPackage.getAvailabilityEndDate().after(currentTime):true)))
				.collect(Collectors.toList());
	}

	private List<BoDPackageData> convertBoDsToWSResponse(List<BoDPackage> topUps) {
		return topUps.stream().map(WSBoDDataFactory::createBoDPackageData).collect(Collectors.toList());
	}

	public SubscriptionResponse subscribeAddOnProductOffer(SubscribeAddOnWSRequest request) {


		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.preSubscribeAddOnProductOffer(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SubscriptionResponse response = doSubscribeAddOnProductOffer(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.postSubscribeAddOnProductOffer(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public TopUpSubscriptionResponse subscribeTopUp(SubscribeTopUpWSRequest request) {

		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.preSubscribeTopUp(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		TopUpSubscriptionResponse response = doSubscribeTopUp(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.postSubscribeTopUp(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private SubscriptionResponse doSubscribeAddOnProductOffer(SubscribeAddOnWSRequest request) {

		String subscriberId = request.getSubscriberId();
		String addOnProductOfferId = request.getAddOnProductOfferId();
		String addOnProductOfferName = request.getAddOnPackageName();
		boolean updateBalanceIndication = request.getUpdateBalanceIndication();
		String subscriptionStatusValue = request.getSubscriptionStatusValue();
		String subscriptionStatusName = request.getSubscriptionStatusName();
		String fnFGroupName=request.getFnFGroupName();
		String requestIpAddress = request.getRequestIpAddress();
		Integer updateAction = request.getUpdateAction();
		String currentPkgCurrency = "";

		SubscriptionState subscriptionState = SubscriptionState.STARTED;
		if (isNullOrBlank(subscriptionStatusValue) && isNullOrBlank(subscriptionStatusName)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Considering subscription status: " + subscriptionState.getName()
						+ " for subscriber id: " + subscriberId + "). Reason: Subscription status not received");
			}
		} else {
			subscriptionState = getSubscriptionStatus(subscriptionStatusValue, subscriptionStatusName);
			if (subscriptionState == null) {
				getLogger().error(MODULE, "Unable to subscribe addOn package for subscriber id: " + subscriberId
						+ ". Reason: Invalid subscription status received");
				return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid subscription status received", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		ProductOffer productOffer = null;

		if (isNullOrBlank(addOnProductOfferId) == false) {
			productOffer = getNVSMXPolicyRepository().getProductOffer().byId(addOnProductOfferId);
			if (productOffer == null) {
				getLogger().error(MODULE, "Unable to subscribe addon product offer. Reason: ACTIVE addOn not found with id: " + addOnProductOfferId);
				return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "ACTIVE addOn not found with id: " + addOnProductOfferId, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			currentPkgCurrency = productOffer.getCurrency();
			
		}

		if (isNullOrBlank(addOnProductOfferName) == false) {

			if (productOffer == null) {

				productOffer = getNVSMXPolicyRepository().getProductOffer().byName(addOnProductOfferName);
				if (productOffer == null) {
					getLogger().error(MODULE, "Unable to subscribe addon product offer. Reason: ACTIVE addOn not found with name: " + addOnProductOfferName);
					return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "ACTIVE addOn not found with name: " + addOnProductOfferName, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				currentPkgCurrency = productOffer.getCurrency();
			} else if (productOffer.getName().equals(addOnProductOfferName) == false) {

				getLogger().error(MODULE, "Unable to subscribe addon product offer. Reason: AddOn id(" + addOnProductOfferId + ") and name(" + addOnProductOfferName
						+ ") are not related ");
				return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "AddOn package id(" + addOnProductOfferId + ") and name(" + addOnProductOfferName
						+ ") are not related ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

		} else {
			if (productOffer == null) {
				getLogger().error(MODULE, "Unable to subscribe addon product offer. Reason: AddOn id or name must be provided: " + addOnProductOfferName);
				return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "AddOn package id or name must be provided", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if (productOffer.getPolicyStatus() != PolicyStatus.SUCCESS) {
			
			getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + ") for subscriber ID: " + subscriberId
					+ ". Reason: Product Offer AddOn(" + productOffer.getName() + ") is failed/partial fail addOn");
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Unable to subscribe addOn(" + productOffer.getName() + ") for subscriber ID: " + subscriberId
					+ ". Reason: AddOn(" + productOffer.getName() + ") is failed/partial fail addOn", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			
		}

		if(productOffer.isFnFOffer()==false){
			if(Collectionz.isNullOrEmpty(request.getFnFMembers())==false){
				getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + ") for subscriber ID: " + subscriberId
						+ ". Reason: FnF members can not be added to non FnF Product Offer AddOn(" + productOffer.getName() + ")");
				return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Unable to subscribe addOn(" + productOffer.getName() + ") for subscriber ID: " + subscriberId
						+ ". Reason: FnF members can not be added to non FnF Product Offer AddOn(" + productOffer.getName() + ")", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if(request.getStartTime() != null && request.getStartTime() == Long.MIN_VALUE){
			getLogger().error(MODULE, "Unable to subscribe addOn product offer. Reason: Invalid Start time received");
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		if( request.getEndTime() != null && request.getEndTime() == Long.MIN_VALUE){
			getLogger().error(MODULE, "Unable to subscribe addOn product offer. Reason: Invalid End time received");
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid End time received ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}


		SubscriptionMetadata subscriptionMetadata=null;
		if(Collectionz.isNullOrEmpty(request.getFnFMembers())==false) {
			subscriptionMetadata = new SubscriptionMetadata();

			for (String member : request.getFnFMembers()) {
				if (StringUtils.isNumeric(member) == false) {
					getLogger().error(MODULE, "Unable to subscribe addOn product offer. Reason: FnF Members should be numeric");
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "FnF Members should be numeric", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}
			if (isNullOrBlank(fnFGroupName) == false) {
				subscriptionMetadata.setFnFGroup(new FnFGroup(fnFGroupName, request.getFnFMembers()));
			} else {
				subscriptionMetadata.setFnFGroup(new FnFGroup(UUID.randomUUID().toString(), request.getFnFMembers()));
			}
		}

		List<Subscription> subscription = null;
		try {
			SubscriptionParameter subscriptionParameter;

			if (isNullOrBlank(subscriberId)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber ID not received");
				}
				
				String alternateId = request.getAlternateId();

				if (isNullOrBlank(alternateId)) {
					getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: Identity parameter missing");
					return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
							ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);

				if (sprInfo == null) {
					getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: Subscriber not found with alternate Id: " + alternateId);
					return new SubscriptionResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				// currency validation for subscribe Addon
				if(validateCurrency(sprInfo.getProductOffer(),currentPkgCurrency) == false) {
					getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: New AddOn product offer(" + productOffer.getName() + ") found with different currency: ");
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
							ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: New AddOn product offer(" + productOffer.getName() + ") found with different currency: ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				if(SubscriberStatus.INACTIVE.name().equals(sprInfo.getStatus())){
					getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: Subscriber status is InActive for alternate Id: " + alternateId);
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
							ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Status is InActive", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				if(sprInfo.getProfileExpiredHours() >= 0){
					getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: Subscriber Profile has expired for alternate Id: " + alternateId);
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
							ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Profile has expired", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscriptionParameter = setSubscriptionParameter(sprInfo, request.getParentId(), productOffer.getId(), subscriptionState.state,
						request.getStartTime(), request.getEndTime(), request.getPriority(), request.getParameter1(),
						request.getParameter2(), sprInfo.getBillingDate(), subscriptionMetadata);
				// This subscriber identity is set to use further in auto re-auth
			} else {
				
				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);
				
				if (sprInfo == null) {
					getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: Subscriber not found with subscriber Id: " + subscriberId);
					return new SubscriptionResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				// currency validation for subscribe Addon
				if(validateCurrency(sprInfo.getProductOffer(),currentPkgCurrency) == false) {
						getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: New AddOn product offer(" + productOffer.getName() + ") found with different currency: ");
						return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: New AddOn product offer(" + productOffer.getName() + ") found with different currency: ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				if(SubscriberStatus.INACTIVE.name().equals(sprInfo.getStatus())){
					getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: Subscriber status is InActive for subscriber Id: " + subscriberId);
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
							ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Status is InActive", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				if(sprInfo.getProfileExpiredHours() >= 0){
					getLogger().error(MODULE, "Unable to subscribe addOn product offer(" + productOffer.getName() + "). Reason: Subscriber Profile has expired for subscriber Id: " + subscriberId);
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
							ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Profile has expired", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscriptionParameter = setSubscriptionParameter(sprInfo, request.getParentId(), productOffer.getId(), subscriptionState.state,
						request.getStartTime(), request.getEndTime(), request.getPriority(), request.getParameter1(),
						request.getParameter2(), sprInfo.getBillingDate(),subscriptionMetadata);

			}

			double subscriptionPrice;
			if(updateBalanceIndication){
				if(productOffer.getSubscriptionPrice() > 0d){
					subscriptionPrice = productOffer.getSubscriptionPrice();
					SubscriberMonetaryBalance monetaryBalance = SubscriberDAO.getInstance().getMonetaryBalance(
							subscriptionParameter.getSprInfo().getSubscriberIdentity(), Predicates.ALL_MONETARY_BALANCE);
					MonetaryBalance availableMonetaryBalance = monetaryBalance.getMainBalance();

					if(isMonetaryBalanceAvailableAndActiveForCurrency(availableMonetaryBalance,productOffer.getCurrency())){
						double usableBalance = availableMonetaryBalance.getUsableBalance();
						if(usableBalance < subscriptionPrice){
							getLogger().error(MODULE, "Unable to subscribe addOn product offer. Reason: Balance insufficient.");
							return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
									"Balance insufficient."
									, null, null, null
									, request.getWebServiceName(), request.getWebServiceMethodName());
						}
						else{
							subscriptionParameter.setSubscriptionPrice(subscriptionPrice);
							subscriptionParameter.setMonetaryBalance(availableMonetaryBalance);
						}
					} else{
						getLogger().error(MODULE, "Unable to subscribe addOn product offer. Reason: No available and active balance found.");
						return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								"No available and active balance found."
								, null, null, null
								, request.getWebServiceName(), request.getWebServiceMethodName());
					}

				} else {
					getLogger().error(MODULE, "Unable to subscribe addOn product offer. Reason: Price not configured.");
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
							"Price not configured. Please check update Balance Indication flag OR configure Addon price."
							, null, null, null
							, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

			subscription = SubscriberDAO.getInstance()
					.subscribeAddOnProductOfferById(subscriptionParameter, adminStaff, requestIpAddress);

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while subscribing addOn product offer("+productOffer.getName()+") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new SubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while subscribing addOn product offer("+productOffer.getName()+") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			getLogger().trace(e);
			return new SubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		List<SubscriptionData> subscriptionData = createSubscriptionData(subscription);

		doReAuthForUpdateOperation(subscriberId, updateAction);

		return new SubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, subscriptionData, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}


	private SubscriptionParameter setSubscriptionParameter(SPRInfo sprInfo, String parentId, String productOfferId,
														   Integer subscriptionStatusValue, Long startTime, Long endTime,
														   Integer priority, String param1, String param2,
														   Integer billDay, SubscriptionMetadata metadata){

		SubscriptionParameter subscriptionParameter = new SubscriptionParameter();
		subscriptionParameter.setSprInfo(sprInfo);
		subscriptionParameter.setParentId(parentId);
		subscriptionParameter.setProductOfferId(productOfferId);
		subscriptionParameter.setSubscriptionStatusValue(subscriptionStatusValue);
		subscriptionParameter.setStartTime(startTime);
		subscriptionParameter.setEndTime(endTime);
		subscriptionParameter.setPriority(priority);
		subscriptionParameter.setParam1(param1);
		subscriptionParameter.setParam2(param2);
		subscriptionParameter.setBillDay(billDay);
		subscriptionParameter.setMetadata(metadata);

		return subscriptionParameter;
	}

	private TopUpSubscriptionResponse doSubscribeTopUp(SubscribeTopUpWSRequest request) {


		String subscriberId = request.getSubscriberId();
		String topUpId = request.getTopUpPackageId();
		String topUpName = request.getTopUpPackageName();
		String subscriptionStatusValue = request.getSubscriptionStatusValue();
		String subscriptionStatusName = request.getSubscriptionStatusName();
		Integer updateAction = request.getUpdateAction();
		Integer priority = request.getPriority();
		boolean updateBalanceIndication = request.getUpdateBalanceIndication();


		SubscriptionState subscriptionState = SubscriptionState.STARTED;
		if (isNullOrBlank(subscriptionStatusValue) && isNullOrBlank(subscriptionStatusName)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Considering subscription status: " + subscriptionState.getName()
						+ " for subscriber id: " + subscriberId + "). Reason: Subscription status not received");
			}
		} else {
			subscriptionState = getSubscriptionStatus(subscriptionStatusValue, subscriptionStatusName);
			if (subscriptionState == null) {
				getLogger().error(MODULE, "Unable to subscribe topUp package for subscriber id: " + subscriberId
						+ ". Reason: Invalid subscription status received");
				return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid subscription status received", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		QuotaTopUp topUp = null;

		if (isNullOrBlank(topUpId) == false) {
			topUp = getNVSMXPolicyRepository().getActiveQuotaTopUpById(topUpId);

			if (topUp == null) {
				getLogger().error(MODULE, "Unable to subscribe topUp. Reason: ACTIVE topUp not found with id: " + topUpId);
				return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "ACTIVE topUp not found with id: " + topUpId, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if (isNullOrBlank(topUpName) == false) {

			if (topUp == null) {

				topUp = getNVSMXPolicyRepository().getActiveQuotaTopUpByName(topUpName);
				if (topUp == null) {
					getLogger().error(MODULE, "Unable to subscribe topUp. Reason: ACTIVE topUp not found with name: " + topUpName);
					return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "ACTIVE topUp not found with name: " + topUpName, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			} else if (topUp.getName().equals(topUpName) == false) {

				getLogger().error(MODULE, "Unable to subscribe topup. Reason: TopUp id(" + topUpId + ") and name(" + topUpName
						+ ") are not related ");
				return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "TopUp package id(" + topUpId + ") and name(" + topUpName
						+ ") are not related ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

		} else {
			if (topUp == null) {
				getLogger().error(MODULE, "Unable to subscribe topup. Reason: TopUp id or name must be provided: " + topUpName);
				return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "TopUp package id or name must be provided", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if(request.getStartTime() != null && request.getStartTime() == Long.MIN_VALUE){
			getLogger().error(MODULE, "Unable to subscribe topUp. Reason: Invalid Start time received");
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		if( request.getEndTime() != null && request.getEndTime() == Long.MIN_VALUE){
			getLogger().error(MODULE, "Unable to subscribe topUp. Reason: Invalid End time received");
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid End time received ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(nonNull(priority) && ( priority < MIN_SUBSCRIPTION_PRIORITY || priority > MAX_SUBSCRIPTION_PRIORITY)){
			String errorMessage = " Reason: Invalid priority "+priority+" received. Priority should be between( "+MIN_SUBSCRIPTION_PRIORITY+" to "+MAX_SUBSCRIPTION_PRIORITY+" )";
			getLogger().error(MODULE, "Unable to subscribe topUp. "+errorMessage );
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,ResultCode.INVALID_INPUT_PARAMETER.name+"."+errorMessage,null,null,null,request.getWebServiceName(), request.getWebServiceMethodName());
		}


		Subscription subscription;
		double subscriptionPrice=0d;
		String monetaryBalanceId=null;
		SPRInfo sprInfo;
		String alternateId=null;
		try {
			if (isNullOrBlank(subscriberId)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber ID not received");
				}

				alternateId = request.getAlternateId();

				if (isNullOrBlank(alternateId)) {
					getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + "). Reason: Identity parameter missing");
					return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
							ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);

				TopUpSubscriptionResponse validate = validateSubscriberProfile(request, subscriberId, topUp, sprInfo, "alternate id");
				if (validate != null){
					return validate;
				}

			} else {

				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);

				TopUpSubscriptionResponse validate = validateSubscriberProfile(request, subscriberId, topUp, sprInfo, "subscriber id");
				if (validate != null){
					return validate;
				}

			}

			if(updateBalanceIndication){
				if(Objects.isNull(topUp.getPrice()) == false && topUp.getPrice() > 0d){
					subscriptionPrice = topUp.getPrice();
					SubscriberMonetaryBalance monetaryBalance = SubscriberDAO.getInstance().getMonetaryBalance(
							sprInfo.getSubscriberIdentity(), Predicates.ALL_MONETARY_BALANCE);
					MonetaryBalance availableMonetaryBalance = monetaryBalance.getMainBalance();

					if(isMonetaryBalanceAvailableAndActive(availableMonetaryBalance)){
						double usableBalance = availableMonetaryBalance.getUsableBalance();
						if(usableBalance < subscriptionPrice){
							getLogger().error(MODULE, "Unable to subscribe TopUp. Reason: Balance insufficient.");
							return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
									"Balance insufficient."
									, null, null, null
									, request.getWebServiceName(), request.getWebServiceMethodName());
						}else{
							monetaryBalanceId = availableMonetaryBalance.getId();
						}

					} else{
						getLogger().error(MODULE, "Unable to subscribe TopUp. Reason: No available and active balance found.");
						return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								"No available and active balance found."
								, null, null, null
								, request.getWebServiceName(), request.getWebServiceMethodName());
					}

				} else {
					getLogger().error(MODULE, "Unable to subscribe TopUp. Reason: Price not configured.");
					return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
							"Price not configured. Please check update Balance Indication flag OR configure TopUp price."
							, null, null, null
							, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

			if (isNullOrBlank(subscriberId)) {
				subscription = SubscriberDAO.getInstance()
						.subscribeTopUpByTopUpIdByAlternateId(alternateId, request.getParentId(), topUp.getId(), subscriptionState,
								request.getStartTime(), request.getEndTime(), request.getPriority(),subscriptionPrice,monetaryBalanceId,  adminStaff, request.getParameter1(), request.getParameter2());
			}else{
				subscription = SubscriberDAO.getInstance()
						.subscribeTopUpByName(subscriberId, request.getParentId(), topUp.getName(), subscriptionState,
								request.getStartTime(), request.getEndTime(), request.getPriority(),subscriptionPrice ,monetaryBalanceId , adminStaff, request.getParameter1(), request.getParameter2());
			}


		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while subscribing topUp("+topUp.getName()+") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new TopUpSubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while subscribing topUp("+topUp.getName()+") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			getLogger().trace(e);
			return new TopUpSubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		TopUpSubscriptionData topUpSubscriptionData = createTopUpSubscriptionData(subscription);

		try {
			ReAuthUtil.doReAuthBySubscriberId(subscription.getSubscriberIdentity(), updateAction);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while performing re-auth for subscriber Id:" + subscription.getSubscriberIdentity() + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}

		return new TopUpSubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, CollectionUtil.newArrayListWithElements(topUpSubscriptionData), null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}


	private TopUpSubscriptionResponse validateSubscriberProfile(SubscribeTopUpWSRequest request,
																String id,
																QuotaTopUp topUp,
																SPRInfo sprInfo,
																String message) {
		if (sprInfo == null) {
			getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + "). Reason: Subscriber not found with "+message+": " + id);
			return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		ProductOffer productOffer = getNVSMXPolicyRepository().getProductOffer().byName(sprInfo.getProductOffer());
		if(Objects.isNull(productOffer)){
			getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + ") for "+message+": "+id+". Reason: Base product offer is not found");
			return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: Base product offer is not found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		BasePackage basePackage = (BasePackage) productOffer.getDataServicePkgData();
		if (Objects.isNull(basePackage)) {
			getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + ") for "+message+": "+id+". Reason: Product offer does not have Base package attached");
			return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: Product offer does not have Base package attached", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (basePackage.isQuotaProfileExist() == false) {
			getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + ") for "+message+": "+id+". Reason: Base package does not have any quota profile attached");
			return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: Base package does not have any quota profile attached", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(subscribingInvalidTopUpPackage(topUp,basePackage)){
			getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + "). Reason: Top up is not applicable for subscriber's base Package(" +basePackage.getName()+ ")");
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
					ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Top up is not applicable for subscriber's base Package(" +basePackage.getName()+ ")", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(SubscriberStatus.INACTIVE.name().equals(sprInfo.getStatus())){
			getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + "). Reason: Subscriber status is InActive for "+message+": " + id);
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
					ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Status is InActive", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(sprInfo.getProfileExpiredHours() >= 0){
			getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + "). Reason: Subscriber Profile has expired for "+message+": " + id);
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
					ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Profile has expired", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		return null;
	}

	private boolean subscribingInvalidTopUpPackage(QuotaTopUp topUp, BasePackage basePackage) {
		if (CollectionUtils.isEmpty(topUp.getApplicablePCCProfiles())) {
			return false;
		}
		List<String> basePackagePCCProfileNames = basePackage.getQoSProfiles().stream().map(QoSProfile::getName).collect(Collectors.toList());
		for (String pccProfileName : basePackagePCCProfileNames) {
			if (topUp.isTopUpIsEligibleToApply(pccProfileName)) {
				return false;
			}
		}
		return true;
	}

	private PolicyRepository getNVSMXPolicyRepository() {
		return DefaultNVSMXContext.getContext().getPolicyRepository();
	}

	public SubscriptionResponse getAddOnSubscriptions(ListSubscriptionWSRequest request) {

		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preListAddOnSubscriptions(request);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SubscriptionResponse response = doGetAddOnSubscriptions(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postListAddOnSubscriptions(request, response);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public TopUpSubscriptionResponse getTopUpSubscriptions(ListSubscriptionWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preListTopUpSubscriptions(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		TopUpSubscriptionResponse response = doGetTopUpSubscriptions(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postListTopUpSubscriptions(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private SubscriptionResponse doGetAddOnSubscriptions(ListSubscriptionWSRequest request) {

		String subscriberId = request.getSubscriberId();
		String subscriptionStatusValue = request.getSubscriptionStatusValue();
		String subscriptionStatusName = request.getSubscriptionStatusName();
		String parameter1 = request.getParameter1();
		String parameter2 = request.getParameter2();

		String alternateId = null;
		if (isNullOrBlank(subscriberId)) {
			LogManager.getLogger().debug(MODULE,"Subscriber ID not received");
			alternateId = request.getAlternateId();

			if (isNullOrBlank(alternateId)) {
				LogManager.getLogger().error(MODULE, "Unable to fetch subscription. Reason: Identity parameter missing");
				return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
						ResultCode.INPUT_PARAMETER_MISSING.name +  ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}



		List<SubscriptionData> subscriptions;

		if (isNullOrBlank(subscriptionStatusValue) && isNullOrBlank(subscriptionStatusName)) {
			try {
				subscriptions = getAddOnSubscriptionDatas(subscriberId, alternateId, null);
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while fetching addOn subscriptions with subscriber id :" + subscriberId + ". Reason: " + e.getMessage());
				if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE, e);
				}
				return new SubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			} catch (Exception e) {
				getLogger().error(MODULE, "Error while fetching addOn subscriptions with subscriber id :" + subscriberId + ". Reason: " + e.getMessage());
				getLogger().trace(e);
				return new SubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			return new SubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, subscriptions, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		SubscriptionState subscriptionState = getSubscriptionStatus(subscriptionStatusValue, subscriptionStatusName);
		if (subscriptionState == null) {
			getLogger().error(MODULE, "Unable to fetch  addOn package subscriptions. Reason: Invalid subscription status received");
			int responseCode = ResultCode.INPUT_PARAMETER_MISSING.code;
			return new SubscriptionResponse(responseCode, "Invalid subscription status received", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		try {
			subscriptions = getAddOnSubscriptionDatas(subscriberId, alternateId, subscriptionState);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching addOn subscriptions with subscriber id :" + subscriberId + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new SubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching addOn subscriptions with subscriber id :" + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(e);
			return new SubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (Collectionz.isNullOrEmpty(subscriptions)) {
			return new SubscriptionResponse(ResultCode.SUCCESS.code, "No addOn subscribed for subscriber Id: " + subscriberId + "and status: "
					+ subscriptionState, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		return new SubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, subscriptions, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}


	private TopUpSubscriptionResponse doGetTopUpSubscriptions(ListSubscriptionWSRequest request) {

		String subscriberId = request.getSubscriberId();
		String subscriptionStatusValue = request.getSubscriptionStatusValue();
		String subscriptionStatusName = request.getSubscriptionStatusName();

		String alternateId = null;
		if (isNullOrBlank(subscriberId)) {
			LogManager.getLogger().debug(MODULE,"Subscriber ID not received");
			alternateId = request.getAlternateId();

			if (isNullOrBlank(alternateId)) {
				LogManager.getLogger().error(MODULE, "Unable to fetch subscription. Reason: Identity parameter missing");
				return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
						ResultCode.INPUT_PARAMETER_MISSING.name +  ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		List<TopUpSubscriptionData> subscriptions;

		if (isNullOrBlank(subscriptionStatusValue) && isNullOrBlank(subscriptionStatusName)) {
			try {
				subscriptions = getTopUpSubscriptionDatas(subscriberId, alternateId, null);
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while fetching topUp subscriptions with subscriber id :" + subscriberId + ". Reason: " + e.getMessage());
				if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE, e);
				}
				return new TopUpSubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
			} catch (Exception e) {
				getLogger().error(MODULE, "Error while fetching topUp subscriptions with subscriber id :" + subscriberId + ". Reason: " + e.getMessage());
				getLogger().trace(e);
				return new TopUpSubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
			}


			return new TopUpSubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, subscriptions, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		SubscriptionState subscriptionState = getSubscriptionStatus(subscriptionStatusValue, subscriptionStatusName);
		if (subscriptionState == null) {
			getLogger().error(MODULE, "Unable to fetch  topUp package subscriptions. Reason: Invalid subscription status received");
			int responseCode = ResultCode.INPUT_PARAMETER_MISSING.code;
			return new TopUpSubscriptionResponse(responseCode, "Invalid subscription status received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		try {
			subscriptions = getTopUpSubscriptionDatas(subscriberId, alternateId, subscriptionState);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching topUp subscriptions with subscriber id :" + subscriberId + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new TopUpSubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching topUp subscriptions with subscriber id :" + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(e);
			return new TopUpSubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (Collectionz.isNullOrEmpty(subscriptions)) {

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Active Subscriptions are not exist with subscriber ID: " + subscriberId);
			}
		}

		return new TopUpSubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, subscriptions, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
	}

	public SubscriptionResponse changeDataAddOnSubscription(ChangeDataAddOnSubscriptionWSRequest request) {

		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preChangeAddOnSubscription(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SubscriptionResponse response = doChangeDataAddOnSubscription(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postChangeAddOnSubscription(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public TopUpSubscriptionResponse changeTopUpSubscription(ChangeTopUpSubscriptionWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preChangeTopUpSubscription(request);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		TopUpSubscriptionResponse response = doChangeTopUpSubscription(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postChangeTopUpSubscription(request, response);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private SubscriptionResponse doChangeDataAddOnSubscription(ChangeDataAddOnSubscriptionWSRequest request) {

		String subscriberId = request.getSubscriberId();
		String dataAddOnSubscriptionId = request.getDataAddOnSubscriptionId();
		String subscriptionStatusValue = request.getSubscriptionStatusValue();
		String subscriptionStatusName = request.getSubscriptionStatusName();
		String dataAddOnProductOfferName = request.getDataAddOnProductOfferName();
		String subscriptionOrder = request.getSubscriptionOrder();
		Integer updateAction = request.getUpdateAction();


		ProductOffer productOffer = null;

		if (isNullOrBlank(dataAddOnSubscriptionId)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Trying to change data addon subscription by addon name. Reason: Data addon subscription id not received");
			}
			if (isNullOrBlank(dataAddOnProductOfferName)) {
				getLogger().error(MODULE, "Unable to change data addon subscription. Reason: Data addon subscription id or addon name not received");
				return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Data addon subscription id or addon name not received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
			}else{
				productOffer = getProductOfferByName(dataAddOnProductOfferName);

				if (productOffer == null) {
					getLogger().error(MODULE, "Unable to change data addon subscription. Reason: ACTIVE data addon not found with name: " + dataAddOnProductOfferName);
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "ACTIVE data addon not found with name: " + dataAddOnProductOfferName, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				if(productOffer.getDataServicePkgData() == null){
					getLogger().error(MODULE, "Unable to change addOn subscription. Reason: Received addOn subscription has no data addOn attached with it");
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Received addOn subscription has no data addOn attached with it", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

			if (isNullOrBlank(subscriptionOrder)) {
				getLogger().error(MODULE, "Unable to change data addon subscription. Reason: Data addon product offer order(OLDEST/LATEST) not received");
				return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Data addon product offer order(OLDEST/LATEST) not received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}



		if (isNullOrBlank(subscriptionStatusValue) && isNullOrBlank(subscriptionStatusName)) {
			getLogger().error(MODULE, "Unable to change addOn subscription" + ". Reason: Subscription status not received");
			return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Subscription status not received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		SubscriptionState newSubscriptionState = getSubscriptionStatus(subscriptionStatusValue, subscriptionStatusName);

		if (newSubscriptionState == null) {
			getLogger().error(MODULE, "Unable to change data addon subscription for id: " + dataAddOnSubscriptionId
					+ "). Reason: Invalid subscription status received");
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid subscription status received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		} else if (newSubscriptionState != SubscriptionState.STARTED
				&& newSubscriptionState != SubscriptionState.UNSUBSCRIBED) {
			getLogger().error(MODULE, "Unable to change data addOn subscription for addOn subscription id: " + dataAddOnSubscriptionId
					+ "). Reason: Invalid subscription status received");
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Subscription Status received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(request.getStartTime()!= null && request.getStartTime() == Long.MIN_VALUE) {
			getLogger().error(MODULE, "Unable to change data addOn subscription. Reason: Invalid Start time received");
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received ", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}
		if( request.getEndTime()!= null && request.getEndTime() == Long.MIN_VALUE) {
			getLogger().error(MODULE, "Unable to change data addOn subscription. Reason: Invalid End time received");
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid End time received ", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(nonNull(request.getPriority()) && request.getPriority() < 1) {
			getLogger().error(MODULE, "Unable to subscribe addOn. Reason: Invalid priority value");
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Priority shoudld be greater than 1 ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (isNullOrBlank(dataAddOnSubscriptionId)) {
			return updateSubscriptionByProductOfferName(request, subscriberId, dataAddOnProductOfferName, updateAction, newSubscriptionState);
		} else {
			return updateSubscriptionBySubscriptionId(request, subscriberId, updateAction, dataAddOnSubscriptionId, dataAddOnProductOfferName, newSubscriptionState);
		}
	}

	private SubscriptionResponse updateSubscriptionBySubscriptionId(ChangeDataAddOnSubscriptionWSRequest request, String subscriberId, Integer updateAction, String dataAddOnSubscriptionId, String dataAddOnProductOfferName, SubscriptionState newSubscriptionState) {

		String requestIpAddress = request.getRequestIpAddress();
		Subscription subscription = null;
		try {
			if (isNullOrBlank(subscriberId)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Subscriber ID not received");
				}
				String alternateId = request.getAlternateId();

				if (isNullOrBlank(alternateId)) {
					LogManager.getLogger().error(MODULE, "Unable to data addon subscription. Reason: Identity parameter missing");
					return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
							ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				if (isNullOrBlank(dataAddOnProductOfferName) == false) {

					subscription = SubscriberDAO.getInstance().getSubscribedAddOnByAlternateId(alternateId, dataAddOnSubscriptionId, adminStaff);

					if (subscription == null) {
						return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Data addon subscription not found by alternateId: " + alternateId, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					ProductOffer productOffer = getProductOfferById(subscription.getProductOfferId());

					if (productOffer.getName().equals(dataAddOnProductOfferName) == false) {
						getLogger().error(MODULE, "Error while changing data addon subscription with subscription(id:" + dataAddOnSubscriptionId
								+ ") for subscriber ID: " + subscriberId + ". Reason: addOn name: " + productOffer.getName()
								+ " from subscription and provided addOn name: "
								+ dataAddOnProductOfferName
								+ " do not match");
						return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Data addon name: " + productOffer.getName()
								+ " from subscription and provided addOn name: " + dataAddOnProductOfferName
								+ " do not match", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					if (productOffer.getDataServicePkgData() == null) {
						getLogger().error(MODULE, "Unable to change data addOn subscription. Reason: Received addOn subscription has no data addOn attached with it");
						return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Received addOn subscription has no data addOn attached with it", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}

				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);
				subscription = SubscriberDAO.getInstance().changeAddOnSubscription(sprInfo, dataAddOnSubscriptionId, newSubscriptionState
						, request.getStartTime(), request.getEndTime(), request.getPriority(), request.getRejectReason(), adminStaff, request.getParameter1(), request.getParameter2(), requestIpAddress);

			} else {

				if (isNullOrBlank(dataAddOnProductOfferName) == false) {

					subscription = SubscriberDAO.getInstance().getSubscribedAddOnBySubscriberId(subscriberId, dataAddOnSubscriptionId, adminStaff);

					if (subscription == null) {
						return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Data addon subscription not found by subscriberId: " + subscriberId, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					ProductOffer productOffer = getProductOfferById(subscription.getProductOfferId());

					if (productOffer.getName().equals(dataAddOnProductOfferName) == false) {
						getLogger().error(MODULE, "Error while changing data addon subscription with subscription(id:" + dataAddOnSubscriptionId
								+ ") for subscriber ID: " + subscriberId + ". Reason: addOn name: " + productOffer.getName()
								+ " from subscription and provided data addon subscription name: "
								+ dataAddOnProductOfferName
								+ " do not match");
						return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Data addon name: " + productOffer.getName()
								+ " from subscription and provided data addon subscription name: " + dataAddOnProductOfferName
								+ " do not match", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					if (productOffer.getDataServicePkgData() == null) {
						getLogger().error(MODULE, "Unable to change data addOn subscription. Reason: Received addOn subscription has no data addOn attached with it");
						return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Received addOn subscription has no data addOn attached with it", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}

				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);
				subscription = SubscriberDAO.getInstance().changeAddOnSubscription(sprInfo, dataAddOnSubscriptionId, newSubscriptionState
						, request.getStartTime(), request.getEndTime(), request.getPriority(), request.getRejectReason(), adminStaff, request.getParameter1(), request.getParameter2(), requestIpAddress);
			}

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while changing data addon subscription with subscription Id:" + dataAddOnSubscriptionId + ". Reason:"
					+ e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new SubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while changing data addon subscription with subscription Id :" + dataAddOnSubscriptionId + ". Reason:"
					+ e.getMessage());
			getLogger().trace(e);
			return new SubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (subscription == null) {
			getLogger().error(MODULE, "Data addon subscription not found with susbcriberId( " + subscriberId + ") and subscriptionId(" + dataAddOnSubscriptionId + ")");
			return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Data addon subscription not found with susbcriberId( " + subscriberId + ") and subscriptionId(" + dataAddOnSubscriptionId + ")", null, null,
					null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		List<Subscription> subscriptions = Collectionz.newArrayList();
		subscriptions.add(subscription);
		List<SubscriptionData> subscriptionData = createSubscriptionData(subscriptions);

		doReAuthForUpdateOperation(subscription.getSubscriberIdentity(), updateAction);

		return new SubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, subscriptionData, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	private SubscriptionResponse updateSubscriptionByProductOfferName(ChangeDataAddOnSubscriptionWSRequest request, String subscriberId,
																	  String dataAddOnProductOfferName, Integer updateAction,
																	  SubscriptionState newSubscriptionState) {
		String requestIpAddress = request.getRequestIpAddress();
		Subscription subscription = null;

		try {
			SubscriptionOrder subscriptionOrder = SubscriptionOrder.fromVal(request.getSubscriptionOrder());
			if (subscriptionOrder == null) {
				return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid subscription order: " + request.getSubscriptionOrder() + ", Possible options: OLDEST/LATEST", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
			}
			if (isNullOrBlank(subscriberId)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber ID not received");
				}

				String alternateId = request.getAlternateId();

				if (isNullOrBlank(alternateId)) {
					getLogger().error(MODULE, "Unable to change data addon (" + dataAddOnProductOfferName + ") subscription. Reason: Identity parameter missing");
					return new SubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
							ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);

				SubscriptionResponse subscriptionResponse = checkSubscriberProfileInfo(dataAddOnProductOfferName, alternateId, sprInfo, request);
				if (subscriptionResponse != null){
					return subscriptionResponse;
				}

				List<Subscription> subscriptions = SubscriberDAO.getInstance().getSubscriptionsByAlternateId(alternateId, adminStaff);

				if (Collectionz.isNullOrEmpty(subscriptions)) {
					return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Unable to change data addon ("+
							dataAddOnProductOfferName + ") subscription. Reason: No addOn subcription found with alternateId: " + alternateId, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscription = subscriptionOrder.getSubscription(subscriptions, dataAddOnProductOfferName);
				if (subscription == null) {
					return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Unable to change data addon ("
							+ dataAddOnProductOfferName + ") subscription. Reason: AddOn subcription not found with name : "
							+ dataAddOnProductOfferName , null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				if(subscription.getType() != SubscriptionType.ADDON){
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Unable to change data addon ("
							+ dataAddOnProductOfferName + ") subscription. Reason: Subcription of "+ subscription.getType() +" type found. Data AddOn Subscription type is required" , null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscription = SubscriberDAO.getInstance().changeAddOnSubscription(sprInfo, subscription.getId()
						, newSubscriptionState, request.getStartTime(), request.getEndTime(), request.getPriority(), request.getRejectReason()
						, adminStaff, request.getParameter1(), request.getParameter2(), requestIpAddress);

			} else {

				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);

				SubscriptionResponse subscriptionResponse = checkSubscriberProfileInfo(dataAddOnProductOfferName, subscriberId, sprInfo, request);
				if (subscriptionResponse != null){
					return subscriptionResponse;
				}

				List<Subscription> addOnSubscriptions = SubscriberDAO.getInstance().getSubscriptions(subscriberId, adminStaff);

				if (Collectionz.isNullOrEmpty(addOnSubscriptions)) {
					return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Unable to change data addon ("
							+ dataAddOnProductOfferName + ") subscription. Reason: No addOn subcription found with subscriberId: " + subscriberId, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscription = subscriptionOrder.getSubscription(addOnSubscriptions, dataAddOnProductOfferName);

				if (subscription == null) {
					return new SubscriptionResponse(ResultCode.NOT_FOUND.code, "Unable to change data addon ("
							+ dataAddOnProductOfferName + ") subscription. Reason: AddOn subcription not found with name : " + dataAddOnProductOfferName , null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				if(subscription.getType() != SubscriptionType.ADDON){
					return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Unable to change data addon ("
							+ dataAddOnProductOfferName + ") subscription. Reason: Subcription of "+ subscription.getType() +" type found. AddOn type is required" , null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscription = SubscriberDAO.getInstance().changeAddOnSubscription(sprInfo, subscription.getId()
						, newSubscriptionState, request.getStartTime(), request.getEndTime(), request.getPriority(), request.getRejectReason()
						, adminStaff, request.getParameter1(), request.getParameter2(), requestIpAddress);

			}
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while changing data addon ("+dataAddOnProductOfferName+") subscription for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new SubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while changing data addon ("+dataAddOnProductOfferName+") subscription for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			getLogger().trace(e);
			return new SubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		List<Subscription> subscriptionDatas = Collectionz.newArrayList();
		subscriptionDatas.add(subscription);
		List<SubscriptionData> subscriptionData = createSubscriptionData(subscriptionDatas);

		doReAuthForUpdateOperation(subscriberId, updateAction);

		return new SubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, subscriptionData, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	private TopUpSubscriptionResponse doChangeTopUpSubscription(ChangeTopUpSubscriptionWSRequest request) {

		String subscriberId = request.getSubscriberId();
		String topUpSubscriptionId = request.getTopUpSubscriptionId();
		String subscriptionStatusValue = request.getSubscriptionStatusValue();
		String subscriptionStatusName = request.getSubscriptionStatusName();
		String topUpName = request.getTopUpName();
		String subscriptionOrder = request.getSubscriptionOrder();
		boolean subscriptionIdReceived = true;
		Integer updateAction = request.getUpdateAction();

		if (isNullOrBlank(topUpSubscriptionId)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Trying to change topUp subscription by topUp name. Reason: topUp subscription id not received");
			}
			if (isNullOrBlank(topUpName)) {
				getLogger().error(MODULE, "Unable to change topUp subscription. Reason: TopUp subscription id or topUp name not received");
				return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "TopUp subscription id or topUp name not received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
			}

			if (isNullOrBlank(subscriptionOrder)) {
				getLogger().error(MODULE, "Unable to change topUp subscription. Reason: TopUp subscription order(OLDEST/LATEST) not received");
				return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "TopUp subscription order(OLDEST/LATEST) not received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
			}
			subscriptionIdReceived = false;
		}

		if (isNullOrBlank(subscriptionStatusValue) && isNullOrBlank(subscriptionStatusName)) {
			getLogger().error(MODULE, "Unable to change topUp subscription" + ". Reason: Subscription status not received");
			return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Subscription status not received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		SubscriptionState newSubscriptionState = getSubscriptionStatus(subscriptionStatusValue, subscriptionStatusName);
		if (newSubscriptionState == null) {
			getLogger().error(MODULE, "Unable to change topUp subscription for topUp subscription id: " + topUpSubscriptionId
					+ "). Reason: Invalid subscription status received");
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid subscription status received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		} else if (newSubscriptionState != SubscriptionState.STARTED
				&& newSubscriptionState != SubscriptionState.UNSUBSCRIBED
				&& newSubscriptionState != SubscriptionState.REJECTED) {
			getLogger().error(MODULE, "Unable to change topUp subscription for topUp subscription id: " + topUpSubscriptionId
					+ "). Reason: Invalid subscription status received");
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Subscription Status received", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(request.getStartTime()!= null && request.getStartTime() == Long.MIN_VALUE) {
			getLogger().error(MODULE, "Unable to subscribe topUp. Reason: Invalid Start time received");
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received ", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}
		if( request.getEndTime()!= null && request.getEndTime() == Long.MIN_VALUE) {
			getLogger().error(MODULE, "Unable to subscribe topUp. Reason: Invalid End time received");
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid End time received ", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		String parameter1 = request.getParameter1();
		String parameter2 = request.getParameter2();
		Long startTime = request.getStartTime();
		Long endTime = request.getEndTime();
		String rejectReason = request.getRejectReason();

		if (subscriptionIdReceived == false) {
			return changeSubscriptionByTopUpName(request, subscriberId, updateAction, topUpName, newSubscriptionState, parameter1, parameter2, startTime, endTime, rejectReason);
		} else {
			return changeSubscriptionByTopUpSubscriptionId(request, subscriberId, updateAction, topUpSubscriptionId, topUpName, newSubscriptionState, startTime, endTime, rejectReason);
		}

	}

	private TopUpSubscriptionResponse changeSubscriptionByTopUpName(ChangeTopUpSubscriptionWSRequest request, String subscriberId, Integer updateAction, String topUpName,
																	SubscriptionState newSubscriptionState, String parameter1, String parameter2, Long startTime, Long endTime, String rejectReason) {
		Subscription subscription;

		SubscriptionOrder subscriptionOrder = SubscriptionOrder.fromVal(request.getSubscriptionOrder());
		if (subscriptionOrder == null) {
			return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid subscription order: " + request.getSubscriptionOrder() + ", Possible options: OLDEST/LATEST", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}
		try {
			if (isNullOrBlank(subscriberId)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Subscriber ID not received");
				}
				String alternateId = request.getAlternateId();

				if (isNullOrBlank(alternateId)) {
					LogManager.getLogger().error(MODULE, "Unable to change topUp subscription. Reason: Identity parameter missing");
					return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
							ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				List<Subscription> subscriptions = SubscriberDAO.getInstance().getTopUpSubscriptionsByAlternateId(alternateId, adminStaff);

				if (Collectionz.isNullOrEmpty(subscriptions)) {
					return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code, "No topUp subcription found with alternateId: " + alternateId, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscription = subscriptionOrder.getTopUpSubscription(subscriptions, topUpName);
				if (subscription == null) {
					if(getLogger().isDebugLogLevel()){
						LogManager.getLogger().debug(MODULE, "Unable to change topUp Subscription. Reason: TopUp subscription not found with name: " + topUpName+" for alternate id: " + alternateId);
					}
					return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code, "TopUp subcription not found with name : " + topUpName , null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscription = SubscriberDAO.getInstance().changeTopUpSubscriptionByAlternateId(alternateId, subscription.getId()
						, newSubscriptionState, startTime, endTime, request.getPriority(), rejectReason
						, adminStaff, parameter1, parameter2);

			} else {

				List<Subscription> topUpSubscriptions = SubscriberDAO.getInstance().getTopUpSubscriptions(subscriberId, adminStaff);

				if (Collectionz.isNullOrEmpty(topUpSubscriptions)) {
					if(getLogger().isDebugLogLevel()){
						LogManager.getLogger().debug(MODULE, "Unable to change topUp Subscription. Reason: TopUp subscription not found with name: " + topUpName + " for subscriber id: " + subscriberId);
					}
					return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code, "No TopUp subcription found with subscriberId: " + subscriberId, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscription = subscriptionOrder.getTopUpSubscription(topUpSubscriptions, topUpName);

				if (subscription == null) {
					if(getLogger().isDebugLogLevel()){
						LogManager.getLogger().debug(MODULE, "Unable to change topUp Subscription. Reason: TopUp subscription not found with name: " + topUpName + " for subscriber id: " + subscriberId);
					}
					return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code, "TopUp subcription not found with name : " + topUpName , null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscription = SubscriberDAO.getInstance().changeTopUpSubscription(subscriberId, subscription.getId()
						, newSubscriptionState, startTime, endTime, request.getPriority(), rejectReason
						, adminStaff, parameter1, parameter2);

			}
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while changing topUp subscription with topUp name:" + topUpName + ". Reason:"
					+ e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new TopUpSubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while changing topUp subscription with topUp name:" + topUpName + ". Reason:"
					+ e.getMessage());
			getLogger().trace(e);
			return new TopUpSubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (subscription == null) {
			getLogger().error(MODULE, "TopUp subscription not found with topUp name:" + topUpName);
			return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code, "TopUp subscription not found with topUp name:" + topUpName, null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
		}

		TopUpSubscriptionData subscriptionData = createTopUpSubscriptionData(subscription);

		doReAuthForUpdateOperation(subscription.getSubscriberIdentity(), updateAction);

		return new TopUpSubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, CollectionUtil.newArrayListWithElements(subscriptionData), null, null,request.getWebServiceName(), request.getWebServiceMethodName());
	}

	private TopUpSubscriptionResponse changeSubscriptionByTopUpSubscriptionId(ChangeTopUpSubscriptionWSRequest request, String subscriberId,
																			  Integer updateAction, String topUpSubscriptionId, String topUpName, SubscriptionState newSubscriptionState,
																			  Long startTime, Long endTime, String rejectReason) {
		Subscription subscription = null;
		try {
			if (isNullOrBlank(subscriberId)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Subscriber ID not received");
				}
				String alternateId = request.getAlternateId();

				if (isNullOrBlank(alternateId)) {
					LogManager.getLogger().error(MODULE, "Unable to change subscription. Reason: Identity parameter missing");
					return new TopUpSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
							ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}


				if (isNullOrBlank(topUpName) == false) {

					subscription = SubscriberDAO.getInstance().getTopUpSubscriptionByAlternateId(alternateId, topUpSubscriptionId, adminStaff);

					if (subscription == null) {
						if (getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Unable to change topUp Subscription. Reason: TopUp subscription not found with subscription Id: " + topUpSubscriptionId + " for alternate id: " + alternateId);
						}
						return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code, "TopUp subcription not found by alternateId: " + alternateId, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}
				subscription = SubscriberDAO.getInstance()
						.changeTopUpSubscriptionByAlternateId(alternateId, topUpSubscriptionId, newSubscriptionState,
								startTime, endTime, request.getPriority(), rejectReason, adminStaff, request.getParameter1(), request.getParameter2());

			} else {

				if (isNullOrBlank(topUpName) == false) {

					subscription = SubscriberDAO.getInstance().getTopUpSubscriptionBySubscriberId(subscriberId, topUpSubscriptionId, adminStaff);

					if (subscription == null) {
						if (getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Unable to change topUp Subscription. Reason: TopUp subscription not found with subscription Id: " + topUpSubscriptionId + " for subscriber id: " + subscriberId);
						}
						return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code, "TopUp subcription not found by subscriberId: " + subscriberId, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					QuotaTopUp subscriptionPackage = getNVSMXPolicyRepository().getQuotaTopUpById(subscription.getPackageId());

					if (subscriptionPackage.getName().equals(topUpName) == false) {
						getLogger().error(MODULE, "Error while changing topUp subscription with subscription(id:" + topUpSubscriptionId
								+ ") for subscriber ID: " + subscriberId + ". Reason: topUp name: " + subscriptionPackage.getName() + " from subscription and provided topUp name: " + topUpName
								+ " do not match");
						return new TopUpSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,  "TopUp name: " + subscriptionPackage.getName() + " from subscription and provided addOn name: " + topUpName
								+ " do not match", null, null, null,request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}
				subscription = SubscriberDAO.getInstance().changeTopUpSubscription(subscriberId, topUpSubscriptionId, newSubscriptionState
						, startTime, endTime, request.getPriority(), rejectReason, adminStaff, request.getParameter1(), request.getParameter2());
			}

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while changing topUp subscription with subscription Id:" + topUpSubscriptionId + ". Reason:"
					+ e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new TopUpSubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while changing topUp subscription with subscription Id :" + topUpSubscriptionId + ". Reason:"
					+ e.getMessage());
			getLogger().trace(e);
			return new TopUpSubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (subscription == null) {
			getLogger().error(MODULE, "TopUp subscription not found with susbcriberId( " + subscriberId + ") and subscriptionId(" + topUpSubscriptionId + ")");
			return new TopUpSubscriptionResponse(ResultCode.NOT_FOUND.code, "TopUp subscription not found with susbcriberId( " + subscriberId + ") and subscriptionId(" + topUpSubscriptionId + ")", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		TopUpSubscriptionData subscriptionData = null;

		doReAuthForUpdateOperation(subscription.getSubscriberIdentity(), updateAction);

		return new TopUpSubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, CollectionUtil.newArrayListWithElements(subscriptionData), null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	public UpdateMonetaryBalanceResponse updateMonetaryBalance(UpdateMonetaryBalanceRequest request) {
		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preUpdateBalanceEnquiry(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		UpdateMonetaryBalanceResponse response = doUpdateMonetaryBalance(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postUpdateBalanceEnquiry(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private UpdateMonetaryBalanceResponse doUpdateMonetaryBalance(UpdateMonetaryBalanceRequest request) {
		String parameter1 = request.getParameter1();
		String parameter2 = request.getParameter2();
		String serviceId = request.getServiceId();
		String serviceName = request.getServiceName();

		ValidationData validationData = request.validateParameters();
		if(validationData!=null){
			LogManager.getLogger().error(MODULE, "Unable to add monetary balance. " +
					"Reason: "+validationData.getMessage());
			return new UpdateMonetaryBalanceResponse(validationData.getCode().code,
					validationData.getCode().name + " Reason: "+validationData.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		OperationType operation = OperationType.fromVal(request.getOperation());
		if(isNullOrBlank(request.getOperation())==false && operation==null){
			getLogger().error(MODULE, "Invalid input parameter. Reason: Invalid parameter value for operation, valid values are "+OperationType.getNames());
			return new UpdateMonetaryBalanceResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid input parameter. Reason: Invalid parameter value for operation, valid values are "+OperationType.getNames(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		PolicyRepository policyRepository = DefaultNVSMXContext.getContext().getPolicyRepository();

		Service serviceData = null;

		if(isNullOrBlank(request.getBalanceId())){
			if(isNullOrBlank(serviceId) == false){
				serviceData = policyRepository.getService().byId(serviceId);

				if(serviceData == null){
					LogManager.getLogger().error(MODULE, "Unable to update monetary balance. Reason: Invalid serviceId");
					return new UpdateMonetaryBalanceResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + " Reason: : Invalid serviceId", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				if(isNullOrBlank(serviceName)==false && serviceName.equals(serviceData.getName())==false){
					LogManager.getLogger().error(MODULE, "Unable to update monetary balance. Reason: serviceId and serviceName does not refer to same service");
					return new UpdateMonetaryBalanceResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + " Reason: : serviceId and serviceName does not refer to same service", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			} else {
				if(isNullOrBlank(serviceName) || Objects.equals(CommonConstants.ALL_SERVICE_DISPLAY_VALUE, serviceName)){
					serviceData = new Service(null, CommonConstants.ALL_SERVICE_DISPLAY_VALUE, PkgStatus.ACTIVE);
				}else {

					serviceData = policyRepository.getService().byName(serviceName);
					if (serviceData == null) {
						LogManager.getLogger().error(MODULE, "Unable to update monetary balance. Reason: Invalid serviceName");
						return new UpdateMonetaryBalanceResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + " Reason: : Invalid serviceName", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}
			}
		}

		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();

		try{
			SPRInfo sprInfo;
			if (isNullOrBlank(subscriberIdentity) == false) {
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);
				if(isNull(sprInfo)){
					LogManager.getLogger().error(MODULE, "Subscriber ID not found.");
					return new UpdateMonetaryBalanceResponse(ResultCode.NOT_FOUND.code, "Reason: Subscriber not found with subscriber identity: "+subscriberIdentity, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				if (isNullOrBlank(alternateIdentity) == false) {
					String subscriberIdByAlternateId = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateIdentity, adminStaff);
					if(subscriberIdentity.equals(subscriberIdByAlternateId)==false){
						LogManager.getLogger().error(MODULE, "Subscriber ID and Alternate ID do not match");
						return new UpdateMonetaryBalanceResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Subscriber ID and Alternate ID do not reference to the same subscriber", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}
			} else {
				subscriberIdentity = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateIdentity, adminStaff);
				if(isNullOrBlank(subscriberIdentity)){
					LogManager.getLogger().error(MODULE, "Alternate ID not found.");
					return new UpdateMonetaryBalanceResponse(ResultCode.NOT_FOUND.code, "Reason: Subscriber not found with alternate identity: "+alternateIdentity, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);
			}

			SubscriberMonetaryBalance subscriberMonetaryBalance = SubscriberDAO.getInstance().getMonetaryBalance(subscriberIdentity,Predicates.ALL_MONETARY_BALANCE);

			if(CollectionUtils.isEmpty(subscriberMonetaryBalance.getAllBalance())){
				throw new OperationFailedException("No balance found for subscriber Identity: "+subscriberIdentity);
			}

			MonetaryBalance monetaryBalance = null;
			if(isNullOrBlank(request.getBalanceId())==false){
				monetaryBalance = subscriberMonetaryBalance.getBalanceById(request.getBalanceId());

				if(monetaryBalance==null){
					throw new OperationFailedException("Could not find the balance with the id "+request.getBalanceId());
				}
				if(isCurrencyInSynWithPackageCurrency(monetaryBalance,sprInfo.getProductOffer()) == false){
					throw new OperationFailedException("Inactive balance found for balance id "+request.getBalanceId()+", " + getMismatchCurrencyMessage(monetaryBalance));
				}
			} else {
				String currencyForFilter = getPackageCurrencyByName(sprInfo.getProductOffer());
				CurrencyValidatorPredicate currencyValidatorPredicate = CurrencyValidatorPredicate.create(currencyForFilter);
				List<MonetaryBalance> monetaryBalances = subscriberMonetaryBalance.getAllBalance().stream().filter(currencyValidatorPredicate).collect(Collectors.toList());

				if(CollectionUtils.isEmpty(monetaryBalances)){
					throw new OperationFailedException("Could not find active balance for current package currency "+ currencyForFilter);
				}

				if(serviceData!=null){
					monetaryBalance = UpdateOrder.OLDEST.getBalance(monetaryBalances,serviceData.getId(),operation);
				}
				if(monetaryBalance == null){
					throw new OperationFailedException("Balance not found for "+serviceData.getName());
				}

				if(isCurrencyInSynWithPackageCurrency(monetaryBalance,sprInfo.getProductOffer()) == false){
					throw new OperationFailedException("Inactive balance found for service " + serviceData.getName() + ", " + getMismatchPackageCurrencyMessage(monetaryBalance,currencyForFilter));
				}
			}

			Double amount = request.getAmount();

			if(amount == null){
				amount = 0D;
			}

			MonetaryBalance previousMonetaryBalance = monetaryBalance.copy();

			if(operation==OperationType.DEBIT){
				amount = amount*-1;
			}
			String transactionId=Strings.isNullOrBlank(request.getTransactionId())?UUID.randomUUID().toString():request.getTransactionId();
			monetaryBalance.setAvailBalance(amount);
			monetaryBalance.setInitialBalance(0);

			SubscriberDAO.getInstance().updateMonetaryBalance(subscriberIdentity,
					new SubscriberMonetaryBalanceWrapper(monetaryBalance, previousMonetaryBalance, sprInfo,
							request.getWebServiceMethodName(), ActionType.UPDATE.name(),transactionId),
					"",request.getRequestIpAddress());

			doUpdateAction(request, subscriberIdentity);

			if (serviceData == null) {
				serviceData = policyRepository.getService().byId(monetaryBalance.getServiceId());
			}

			UpdatedMonetaryBalanceData monetaryBalanceData = new UpdatedMonetaryBalanceData();
			monetaryBalanceData.setBalanceId(monetaryBalance.getId());
			monetaryBalanceData.setServiceId(nonNull(serviceData) ? serviceData.getId() : null);
			monetaryBalanceData.setPreviousBalance(previousMonetaryBalance.getAvailBalance());
			monetaryBalanceData.setCurrentBalance(previousMonetaryBalance.getAvailBalance() + amount);
			monetaryBalanceData.setValidFromDate(monetaryBalance.getValidFromDate());
			monetaryBalanceData.setValidToDate(monetaryBalance.getValidToDate());
			monetaryBalanceData.setSubscriberId(subscriberIdentity);

			return new UpdateMonetaryBalanceResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, monetaryBalanceData, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while updating monetary balance for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			if (e.getErrorCode() == ResultCode.INTERNAL_ERROR) {
				getLogger().trace(MODULE,e);
			}
			return new UpdateMonetaryBalanceResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while updating monetary balance for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			return new UpdateMonetaryBalanceResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	private String getMismatchCurrencyMessage(MonetaryBalance monetaryBalance) {
		return "System currency("+SystemParameterDAO.getCurrency()+") is mismatch with existing balance currency("+monetaryBalance.getCurrency()+")";
	}

	private String getMismatchPackageCurrencyMessage(MonetaryBalance monetaryBalance,String currency) {
		return "Package currency("+currency+") is mismatch with existing balance currency("+monetaryBalance.getCurrency()+")";
	}

	private boolean isCurrencyInSynWithSystemCurrency(MonetaryBalance monetaryBalance) {
		return SystemParameterDAO.getCurrency().equalsIgnoreCase(monetaryBalance.getCurrency());
	}

	private boolean isCurrencyInSynWithPackageCurrency(MonetaryBalance monetaryBalance,String packageName) {
		String currency = getPackageCurrencyByName(packageName);
		return currency.equalsIgnoreCase(monetaryBalance.getCurrency());
	}

	private void doUpdateAction(UpdateMonetaryBalanceRequest request, String subscriberIdBySpr) {
		try {
			ReAuthUtil.doReAuthBySubscriberId(subscriberIdBySpr, request.getUpdateAction());
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while performing re-auth for subscriber Id:" + subscriberIdBySpr + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}
	}

	private enum SubscriptionOrder {

		OLDEST("OLDEST") {
			@Override
			Subscription getSubscription(List<Subscription> subscriptions, String addOnName) {

				Subscription oldestSubscription = null;

				for (int index = 0; index < subscriptions.size(); index++) {

					Subscription subscription = subscriptions.get(index);

					ProductOffer subscriptionPackage = DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().addOn().byId(subscription.getProductOfferId());

					if (subscriptionPackage == null) {
						continue;
					}

					if (subscriptionPackage.getName().equals(addOnName)) {

						if (Objects.isNull(oldestSubscription) || subscription.getStartTime().before(oldestSubscription.getStartTime())) {
							oldestSubscription = subscription;
						}
					}

				}

				return oldestSubscription;

			}

			@Override
			Subscription getTopUpSubscription(List<Subscription> subscriptions, String topUpName) {

				Subscription oldestSubscription = null;

				for (int index = 0; index < subscriptions.size(); index++) {

					Subscription subscription = subscriptions.get(index);

					QuotaTopUp subscriptionPackage = DefaultNVSMXContext.getContext().getPolicyRepository().getQuotaTopUpById(subscription.getPackageId());

					if (subscriptionPackage == null) {
						continue;
					}

					if (subscriptionPackage.getName().equals(topUpName)) {

						if (oldestSubscription == null || subscription.getStartTime().before(oldestSubscription.getStartTime())) {
							oldestSubscription = subscription;
						}
					}

				}

				return oldestSubscription;

			}
		},
		LATEST("LATEST") {
			@Override
			Subscription getSubscription(List<Subscription> addOnSubscriptions, String addOnName) {

				Subscription latestSubscription = null;

				for (int index = 0; index < addOnSubscriptions.size(); index++) {

					Subscription subscription = addOnSubscriptions.get(index);

					ProductOffer subscriptionPackage = DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().addOn().byId(subscription.getProductOfferId());

					if (subscriptionPackage == null) {
						continue;
					}

					if (subscriptionPackage.getName().equals(addOnName)) {
						if (latestSubscription == null || subscription.getStartTime().after(latestSubscription.getStartTime())) {
							latestSubscription = subscription;
						}
					}

				}

				return latestSubscription;

			}

			@Override
			Subscription getTopUpSubscription(List<Subscription> topUpSubscriptions, String topUpName) {

				Subscription latestSubscription = null;

				for (int index = 0; index < topUpSubscriptions.size(); index++) {

					Subscription subscription = topUpSubscriptions.get(index);

					QuotaTopUp subscriptionPackage = DefaultNVSMXContext.getContext().getPolicyRepository().getQuotaTopUpById(subscription.getPackageId());

					if (subscriptionPackage == null) {
						continue;
					}

					if (subscriptionPackage.getName().equals(topUpName)) {

						if (latestSubscription == null || subscription.getStartTime().after(latestSubscription.getStartTime())) {
							latestSubscription = subscription;
						}
					}

				}

				return latestSubscription;

			}
		};

		private String name;

		private SubscriptionOrder(String name) {
			this.name = name;
		}

		static SubscriptionOrder fromVal(String name) {

			if (OLDEST.name.equalsIgnoreCase(name)) {
				return OLDEST;
			} else if (LATEST.name.equalsIgnoreCase(name)) {
				return LATEST;
			}

			return null;
		}

		abstract Subscription getSubscription(List<Subscription> addOnSubscriptions, String addOnName);
		abstract Subscription getTopUpSubscription(List<Subscription> topUpSubscriptions, String topUpName);
	}

	public List<TopUpPackageData> getAllTopUps() {

		List<TopUpPackageData> topUpPkgs = Collectionz.newArrayList();
		List<QuotaTopUp> quotaTopUps = getNVSMXPolicyRepository().getActiveAllQuotaTopUpDatas();
		if (Collectionz.isNullOrEmpty(quotaTopUps) == false) {
			for (QuotaTopUp topup : quotaTopUps) {
				if (topup.getStatus() != PolicyStatus.FAILURE) {
					topUpPkgs.add(QUOTA_TOP_UP_TO_WS_TOP_UP_MAPPER.apply(topup));
				}
			}
		}
		return topUpPkgs;
	}



	public List<SubscriptionData> getAddOnSubscriptionDatas(String subscriberId, String alternateId, SubscriptionState subscriptionState) throws UnauthorizedActionException, OperationFailedException {

		List<Subscription> addOnSubscriptions;

		if (isNullOrBlank(subscriberId) == false) {
			SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);

			if (sprInfo == null) {
				throw new OperationFailedException("Subscriber not found with subscriber identity: " + subscriberId, ResultCode.NOT_FOUND);
			}

			addOnSubscriptions = SubscriberDAO.getInstance().getSubscriptions(subscriberId, adminStaff);
		} else {
			addOnSubscriptions = SubscriberDAO.getInstance().getSubscriptionsByAlternateId(alternateId, adminStaff);
		}


		List<SubscriptionData> subscriptionDatas = new ArrayList<SubscriptionData>();

		for (Subscription addOnSubscription : addOnSubscriptions) {
			if(Objects.equals(SubscriptionType.ADDON, addOnSubscription.getType())
					|| Objects.equals(SubscriptionType.RO_ADDON, addOnSubscription.getType())) {
				if (subscriptionState == null || addOnSubscription.getStatus() == subscriptionState) {
					subscriptionDatas.add(SubscriptionData.fromSubscription(addOnSubscription));
				}
			}

		}

		return subscriptionDatas;
	}


	public List<TopUpSubscriptionData> getTopUpSubscriptionDatas(String subscriberId, String alternateId, SubscriptionState subscriptionState) throws UnauthorizedActionException, OperationFailedException {

		List<Subscription> subscriptions;

		if (isNullOrBlank(subscriberId) == false) {
			SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);

			if (sprInfo == null) {
				throw new OperationFailedException("Subscriber not found with subscriber identity: " + subscriberId, ResultCode.NOT_FOUND);
			}
			subscriptions = SubscriberDAO.getInstance().getSubscriptions(subscriberId, adminStaff);
		} else {
			subscriptions = SubscriberDAO.getInstance().getSubscriptionsByAlternateId(alternateId, adminStaff);
		}

		List<TopUpSubscriptionData> topUpSubscriptions = new ArrayList<>();

		if (Collectionz.isNullOrEmpty(subscriptions) == false) {
			for (Subscription subscription : subscriptions) {
				if(subscription.getType() == SubscriptionType.TOP_UP) {
					if (Objects.isNull(subscriptionState) || subscription.getStatus() == subscriptionState) {
						topUpSubscriptions.add(createTopUpSubscriptionData(subscription));
					}
				}
			}
		}

		return topUpSubscriptions;
	}

	private SubscriptionState getSubscriptionStatus(String subscriptionStatusValue, String subscriptionStatusName) {
		SubscriptionState subscriptionState = SubscriptionState.fromStringValue(subscriptionStatusValue);
		if (subscriptionState == null) {
			subscriptionState = SubscriptionState.fromName(subscriptionStatusName);
		}

		if (SubscriptionState.SUBSCRIBED == subscriptionState) {
			subscriptionState = SubscriptionState.STARTED;
		}
		return subscriptionState;
	}

	private List<SubscriptionData> createSubscriptionData(List<Subscription> subscriptions) {
		List<SubscriptionData> subscriptionDatas = Collectionz.newArrayList();
		for(Subscription subscription : subscriptions) {
			subscriptionDatas.add(SubscriptionData.fromSubscription(subscription));
		}
		return subscriptionDatas;
	}


	private TopUpSubscriptionData createTopUpSubscriptionData(Subscription subscription) {

		QuotaTopUp subscriptionPackage = getNVSMXPolicyRepository().getQuotaTopUpById(subscription.getPackageId());

		return new TopUpSubscriptionData(subscription.getId(),
				subscription.getSubscriberIdentity(), subscriptionPackage.getId(), subscriptionPackage.getName()
				, subscription.getStartTime() == null ? null : subscription.getStartTime().getTime()
				, subscription.getEndTime() == null ? BIG_DATE_IN_MILLIS : subscription.getEndTime().getTime(), subscription.getStatus()
				, subscription.getParameter1(), subscription.getParameter2(), subscription.getPriority());
	}

	public UMQueryResponse getUsageMonitoringInformation(ListUsageMonitoringInformationWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preListUsageMonitoringInformation(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		UMQueryResponse response =  doGetUsageMonitoringInformation(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postListUsageMonitoringInformation(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private UMQueryResponse doGetUsageMonitoringInformation(ListUsageMonitoringInformationWSRequest request) {
	
		String subscriberIdentity = request.getSubscriberId();
		

		/**
		 * Current-usage map structure Map <Subscription Id/ package Id,
		 * 
		 * Map <"QuotaPrifile Id + SEPARATOR + Service Id",
		 * 
		 * Subscriber Usage>>
		 * 
		 */
		Map<String, Map<String, SubscriberUsage>> currentUsage = null;
		try {
			if (isNullOrBlank(subscriberIdentity)) {

				String alternateId = request.getAlternateId();
				
				if (isNullOrBlank(alternateId)) {
					getLogger().error(MODULE, "Unable to get usage information. Reason: Identity parameter missing");
					return new UMQueryResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name
							+ ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				currentUsage = SubscriberDAO.getInstance().getCurrentUsageByAlternateId(alternateId);
			} else {
				currentUsage = SubscriberDAO.getInstance().getCurrentUsage(subscriberIdentity);
			}
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Unable to get usage information. Reason: No usage found for subscriber id: " + subscriberIdentity);
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new UMQueryResponse(ResultCode.INTERNAL_ERROR.code, "No usage found for subscriber id: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (Maps.isNullOrEmpty(currentUsage)) {
			getLogger().error(MODULE, "Unable to get usage information. Reason: Current usage not found with subscriber identity: "
					+ subscriberIdentity);
			return new UMQueryResponse(ResultCode.NOT_FOUND.code, "Current usage not found with subscriber identity: "
					+ subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		List<SubscriberUsage> selectedSubscriberUsages = new ArrayList<SubscriberUsage>();
		for (Map<String, SubscriberUsage> serviceWiseUsages : currentUsage.values()) {
			selectedSubscriberUsages.addAll(serviceWiseUsages.values());
		}

		//Filteration based on service / quota profile is only allowed if parent entity is given

		if (isNullOrBlank(request.getServiceId()) == false || isNullOrBlank(request.getServiceName()) == false) {
			if((isNullOrBlank(request.getQuotaProfileId()) && isNullOrBlank(request.getQuotaProfileName())) ||
					(isNullOrBlank(request.getPackageId()) && isNullOrBlank(request.getPackageName()))){

				return new UMQueryResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Package id/name and Quota Profile id/name must be provided to filter based on data service type"
						, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if ((isNullOrBlank(request.getQuotaProfileId()) == false || isNullOrBlank(request.getQuotaProfileName()) == false) &&
				(isNullOrBlank(request.getPackageId()) && isNullOrBlank(request.getPackageName()))) {

			return new UMQueryResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Package id/name must be provided to filter based on Quota Profile"
					, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		UserPackage pkgData = null;
		try {
			pkgData = getPkgData(request.getPackageId(), request.getPackageName(), subscriberIdentity);
		} catch (ValidationFailedException e) {
			LogManager.ignoreTrace(e);
			return new UMQueryResponse(e.getResultCode(), e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (pkgData != null) {
			QuotaProfile quotaProfile;
			try {
				quotaProfile = getQuotaProfileData(request.getQuotaProfileId(), request.getQuotaProfileName(), pkgData);
			} catch (ValidationFailedException e) {
				LogManager.ignoreTrace(e);
				return new UMQueryResponse(e.getResultCode(), e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

			String serviceId = null;
			if (quotaProfile != null) {
				
				if (quotaProfile.getType() == QuotaProfileType.SY_COUNTER_BASED) {
					getLogger().error(MODULE, "Unable to get usage information. Reason: Quota profile: " + quotaProfile.getName() + " (" + quotaProfile.getId() + ") is SY based");
					return new UMQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Quota profile: " + quotaProfile.getName() + " (" + quotaProfile.getId() + ") is SY based", null,null,null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				
				try {
					if(isNullOrBlank(request.getServiceId()) == false && isNullOrBlank(request.getServiceName()) == false) {
						validateService(request.getServiceId(), request.getServiceName(), quotaProfile);
						serviceId = request.getServiceId();
					} else if(isNullOrBlank(request.getServiceId()) == false) {
						serviceId = request.getServiceId();
					} else if(isNullOrBlank(request.getServiceName()) == false) {
						serviceId = getServiceIdFromName(request.getServiceName(), quotaProfile);
					}

				} catch (ValidationFailedException e) {
					LogManager.ignoreTrace(e);
					return new UMQueryResponse(e.getResultCode(), e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

			selectedSubscriberUsages = filter(pkgData.getId(), selectedSubscriberUsages, quotaProfile, serviceId);
		}

		if (Collectionz.isNullOrEmpty(selectedSubscriberUsages) == false) {
			List<UMInfoData> umInfos = convertSubscriberUsagesToUmInfos(selectedSubscriberUsages, pkgData);
			return new UMQueryResponse(ResultCode.SUCCESS.code, SUCCESS, umInfos, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} else {
			return new UMQueryResponse(ResultCode.NOT_FOUND.code, "Current usage not found with subscriber identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	private void validateService(String serviceId, String serviceName, QuotaProfile quotaProfile) throws ValidationFailedException {

		String quotaProfileServiceName = getServiceNameById(serviceId, quotaProfile);
		if (quotaProfileServiceName == null) {
			getLogger().error(MODULE, "Unable to get usage information. Reason: Service name not found for service Id: " + serviceId
					+ " from quota profile: " + quotaProfile.getName() + " (" + quotaProfile.getId() + ")");
			throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, "Data Service type not found with id: " + serviceId);
		} else {

			if (serviceName.equals(quotaProfileServiceName) == false) {
				getLogger().error(MODULE, "Unable to get usage information. Reason: Service name(" + serviceName +") not match" +
						" with service name( " + serviceName + ") configured");
				throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, "Service name(" + serviceName +") not match" +
						" with service name( " + serviceName + ") configured");
			}

		}
	}

	private List<SubscriberUsage> filter(String pkgId, List<SubscriberUsage> allSubscriberUsages, QuotaProfile quotaProfile,
										 String serviceId) {

		List<SubscriberUsage> selectedSubscriberUsages = new ArrayList<SubscriberUsage>();

		for (int index = 0; index < allSubscriberUsages.size(); index++) {

			SubscriberUsage subscriberUsage = allSubscriberUsages.get(index);

			if (pkgId.equals(subscriberUsage.getPackageId()) == false) {
				continue;
			}

			if (quotaProfile != null) {
				if (quotaProfile.getId().equals(subscriberUsage.getQuotaProfileId()) == false) {
					continue;
				}
				if (serviceId != null) {
					if (serviceId.equals(subscriberUsage.getServiceId()) == false) {
						continue;
					}
				}
			}

			selectedSubscriberUsages.add(subscriberUsage);

		}
		return selectedSubscriberUsages;
	}

	private String getServiceIdFromName(String serviceName, QuotaProfile quotaProfile) throws ValidationFailedException {

		if (Maps.isNullOrEmpty(quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails()) == false) {

			for (QuotaProfileDetail quotaProfileDetail : quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().values()) {

				if (quotaProfileDetail.getServiceName().equals(serviceName)) {
					return quotaProfileDetail.getServiceId();
				}

			}

			getLogger().error(MODULE, "Unable to get usage information. Reason: Service not found for service name: " + serviceName
					+ " from quota profile: " + quotaProfile.getName() + " (" + quotaProfile.getId() + ")");
			throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, "Data Service type not found with name: " + serviceName);
		} else {

			getLogger().error(MODULE, "Unable to get usage information. Reason: Quota profile: " + quotaProfile.getName() + " (" + quotaProfile.getId() + ") not properly configured");
			throw new ValidationFailedException(ResultCode.INTERNAL_ERROR.code, "Quota profile: " + quotaProfile.getName() + " (" + quotaProfile.getId() + ") not properly configured");
		}
	}

	private @Nullable String getServiceNameById(String serviceId, QuotaProfile selectedQuotaProfile) {

		if (Maps.isNullOrEmpty(selectedQuotaProfile.getHsqLevelServiceWiseQuotaProfileDetails()) == false) {

			for (QuotaProfileDetail quotaProfileDetail : selectedQuotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().values()) {

				if (quotaProfileDetail.getServiceId().equals(serviceId)) {
					return ((UMBaseQuotaProfileDetail)quotaProfileDetail).getServiceName();
				}

			}
		}

		return null;
	}

	private QuotaProfile getQuotaProfileData(String quotaProfileId, String quotaProfileName, UserPackage pkg) throws ValidationFailedException {
		QuotaProfile quotaProfile = null;

		if (isNullOrBlank(quotaProfileId) == false) {

			quotaProfile = getQuotaProfileByIdFromPackage(quotaProfileId, pkg);

			if (quotaProfile == null) {
				getLogger().error(MODULE, "Unable to get usage information. Reason: Quota profile not found with id: " + quotaProfileId);
				throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, "Quota profile not found with id: " + quotaProfileId);
			}
		}

		if (isNullOrBlank(quotaProfileName) == false) {

			if (quotaProfile == null) {

				quotaProfile = getQuotaProfileByNameFromPackage(quotaProfileName, pkg);

				if (quotaProfile == null) {
					getLogger().error(MODULE, "Unable to get usage information. Reason: Quota profile not found with name: " + quotaProfileName);
					throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, "Quota profile not found with name"
							+ quotaProfileName);
				}
			} else if (quotaProfile.getName().equals(quotaProfileName) == false) {

				getLogger().error(MODULE, "Unable to get usage information. Reason: Quota profile id(" + quotaProfileId + ") and name("
						+ quotaProfileName + ") are not related ");
				throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, "Quota profile id(" + quotaProfileId + ") and name("
						+ quotaProfileName + ") are not related ");
			}

		}
		return quotaProfile;
	}

	private QuotaProfile getQuotaProfileByNameFromPackage(String quotaProfileName, UserPackage pkg) {
		
		if (Collectionz.isNullOrEmpty(pkg.getQoSProfiles()) == false) {
			for (QoSProfile qosProfile : pkg.getQoSProfiles()) {

				QuotaProfile quotaProfile = qosProfile.getQuotaProfile();
				if (quotaProfile != null) {

					if (quotaProfile.getName().equals(quotaProfileName)) {
						return quotaProfile;
                    }
                }
            }
        }
		
		return null;
	}

	private UserPackage getPkgData(String packageId, String packageName, String subscriberIdentity) throws ValidationFailedException {

		UserPackage userPackage = null;

		if (isNullOrBlank(packageId) == false) {
			userPackage = DefaultNVSMXContext.getContext().getPolicyRepository().getPkgDataById(packageId);

			if (userPackage == null) {
				getLogger().error(MODULE, "Unable to get usage information for subscriber Identity: " + subscriberIdentity + ". Reason: Package not found with id: " + packageId);
				throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER + ". Reason: Package not found with id: " + packageId);
			}
			
			if (userPackage.getStatus() == PolicyStatus.FAILURE) {
				getLogger().error(MODULE, "Unable to get usage information for subscriber Identity: " + subscriberIdentity +". Reason: Package(" + packageId + ") is failed package");
				throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER + ". Reason: Package(" + packageId + ") is failed package");
		}
		}

		if (isNullOrBlank(packageName) == false) {

			if (userPackage == null) {

				userPackage = DefaultNVSMXContext.getContext().getPolicyRepository().getPkgDataByName(packageName);
				if (userPackage == null) {
					getLogger().error(MODULE, "Unable to get usage informationfor subscriber Identity: " + subscriberIdentity +". Reason: Package not found with name: " + packageName);
					throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER + ". Reason: Package not found with name: " + packageName);
				}

				if (userPackage.getStatus() == PolicyStatus.FAILURE) {
					getLogger().error(MODULE, "Unable to get usage information for subscriber Identity: " + subscriberIdentity +". Reason: Package(" + packageName + ") is failed package");
					throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER + ". Reason: Package(" + packageName + ") is failed package");
				}
			} else if (userPackage.getName().equals(packageName) == false) {

				getLogger().error(MODULE, "Unable to get usage informationfor subscriber Identity: " + subscriberIdentity +". Reason: Package id(" + packageId + ") and name(" + packageName
						+ ") are not related ");
				throw new ValidationFailedException(ResultCode.INVALID_INPUT_PARAMETER.code, "Package id(" + packageId + ") and name(" + packageName
						+ ") are not related ");
			}

		}
		return userPackage;
	}

	private List<UMInfoData> convertSubscriberUsagesToUmInfos(List<SubscriberUsage> filteredUsages, UserPackage userPackage) {

		List<UMInfoData> umInfoDatas = new ArrayList<UMInfoData>();
		boolean isPkgDataReceived = userPackage == null ? false : true;
		for (SubscriberUsage subscriberUsage : filteredUsages) {

			if (isPkgDataReceived == false) {
				userPackage = DefaultNVSMXContext.getContext().getPolicyRepository().getPkgDataById(subscriberUsage.getPackageId());

				if (userPackage == null) {

					getLogger().warn(MODULE, "Skippping conversion of usage info for subsciber id :" + subscriberUsage.getSubscriberIdentity() + ". Reason: Pkg data not found");
					continue;
				}

				if (userPackage.getStatus() == PolicyStatus.FAILURE) {

					getLogger().warn(MODULE, "Skippping conversion of usage info for subsciber id :" + subscriberUsage.getSubscriberIdentity()
							+ ". Reason: Pkg(" + userPackage.getName() + ") is failed package");
					continue;
				}

			}

			QuotaProfile selectedQuotaProfile = getQuotaProfileByIdFromPackage(subscriberUsage.getQuotaProfileId(), userPackage);

			if (selectedQuotaProfile == null) {

				getLogger().warn(MODULE, "Skippping conversion of usage info for package :" + userPackage.getName() + ". Reason: Quota Profile not found");
				continue;
			}

			if (selectedQuotaProfile.getType() == QuotaProfileType.SY_COUNTER_BASED) {
				getLogger().warn(MODULE, "Skippping conversion of usage info for package :" + userPackage.getName() + ". Reason: Quota profile: " + selectedQuotaProfile.getName() + " (" + selectedQuotaProfile.getId() + ") is SY based");
				continue;
			}

			String serviceName = getServiceNameById(subscriberUsage.getServiceId(), selectedQuotaProfile);

			if (serviceName == null) {
				getLogger().warn(MODULE, "Skippping conversion of usage info for package :" + userPackage.getName() + ". Reason: Service name not found for service Id: " + subscriberUsage.getServiceId()
						+ " from quota profile: " + selectedQuotaProfile.getName() + " (" + selectedQuotaProfile.getId() + ")");
				continue;
			}

			umInfoDatas
					.add(new UMInfoData(subscriberUsage.getPackageId(), userPackage.getName(), subscriberUsage.getSubscriptionId()
							, subscriberUsage.getQuotaProfileId(), selectedQuotaProfile.getName(), subscriberUsage.getServiceId(), serviceName
							, new BillingCycleUsage(subscriberUsage.getBillingCycleTotal(), subscriberUsage.getBillingCycleUpload(), subscriberUsage
							.getBillingCycleDownload(), subscriberUsage.getBillingCycleTime())
							, new DailyUsage(subscriberUsage.getDailyTotal(), subscriberUsage.getDailyUpload(), subscriberUsage.getDailyDownload(), subscriberUsage
							.getDailyTime())
							, new WeeklyUsage(subscriberUsage.getWeeklyTotal(), subscriberUsage.getWeeklyUpload(), subscriberUsage
							.getWeeklyDownload(), subscriberUsage.getWeeklyTime())
							, new CustomUsage(subscriberUsage.getCustomTotal(), subscriberUsage.getCustomUpload(), subscriberUsage
							.getCustomDownload(), subscriberUsage.getCustomTime())));
		}

		return umInfoDatas;
	}

	private QuotaProfile getQuotaProfileByIdFromPackage(String id, UserPackage pkg) {

		if (Collectionz.isNullOrEmpty(pkg.getQoSProfiles()) == false) {
			for (QoSProfile qosProfile : pkg.getQoSProfiles()) {

				QuotaProfile quotaProfile = qosProfile.getQuotaProfile();
				if (quotaProfile != null) {

					if (quotaProfile.getId().equals(id)) {
						return quotaProfile;
					}
				}
			}
		}
		return null;
	}

	public BalanceEnquiryResponse getBalance(BalanceEnquiryRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preBalanceEnquiry(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		BalanceEnquiryResponse response = doBalanceEnquiry(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postBalanceEnquiry(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private BalanceEnquiryResponse doBalanceEnquiry(BalanceEnquiryRequest request) {

		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();
		String pkgId = request.getPackageId();
		String pkgName = request.getPackageName();
		String subscriptionId = request.getSubscriptionId();
		String parameter2 = request.getParameter2();
		String parameter1 = request.getParameter1();
		List<SubscriptionInformation> balance = null;
		SPRInfo sprInfo = null;

		if(isNullOrBlank(subscriberIdentity) && isNullOrBlank(alternateIdentity)){
			LogManager.getLogger().error(MODULE, "Unable to fetch balance. Reason: Identity parameter missing");
			return new BalanceEnquiryResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
					ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		if (isNullOrBlank(subscriberIdentity)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE,"Subscriber ID not received");
			}
			try{
				subscriberIdentity = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateIdentity, adminStaff);
			}catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while fetching balance with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
				if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE,e);
				}
				return new BalanceEnquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}
		try{
			sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);
			if(sprInfo == null){
				getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: " + subscriberIdentity);
				return new BalanceEnquiryResponse(ResultCode.NOT_FOUND.code,
						ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with subscriber identity:" +subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			if(isNullOrBlank(pkgId) && isNullOrBlank(pkgName) && isNullOrBlank(subscriptionId)) {
				balance = SubscriberDAO.getInstance().getAllBalance(sprInfo);
			}else{
				
				/*
				 * if package name provided then package should be found from repository 
				 */
				if (isNullOrBlank(pkgName) == false) {

					UserPackage pkg = DefaultNVSMXContext.getContext().getPolicyRepository().getPkgDataByName(pkgName);

					if (pkg == null) {
						LogManager.getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: "+subscriberIdentity+" with package name: " + pkgName);
						return new BalanceEnquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: No package found with name: " + pkgName+" for subscriber Identity: "+subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					if (pkg.getStatus() == PolicyStatus.FAILURE) {
						LogManager.getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: " + subscriberIdentity + " with package name: " + pkgName
								+ ". Reason: Package(" + pkgName + ") is failed package");
						return new BalanceEnquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Package(" + pkgName + ") is failed package for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}
					
					/*
					 * if 
					 * 	package name and package id both are provided then check the relation between package id and name. They must belong to same package
					 */
					if(isNullOrBlank(pkgId) == false){
						if(pkg.getId().equalsIgnoreCase(pkgId) == false){
							LogManager.getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: "+subscriberIdentity+". Reason: Package name: " + pkgName + " and id: " + pkg.getId() +
									" are not related");
							return new BalanceEnquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
									ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Package name: " + pkgName + " and id: " + pkg.getId() +
											" are not related for subscriber identity: "+subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
						}
					}
					pkgId = pkg.getId();
				}


				balance = SubscriberDAO.getInstance().getBalanceBySubscriberId(sprInfo, pkgId, subscriptionId);
			}
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching balance with subscriber Id: " + subscriberIdentity + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE,e);
			}
			return new BalanceEnquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (UnauthorizedActionException e) {
			//This exception will never going to be occur
			getLogger().error(MODULE, "Error while fetching balance with subscriber Id: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			return new BalanceEnquiryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		StringBuilder skippedSyQuotaProfilePkgs = new StringBuilder();
		StringBuilder skippedPackagesWithoutQuota = new StringBuilder();
		List<SubscriptionInformationData> balanceForWS = null;
		if (Collectionz.isNullOrEmpty(balance) == false) {
			balanceForWS = getBalanceForWS(balance, skippedSyQuotaProfilePkgs, skippedPackagesWithoutQuota);
		}

		return new BalanceEnquiryResponse(ResultCode.SUCCESS.code, createResponseMessage(skippedSyQuotaProfilePkgs, skippedPackagesWithoutQuota), balanceForWS, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	private String createResponseMessage(StringBuilder skippedSyQuotaProfilePkgs, StringBuilder skippedPackagesWithoutQuota) {

		StringBuilder builder = new StringBuilder();

		builder.append(SUCCESS);
		if (skippedSyQuotaProfilePkgs.length() > 0 || skippedPackagesWithoutQuota.length() > 0) {
			builder.append(OPENING_PARENTHESES);
		} else {
			return builder.toString();
		}

		if (skippedSyQuotaProfilePkgs.length() > 0) {
			skippedSyQuotaProfilePkgs.delete(skippedSyQuotaProfilePkgs.length() - 1, skippedSyQuotaProfilePkgs.length());
			builder.append("with skipped syQuotaProfile packages: " + skippedSyQuotaProfilePkgs.toString());
		}

		if (skippedPackagesWithoutQuota.length() > 0) {

			if (skippedSyQuotaProfilePkgs.length() > 0) {
				builder.append(SEMICOLON);
			}

			skippedPackagesWithoutQuota.delete(skippedPackagesWithoutQuota.length() - 1, skippedPackagesWithoutQuota.length());
			builder.append("with skipped packages without quota: " + skippedPackagesWithoutQuota.toString());
		}

		return builder.append(CLOSING_PARENTHESES).toString();
	}

	private void getUMBalanceForWS(List<SubscriptionInformation> balance,
								   StringBuilder skippedSyQuotaProfilePkgs,
								   StringBuilder skippedPackagesWithoutQuota,
								   List<RnCSubscriptionInformatonData> balanceForWS) {

		for (SubscriptionInformation subscriptionInformation : balance) {

			if (subscriptionInformation.getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED) {
				skippedSyQuotaProfilePkgs.append(subscriptionInformation.getPackageName()).append(COMMA);
				continue;
			}

			if (subscriptionInformation.getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
				continue;
			}

			List<RnCQuotaProfileBalance> quotaBalanceForWS = getUMQuotaBalanceForWS(subscriptionInformation.getQuotaProfileBalances());
			if (quotaBalanceForWS.isEmpty()) {
				skippedPackagesWithoutQuota.append(subscriptionInformation.getPackageName())
						.append(COMMA);
				continue;
			}

			RnCSubscriptionInformatonData rnCSubscription = new RnCSubscriptionInformatonData();

			rnCSubscription.setAddonSubscriptionId(subscriptionInformation.getAddonSubscriptionId());
			rnCSubscription.setPackageName(subscriptionInformation.getPackageName());
			rnCSubscription.setPackageId(subscriptionInformation.getPackageId());
			rnCSubscription.setPackageDescription(subscriptionInformation.getPackageDescription());
			rnCSubscription.setPackageType(subscriptionInformation.getPackageType());
			rnCSubscription.setStartTime(getTimeInMilliSeconds(subscriptionInformation.getStartTime()));
			rnCSubscription.setEndTime(getTimeInMilliSeconds(subscriptionInformation.getEndTime()));
			rnCSubscription.setAddOnStatus(subscriptionInformation.getAddOnStatus());

			rnCSubscription.setQuotaprofileBalance(quotaBalanceForWS);

			balanceForWS.add(rnCSubscription);
		}

	}

	private Long getTimeInMilliSeconds(Timestamp timestamp){
		return timestamp==null?null:timestamp.getTime();
	}

	private List<RnCQuotaProfileBalance> getUMQuotaBalanceForWS(List<com.elitecore.corenetvertex.spr.balance.QuotaProfileBalance> quotaProfileBalances) {

		List<RnCQuotaProfileBalance> quotaBalances = new ArrayList<>();
		for (com.elitecore.corenetvertex.spr.balance.QuotaProfileBalance quotaProfileBalance : quotaProfileBalances) {

			RnCQuotaProfileBalance rnCQuotaProfileBalance = new RnCQuotaProfileBalance();

			rnCQuotaProfileBalance.setQuotaProfileId(quotaProfileBalance.getQuotaProfileId());
			rnCQuotaProfileBalance.setQuotaProfileName(quotaProfileBalance.getQuotaProfileName());

			List<com.elitecore.corenetvertex.spr.balance.UsageInfo> balanceInfos = quotaProfileBalance.getBalanceInfos();
			balanceInfos.add(quotaProfileBalance.getAllServiceBalance());

			rnCQuotaProfileBalance.setDataServiceTypeBalance(getUMUsageInfoForWS(balanceInfos));

			quotaBalances.add(rnCQuotaProfileBalance);
		}
		return quotaBalances;
	}

	private List<DataServiceTypeBalance> getUMUsageInfoForWS(List<com.elitecore.corenetvertex.spr.balance.UsageInfo> balanceInfos) {

		List<DataServiceTypeBalance> usageInfos = new ArrayList<>();
		Map<String, DataServiceTypeBalance> serviceTypeBalanceMap = new HashMap<>();
		for(com.elitecore.corenetvertex.spr.balance.UsageInfo usageInfo : balanceInfos) {

			DataServiceTypeBalance dataServiceTypeBalance;

			if(serviceTypeBalanceMap.containsKey(usageInfo.getServiceId())){
				dataServiceTypeBalance = serviceTypeBalanceMap.get(usageInfo.getServiceId());
			} else {
				dataServiceTypeBalance = new DataServiceTypeBalance();
				dataServiceTypeBalance.setServiceId(usageInfo.getServiceId());
				dataServiceTypeBalance.setServiceName(usageInfo.getServiceName());
				dataServiceTypeBalance.setLevel(BalanceLevel.HSQ.displayVal);

				serviceTypeBalanceMap.put(usageInfo.getServiceId(),dataServiceTypeBalance);
			}

			RncBalance balance = new RncBalance();

			balance.setTotal(getUMUsageForWS(usageInfo.getAllowedUsage()));
			balance.setActual(getUMRemainingBalance(usageInfo.getBalance(),usageInfo.getAllowedUsage(), usageInfo.getCurretUsage()));
			balance.setRemaining(balance.getActual());

			setBalance(dataServiceTypeBalance,balance,usageInfo.getAggregationKey());

		}

		for(DataServiceTypeBalance serviceTypeBalance: serviceTypeBalanceMap.values())
			usageInfos.add(serviceTypeBalance);

		return usageInfos;
	}

	private void setBalance(DataServiceTypeBalance dataServiceTypeBalance, RncBalance balance, AggregationKey key){

		if(AggregationKey.BILLING_CYCLE==key){
			dataServiceTypeBalance.setBillingCycleBalance(balance);
		} else if(AggregationKey.DAILY==key){
			dataServiceTypeBalance.setDailyBalance(balance);
		} else if(AggregationKey.WEEKLY==key){
			dataServiceTypeBalance.setWeeklyBalance(balance);
		} else if(AggregationKey.CUSTOM==key){
			dataServiceTypeBalance.setCustomBalance(balance);
		}
	}

	private Usage getUMUsageForWS(com.elitecore.corenetvertex.spr.balance.Usage balance) {
		return new Usage(balance.getUploadOctets()==CommonConstants.QUOTA_UNLIMITED_SPECIAL_SYMBOL?CommonConstants.QUOTA_UNLIMITED:balance.getUploadOctets(),
				balance.getDownloadOctets()==CommonConstants.QUOTA_UNLIMITED_SPECIAL_SYMBOL?CommonConstants.QUOTA_UNLIMITED:balance.getDownloadOctets(),
				balance.getTotalOctets()==CommonConstants.QUOTA_UNLIMITED_SPECIAL_SYMBOL?CommonConstants.QUOTA_UNLIMITED:balance.getTotalOctets(),
				balance.getTime()==CommonConstants.QUOTA_UNLIMITED_SPECIAL_SYMBOL?CommonConstants.QUOTA_UNLIMITED:balance.getTime(),
				null);
	}
	private Usage getUsageForWS(com.elitecore.corenetvertex.spr.balance.Usage balance) {
		return new Usage(balance.getUploadOctets(),balance.getDownloadOctets(),balance.getTotalOctets(),balance.getTime(), null);
	}

	private Usage getUMRemainingBalance(com.elitecore.corenetvertex.spr.balance.Usage balance, com.elitecore.corenetvertex.spr.balance.Usage total, com.elitecore.corenetvertex.spr.balance.Usage currentUsage) {
		return new Usage(total.getUploadOctets()==CommonConstants.QUOTA_UNLIMITED_SPECIAL_SYMBOL?CommonConstants.QUOTA_UNLIMITED-currentUsage.getUploadOctets():balance.getUploadOctets(),
				total.getDownloadOctets()==CommonConstants.QUOTA_UNLIMITED_SPECIAL_SYMBOL?CommonConstants.QUOTA_UNLIMITED-currentUsage.getDownloadOctets():balance.getDownloadOctets(),
				total.getTotalOctets()==CommonConstants.QUOTA_UNLIMITED_SPECIAL_SYMBOL?CommonConstants.QUOTA_UNLIMITED-currentUsage.getTotalOctets():balance.getTotalOctets(),
				total.getTime()==CommonConstants.QUOTA_UNLIMITED_SPECIAL_SYMBOL?CommonConstants.QUOTA_UNLIMITED-currentUsage.getTime():balance.getTime(),
				null);
	}

	private List<SubscriptionInformationData> getBalanceForWS(List<SubscriptionInformation> balance, StringBuilder skippedSyQuotaProfilePkgs, StringBuilder skippedPackagesWithoutQuota) {

		List<SubscriptionInformationData> subscriberInformationWSs = new ArrayList<SubscriptionInformationData>();
		for (SubscriptionInformation subscriptionInformation : balance) {

			if (subscriptionInformation.getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED) {
				skippedSyQuotaProfilePkgs.append(subscriptionInformation.getPackageName() + ", ");
				continue;
			}

			List<QuotaProfileBalance> quotaBalanceForWS = getQuotaBalanceForWS(subscriptionInformation.getQuotaProfileBalances());
			if (quotaBalanceForWS.isEmpty()) {
				skippedPackagesWithoutQuota.append(subscriptionInformation.getPackageName() + ", ");
				continue;
			}

			subscriberInformationWSs.add(new SubscriptionInformationData(subscriptionInformation.getAddonSubscriptionId()
					, subscriptionInformation.getPackageName(), subscriptionInformation.getPackageId(), subscriptionInformation.getPackageDescription()
					, subscriptionInformation.getPackageType()
					, subscriptionInformation.getStartTime() == null ? null : subscriptionInformation.getStartTime().getTime()
					, subscriptionInformation.getEndTime() == null ? null : subscriptionInformation.getEndTime().getTime(), subscriptionInformation.getAddOnStatus()
					, quotaBalanceForWS));
		}

		return subscriberInformationWSs;
	}

	private List<QuotaProfileBalance> getQuotaBalanceForWS(List<com.elitecore.corenetvertex.spr.balance.QuotaProfileBalance> quotaProfileBalances) {

		List<QuotaProfileBalance> quotaBalances = new ArrayList<QuotaProfileBalance>();
		for (com.elitecore.corenetvertex.spr.balance.QuotaProfileBalance quotaProfileBalance : quotaProfileBalances) {
			quotaBalances.add(new QuotaProfileBalance(quotaProfileBalance.getQuotaProfileId(), quotaProfileBalance.getQuotaProfileName()
					, getAllserviceQuotaForWS(quotaProfileBalance.getAllServiceBalance()), getUsageInfoForWS(quotaProfileBalance.getBalanceInfos())));
		}
		return quotaBalances;
	}

	private List<UsageInfo> getUsageInfoForWS(List<com.elitecore.corenetvertex.spr.balance.UsageInfo> balanceInfos) {

		List<UsageInfo> usageInfos = new ArrayList<UsageInfo>();
		for(com.elitecore.corenetvertex.spr.balance.UsageInfo usageInfo : balanceInfos) {

			usageInfos.add(new UsageInfo(usageInfo.getServiceId(), usageInfo.getServiceName(), usageInfo.getAggregationKey().getVal()
					, getUsageForWS(usageInfo.getCurretUsage())
					, getUsageForWS(usageInfo.getAllowedUsage()), getUsageForWS(usageInfo.getBalance())));
		}

		return usageInfos;
	}


	private UsageInfo getAllserviceQuotaForWS(com.elitecore.corenetvertex.spr.balance.UsageInfo allServiceBalance) {
		return new UsageInfo(allServiceBalance.getServiceId(), allServiceBalance.getServiceName(), allServiceBalance.getAggregationKey().getVal()
				, getUsageForWS(allServiceBalance.getCurretUsage())
				, getUsageForWS(allServiceBalance.getAllowedUsage()), getUsageForWS(allServiceBalance.getBalance()));
	}

	public NonMonitoryBalanceInquiryResponse getNonMonitoryBalance(NonMonitoryBalanceEnquiryRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preNonMonitoryBalanceEnquiry(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		NonMonitoryBalanceInquiryResponse response = doNonMonitoryBalanceInquiry(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postNonMonitoryBalanceEnquiry(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public RnCBalanceEnquiryResponse getRnCBalance(RnCBalanceEnquiryRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preRnCBalanceEnquiry(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		RnCBalanceEnquiryResponse response = doRnCBalanceEnquiry(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postRnCBalanceEnquiry(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public MonetaryBalanceInquiryResponse getMonetaryBalance(MonetaryBalanceEnquiryRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preMonetaryBalanceEnquiry(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		MonetaryBalanceInquiryResponse response = doMonetaryBalanceInquiry(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postMonetaryBalanceEnquiry(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public MonetoryOperationResponse addMonetaryBalance(MonetaryOperationRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preAddBalanceEnquiry(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		MonetoryOperationResponse response = doAddMonetaryBalance(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postAddBalanceEnquiry(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private MonetoryOperationResponse doAddMonetaryBalance(MonetaryOperationRequest request){
		String parameter1 = request.getParameter1();
		String parameter2 = request.getParameter2();
		String serviceId = request.getServiceId();
		String serviceName = request.getServiceName();

		ValidationData validationData = request.validateAddBalanceOperationParameters();

		if(validationData!=null){
			LogManager.getLogger().error(MODULE, "Unable to add monetary balance. " +
					"Reason: "+validationData.getMessage());
			return new MonetoryOperationResponse(validationData.getCode().code,
					validationData.getCode().name + " Reason: "+validationData.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		PolicyRepository policyRepository = DefaultNVSMXContext.getContext().getPolicyRepository();

		Service serviceData;

		if(isNullOrBlank(serviceId) == false){
			serviceData = policyRepository.getService().byId(serviceId);

			if(serviceData == null){
				LogManager.getLogger().error(MODULE, "Unable to add monetary balance. Reason: Invalid serviceId");
				return new MonetoryOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + " Reason: : Invalid serviceId", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			if(isNullOrBlank(serviceName)==false && serviceName.equals(serviceData.getName())==false){
				LogManager.getLogger().error(MODULE, "Unable to add monetary balance. Reason: serviceId and serviceName does not refer to same service");
				return new MonetoryOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + " Reason: : serviceId and serviceName does not refer to same service", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		} else {
			if(isNullOrBlank(serviceName) || Objects.equals(CommonConstants.ALL_SERVICE_DISPLAY_VALUE, serviceName)){
				serviceData = new Service(null, serviceName, PkgStatus.ACTIVE);
			}else {

				serviceData = policyRepository.getService().byName(serviceName);
				if (serviceData == null) {
					LogManager.getLogger().error(MODULE, "Unable to add monetary balance. Reason: Invalid serviceName");
					return new MonetoryOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + " Reason: : Invalid serviceName", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}
		}

		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();

		try{
			SPRInfo sprInfo;
			String subscriberIdBySpr = subscriberIdentity;

			if (isNullOrBlank(subscriberIdentity) == false) {
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);
				if(sprInfo==null){
					LogManager.getLogger().error(MODULE, "Subscriber ID not found.");
					return new MonetoryOperationResponse(ResultCode.NOT_FOUND.code, "Reason: Subscriber not found with subscriber identity: "+subscriberIdentity, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				if (isNullOrBlank(alternateIdentity) == false) {
					String subscriberIdByAlternateId = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateIdentity, adminStaff);
					if(subscriberIdentity.equals(subscriberIdByAlternateId)==false){
						LogManager.getLogger().error(MODULE, "Subscriber ID and Alternate ID do not match");
						return new MonetoryOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Subscriber ID and Alternate ID do not reference to the same subscriber", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}
			} else {
				subscriberIdBySpr = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateIdentity, adminStaff);
				if(isNullOrBlank(subscriberIdBySpr)){
					LogManager.getLogger().error(MODULE, "Alternate ID not found.");
					return new MonetoryOperationResponse(ResultCode.NOT_FOUND.code, "Reason: Subscriber not found with alternate identity: "+alternateIdentity, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);
			}
			// get currency based on Base Package
			String currency = getPackageCurrencyByName(sprInfo.getProductOffer());

			SubscriberMonetaryBalance balanceFromDb = SubscriberDAO.getInstance().getMonetaryBalance(subscriberIdBySpr, Predicates.ALL_MONETARY_BALANCE);

			long startDate = request.getValidFromDate()==null?System.currentTimeMillis():request.getValidFromDate();
			long expiryDate = request.getValidToDate()==null?CommonConstants.FUTURE_DATE:request.getValidToDate();
			String transactionId=Strings.isNullOrBlank(request.getTransactionId())?UUID.randomUUID().toString():request.getTransactionId();

			boolean isDefaultBalance = request.getValidFromDate()==null && request.getValidToDate()==null;
			MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),
					subscriberIdBySpr, serviceData.getId()
					, request.getTotalBalance(),request.getTotalBalance(), 0,0, 0, startDate,
					expiryDate, currency, isDefaultBalance?MonetaryBalanceType.DEFAULT.name():MonetaryBalanceType.PROMOTIONAL.name(), System.currentTimeMillis(),0, request.getParameter1(), request.getParameter2());

			MonetaryBalance existingBalance;
			if(isDefaultBalance){
				//If start date and end date are not given then get default existing balance for the same service.
				existingBalance = SubscriberMonetaryBalanceUtil.getDefaultBalance(serviceData.getId(),balanceFromDb);
			} else {
				//Get matching balance with same start date, end date and service.
				existingBalance = SubscriberMonetaryBalanceUtil.getMatchingBalance(balanceFromDb,monetaryBalance);
			}

			if(existingBalance!=null){
				double availableBalance = existingBalance.getAvailBalance();
				double initialBalance = existingBalance.getInitialBalance();

				MonetaryBalance previousMonetaryBalance = existingBalance.copy();

				existingBalance.setInitialBalance(0);
				existingBalance.setAvailBalance(monetaryBalance.getInitialBalance());

				SubscriberDAO.getInstance().updateMonetaryBalance(subscriberIdBySpr,
						new SubscriberMonetaryBalanceWrapper(existingBalance, previousMonetaryBalance, sprInfo,
								request.getWebServiceMethodName(), ActionType.UPDATE.name(),transactionId),
						"",request.getRequestIpAddress());

				monetaryBalance.setInitialBalance(initialBalance);
				monetaryBalance.setAvailBalance(availableBalance+monetaryBalance.getAvailBalance());
				monetaryBalance.setId(existingBalance.getId());
			} else {
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberIdBySpr,
						new SubscriberMonetaryBalanceWrapper(monetaryBalance, null, sprInfo,
						request.getWebServiceMethodName(), ActionType.ADD.name(),transactionId),
						"",request.getRequestIpAddress());
			}

			MonetaryBalanceData monetaryBalanceData = MonetaryBalanceData.create(monetaryBalance);

			monetaryBalanceData.setServiceName(serviceData.getName());

			return new MonetoryOperationResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, monetaryBalanceData, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while adding monetary balance for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			if (e.getErrorCode() == ResultCode.INTERNAL_ERROR) {
				getLogger().trace(MODULE,e);
			}
			return new MonetoryOperationResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while adding monetary balance for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			return new MonetoryOperationResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	private String getPackageCurrencyByName(String  packageName){
		String currency = "";
		PolicyRepository policyRepository = DefaultNVSMXContext.getContext().getPolicyRepository();
		ProductOffer productOffer = policyRepository.getProductOffer().byName(packageName);
		if(productOffer != null && productOffer.getCurrency() != null){
			currency = productOffer.getCurrency();
		}
		return currency;
	}

	private MonetaryBalanceInquiryResponse doMonetaryBalanceInquiry(MonetaryBalanceEnquiryRequest request) {
		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();
		String serviceId = request.getServiceId();
		String serviceName = request.getServiceName();
		String parameter2 = request.getParameter2();
		String parameter1 = request.getParameter1();

		if(isNullOrBlank(subscriberIdentity) && isNullOrBlank(alternateIdentity)){
			LogManager.getLogger().error(MODULE, "Unable to fetch monetary balance. Reason: Identity parameter missing");
			return new MonetaryBalanceInquiryResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
					ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		PolicyRepository policyRepository = DefaultNVSMXContext.getContext().getPolicyRepository();
		Service serviceData=null;

		if(isNullOrBlank(serviceId) == false){
			serviceData = policyRepository.getService().byId(serviceId);

			if(serviceData == null){
				LogManager.getLogger().error(MODULE, "Unable to add monetary balance. Reason: Invalid serviceId");
				return new MonetaryBalanceInquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
						ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Invalid serviceId", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			if(isNullOrBlank(serviceName)==false && serviceName.equals(serviceData.getName())==false){
				LogManager.getLogger().error(MODULE, "Unable to add monetary balance. Reason: serviceId and serviceName does not refer to same service");
				return new MonetaryBalanceInquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
						ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: serviceId and serviceName does not refer to same service", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		} else {
			if(isNullOrBlank(serviceName)==false){
				serviceData = policyRepository.getService().byName(serviceName);
				if(serviceData == null){
					LogManager.getLogger().error(MODULE, "Unable to add monetary balance. Reason: Invalid serviceName");
					return new MonetaryBalanceInquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
							ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Invalid serviceName", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}
		}

		try{

			if(isNullOrBlank(alternateIdentity) == false) {
				String subscriberIdByAlternateId = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateIdentity, adminStaff);

				if(isNullOrBlank(subscriberIdentity) == false && subscriberIdentity.equals(subscriberIdByAlternateId)== false) {
					LogManager.getLogger().error(MODULE, "Subscriber ID and Alternate ID do not match");
					return new MonetaryBalanceInquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Subscriber ID and Alternate ID do not reference to the same subscriber", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				subscriberIdentity = subscriberIdByAlternateId;
			}

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching monetary balance with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());

			if (e.getErrorCode() == ResultCode.INTERNAL_ERROR) {
				getLogger().trace(MODULE,e);
			}

			return new MonetaryBalanceInquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		SPRInfo sprInfo;
		try{
			sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);

			if(sprInfo == null){
				getLogger().error(MODULE, "Unable to fetch monetary balance for subscriber Id: " + subscriberIdentity + ". Reason: Subscriber not found");
				return new MonetaryBalanceInquiryResponse(ResultCode.NOT_FOUND.code,
						ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with subscriber identity:" +subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

			String subscriberIdentityBySprInfo = sprInfo.getSubscriberIdentity();

			SubscriberMonetaryBalance monetaryBalance = SubscriberDAO.getInstance().getMonetaryBalance(subscriberIdentityBySprInfo, Predicates.ALL_MONETARY_BALANCE);
			if(serviceData==null) {
				List<MonetaryBalanceData> monetaryBalancesForWS = getMonetaryBalancesForWS(monetaryBalance);
				if (monetaryBalancesForWS.isEmpty()) {
					return new MonetaryBalanceInquiryResponse(ResultCode.NOT_FOUND.code, "Monetary balance does not exist for subscriber Id: " + subscriberIdentityBySprInfo, monetaryBalancesForWS, parameter1, parameter2,
							request.getWebServiceName(), request.getWebServiceMethodName());
				} else {
					return new MonetaryBalanceInquiryResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, monetaryBalancesForWS, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			} else {
				List<MonetaryBalanceData> monetaryBalancesForWS = getServiceWiseMonetaryBalancesForWS(monetaryBalance, serviceData.getId());
				if (monetaryBalancesForWS.isEmpty()) {
					return new MonetaryBalanceInquiryResponse(ResultCode.NOT_FOUND.code, "Monetary balance for service " + request.getServiceId() + " does not exist.", monetaryBalancesForWS, parameter1, parameter2,
							request.getWebServiceName(), request.getWebServiceMethodName());
				} else {
					return new MonetaryBalanceInquiryResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, monetaryBalancesForWS, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}
		}catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching monetary balance with subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			if (e.getErrorCode() == ResultCode.INTERNAL_ERROR) {
				getLogger().trace(MODULE,e);
			}
			return new MonetaryBalanceInquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while fetching monetary balance with subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			return new MonetaryBalanceInquiryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	private NonMonitoryBalanceInquiryResponse doNonMonitoryBalanceInquiry(NonMonitoryBalanceEnquiryRequest request) {

		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();
		String pkgId = request.getPackageId();
		String pkgName = request.getPackageName();
		String subscriptionId = request.getSubscriptionId();
		String parameter2 = request.getParameter2();
		String parameter1 = request.getParameter1();
		SubscriberNonMonitoryBalance rncBalance = null;
		List<SubscriptionInformation> umBalance = null;
		SPRInfo sprInfo = null;

		StringBuilder skippedPackagesWithoutQuota = new StringBuilder();
		StringBuilder skippedSyQuotaProfilePkgs = new StringBuilder();
		List<RnCSubscriptionInformatonData> balanceForWS = new ArrayList<>();

		if(isNullOrBlank(subscriberIdentity) && isNullOrBlank(alternateIdentity)){
			LogManager.getLogger().error(MODULE, "Unable to fetch balance. Reason: Identity parameter missing");
			return new NonMonitoryBalanceInquiryResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
					ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		if (isNullOrBlank(subscriberIdentity)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE,"Subscriber ID not received");
			}
			try{
				sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateIdentity,adminStaff);

				if(sprInfo == null){
					getLogger().error(MODULE, "Unable to fetch balance for alternate Id: " + alternateIdentity);
					return new NonMonitoryBalanceInquiryResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with alternate identity:" +alternateIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscriberIdentity = sprInfo.getSubscriberIdentity();
			}catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while fetching balance with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE,e);
				return new NonMonitoryBalanceInquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			} catch (UnauthorizedActionException e) {
				//This exception will never going to be occur
				getLogger().error(MODULE, "Error while fetching balance with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE,e);
				return new NonMonitoryBalanceInquiryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		} else {
			try{
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);

				if(sprInfo == null){
					getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: " + subscriberIdentity);
					return new NonMonitoryBalanceInquiryResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with subscriber identity:" +subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while fetching balance with subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE,e);
				return new NonMonitoryBalanceInquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			} catch (UnauthorizedActionException e) {
				//This exception will never going to be occur
				getLogger().error(MODULE, "Error while fetching balance with subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE,e);
				return new NonMonitoryBalanceInquiryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}
		try{
			if(isNullOrBlank(pkgId) && isNullOrBlank(pkgName) && isNullOrBlank(subscriptionId)) {
				rncBalance = SubscriberDAO.getInstance().getRGNonMonitoryBalanceWithResetExpiredBalance(sprInfo,null,null);
				umBalance = SubscriberDAO.getInstance().getAllBalance(sprInfo);
			}else{
				/*
				 * if package name provided then package should be found from repository
				 */
				UserPackage pkg =null;
				if (isNullOrBlank(pkgName) == false) {

					pkg = DefaultNVSMXContext.getContext().getPolicyRepository().getPkgDataByName(pkgName);

					if (pkg == null) {
						LogManager.getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: "+subscriberIdentity+" with package name: " + pkgName);
						return new NonMonitoryBalanceInquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: No package found with name: " + pkgName+" for subscriber Identity: "+subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					if (pkg.getStatus() == PolicyStatus.FAILURE) {
						LogManager.getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: " + subscriberIdentity + " with package name: " + pkgName
								+ ". Reason: Package(" + pkgName + ") is failed package");
						return new NonMonitoryBalanceInquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Package(" + pkgName + ") is failed package for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					/*
					 * if
					 * 	package name and package id both are provided then check the relation between package id and name. They must belong to same package
					 */
					if(isNullOrBlank(pkgId) == false){
						if(pkg.getId().equalsIgnoreCase(pkgId) == false){
							LogManager.getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: "+subscriberIdentity+". Reason: Package name: " + pkgName + " and id: " + pkg.getId() +
									" are not related");
							return new NonMonitoryBalanceInquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
									ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Package name: " + pkgName + " and id: " + pkg.getId() +
											" are not related for subscriber identity: "+subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
						}
					}
					pkgId = pkg.getId();
				} else if(isNullOrBlank(pkgId) == false) {
					pkg = DefaultNVSMXContext.getContext().getPolicyRepository().getPkgDataById(pkgId);

					if (pkg == null) {
						LogManager.getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: "+subscriberIdentity+" with package name: " + pkgName);
						return new NonMonitoryBalanceInquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: No package found with id: " + pkgId+" for subscriber Identity: "+subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					if (pkg.getStatus() == PolicyStatus.FAILURE) {
						LogManager.getLogger().error(MODULE, "Unable to fetch balance for subscriber Id: " + subscriberIdentity + " with package name: " + pkgName
								+ ". Reason: Package(" + pkg.getName() + " with Id "+pkgId+") is failed package");
						return new NonMonitoryBalanceInquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Package(" + pkg.getName() + " with Id "+pkgId+") is failed package for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}

				NonMonitoryBalanceSubscriptionCheckForQuotaType check = isUMBasedPackageOrSubscription(sprInfo,pkg,subscriptionId);

				if(check.isRnCBased) {
					rncBalance = SubscriberDAO.getInstance().getRGNonMonitoryBalanceWithResetExpiredBalance(sprInfo, pkgId, subscriptionId);
				} else if(check.isUMBased){
					umBalance = SubscriberDAO.getInstance().getBalanceBySubscriberId(sprInfo, pkgId, subscriptionId);
				} else {
					throw new OperationFailedException("Reason: No subscription found for "+(pkgId==null?"":"package Id "+pkgId+ (subscriptionId==null && pkgId!=null?"":" and"))+(subscriptionId==null?"":" subscription ID: " + subscriptionId),ResultCode.NOT_FOUND);
				}
			}

			if (Collectionz.isNullOrEmpty(umBalance) == false) {
				getUMBalanceForWS(umBalance, skippedSyQuotaProfilePkgs, skippedPackagesWithoutQuota,balanceForWS);
			}
			if (rncBalance != null) {
				getRnCBalanceForWS(subscriberIdentity,rncBalance, skippedPackagesWithoutQuota,balanceForWS);
			}
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching balance with subscriber Id: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			return new NonMonitoryBalanceInquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while fetching balance with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			return new NonMonitoryBalanceInquiryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		return new NonMonitoryBalanceInquiryResponse(ResultCode.SUCCESS.code, createResponseMessage(skippedSyQuotaProfilePkgs, skippedPackagesWithoutQuota), balanceForWS, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
	}


	private RnCBalanceEnquiryResponse doRnCBalanceEnquiry(RnCBalanceEnquiryRequest request) {

		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();
		String pkgId = request.getPackageId();
		String pkgName = request.getPackageName();
		String subscriptionId = request.getSubscriptionId();
		String parameter2 = request.getParameter2();
		String parameter1 = request.getParameter1();
		SubscriberRnCNonMonetaryBalance rncBalance = null;
		SPRInfo sprInfo = null;

		List<RnCBalanceInformationData> rncBalanceForWS = new ArrayList<>();

		if (isNullOrBlank(subscriberIdentity) && isNullOrBlank(alternateIdentity)) {
			LogManager.getLogger().error(MODULE, "Unable to fetch RnC balance. Reason: Identity parameter missing");
			return new RnCBalanceEnquiryResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
					ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		if (isNullOrBlank(subscriberIdentity)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Subscriber ID not received");
			}
			try {
				sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateIdentity, adminStaff);

				if (sprInfo == null) {
					getLogger().error(MODULE, "Unable to fetch RnC balance for alternate Id: " + alternateIdentity);
					return new RnCBalanceEnquiryResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with alternate identity:" + alternateIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				subscriberIdentity = sprInfo.getSubscriberIdentity();
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while fetching balance with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				return new RnCBalanceEnquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			} catch (UnauthorizedActionException e) {
				//This exception will never going to be occur
				getLogger().error(MODULE, "Error while fetching balance with alternateId: " + alternateIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				return new RnCBalanceEnquiryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		} else {
			try {
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, adminStaff);

				if (sprInfo == null) {
					getLogger().error(MODULE, "Unable to fetch RnC balance for subscriber Id: " + subscriberIdentity);
					return new RnCBalanceEnquiryResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found with subscriber identity:" + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Error while fetching RnC balance with subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				return new RnCBalanceEnquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			} catch (UnauthorizedActionException e) {
				//This exception will never going to be occur
				getLogger().error(MODULE, "Error while fetching RnC balance with subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				return new RnCBalanceEnquiryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}
		try {
			if (isNullOrBlank(pkgId) && isNullOrBlank(pkgName) && isNullOrBlank(subscriptionId)) {
				rncBalance = SubscriberDAO.getInstance().getRncNonMonetaryBalance(sprInfo, null, null);
			} else {
				/*
				 * if package name provided then package should be found from repository
				 */
				RnCPackage pkg = null;
				if (isNullOrBlank(pkgName) == false) {

					pkg = DefaultNVSMXContext.getContext().getPolicyRepository().getRnCPackage().byName(pkgName);

					if (pkg == null) {
						LogManager.getLogger().error(MODULE, "Unable to fetch RnC balance for subscriber Id: " + subscriberIdentity + " with package name: " + pkgName);
						return new RnCBalanceEnquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: No RnC package found with name: " + pkgName + " for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					if (pkg.getPolicyStatus() == PolicyStatus.FAILURE) {
						LogManager.getLogger().error(MODULE, "Unable to fetch RnC balance for subscriber Id: " + subscriberIdentity + " with package name: " + pkgName
								+ ". Reason: Package(" + pkgName + ") is failed package");
						return new RnCBalanceEnquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: RnC Package(" + pkgName + ") is failed package for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					/*
					 * if
					 * 	package name and package id both are provided then check the relation between package id and name. They must belong to same package
					 */
					if (isNullOrBlank(pkgId) == false) {
						if (pkg.getId().equalsIgnoreCase(pkgId) == false) {
							LogManager.getLogger().error(MODULE, "Unable to fetch RnC balance for subscriber Id: " + subscriberIdentity + ". Reason: Package name: " + pkgName + " and id: " + pkg.getId() +
									" are not related");
							return new RnCBalanceEnquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
									ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: RnC Package name: " + pkgName + " and id: " + pkg.getId() +
											" are not related for subscriber identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
						}
					}
					pkgId = pkg.getId();
				} else if (isNullOrBlank(pkgId) == false) {
					pkg = DefaultNVSMXContext.getContext().getPolicyRepository().getRnCPackage().byId(pkgId);

					if (pkg == null) {
						LogManager.getLogger().error(MODULE, "Unable to fetch RnC balance for subscriber Id: " + subscriberIdentity + " with package id: " + pkgId);
						return new RnCBalanceEnquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: No RnC package found with id: " + pkgId + " for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}

					if (pkg.getPolicyStatus() == PolicyStatus.FAILURE) {
						LogManager.getLogger().error(MODULE, "Unable to fetch RnC balance for subscriber Id: " + subscriberIdentity + " with package id: " + pkgId
								+ ". Reason: Package(" + pkg.getName() + " with Id " + pkgId + ") is failed package");
						return new RnCBalanceEnquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: RnC Package(" + pkg.getName() + " with Id " + pkgId + ") is failed package for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				} else if (isNullOrBlank(subscriptionId) == false){
					RnCBalanceEnquiryResponse rnCBalanceEnquiryResponse = getRnCBalanceEnquiryResponse(request, subscriberIdentity, subscriptionId);
					if (rnCBalanceEnquiryResponse != null) {
						return rnCBalanceEnquiryResponse;
					}
				}

				rncBalance = SubscriberDAO.getInstance().getRncNonMonetaryBalance(sprInfo, pkgId, subscriptionId);

			}

			if (rncBalance != null) {
				getRncNonMonetaryBalanceForWS(rncBalance, rncBalanceForWS);
			}
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while fetching balance with subscriber Id: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			return new RnCBalanceEnquiryResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		return new RnCBalanceEnquiryResponse(ResultCode.SUCCESS.code,ResultCode.SUCCESS.name, rncBalanceForWS, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	private RnCBalanceEnquiryResponse getRnCBalanceEnquiryResponse(RnCBalanceEnquiryRequest request, String subscriberIdentity, String subscriptionId) throws OperationFailedException {
		List<Subscription> subscriptions = null;
		try {
			subscriptions = SubscriberDAO.getInstance().getSubscriptions(subscriberIdentity, adminStaff);
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while fetching Subscriptions with subscriber id: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			return new RnCBalanceEnquiryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		Subscription subscription = getSubscriptionById(subscriptions,subscriptionId);
		if(Objects.isNull(subscription)){
			LogManager.getLogger().error(MODULE, "Unable to fetch Subscription with subscriber id: " + subscriberIdentity + "and with subscription id: " + subscriptionId);
			return new RnCBalanceEnquiryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
					ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: No Subscription found with id: " + subscriptionId+ " for subscriber Identity: " + subscriberIdentity, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		return null;
	}

	private NonMonitoryBalanceSubscriptionCheckForQuotaType isUMBasedPackageOrSubscription(SPRInfo sprInfo, UserPackage userPackage, String subscriptionId) throws UnauthorizedActionException,OperationFailedException{

		NonMonitoryBalanceSubscriptionCheckForQuotaType check = new NonMonitoryBalanceSubscriptionCheckForQuotaType();

		if(userPackage!=null){
			isUMBasedPackageOrSubscription(userPackage,check);
			return check;
		}

		List<Subscription> subscriptions = SubscriberDAO.getInstance().getSubscriptions(sprInfo.getSubscriberIdentity(),adminStaff);

		if(subscriptionId!=null) {
			Subscription subscription = getSubscriptionById(subscriptions,subscriptionId);

			if(subscription!=null){
				UserPackage subscriptionPackage = DefaultNVSMXContext.getContext()
						.getPolicyRepository().getPkgDataById(subscription.getPackageId());

				if(subscriptionPackage != null){
					isUMBasedPackageOrSubscription(subscriptionPackage, check);
					return check;
				}
			}

			throw new OperationFailedException("No subscription found for subscription ID: " + subscriptionId,ResultCode.NOT_FOUND);
		}

		return check;
	}

	private NonMonitoryBalanceSubscriptionCheckForQuotaType isUMBasedPackageOrSubscription(UserPackage userPackage, NonMonitoryBalanceSubscriptionCheckForQuotaType check){

		if(userPackage.getQuotaProfileType()== QuotaProfileType.RnC_BASED)
			if(check.isRnCBased==false)
				check.isRnCBased=true;

		if(userPackage.getQuotaProfileType()== QuotaProfileType.USAGE_METERING_BASED)
			if(check.isUMBased==false)
				check.isUMBased=true;

		return check;
	}

	private List<MonetaryBalanceData> getMonetaryBalancesForWS(SubscriberMonetaryBalance subscriberMonetaryBalance) {
		Collection<MonetaryBalance> monetaryBalances = subscriberMonetaryBalance.getAllBalance();

		List<MonetaryBalanceData> monetaryBalanceforWS = new ArrayList<>();

		monetaryBalances.forEach(balance -> {
					MonetaryBalanceData monetaryBalanceData = MonetaryBalanceData.create(balance);
					Service serviceData = DefaultNVSMXContext.getContext().getPolicyRepository().getService().byId(monetaryBalanceData.getServiceId());
					if (serviceData != null) {
						monetaryBalanceData.setServiceName(serviceData.getName());
					}
					monetaryBalanceforWS.add(monetaryBalanceData);
				}

		);

		return monetaryBalanceforWS;
	}

	private List<MonetaryBalanceData> getServiceWiseMonetaryBalancesForWS(SubscriberMonetaryBalance subscriberMonetaryBalance, String serviceIdentity) {
		Collection<MonetaryBalance> monetaryBalances = subscriberMonetaryBalance.getAllBalance();

		List<MonetaryBalanceData> monetaryBalanceforWS = new ArrayList<>();

		if (monetaryBalances.isEmpty()) {
			return monetaryBalanceforWS;
		}

		for(MonetaryBalance monetaryBalance : monetaryBalances){

			String balanceServiceId = monetaryBalance.getServiceId();
			if(balanceServiceId==null || balanceServiceId.equals(serviceIdentity)==false){
				continue;
			}

			MonetaryBalanceData monetaryBalanceData = MonetaryBalanceData.create(monetaryBalance);
			Service serviceData = DefaultNVSMXContext.getContext().getPolicyRepository().getService().byId(monetaryBalanceData.getServiceId());
			if (serviceData != null) {
				monetaryBalanceData.setServiceName(serviceData.getName());
			}

			monetaryBalanceforWS.add(monetaryBalanceData);
		}

		return monetaryBalanceforWS;
	}

	private void getRnCBalanceForWS(String subscriberIdentity, SubscriberNonMonitoryBalance balance,
									StringBuilder skippedPackagesWithoutQuota,
									List<RnCSubscriptionInformatonData> balanceForWS)
			throws UnauthorizedActionException,OperationFailedException{
		List<Subscription> addonSubscriptions = SubscriberDAO.getInstance().getSubscriptions(subscriberIdentity,adminStaff);

		for(SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance:balance.getPackageBalances().values()){
			UserPackage userPackage=null;
			Subscription subscription = null;
			QuotaTopUp quotaTopUp = null;
			ProductOffer productOffer = null;

			for(com.elitecore.corenetvertex.spr.QuotaProfileBalance quotaProfileBalance : subscriptionNonMonitoryBalance.getAllQuotaProfileBalance().values()){
				ArrayList<NonMonetoryBalance> hsqBalances = quotaProfileBalance.getHsqBalance();

				if(Collectionz.isNullOrEmpty(hsqBalances)){
					break;
				}
				NonMonetoryBalance hsqBalance = hsqBalances.get(0);

				subscription = getSubscriptionById(addonSubscriptions,hsqBalance.getSubscriptionId());
				userPackage=DefaultNVSMXContext.getContext().getPolicyRepository().getPkgDataById(hsqBalance.getPackageId());
				quotaTopUp = DefaultNVSMXContext.getContext().getPolicyRepository().getQuotaTopUpById(hsqBalance.getPackageId());

				if(isNullOrBlank(hsqBalance.getProductOfferId())){
					break;
				}

				productOffer = DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().byId(hsqBalance.getProductOfferId());

				break;
			}

			if(Objects.isNull(userPackage) && Objects.isNull(quotaTopUp)) {
				continue;
			}

			if(userPackage!=null && PkgType.BASE != userPackage.getPackageType() && subscription==null){
				continue;
			}

			if(quotaTopUp!=null && subscription==null){
				continue;
			}

			String id;
			String name;
			String packageType;
			String description;
			Timestamp availabilityEndDate;
			Timestamp availabilityStartDate;
			List<QuotaProfile> quotaProfiles;

			if(userPackage!=null){
				id = userPackage.getId();
				name = userPackage.getName();
				packageType = userPackage.getType();
				description = userPackage.getDescription();
				availabilityEndDate = userPackage.getAvailabilityEndDate();
				availabilityStartDate = userPackage.getAvailabilityStartDate();
				quotaProfiles = userPackage.getQuotaProfiles();

			} else {
				id = quotaTopUp.getId();
				name = quotaTopUp.getName();
				packageType = quotaTopUp.getPackageType().name();
				description = quotaTopUp.getDescription();
				availabilityStartDate = quotaTopUp.getAvailabilityStartDate();
				availabilityEndDate = quotaTopUp.getAvailabilityEndDate();
				quotaProfiles = Arrays.asList(quotaTopUp.getQuotaProfile());
			}

			if(subscription!=null){
				availabilityStartDate = subscription.getStartTime();
				availabilityEndDate = subscription.getEndTime();
			}

			RnCSubscriptionInformatonData rnCSubscription = new RnCSubscriptionInformatonData();

			rnCSubscription.setPackageId(id);
			rnCSubscription.setPackageName(name);
			rnCSubscription.setPackageType(packageType);
			rnCSubscription.setPackageDescription(description);
			rnCSubscription.setEndTime(availabilityEndDate==null?null:availabilityEndDate.getTime());
			rnCSubscription.setStartTime(availabilityStartDate==null?null:availabilityStartDate.getTime());

			if(productOffer!=null){
				rnCSubscription.setProductOfferId(productOffer.getId());
				rnCSubscription.setProductOfferName(productOffer.getName());
			}

			if(subscription!=null){
				rnCSubscription.setAddonSubscriptionId(subscription.getId());
				rnCSubscription.setAddOnStatus(subscription.getStatus());
			} else {
				rnCSubscription.setAddonSubscriptionId(null);
				rnCSubscription.setAddOnStatus(null);
			}

			if(quotaProfiles.isEmpty()) {
				skippedPackagesWithoutQuota.append(name + ", ");
				continue;
			}

			List<RnCQuotaProfileBalance> quotaProfileBalance = new ArrayList<>();

			createQuotaProfileBalance(quotaProfiles,quotaProfileBalance,subscriptionNonMonitoryBalance);

			rnCSubscription.setQuotaprofileBalance(quotaProfileBalance);
			balanceForWS.add(rnCSubscription);
		}

	}

	// get Rnc Non monetaray balance
	private void getRncNonMonetaryBalanceForWS(SubscriberRnCNonMonetaryBalance balance,
											   List<RnCBalanceInformationData> balanceForWS){

		for(SubscriptionRnCNonMonetaryBalance subscriptionNonMonitoryBalance:balance.getPackageBalances().values()){
			RnCPackage rncPackage=null;
			ProductOffer productOffer=null;

			for(RnCNonMonetaryBalance rncNonMonetaryBalance : subscriptionNonMonitoryBalance.getAllRateCardBalance().values()){
				rncPackage=getNVSMXPolicyRepository().getRnCPackage().byId(rncNonMonetaryBalance.getPackageId());

				if(isNullOrBlank(rncNonMonetaryBalance.getProductOfferId())){
					break;
				}

				productOffer = getNVSMXPolicyRepository().getProductOffer().byId(rncNonMonetaryBalance.getProductOfferId());

				break;
			}

			if(Objects.isNull(rncPackage)) {
				return;
			}

			RnCBalanceInformationData rncBalanceInformationData = new RnCBalanceInformationData();

			rncBalanceInformationData.setPackageId(rncPackage.getId());
			rncBalanceInformationData.setPackageName(rncPackage.getName());

			if(productOffer != null){
				rncBalanceInformationData.setProductOfferId(productOffer.getId());
				rncBalanceInformationData.setProductOfferName(productOffer.getName());
			}

			rncBalanceInformationData.setPackageType(rncPackage.getPkgType().name());
			rncBalanceInformationData.setChargingType(rncPackage.getChargingType().name());
			rncBalanceInformationData.setPackageDescription(rncPackage.getDescription());
			rncBalanceInformationData.setAddonSubscriptionId(null);
			rncBalanceInformationData.setAddOnStatus(null);
			List<RateCardGroup> rateCardGroups = rncPackage.getRateCardGroups();
			if(rateCardGroups.isEmpty()) {
				return;
			}
			Map<String, RnCNonMonetaryRateCardBalance> rncNonMonetaryRateCardBalances = new HashMap<>();
			for (RateCardGroup rateCardGroup : rateCardGroups) {
				createAndAddRateCardBalance(rateCardGroup.getPeakRateCard(), rncNonMonetaryRateCardBalances, subscriptionNonMonitoryBalance, rncPackage.getChargingType());
				createAndAddRateCardBalance(rateCardGroup.getOffPeakRateCard(), rncNonMonetaryRateCardBalances, subscriptionNonMonitoryBalance, rncPackage.getChargingType());

			}
			List<RnCNonMonetaryRateCardBalance> rncNonMonetaryBalances = Collectionz.newArrayList();
			for(RnCNonMonetaryRateCardBalance rncNonMonetaryBalance : rncNonMonetaryRateCardBalances.values()){
				rncNonMonetaryBalances.add(rncNonMonetaryBalance);
			}
			rncBalanceInformationData.setRncNonMonetaryRateCardBalance(rncNonMonetaryBalances);
			balanceForWS.add(rncBalanceInformationData);
		}

	}
	private void createQuotaProfileBalance(List<QuotaProfile> quotaProfiles, List<RnCQuotaProfileBalance> rnCQuotaProfileBalances, SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance){
		for(QuotaProfile quotaProfile: quotaProfiles){

			RnCQuotaProfileBalance rnCQuotaProfileBalance = new RnCQuotaProfileBalance();

			rnCQuotaProfileBalance.setQuotaProfileName(quotaProfile.getName());
			rnCQuotaProfileBalance.setQuotaProfileId(quotaProfile.getId());
			rnCQuotaProfileBalance.setBalanceLevel(quotaProfile.getBalanceLevel().getDisplayVal());
			rnCQuotaProfileBalance.setProration(quotaProfile.getProration());

			setAvailableHSQBalance(quotaProfile, rnCQuotaProfileBalance, subscriptionNonMonitoryBalance.getBalance(quotaProfile.getId()));

			createBalanceInformation(quotaProfile, rnCQuotaProfileBalance, subscriptionNonMonitoryBalance.getBalance(quotaProfile.getId()));

			rnCQuotaProfileBalances.add(rnCQuotaProfileBalance);
		}
	}

	private void createAndAddRateCardBalance(RateCard rateCard, Map<String, RnCNonMonetaryRateCardBalance> rncNonMonetaryRateCardBalances, SubscriptionRnCNonMonetaryBalance subscriptionRnCNonMonetaryBalance, ChargingType chargingType) {
		if(rateCard == null){
			return;
		}
		if(RateCardType.NON_MONETARY != rateCard.getType()){
			return;
		}
		NonMonetaryRateCard nonMonetaryRateCard = (NonMonetaryRateCard) rateCard;
		RnCNonMonetaryBalance rncNonMonetaryBalance = subscriptionRnCNonMonetaryBalance.getAllRateCardBalance().get(nonMonetaryRateCard.getId());
		if (nonNull(rncNonMonetaryBalance)) {
			if (rncNonMonetaryRateCardBalances.containsKey(nonMonetaryRateCard.getId()) == false) {
				rncNonMonetaryRateCardBalances.put(nonMonetaryRateCard.getId(),RnCNonMonetaryRateCardBalance.create(nonMonetaryRateCard, rncNonMonetaryBalance, chargingType));
			}
		}

	}






	private void setAvailableHSQBalance(QuotaProfile quotaProfile, RnCQuotaProfileBalance rnCQuotaProfileBalance, com.elitecore.corenetvertex.spr.QuotaProfileBalance quotaProfileBalance){

		int level = quotaProfile.getBalanceLevel().getFupLevel();
		QuotaUsageType quotaUsageType = ((RncProfileDetail)quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().values().iterator().next()).getQuotaUnit();
		long volumeBalance = 0;
		long timeBalance = 0;
		NonMonetoryBalance nonMonetoryBalance;
		for(int i = 0 ; i <= level ; i++){

			nonMonetoryBalance = quotaProfileBalance.getBalance(ALL_SERVICE_ID, DEFAULT_RATING_GROUP, i);
			if(nonNull(nonMonetoryBalance)) {
				if(QuotaUsageType.TIME != quotaUsageType) {
					volumeBalance = volumeBalance + nonMonetoryBalance.getBillingCycleAvailableVolume();
				}

				if(QuotaUsageType.VOLUME != quotaUsageType) {
					timeBalance = timeBalance + nonMonetoryBalance.getBillingCycleAvailableTime();
				}
			}
		}

		rnCQuotaProfileBalance.setAvailableHSQVolumeBalance(volumeBalance);
		rnCQuotaProfileBalance.setAvailableHSQTimeBalance(timeBalance);
	}

	private void createBalanceInformation(QuotaProfile quotaProfile, RnCQuotaProfileBalance rnCQuotaProfileBalance, com.elitecore.corenetvertex.spr.QuotaProfileBalance quotaProfileBalance){
		List<DataServiceTypeBalance> balanceInfos =  new ArrayList<>();
		for(Map<String, QuotaProfileDetail> quotaProfileDetail : quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()) {
			for (QuotaProfileDetail profileDetail : quotaProfileDetail.values()) {
				RncProfileDetail rncProfileDetail = (RncProfileDetail) profileDetail;

				setVolumeUnitTypeAndQuotaUsageTypeIfNull(rnCQuotaProfileBalance, rncProfileDetail);

				NonMonetoryBalance balance = quotaProfileBalance.getBalance(rncProfileDetail.getDataServiceType().getServiceIdentifier(), rncProfileDetail.getRatingGroup().getIdentifier(), rncProfileDetail.getFupLevel());

				if (balance != null) {

					DataServiceTypeBalance serviceTypeBalance = new DataServiceTypeBalance();

					serviceTypeBalance.setServiceId(rncProfileDetail.getServiceId());
					serviceTypeBalance.setServiceName(rncProfileDetail.getServiceName());
					serviceTypeBalance.setRatingGroupId(rncProfileDetail.getRatingGroupId());
					serviceTypeBalance.setExpiryTime(balance.getBillingCycleResetTime());

					for (BalanceLevel level : BalanceLevel.values()) {
						if (level.fupLevel == rncProfileDetail.getFupLevel()) {
							serviceTypeBalance.setLevel(level.displayVal);
						}
					}

					createAggregateBalance(serviceTypeBalance, balance, rncProfileDetail);

					balanceInfos.add(serviceTypeBalance);

				}
			}
		}
		rnCQuotaProfileBalance.setDataServiceTypeBalance(balanceInfos);
	}


	private void setVolumeUnitTypeAndQuotaUsageTypeIfNull(RnCQuotaProfileBalance rnCQuotaProfileBalance, RncProfileDetail rncProfileDetail){
		if(isNullOrBlank(rnCQuotaProfileBalance.getQuotaUsageType())){
			rnCQuotaProfileBalance.setQuotaUsageType(rncProfileDetail.getQuotaUnit().getValue());
		}
		if(isNullOrBlank(rnCQuotaProfileBalance.getVolumeUnitType())){
			rnCQuotaProfileBalance.setVolumeUnitType(rncProfileDetail.getUnitType().getValue());
		}
	}

	private void createAggregateBalance(DataServiceTypeBalance balanceInfo, NonMonetoryBalance balance, RncProfileDetail rncProfileDetail){

		RncBalance dailyUsage = new RncBalance();
		RncBalance weeklyUsage = new RncBalance();
		RncBalance billingCycleBalance = new RncBalance();
		CarryForwardBalance carryForwardBalance = new CarryForwardBalance();

		dailyUsage.setTotal(getAggregateAllowedUsage(rncProfileDetail,"daily",balance));
		weeklyUsage.setTotal(getAggregateAllowedUsage(rncProfileDetail,"weekly",balance));
		billingCycleBalance.setTotal(getAggregateAllowedUsage(rncProfileDetail,"billing",balance));

		dailyUsage.setActual(getAggregateActualBalance(balance,"daily",dailyUsage.getTotal()));
		weeklyUsage.setActual(getAggregateActualBalance(balance,"weekly",weeklyUsage.getTotal()));
		billingCycleBalance.setActual(getAggregateActualBalance(balance,"billing",billingCycleBalance.getTotal()));

		dailyUsage.setRemaining(getAggregateRemainingBalance(balance,"daily",dailyUsage.getTotal()));
		weeklyUsage.setRemaining(getAggregateRemainingBalance(balance,"weekly",weeklyUsage.getTotal()));
		billingCycleBalance.setRemaining(getAggregateRemainingBalance(balance,"billing",billingCycleBalance.getTotal()));

		carryForwardBalance.setVolume(balance.getCarryForwardVolume());
		carryForwardBalance.setTime(balance.getCarryForwardTime());

		balanceInfo.setDailyBalance(dailyUsage);
		balanceInfo.setWeeklyBalance(weeklyUsage);
		balanceInfo.setBillingCycleBalance(billingCycleBalance);
		balanceInfo.setCarryForwardBalance(carryForwardBalance);

	}

	private Usage getAggregateActualBalance(NonMonetoryBalance balance, String aggregationKey, Usage totalBalance){
		Usage usage = new Usage();

		switch (aggregationKey) {
			case "daily":
				getDailyActualBalance(balance, totalBalance, usage);
				break;

			case "weekly":
				getWeeklyActualBalance(balance, totalBalance, usage);
				break;

			case "billing":
				getBillingCycleActualBalance(balance, totalBalance, usage);
				break;
		}

		return  usage;
	}

	private void getBillingCycleActualBalance(NonMonetoryBalance balance, Usage totalBalance, Usage usage) {
		if (totalBalance.getTotalOctets() != null) {
			usage.setTotalOctets(balance.getBillingCycleAvailableVolume() - balance.getReservationVolume());
		} else {
			usage.setTotalOctets(null);
		}

		if (totalBalance.getTime() != null) {
			usage.setTime(balance.getBillingCycleAvailableTime() - balance.getReservationTime());
		} else {
			usage.setTime(null);
		}
	}

	private void getWeeklyActualBalance(NonMonetoryBalance balance, Usage totalBalance, Usage usage) {
		if (totalBalance.getTotalOctets() != null) {
			usage.setTotalOctets(totalBalance.getTotalOctets() - balance.getWeeklyVolume() - balance.getReservationVolume());
		} else {
			usage.setTotalOctets(null);
		}

		if (totalBalance.getTime() != null) {
			usage.setTime(totalBalance.getTime() - balance.getWeeklyTime() - balance.getReservationTime());
		} else {
			usage.setTime(null);
		}
	}

	private void getDailyActualBalance(NonMonetoryBalance balance, Usage totalBalance, Usage usage) {
		if(totalBalance.getTotalOctets()!=null) {
			usage.setTotalOctets(totalBalance.getTotalOctets() - balance.getDailyVolume() - balance.getReservationVolume());
		}else{
			usage.setTotalOctets(null);
		}

		if (totalBalance.getTime() != null) {
			usage.setTime(totalBalance.getTime() - balance.getDailyTime() - balance.getReservationTime());
		} else {
			usage.setTime(null);
		}
	}

	private Usage getAggregateRemainingBalance(NonMonetoryBalance balance,  String aggregationKey, Usage totalBalance){
		Usage usage = new Usage();

		switch (aggregationKey) {
			case "daily":
				getDailyRemainingBalance(balance, totalBalance, usage);
				break;

			case "weekly":
				getWeeklyRemainingBalance(balance, totalBalance, usage);
				break;

			case "billing":
				getBillingCycleRemainingBalance(balance, totalBalance, usage);
				break;
		}

		return  usage;
	}

	private void getBillingCycleRemainingBalance(NonMonetoryBalance balance, Usage totalBalance, Usage usage) {
		if (totalBalance.getTotalOctets() != null) {
			usage.setTotalOctets(balance.getBillingCycleAvailableVolume());
		} else {
			usage.setTotalOctets(null);
		}

		if (totalBalance.getTime() != null) {
			usage.setTime(balance.getBillingCycleAvailableTime());
		} else {
			usage.setTime(null);
		}
	}

	private void getWeeklyRemainingBalance(NonMonetoryBalance balance, Usage totalBalance, Usage usage) {
		if (totalBalance.getTotalOctets() != null) {
			usage.setTotalOctets(totalBalance.getTotalOctets() - balance.getWeeklyVolume());
		} else {
			usage.setTotalOctets(null);
		}

		if (totalBalance.getTime() != null) {
			usage.setTime(totalBalance.getTime() - balance.getWeeklyTime());
		} else {
			usage.setTime(null);
		}
	}

	private void getDailyRemainingBalance(NonMonetoryBalance balance, Usage totalBalance, Usage usage) {
		if (totalBalance.getTotalOctets() != null) {
			usage.setTotalOctets(totalBalance.getTotalOctets() - balance.getDailyVolume());
		} else {
			usage.setTotalOctets(null);
		}

		if (totalBalance.getTime() != null) {
			usage.setTime(totalBalance.getTime() - balance.getDailyTime());
		} else {
			usage.setTime(null);
		}
	}

	private Usage getAggregateAllowedUsage(RncProfileDetail rncProfileDetail, String aggregationKey, NonMonetoryBalance balance){
		Usage usage = new Usage();
		AllowedUsage allowedUsage = null;

		switch (aggregationKey) {
			case "daily":
				allowedUsage = rncProfileDetail.getAllowedUsage(AggregationKey.DAILY);
				break;
			case "weekly":
				allowedUsage = rncProfileDetail.getAllowedUsage(AggregationKey.WEEKLY);
				break;
			case "billing":
				allowedUsage = rncProfileDetail.getAllowedUsage(AggregationKey.BILLING_CYCLE);
				break;
		}

		if(allowedUsage!=null) {
			if (rncProfileDetail.getUnitType().getVolumeInBytes(allowedUsage) != CommonConstants.QUOTA_UNDEFINED) {
				if("billing".equals(aggregationKey)){
					usage.setTotalOctets(balance.getBillingCycleTotalVolume());
				} else {
					usage.setTotalOctets(rncProfileDetail.getUnitType().getVolumeInBytes(allowedUsage));
				}
			} else {
				usage.setTotalOctets(null);
			}

			if (allowedUsage.getTimeInSeconds() != CommonConstants.QUOTA_UNDEFINED) {
				if("billing".equals(aggregationKey)){
					usage.setTime(balance.getBillingCycleTime());
				} else {
					usage.setTime(allowedUsage.getTimeInSeconds());
				}
			} else {
				usage.setTime(null);
			}
		}

		return  usage;
	}

	private Subscription getSubscriptionById(List<Subscription> addonSubscriptions, String subscriptionId){
		for(Subscription subscription: addonSubscriptions)
			if(subscription.getId().equals(subscriptionId))
				return subscription;

		return null;
	}

	private void applyRequestInterceptors(WebServiceRequest request) {
		for(WebServiceInterceptor interceptor : interceptors){
			interceptor.requestReceived(request);
		}
	}

	private void applyResponseInterceptors(WebServiceResponse response) {
		for(WebServiceInterceptor interceptor : interceptors){
			interceptor.responseReceived(response);
		}
	}

	private class NonMonitoryBalanceSubscriptionCheckForQuotaType {
		boolean isRnCBased=false;
		boolean isUMBased=false;
	}

	public SubscriptionResponse changeRnCAddonSubscription(ChangeRnCAddOnProductOfferWSRequest request) {
		this.changeRnCAddonSubscriptionProcessor.preProcess(request);
		SubscriptionResponse response = this.changeRnCAddonSubscriptionProcessor.process(request, adminStaff);
		this.changeRnCAddonSubscriptionProcessor.postProcess(request, response);
		return response;
	}

	private ProductOffer getProductOfferByName(String name) {
		return DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().addOn().byName(name);
	}

	private ProductOffer getProductOfferById(String id) {
		return DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().addOn().byId(id);
	}

	private SubscriptionResponse checkSubscriberProfileInfo(String productOfferName, String id, SPRInfo sprInfo, ChangeDataAddOnSubscriptionWSRequest request) {
		if (sprInfo == null) {
			getLogger().error(MODULE, "Unable to change data addon product offer(" + productOfferName + ") subscription. Reason: Subscriber not found with Id: " + id);
			return new SubscriptionResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(SubscriberStatus.INACTIVE.name().equals(sprInfo.getStatus())){
			getLogger().error(MODULE, "Unable to change data addon product offer(" + productOfferName + ") subscription. Reason: Subscriber status is InActive for subscriber Id: " + sprInfo.getSubscriberIdentity());
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
					ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Status is InActive", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(sprInfo.getProfileExpiredHours() >= 0){
			getLogger().error(MODULE, "Unable to change data addOn product offer(" + productOfferName + ") subscription. Reason: Subscriber Profile has expired for subscriber Id: " + sprInfo.getSubscriberIdentity());
			return new SubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
					ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Profile has expired", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		return null;
	}

	public UpdateCreditLimitResponse updateCreditLimit(UpdateCreditLimitWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preUpdateCreditLimit(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		UpdateCreditLimitResponse response = doUpdateCreditLimit(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postUpdateCreditLimit(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private UpdateCreditLimitResponse doUpdateCreditLimit(UpdateCreditLimitWSRequest request) {

		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();
		String creditLimit = request.getCreditLimit();
		String applicableBillingCycle = isNullOrBlank(request.getApplicableBillingCycle())?"1":request.getApplicableBillingCycle();
		String parameter1 = request.getParameter1();
		String parameter2 = request.getParameter2();

		ValidationData validationData = request.validateParameters();
		if(validationData!=null){
			LogManager.getLogger().error(MODULE, "Unable to add monetary balance. " +
					"Reason: "+validationData.getMessage());
			return new UpdateCreditLimitResponse(validationData.getCode().code,
					validationData.getCode().name + " Reason: "+validationData.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		try{

			String subscriberIdBySpr = subscriberIdentity;
			String packageName = "";
			if (isNullOrBlank(subscriberIdBySpr) == false) {
				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdBySpr, adminStaff);
				if(sprInfo==null){
					LogManager.getLogger().error(MODULE, "Subscriber ID not found.");
					return new UpdateCreditLimitResponse(ResultCode.NOT_FOUND.code, "Reason: Subscriber not found with subscriber identity: "+subscriberIdBySpr, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				packageName = sprInfo.getProductOffer();
				if (isNullOrBlank(alternateIdentity) == false) {
					String subscriberIdByAlternateId = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateIdentity, adminStaff);
					if(subscriberIdBySpr.equals(subscriberIdByAlternateId)==false){
						LogManager.getLogger().error(MODULE, "Subscriber ID and Alternate ID do not match");
						return new UpdateCreditLimitResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Subscriber ID and Alternate ID do not reference to the same subscriber", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}
			} else {
				subscriberIdBySpr = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateIdentity, adminStaff);
				if(isNullOrBlank(subscriberIdBySpr)){
					LogManager.getLogger().error(MODULE, "Alternate ID not found.");
					return new UpdateCreditLimitResponse(ResultCode.NOT_FOUND.code, "Reason: Subscriber not found with alternate identity: "+alternateIdentity, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

			SubscriberMonetaryBalance balanceFromDb = SubscriberDAO.getInstance().getMonetaryBalance(subscriberIdBySpr, Predicates.DEFAULT_MONETARY_BALANCE);
			String currency = getPackageCurrencyByName(packageName);

			SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdBySpr, adminStaff);
			Timestamp billDate = null;
			if(nonNull(sprInfo.getBillingDate())){
				billDate = RenewalIntervalUtility.getBillDate(new Timestamp(System.currentTimeMillis()),sprInfo.getBillingDate());
			}else{
				throw new OperationFailedException("Update Credit Limit operation is not supported for prepaid subscribers.", ResultCode.OPERATION_NOT_SUPPORTED);
			}

			MonetaryBalance monetaryBalance = null;
			for(MonetaryBalance balance: balanceFromDb.getAllBalance()){
				monetaryBalance = new MonetaryBalance.MonetaryBalanceBuilder(
						balance.getId(),
						balance.getSubscriberId(),
						null,
						currency,MonetaryBalanceType.DEFAULT.name(),
						System.currentTimeMillis())
						.withCreditLimit(balance.getCreditLimit())
						.withNextBillingCycleCreditLimit(balance.getNextBillingCycleCreditLimit())
						.build();

				if (StringUtils.equals(applicableBillingCycle,"0")) {
					monetaryBalance.setCreditLimit(isNullOrBlank(creditLimit) ? balance.getCreditLimit() : ConvertStringToDigit.convertStringToLong(creditLimit));
					monetaryBalance.setCreditLimitUpdateTime(nonNull(balance.getCreditLimitUpdateTime()) ? balance.getCreditLimitUpdateTime() : 0);

				} else if (StringUtils.equals(applicableBillingCycle,"1")) {
					monetaryBalance.setNextBillingCycleCreditLimit(isNullOrBlank(creditLimit) ? balance.getNextBillingCycleCreditLimit() : ConvertStringToDigit.convertStringToLong(creditLimit));
					monetaryBalance.setCreditLimitUpdateTime(billDate.getTime());

				}
			}

			if(monetaryBalance!=null){
				SubscriberDAO.getInstance().updateCreditLimit(subscriberIdBySpr,
						new SubscriberMonetaryBalanceWrapper(monetaryBalance, null, sprInfo,
						request.getWebServiceMethodName(), ActionType.UPDATE.name(),null),
						"", request.getRequestIpAddress());
			} else {
				monetaryBalance = new MonetaryBalance.MonetaryBalanceBuilder(
						UUID.randomUUID().toString(),
						subscriberIdBySpr,
						null,
						currency,MonetaryBalanceType.DEFAULT.name(),
						System.currentTimeMillis())
						.withValidFromDate(System.currentTimeMillis())
						.withValidToDate(CommonConstants.FUTURE_DATE)
						.build();

				if (StringUtils.equals(applicableBillingCycle,"0")) {
					monetaryBalance.setCreditLimit(isNullOrBlank(creditLimit) ? 0 : ConvertStringToDigit.convertStringToLong(creditLimit));
					monetaryBalance.setCreditLimitUpdateTime(0);

				} else if (StringUtils.equals(applicableBillingCycle,"1")) {
					monetaryBalance.setNextBillingCycleCreditLimit(isNullOrBlank(creditLimit) ? 0 : ConvertStringToDigit.convertStringToLong(creditLimit));
					monetaryBalance.setCreditLimitUpdateTime(billDate.getTime());

				}
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberIdBySpr,
						new SubscriberMonetaryBalanceWrapper(monetaryBalance, null, sprInfo,
								request.getWebServiceMethodName(), ActionType.ADD.name(),null),
						"",request.getRequestIpAddress());
			}

			BalanceEDRData monetaryBalanceData = new BalanceEDRData();
			monetaryBalanceData.setBalanceId(monetaryBalance.getId());
			monetaryBalanceData.setCreditLimit(monetaryBalance.getCreditLimit());
			monetaryBalanceData.setNextBillingCycleCreditLimit(monetaryBalance.getNextBillingCycleCreditLimit());
			monetaryBalanceData.setSubscriberId(monetaryBalance.getSubscriberId());

			return new UpdateCreditLimitResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, monetaryBalanceData, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while adding monetary balance for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			if (e.getErrorCode() == ResultCode.INTERNAL_ERROR) {
				getLogger().trace(MODULE,e);
			}
			return new UpdateCreditLimitResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while adding monetary balance for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			return new UpdateCreditLimitResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	public MonetaryRechargeSubscriptionResponse subscribeMonetaryRechargePlan(SubscribeMonetaryRechargePlanWSRequest request) throws ParseException, OperationFailedException {
		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.preSubscribeMonetaryRechargePlan(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		MonetaryRechargeSubscriptionResponse response = doSubscribeMonetaryRechargePlan(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.postSubscribeMonetaryRechargePlan(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private MonetaryRechargeSubscriptionResponse doSubscribeMonetaryRechargePlan(SubscribeMonetaryRechargePlanWSRequest request) throws ParseException, OperationFailedException {
		String subscriberId = request.getSubscriberId();
		String alternateId = request.getAlternateId();
		String monetaryRechargePlanId = request.getMonetaryRechargePlanId();
		String monetaryRechargePlanName = request.getMonetaryRechargePlanName();
		Integer updateAction = request.getUpdateAction();
		boolean updateBalanceIndication = request.getUpdateBalanceIndication();
		BigDecimal price = request.getPrice();
		long expiryDate= request.getExpiryDate();
		MonetaryRechargePlan monetaryRechargePlan = null;

		double previousAvailableBalance = 0;
		MonetaryBalance availableMonetaryBalance;
		long initialValidity = 0;
		Timestamp subscriberExpiry;
		SPRInfo sprInfo;

		try {
			if (isNullOrBlank(subscriberId)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Subscriber ID not received");
				}

				if (isNullOrBlank(alternateId)) {
					getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: Identity parameter missing");

					return new MonetaryRechargeSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
							ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, null, null, 0, 0, 0, 0,
							request.getWebServiceName(), request.getWebServiceMethodName());

				}

				sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);
				if (sprInfo == null) {
					getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: Subscriber not found with " + alternateId);
					return new MonetaryRechargeSubscriptionResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found", null, null, null, null, null, 0, 0, 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			} else {
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);
				if (sprInfo == null) {
					getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: Subscriber not found with " + subscriberId);
					return new MonetaryRechargeSubscriptionResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found", null, null, null, null, null, 0, 0, 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}
			subscriberId = sprInfo.getSubscriberIdentity();
			subscriberExpiry=sprInfo.getExpiryDate();
		} catch (OperationFailedException e) {

			getLogger().error(MODULE, "Error while subscribing monetary recharge plan for subscriber(" + subscriberId + "). Reason:" + e.getMessage());

			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}

			return new MonetaryRechargeSubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, null,
					null, 0, 0 , 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {

			getLogger().error(MODULE, "Error while subscribing monetary recharge plan for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			getLogger().trace(e);

			return new MonetaryRechargeSubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, null, null,
					0, 0 , 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (isNullOrBlank(monetaryRechargePlanId) == false) {
			monetaryRechargePlan = getNVSMXPolicyRepository().getActiveMonetaryRechargePlanById(monetaryRechargePlanId);

			if (monetaryRechargePlan == null) {
				getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: ACTIVE monetary recharge plan not found with id: " + monetaryRechargePlanId);
				return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "ACTIVE monetary recharge plan not found with id: " + monetaryRechargePlanId, null, null, null, null, null,0, 0 , 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
			}

			if (price == null) {
				price = monetaryRechargePlan.getPrice();
			}
		}

		if (isNullOrBlank(monetaryRechargePlanName) == false) {

			if (monetaryRechargePlan == null) {

				monetaryRechargePlan = getNVSMXPolicyRepository().getActiveMonetaryRechargePlanByName(monetaryRechargePlanName);
				if (monetaryRechargePlan == null) {
					getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: ACTIVE monetary recharge plan not found with name: " + monetaryRechargePlanName);
					return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "ACTIVE monetary recharge plan not found with name: " + monetaryRechargePlanName, null, null, null, null, null,0, 0, 0 , 0, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				if (price == null) {
					price = monetaryRechargePlan.getPrice();
				}

			} else if (monetaryRechargePlan.getName().equals(monetaryRechargePlanName) == false) {

				getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: monetary recharge plan id(" + monetaryRechargePlanId + ") and name(" + monetaryRechargePlanName
						+ ") are not related ");

				return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "monetary recharge plan id(" + monetaryRechargePlanId + ") and name(" + monetaryRechargePlanName
						+ ") are not related ", null, null, null, null, null,0, 0, 0 , 0, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if (price == null && monetaryRechargePlan == null) {
			getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: monetary recharge plan id or name or price must be provided");
			return new MonetaryRechargeSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "monetary recharge plan package id or name or price must be provided", null, null,
					null, null, null,0, 0 , 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (price != null && monetaryRechargePlan == null) {
			monetaryRechargePlan = getNVSMXPolicyRepository().getMonetaryRechargePlanByPrice(price);
			if (monetaryRechargePlan == null) {
				return doFlexiRechargeOfMonetaryBalance(request, subscriberId, expiryDate,subscriberExpiry,updateAction, price);
			}
		} else if (price != null && monetaryRechargePlan.getPrice().equals(price) == false) {
			getLogger().error(MODULE, "Unable to subscribe monetary recharge plan. Reason: monetary recharge plan id(" + monetaryRechargePlanId + ") and name(" + monetaryRechargePlanName
					+ ") are not related");
			return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "monetary recharge plan package id(" + monetaryRechargePlanId + ") and name(" + monetaryRechargePlanName
					+ ") are not related ", null, null, null, null, null,0, 0, 0 , 0, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (monetaryRechargePlan.getAvailabilityStatus() != PkgStatus.ACTIVE) {
			getLogger().error(MODULE, "Unable to subscribe monetary recharge plan(" + monetaryRechargePlan.getName() + ") for subscriber ID: " + subscriberId
					+ ". Reason: Monetary recharge plan(" + monetaryRechargePlan.getName() + ") is INACTIVE/RETIRED");
			return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Unable to subscribe monetary recharge plan(" + monetaryRechargePlan.getName() + ") for subscriber ID: " + subscriberId
					+ ". Reason: Monetary recharge plan(" + monetaryRechargePlan.getName() + ") is failed/partial fail plan", null, null, null, null, null,0, 0, 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (monetaryRechargePlan.getStatus() != PolicyStatus.SUCCESS) {

			getLogger().error(MODULE, "Unable to subscribe monetary recharge plan(" + monetaryRechargePlan.getName() + ") for subscriber ID: " + subscriberId
					+ ". Reason: Monetary recharge plan(" + monetaryRechargePlan.getName() + ") is failed/partial fail plan");
			return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Unable to subscribe monetary recharge plan(" + monetaryRechargePlan.getName() + ") for subscriber ID: " + subscriberId
					+ ". Reason: Monetary recharge plan(" + monetaryRechargePlan.getName() + ") is failed/partial fail plan", null, null, null, null, null,0, 0, 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());

		}



		try {

			if (validateSubscriberAndMonetaryRechargePlan(subscriberId, monetaryRechargePlan, sprInfo)) {
				return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Unable to subscribe monetary recharge plan(" + monetaryRechargePlan.getName() + ") for subscriber ID: " + subscriberId
						+ ". Reason: Monetary recharge plan(" + monetaryRechargePlan.getName() + ") is in TEST mode and subscriber mode is LIVE", null, null, null, null, null, 0, 0, 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			SubscriberMonetaryBalance monetaryBalance = SubscriberDAO.getInstance().getMonetaryBalance(subscriberId, Predicates.ALL_MONETARY_BALANCE);
			availableMonetaryBalance = monetaryBalance.getMainBalance();

			if (monetaryRechargePlan.getAmount().compareTo(BigDecimal.ZERO) == 0 && nonNull(expiryDate) && subscriberExpiry!=null) {
                  ///amount is zero so updating only profile
					doUpdateSubscriberExpiry(subscriberId,expiryDate,request.getRequestIpAddress());
					return new MonetaryRechargeSubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, null, null, subscriberId, monetaryRechargePlan.getId(),
							monetaryRechargePlan.getName(), previousAvailableBalance,
							availableMonetaryBalance.getAvailBalance(), initialValidity, expiryDate, request.getWebServiceName(), request.getWebServiceMethodName());
			}

			if (isMonetaryBalanceAvailableAndActive(availableMonetaryBalance) == false) {

				if (updateBalanceIndication == true) {
					getLogger().error(MODULE, "Unable to subscribe monetary recharge plan(" + monetaryRechargePlan.getName() + "). Reason: No sufficient monetary balance found.");
					return new MonetaryRechargeSubscriptionResponse(ResultCode.NOT_FOUND.code,
							ResultCode.NOT_FOUND.name + ". Reason: Sufficient monetary balance not found", null, null, null, null,
							null,0, 0 , 0, 0,request.getWebServiceName(), request.getWebServiceMethodName());

				} else {
					if (monetaryRechargePlan.getAmount().compareTo(BigDecimal.ZERO) > 0) {
						addMonetaryBalanceOfRechargePlan(subscriberId, monetaryRechargePlan, expiryDate,subscriberExpiry,
								request.getRequestIpAddress(), request.getWebServiceMethodName());
					} else {
						getLogger().error(MODULE, "Unable to subscribe monetary recharge plan(" + monetaryRechargePlan.getName() + "). Reason: No sufficient monetary balance found.");
						return new MonetaryRechargeSubscriptionResponse(ResultCode.NOT_FOUND.code,
								ResultCode.NOT_FOUND.name + ". Reason: Sufficient monetary balance not found", null, null, null, null,
								null,0, 0 , 0, 0,request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}

			} else {
				previousAvailableBalance = availableMonetaryBalance.getAvailBalance();

				if (updateBalanceIndication == true) {

					if (isPreviousAvailableBalanceLessThanPrice(price, previousAvailableBalance)) {
						getLogger().error(MODULE, "Unable to subscribe monetary recharge plan(" + monetaryRechargePlan.getName() + "). Reason: No sufficient monetary balance found.");
						return new MonetaryRechargeSubscriptionResponse(ResultCode.NOT_FOUND.code,
								ResultCode.NOT_FOUND.name + ". Reason: Sufficient monetary balance not found", null, null, null, null,
								null,0, 0 , 0, 0,request.getWebServiceName(), request.getWebServiceMethodName());

					} else {
						rechargeMonetaryBalance(subscriberId, price, expiryDate,subscriberExpiry,monetaryRechargePlan, availableMonetaryBalance, request.getRequestIpAddress());
					}
				} else {
					rechargeMonetaryBalance(subscriberId, BigDecimal.valueOf(0),expiryDate, subscriberExpiry, monetaryRechargePlan, availableMonetaryBalance, request.getRequestIpAddress());
				}
			}

			doReAuthForUpdateOperation(subscriberId, updateAction);

			SubscriberMonetaryBalance subscriberMonetaryBalanceAfterRecharge = SubscriberDAO.getInstance().getMonetaryBalance(subscriberId, Predicates.ALL_MONETARY_BALANCE);
			MonetaryBalance monetaryBalanceAfterRecharge = subscriberMonetaryBalanceAfterRecharge.getMainBalance();

			return new MonetaryRechargeSubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, null, null, subscriberId, monetaryRechargePlan.getId(),
					monetaryRechargePlan.getName(), previousAvailableBalance,
					monetaryBalanceAfterRecharge.getAvailBalance(), initialValidity, monetaryBalanceAfterRecharge.getValidToDate(), request.getWebServiceName(), request.getWebServiceMethodName());

		} catch (OperationFailedException e) {

			getLogger().error(MODULE, "Error while subscribing monetary recharge plan("+monetaryRechargePlan.getName()+") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());

			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}

			return new MonetaryRechargeSubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, null,
					null, 0, 0 , 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {

			getLogger().error(MODULE, "Error while subscribing monetary recharge plan("+monetaryRechargePlan.getName()+") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			getLogger().trace(e);

			return new MonetaryRechargeSubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, null, null,
					0, 0 , 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	private void doUpdateSubscriberExpiry(String subscriberId, long expiryDate, String requestIpAddress) throws ParseException, OperationFailedException,UnauthorizedActionException{

		EnumMap<SPRFields, String> updatedProfile = createSPRFieldMap(expiryDate);
		SubscriberDAO.getInstance().updateSubscriber(subscriberId, updatedProfile, adminStaff, requestIpAddress);
	}

	private EnumMap<SPRFields, String> createSPRFieldMap(long expiryDate) throws ParseException, OperationFailedException {

		EnumMap<SPRFields, String> sprFieldMap = new EnumMap<>(SPRFields.class);
		Timestamp dateToTimestamp = getTimestampValue(expiryDate);
		SPRFields.EXPIRY_DATE.validateTimeStampValue(dateToTimestamp);
		sprFieldMap.put(SPRFields.EXPIRY_DATE,String.valueOf(expiryDate));
		return sprFieldMap;
	}

	private Timestamp getTimestampValue(long expiryDate) throws OperationFailedException {
		Timestamp dateToTimestamp = null;
		if(Objects.isNull(expiryDate)==false){
			try{
				dateToTimestamp = new Timestamp(expiryDate);
			}catch(NumberFormatException e){
				LogManager.getLogger().error(MODULE, "Error while converting "+ expiryDate +" to timestamp");
				throw new OperationFailedException("Error while converting "+ expiryDate +" to timestamp",ResultCode.INVALID_INPUT_PARAMETER);
			}catch(Exception e){
				LogManager.getLogger().error(MODULE, "Error while converting "+ expiryDate +" to timestamp");
				LogManager.getLogger().trace(MODULE, e);
				throw new OperationFailedException("Error while converting "+ expiryDate +" to timestamp",ResultCode.INVALID_INPUT_PARAMETER);
			}
		}
		return dateToTimestamp;
	}

	private MonetaryRechargeSubscriptionResponse doFlexiRechargeOfMonetaryBalance(SubscribeMonetaryRechargePlanWSRequest request,
																				  String subscriberId,
																				  long expiryDate, Timestamp subscriberExpiry,
																				  Integer updateAction,
																				  BigDecimal price) {
		if (price.compareTo(BigDecimal.ZERO) < 0) {
			getLogger().error(MODULE, "Unable to subscribe Flexi-Recharge plan. Reason: Negative value provided for Price");

			return new MonetaryRechargeSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Negative value provided for Price", null, null, null, null, null, 0, 0, 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		String tax = SystemParameterDAO.getTax();

		double taxDoubleValue;

		if (Strings.isNullOrEmpty(tax) == false) {
			taxDoubleValue = Double.valueOf(tax);
			price = price.subtract(price.multiply(BigDecimal.valueOf(taxDoubleValue)).divide(BigDecimal.valueOf(100)));
		}

		try {
			SubscriberMonetaryBalance monetaryBalance = SubscriberDAO.getInstance().getMonetaryBalance(subscriberId, Predicates.ALL_MONETARY_BALANCE);
			MonetaryBalance availableMonetaryBalance = monetaryBalance.getMainBalance();

			long initialValidity = 0;
			double previousAvailableBalance = 0;
			if (isMonetaryBalanceAvailableAndActive(availableMonetaryBalance) == false) {
				addMonetaryBalanceOfFlexiRechargePlan(subscriberId, price, expiryDate, subscriberExpiry,
						request.getRequestIpAddress(), request.getWebServiceMethodName());

			} else {
				initialValidity = availableMonetaryBalance.getValidToDate();
				previousAvailableBalance = availableMonetaryBalance.getAvailBalance();
				rechargeMonetaryBalance(subscriberId, price, expiryDate, subscriberExpiry, null, availableMonetaryBalance, request.getRequestIpAddress());
			}

			doReAuthForUpdateOperation(subscriberId, updateAction);

			SubscriberMonetaryBalance subscriberMonetaryBalanceAfterRecharge = SubscriberDAO.getInstance().getMonetaryBalance(subscriberId, Predicates.ALL_MONETARY_BALANCE);
			MonetaryBalance monetaryBalanceAfterRecharge = subscriberMonetaryBalanceAfterRecharge.getMainBalance();

			return new MonetaryRechargeSubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS, null, null, subscriberId, null,
					null, previousAvailableBalance,
					monetaryBalanceAfterRecharge.getAvailBalance(), initialValidity, monetaryBalanceAfterRecharge.getValidToDate(), request.getWebServiceName(), request.getWebServiceMethodName());

		} catch (OperationFailedException e) {
            getLogger().error(MODULE, "Error while flexi recharge with price(" + price + ") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());

			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}

			return new MonetaryRechargeSubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(), null, null, null, null,
					null, 0, 0, 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());

		} catch (Exception e) {
			getLogger().error(MODULE, "Error while flexi recharge with proce(" + price + ") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			getLogger().trace(e);
			return new MonetaryRechargeSubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, null, null, null, null, null,
					0, 0, 0, 0, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	private void doReAuthForUpdateOperation(String subscriberId, Integer updateAction) {
		try {
			ReAuthUtil.doReAuthBySubscriberId(subscriberId, updateAction);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while performing re-auth for subscriber Id:" + subscriberId + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}
	}

	private boolean isMonetaryBalanceAvailableAndActive(MonetaryBalance availableMonetaryBalance) {
		return availableMonetaryBalance != null && SystemParameterDAO.getCurrency().equalsIgnoreCase(availableMonetaryBalance.getCurrency()) != false;
	}

	private boolean isMonetaryBalanceAvailableAndActiveForCurrency(MonetaryBalance availableMonetaryBalance,String currency){
		return availableMonetaryBalance != null && currency.equalsIgnoreCase(availableMonetaryBalance.getCurrency()) != false;
	}

	private boolean isPreviousAvailableBalanceLessThanPrice(BigDecimal price, double previousAvailableBalance) {
		return BigDecimal.valueOf(previousAvailableBalance).compareTo(price) < 0;
	}

	private boolean validateSubscriberAndMonetaryRechargePlan(String subscriberId, MonetaryRechargePlan monetaryRechargePlan, SPRInfo sprInfo) {
		if (StringUtils.equalsIgnoreCase(sprInfo.getSubscriberMode().val,"LIVE") && StringUtils.equalsIgnoreCase(monetaryRechargePlan.getMode().val,"TEST")) {
			getLogger().error(MODULE, "Unable to subscribe monetary recharge plan(" + monetaryRechargePlan.getName() + ") for subscriber ID: " + subscriberId
					+ ". Reason: Monetary recharge plan(" + monetaryRechargePlan.getName() + ") is in TEST mode and subscriber mode is LIVE");
			return true;
		}
		return false;
	}

	private void rechargeMonetaryBalance(String subscriberId, BigDecimal price, long expiryDate, Timestamp subscriberExpiry,
										 MonetaryRechargePlan monetaryRechargePlan, MonetaryBalance availBalance, String requestIPAddress)
			throws OperationFailedException, ParseException {
		long updatedValidity;
		long initialValidity = availBalance.getValidToDate();
		EnumMap<SPRFields, String> updatedProfile = null;
		if (monetaryRechargePlan != null) {

			if(expiryDate==0L || expiryDate==0){
				ValidityPeriodUnit validityPeriodUnit = monetaryRechargePlan.getValidityPeriodUnit();
				updatedValidity = validityPeriodUnit.addTime(initialValidity, monetaryRechargePlan.getValidity());
				if(nonNull(subscriberExpiry)){
					updatedProfile = createSPRFieldMap(updatedValidity);
					SubscriberDAO.getInstance().rechargeMonetaryBalance(
							new MonetaryRechargeData(subscriberId, availBalance, null, price, monetaryRechargePlan.getAmount(),
									monetaryRechargePlan.getName(), updatedValidity, requestIPAddress, CommonConstants.RECHARGE_MONETARY_BALANCE
									, ActionType.UPDATE.name()),
							updatedProfile,subscriberId, requestIPAddress);
				}
				else{
					SubscriberDAO.getInstance().rechargeMonetaryBalance(
							new MonetaryRechargeData(subscriberId, availBalance, null, price, monetaryRechargePlan.getAmount(),
									monetaryRechargePlan.getName(), updatedValidity, requestIPAddress, CommonConstants.RECHARGE_MONETARY_BALANCE
									, ActionType.UPDATE.name()),
							updatedProfile,subscriberId, requestIPAddress);
				}
			}
			else {
				updatedValidity = expiryDate;
				if(nonNull(subscriberExpiry)){
					updatedProfile=createSPRFieldMap(expiryDate);
					SubscriberDAO.getInstance().rechargeMonetaryBalance(new MonetaryRechargeData(subscriberId, availBalance, null,
							price, monetaryRechargePlan.getAmount(), monetaryRechargePlan.getName(), updatedValidity, requestIPAddress,
							CommonConstants.RECHARGE_MONETARY_BALANCE, ActionType.UPDATE.name()),
							updatedProfile,subscriberId, requestIPAddress);
				}
				else{
					SubscriberDAO.getInstance().rechargeMonetaryBalance(new MonetaryRechargeData(subscriberId, availBalance, null,
							price, monetaryRechargePlan.getAmount(), monetaryRechargePlan.getName(), updatedValidity, requestIPAddress,
							CommonConstants.RECHARGE_MONETARY_BALANCE, ActionType.UPDATE.name()),
							updatedProfile,subscriberId, requestIPAddress);
				}
			}

		} else {
			if(expiryDate==0L || expiryDate==0){
				updatedValidity = initialValidity;
				if(nonNull(subscriberExpiry)){
					updatedProfile = createSPRFieldMap(updatedValidity);
					SubscriberDAO.getInstance().rechargeMonetaryBalance(new MonetaryRechargeData(subscriberId, availBalance,
							null, BigDecimal.ZERO, price, null, updatedValidity, requestIPAddress,
							CommonConstants.FLEXI_RECHARGE_MONETARY_BALANCE, ActionType.UPDATE.name()),
							updatedProfile,subscriberId, requestIPAddress);
				}
				else{
					SubscriberDAO.getInstance().rechargeMonetaryBalance(new MonetaryRechargeData(subscriberId, availBalance,
							null, BigDecimal.ZERO, price, null, updatedValidity, requestIPAddress,
							CommonConstants.FLEXI_RECHARGE_MONETARY_BALANCE, ActionType.UPDATE.name()),
							updatedProfile,subscriberId, requestIPAddress);
				}
			}
			else {
				updatedValidity = expiryDate;
				if(nonNull(subscriberExpiry)){
					updatedProfile = createSPRFieldMap(expiryDate);
					SubscriberDAO.getInstance().rechargeMonetaryBalance(new MonetaryRechargeData(subscriberId, availBalance,
							null, BigDecimal.ZERO, price, null, updatedValidity, requestIPAddress,
							CommonConstants.FLEXI_RECHARGE_MONETARY_BALANCE, ActionType.UPDATE.name()),
							updatedProfile,subscriberId, requestIPAddress);
				}
				else{
					SubscriberDAO.getInstance().rechargeMonetaryBalance(new MonetaryRechargeData(subscriberId, availBalance,
							null, BigDecimal.ZERO, price, null, updatedValidity, requestIPAddress,
							CommonConstants.FLEXI_RECHARGE_MONETARY_BALANCE, ActionType.UPDATE.name()),
							updatedProfile,subscriberId, requestIPAddress);
				}
			}
		}

	}

	private void addMonetaryBalanceOfRechargePlan(String subscriberId, MonetaryRechargePlan monetaryRechargePlan,
												  long expiryDate, Timestamp subscriberExpiry, String requestIPAddress, String operation)
			throws ParseException, OperationFailedException,UnauthorizedActionException {

		MonetaryBalance balanceToBeAdded = new MonetaryBalance.MonetaryBalanceBuilder(UUID.randomUUID().toString(), subscriberId,null,
				SystemParameterDAO.getCurrency(), MonetaryBalanceType.DEFAULT.name(),System.currentTimeMillis())
				.withValidFromDate(System.currentTimeMillis())
				.withAvailableBalance(monetaryRechargePlan.getAmount().doubleValue())
				.withCreditLimit(0)
				.withTotalReservation(0)
				//.withPreviousBalance(0)
				.build();

		balanceToBeAdded.setInitialBalance(monetaryRechargePlan.getAmount().doubleValue());

		if (monetaryRechargePlan.getValidity() > 0) {
            balanceToBeAdded.setValidToDate(monetaryRechargePlan.getValidityPeriodUnit().addTime(System.currentTimeMillis(), monetaryRechargePlan.getValidity()));
        } else {
            balanceToBeAdded.setValidToDate(CommonConstants.FUTURE_DATE);
        }

		EnumMap<SPRFields, String> updatedProfile = null;
		if(expiryDate!=0 || expiryDate != 0L){
			balanceToBeAdded.setValidToDate(expiryDate);
			if(nonNull(subscriberExpiry)) {
				updatedProfile = createSPRFieldMap(expiryDate);
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberId,
						new SubscriberMonetaryBalanceWrapper(balanceToBeAdded, null,
								null, operation, ActionType.ADD.name(),null),
						"", requestIPAddress,updatedProfile,monetaryRechargePlan.getName());
			}
			else
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberId,
						new SubscriberMonetaryBalanceWrapper(balanceToBeAdded, null,
						null, operation, ActionType.ADD.name(),null),
						"", requestIPAddress,updatedProfile, monetaryRechargePlan.getName());
		}
		else {
				if (monetaryRechargePlan.getValidity() > 0) {
					balanceToBeAdded.setValidToDate(monetaryRechargePlan.getValidityPeriodUnit().addTime(System.currentTimeMillis(), monetaryRechargePlan.getValidity()));
				} else {
					balanceToBeAdded.setValidToDate(CommonConstants.FUTURE_DATE);
				}
			if(nonNull(subscriberExpiry)){
				 updatedProfile = createSPRFieldMap(balanceToBeAdded.getValidToDate());
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberId,
						new SubscriberMonetaryBalanceWrapper(balanceToBeAdded, null,
						null, operation, ActionType.ADD.name(),null),
						"", requestIPAddress,updatedProfile, monetaryRechargePlan.getName());
			}
			else{
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberId,
						new SubscriberMonetaryBalanceWrapper(balanceToBeAdded, null,
						null, operation, ActionType.ADD.name(),null),
						"", requestIPAddress,updatedProfile, monetaryRechargePlan.getName());
			}
		}

	}

	// Change Bill Day
	public ChangeBillDayResponse changeBillDay(ChangeBillDayWSRequest request) {

		applyRequestInterceptors(request);
		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preChangeBillDay(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		ChangeBillDayResponse response = doChangeBillDay(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postChangeBillDay(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private ChangeBillDayResponse doChangeBillDay(ChangeBillDayWSRequest request) {

		String subscriberIdentity = request.getSubscriberId();
		String alternateIdentity = request.getAlternateId();
		Integer nextBillDate = request.getNextBillDate();
		String parameter1 = request.getParameter1();
		String parameter2 = request.getParameter2();
		ValidationData validationData = request.validateParameters();
		if (validationData != null) {
			LogManager.getLogger().error(MODULE, "Unable to change bill day. " +
					"Reason: " + validationData.getMessage());
			return new ChangeBillDayResponse(validationData.getCode().code,
					validationData.getCode().name + " Reason: " + validationData.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		try {
			SPRInfo sprInfo;
			String subscriberIdBySpr = subscriberIdentity;

			if (isNullOrBlank(subscriberIdBySpr) == false) {
				sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberIdBySpr, adminStaff);
				if(sprInfo==null){
					LogManager.getLogger().error(MODULE, "Subscriber ID not found.");
					return new ChangeBillDayResponse(ResultCode.NOT_FOUND.code, "Reason: Subscriber not found with subscriber identity: "+subscriberIdBySpr, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
				if (isNullOrBlank(alternateIdentity) == false) {
					String subscriberIdByAlternateId = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateIdentity, adminStaff);
					if(subscriberIdBySpr.equals(subscriberIdByAlternateId)==false){
						LogManager.getLogger().error(MODULE, "Subscriber ID and Alternate ID do not match");
						return new ChangeBillDayResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Subscriber ID and Alternate ID do not reference to the same subscriber", null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
					}
				}
			} else {
				sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateIdentity, adminStaff);
				if(sprInfo == null){
					LogManager.getLogger().error(MODULE, "Alternate ID not found.");
					return new ChangeBillDayResponse(ResultCode.NOT_FOUND.code, "Reason: Subscriber not found with alternate identity: "+alternateIdentity, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

			if(sprInfo.getBillingDate() == null){
				LogManager.getLogger().error(MODULE, "Bill Day is not configured with subscriberIdentity:" + subscriberIdentity);
				return new ChangeBillDayResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + " Reason: Bill Day is not configured with subscriberIdentity:" + subscriberIdentity, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
			}

			if(sprInfo.getBillingDate().equals(nextBillDate)){
				LogManager.getLogger().error(MODULE, "Next Bill Day and Existing Bill Day is same for subscriberIdentity:" + subscriberIdentity);
				return new ChangeBillDayResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name +" Reason: Next Bill Day and Existing Bill Day is same for subscriberIdentity: " + subscriberIdentity, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
			}


			// Change Bill Date
			Timestamp billChangeDate = getBillChangeDate(sprInfo.getBillingDate());
			Timestamp nextBillingDate = getNextBillingCycleDateFromBillChangeDate(nextBillDate,billChangeDate);
			SubscriberDAO.getInstance().changeBillDay(sprInfo.getSubscriberIdentity(), nextBillingDate, billChangeDate, request.getRequestIpAddress());

			return new ChangeBillDayResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while changing bill day for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			if (e.getErrorCode() == ResultCode.INTERNAL_ERROR) {
				getLogger().trace(MODULE, e);
			}
			return new ChangeBillDayResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while changing bill day for subscriberIdentity: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			return new ChangeBillDayResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, parameter1, parameter2, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	private Timestamp getBillChangeDate(Integer billDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		if (billDate <= calendar.get(Calendar.DAY_OF_MONTH)) {
			calendar.add(Calendar.MONTH, 1);
		}
		calendar.set(Calendar.DAY_OF_MONTH, billDate);
		calendar.set(Calendar.HOUR_OF_DAY, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		calendar.set(Calendar.MILLISECOND, 00);

		return new Timestamp(calendar.getTimeInMillis());
	}

	private Timestamp getNextBillingCycleDateFromBillChangeDate(Integer billDate, Timestamp billChangeDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(billChangeDate.getTime());

		if (calendar.get(Calendar.DAY_OF_MONTH) >= billDate) {
			calendar.add(Calendar.MONTH, 1);
		}
		calendar.set(Calendar.DAY_OF_MONTH, billDate);
		calendar.set(Calendar.HOUR_OF_DAY, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		calendar.set(Calendar.MILLISECOND, 00);

		return new Timestamp(calendar.getTimeInMillis());
	}

	private void addMonetaryBalanceOfFlexiRechargePlan(String subscriberId, BigDecimal amount,long expiryDate,
													   Timestamp subscriberExpiry, String requestIPAddress, String operation)
			throws ParseException, OperationFailedException,UnauthorizedActionException  {
		MonetaryBalance balanceToBeAdded = new MonetaryBalance.MonetaryBalanceBuilder(UUID.randomUUID().toString(), subscriberId,null,
				SystemParameterDAO.getCurrency(), MonetaryBalanceType.DEFAULT.name(),System.currentTimeMillis())
				.withValidFromDate(System.currentTimeMillis())
				.withAvailableBalance(amount.doubleValue())
				.withCreditLimit(0)
				.withTotalReservation(0)
				//.withPreviousBalance(0)
				.withValidToDate(CommonConstants.FUTURE_DATE).build();

		balanceToBeAdded.setInitialBalance(amount.doubleValue());
		EnumMap<SPRFields, String> updatedProfile = null;
		if(expiryDate!=0 || expiryDate!=0L){
			balanceToBeAdded.setValidToDate(expiryDate);
			if(nonNull(subscriberExpiry))
			{
				 updatedProfile = createSPRFieldMap(expiryDate);
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberId,
						new SubscriberMonetaryBalanceWrapper(balanceToBeAdded, null,
						null, operation, ActionType.ADD.name(),null),
						"", requestIPAddress,updatedProfile, null);
			}
			else
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberId,
						new SubscriberMonetaryBalanceWrapper(balanceToBeAdded, null,
						null, operation, ActionType.ADD.name(),null),
						"", requestIPAddress,updatedProfile, null);
		}
		else {
			if(nonNull(subscriberExpiry)){
				 updatedProfile = createSPRFieldMap(balanceToBeAdded.getValidToDate());
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberId,
						new SubscriberMonetaryBalanceWrapper(balanceToBeAdded, null,
						null, operation, ActionType.ADD.name(),null),
						"", requestIPAddress,updatedProfile, null);
			}
			else{
				SubscriberDAO.getInstance().addMonetaryBalance(subscriberId,
						new SubscriberMonetaryBalanceWrapper(balanceToBeAdded, null,
								null, operation, ActionType.ADD.name(),null),
						"", requestIPAddress,updatedProfile, null);
			}
		}
	}

	public BodSubscriptionResponse subscribeBod(SubscribeBodWsRequest request) {

		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.preSubscribeBod(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		BodSubscriptionResponse response = doSubscribeBod(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.postSubscribeBod(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private BodSubscriptionResponse doSubscribeBod(SubscribeBodWsRequest request) {

		String subscriberId = request.getSubscriberId();
		String bodId = request.getBodId();
		String bodName = request.getBodName();
		String subscriptionStatusValue = request.getSubscriptionStatusValue();
		String subscriptionStatusName = request.getSubscriptionStatusName();
		Integer updateAction = request.getUpdateAction();
		boolean updateBalanceIndication = request.isUpdateBalanceIndication();

		SubscriptionState subscriptionState = SubscriptionState.STARTED;

		Long startTime;
		try {
			startTime = convertLong(request.getStartTime());
		} catch (NumberFormatException e) {
			getLogger().error(MODULE, "Unable to subscribe BoD. Reason: Invalid start time received: " + request.getStartTime());
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid start time received: " + request.getStartTime(),
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		Long endTime;
		try {
			endTime = convertLong(request.getEndTime());
		} catch (NumberFormatException e) {
			getLogger().error(MODULE, "Unable to subscribe BoD. Reason: Invalid end time received: " + request.getEndTime());
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid end time received: " + request.getStartTime(),
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		Integer priority;
		try {
			priority = convertToInteger(request.getPriority());
		} catch (NumberFormatException e) {
			getLogger().error(MODULE, "Unable to subscribe BoD. Reason: Invalid priority received: " + request.getPriority());
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid priority received: " + request.getPriority(),
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (isNullOrBlank(subscriptionStatusValue) && isNullOrBlank(subscriptionStatusName)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Considering subscription status: " + subscriptionState.getName()
						+ " for subscriber id: " + subscriberId + "). Reason: Subscription status not received");
			}
		} else {
			subscriptionState = getSubscriptionStatus(subscriptionStatusValue, subscriptionStatusName);
			if (subscriptionState == null) {
				getLogger().error(MODULE, "Unable to subscribe BoD package for subscriber id: " + subscriberId
						+ ". Reason: Invalid subscription status received");
				return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
						"Invalid subscription status received", null, null, null,
						request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		BoDPackage bodPackage = null;

		if (isNullOrBlank(bodId) == false) {
			bodPackage = getNVSMXPolicyRepository().getBoDPackage().byId(bodId);

			if (bodPackage == null) {
				getLogger().error(MODULE, "Unable to subscribe BoD. Reason: ACTIVE BoD not found with id: " + bodId);
				return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "ACTIVE BoD not found with id: " + bodId,
						null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if (isNullOrBlank(bodName) == false) {

			if (bodPackage == null) {

				bodPackage = getNVSMXPolicyRepository().getBoDPackage().byName(bodName);
				if (bodPackage == null) {
					getLogger().error(MODULE, "Unable to subscribe BoD. Reason: ACTIVE BoD not found with name: " + bodName);
					return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "ACTIVE BoD not found with name: " + bodName,
							null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			} else if (bodPackage.getName().equals(bodName) == false) {

				getLogger().error(MODULE, "Unable to subscribe BoD. Reason: BoD id(" + bodId + ") and name(" + bodName
						+ ") are not related ");
				return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "BoD package id(" + bodId + ") and name(" + bodName
						+ ") are not related ", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

		} else {
			if (bodPackage == null) {
				getLogger().error(MODULE, "Unable to subscribe BoD. Reason: BoD id or name must be provided: " + bodName);
				return new BodSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "BoD package id or name must be provided", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}

		if (bodPackage.getPolicyStatus() != PolicyStatus.SUCCESS) {

			getLogger().error(MODULE, "Unable to subscribe BoD(" + bodPackage.getName() + ") for subscriber ID: " + subscriberId
					+ ". Reason: Bod(" + bodPackage.getName() + ") is failed/partial fail");
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Unable to subscribe BoD(" + bodPackage.getName() + ") for subscriber ID: " + subscriberId
					+ ". Reason: Bod(" + bodPackage.getName() + ") is failed/partial fail", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());

		}

		Timestamp currentTime = new Timestamp(TimeSource.systemTimeSource().currentTimeInMillis());
		if(nonNull(bodPackage.getAvailabilityStartDate()) && !(bodPackage.getAvailabilityStartDate().before(currentTime))){
			getLogger().error(MODULE, "Unable to subscribe BoD. Reason: BoD Plan with future Availability Start Date is not be allowed to subscribe.");
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "BoD Plan with future Availability Start Date should not be allowed to subscribe",
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(nonNull(bodPackage.getAvailabilityEndDate()) && !(bodPackage.getAvailabilityEndDate().after(currentTime))) {
			getLogger().error(MODULE, "Unable to subscribe BoD. Reason: BoD Plan with Past Availability End Date is not be allowed to subscribe.");
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "BoD Plan with Past Availability End Date should not be allowed to subscribe",
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(startTime != null && startTime == Long.MIN_VALUE){
			getLogger().error(MODULE, "Unable to subscribe BoD. Reason: Invalid Start time received");
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid Start time received ",
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		if( endTime != null && endTime == Long.MIN_VALUE){
			getLogger().error(MODULE, "Unable to subscribe BoD. Reason: Invalid End time received");
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Invalid End time received ",
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		Subscription subscription;
		SubscriptionParameter subscriptionParameter;
		try {
			if (isNullOrBlank(subscriberId)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Subscriber ID not received");
				}

				String alternateId = request.getAlternateId();

				if (isNullOrBlank(alternateId)) {
					getLogger().error(MODULE, "Unable to subscribe BoD(" + bodPackage.getName() + "). Reason: Identity parameter missing");
					return new BodSubscriptionResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
							ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing",
							null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}

				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);

				BodSubscriptionResponse validatedResponse = validateSubscriberProfileForBod(request, subscriberId, bodPackage, sprInfo, "alternate Id");
				if (validatedResponse != null) {
					return validatedResponse;
				}

				subscriptionParameter = new SubscriptionParameter(sprInfo, request.getParentId(), null,
						subscriptionState.state, null, bodPackage.getId(), bodPackage.getName(), startTime,
						endTime, priority, request.getParameter1(), request.getParameter2(), null, null);

			} else {

				SPRInfo sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);

				BodSubscriptionResponse validatedResponse = validateSubscriberProfileForBod(request, subscriberId, bodPackage, sprInfo, "alternate Id");
				if (validatedResponse != null){
					return validatedResponse;
				}

				subscriptionParameter = new SubscriptionParameter(sprInfo, request.getParentId(), null,
						subscriptionState.state, null, bodPackage.getId(), bodPackage.getName(), startTime,
						endTime, priority, request.getParameter1(), request.getParameter2(), null, null);

			}


			double subscriptionPrice;
			if(updateBalanceIndication){
				if(nonNull(bodPackage.getPrice()) && bodPackage.getPrice().doubleValue() > 0d){
					subscriptionPrice = bodPackage.getPrice().doubleValue();
					SubscriberMonetaryBalance monetaryBalance = SubscriberDAO.getInstance().getMonetaryBalance(
							subscriptionParameter.getSprInfo().getSubscriberIdentity(), Predicates.ALL_MONETARY_BALANCE);
					MonetaryBalance availableMonetaryBalance = monetaryBalance.getMainBalance();

					if(isMonetaryBalanceAvailableAndActive(availableMonetaryBalance)){
						double usableBalance = availableMonetaryBalance.getUsableBalance();
						if(usableBalance < subscriptionPrice){
							getLogger().error(MODULE, "Unable to subscribe BoD. Reason: Balance insufficient.");
							return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
									"Balance insufficient."
									, null, null, null
									, request.getWebServiceName(), request.getWebServiceMethodName());
						}
						else{
							subscriptionParameter.setSubscriptionPrice(subscriptionPrice);
							subscriptionParameter.setMonetaryBalance(availableMonetaryBalance);
						}
					} else{
						getLogger().error(MODULE, "Unable to subscribe BoD. Reason: No available and active balance found.");
						return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
								"No available and active balance found."
								, null, null, null
								, request.getWebServiceName(), request.getWebServiceMethodName());
					}

				} else {
					getLogger().error(MODULE, "Unable to subscribe BoD. Reason: Price not configured.");
					return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
							"Price not configured. Please check update Balance Indication flag OR configure BoD price."
							, null, null, null
							, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

			subscription = SubscriberDAO.getInstance().subscribeBodPackage(subscriptionParameter, adminStaff, request.getRequestIpAddress());

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while subscribing BoD("+bodPackage.getName()+") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new BodSubscriptionResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason:" + e.getMessage(),
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while subscribing BoD("+bodPackage.getName()+") for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			getLogger().trace(e);
			return new BodSubscriptionResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name,
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		BodSubscriptionData bodSubscriptionData = createBodSubscriptionData(subscription);

		try {
			ReAuthUtil.doReAuthBySubscriberId(subscriberId, updateAction);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while performing re-auth for subscriber Id:" + bodSubscriptionData.getSubscriberIdentity() + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}

		return new BodSubscriptionResponse(ResultCode.SUCCESS.code, SUCCESS,CollectionUtil.newArrayListWithElements(bodSubscriptionData), null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	private BodSubscriptionData createBodSubscriptionData(Subscription subscription) {
		BoDPackage subscriptionPackage = getNVSMXPolicyRepository().getBoDPackage().byId(subscription.getPackageId());

		return new BodSubscriptionData(subscription.getId(),
				subscription.getSubscriberIdentity(), subscriptionPackage.getId(), subscriptionPackage.getName()
				, subscription.getStartTime() == null ? null : subscription.getStartTime().getTime()
				, subscription.getEndTime() == null ? BIG_DATE_IN_MILLIS : subscription.getEndTime().getTime(), subscription.getStatus()
				, subscription.getParameter1(), subscription.getParameter2(), subscription.getPriority());
	}

	private BodSubscriptionResponse validateSubscriberProfileForBod(SubscribeBodWsRequest request,
																	String id,
																	BoDPackage boDPackage,
																	SPRInfo sprInfo,
																	String message) {
		if (sprInfo == null) {
			getLogger().error(MODULE, "Unable to subscribe BoD(" + boDPackage.getName() + "). Reason: Subscriber not found with "+message+": " + id);
			return new BodSubscriptionResponse(ResultCode.NOT_FOUND.code,
					ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}


		if(SubscriberStatus.INACTIVE.name().equals(sprInfo.getStatus())){
			getLogger().error(MODULE, "Unable to subscribe BoD(" + boDPackage.getName() + "). Reason: Subscriber status is InActive for "+message+": " + id);
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
					ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Status is InActive", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if(sprInfo.getProfileExpiredHours() >= 0){
			getLogger().error(MODULE, "Unable to subscribe BoD(" + boDPackage.getName() + "). Reason: Subscriber Profile has expired for "+message+": " + id);
			return new BodSubscriptionResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
					ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason: Subscriber Profile has expired", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		return null;
	}

	public FnFOperationResponse fnFOperation(FnFOperationRequest request) {

		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.preFnFOperation(request);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		FnFOperationResponse response = doFnFOperation(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			try{
				groovyScript.postFnFOperation(request, response);
			} catch (Throwable e) {//NOSONAR - To avoid OutOfMemory error
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	public FnFOperationResponse doFnFOperation(FnFOperationRequest request) {
		String subscriberId = request.getSubscriberId();
		String alternateId = request.getAlternateId();
		String groupName = request.getGroupName();
		Set<String> members = new HashSet<>(request.getMembers());

		if (Strings.isNullOrBlank(groupName)) {
			getLogger().error(MODULE, "Unable to update FnF group for subscriber id: " + subscriberId
					+ ". Reason: Group name is required");
			return new FnFOperationResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
					"Group name is required", null, null, null,
					request.getWebServiceName(), request.getWebServiceMethodName());
		}

		if (Collectionz.isNullOrEmpty(members)) {
			getLogger().error(MODULE, "Unable to update FnF group for subscriber id: " + subscriberId
					+ ". Reason: At least 1 member required");
			return new FnFOperationResponse(ResultCode.INPUT_PARAMETER_MISSING.code,
					"At least 1 member required", null, null, null,
					request.getWebServiceName(), request.getWebServiceMethodName());
		}

		try {
			SPRInfo sprInfo = getSprDetails(subscriberId, alternateId);
			validateSubscriberProfile(sprInfo);


			List<Subscription> addOnSubscriptions =
					SubscriberDAO.getInstance().getSubscriptions(sprInfo.getSubscriberIdentity(), adminStaff);

			addOnSubscriptions = addOnSubscriptions.stream().filter(sub -> {

				if(SubscriptionType.RO_ADDON != sub.getType()){
					return false;
				}

				SubscriptionMetadata metadata = sub.getMetadata();
				if (Objects.isNull(metadata)) {
					return false;
				}

				FnFGroup fnFGroup = metadata.getFnFGroup();

				if (Objects.isNull(fnFGroup)) {
					return false;
				}

				return groupName.equals(fnFGroup.getName());
			}).collect(Collectors.toList());

			if (Collectionz.isNullOrEmpty(addOnSubscriptions)) {
				return new FnFOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code,
						"Group does not exist.", null, null, null,
						request.getWebServiceName(), request.getWebServiceMethodName());
			}

			for(String member: members){
				if(StringUtils.isNumeric(member)==false){
					getLogger().error(MODULE, "Unable update FnF group. Reason: FnF Members should be numeric");
					return new FnFOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "FnF Members should be numeric", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

			//Group name will be unique across subscribers and their subscriptions
			Subscription subscription = addOnSubscriptions.get(0);
			FnFGroup fnFGroup = subscription.getMetadata().getFnFGroup();
			Set<String> groupMembers = new HashSet<>(fnFGroup.getMembers());

			if(FnFOperationRequest.Operation.ADD==request.getOperation()) {
				if(groupMembers.addAll(members)==false){
					return new FnFOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Member is already part of the group.", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			} else {
				if(groupMembers.removeAll(members)==false){
					return new FnFOperationResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Member is not part of the group", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
				}
			}

			fnFGroup.getMembers().clear();
			fnFGroup.getMembers().addAll(groupMembers);

			SubscriberDAO.getInstance().updateFnFGroup(sprInfo,subscription,adminStaff,request.getRequestIpAddress());

			return new FnFOperationResponse(ResultCode.SUCCESS.code, SUCCESS, fnFGroup, request.getParameter1(), request.getParameter2(), request.getWebServiceName(), request.getWebServiceMethodName());

		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while updating FnF group for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return new FnFOperationResponse(e.getErrorCode().code, e.getErrorCode().name + ". Reason: " + e.getMessage(),
					null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while updating FnF group for subscriber(" + subscriberId + "). Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			return new FnFOperationResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

	public SPRInfo getSprDetails(String subscriberId, String alternateId) throws OperationFailedException, UnauthorizedActionException{
		SPRInfo sprInfo;
		if (Strings.isNullOrBlank(subscriberId)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE,"Subscriber ID not received");
			}

			if (Strings.isNullOrBlank(alternateId)) {
				throw new OperationFailedException("Subscriber identity parameter missing", ResultCode.INPUT_PARAMETER_MISSING);
			}

			sprInfo = SubscriberDAO.getInstance().getSubscriberByAlternateId(alternateId, adminStaff);

			if(Objects.isNull(sprInfo)){
				throw new OperationFailedException("Subscriber not found with alternate id "+alternateId, ResultCode.NOT_FOUND);
			}

		} else {
			sprInfo = SubscriberDAO.getInstance().getSubscriberById(subscriberId, adminStaff);
			if(Objects.isNull(sprInfo)){
				throw new OperationFailedException("Subscriber not found with subscriber id "+subscriberId, ResultCode.NOT_FOUND);
			}
		}
		return sprInfo;
	}

	private void validateSubscriberProfile(SPRInfo sprInfo) throws OperationFailedException {
		if(SubscriberStatus.INACTIVE.name().equals(sprInfo.getStatus())){
			throw new OperationFailedException("Subscriber Status is InActive", ResultCode.INVALID_INPUT_PARAMETER);
		}

		if(sprInfo.getProfileExpiredHours() >= 0){
			throw new OperationFailedException("Subscriber Profile has expired", ResultCode.INVALID_INPUT_PARAMETER);
		}
	}

	private Long convertLong(String longStr) {
		if (longStr == null || StringUtils.isBlank(longStr) || StringUtils.isBlank(longStr.trim())) {
			return null;
		}

		return Long.parseLong(longStr);
	}

	private Integer convertToInteger(String intStr) {
		if (StringUtils.isBlank(intStr)) {
			return null;
		}

		return Integer.parseInt(intStr);
	}

	private Map<String,String> getGroupMapByName(List<String> groupNameList){
		Map<String,String> idWiseGroupNameMap = Maps.newHashMap();
		try {
			List<GroupData> groupDatas = importExportUtil.getGroups();
			for (String groupName : groupNameList) {
				GroupData groupData = groupDatas.stream().filter(name -> name.getName().equals(groupName)).findAny().orElse(null);
				if (groupData == null) {
					idWiseGroupNameMap.clear();
					return idWiseGroupNameMap;
				}
				idWiseGroupNameMap.put(groupData.getId(), groupData.getName());
			}
		}catch(Exception e){
			getLogger().error(MODULE, "Error while fetch List of product Offer. Reason:" + e.getMessage());
			getLogger().trace(e);
		}
		return idWiseGroupNameMap;
	}

	private boolean validateCurrency(String productOffer,String currentPkgCurrency) {
		ProductOffer validProductOffer = getNVSMXPolicyRepository().getProductOffer().byName(productOffer);
		if (validProductOffer == null) {
			return false;
		} else {
			if (validProductOffer.getCurrency().equalsIgnoreCase(currentPkgCurrency) == false) {
				return false;
			}
		}
		return true;
	}
}