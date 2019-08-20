
package com.elitecore.nvsmx.policydesigner.controller.subscriber;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.ActionType;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.Balance;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.corenetvertex.pm.store.BoDPackageStore;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIdStatusDetail;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIds;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.AlternateIdType;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfo.SubscriberMode;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.corenetvertex.spr.util.SubscriberMonetaryBalanceUtil;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.monetarybalance.MonetaryBalanceWrapper;
import com.elitecore.nvsmx.policydesigner.controller.session.SessionSearchAttributes;
import com.elitecore.nvsmx.policydesigner.controller.util.ApplicableBoDByBasePackagePredicate;
import com.elitecore.nvsmx.policydesigner.controller.util.ApplicableQuotaTopUpByBasePackagePredicate;
import com.elitecore.nvsmx.policydesigner.controller.util.StaffGroupBoDPackagePredicate;
import com.elitecore.nvsmx.policydesigner.controller.util.StaffGroupIMSPackagePredicate;
import com.elitecore.nvsmx.policydesigner.controller.util.StaffGroupProductOfferPredicate;
import com.elitecore.nvsmx.policydesigner.controller.util.StaffGroupQuotaTopUpPredicate;
import com.elitecore.nvsmx.policydesigner.controller.util.StaffGroupUserPackagePredicate;
import com.elitecore.nvsmx.policydesigner.model.session.SessionSearchField;
import com.elitecore.nvsmx.policydesigner.model.subscriber.BalanceInfo;
import com.elitecore.nvsmx.policydesigner.model.subscriber.NonMonetaryRateCardBalance;
import com.elitecore.nvsmx.policydesigner.model.subscriber.QuotaProfileBalance;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriptionInformation;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.util.DateConverter;
import com.elitecore.nvsmx.system.util.FailedDataPackageFilter;
import com.elitecore.nvsmx.system.util.FailedIMSPackageFilter;
import com.elitecore.nvsmx.system.util.FailedQuotaTopUpFilter;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.elitecore.nvsmx.ws.subscription.blmanager.OperationType;
import com.elitecore.nvsmx.ws.util.AlternateIdOperationUtils;
import com.elitecore.nvsmx.ws.util.ProductOfferCurrencyPredicate;
import com.elitecore.nvsmx.ws.util.ReAuthUtil;
import com.elitecore.passwordutil.PasswordEncryption;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Provides functionalities relative to Subscribers Module
 * @author aneri.chavda
 */
public class SubscriberCTRL extends ActionSupport implements ServletRequestAware,ServletResponseAware, SessionAware ,ValidationAware {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "SUBS-CTRL";
	private static final String SUBSCRIBER_VIEW = "policydesigner/subscriber/Subscriber/view";
	private static final String SEARCH_TEST_SUBSCRIBER="policydesigner/subscriber/Subscriber/searchTestSubscriber";
	private static final String SEARCH_DELETED_SUBSCRIBER = "policydesigner/subscriber/Subscriber/searchDeletedSubscriber";
	private static final long SECONDS_IN_MINUTES=60;
	private static final long SECONDS_IN_HOUR =SECONDS_IN_MINUTES*60;
	private static final long SECONDS_IN_DAY = SECONDS_IN_HOUR *24;
	public static final String NOTE_REFER_LOGS = "note.refer.logs";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Map<String, Object> session;
	private SPRInfo subscriber = new SPRInfoImpl();
	private Map<String, String> criteria = new HashMap<>();
	private String countryNames;
	private List<BasePackage> packages = new ArrayList<>();
	private List<BasePackage> livePackages = new ArrayList<>();
	private List<ProductOffer> productOffers = new ArrayList<>();
	private List<Service> services = new ArrayList<>();
	private List<ProductOffer> liveProductOffers = new ArrayList<>();
	private List<IMSPackage> imsPackages = new ArrayList<>();
	private List<IMSPackage> imsLivePackages = new ArrayList<>();
	private String liveAddOnString;
	private String allAddOnString;

	private List<QuotaTopUp> allTopUps = new ArrayList<>();
	private List<QuotaTopUp> liveTopUps = new ArrayList<>();
	private String actionChainUrl;
	private static final String UNLIMITED_USAGE = "UNLIMITED";
	private JsonArray monetaryBalancesJsonArray;
	private JsonArray subscriptionInformationJsonArray;
	private List<Package> dataPackages = new ArrayList<>();
	private List<RnCPackage> filteredRncPackages = new ArrayList<>();
	private List<IMSPackage> imsPackage = Collectionz.newArrayList();
	Object [] messageParameter = {Discriminators.SUBSCRIBER};
	private String allTopUpsString ;
	private String liveTopUpsString;
	private JsonArray subscriberAlternateIds;
	private String allBodDataString;
	private String liveBodDataString;
	private String currency;


	@SkipValidation
	public String initCreate() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called initCreate()");
		}
		try{
			createBaseProductOfferList(null);
			request.setAttribute(Attributes.DATA_PACKAGES,getProductOffers());
			request.setAttribute(Attributes.DATA_PACKAGES_LIVE, getLiveProductOffers());
			request.setAttribute(Attributes.IMS_PACKAGES, getImsPackages());
			request.setAttribute(Attributes.IMS_PACKAGES_LIVE, getImsLivePackages());
			if (Strings.isNullOrBlank(countryNames)) {
				countryNames= CRUDOperationUtil.getProperty(CountryData.class,"name");
			}
			return Results.CREATE.getValue();
		}catch(Exception ex){
			getLogger().error(MODULE, "Error while trying to initiate subscriber creation. Reason: " + ex.getMessage());
			getLogger().trace(MODULE, ex);
			addActionError("Initiate subscriber creation failed");
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.LIST.getValue();
		}
	}

	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called create()");
		}
		try {

			SPRInfoImpl sprInfo = (SPRInfoImpl) subscriber;
			if(Strings.isNullOrBlank(sprInfo.getPassword()) == false){
				String encryptedPassword = PasswordEncryption.getInstance().crypt(sprInfo.getPassword(), Integer.valueOf(sprInfo.getEncryptionType()));
				sprInfo.setPassword(encryptedPassword);
			}
			SubscriberDAO.getInstance().addSubscriber(new SubscriberDetails(sprInfo, null), getStaffData(),
					request.getRemoteAddr());
			session.put(Attributes.SUBSCRIBER_IDENTITY, subscriber.getSubscriberIdentity());
			session.put(Attributes.SUBSCRIBER, subscriber);
			MessageFormat messageFormat = new MessageFormat(getText(ActionMessageKeys.CREATE_SUCCESS.key));
			addActionMessage(messageFormat.format(messageParameter));
			setActionChainUrl(SUBSCRIBER_VIEW);
		} catch(UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error in adding subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, e.getMessage());
			return Results.LIST.getValue();
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error in adding subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			return Results.LIST.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in adding subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.SUBSCRIBER + "	" +getText(ActionMessageKeys.CREATE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.LIST.getValue();
		}
		return Results.REDIRECT_ACTION.getValue();
	}

	private StaffData getStaffData() {
		return (StaffData) request.getSession().getAttribute(Attributes.STAFF_DATA);
	}

	private String parseQueryStringAndGetSubscriberIdentity(){
		String queryString = request.getQueryString();
		if(queryString!=null && queryString.contains("+") && queryString.contains(Attributes.SUBSCRIBER_IDENTITY)){
			return queryString.split(Attributes.SUBSCRIBER_IDENTITY+"=")[1];
		}else{
			return request.getParameter(Attributes.SUBSCRIBER_IDENTITY);
		}
	}

	@SkipValidation
	public String initUpdate() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called initUpdate()");
		}


		String subscriberIdentity  = null;
		try {

			if (Strings.isNullOrBlank(countryNames)) {
				countryNames = CRUDOperationUtil.getProperty(CountryData.class,"name");
			}

			subscriberIdentity = parseQueryStringAndGetSubscriberIdentity();

			if(Strings.isNullOrBlank(subscriberIdentity)) {
				getLogger().error(MODULE, "Error while updating subscriber. Reason: Subscriber Identity missing");
				addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
				addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
				return Results.LIST.getValue();
			}

			SPRInfoImpl subscriberData = (SPRInfoImpl) SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, getStaffData());

			if(subscriberData == null){
				return subscriberNotFoundError(subscriberIdentity);
			}

			if(Strings.isNullOrBlank(subscriberData.getPassword()) == false){
				String decryptedPassword = PasswordEncryption.getInstance().decrypt(subscriberData.getPassword(), Integer.valueOf(subscriberData.getEncryptionType()));
				subscriberData.setPassword(decryptedPassword);
			}
			this.subscriber = subscriberData;

			//Set Currency
			String productOfferName=subscriberData.getProductOffer();
			ProductOffer productOffer = getNVSMXPolicyRepository().getProductOffer().byName(productOfferName);
			currency=productOffer.getCurrency();

			createBaseProductOfferList(productOffer.getCurrency());
			if(SubscriberMode.LIVE.val.equals(subscriberData.getSubscriberMode().val)) {
				request.setAttribute(Attributes.DATA_PACKAGES,getLiveProductOffers());
				request.setAttribute(Attributes.IMS_PACKAGES, getImsLivePackages());
				setImsPackage(getImsLivePackages());
			} else {
				request.setAttribute(Attributes.DATA_PACKAGES,getProductOffers());
				request.setAttribute(Attributes.IMS_PACKAGES, getImsPackages());
				filteredRncPackages = new ArrayList<>();
				setImsPackage(getImsPackages());
			}
		} catch(UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error in updating subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, e.getMessage());
			return Results.LIST.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while updating subscriber with ID: " + subscriberIdentity + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Initiate subscriber updation failed");
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.LIST.getValue();
		}

		request.setAttribute("subscriberProductOfferName", subscriber.getProductOffer());
		request.setAttribute("subscriberImsPackageName", subscriber.getImsPackage());

		return Results.UPDATE.getValue();
	}

	public String update() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called update()");
		}

		String subscriberId = parseQueryStringAndGetSubscriberIdentity();
		MessageFormat messageFormat;
		try {
			SPRInfoImpl sprInfoImpl = (SPRInfoImpl) subscriber;
			sprInfoImpl.setSubscriberIdentity(subscriberId);
			Integer updateAction = Integer.valueOf(request.getParameter(Attributes.UPDATE_ACTION));

			if(Strings.isNullOrBlank(sprInfoImpl.getPassword()) == false){
				String encryptedPassword = PasswordEncryption.getInstance().crypt(sprInfoImpl.getPassword(), Integer.valueOf(sprInfoImpl.getEncryptionType()));
				sprInfoImpl.setPassword(encryptedPassword);
			}
			sprInfoImpl.setModifiedDate(new Timestamp(System.currentTimeMillis()));
			SubscriberDAO.getInstance().updateSubscriber(sprInfoImpl, getStaffData(), request.getRemoteAddr());

			request.getSession().setAttribute(Attributes.SUBSCRIBER_IDENTITY, subscriberId);
			messageFormat = new MessageFormat(getText(ActionMessageKeys.UPDATE_SUCCESS.key));
			addActionMessage(messageFormat.format(messageParameter));

			reAuth(subscriberId, updateAction);
			setActionChainUrl(SUBSCRIBER_VIEW);
			return Results.REDIRECT_ACTION.getValue();
		} catch(UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while updating subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, e.getMessage());
			return Results.LIST.getValue();
		} catch(OperationFailedException e) {
			messageFormat = new MessageFormat(getText(ActionMessageKeys.UPDATE_FAILURE.key));
			addActionError(messageFormat.format(messageParameter));
			addActionError(e.getMessage());
			getLogger().error(MODULE, "Error while updating subscriber with ID: "+ subscriber.getSubscriberIdentity() + ". Reason: "	+ e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return Results.LIST.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while updating subscriber with ID: "+ subscriber.getSubscriberIdentity() + ". Reason: "	+ e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.SUBSCRIBER + " " +getText(ActionMessageKeys.UPDATE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.LIST.getValue();
		}

	}

	/*
		This method is created to isolate ReAuth Handling from subscriber operations
	 */
	private void reAuth(String subscriberId, Integer updateAction) {
		try {
			ReAuthUtil.doReAuthBySubscriberId(subscriberId, updateAction);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		}
	}

	@SkipValidation
	public String markedForDeletion() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called markedForDeletion()");
		}
		String subscriberId = parseQueryStringAndGetSubscriberIdentity();
		MessageFormat messageFormat;
		if(Strings.isNullOrBlank(subscriberId)) {
			getLogger().error(MODULE, "Error while deleting subscriber. Reason: Subscriber Identity missing");
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
		}

		try {
			Integer updateAction = Integer.valueOf(request.getParameter(Attributes.UPDATE_ACTION));
			SubscriberDAO.getInstance().markedForDeletion(subscriberId, getStaffData(), request.getRemoteAddr());
			reAuth(subscriberId, updateAction);

		} catch(UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error while deleting subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, e.getMessage());
			return Results.LIST.getValue();
		} catch(OperationFailedException e) {
			messageFormat = new MessageFormat(getText(ActionMessageKeys.DELETE_SUCCESS.key));
			addActionError(messageFormat.format(messageParameter));
			addActionError("Unable to perform Reauthorize operation");
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return Results.LIST.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while deleting subscriber with ID: " + subscriber.getSubscriberIdentity() + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.SUBSCRIBER + " " +getText(ActionMessageKeys.DELETE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.LIST.getValue();
		}
		messageFormat = new MessageFormat(getText(ActionMessageKeys.DELETE_SUCCESS.key));
		addActionMessage(messageFormat.format(messageParameter));
		return Results.LIST.getValue();
	}

	@SkipValidation
	public String search() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called search()");
		}
		criteria = Collections.emptyMap();
		String json = GsonFactory.defaultInstance().toJson(criteria);
		request.setAttribute(Attributes.CRITERIA, json);
		return Results.LIST.getValue();
	}

	@SkipValidation
	public String searchCriteria() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called searchCriteria()");
		}
		String alternateIdField = request.getParameter(Attributes.ALTERNATE_ID_FIELD);
		criteria.put(Attributes.SUBSCRIBER_IDENTITY, subscriber.getSubscriberIdentity());
		if(Strings.isNullOrBlank(alternateIdField) == false){
			criteria.put(Attributes.ALTERNATE_ID_FIELD, alternateIdField);
		}
		String json = GsonFactory.defaultInstance().toJson(criteria);
		request.setAttribute(Attributes.CRITERIA, json);
		return Results.LIST.getValue();
	}

	@SkipValidation
	public String view() {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called view()");
		}
		String subscriberIdentity = parseQueryStringAndGetSubscriberIdentity();

		if(Strings.isNullOrBlank(subscriberIdentity)){
			subscriberIdentity = (String) request.getAttribute(Attributes.SUBSCRIBER_IDENTITY);
		}

		//isChainedActionRequest then fetch it from session
		if (Strings.isNullOrEmpty(subscriberIdentity)) {
			subscriberIdentity = (String) session.get(Attributes.SUBSCRIBER_IDENTITY);
		}

		try {
			subscriber = SubscriberDAO.getInstance().getSubscriberById(subscriberIdentity, getStaffData());
			if(subscriber == null){
				return subscriberNotFoundError(subscriberIdentity);
			}
			String productOfferName = subscriber.getProductOffer();
			ProductOffer productOffer = getNVSMXPolicyRepository().getProductOffer().byName(productOfferName);
			currency = productOffer.getCurrency();
			UserPackage dataServicePkgData = productOffer.getDataServicePkgData();

			session.put(Attributes.SUBSCRIBER_IDENTITY, subscriberIdentity);
			session.put(Attributes.SUBSCRIBER, subscriber);

			Collection<MonetaryBalance> monetaryBalanceTmp = SubscriberDAO.getInstance().getMonetaryBalance(subscriberIdentity, monetaryBal -> true).getAllBalance();
			List<MonetaryBalanceWrapper> monetaryBalance = new ArrayList<>();

			if(monetaryBalanceTmp != null && monetaryBalanceTmp.isEmpty()==false) {
				long currentTime = System.currentTimeMillis();
				for(MonetaryBalance bal : monetaryBalanceTmp)
				{
					if(bal.getValidToDate()<currentTime){
						continue;
					}
					monetaryBalance.add(new MonetaryBalanceWrapper.MonetaryBalanceWrapperBuilder().withMonetaryBalanceData(bal, getNVSMXPolicyRepository()).build());
				}
				Collections.sort(monetaryBalance);
			}
			Gson gsonBalance = GsonFactory.defaultInstance();
			JsonArray asJsonArray = gsonBalance.toJsonTree(monetaryBalance).getAsJsonArray();
			setMonetaryBalance(asJsonArray);
			if(SubscriberDAO.getInstance().isTestSubscriber(subscriberIdentity)){
				setAllAddOnsForSameCurrency(productOffer.getCurrency());
				setAllBodDataList(dataServicePkgData);
				setTopUpList((BasePackage) dataServicePkgData,true);
			}else{
				setLiveAddOns();
				setLiveBodDataList(dataServicePkgData);
				setTopUpList((BasePackage) dataServicePkgData,false);
			}

			services = getNVSMXPolicyRepository().getService().all();

			Integer unsubscribeFailureCount = (Integer) session.get(Attributes.UNSUBSCRIBE_FAILURE_COUNT);
			if(unsubscribeFailureCount != null){
				Integer maxFailureCount = ConfigurationProvider.getInstance().getMaxFailureCount();
				if(unsubscribeFailureCount >= 1 && unsubscribeFailureCount <= maxFailureCount){
					String addonSubscriptionId = request.getParameter("addonSubscriptionId");
					String addonId = request.getParameter("addonId");
					request.setAttribute("addonSubscriptionId", addonSubscriptionId);
					request.setAttribute("addonId", addonId);
					addFieldError("jCaptchaResponse", "Invalid Captcha");
				}
			}

			SessionSearchField sessionSearchFields = new SessionSearchField();
			sessionSearchFields.setSessionAttribute(SessionSearchAttributes.SUBSCIBER_IDENTITY);
			sessionSearchFields.setAttributeValue(subscriber.getSubscriberIdentity());
			Gson gson = GsonFactory.defaultInstance();

			SubscriberAlternateIds subscriberAlternateIds = SubscriberDAO.getInstance().getExternalAlternateIds(subscriberIdentity,getStaffData());
			if(subscriberAlternateIds!= null) {
				List<com.elitecore.corenetvertex.util.commons.Entry> entries = convertAlternateIdsToListOfEntry(subscriberAlternateIds.getSubscriberAlternateIdStatusList());
				setSubscriberAlternateIds(gson.toJsonTree(entries, new TypeToken<List<com.elitecore.corenetvertex.util.commons.Entry>>() {
				}.getType()).getAsJsonArray());
			}

			setSubscriptionInformationJsonArray(searchSubscriptions());
			String criteriaJson = gson.toJson(sessionSearchFields);
			request.setAttribute("criteriaJson", criteriaJson);
			return Results.VIEW.getValue();
		} catch(UnauthorizedActionException e) {
			getLogger().error(MODULE, "Unable to fetch subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, e.getMessage());
			return Results.LIST.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Unable to fetch subscriber for SubscriberIdentity '"+subscriberIdentity+ "' Reason: "	+ e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.VIEW_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.LIST.getValue();
		}
	}

	private List<com.elitecore.corenetvertex.util.commons.Entry> convertAlternateIdsToListOfEntry(List<SubscriberAlternateIdStatusDetail> alternateIdStatusDetailList) {
		if(CollectionUtils.isEmpty(alternateIdStatusDetailList)){
			return null;
		}
		List<com.elitecore.corenetvertex.util.commons.Entry> entryList = new ArrayList<>();
		for(SubscriberAlternateIdStatusDetail entry:alternateIdStatusDetailList){
			if(AlternateIdType.EXTERNAL.name().equalsIgnoreCase(entry.getType())){
				entryList.add(new com.elitecore.corenetvertex.util.commons.Entry(entry.getAlternateId(),entry.getStatus()));
			}
		}
		return entryList;

	}

	private void setLiveAddOns() {
		Predicate staffGroupProductOfferPredicate = StaffGroupProductOfferPredicate.create(getStaffBelongingGroupsIds());
		List<ProductOffer> allAddOnDatas = getNVSMXPolicyRepository().getProductOffer()
				.addOn().active().live().all()
				.stream().filter(staffGroupProductOfferPredicate::apply)
				.filter(productOffer -> productOffer.getPolicyStatus() != PolicyStatus.FAILURE)
				.collect(Collectors.toList());
		if(Collectionz.isNullOrEmpty(allAddOnDatas) == false){
			JsonArray asJsonArray = GsonFactory.defaultInstance().toJsonTree(allAddOnDatas, new TypeToken<List<AddOn>>() {
			}.getType()).getAsJsonArray();
			setLiveAddOnString(asJsonArray.toString());
		}
	}

	private void setAllAddOns() {
		StaffGroupProductOfferPredicate staffGroupProductOfferPredicate = StaffGroupProductOfferPredicate.create(getStaffBelongingGroupsIds());
		List<ProductOffer> allAddOnDatas = getNVSMXPolicyRepository().getProductOffer()
				.addOn().active().all()
				.stream()
				.filter(staffGroupProductOfferPredicate::apply)
				.filter(productOffer -> productOffer.getPolicyStatus() != PolicyStatus.FAILURE)
				.collect(Collectors.toList());
		if (Collectionz.isNullOrEmpty(allAddOnDatas) == false) {
			JsonArray asJsonArray = GsonFactory.defaultInstance().toJsonTree(allAddOnDatas, new TypeToken<List<AddOn>>() {
			}.getType()).getAsJsonArray();
			setAllAddOnString(asJsonArray.toString());
		}
	}

	private void setAllAddOnsForSameCurrency(String  currency) {
		StaffGroupProductOfferPredicate staffGroupProductOfferPredicate = StaffGroupProductOfferPredicate.create(getStaffBelongingGroupsIds());
		List<ProductOffer> allAddOnDatas = getNVSMXPolicyRepository().getProductOffer()
				.addOn().active().all()
				.stream()
				.filter(staffGroupProductOfferPredicate::apply)
				.filter(productOffer -> productOffer.getPolicyStatus() != PolicyStatus.FAILURE)
				.filter(productOffer -> productOffer.getCurrency().equalsIgnoreCase(currency))
				.collect(Collectors.toList());
		if (Collectionz.isNullOrEmpty(allAddOnDatas) == false) {
			JsonArray asJsonArray = GsonFactory.defaultInstance().toJsonTree(allAddOnDatas, new TypeToken<List<AddOn>>() {
			}.getType()).getAsJsonArray();
			setAllAddOnString(asJsonArray.toString());
		}
	}

	private String subscriberNotFoundError(String subscriberIdentity) {
		getLogger().error(MODULE, "Unable to fetch subscriber. Reason: Subscriber " + subscriberIdentity +" does not exist or deleted");
		addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
		addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
		return Results.LIST.getValue();
	}

	private void setTopUpList(BasePackage basePackage, boolean isTestSubscriber){
		if (Objects.isNull(basePackage)) {
			getLogger().error(MODULE, "Unable to find applicable Top Ups. Reason: Product offer does not have Base package attached");
			return ;
		}


		if (basePackage.isQuotaProfileExist() == false) {
			getLogger().error(MODULE, "Unable to find applicable Top Ups . Reason: Base package does not have any quota profile attached");
			return ;
		}

		if(isTestSubscriber){
			setAllTopUpsList(basePackage);
		}else{
			setLiveTopUpsList(basePackage);
		}
	}

	private void setLiveTopUpsList(BasePackage subscriberBasePackage) {
		List<QuotaTopUp> activeLiveQuotaTopUps = getNVSMXPolicyRepository().getActiveLiveQuotaTopUpDatas();

		if (Collectionz.isNullOrEmpty(activeLiveQuotaTopUps)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Quota Top-Up List can't be set.Reason: No active live Top-Up found");
			}
			return;
		}
		Collectionz.filter(activeLiveQuotaTopUps,Predicates.and(FailedQuotaTopUpFilter.getInstance(),StaffGroupQuotaTopUpPredicate.create(getStaffBelongingGroupsIds())));

		if (Collectionz.isNullOrEmpty(activeLiveQuotaTopUps)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Quota Top-Up List can't be set.Reason: No live Top-Up belong to staff: " + getStaffData().getUserName());
			}
			return;
		}

		List<QuotaTopUp> quotaTopUpByBasePackage = getQuotaTopUpByBasePackage(activeLiveQuotaTopUps, subscriberBasePackage);

		JsonArray asJsonArray = GsonFactory.defaultInstance().toJsonTree(quotaTopUpByBasePackage, new TypeToken<List<QuotaTopUp>>() {
		}.getType()).getAsJsonArray();
		setLiveTopUps(quotaTopUpByBasePackage);
		setLiveTopUpsString(asJsonArray.toString());
	}

	private void setAllTopUpsList(BasePackage basePackage) {
		List<QuotaTopUp> activeAllQuotaTopUps = getNVSMXPolicyRepository().getActiveAllQuotaTopUpDatas();

		if (Collectionz.isNullOrEmpty(activeAllQuotaTopUps)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Quota Top-Up List can't be set.Reason: No active Top-Up found");
			}
			return;
		}
		Collectionz.filter(activeAllQuotaTopUps, Predicates.and(FailedQuotaTopUpFilter.getInstance(),StaffGroupQuotaTopUpPredicate.create(getStaffBelongingGroupsIds())));

		if (Collectionz.isNullOrEmpty(activeAllQuotaTopUps)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Quota Top-Up List can't be set.Reason: No Top-Up belong to staff :" + getStaffData().getUserName());
			}
			return;
		}
		List<QuotaTopUp> quotaTopUpByBasePackage = getQuotaTopUpByBasePackage(activeAllQuotaTopUps, basePackage);

		JsonArray asJsonArray = GsonFactory.defaultInstance().toJsonTree(quotaTopUpByBasePackage, new TypeToken<List<QuotaTopUp>>() {
		}.getType()).getAsJsonArray();

		setAllTopUpsString(asJsonArray.toString());

	}

	private void setAllBodDataList(UserPackage basePackage) {

		if(Objects.isNull(basePackage) || basePackage.isQoSProfileDetailsExist()==false){
			return;
		}
		Timestamp currentTime = new Timestamp(TimeSource.systemTimeSource().currentTimeInMillis());
		List<String> pccProfileNames = basePackage.getQoSProfiles().stream().map(QoSProfile::getName).collect(Collectors.toList());
		ApplicableBoDByBasePackagePredicate applicableBoDPredicate = ApplicableBoDByBasePackagePredicate.create(pccProfileNames);
		List<BoDPackage> allBodPackages = getNVSMXPolicyRepository().getBoDPackage()
				.all().stream()
				.filter(StaffGroupBoDPackagePredicate.create(getStaffBelongingGroupsIds()))
				.filter(bodPackage -> bodPackage.getPolicyStatus() != PolicyStatus.FAILURE)
				.filter(bodPackage -> bodPackage.getPkgStatus() == PkgStatus.ACTIVE)
				.filter(applicableBoDPredicate)
				.filter(boDPackage -> ((Objects.nonNull(boDPackage.getAvailabilityStartDate())?boDPackage.getAvailabilityStartDate().before(currentTime):true)
						&& (Objects.nonNull(boDPackage.getAvailabilityEndDate())?boDPackage.getAvailabilityEndDate().after(currentTime):true)))
				.collect(Collectors.toList());
		if (Collectionz.isNullOrEmpty(allBodPackages) == false) {
			JsonArray asJsonArray = GsonFactory.defaultInstance().toJsonTree(allBodPackages, new TypeToken<List<AddOn>>() {
			}.getType()).getAsJsonArray();
			setAllBodDataString(asJsonArray.toString());
		}
	}

	private void setLiveBodDataList(UserPackage basePackage) {
		if(Objects.isNull(basePackage) || basePackage.isQoSProfileDetailsExist()==false){
			return;
		}
		Timestamp currentTime = new Timestamp(TimeSource.systemTimeSource().currentTimeInMillis());
		List<String> pccProfileNames = basePackage.getQoSProfiles().stream().map(QoSProfile::getName).collect(Collectors.toList());
		ApplicableBoDByBasePackagePredicate applicableBoDPredicate = ApplicableBoDByBasePackagePredicate.create(pccProfileNames);
		List<BoDPackage> allBodPackages = getNVSMXPolicyRepository().getBoDPackage()
				.all().stream()
				.filter(StaffGroupBoDPackagePredicate.create(getStaffBelongingGroupsIds()))
				.filter(bodPackage -> bodPackage.getPolicyStatus() != PolicyStatus.FAILURE)
				.filter(bodPackage -> bodPackage.getPkgStatus() == PkgStatus.ACTIVE)
				.filter(bodPackage -> bodPackage.getPackageMode().getOrder() > PkgMode.TEST.getOrder())
				.filter(applicableBoDPredicate)
				.filter(boDPackage -> ((Objects.nonNull(boDPackage.getAvailabilityStartDate())?boDPackage.getAvailabilityStartDate().before(currentTime):true)
						&& (Objects.nonNull(boDPackage.getAvailabilityEndDate())?boDPackage.getAvailabilityEndDate().after(currentTime):true)))
				.collect(Collectors.toList());
		if (Collectionz.isNullOrEmpty(allBodPackages) == false) {
			JsonArray asJsonArray = GsonFactory.defaultInstance().toJsonTree(allBodPackages, new TypeToken<List<AddOn>>() {
			}.getType()).getAsJsonArray();
			setLiveBodDataString(asJsonArray.toString());
		}
	}


	public JsonArray searchSubscriptions() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "searchSubscription Method called");
		}

		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		List<SubscriptionInformation> subscriptionInformationList;
		try {

			subscriptionInformationList = Collectionz.newArrayList();
			SPRInfo subscriberData = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			if (subscriberData != null) {

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "getting Subscriptions for Subscriber: " + subscriberData);
				}

				List<Subscription> addOnSubscriptions = SubscriberDAO.getInstance().getSubscribedAddOns(subscriberData, getStaffData());
				List<Subscription> topUpSubscriptions = SubscriberDAO.getInstance().getTopUpSubscriptions(subscriberData.getSubscriberIdentity(),getStaffData());
				List<Subscription> bodSubscriptions = SubscriberDAO.getInstance().getBodSubscriptions(subscriberData.getSubscriberIdentity(),getStaffData());
				Map<String, Map<String, SubscriberUsage>> currentUsage = SubscriberDAO.getInstance().getCurrentUsage(subscriberData.getSubscriberIdentity());

				SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = SubscriberDAO.getInstance().getRGNonMonitoryBalanceWithResetExpiredBalance(subscriberData,null,null);
				SubscriberRnCNonMonetaryBalance subscriberNonMonitoryRnCBalance = SubscriberDAO.getInstance().getRncNonMonetaryBalance(subscriberData,null,null);

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Total Subscription found are " + addOnSubscriptions.size());
				}


				Set<RnCPackage> rnCPackageList = new HashSet<>();
				String baseProductOfferName = subscriberData.getProductOffer();
				ProductOffer baseProductOffer = DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().byName(baseProductOfferName);

				List <SubscriptionInformation> baseSubscription = null;
				if(Objects.nonNull(baseProductOffer)) {
					BasePackage basePackage = (BasePackage) baseProductOffer.getDataServicePkgData();
					List<ProductOfferServicePkgRel> productOfferServicePkgRelList = baseProductOffer.getProductOfferServicePkgRelDataList();
					if(Collectionz.isNullOrEmpty(productOfferServicePkgRelList) == false) {
						for(ProductOfferServicePkgRel productOfferServicePkgRel : productOfferServicePkgRelList){
							rnCPackageList.add(productOfferServicePkgRel.getRncPackageData());
						}
					}

					if (basePackage != null) {
						baseSubscription = getBasePackageSubscription(subscriberData, getQuotaProfileBalance(basePackage, currentUsage.get(basePackage.getId()), subscriberNonMonitoryBalance.getPackageBalance(basePackage.getId())), basePackage,baseProductOffer);
					}

					//Adding base package subscription information

					List<SubscriptionInformation> rncSubscription = new ArrayList<>();
					for(RnCPackage rncPackage : rnCPackageList){
						rncSubscription.addAll(getRnCPackageSubscription(subscriberData, subscriberNonMonitoryRnCBalance.getPackageBalances(), rncPackage, null,baseProductOffer));
					}

					if (baseSubscription != null) {
						subscriptionInformationList.addAll(baseSubscription);
					}

					if (rncSubscription != null) {
						subscriptionInformationList.addAll(rncSubscription);
					}
				}

				List<String> staffBelongingGroups = getStaffBelongingGroupsIds();

				//Adding promotional package subscription information
				List<SubscriptionInformation> promotionalSubscriptions = getPromotionalSubscriptions(currentTime, currentUsage, subscriberNonMonitoryBalance);
				if (Collectionz.isNullOrEmpty(promotionalSubscriptions) == false) {
					subscriptionInformationList.addAll(promotionalSubscriptions);
				}

				//Adding AddOn product offer subscription information
				List<SubscriptionInformation> addOnSubscriptionsInformations = getAddOnSubscriptions(subscriberData, addOnSubscriptions, currentUsage, subscriberNonMonitoryBalance, subscriberNonMonitoryRnCBalance);
				if (Collectionz.isNullOrEmpty(addOnSubscriptionsInformations) == false) {
					subscriptionInformationList.addAll(addOnSubscriptionsInformations);
				}

				//Adding topup package subscription information
				List<SubscriptionInformation> topUpSubscriptionsList = getTopUpSubscriptions(subscriberData, topUpSubscriptions, currentUsage, subscriberNonMonitoryBalance);
				if (Collectionz.isNullOrEmpty(topUpSubscriptionsList) == false) {
					subscriptionInformationList.addAll(topUpSubscriptionsList);
				}

				//Adding bod package subscription information
				List<SubscriptionInformation> bodSubscriptionList = getBodSubscriptions(subscriberData, bodSubscriptions);
				if (Collectionz.isNullOrEmpty(bodSubscriptionList) == false) {
					subscriptionInformationList.addAll(bodSubscriptionList);
				}

				int noOfSubscriptions = subscriptionInformationList.size();
				Collectionz.filter(subscriptionInformationList, new SubscriptionPkgGroupPredicate(staffBelongingGroups));
				if (noOfSubscriptions != subscriptionInformationList.size()) {
					request.setAttribute(Attributes.SHOW_WARNING, "true");
				}
			}

		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error in reading addOns for subscriber. Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			subscriptionInformationList = Collections.emptyList();
		} catch (Exception ex) {
			getLogger().error(MODULE, "Error in reading addOns for subscriber. Reason: " + ex.getMessage());
			getLogger().trace(MODULE, ex);
			subscriptionInformationList = Collections.emptyList();
			addActionError(getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
		}
		Gson gson = GsonFactory.defaultInstance();
		return gson.toJsonTree(subscriptionInformationList).getAsJsonArray();
	}

	private List<String> getStaffBelongingGroupsIds() {
		return CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
	}

	private List<SubscriptionInformation>  getBasePackageSubscription(SPRInfo subscriber, List<QuotaProfileBalance> quotaProfileBalance, BasePackage basePackage, ProductOffer productOffer) {

		if (basePackage == null) {
			getLogger().error(MODULE, "Data package associated with product offer(" + subscriber.getProductOffer() + ") associated to subscriber: " + subscriber.getSubscriberIdentity() + " not found");
			return null;

		} else {
			List<SubscriptionInformation> basePackageSubscriptionInformationList = new ArrayList<>();

			SubscriptionInformation basePackageSubscriptionInformation = new SubscriptionInformation();
			basePackageSubscriptionInformation.setPackageName(basePackage.getName());
			basePackageSubscriptionInformation.setPackageId(basePackage.getId());
			basePackageSubscriptionInformation.setProductOfferName(productOffer.getName());
			basePackageSubscriptionInformation.setProductOfferId(productOffer.getId());
			basePackageSubscriptionInformation.setPackageType(PkgType.BASE.name());
			basePackageSubscriptionInformation.setPkgTypeDisplayValue(PkgType.BASE.val);
			basePackageSubscriptionInformation.setQuotaProfileBalances(quotaProfileBalance);
			basePackageSubscriptionInformation.setGroupIds(basePackage.getGroupIds());
			basePackageSubscriptionInformationList.add(basePackageSubscriptionInformation);

			return basePackageSubscriptionInformationList;
		}
	}

	private List<SubscriptionInformation> getRnCPackageSubscription(SPRInfo subscriber, Map<String, SubscriptionRnCNonMonetaryBalance> subscriptionNonMonitoryBalance, RnCPackage rncPackage, Subscription subscription, ProductOffer productOffer) {

		if (rncPackage == null) {
			getLogger().error(MODULE, "RnC Package associated with product offer(" + subscriber.getProductOffer() + ") associated to subscriber: " + subscriber.getSubscriberIdentity() + " not found");
			return null;
		} else if (Objects.isNull(subscriptionNonMonitoryBalance)) {
			getLogger().debug(MODULE, "RnC Package associated with product offer(" + subscriber.getProductOffer() + ") associated to subscriber: " + subscriber.getSubscriberIdentity() + " does not contains any Non-Monetary Rate Card");
			return null;
		} else {

			List<SubscriptionInformation> rncPackageSubscriptionInformationList = new ArrayList<>();

			SubscriptionRnCNonMonetaryBalance subRnCNonMonetaryBalance = null;
			SubscriptionInformation basePackageSubscriptionInformationForRnc = new SubscriptionInformation();

			if(subscription == null){
				subRnCNonMonetaryBalance = subscriptionNonMonitoryBalance.get(rncPackage.getId());
			} else {
				subRnCNonMonetaryBalance = subscriptionNonMonitoryBalance.get(subscription.getId());
				basePackageSubscriptionInformationForRnc.setAddonSubscriptionId(subscription.getId());
				basePackageSubscriptionInformationForRnc.setStartTime(NVSMXUtil.simpleDateFormatPool.get().format(subscription.getStartTime()));
				basePackageSubscriptionInformationForRnc.setEndTime(NVSMXUtil.simpleDateFormatPool.get().format(subscription.getEndTime()));
				basePackageSubscriptionInformationForRnc.setAddOnStatus(subscription.getStatus());
				basePackageSubscriptionInformationForRnc.setPriority(String.valueOf(subscription.getPriority()));
			}

			basePackageSubscriptionInformationForRnc.setPackageName(rncPackage.getName());
			basePackageSubscriptionInformationForRnc.setPackageType(CommonConstants.RNC);
			basePackageSubscriptionInformationForRnc.setPkgTypeDisplayValue(CommonConstants.RNC);
			basePackageSubscriptionInformationForRnc.setRncBalances(getNonMonetaryRateCardBalance(rncPackage, subRnCNonMonetaryBalance));
			basePackageSubscriptionInformationForRnc.setGroupIds(rncPackage.getGroupIds());
			basePackageSubscriptionInformationForRnc.setChargingType(rncPackage.getChargingType().name());

			basePackageSubscriptionInformationForRnc.setPackageDescription(productOffer.getDescription());
			basePackageSubscriptionInformationForRnc.setPackageId(rncPackage.getId());
			basePackageSubscriptionInformationForRnc.setProductOfferName(productOffer.getName());
			basePackageSubscriptionInformationForRnc.setProductOfferId(productOffer.getId());
			rncPackageSubscriptionInformationList.add(basePackageSubscriptionInformationForRnc);

			return rncPackageSubscriptionInformationList;
		}
	}

	private List<SubscriptionInformation> getPromotionalSubscriptions(Timestamp currentTime, Map<String, Map<String, SubscriberUsage>> currentUsage, SubscriberNonMonitoryBalance subscriberNonMonitoryBalance) {

		List<SubscriptionInformation> promotionalSubscriptions = Collectionz.newArrayList();
		for (PromotionalPackage promotionalPackage : DefaultNVSMXContext.getContext().getPolicyRepository().getPromotionalPackages()) {

			if (currentTime.after(promotionalPackage.getAvailabilityEndDate())) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping Promotional Package " + promotionalPackage.getName() + ".Reason: Availability End Time has been elapsed");
				}
				continue;
			}

			SubscriptionInformation promotionalSubscriptionInformation = new SubscriptionInformation();
			promotionalSubscriptionInformation.setPackageName(promotionalPackage.getName());
			promotionalSubscriptionInformation.setPackageId(promotionalPackage.getId());
			promotionalSubscriptionInformation.setPackageType(PkgType.PROMOTIONAL.name());
			promotionalSubscriptionInformation.setPkgTypeDisplayValue(PkgType.PROMOTIONAL.val);
			List<QuotaProfileBalance> profileBalance = getQuotaProfileBalance(promotionalPackage, currentUsage.get(promotionalPackage.getId()), subscriberNonMonitoryBalance.getPackageBalance(promotionalPackage.getId()));
			promotionalSubscriptionInformation.setQuotaProfileBalances(profileBalance);
			promotionalSubscriptionInformation.setGroupIds(promotionalPackage.getGroupIds());
			promotionalSubscriptions.add(promotionalSubscriptionInformation);
		}
		return promotionalSubscriptions;

	}

	private List<SubscriptionInformation> getAddOnSubscriptions(SPRInfo subscriber, List<Subscription> addOnSubscriptions, Map<String, Map<String, SubscriberUsage>> currentUsage, SubscriberNonMonitoryBalance subscriberNonMonitoryBalance, SubscriberRnCNonMonetaryBalance subscriberNonMonitoryRnCBalance) {

		List<SubscriptionInformation> addOnSubscriptionsInformations = Collectionz.newArrayList();

		for (Subscription subscription : addOnSubscriptions) {

			ProductOffer productOffer = DefaultNVSMXContext.getContext().getPolicyRepository().getProductOffer().byId(subscription.getProductOfferId());

			if (productOffer == null) {
				getLogger().warn(MODULE, "Skipping subscription(id:" + subscription.getId() + ") for subscriber ID: "
						+ subscriber.getSubscriberIdentity() + ". Reason: Package not found for id: "
						+ subscription.getProductOfferId());
				continue;
			}

			if (SubscriptionType.RO_ADDON == subscription.getType()) {
				RnCPackage rncPackageData = getNVSMXPolicyRepository().getRnCPackage().byId(subscription.getPackageId());

				if (rncPackageData == null) {
					continue;
				}

				if (subscription.getPackageId().equalsIgnoreCase(rncPackageData.getId())) {
					addOnSubscriptionsInformations.addAll(getRnCPackageSubscription(subscriber, subscriberNonMonitoryRnCBalance.getPackageBalances(), rncPackageData, subscription, productOffer));
				}
				continue;
			}
			UserPackage dataPackage = getNVSMXPolicyRepository().getPkgDataById(subscription.getPackageId());
			if (dataPackage != null) {
				List<QuotaProfileBalance> profileBalance = getQuotaProfileBalance(dataPackage, currentUsage.get(subscription.getId()), subscriberNonMonitoryBalance.getPackageBalance(subscription.getId()));
				addOnSubscriptionsInformations.add(
						createSubscriptionInformation(subscription, profileBalance,
								productOffer.getDataServicePkgData().getName(), productOffer.getDescription(),
								productOffer.getDataServicePkgData().getId(), productOffer.getType().name(),
								productOffer.getType().val, productOffer.getGroups(), productOffer.getName(),productOffer.getId())
				);
			}
		}
		return addOnSubscriptionsInformations;
	}


	private List<SubscriptionInformation> getTopUpSubscriptions(SPRInfo subscriber, List<Subscription> topupSubscriptions, Map<String, Map<String, SubscriberUsage>> currentUsage, SubscriberNonMonitoryBalance subscriberNonMonitoryBalance) {

		List<SubscriptionInformation> addOnSubscriptionsInformations = Collectionz.newArrayList();

		for (Subscription subscription : topupSubscriptions) {

			QuotaTopUp subscriptionPackage = DefaultNVSMXContext.getContext().getPolicyRepository().getQuotaTopUpById(subscription.getPackageId());

			if (subscriptionPackage == null) {
				getLogger().warn(MODULE, "Skipping subscription(id:" + subscription.getId() + ") for subscriber ID: "
						+ subscriber.getSubscriberIdentity() + ". Reason: Package not found for id: "
						+ subscription.getPackageId());
				continue;
			}

			List<QuotaProfileBalance> profileBalance = getTopUpQuotaProfileBalance(subscriptionPackage, currentUsage.get(subscription.getId()), subscriberNonMonitoryBalance.getPackageBalance(subscription.getId()));
			addOnSubscriptionsInformations.add(
					createSubscriptionInformation(subscription, profileBalance,
							subscriptionPackage.getName(), subscriptionPackage.getDescription(),
							subscriptionPackage.getId(), subscriptionPackage.getPackageType().name(),
							subscriptionPackage.getPackageType().getVal(), subscriptionPackage.getGroupIds(),null, null));
		}
		return addOnSubscriptionsInformations;
	}


	private List<SubscriptionInformation> getBodSubscriptions(SPRInfo subscriber, List<Subscription> bodSubscriptions) {

		List<SubscriptionInformation> bodSubscriptionsInformations = Collectionz.newArrayList();

		for (Subscription subscription : bodSubscriptions) {

			BoDPackage subscriptionPackage = DefaultNVSMXContext.getContext().getPolicyRepository().getBoDPackage().byId(subscription.getPackageId());

			if (subscriptionPackage == null) {
				getLogger().warn(MODULE, "Skipping subscription(id:" + subscription.getId() + ") for subscriber ID: "
						+ subscriber.getSubscriberIdentity() + ". Reason: BoD Package not found for id: "
						+ subscription.getPackageId());
				continue;
			}

			bodSubscriptionsInformations.add(
					createSubscriptionInformation(subscription, null,
							subscriptionPackage.getName(), subscriptionPackage.getDescription(),
							subscriptionPackage.getId(), SubscriptionType.BOD.name(),
							"BoD", subscriptionPackage.getGroupIds(),null, null));
		}
		return bodSubscriptionsInformations;
	}


	private SubscriptionInformation createSubscriptionInformation(Subscription subscription, List<QuotaProfileBalance> profileBalance,
																  String subscriptionPackageName, String subscriptionPackageDescription,
																  String subscriptionPackageId, String subscriptionPackageType,
																  String subscriptionPackageDisplayValue, List<String> subscriptionPackageGroupIds, String productOfferName, String productOfferId) {
		SubscriptionInformation subscriptionInformation = new SubscriptionInformation();
		subscriptionInformation.setPackageName(subscriptionPackageName);
		subscriptionInformation.setPackageDescription(subscriptionPackageDescription);
		subscriptionInformation.setPackageId(subscriptionPackageId);
		subscriptionInformation.setPackageType(subscriptionPackageType);
		subscriptionInformation.setPkgTypeDisplayValue(subscriptionPackageDisplayValue);
		subscriptionInformation.setAddonSubscriptionId(subscription.getId());
		subscriptionInformation.setStartTime(NVSMXUtil.simpleDateFormatPool.get().format(subscription.getStartTime()));
		subscriptionInformation.setEndTime(NVSMXUtil.simpleDateFormatPool.get().format(subscription.getEndTime()));
		subscriptionInformation.setAddOnStatus(subscription.getStatus());
		subscriptionInformation.setGroupIds(subscriptionPackageGroupIds);
		subscriptionInformation.setQuotaProfileBalances(profileBalance);
		subscriptionInformation.setProductOfferName(productOfferName);
		subscriptionInformation.setProductOfferId(productOfferId);
		subscriptionInformation.setPriority(String.valueOf(subscription.getPriority()));
		return subscriptionInformation;
	}


	private List<QuotaProfileBalance> getTopUpQuotaProfileBalance(QuotaTopUp quotaTopUp, Map<String, SubscriberUsage> subscriberUsageMap, SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance) {

		if (subscriberUsageMap == null && subscriptionNonMonitoryBalance==null) {
			return Collections.emptyList();
		}

		List<QuotaProfileBalance> quotaProfileBalanceList = Collectionz.newArrayList();
		QuotaProfile quotaProfile = quotaTopUp.getQuotaProfile();
		QuotaProfileBalance quotaProfileBalance = new QuotaProfileBalance();
		quotaProfileBalance.setQuotaProfileId(quotaProfile.getId());
		quotaProfileBalance.setQuotaProfileName(quotaProfile.getName());


		if (subscriberUsageMap != null) {
			Map<String, QuotaProfileDetail> quotaProfileDetailDataList = quotaProfile.getBalanceLevel().getBalanceLevelQuotaProfileDetail(quotaProfile);
			setQuotaProfileBalanceInfoList(quotaProfileBalance, subscriberUsageMap, quotaProfileDetailDataList);
		} else {
			List<Map<String, QuotaProfileDetail>> fupLevelServiceWiseQuotaProfileDetails = quotaProfile.getAllLevelServiceWiseQuotaProfileDetails();
			setQuotaProfileBalanceInfoList(quotaProfileBalance, subscriptionNonMonitoryBalance, fupLevelServiceWiseQuotaProfileDetails);
		}
		quotaProfileBalanceList.add(quotaProfileBalance);
		return quotaProfileBalanceList;
	}


	private List<QuotaProfileBalance> getQuotaProfileBalance(UserPackage userPackage, Map<String, SubscriberUsage> map, SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance) {

		if (map == null && subscriptionNonMonitoryBalance==null) {
			return Collections.emptyList();
		}

		List<QuotaProfileBalance> quotaProfileBalanceList = Collectionz.newArrayList();
		List<QuotaProfile> quotaProfileList = userPackage.getQuotaProfiles();
		for (QuotaProfile quotaProfile : quotaProfileList) {
			QuotaProfileBalance quotaProfileBalance = new QuotaProfileBalance();
			quotaProfileBalance.setQuotaProfileId(quotaProfile.getId());
			quotaProfileBalance.setQuotaProfileName(quotaProfile.getName());

			if (QuotaProfileType.SY_COUNTER_BASED == quotaProfile.getType()) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping balance calculation for Sy quota Profile: " + quotaProfile.getName());
				}
				quotaProfileBalanceList.add(quotaProfileBalance);
				continue;
			}

			if(map!=null) {
				Map<String, QuotaProfileDetail> quotaProfileDetailDataList = quotaProfile.getBalanceLevel().getBalanceLevelQuotaProfileDetail(quotaProfile);
				setQuotaProfileBalanceInfoList(quotaProfileBalance, map, quotaProfileDetailDataList);
			} else {
				List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetails = quotaProfile.getAllLevelServiceWiseQuotaProfileDetails();
				setQuotaProfileBalanceInfoList(quotaProfileBalance, subscriptionNonMonitoryBalance, fupLevelserviceWiseQuotaProfileDetails);
			}
			quotaProfileBalanceList.add(quotaProfileBalance);
		}
		return quotaProfileBalanceList;
	}

	private List<NonMonetaryRateCardBalance> getNonMonetaryRateCardBalance(RnCPackage rncPackage, SubscriptionRnCNonMonetaryBalance subRnCNonMonetaryBalance) {

		Set<RnCNonMonetaryBalance> balanceList = new HashSet<>();
		List<NonMonetaryRateCardBalance> nonMonetaryBalanceList = new ArrayList<>();
		if(Objects.isNull(subRnCNonMonetaryBalance)){
			return null;
		}

		Map<String, RnCNonMonetaryBalance> rnCNonMonetaryBalanceMap = subRnCNonMonetaryBalance.getAllRateCardBalance();
		Map<String, NonMonetaryRateCard> idWiseNonMonetaryRateCard = new HashMap<>();

		for(RateCardGroup rateCardGroup : rncPackage.getRateCardGroups()){

			if(Objects.nonNull(rateCardGroup.getPeakRateCard()) && rateCardGroup.getPeakRateCard().getType().equals(RateCardType.NON_MONETARY)){

				NonMonetaryRateCard peakNonMonetaryRateCard = (NonMonetaryRateCard) rateCardGroup.getPeakRateCard();
				balanceList.add(rnCNonMonetaryBalanceMap.get(peakNonMonetaryRateCard.getId()));
				idWiseNonMonetaryRateCard.put(peakNonMonetaryRateCard.getId(), peakNonMonetaryRateCard);
			}
			if(Objects.nonNull(rateCardGroup.getOffPeakRateCard()) && rateCardGroup.getOffPeakRateCard().getType().equals(RateCardType.NON_MONETARY)){

				NonMonetaryRateCard offPeakNonMonetaryRateCard = (NonMonetaryRateCard) rateCardGroup.getOffPeakRateCard();
				balanceList.add(rnCNonMonetaryBalanceMap.get(offPeakNonMonetaryRateCard.getId()));
				idWiseNonMonetaryRateCard.put(offPeakNonMonetaryRateCard.getId(), offPeakNonMonetaryRateCard);
			}
		}

		for(RnCNonMonetaryBalance rnCNonMonetaryBalance : balanceList){
			NonMonetaryRateCardBalance rateCardBalance = new NonMonetaryRateCardBalance();
			rateCardBalance.setBalanceExpiryTime(rnCNonMonetaryBalance.getBalanceExpiryTime());
			rateCardBalance.setBillingCycleAvailableTime(rnCNonMonetaryBalance.getBillingCycleAvailable());
			rateCardBalance.setBillingCycleTotalTime(rnCNonMonetaryBalance.getBillingCycleTotal());
			rateCardBalance.setDailyResetTime(rnCNonMonetaryBalance.getDailyResetTime());
			rateCardBalance.setReservationTime(rnCNonMonetaryBalance.getReservationTime());
			rateCardBalance.setWeeklyResetTime(rnCNonMonetaryBalance.getWeeklyResetTime());
			rateCardBalance.setId(rnCNonMonetaryBalance.getId());
			rateCardBalance.setPackageId(rnCNonMonetaryBalance.getPackageId());
			rateCardBalance.setRatecardId(rnCNonMonetaryBalance.getRatecardId());
			rateCardBalance.setDailyTimeLimit(rnCNonMonetaryBalance.getDailyLimit());
			rateCardBalance.setRenewalInterval(rnCNonMonetaryBalance.getRenewalInterval());
			rateCardBalance.setSubscriberIdentity(rnCNonMonetaryBalance.getSubscriberIdentity());
			rateCardBalance.setSubscriptionId(rnCNonMonetaryBalance.getSubscriptionId());
			rateCardBalance.setWeeklyTimeLimit(rnCNonMonetaryBalance.getWeeklyLimit());
			rateCardBalance.setStatus(rnCNonMonetaryBalance.getStatus());
			rateCardBalance.setRatecardName(idWiseNonMonetaryRateCard.get(rnCNonMonetaryBalance.getRatecardId()).getName());
			if(rnCNonMonetaryBalance.getBillingCycleTotal() == CommonConstants.QUOTA_UNLIMITED){
				rateCardBalance.setDisplayTotalTime(CommonConstants.UNLIMITED);
				rateCardBalance.setDisplayAvailableTime(CommonConstants.UNLIMITED);
				rateCardBalance.setDisplayUsageTime(formatHHMMSS(rnCNonMonetaryBalance.getBillingCycleTotal() - rnCNonMonetaryBalance.getBillingCycleAvailable()));
			}else if(rnCNonMonetaryBalance.getChargingType() == ChargingType.EVENT){
				rateCardBalance.setDisplayTotalTime(String.valueOf(rnCNonMonetaryBalance.getBillingCycleTotal()));
				rateCardBalance.setDisplayAvailableTime(String.valueOf(rnCNonMonetaryBalance.getBillingCycleAvailable()));
				rateCardBalance.setDisplayUsageTime(String.valueOf(rnCNonMonetaryBalance.getBillingCycleTotal() - rnCNonMonetaryBalance.getBillingCycleAvailable()));
			} else{
				rateCardBalance.setDisplayTotalTime(formatHHMMSS(rnCNonMonetaryBalance.getBillingCycleTotal()));
				rateCardBalance.setDisplayAvailableTime(formatHHMMSS(rnCNonMonetaryBalance.getBillingCycleAvailable()));
				rateCardBalance.setDisplayUsageTime(formatHHMMSS(rnCNonMonetaryBalance.getBillingCycleTotal() - rnCNonMonetaryBalance.getBillingCycleAvailable()));
			}
			nonMonetaryBalanceList.add(rateCardBalance);
		}
		return nonMonetaryBalanceList;
	}

	private String formatHHMMSS(long secondsCount){
		String hour;
		String minute;
		String second;
		//Calculate the seconds to display:
		long seconds = secondsCount %60;
		secondsCount -= seconds;
		//Calculate the minutes:
		long minutesCount = secondsCount / 60;
		long minutes = minutesCount % 60;
		minutesCount -= minutes;
		//Calculate the hours:
		long hoursCount = minutesCount / 60;

		if(hoursCount < 10){
			hour = "0"+hoursCount;
		} else{
			hour = ""+hoursCount;
		}

		if(minutes < 10){
			minute = "0"+minutes;
		} else{
			minute = ""+minutes;
		}

		if(seconds < 10){
			second = "0"+seconds;
		} else{
			second = ""+seconds;
		}
		return hour + " : " + minute + " : " + second;
	}

	private void setQuotaProfileBalanceInfoList(QuotaProfileBalance quotaProfileBalance, Map<String, SubscriberUsage> map, Map<String, QuotaProfileDetail> quotaProfileDetailDataList) {

		for (QuotaProfileDetail quotaProfileDetailData : quotaProfileDetailDataList.values()) {

			if (quotaProfileDetailData instanceof SyCounterBaseQuotaProfileDetail) {
				continue;
			}

			UMBaseQuotaProfileDetail umBaseQuotaProfileDetailData = (UMBaseQuotaProfileDetail) quotaProfileDetailData;
			String key = umBaseQuotaProfileDetailData.getUsageKey();
			SubscriberUsage subscriberUsage = map.get(key);
			if (subscriberUsage == null) {
				continue;
			}

			for (AggregationKey aggregationKey : AggregationKey.values()) {

				AllowedUsage allowedUsage = umBaseQuotaProfileDetailData.getAllowedUsage(aggregationKey);

				if (allowedUsage == null) {
					continue;
				}

				BalanceInfo billingCycleBalance = new BalanceInfo();
				billingCycleBalance.setServiceId(quotaProfileDetailData.getServiceId());
				billingCycleBalance.setServiceName(umBaseQuotaProfileDetailData.getServiceName());
				billingCycleBalance.setAggregationKey(aggregationKey);
				billingCycleBalance.setDownloadOctets(subscriberUsage.getDownload(aggregationKey));
				billingCycleBalance.setUploadOctets(subscriberUsage.getUpload(aggregationKey));
				billingCycleBalance.setTotalOctets(subscriberUsage.getTotal(aggregationKey));
				billingCycleBalance.setTime(subscriberUsage.getBillingCycleTime());
				billingCycleBalance.setUsage(getReadableUsage(subscriberUsage.getTotal(aggregationKey), subscriberUsage.getUpload(aggregationKey), subscriberUsage.getDownload(aggregationKey)));
				billingCycleBalance.setUsageLimitStr(getReadableUsageLimit(allowedUsage.getTotal(), allowedUsage.getTotalUnit(),
						allowedUsage.getUpload(), allowedUsage.getUploadUnit(),
						allowedUsage.getDownload(), allowedUsage.getDownloadUnit()));
				billingCycleBalance.setUsageLimit(allowedUsage.getTotalInBytes());
				billingCycleBalance.setLevel(quotaProfileDetailData.getFupLevel());
				billingCycleBalance.setQuotaUsageType(QuotaUsageType.VOLUME.getValue());
				quotaProfileBalance.addBalanceInformation(billingCycleBalance);
			}

		}
	}

	private void setQuotaProfileBalanceInfoList(QuotaProfileBalance quotaProfileBalance, SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance, List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetails) {

		for (Map<String, QuotaProfileDetail> quotaProfileDetailData : fupLevelserviceWiseQuotaProfileDetails) {

			for(QuotaProfileDetail quotaProfileDetail: quotaProfileDetailData.values()){
				if (quotaProfileDetail instanceof SyCounterBaseQuotaProfileDetail) {
					continue;  //FIXME: Can this case occure?
				}

				RncProfileDetail rncProfileDetail = (RncProfileDetail) quotaProfileDetail;
				NonMonetoryBalance nonMonetoryBalance = subscriptionNonMonitoryBalance.getBalance(rncProfileDetail.getQuotaProfileId(),rncProfileDetail.getDataServiceType().getServiceIdentifier(), rncProfileDetail.getRatingGroup().getIdentifier(), rncProfileDetail.getFupLevel());

				if (nonMonetoryBalance == null) {
					continue;
				}

				VolumeUnitType volumeUnitType = rncProfileDetail.getUnitType();
				for (AggregationKey aggregationKey : AggregationKey.values()) {

					AllowedUsage allowedUsage = rncProfileDetail.getAllowedUsage(aggregationKey);

					if (allowedUsage == null) {
						continue;
					}

					Balance balance = nonMonetoryBalance.createAggregateBalance(aggregationKey);

					BalanceInfo billingCycleBalance = new BalanceInfo();
					billingCycleBalance.setServiceId(rncProfileDetail.getServiceId());
					billingCycleBalance.setServiceName(rncProfileDetail.getServiceName());
					billingCycleBalance.setAggregationKey(aggregationKey);

					long allowedVolumeUsage;
					long allowedTimeUsage;
					if(AggregationKey.BILLING_CYCLE==aggregationKey){
						allowedVolumeUsage = getAllowedVolumeUsage(nonMonetoryBalance.getBillingCycleTotalVolume(),rncProfileDetail.getQuotaUnit());
						allowedTimeUsage = getAllowedTimeUsage(nonMonetoryBalance.getBillingCycleAvailableTime(),rncProfileDetail.getQuotaUnit());
						billingCycleBalance.setTotalOctets(allowedVolumeUsage-balance.total());
						billingCycleBalance.setTime(allowedTimeUsage-balance.time());
					} else {
						billingCycleBalance.setTotalOctets(balance.total());
						billingCycleBalance.setTime(balance.time());
						allowedVolumeUsage = getAllowedVolumeUsage(volumeUnitType.getVolumeInBytes(allowedUsage),rncProfileDetail.getQuotaUnit());
						allowedTimeUsage = getAllowedTimeUsage(allowedUsage.getTimeInSeconds(), rncProfileDetail.getQuotaUnit());
					}

					long totalQuotaAllowedVolume = volumeUnitType.getVolumeInBytes(allowedUsage);
					long totalQuotaAllowedTime = allowedUsage.getTimeInSeconds();

					billingCycleBalance.setTimeLimit(allowedUsage.getTimeInSeconds());


					billingCycleBalance.setUsage(getReadableUsage(balance.total(), allowedVolumeUsage,
							balance.time(), allowedTimeUsage, aggregationKey, rncProfileDetail.getUnitType()));

					double proratedVolumeLimit = getProratedQuotaUnit(totalQuotaAllowedVolume, allowedVolumeUsage,volumeUnitType.getVolume(allowedUsage));
					double proratedTimeLimit = getProratedQuotaUnit(totalQuotaAllowedTime, allowedTimeUsage,allowedUsage.getTime());

					billingCycleBalance.setUsageLimitStr(getReadableUsageLimit(proratedVolumeLimit, allowedUsage.getTotalUnit(),
							proratedTimeLimit, allowedUsage.getTimeUnit(),rncProfileDetail.getUnitType()));
					billingCycleBalance.setUsageLimit(allowedVolumeUsage);
					billingCycleBalance.setLevel(rncProfileDetail.getFupLevel());
					billingCycleBalance.setQuotaUsageType(rncProfileDetail.getQuotaUnit().getValue());
					quotaProfileBalance.addBalanceInformation(billingCycleBalance);
				}
			}

		}
	}

	private double getProratedQuotaUnit(long totalInMinotUnit, long totalProvisionedInMinorUnit, long configuredUnit){
		if(configuredUnit==CommonConstants.QUOTA_UNDEFINED || configuredUnit==CommonConstants.QUOTA_UNLIMITED){
			return configuredUnit;
		}

		return (double)configuredUnit*totalProvisionedInMinorUnit/totalInMinotUnit;
	}

	private long getAllowedVolumeUsage(long totalInMinotUnit, QuotaUsageType type){
		if(type==QuotaUsageType.VOLUME || type==QuotaUsageType.HYBRID){
			return totalInMinotUnit;
		}

		return CommonConstants.QUOTA_UNDEFINED;
	}

	private long getAllowedTimeUsage(long totalInMinotUnit, QuotaUsageType type){
		if(type==QuotaUsageType.TIME || type==QuotaUsageType.HYBRID){
			return totalInMinotUnit;
		}

		return CommonConstants.QUOTA_UNDEFINED;
	}

	private String getReadableUsage(long totalOctets, long uploadOctets, long downloadOctets) {

		String usage = convertBytesToSuitableUnit(totalOctets);
		if (uploadOctets > 0 || downloadOctets > 0) {
			String uploadUsage = convertBytesToSuitableUnit(uploadOctets) + NVSMXCommonConstants.UPLOAD_STRING;
			String downloadUsage = convertBytesToSuitableUnit(downloadOctets) + NVSMXCommonConstants.DOWNLOAD_STRING;
			usage += NVSMXCommonConstants.TOTAL + uploadUsage + downloadUsage;
		}
		return usage;
	}

	private String getReadableUsage(long totalOctets, long allowedOctets,long usedTime, long allowedTime, AggregationKey aggregationKey,VolumeUnitType volumeUsageType ) {

		if(AggregationKey.BILLING_CYCLE == aggregationKey){
			totalOctets = allowedOctets-totalOctets;
			usedTime = allowedTime-usedTime;
		}

		switch (volumeUsageType){
			case TOTAL:
				StringBuilder lineBreak=new StringBuilder("");

				if(allowedOctets!=CommonConstants.QUOTA_UNDEFINED && allowedTime!=CommonConstants.QUOTA_UNDEFINED){
					lineBreak.append(NVSMXCommonConstants.HTML_LINE_BREAK);
				}

				return (allowedOctets!=CommonConstants.QUOTA_UNDEFINED?convertBytesToSuitableUnit(totalOctets)+NVSMXCommonConstants.TOTAL:"") + (CommonConstants.QUOTA_UNDEFINED!=allowedTime?lineBreak+convertTimeToSuitableUnit(usedTime)+NVSMXCommonConstants.TOTAL:"");
			case UPLOAD:
				return (allowedOctets!=CommonConstants.QUOTA_UNDEFINED?convertBytesToSuitableUnit(totalOctets)+NVSMXCommonConstants.UPLOAD_STRING:"") + (CommonConstants.QUOTA_UNDEFINED!=allowedTime? convertTimeToSuitableUnit(usedTime)+NVSMXCommonConstants.TOTAL:"");
			case DOWNLOAD:
				return (allowedOctets!=CommonConstants.QUOTA_UNDEFINED?convertBytesToSuitableUnit(totalOctets)+NVSMXCommonConstants.DOWNLOAD_STRING:"") + (CommonConstants.QUOTA_UNDEFINED!=allowedTime? convertTimeToSuitableUnit(usedTime)+NVSMXCommonConstants.TOTAL:"");
			default:
				return null;
		}
	}

	private String getReadableUsageLimit(Long total, DataUnit totalUnit, Long upload, DataUnit uploadUnit, Long download, DataUnit downloadUnit) {

		String totalStr = total == null || total == 0 || total==CommonConstants.QUOTA_UNLIMITED ? UNLIMITED_USAGE : String.valueOf(total) + " " + totalUnit;

		String uploadStr = upload == null || upload == 0 || upload==CommonConstants.QUOTA_UNLIMITED ? UNLIMITED_USAGE + NVSMXCommonConstants.UPLOAD_STRING : String.valueOf(upload) + " " + uploadUnit + NVSMXCommonConstants.UPLOAD_STRING;

		String downloadStr = download == null || download == 0 || download==CommonConstants.QUOTA_UNLIMITED ? UNLIMITED_USAGE + NVSMXCommonConstants.DOWNLOAD_STRING : String.valueOf(download) + " " + downloadUnit + NVSMXCommonConstants.DOWNLOAD_STRING;
		StringBuilder totalQuota = new StringBuilder(totalStr).append(NVSMXCommonConstants.TOTAL)
				.append(uploadStr)
				.append(downloadStr);

		return totalQuota.toString();
	}

	private String getReadableUsageLimit(double total, DataUnit totalUnit, double time, TimeUnit timeUnit, VolumeUnitType volumeUsageType) {

		String  volumeBalance=null;
		DecimalFormat twoDecimalFormat = new DecimalFormat(".##");

		if(total!=CommonConstants.QUOTA_UNDEFINED){
			switch (volumeUsageType){
				case TOTAL:
					volumeBalance= total==CommonConstants.QUOTA_UNLIMITED ? UNLIMITED_USAGE + NVSMXCommonConstants.TOTAL : twoDecimalFormat.format(total) + " " + totalUnit + NVSMXCommonConstants.TOTAL;
					break;
				case UPLOAD:
					volumeBalance= total==CommonConstants.QUOTA_UNLIMITED ? UNLIMITED_USAGE + NVSMXCommonConstants.UPLOAD_STRING : twoDecimalFormat.format(total) + " " + totalUnit + NVSMXCommonConstants.UPLOAD_STRING;
					break;
				case DOWNLOAD:
					volumeBalance= total==CommonConstants.QUOTA_UNLIMITED ? UNLIMITED_USAGE + NVSMXCommonConstants.DOWNLOAD_STRING : twoDecimalFormat.format(total) + " " + totalUnit + NVSMXCommonConstants.DOWNLOAD_STRING;
					break;
				default:
					break;
			}
		}

		String timeBalance = null;

		if(time!=CommonConstants.QUOTA_UNDEFINED){
			timeBalance = time==CommonConstants.QUOTA_UNLIMITED ? UNLIMITED_USAGE : twoDecimalFormat.format(time) + " " + timeUnit;
		}

		StringBuilder totalQuota = new StringBuilder();

		if(Objects.isNull(volumeBalance)==false){
			totalQuota.append(volumeBalance);
		}

		if(Objects.isNull(timeBalance)==false){
			totalQuota.append(timeBalance).append(NVSMXCommonConstants.TOTAL);
		}

		return totalQuota.toString();
	}

	private PolicyRepository getNVSMXPolicyRepository() {
		return DefaultNVSMXContext.getContext().getPolicyRepository();
	}

	@SkipValidation
	public String viewDeletedProfile() {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called viewDeletedProfile()");
		}

		String subscriberIdentity = request.getParameter("subscriberIdentity");
		Integer unsubscribeFailureCount = (Integer) session.get(Attributes.UNSUBSCRIBE_FAILURE_COUNT);
		if(unsubscribeFailureCount != null && unsubscribeFailureCount >= 1 && unsubscribeFailureCount <= 3){
			addFieldError("jCaptchaResponse", "Invalid Captcha");
		}

		if(Strings.isNullOrBlank(subscriberIdentity)){
			getLogger().error(MODULE, "Unable to fetch deleted subscriber. Reason: Subscriber Identity missing");
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
		}

		try {
			subscriber = SubscriberDAO.getInstance().getDeleteMarkedProfile(subscriberIdentity, getStaffData());
			ProductOffer productOffer = getNVSMXPolicyRepository().getProductOffer().byName(subscriber.getProductOffer());
			UserPackage dataServicePkgData = productOffer.getDataServicePkgData();


			if(SubscriberDAO.getInstance().isTestSubscriber(subscriberIdentity)){
				setAllAddOns();
				setTopUpList((BasePackage) dataServicePkgData,true);
				setAllBodDataList((BasePackage) dataServicePkgData);
			}else{
				setLiveAddOns();
				setTopUpList((BasePackage) dataServicePkgData,false);
			}

			Gson gson = GsonFactory.defaultInstance();

			SubscriberAlternateIds subscriberAlternateIds = SubscriberDAO.getInstance().getExternalAlternateIds(subscriberIdentity,getStaffData());
			if(subscriberAlternateIds!= null) {
				List<com.elitecore.corenetvertex.util.commons.Entry> entries = convertAlternateIdsToListOfEntry(subscriberAlternateIds.getSubscriberAlternateIdStatusList());
				setSubscriberAlternateIds(gson.toJsonTree(entries, new TypeToken<List<com.elitecore.corenetvertex.util.commons.Entry>>() {
				}.getType()).getAsJsonArray());
			}

			session.put(Attributes.SUBSCRIBER_IDENTITY, subscriberIdentity);
			session.put(Attributes.SUBSCRIBER, subscriber);

		} catch(UnauthorizedActionException e) {
			getLogger().error(MODULE, "Unable to fetch deleted subscriber profile. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, e.getMessage());
			return Results.LIST.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Unable to fetch deleted subscriber profile. Reason: "	+ e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.VIEW_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.LIST.getValue();
		}
		return Results.VIEW.getValue();

	}

	public static String convertBytesToSuitableUnit(long bytes) {
		String bytesToSuitableUnit = bytes + " B";
		final double BYTE = 1024;
		final double KB = BYTE;
		final double MB = KB * BYTE;
		final double GB = MB * BYTE;
		DecimalFormat threeDecimalForm = new DecimalFormat("#.###");

		if (bytes >= GB) {
			double tempBytes = bytes / GB;
			bytesToSuitableUnit = threeDecimalForm.format(tempBytes) + " GB";
			return bytesToSuitableUnit;
		}
		if (bytes >= MB) {
			double tempBytes = bytes / MB;
			bytesToSuitableUnit = threeDecimalForm.format(tempBytes) + " MB";
			return bytesToSuitableUnit;
		}
		if (bytes >= KB) {
			double tempBytes = bytes / KB;
			bytesToSuitableUnit = threeDecimalForm.format(tempBytes) + " KB";
			return bytesToSuitableUnit;
		}
		return bytesToSuitableUnit;
	}

	public static String convertTimeToSuitableUnit(long time) {
		long remaingSeconds;

		if(time>= SECONDS_IN_DAY){
			remaingSeconds = time%SECONDS_IN_DAY;
			return Math.floorDiv(time, SECONDS_IN_DAY)+ " Day"+(remaingSeconds>0?" "+convertTimeToSuitableUnit(remaingSeconds):"");
		}

		if(time>= SECONDS_IN_HOUR){
			remaingSeconds = time%SECONDS_IN_HOUR;
			return Math.floorDiv(time, SECONDS_IN_HOUR)+ " Hour"+(remaingSeconds>0?" "+convertTimeToSuitableUnit(remaingSeconds):"");
		}

		if(time>= SECONDS_IN_MINUTES){
			remaingSeconds = time%SECONDS_IN_MINUTES;
			return Math.floorDiv(time, SECONDS_IN_MINUTES)+ " Minute"+(remaingSeconds>0?" "+convertTimeToSuitableUnit(remaingSeconds):"");
		}

		return time+" Second";

	}

	@SkipValidation
	public String subscribeAddOn(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called subscribeAddOnProductOffer()");
		}
		setActionChainUrl(SUBSCRIBER_VIEW+"#section2");
		subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
		String subscriberId = subscriber.getSubscriberIdentity();
		Integer billDay = subscriber.getBillingDate();
		String selectedPackageId = request.getParameter("selectedAddOn");
		String startDate = request.getParameter("subscriptionStartDate");
		String priorityStr = request.getParameter("priority");
		Integer priority = null;
		if(Strings.isNullOrBlank(priorityStr) == false){
			priority = Integer.valueOf(request.getParameter("priority"));
		}
		String pkgType = request.getParameter("pkgType");
		Integer updateAction = Integer.valueOf(request.getParameter(Attributes.UPDATE_ACTION));
		Long startDateInMillis = null;
		if(Strings.isNullOrEmpty(startDate) == false){
			DateConverter converter = new DateConverter();
			Timestamp timeStamp = (Timestamp) converter.convertFromString(null,new String[]{startDate} , Timestamp.class);
			startDateInMillis = timeStamp.getTime();

			ProductOffer addOnProductOffer = getNVSMXPolicyRepository().getProductOffer().byId(selectedPackageId);
			if(addOnProductOffer!=null && addOnProductOffer.getValidityPeriod()!=null && addOnProductOffer.getValidityPeriodUnit()!=null){
				long currentTimeMS = System.currentTimeMillis();
				long endTime = addOnProductOffer.getValidityPeriodUnit().addTime(startDateInMillis,addOnProductOffer.getValidityPeriod());

				if(endTime< currentTimeMS){
					addActionError("Start time for product offer "+addOnProductOffer.getName()+" should not be older than "+addOnProductOffer.getValidityPeriod()+" "+addOnProductOffer.getValidityPeriodUnit().displayValue);
					return Results.REDIRECT_ACTION.getValue();
				}
			}

		}

		try {
			if (PkgType.ADDON.val.equalsIgnoreCase(pkgType)) {
				SubscriberDAO.getInstance().subscribeAddOnProductOfferById(subscriber, subscriber.getParentId(), selectedPackageId
						, startDateInMillis, priority, getStaffData(), billDay, request.getRemoteAddr(),null);
			}else if(SubscriptionType.BOD.name().equalsIgnoreCase(pkgType)) {
				// logic to store data in db
				BoDPackageStore boDPackageStore = getNVSMXPolicyRepository().getBoDPackage();
				if(boDPackageStore == null){
					throw new OperationFailedException("Unable to subscribe BOD with Id:(" + selectedPackageId + ") for subscriber ID: " + subscriberId
							+ ". Reason: BOD Package Store not found for Id: " + selectedPackageId, ResultCode.NOT_FOUND);
				}

				BoDPackage bodPackage = boDPackageStore.byId(selectedPackageId);

				SubscriberDAO.getInstance().subscribeBodPackage(new SubscriptionParameter(subscriber, null,
								null, SubscriptionState.STARTED.state, null, selectedPackageId,
								bodPackage.getName(), startDateInMillis, null, priority, null, null, null, null)
						, getStaffData(), request.getRemoteAddr());

			} else {
				QuotaTopUp topUp = getNVSMXPolicyRepository().getActiveQuotaTopUpById(selectedPackageId);
				if (topUp == null) {
					throw new OperationFailedException("Unable to subscribe quota topUp with Id:(" + selectedPackageId + ") for subscriber ID: " + subscriberId
							+ ". Reason: quota topUp not found for Id: " + selectedPackageId, ResultCode.NOT_FOUND);
				}

				ProductOffer productOffer = getNVSMXPolicyRepository().getProductOffer().byName(subscriber.getProductOffer());
				if(Objects.isNull(productOffer)){
					getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + ")  Reason: Base product offer is not found");
					throw new OperationFailedException("Unable to subscribe Top Up. Reason: Base product offer is not found");
				}

				BasePackage basePackage = (BasePackage) productOffer.getDataServicePkgData();
				if (Objects.isNull(basePackage)) {
					getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + ").Reason: Product offer does not have Base package attached");
					throw  new OperationFailedException("Unable to subscribe topUp(" + topUp.getName() + "). Reason: Product offer does not have Base package attached");
				}

				if (basePackage.isQuotaProfileExist() == false) {
					getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + "). Reason: Base package does not have any quota profile attached");
					throw new OperationFailedException("Unable to subscribe topUp(" + topUp.getName() + "). Reason: Base package does not have any quota profile attached");
				}

				if(subscribingInvalidTopUpPackage(topUp,basePackage)){
					getLogger().error(MODULE, "Unable to subscribe topUp(" + topUp.getName() + "). Reason: Top up is not applicable for subscriber's base Package(" +basePackage.getName()+ ")");
					throw new OperationFailedException("Unable to subscribe topUp(" + topUp.getName() + ") Reason: Top up is not applicable for subscriber's base Package(" +basePackage.getName()+ ")");
				}

				SubscriberDAO.getInstance().subscribeTopUpByName(subscriberId, null, topUp.getName(), SubscriptionState.STARTED,
						startDateInMillis, null, priority, 0,null,getStaffData(), null, null);
			}
			reAuth(subscriberId, updateAction);
			addActionMessage("Package subscribed successfully");
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error in subscribing package with id: " + selectedPackageId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, e.getMessage());
			return Results.REDIRECT_ERROR.getValue();
		} catch (OperationFailedException e) {
			addActionError("Failed to subscribe Package");
			addActionError(e.getMessage());
			getLogger().trace(MODULE, e);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in subscribing package with id: " + selectedPackageId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Package subscribe failed");
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}

		return Results.REDIRECT_ACTION.getValue();
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

	@SkipValidation
	public String addAlternateId() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Adding alternate Id");
		}
		String subscriberId = null;
		try {
			setActionChainUrl(SUBSCRIBER_VIEW + "#section5");
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			subscriberId = subscriber.getSubscriberIdentity();
			String alternateId = request.getParameter("alternateId");
			alternateId = StringUtils.trim(alternateId);
			SubscriberDAO.getInstance().addExternalAlternateIdentity(subscriberId,alternateId,getStaffData());
			addActionMessage(getText("subscriber.alternateid.add.success"));
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			return Results.REDIRECT_ACTION.getValue();
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while adding alternate Identity for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			return Results.REDIRECT_ACTION.getValue();
		}catch (Exception e) {
			getLogger().error(MODULE, "Error while adding alternate Identity for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Add alternate Id failed: Reason " + e.getMessage());
			addActionError(getText(NOTE_REFER_LOGS));
		}
		return Results.ERROR.getValue();
	}

	@SkipValidation
	public String changeAlternateIdentityStatus() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "changing Alternate Identity Status");
		}
		String subscriberId = null;
		try {
			setActionChainUrl(SUBSCRIBER_VIEW + "#section5");
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			subscriberId = subscriber.getSubscriberIdentity();
			if(StringUtils.isEmpty(subscriberId)){
				throw new OperationFailedException("Subscriber id not found",ResultCode.INPUT_PARAMETER_MISSING);
			}
			String[] alternateIds = request.getParameterValues("ids");
			if(ArrayUtils.isEmpty(alternateIds)){
				throw new OperationFailedException("Alternate ids not received",ResultCode.INPUT_PARAMETER_MISSING);
			}
			String status = request.getParameter("status");
			for(String alternateId:alternateIds){
				int result = SubscriberDAO.getInstance().changingExternalAlternateIdentityStatus(subscriberId, alternateId, status, getStaffData());
				if(CommonConstants.STATUS_INACTIVE.equalsIgnoreCase(status) && (ResultCode.SUCCESS == getResultCode(result))){
					getAlternateIdUtils().removeAlternateIdFromCache(alternateId);
				}
			}
			addActionMessage(getText("subscriber.alternateid.changestatus.success"));
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			return Results.REDIRECT_ACTION.getValue();
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while changing Alternate Identity Status for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			return Results.REDIRECT_ACTION.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while changing Alternate Identity Status for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Changing Alternate Identity Status: Reason " + e.getMessage());
			addActionError(getText(NOTE_REFER_LOGS));
		}
		return Results.ERROR.getValue();
	}

	@SkipValidation
	public String removeAlternateIdentity() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "removing alternate Identity");
		}
		String subscriberId = null;
		try {
			setActionChainUrl(SUBSCRIBER_VIEW + "#section5");
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			subscriberId = subscriber.getSubscriberIdentity();
			if(StringUtils.isEmpty(subscriberId)){
				throw new OperationFailedException("Subscriber id not found",ResultCode.INPUT_PARAMETER_MISSING);
			}
			String[] alternateIds = request.getParameterValues("ids");
			if(ArrayUtils.isEmpty(alternateIds)){
				throw new OperationFailedException("Alternate ids not received",ResultCode.INPUT_PARAMETER_MISSING);
			}
			for(String alternateId:alternateIds){
				int result = SubscriberDAO.getInstance().removeExternalAlternateIdentity(subscriberId, alternateId, getStaffData());
				if (ResultCode.SUCCESS == getResultCode(result)) {
					getAlternateIdUtils().removeAlternateIdFromCache(alternateId);
				}
			}
			addActionMessage(getText("subscriber.alternateid.remove.success"));
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			return Results.REDIRECT_ACTION.getValue();
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while removing alternate Identity for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			return Results.REDIRECT_ACTION.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while removing alternate Identity for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Add alternate Id failed: Reason " + e.getMessage());
			addActionError(getText(NOTE_REFER_LOGS));
		}
		return Results.ERROR.getValue();
	}

	@SkipValidation
	public String updateAlternateIdentity() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "updating alternate Identity");
		}
		String subscriberId = null;
		try {
			setActionChainUrl(SUBSCRIBER_VIEW + "#section5");
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			subscriberId = subscriber.getSubscriberIdentity();
			String oldAlternateIdentity = request.getParameter("oldAlternateIdentity");
			String updatedAlternateIdentity = request.getParameter("updatedAlternateIdentity");
			updatedAlternateIdentity = StringUtils.trim(updatedAlternateIdentity);
			int result = SubscriberDAO.getInstance().updateExternalAlternateIdentity(subscriberId, oldAlternateIdentity, updatedAlternateIdentity, getStaffData());
			if(ResultCode.SUCCESS == getResultCode(result)){
				getAlternateIdUtils().removeAlternateIdFromCache(oldAlternateIdentity);
			}
			addActionMessage(getText("subscriber.alternateid.update.success"));
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
			return Results.REDIRECT_ACTION.getValue();
		}catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error while updating Alternate Identity for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			return Results.REDIRECT_ACTION.getValue();
		}catch (Exception e) {
			getLogger().error(MODULE, "Error while updating Alternate Identity for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Error while updating Alternate Identity. Reason: " + e.getMessage());
			addActionError(getText(NOTE_REFER_LOGS));
		}
		return Results.ERROR.getValue();
	}



	@SkipValidation
	public String addMonetaryBalance(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called addMonetaryBalance");
		}
		String addMonetaryBalanceCurrency = "";
		setActionChainUrl(SUBSCRIBER_VIEW+"#section4");
		subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
		PolicyRepository policyRepository = getNVSMXPolicyRepository();
		if(policyRepository != null){
			ProductOffer productOffer = policyRepository.getProductOffer().byName(subscriber.getProductOffer());
			if(productOffer != null){
				addMonetaryBalanceCurrency = productOffer.getCurrency();
			}
		}

		String subscriberId = subscriber.getSubscriberIdentity();
		String startDate = request.getParameter("balanceStartDate");
		String expiryDate = request.getParameter("balanceExpiryDate");
		String serviceId = request.getParameter("service");
		String remark = request.getParameter("remark");
		String balanceString = request.getParameter("balance");

		if(Strings.isNullOrBlank(balanceString)){
			addActionError("Add monetary balance failed. Reason: Please specify amount");
			return Results.REDIRECT_ACTION.getValue();
		}

		Double balance = Double.parseDouble(balanceString);

		if(balance>CommonConstants.MONETARY_VALUE_LIMIT){
			addActionError("Add monetary balance failed. Reason: Amount should not be greater than "+CommonConstants.MAX_MONETARY_VALUE);
			return Results.REDIRECT_ACTION.getValue();
		}

		Integer updateAction = Integer.valueOf(request.getParameter(Attributes.UPDATE_ACTION));
		long startDateInMillis = 0;
		long endDateInMillis = 0;
		if(Strings.isNullOrEmpty(startDate) == false){
			DateConverter converter = new DateConverter();
			Timestamp timeStamp = (Timestamp) converter.convertFromString(null,new String[]{startDate} , Timestamp.class);
			startDateInMillis = timeStamp.getTime();
		}

		if(Strings.isNullOrEmpty(expiryDate) == false){
			DateConverter converter = new DateConverter();
			Timestamp timeStamp = (Timestamp) converter.convertFromString(null,new String[]{expiryDate} , Timestamp.class);
			endDateInMillis = timeStamp.getTime();
		}

		boolean isDefaultBalance = startDateInMillis==0 && endDateInMillis==0;

		if(startDateInMillis==0){
			startDateInMillis = System.currentTimeMillis();
		}

		if(endDateInMillis==0){
			endDateInMillis = CommonConstants.FUTURE_DATE;
		}

		if(endDateInMillis<startDateInMillis){
			addActionError("Add monetary balance failed. Reason: Balance start date is later then expiry date");
			return Results.REDIRECT_ACTION.getValue();
		}

		if(endDateInMillis<System.currentTimeMillis()){
			addActionError("Add monetary balance failed. Reason: Balance expiry date has already passed");
			return Results.REDIRECT_ACTION.getValue();
		}

		if(StringUtils.isBlank(serviceId)==true){
			serviceId = null;
		}

		try {
			MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),
					subscriber.getSubscriberIdentity(),
					serviceId,
					//0,
					balance,
					balance,
					0,
					0,
					0,
					startDateInMillis,
					endDateInMillis,
					addMonetaryBalanceCurrency,
					isDefaultBalance?MonetaryBalanceType.DEFAULT.name():MonetaryBalanceType.PROMOTIONAL.name(),
					System.currentTimeMillis(),
					0,
					null,
					null);

			SubscriberMonetaryBalance balanceFromDb = SubscriberDAO.getInstance().getMonetaryBalance(subscriber.getSubscriberIdentity(), com.elitecore.corenetvertex.util.util.Predicates.ALL_MONETARY_BALANCE);

			MonetaryBalance existingBalance;
			if(isDefaultBalance){
				//If start date and end date are not given then get default existing balance for the same service.
				existingBalance = SubscriberMonetaryBalanceUtil.getDefaultBalance(serviceId,balanceFromDb);
			} else {
				//Get matching balance with same start date, end date and service.
				existingBalance = SubscriberMonetaryBalanceUtil.getMatchingBalance(balanceFromDb,monetaryBalance);
			}

			if(existingBalance!=null){
				MonetaryBalance previousMonetaryBalance = existingBalance.copy();

				existingBalance.setInitialBalance(0);
				existingBalance.setAvailBalance(monetaryBalance.getInitialBalance());

				SubscriberDAO.getInstance().updateMonetaryBalance(subscriber.getSubscriberIdentity(),
						new SubscriberMonetaryBalanceWrapper(existingBalance, previousMonetaryBalance, subscriber,
								"addMonetaryBalance", ActionType.UPDATE.name(),UUID.randomUUID().toString()),
						remark, request.getRemoteAddr());
			} else {
				SubscriberDAO.getInstance().addMonetaryBalance(subscriber.getSubscriberIdentity(),
						new SubscriberMonetaryBalanceWrapper(monetaryBalance, null, subscriber,
								"addMonetaryBalance", ActionType.ADD.name(),UUID.randomUUID().toString()
						),
						remark, request.getRemoteAddr());
			}

			reAuth(subscriberId, updateAction);
			addActionMessage("Balance added successfully");
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while adding monetary balance for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Add monetary balance failed: Reason "+e.getMessage());
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}

		return Results.REDIRECT_ACTION.getValue();
	}

	@SkipValidation
	public String updateMonetaryBalance(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called updateMonetaryBalance");
		}
		setActionChainUrl(SUBSCRIBER_VIEW+"#section4");
		subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
		String subscriberId = subscriber.getSubscriberIdentity();
		String remark = request.getParameter("remark");
		String balanceString = request.getParameter("balance");
		String balanceId = request.getParameter("selectedBalanceId");
		String operation = request.getParameter("balanceOperation");

		Integer updateAction = Integer.valueOf(request.getParameter(Attributes.UPDATE_ACTION));

		try {
			if(Strings.isNullOrBlank(balanceString)){
				addActionError("Update monetary balance failed. Reason: Amount missing");
				return Results.REDIRECT_ACTION.getValue();
			}

			Double balance = Double.parseDouble(balanceString);
			MonetaryBalance monetaryBalance = SubscriberDAO.getInstance().getMonetaryBalance(subscriberId, balanceObj-> true).getBalanceById(balanceId);

			if(monetaryBalance == null){
				addActionError("Update monetary balance failed. Reason: Could not find monetary balance");
				return Results.REDIRECT_ACTION.getValue();
			}

			if (OperationType.DEBIT.getName().equals(operation)) {
				balance = -1*balance;
			}

			if (monetaryBalance.getAvailBalance() + balance > CommonConstants.MONETARY_VALUE_LIMIT) {
				addActionError("Update monetary balance failed. Reason: Balance after update should not exceed "+CommonConstants.MAX_MONETARY_VALUE);
				return Results.REDIRECT_ACTION.getValue();
			}

			MonetaryBalance previousMonetaryBalance = monetaryBalance.copy();

			monetaryBalance.setAvailBalance(balance);
			monetaryBalance.setInitialBalance(0);

			SubscriberDAO.getInstance().updateMonetaryBalance(subscriberId,
					new SubscriberMonetaryBalanceWrapper(monetaryBalance, previousMonetaryBalance, subscriber,
							"updateMonetaryBalance", ActionType.UPDATE.name(),UUID.randomUUID().toString()),
					remark,request.getRemoteAddr());
			reAuth(subscriberId, updateAction);
			addActionMessage("Balance updated successfully");
			subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);

		} catch (Exception e) {
			getLogger().error(MODULE, "Error while update monetary balance for subscriber: " + subscriberId + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Update monetary balance failed. Reason: "+e.getMessage());
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}

		return Results.REDIRECT_ACTION.getValue();
	}

	@SkipValidation
	public String unsubscribeAddOn() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called unsubscribeAddOn()");
		}
		String remark = request.getParameter("remark");
		String addonSubscriptionId = request.getParameter("addonSubscriptionId");
		Integer updateAction = Integer.valueOf(request.getParameter(Attributes.UPDATE_ACTION));
		setActionChainUrl(SUBSCRIBER_VIEW + "#section2");
		if (Strings.isNullOrEmpty(remark)) {
			getAndIncrementFailureCount(Attributes.CAPTCHA_FAILURE_COUNT);
			getAndIncrementFailureCount(Attributes.UNSUBSCRIBE_FAILURE_COUNT);
			addActionError("Please Specify Remark !");
			return Results.REDIRECT_ERROR.getValue();
		}
		if (Strings.isNullOrBlank(addonSubscriptionId)) {
			getLogger().error(MODULE, "Error in unsubscribing subscription. Reason: Addon Subscription Id not found");
			addActionError("SubscriptionId not found");
			return Results.REDIRECT_ERROR.getValue();
		}

		subscriber = (SPRInfo) session.get(Attributes.SUBSCRIBER);
		String subscriberIdentity = subscriber.getSubscriberIdentity();
		String pkgType = request.getParameter("unSubscriptionPkgType");

		try {
			if (PkgType.ADDON.val.equalsIgnoreCase(pkgType) || SubscriptionType.BOD.name().equalsIgnoreCase(pkgType)) {
				SubscriberDAO.getInstance().unsubscribeAddOn(subscriber, addonSubscriptionId, remark, getStaffData(), request.getRemoteAddr());
			} else {
				SubscriberDAO.getInstance().changeTopUpSubscription(subscriberIdentity, addonSubscriptionId, SubscriptionState.UNSUBSCRIBED
						, null, null, null,remark, getStaffData(), null, null);
			}
			addActionMessage("Subscription deleted successfully");
			reAuth(subscriberIdentity, updateAction);
			resetUnsubscribeFailureCount();
		} catch (UnauthorizedActionException e) {
			getLogger().error(MODULE, "Error in unsubscribing subscription. Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, e.getMessage());
			return Results.REDIRECT_ERROR.getValue();
		} catch (OperationFailedException e) {
			addActionError("Subscription deleted successfully");
			addActionError(e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in unsubscribing subscription. Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Subscription delete failed");
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}
		return Results.REDIRECT_ACTION.getValue();

	}

	private void resetUnsubscribeFailureCount() {
		session.put(Attributes.UNSUBSCRIBE_FAILURE_COUNT, 0);
	}

	private int getAndIncrementFailureCount(String failureCount ){
		Integer count = (Integer) session.get(failureCount);
		if(count == null || count ==  0){
			count = 1;
		}else{
			count += 1;
		}
		session.put(failureCount, count);
		return count;
	}

	@SkipValidation
	public String addTestSubscriber() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called addTestSubscriber()");
		}
		String subscriberIdentity = request.getParameter(Attributes.SUBSCRIBER_IDENTITY);
		setActionChainUrl(SEARCH_TEST_SUBSCRIBER);
		if(Strings.isNullOrBlank(subscriberIdentity)) {
			request.setAttribute(Attributes.ACTION, actionChainUrl);
			getLogger().error(MODULE, "Error while creating test subscriber. Reason: Subscriber Identity missing");
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}

		try {
			SubscriberDAO.getInstance().addTestSubscriber(subscriberIdentity, getStaffData());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in adding test subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Test Subscriber add failed");
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}
		addActionMessage("Test subscriber added successfully");
		return Results.REDIRECT_ACTION.getValue();
	}

	@SkipValidation
	public String searchTestSubscriber() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called searchTestSubscriber()");
		}

		Set<Subscriber> testSubscribersSet = new HashSet<>();

		try {
			Iterator<String> testSubscribers = SubscriberDAO.getInstance().getTestSubscriberIterator();

			int i = 0;

			while (testSubscribers.hasNext()) {
				Subscriber subscriberData = new Subscriber(i, testSubscribers.next());
				testSubscribersSet.add(subscriberData);
				i++;
			}

			Gson gson = GsonFactory.defaultInstance();
			JsonArray testSubscribersJsonArray = gson.toJsonTree(testSubscribersSet,new TypeToken<List<Subscriber>>() {}.getType()).getAsJsonArray();
			request.setAttribute(Attributes.TEST_SUBSCRIBERS, testSubscribersJsonArray);

		} catch (Exception e) {
			getLogger().error(MODULE, "Error in searching test subscribers. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Test Subscriber " + getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
		}

		return Results.SEARCH_TEST_SUBCRIBER.getValue();
	}

	@SkipValidation
	public String testSubscriberSearchCriteria() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called testSubscriberSearchCriteria()");
		}

		try {

			if(Strings.isNullOrBlank(subscriber.getSubscriberIdentity())){
				return searchTestSubscriber();
			}

			Iterator<String> testSubscribers = SubscriberDAO.getInstance().getTestSubscriberIterator();
			Set<Subscriber> testSubscribersSet = new HashSet<>();

			int i = 0;

			while (testSubscribers.hasNext()) {
				String subscriberIdentity = testSubscribers.next();
				if(subscriberIdentity.toLowerCase().contains(subscriber.getSubscriberIdentity().toLowerCase())){
					Subscriber subscriberData = new Subscriber(i, subscriberIdentity);
					testSubscribersSet.add(subscriberData);
					i++;
				}
			}

			Gson gson = GsonFactory.defaultInstance();
			JsonArray testSubscribersJsonArray = gson.toJsonTree(testSubscribersSet,new TypeToken<List<Subscriber>>() {}.getType()).getAsJsonArray();
			request.setAttribute(Attributes.TEST_SUBSCRIBERS, testSubscribersJsonArray);
		} catch (Exception e) {
			setActionChainUrl(SEARCH_TEST_SUBSCRIBER);
			getLogger().error(MODULE, "Error in searching test subscribers. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Test Subscriber " + getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}

		return Results.SEARCH_TEST_SUBCRIBER.getValue();
	}

	@SkipValidation
	public String removeTestSubscriber() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called removeTestSubscriber()");
		}
		String subscriberId = parseQueryStringAndGetSubscriberIdentity() ;
		setActionChainUrl(SEARCH_TEST_SUBSCRIBER);
		if (Strings.isNullOrBlank(subscriberId)) {
			getLogger().error(MODULE, "Error in removing test subscriber. Reason: Subscriber Identity missing.");
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}

		try {
			SubscriberDAO.getInstance().removeTestSubscriber(subscriberId,getStaffData());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in removing test subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			return Results.REDIRECT_ERROR.getValue();
		}
		addActionMessage("Test Subscriber removed successfully");
		return Results.REDIRECT_ACTION.getValue();
	}

	@SkipValidation
	public String removeTestSubscribers(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called removeTestSubscribers()");
		}

		String[] subscriberIds = request.getParameterValues(Attributes.IDS);
		setActionChainUrl(SEARCH_TEST_SUBSCRIBER);

		if(subscriberIds == null){
			request.setAttribute(Attributes.ACTION, actionChainUrl);
			getLogger().error(MODULE, "Error in removing test subscriber. Reason: Subscriber Identity missing.");
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}

		try {
			SubscriberDAO.getInstance().removeTestSubscriber(Arrays.asList(subscriberIds),getStaffData());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in removing test subscribers. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}
		addActionMessage("Test Subscribers removed successfully");
		return Results.REDIRECT_ACTION.getValue();
	}

	@SkipValidation
	public String searchDeletedSubscriber(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called searchDeletedSubscriber()");
		}
		String subscriberIdentity = subscriber.getSubscriberIdentity();
		if(Strings.isNullOrBlank(subscriberIdentity)==false) {
			criteria.put(Attributes.SUBSCRIBER_IDENTITY, subscriber.getSubscriberIdentity());
		}
		String json = GsonFactory.defaultInstance().toJson(criteria);
		request.setAttribute(Attributes.CRITERIA, json);
		return Results.LIST_DELETED.getValue();
	}

	@SkipValidation
	public String purgeSubscriber() {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called purgeSubscriber()");
		}
		String subscriberIdentity = parseQueryStringAndGetSubscriberIdentity();
		setActionChainUrl(SEARCH_DELETED_SUBSCRIBER);
		if(Strings.isNullOrBlank(subscriberIdentity)) {
			getLogger().error(MODULE, "Error while purge subscriber. Reason: Subscriber Identity missing");
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}

		try {
			SubscriberDAO.getInstance().purgeSubscriber(subscriberIdentity, getStaffData(), request.getRemoteAddr());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in purge subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionMessage(Discriminators.SUBSCRIBER + " purged failed");
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}
		addActionMessage(Discriminators.SUBSCRIBER + " purged successfully");
		return Results.REDIRECT_ACTION.getValue();
	}

	@SkipValidation
	public String purgeSubscribers() {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called purgeSubscribers()");
		}
		String[] subscriberIds = request.getParameterValues("ids");
		setActionChainUrl(SEARCH_DELETED_SUBSCRIBER);
		if(subscriberIds == null || subscriberIds.length == 0) {
			getLogger().error(MODULE, "Error in purge subscribers. Reason: Subscriber Identity missing.");
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}

		Map<String, Integer> purgeSubscriberResults;
		try {
			request.setAttribute(Attributes.ACTION, actionChainUrl);
			purgeSubscriberResults = SubscriberDAO.getInstance().purgeSubscriber(Arrays.asList(subscriberIds), getStaffData(), request.getRemoteAddr());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in purge subscribers. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Subscribers purged-all failed");
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}

		addActionMessage("Subscribers purged successfully");
		addActionMessage(generateResultMessage(purgeSubscriberResults));
		addActionMessage("Refer logs for more details");

		return Results.REDIRECT_ACTION.getValue();
	}

	private String generateResultMessage(Map<String, Integer> purgeSubscriberResults) {
		StringBuilder builder = new StringBuilder();

		for (Entry<String, Integer> entry:  purgeSubscriberResults.entrySet()) {
			builder.append("Subscriber(").append(entry.getKey()).append(") = ").append(entry.getValue() > 0 ? "SUCCESS" : "FAIL").append("\n");
		}

		return builder.toString();
	}

	@SkipValidation
	public String purgeAll() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called purgeSubscribers()");
		}
		setActionChainUrl(SEARCH_DELETED_SUBSCRIBER);
		Map<String, Integer> purgeAllSubscribersResults;
		try {
			purgeAllSubscribersResults = SubscriberDAO.getInstance().purgeAllSubscribers(getStaffData(), request.getRemoteAddr());
			if(purgeAllSubscribersResults.size() == 0){
				return Results.REDIRECT_ACTION.getValue();
			}
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in purge-all subscribers. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Subscribers purged-all failed");
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}

		addActionMessage("Subscribers purged successfully");
		addActionMessage(generateResultMessage(purgeAllSubscribersResults));
		addActionMessage("Refer logs for more details");

		return Results.REDIRECT_ACTION.getValue();

	}

	@SkipValidation
	public String restoreSubscriber() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called restoreSubscriber()");
		}
		String subscriberIdentity = parseQueryStringAndGetSubscriberIdentity();
		setActionChainUrl(SEARCH_DELETED_SUBSCRIBER);

		if(Strings.isNullOrBlank(subscriberIdentity)) {
			request.setAttribute(Attributes.ACTION, actionChainUrl);
			getLogger().error(MODULE, "Error while restore subscriber. Reason: Subscriber Identity missing");
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}

		try {
			SubscriberDAO.getInstance().restoreSubscriber(subscriberIdentity, getStaffData(), request.getRemoteAddr());
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Error in restore subscriber. Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			addActionError(Discriminators.SUBSCRIBER+" restore failed");
			return Results.REDIRECT_ERROR.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in restore subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.SUBSCRIBER+" restore failed");
			request.setAttribute(Attributes.ACTION, actionChainUrl);
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}
		addActionMessage("Subscriber restored successfully with status INACTIVE");
		return Results.REDIRECT_ACTION.getValue();
	}

	@SkipValidation
	public String restoreSubscribers() {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called restoreSubscribers()");
		}
		String[] subscriberIds = request.getParameterValues(Attributes.IDS);
		setActionChainUrl(SEARCH_DELETED_SUBSCRIBER);
		if(subscriberIds == null || subscriberIds.length == 0) {
			getLogger().error(MODULE, "Error in restore subscriber. Reason: Subscriber Identity missing.");
			addActionError(Discriminators.SUBSCRIBER+" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}

		Map<String, Integer> restoreSubscriberResult;
		try {
			restoreSubscriberResult = SubscriberDAO.getInstance().restoreSubscriber(Arrays.asList(subscriberIds), getStaffData());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in restore subscriber. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Subscribers restore failed");
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}

		addActionMessage("Subscribers restored successfully with status INACTIVE");
		addActionMessage(generateResultMessage(restoreSubscriberResult));
		addActionMessage("Refer logs for more details");

		return Results.REDIRECT_ACTION.getValue();
	}

	@SkipValidation
	public String restoreAll(){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called restoreAll()");
		}

		Map<String, Integer> restoreSubscriberResult;
		setActionChainUrl(SEARCH_DELETED_SUBSCRIBER);
		try {
			restoreSubscriberResult = SubscriberDAO.getInstance().restoreAllSubscribers(getStaffData(), request.getRemoteAddr());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in restore-all subscribers. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError("Subscribers restore-all failed");
			addActionError(getText(NOTE_REFER_LOGS));
			return Results.REDIRECT_ERROR.getValue();
		}

		addActionMessage("Subscribers restored successfully with status INACTIVE");
		addActionMessage(generateResultMessage(restoreSubscriberResult));
		addActionMessage("Refer logs for more details");

		return Results.REDIRECT_ACTION.getValue();
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public SPRInfo getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(SPRInfo subscriber) {
		this.subscriber = subscriber;
	}

	public List<BasePackage> getPackages() {
		packages = getNVSMXPolicyRepository().getActiveAllBasePkgDatas();
		filterDataPackages(packages);
		return packages;

	}

	public void setPackages(List<BasePackage> packages) {
		this.packages = packages;
	}

	public List<Package> getDataPackages() {
		return dataPackages;
	}

	public void setDataPackages(List<Package> dataPackages) {
		this.dataPackages = dataPackages;
	}


	public List<RnCPackage> getFilteredRncPackages() {
		return filteredRncPackages;
	}

	public void setFilteredRncPackages(List<RnCPackage> filteredRncPackages) {
		this.filteredRncPackages = filteredRncPackages;
	}

	public List<BasePackage> getLivePackages() {
		livePackages = getNVSMXPolicyRepository().getActiveLiveBasePkgDatas();
		filterDataPackages(livePackages);
		return livePackages;
	}

	public List<ProductOffer> getProductOffers() {
		return productOffers;
	}

	public void setLiveProductOffers(List<ProductOffer> liveProductOffers) {
		this.liveProductOffers = liveProductOffers;
	}

	public List<ProductOffer> getLiveProductOffers(){
		return liveProductOffers;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public void createBaseProductOfferList(String currency){
		StaffGroupProductOfferPredicate staffGroupProductOfferPredicate = StaffGroupProductOfferPredicate.create(getStaffBelongingGroupsIds());
		ProductOfferCurrencyPredicate productOfferCurrencyPredicate=ProductOfferCurrencyPredicate.create(currency);

		List<ProductOffer> productOfferList = getNVSMXPolicyRepository().
				getProductOffer().
				base().
				active().all().
				stream().
				filter(offerData -> offerData.getPolicyStatus() != PolicyStatus.FAILURE).
				filter(staffGroupProductOfferPredicate::apply).
				filter(productOfferCurrencyPredicate::test).
				collect(Collectors.toList());
		setProductOffers(productOfferList);

		List<ProductOffer> liveProductOfferList = getNVSMXPolicyRepository().
				getProductOffer().
				base().
				active().
				live().
				all().
				stream().
				filter(offerData -> offerData.getPolicyStatus() != PolicyStatus.FAILURE).
				filter(staffGroupProductOfferPredicate::apply).
				filter(productOfferCurrencyPredicate::test).
				collect(Collectors.toList());

		setLiveProductOffers(liveProductOfferList);
	}

	public void setProductOffers(List<ProductOffer> productOffers) {
		this.productOffers = productOffers;
	}

	public void setLivePackages(List<BasePackage> livePackages) {
		this.livePackages = livePackages;
	}

	public List<IMSPackage> getImsPackages() {
		imsPackages = getNVSMXPolicyRepository().getActiveAllImsPkgDatas();
		filterIMSPackages(imsPackages);
		return imsPackages ;
	}

	public void setImsPackages(List<IMSPackage> imsPackages) {
		this.imsPackages = imsPackages;
	}

	public List<IMSPackage> getImsLivePackages() {
		imsLivePackages = getNVSMXPolicyRepository().getActiveLiveImsPkgDatas();
		filterIMSPackages(imsLivePackages);
		return imsLivePackages;
	}

	public void setImsLivePackages(List<IMSPackage> imsLivePackages) {
		this.imsLivePackages = imsLivePackages;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;

	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response =  response;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}


	private void filterIMSPackages(List<? extends IMSPackage> imsPackageList) {
		if(Collectionz.isNullOrEmpty(imsPackageList)){
			return ;
		}
		StaffGroupIMSPackagePredicate staffGroupIMSPackagePredicate = StaffGroupIMSPackagePredicate.create(getStaffBelongingGroupsIds());
		Collectionz.filter(imsPackageList, Predicates.and(FailedIMSPackageFilter.getInstance(),staffGroupIMSPackagePredicate));
	}

	private void filterDataPackages(List<? extends Package> pkgList){
		if(Collectionz.isNullOrEmpty(pkgList)){
			return ;
		}
		StaffGroupUserPackagePredicate staffGroupUserPackagePredicate = StaffGroupUserPackagePredicate.create(getStaffBelongingGroupsIds());
		Collectionz.filter(pkgList, Predicates.and( FailedDataPackageFilter.getInstance(),staffGroupUserPackagePredicate));
	}

	public List<IMSPackage> getImsPackage() {
		return imsPackage;
	}

	public void setImsPackage(List<IMSPackage> imsPackages) {
		this.imsPackage = imsPackages;
	}


	public String getCountryNames() {
		return countryNames;
	}

	public void setCountryNames(String countryNames) {
		this.countryNames = countryNames;
	}

	public JsonArray getSubscriptionInformationJsonArray() {
		return subscriptionInformationJsonArray;
	}

	public void setSubscriptionInformationJsonArray(JsonArray subscriptionInformationJsonArray) {
		this.subscriptionInformationJsonArray = subscriptionInformationJsonArray;
	}

	public JsonArray getMonetaryBalancesJsonArray() {
		return monetaryBalancesJsonArray;
	}

	public void setMonetaryBalance(JsonArray monetaryBalancesJsonArray)
	{
		this.monetaryBalancesJsonArray = monetaryBalancesJsonArray;
	}


	private class Subscriber {

		Object key;
		Object value;

		private Subscriber(Object key, Object value) {
			this.key = key;
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

		public Object getKey() {
			return key;
		}

	}

	private static class SubscriptionPkgGroupPredicate implements Predicate<SubscriptionInformation> {

		private List<String> staffBelongingGroupIds;

		public SubscriptionPkgGroupPredicate(List<String> staffBelongingGroupIds) {
			this.staffBelongingGroupIds = staffBelongingGroupIds;
		}

		@Override
		public boolean apply(SubscriptionInformation subscriptionInformation) {
			if (Collectionz.isNullOrEmpty(subscriptionInformation.getGroupIds())) {
				return true;
			}
			for (String groupId : subscriptionInformation.getGroupIds()) {
				if (staffBelongingGroupIds.contains(groupId)) {
					return true;
				}
			}
			return false;
		}

	}

	public List<QuotaTopUp> getAllTopUps() {
		return allTopUps;
	}

	public void setAllTopUps(List<QuotaTopUp> allTopUps) {
		this.allTopUps = allTopUps;
	}

	public List<QuotaTopUp> getLiveTopUps() {
		return liveTopUps;
	}

	public void setLiveTopUps(List<QuotaTopUp> liveTopUps) {
		this.liveTopUps = liveTopUps;
	}


	public String getAllTopUpsString() {
		return allTopUpsString;
	}

	public void setAllTopUpsString(String allTopUpsString) {
		this.allTopUpsString = allTopUpsString;
	}

	public String getLiveTopUpsString() {
		return liveTopUpsString;
	}

	public void setLiveTopUpsString(String liveTopUpsString) {
		this.liveTopUpsString = liveTopUpsString;
	}

	public String getLiveAddOnString() {
		return liveAddOnString;
	}

	public void setLiveAddOnString(String liveAddOnString) {
		this.liveAddOnString = liveAddOnString;
	}

	public String getAllAddOnString() {
		return allAddOnString;
	}

	public void setAllAddOnString(String allAddOnString) {
		this.allAddOnString = allAddOnString;
	}

	public JsonArray getSubscriberAlternateIds() {
		return subscriberAlternateIds;
	}

	public void setSubscriberAlternateIds(JsonArray subscriberAlternateIds) {
		this.subscriberAlternateIds = subscriberAlternateIds;
	}
	private AlternateIdOperationUtils getAlternateIdUtils(){
		return DefaultNVSMXContext.getContext().getAlternateIdOperationUtils();
	}
	private ResultCode getResultCode(int result) {
		if(result > 0 ){
			return  ResultCode.SUCCESS;
		}else{
			return ResultCode.NOT_FOUND;
		}
	}

	private List<QuotaTopUp> getQuotaTopUpByBasePackage(List<QuotaTopUp> quotaTopUps, BasePackage basePackage){
		List<String> basePccProfileNames = basePackage.getQoSProfiles().stream().map(QoSProfile::getName).collect(Collectors.toList());
		ApplicableQuotaTopUpByBasePackagePredicate applicableQuotaTopUpPredicate = ApplicableQuotaTopUpByBasePackagePredicate.create(basePccProfileNames);
		return   quotaTopUps.stream()
				.filter(applicableQuotaTopUpPredicate)
				.filter(topUp -> PolicyStatus.FAILURE != topUp.getStatus())
				.collect(Collectors.toList());
	}



	public void setAllBodDataString(String allBodDataString) { this.allBodDataString = allBodDataString; }

	public String getAllBodDataString() { return allBodDataString; }

	public String getLiveBodDataString() { return liveBodDataString; }

	public void setLiveBodDataString(String liveBodDataString) { this.liveBodDataString = liveBodDataString; }

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}