package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.EntityType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.util.FailedDataPackageFilter;
import com.elitecore.nvsmx.system.util.FailedIMSPackageFilter;
import com.elitecore.nvsmx.system.util.FailedQuotaTopUpFilter;
import com.elitecore.nvsmx.ws.reload.blmanager.ReloadPolicyBlManager;
import com.elitecore.nvsmx.ws.reload.response.ReloadPolicyResponse;
import com.elitecore.nvsmx.ws.util.ProductOfferCurrencyPredicate;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class PkgDataReloadCTRL extends ActionSupport implements ServletRequestAware {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = PkgDataReloadCTRL.class.getSimpleName();
	public static final java.util.function.Predicate<RnCPackage> RNC_TEST_PACKAGE_NON_FAILURE_STATUS_PREDICATE = rnCPackage -> rnCPackage.getPackageMode().getOrder() >= PkgMode.TEST.getOrder() && rnCPackage.getPolicyStatus() != PolicyStatus.FAILURE;
	private HttpServletRequest request;

	public Map<String,List<PolicyDetail>> pkgTypeWisePolicy = Arrays.stream(EntityType.values())
			.collect(Collectors.toMap(EntityType::getValue, k -> new ArrayList<>(), (e1, e2) -> e2, LinkedHashMap::new));
	public Map<String,String> pkgTypeWisePolicyJsonMap = Maps.newLinkedHashMap();
	public Map<String,PolicyCacheDetail> pkgTypeWisePolicyStatistics = Maps.newLinkedHashMap();

	private List<BasePackage> livePackages = Collectionz.newArrayList();
	private List<BasePackage> dataPackages = Collectionz.newArrayList();
	private List<ProductOffer> productOffers = Collectionz.newArrayList();
	private List<ProductOffer> liveProductOffers = new ArrayList<>();
	private List<SubscriptionPackage> liveAddOns = Collectionz.newArrayList();
	private List<SubscriptionPackage> allAddOns= Collectionz.newArrayList();
	private List<ProductOffer> productOfferAddOns= Collectionz.newArrayList();
	private List<ProductOffer> liveProductOffersAddons = new ArrayList<>();
	private List<IMSPackage> imsLivePackages = Collectionz.newArrayList();
	private List<IMSPackage> imsPackages = Collectionz.newArrayList();
	private List<RnCPackage> rncPackages = Collectionz.newArrayList();
	private List<RnCPackage> rncLivePackages = Collectionz.newArrayList();
	private List<PolicyDetail> policyDetail = Collectionz.newArrayList();
	private List<QuotaTopUp> allTopUps = new ArrayList<>();
	private List<QuotaTopUp> liveTopUps = new ArrayList<>();
	private List<BoDPackage> allBodData = new ArrayList<>();
	private List<BoDPackage> liveBodData = new ArrayList<>();
	private PolicyCacheDetail policyCacheDetail;
	private String unreachableInstances;
	private com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail reloadPolicyCacheDetail;
	private static final ReloadPolicyBlManager reloadPolicyBlManager = new ReloadPolicyBlManager(RMIGroupManager.getInstance(), EndPointManager.getInstance(),DefaultNVSMXContext.getContext().getPolicyRepository());
	private String currency;

	public com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail getReloadPolicyCacheDetail() {
		return reloadPolicyCacheDetail;
	}

	public void setReloadPolicyCacheDetail(com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail reloadPolicyCacheDetail) {
		this.reloadPolicyCacheDetail = reloadPolicyCacheDetail;
	}

	public void setPkgTypeWisePolicy(Map<String, List<PolicyDetail>> pkgTypeWisePolicy) {
		this.pkgTypeWisePolicy = pkgTypeWisePolicy;
	}

	public Map<String, List<PolicyDetail>> getPkgTypeWisePolicy() {
		return pkgTypeWisePolicy;
	}

	public Map<String, String> getPkgTypeWisePolicyJsonMap() {
		return pkgTypeWisePolicyJsonMap;
	}

	public void setPkgTypeWisePolicyJsonMap(Map<String, String> pkgTypeWisePolicyJsonMap) {
		this.pkgTypeWisePolicyJsonMap = pkgTypeWisePolicyJsonMap;
	}

	public Map<String, PolicyCacheDetail> getPkgTypeWisePolicyStatistics() {
		return pkgTypeWisePolicyStatistics;
	}

	public void setPkgTypeWisePolicyStatistics(Map<String, PolicyCacheDetail> pkgTypeWisePolicyStatistics) {
		this.pkgTypeWisePolicyStatistics = pkgTypeWisePolicyStatistics;
	}

	public String reloadProductOffers() {
		String staffBelongingGroupIds = request.getParameter(Attributes.GROUPIDS);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading data packages");
		}

		if(StringUtils.isEmpty(staffBelongingGroupIds)) {
			staffBelongingGroupIds = getStaffBelongingGroups();
		}

		if (Strings.isNullOrBlank(staffBelongingGroupIds)) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Reload Package operation can't be performed.Reason: User doesn't belong to any Group");
			}
			return Results.SUCCESS.getValue();
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading Package for staff groups :" + staffBelongingGroupIds);
		}

		reloadPolicyBlManager.reloadPoliciesByGroups(staffBelongingGroupIds);

		createBaseProductOfferList();

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "All the data packages reloaded successfully");
		}

		return Results.SUCCESS.getValue();
	}


	public String reloadPackages() {
		String staffBelongingGroupIds = request.getParameter(Attributes.GROUPIDS);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading data packages");
		}

		if(StringUtils.isEmpty(staffBelongingGroupIds)) {
			staffBelongingGroupIds = getStaffBelongingGroups();
		}

		if (Strings.isNullOrBlank(staffBelongingGroupIds)) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Reload Package operation can't be performed.Reason: User doesn't belong to any Group");
			}
			return Results.SUCCESS.getValue();
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading Package for staff groups :" + staffBelongingGroupIds);
		}

		reloadPolicyBlManager.reloadPoliciesByGroups(staffBelongingGroupIds);
		StaffGroupUserPackagePredicate staffGroupUserPackagePredicate = StaffGroupUserPackagePredicate.create(getStaffBelongingGroupsIds());

		StaffGroupUserPackagePredicate userPackagePredicateForGroupsFromRequest = StaffGroupUserPackagePredicate.create(getGroupIdsFromRequest());

		List<BasePackage> activeLivePkgs = getNVSMXPolicyRepository().getActiveLiveBasePkgDatas();

		if (Collectionz.isNullOrEmpty(activeLivePkgs) == false) {
			List<BasePackage> filteredLivePackageList = activeLivePkgs.stream().filter(livePkg -> livePkg.getStatus() != PolicyStatus.FAILURE).
					filter(livePkg -> staffGroupUserPackagePredicate.apply(livePkg))
					.filter(livePkg -> userPackagePredicateForGroupsFromRequest.apply(livePkg)).collect(Collectors.toList());
			if (Collectionz.isNullOrEmpty(filteredLivePackageList) == false) {
				setLivePackages(filteredLivePackageList);
			}
		}

		List<BasePackage> activeAllPkgs = getNVSMXPolicyRepository().getActiveAllBasePkgDatas();
		if (Collectionz.isNullOrEmpty(activeAllPkgs) == false) {
			List<BasePackage> filteredAllPackageList = activeAllPkgs.stream().filter(activePkg -> activePkg.getStatus() != PolicyStatus.FAILURE).
					filter(activePkg -> staffGroupUserPackagePredicate.apply(activePkg))
					.filter(activePkg -> userPackagePredicateForGroupsFromRequest.apply(activePkg)).collect(Collectors.toList());
			if (Collectionz.isNullOrEmpty(filteredAllPackageList) == false) {
				setDataPackages(filteredAllPackageList);
			}

		}

		createBaseProductOfferList();

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "All the data packages reloaded successfully");
		}

		return Results.SUCCESS.getValue();
	}



	public void createBaseProductOfferList(){
		StaffGroupProductOfferPredicate staffGroupProductOfferPredicate = StaffGroupProductOfferPredicate.create(getStaffBelongingGroupsIds());
		ProductOfferCurrencyPredicate productOfferCurrencyPredicate=ProductOfferCurrencyPredicate.create(request.getParameter(Attributes.CURRENCY));

		List<ProductOffer> productOffers = getNVSMXPolicyRepository().
				getProductOffer().
				base().
				active().all().
				stream().
				filter(offerData -> offerData.getPolicyStatus() != PolicyStatus.FAILURE).
				filter(offerData -> staffGroupProductOfferPredicate.apply(offerData)).
				filter(offerData -> productOfferCurrencyPredicate.test(offerData)).
				collect(Collectors.toList());
		setProductOffers(productOffers);

		List<ProductOffer> liveProductOfferList = getNVSMXPolicyRepository().
				getProductOffer().
				base().
				active().
				live().
				all().
				stream().
				filter(offerData -> offerData.getPolicyStatus() != PolicyStatus.FAILURE).
				filter(offerData -> staffGroupProductOfferPredicate.apply(offerData)).filter(offerData -> productOfferCurrencyPredicate.test(offerData)).collect(Collectors.toList());

		setLiveProductOffers(liveProductOfferList);
	}


	public String reloadRnCPackages() {

		String staffBelongingGroupIds = request.getParameter(Attributes.GROUPIDS);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading RnC packages");
		}

		if(StringUtils.isEmpty(staffBelongingGroupIds)) {
			staffBelongingGroupIds = getStaffBelongingGroups();
		}

		if (Strings.isNullOrBlank(staffBelongingGroupIds)) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Reload RnC Package operation can't be performed.Reason: User doesn't belong to any Group");
			}
			return Results.SUCCESS.getValue();
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading RnC Package for staff groups :" + staffBelongingGroupIds);
		}

		reloadPolicyBlManager.reloadPoliciesByGroups(staffBelongingGroupIds);
		java.util.function.Predicate<RnCPackage> staffGroupRnCPackagePredicate = StaffGroupRnCPackagePredicate.create(getStaffBelongingGroupsIds());
		java.util.function.Predicate<RnCPackage> rnCPackagePredicateForRequestGroups = StaffGroupRnCPackagePredicate.create(getGroupIdsFromRequest());
		List<RnCPackage> activeLivePkgs = getNVSMXPolicyRepository().getRnCPackage().all();
		if (Collectionz.isNullOrEmpty(activeLivePkgs) == false) {

			activeLivePkgs = activeLivePkgs.stream().filter((pkg)-> (pkg.getPackageMode() == PkgMode.LIVE
					&& pkg.getPolicyStatus() != PolicyStatus.FAILURE ) || (pkg.getPackageMode() == PkgMode.LIVE2
					&& pkg.getPolicyStatus() != PolicyStatus.FAILURE ) ).collect(Collectors.toList());

			activeLivePkgs = activeLivePkgs.stream().filter(staffGroupRnCPackagePredicate).filter(rnCPackagePredicateForRequestGroups).collect(Collectors.toList());
			if (Collectionz.isNullOrEmpty(activeLivePkgs) == false) {
				setRncLivePackages(activeLivePkgs);
			}
		}

		List<RnCPackage> activeAllPkgs = getNVSMXPolicyRepository().getRnCPackage().all()
				.stream().filter(RNC_TEST_PACKAGE_NON_FAILURE_STATUS_PREDICATE)
				.filter(staffGroupRnCPackagePredicate).filter(rnCPackagePredicateForRequestGroups).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(activeAllPkgs)) {
			setRncPackages(activeAllPkgs);
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "All the RnC packages reloaded successfully");
		}

		return Results.SUCCESS.getValue();
	}

	private PolicyRepository getNVSMXPolicyRepository() {
		return DefaultNVSMXContext.getContext().getPolicyRepository();
	}

	public String reloadAddOns(){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading subscription packages");
		}
		final String staffBelongingGroupIds = getStaffBelongingGroups();
		String syInterface = request.getParameter("syInterface");
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading AddOns");
		}


		if (Strings.isNullOrBlank(staffBelongingGroupIds)) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Reload operation can't be performed.Reason: User doesn't belong to any Group");
			}
			return Results.SUCCESS.getValue();
		}
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading AddOn for staff groups: " + staffBelongingGroupIds);
		}

		reloadPolicyBlManager.reloadPoliciesByGroups(staffBelongingGroupIds);

		List<String> staffGroups = CommonConstants.COMMA_SPLITTER.split(staffBelongingGroupIds);
		StaffGroupUserPackagePredicate staffGroupUserPackagePredicate = StaffGroupUserPackagePredicate.create(getStaffBelongingGroupsIds());

		StaffGroupUserPackagePredicate userPackagePredicateForRequestGroups = StaffGroupUserPackagePredicate.create(getGroupIdsFromRequest());

		List<AddOn> allAddOns = getNVSMXPolicyRepository().getActiveAllAddOnPkgDatas(Boolean.valueOf(syInterface));

		if(Collectionz.isNullOrEmpty(allAddOns) == false){
			Collectionz.filter(allAddOns, Predicates.and(FailedDataPackageFilter.getInstance(), staffGroupUserPackagePredicate));
			Collectionz.filter(allAddOns, userPackagePredicateForRequestGroups);
			if (Collectionz.isNullOrEmpty(allAddOns) == false) {
				this.allAddOns.addAll(allAddOns);
			}
		}
		List<AddOn> liveAddOns = getNVSMXPolicyRepository().getActiveLiveAddOnDatas(Boolean.valueOf(syInterface));
		if(Collectionz.isNullOrEmpty(liveAddOns) == false) {
			Collectionz.filter(liveAddOns, Predicates.and(FailedDataPackageFilter.getInstance(), staffGroupUserPackagePredicate));
			Collectionz.filter(liveAddOns, userPackagePredicateForRequestGroups);
			if (Collectionz.isNullOrEmpty(liveAddOns) == false) {
				this.liveAddOns.addAll(liveAddOns);
			}
		}

		reloadBodData();
		reloadQuotaTopUps(null);

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Subscription packages reloaded successfully");
		}

		return Results.SUCCESS.getValue();
	}


	private String getStaffBelongingGroups() {
		return (String)request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
	}

	public String reloadProductOfferAddOns(){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading subscription packages");
		}

		String staffBelongingGroupIds = request.getParameter(Attributes.GROUPIDS);

		String syInterface = request.getParameter("syInterface");
		String productOfferName = request.getParameter("dataPackage");
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading Product OfferAddOns");
		}
		ProductOffer getProductOffer = getNVSMXPolicyRepository().getProductOffer().byName(productOfferName);
		if(getProductOffer != null){
			currency = getProductOffer.getCurrency();
		}
		if(StringUtils.isEmpty(staffBelongingGroupIds)) {
			staffBelongingGroupIds = getStaffBelongingGroups();
		}

		if (Strings.isNullOrBlank(staffBelongingGroupIds)) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Reload operation can't be performed.Reason: User doesn't belong to any Group");
			}
			return Results.SUCCESS.getValue();
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading AddOn for groups: " + staffBelongingGroupIds);
		}

		reloadPolicyBlManager.reloadPoliciesByGroups(staffBelongingGroupIds);

		Predicate staffGroupProductOfferPredicate = StaffGroupProductOfferPredicate.create(getStaffBelongingGroupsIds());
		Predicate productOfferPredicateForRequestGroups = StaffGroupProductOfferPredicate.create(getGroupIdsFromRequest());

		List<ProductOffer> allAddOns = getNVSMXPolicyRepository().getProductOffer()
				.addOn().active().all()
				.stream().filter(productOffer -> staffGroupProductOfferPredicate.apply(productOffer))
				.filter(productOffer -> productOfferPredicateForRequestGroups.apply(productOffer))
				.filter(productOffer -> productOffer.getCurrency().equalsIgnoreCase(currency))
				.collect(Collectors.toList());
		setProductOfferAddOns(allAddOns);


		List<ProductOffer> liveProductOfferAddons = getNVSMXPolicyRepository().getProductOffer()
				.addOn().active().live().all()
				.stream().filter(productOffer -> staffGroupProductOfferPredicate.apply(productOffer))
				.filter(productOffer -> productOfferPredicateForRequestGroups.apply(productOffer))
				.filter(productOffer -> productOffer.getCurrency().equalsIgnoreCase(currency))
				.collect(Collectors.toList());
		setLiveProductOffersAddons(liveProductOfferAddons);

		reloadQuotaTopUps(productOfferName);
		reloadBodData();

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Subscription packages reloaded successfully");
		}

		return Results.SUCCESS.getValue();
	}

	public void reloadBodData(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading BODs");
		}

		List<BoDPackage> allBodPackages = getNVSMXPolicyRepository().getBoDPackage()
				.all().stream()
				.filter(StaffGroupBoDPackagePredicate.create(getStaffBelongingGroupsIds()))
				.filter(bodPackage -> bodPackage.getPolicyStatus() != PolicyStatus.FAILURE)
				.filter(bodPackage -> bodPackage.getPkgStatus() == PkgStatus.ACTIVE)
				.collect(Collectors.toList());
		setAllBodData(allBodPackages);


		List<BoDPackage> liveBodPackages = getNVSMXPolicyRepository().getBoDPackage()
				.all().stream()
				.filter(StaffGroupBoDPackagePredicate.create(getStaffBelongingGroupsIds()))
				.filter(bodPackage -> bodPackage.getPolicyStatus() != PolicyStatus.FAILURE)
				.filter(bodPackage -> bodPackage.getPkgStatus() == PkgStatus.ACTIVE)
				.filter(bodPackage -> bodPackage.getPackageMode().getOrder() > PkgMode.TEST.getOrder())
				.collect(Collectors.toList());
		setLiveBodData(liveBodPackages);


	}

    public void reloadQuotaTopUps(String productOfferName){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading Top-Ups");
		}

		UserPackage dataServicePkgData = null;

		if(StringUtils.isNotBlank(productOfferName)){
			ProductOffer productOffer = getNVSMXPolicyRepository().getProductOffer().byName(productOfferName);
			dataServicePkgData = productOffer.getDataServicePkgData();
			if (Objects.isNull(dataServicePkgData)) {
				getLogger().error(MODULE, "Unable to find applicable Top Ups. Reason: Product offer does not have Base package attached");
				setLiveTopUps(Collections.emptyList());
				setAllTopUps(Collections.emptyList());
				return ;
			}
			if (dataServicePkgData.isQuotaProfileExist() == false) {
				getLogger().error(MODULE, "Unable to find applicable Top Ups. Reason: Base package does not have any quota profile attached");
				setLiveTopUps(Collections.emptyList());
				setAllTopUps(Collections.emptyList());
				return ;
			}

		}


		StaffGroupQuotaTopUpPredicate staffGroupQuotaTopUpPredicate = StaffGroupQuotaTopUpPredicate.create(getStaffBelongingGroupsIds());
		List<QuotaTopUp> activeLiveQuotaTopUps = getNVSMXPolicyRepository().getActiveLiveQuotaTopUpDatas();

		if (Collectionz.isNullOrEmpty(activeLiveQuotaTopUps) == false) {
			Collectionz.filter(activeLiveQuotaTopUps, Predicates.and(staffGroupQuotaTopUpPredicate, FailedQuotaTopUpFilter.getInstance()));
		}

		List<QuotaTopUp> activeAllQuotaTopUps = getNVSMXPolicyRepository().getActiveAllQuotaTopUpDatas();

		if (Collectionz.isNullOrEmpty(activeAllQuotaTopUps) == false) {
			Collectionz.filter(activeAllQuotaTopUps, Predicates.and(staffGroupQuotaTopUpPredicate, FailedQuotaTopUpFilter.getInstance()));

		}

		if(StringUtils.isEmpty(productOfferName)){

			if (Collectionz.isNullOrEmpty(activeLiveQuotaTopUps) == false) {
				setLiveTopUps(activeLiveQuotaTopUps);
			}
			if (Collectionz.isNullOrEmpty(activeAllQuotaTopUps) == false) {
				setAllTopUps(activeAllQuotaTopUps);
			}
		}else{
			List<QuotaTopUp> liveQuotaTopUps = getQuotaTopUpByBasePackage(activeLiveQuotaTopUps, (BasePackage) dataServicePkgData);
			List<QuotaTopUp> allQuotaTopUps = getQuotaTopUpByBasePackage(activeAllQuotaTopUps, (BasePackage) dataServicePkgData);
			if (Collectionz.isNullOrEmpty(liveQuotaTopUps) == false) {
				setLiveTopUps(activeLiveQuotaTopUps);
			}
			if (Collectionz.isNullOrEmpty(allQuotaTopUps) == false) {
				setAllTopUps(activeAllQuotaTopUps);
			}

		}
	}

	private List<QuotaTopUp> getQuotaTopUpByBasePackage(List<QuotaTopUp> quotaTopUps, BasePackage basePackage){
		if(CollectionUtils.isEmpty(quotaTopUps)){
			return Collections.emptyList();
		}
		List<String> basePccProfileNames = basePackage.getQoSProfiles().stream().map(QoSProfile::getName).collect(Collectors.toList());
		ApplicableQuotaTopUpByBasePackagePredicate applicableQuotaTopUpPredicate = ApplicableQuotaTopUpByBasePackagePredicate.create(basePccProfileNames);
		return quotaTopUps.stream().filter(applicableQuotaTopUpPredicate).collect(Collectors.toList());
	}

	public String reloadImsPackages(){
		final String staffBelongingGroupIds = getStaffBelongingGroups();
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading IMS packages");
		}

		if (Strings.isNullOrBlank(staffBelongingGroupIds)) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Reload IMS Package operation can't be performed.Reason: User doesn't belong to any group");
			}
			return Results.SUCCESS.getValue();
		}
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading IMS Package for groups: " + staffBelongingGroupIds);
		}

		reloadPolicyBlManager.reloadPoliciesByGroups(staffBelongingGroupIds);

		List<String> staffGroups = CommonConstants.COMMA_SPLITTER.split(staffBelongingGroupIds);

		StaffGroupIMSPackagePredicate staffGroupIMSPackagePredicate = StaffGroupIMSPackagePredicate.create(staffGroups);

		List<IMSPackage> activeLiveImsPkgs = getNVSMXPolicyRepository().getActiveLiveImsPkgDatas();

		if (Collectionz.isNullOrEmpty(activeLiveImsPkgs) == false) {
			Collectionz.filter(activeLiveImsPkgs, Predicates.and(FailedIMSPackageFilter.getInstance(), staffGroupIMSPackagePredicate));
			if (Collectionz.isNullOrEmpty(activeLiveImsPkgs) == false) {
				setImsLivePackages(activeLiveImsPkgs);
			}
		}

		List<IMSPackage> activeAllImsPkgs = getNVSMXPolicyRepository().getActiveAllImsPkgDatas();

		if (Collectionz.isNullOrEmpty(activeAllImsPkgs) == false) {
			Collectionz.filter(activeAllImsPkgs, Predicates.and(FailedIMSPackageFilter.getInstance(), staffGroupIMSPackagePredicate));
			if (Collectionz.isNullOrEmpty(activeAllImsPkgs) == false) {
				setImsPackages(activeAllImsPkgs);
			}
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "IMS packages reloaded successfully");
		}

		return Results.SUCCESS.getValue();

	}


	public String reload() {
		if (getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Reloading policies");
		try {

			final String staffBelongingGroupIds = getStaffBelongingGroups();

			ReloadPolicyResponse reloadPolicyResponse = reloadPolicyBlManager.reloadPoliciesByGroups(staffBelongingGroupIds);

			final List<String> staffGroups = getStaffBelongingGroupsIds();

			policyDetail = getNVSMXPolicyRepository().getOfferDetail(StaffGroupProductOfferPredicate.create(staffGroups));
			policyDetail.addAll(getNVSMXPolicyRepository().getRnCPackageDetail(StaffGroupRnCPackagePredicate.create(staffGroups)));
			policyDetail.addAll(getNVSMXPolicyRepository().getPolicyDetail(StaffGroupUserPackagePredicate.create(staffGroups), StaffGroupIMSPackagePredicate.create(staffGroups), StaffGroupQuotaTopUpPredicate.create(staffGroups), StaffGroupMonetaryRechargePlanPredicate.create(staffGroups)));
			policyDetail.addAll(getNVSMXPolicyRepository().getBoDPackageDetail(StaffGroupBoDPackagePredicate.create(staffGroups)));

			for (PolicyDetail policyData: policyDetail) {
				createPkgTypeWisePolicy(policyData);
			}

			Gson gson = GsonFactory.defaultInstance();
			for(Map.Entry<String, List<PolicyDetail>> policies: pkgTypeWisePolicy.entrySet()){
				String jsonValue = gson.toJsonTree(policies.getValue()).getAsJsonArray().toString().replaceAll("\\\\n|\\\\t", "");
				jsonValue = jsonValue.replaceAll("\\\"", "\\\\\"");
				pkgTypeWisePolicyJsonMap.put(policies.getKey(), jsonValue);
			}

			pkgTypeWisePolicyStatistics = reloadPolicyBlManager.getPkgTypeWisePolicyStatistics();
			setReloadPolicyCacheDetail(reloadPolicyResponse.getLocalPolicyCacheDetail());

			unreachableInstances = reloadPolicyResponse.getRemoteRMIResponseList().stream()
					.filter(remoteRMIResponse -> ResultCode.INTERNAL_ERROR.code == remoteRMIResponse.getResponseCode() )
					.map(remoteRMIResponse -> remoteRMIResponse.getInstanceData().getName())
					.collect(Collectors.joining(","));

			addActionMessage("Policy Reloaded Successfully");
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while reloading policy repository. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
		}

		return Results.RELOAD_POLICY.getValue();
	}

	private void createPkgTypeWisePolicy(PolicyDetail policyDetail) {
		String entityType = policyDetail.getEntityType();
		pkgTypeWisePolicy.get(entityType).add(policyDetail);
	}



	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<ProductOffer> getProductOfferAddOns() {
		return productOfferAddOns;
	}

	public void setProductOfferAddOns(List<ProductOffer> productOfferAddOns) {
		this.productOfferAddOns = productOfferAddOns;
	}

	public List<BasePackage> getLivePackages() {
		return livePackages;
	}

	public void setLivePackages(List<BasePackage> livePackages2) {
		this.livePackages = livePackages2;
	}

	public List<ProductOffer> getProductOffers() {
		return productOffers;
	}

	public void setProductOffers(List<ProductOffer> productOffers) {
		this.productOffers = productOffers;
	}

	public List<RnCPackage> getRncLivePackages() {
		return rncLivePackages;
	}

	public void setRncLivePackages(List<RnCPackage> rncLivePackages) {
		this.rncLivePackages = rncLivePackages;
	}

	public List<RnCPackage> getRncPackages() {
		return rncPackages;
	}

	public void setRncPackages(List<RnCPackage> rncPackages) {
		this.rncPackages = rncPackages;
	}

	public List<BasePackage> getDataPackages() {
		return dataPackages;
	}

	public void setDataPackages(List<BasePackage> dataPackages2) {
		this.dataPackages = dataPackages2;
	}

	public List<SubscriptionPackage> getLiveAddOns() {
		return liveAddOns;
	}

	public void setLiveAddOns(List<SubscriptionPackage> liveAddons) {
		this.liveAddOns = liveAddons;
	}

	public List<SubscriptionPackage> getAllAddOns() {
		return allAddOns;
	}

	public void setAllAddOns(List<SubscriptionPackage> allAddOns) {
		this.allAddOns = allAddOns;
	}

	public List<IMSPackage> getImsLivePackages() {
		return imsLivePackages;
	}

	public void setImsLivePackages(List<IMSPackage> imsLivePackages) {
		this.imsLivePackages = imsLivePackages;
	}

	public List<IMSPackage> getImsPackages() {
		return imsPackages;
	}

	public void setImsPackages(List<IMSPackage> imsPackages) {
		this.imsPackages = imsPackages;
	}

	public List<PolicyDetail> getPolicyDetail() {
		return policyDetail;
	}

	public void setPolicyDetail(List<PolicyDetail> policyDetail) {
		this.policyDetail = policyDetail;
	}

	public PolicyCacheDetail getPolicyCacheDetail() {
		return policyCacheDetail;
	}

	public void setPolicyCacheDetail(PolicyCacheDetail policyCacheDetail) {
		this.policyCacheDetail = policyCacheDetail;
	}


	public String getUnreachableInstances() {
		return unreachableInstances;
	}

	public void setUnreachableInstances(String unreachableInstances) {
		this.unreachableInstances = unreachableInstances;
	}

	public List<QuotaTopUp> getAllTopUps() {
		return allTopUps;
	}

	public void setAllTopUps(List<QuotaTopUp> allTopUps) { this.allTopUps = allTopUps; }

	public List<QuotaTopUp> getLiveTopUps() {
		return liveTopUps;
	}

	public void setLiveTopUps(List<QuotaTopUp> liveTopUps) {
		this.liveTopUps = liveTopUps;
	}

	public List<ProductOffer> getLiveProductOffers() {
		return liveProductOffers;
	}

	public void setLiveProductOffers(List<ProductOffer> liveProductOffers) {
		this.liveProductOffers = liveProductOffers;
	}

	public List<ProductOffer> getLiveProductOffersAddons() {
		return liveProductOffersAddons;
	}

	public void setLiveProductOffersAddons(List<ProductOffer> liveProductOffersAddons) {
		this.liveProductOffersAddons = liveProductOffersAddons;
	}

	private List<String> getStaffBelongingGroupsIds() {
		return CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
	}

	private List<String> getGroupIdsFromRequest() {
		return CommonConstants.COMMA_SPLITTER.split(request.getParameter(Attributes.GROUPIDS));
	}

	public void setAllBodData(List<BoDPackage> allBodData) { this.allBodData = allBodData; }

	public void setLiveBodData(List<BoDPackage> liveBodData) { this.liveBodData = liveBodData; }

	public List<BoDPackage> getAllBodData() {
		return allBodData;
	}

	public List<BoDPackage> getLiveBodData() {
		return liveBodData;
	}
}