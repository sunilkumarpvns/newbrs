package com.elitecore.corenetvertex.pm.util;

import com.elitecore.commons.base.Predicate;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PackagePredicates;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.EmergencyPackageFactory;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.sliceconfig.DataSliceConfiguration;
import com.elitecore.corenetvertex.pm.store.BoDPackageStore;
import com.elitecore.corenetvertex.pm.store.DataPolicyStore;
import com.elitecore.corenetvertex.pm.store.MonetaryRechargePlanPolicyStore;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.store.QuotaTopUpPolicyStore;
import com.elitecore.corenetvertex.pm.store.RnCPackageStore;
import com.elitecore.corenetvertex.pm.store.ServiceStore;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by harsh on 5/19/17.
 *
 * Use: DummyPolicyProvider
 */
public class PolicyProvider implements PolicyRepository {

    private final DataPackageStore store;
    private final IMSPackageStore imsStore;
    private final QuotaTopUpStore quotaTopUpStore;

    private List<GroupManageOrder> groupManageOrders;
    private Map<String, PCCRule> pccRuleById;
    private Map<String, PCCRule> pccRuleByName;
    private List<PCCRule> pccRules;
    private Map<String, ChargingRuleBaseName> crbnRuleById;
    private Map<String, ChargingRuleBaseName> crbnRuleByName;
    private List<ChargingRuleBaseName> crbns;
    private java.util.function.Predicate<Package> emergencyFilter = pkg -> PkgType.EMERGENCY == pkg.getPackageType();
    private ServiceStore serviceStore;
    private ProductOfferStore productOfferStore;
    private RnCPackageStore rnCPackageStore;
    private BoDPackageStore boDPackageStore;

    public PolicyProvider(DataPackageStore store) {
        this.store = store;
        this.imsStore = new IMSPackageStore();
        this.quotaTopUpStore = new QuotaTopUpStore();
        this.groupManageOrders = new ArrayList<>();
        this.pccRuleById = new HashMap<>();
        this.pccRuleByName = new HashMap<>();
        this.pccRules = new ArrayList<>();
        this.crbns = new ArrayList<>();
        this.crbnRuleById = new HashMap<>();
        this.crbnRuleByName = new HashMap<>();
        this.serviceStore = new ServiceStore();
        this.productOfferStore = new ProductOfferStore();
        this.rnCPackageStore = new RnCPackageStore();
        this.boDPackageStore = new BoDPackageStore();
    }

    public PolicyProvider() {
        this(new DataPackageStore());
    }


    @Override
    public AddOn getAddOnById(String id) {
        return (AddOn) store.getByid(id, PackagePredicates.ADD_ON);
    }

    @Override
    public AddOn getAddOnByName(String name) {
        return (AddOn) store.getByName(name, PackagePredicates.ADD_ON);
    }

    @Override
    public AddOn getActiveAddOnById(String id) {
        return null;
    }

    @Override
    public List<AddOn> getActiveLiveAddOnDatas() {
        return null;
    }

    @Override
    public MonetaryRechargePlanPolicyStore<MonetaryRechargePlan> monetaryRechargePlan() {
        return null;
    }

    @Override
    public BasePackage getBasePackageDataById(String id) {
        return (BasePackage) store.getByid(id, PackagePredicates.BASE);
    }

    @Override
    public BasePackage getBasePackageDataByName(String name) {
        return (BasePackage) store.getByName(name, PackagePredicates.BASE);
    }

    @Override
    public BasePackage getActiveBasePackageById(String id) {
        return null;
    }

    @Override
    public BasePackage getActiveBasePackageByName(String name) {
        return null;
    }

	@Override
	public DataPolicyStore<BasePackage> base() {
		return null;
	}

	@Override
    public List<BasePackage> getActiveLiveBasePkgDatas() {
        return null;
    }

    @Override
    public List<BasePackage> getActiveAllBasePkgDatas() {
        return null;
    }

    @Override
    public List<Package> getPkgDatasByName(String... packageNames) {
        return null;
    }

    @Override
    public UserPackage getPkgDataByName(String pkgName) {
        return null;
    }

    @Override
    public UserPackage getPkgDataById(String pkgId) {
        return null;
    }

    @Override
    public List<Package> getAllPackageDatas() {
        return (List<Package>) store.all();
    }

    @Override
    public List<IMSPackage> getIMSPackageByName(String... packageNames) {
        return null;
    }

    @Override
    public IMSPackage getIMSPkgById(String id) {
        return imsStore.getById(id);
    }

    @Override
    public IMSPackage getIMSPkgByName(String pkgName) {
        return imsStore.getByName(pkgName);
    }

    @Override
    public List<EmergencyPackage> getEmergencyPackages() {
        return (List<EmergencyPackage>) store.all(emergencyFilter);
    }

    public List<Package> getPackages() {
        return (List<Package>) store.all();
    }

    public List<IMSPackage> getIMSPackages() {
        return imsStore.all();
    }

    public List<QuotaTopUp> getQuotaTopUps() {
        return quotaTopUpStore.all();
    }

    public List<MonetaryRechargePlan> getMonetaryRechargePlans() {
        return monetaryRechargePlan().all();
    }

    @Override
    public EmergencyPackage getEmergencyPackagebyId(String id) {
        return (EmergencyPackage) store.getByid(id, PackagePredicates.EMERGENCY);
    }

    @Override
    public EmergencyPackage getEmergencyPackageByName(String name) {
        return (EmergencyPackage) store.getByName(name, PackagePredicates.EMERGENCY);
    }

    @Override
    public PCCRule getPccRule(String pccRuleName) {
        return null;
    }

    @Override
    public PCCRule getPCCRuleById(String id) {
        return pccRuleById.get(id);
    }

    public PCCRule getPCCRuleByName(String name) {
        return pccRuleByName.get(name);
    }

    @Override
    public IMSPackage getActiveIMSPackageByName(String destArray) {
        return null;
    }

    @Override
    public List<IMSPackage> getActiveLiveImsPkgDatas() {
        return null;
    }

    @Override
    public List<IMSPackage> getActiveAllImsPkgDatas() {
        return null;
    }

    @Override
    public List<ProductOffer> getActiveAllBaseProductOffers() {
        return null;
    }

    @Override
    public List<ProductOffer> getActiveAllAddOnProductOffers() {
        return null;
    }

    @Override
    public List<ProductOffer> getActiveLiveBaseProductOffers() {
        return null;
    }

    @Override
    public List<ProductOffer> getActiveLiveAddOnProductOffers() {
        return null;
    }

    @Override
    public List<QuotaTopUp> getActiveAllQuotaTopUpDatas() {
        return null;
    }

    @Override
    public List<QuotaTopUp> getActiveLiveQuotaTopUpDatas() {
        return null;
    }

    @Override
    public SubscriptionPackage getActiveAddOnByName(String id) {
        return null;
    }

    @Override
    public List<AddOn> getActiveAllAddOnPkgDatas(Boolean syInterface) {
        return null;
    }

    @Override
    public List<AddOn> getActiveLiveAddOnDatas(Boolean syInterface) {
        return null;
    }

    @Override
    public List<String> getAllDataPackageNames() {
        return null;
    }

    @Override
    public List<String> getAllIMSPackageNames() {
        return null;
    }

    @Override
    public PolicyCacheDetail reload() {
        return null;
    }

    @Override
    public Iterator<PolicyCacheDetail> reloadHistory() {
        return null;
    }

    @Override
    public List<PolicyDetail> getPolicyDetail() {
        return null;
    }

    @Override
    public List<PolicyDetail> getPolicyDetail(Predicate<UserPackage> dataPackageFilter, Predicate<IMSPackage> imsPackageFilter, Predicate<QuotaTopUp> quotaTopUpFilter, Predicate<MonetaryRechargePlan> monetaryRechargePlanFilter) {
        return null;
    }

    @Override
    public List<PolicyDetail> getPolicyDetail(String... policyName) {
        return null;
    }

    @Override
    public synchronized List<PolicyDetail> getOfferDetails(String... offerNames) {
        return null;
    }

    @Override
    public synchronized List<PolicyDetail> getRnCDetails(String... rncPackageNames) {
        return null;
    }

    @Override
    public List<PolicyDetail> getBoDPackageDetails(String... bodPackageNames) {
        return null;
    }

    @Override
    public ChargingRuleBaseName getChargingRuleBaseNameByName(String chargingRuleBaseName) {
        return this.crbnRuleByName.get(chargingRuleBaseName);
    }



    @Override
    public ChargingRuleBaseName getChargingRuleBaseNameById(String chargingRuleBaseNameId) {
        return this.crbnRuleById.get(chargingRuleBaseNameId);
    }

    @Override
    public List<PolicyDetail> getOfferDetail(Predicate<ProductOffer> productOfferFilter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PolicyDetail> getRnCPackageDetail(java.util.function.Predicate<RnCPackage>  predicate) {
        return null;
    }

    @Override
    public List<PromotionalPackage> getPromotionalPackages() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public PromotionalPackage getPromotionalPackageById(String pkgId) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public PromotionalPackage getPromotionalPackageByName(String pkgName) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public PolicyCacheDetail reloadDataPackages(String... names) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PolicyCacheDetail reloadIMSPackages(String... names) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PolicyCacheDetail reloadProductOffers(java.util.function.Predicate<ProductOfferData>  predicate) {
        // TODO Auto-generated method stub
        return null;
    }

    public PolicyCacheDetail reloadRnCPackages(java.util.function.Predicate<RncPackageData>  predicate) {
        return null;
    }

    @Override
    public PolicyCacheDetail reloadQuotaTopUps(String... names) {
        return null;
    }

    @Override
    public PolicyCacheDetail reloadMonetaryRechargePlans(String... names) {
        return null;
    }


    @Override
	public PolicyCacheDetail reloadByName(String... names) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public PolicyCacheDetail reloadMonetaryRechargePlansOfGroups(List<String> groupIds) {
        return null;
    }

    @Override
    public PolicyCacheDetail reloadIMSPackagesOfGroups(List<String> groupIds) {
        return null;
    }

    @Override
    public PolicyCacheDetail reloadQuotaTopUpsOfGroups(List<String> groupIds) {
        return null;
    }

    @Override
    public PolicyCacheDetail reloadDataPackagesOfGroups(List<String> groupIds) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public ArrayList<EmergencyPackage> getEmergencyPackagesOfGroup(String groupId) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public ArrayList<PromotionalPackage> getPromotionalPackagesOfGroup(String groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    public MockBasePackage addBasePolicy(String id, String name) {
        MockBasePackage basePackage = MockPackages.basePackage(id, name);
        store.policyReloaded(basePackage);
        return basePackage;
    }

    public void addEmergencyGroupManageOrder(String groupId, String packageId, int orderNo) {
        this.groupManageOrders.add(new GroupManageOrder(UUID.randomUUID().toString(), groupId, PkgType.EMERGENCY, orderNo, packageId));
    }

    public void addEmergencyPackage(String id, String name) {
        addEmergencyPackage(id, name, PolicyStatus.SUCCESS);
    }

    public void addEmergencyPackage(String id, String name, PolicyStatus policyStatus) {
        EmergencyPackage pkg = EmergencyPackageFactory.createEmergencyPackage(id, name)
                .withPkgManageOrder(groupManageOrders.stream().filter(order -> order.getPackageId().equals(id)).collect(Collectors.toList()))
                .withPolicyStatus(policyStatus)
                .build();

        this.store.policyReloaded(pkg);
    }

    public List<GroupManageOrder> getGroupManageOrders() {
        return groupManageOrders;
    }

    public void addPCCRule(String id, String name) {
        PCCRule pccRule = mock(PCCRule.class);
        when(pccRule.getId()).thenReturn(id);
        when(pccRule.getName()).thenReturn(name);

        this.pccRuleById.put(id, pccRule);
        this.pccRuleByName.put(name, pccRule);
        this.pccRules.add(pccRule);
    }

    public List<PCCRule> getPccRules() {
        return pccRules;
    }

    public List<ChargingRuleBaseName> getChargingRuleBaseNames() {
        return crbns;
    }

    public MockIMSPackage addIMSPolicy(String id, String name) {
        MockIMSPackage imsPackage = MockPackages.ims(id, name);
        imsStore.add(imsPackage);
        return imsPackage;
    }

    public MockQuotaTopUp addQuotaTopUpPolicy(String id, String name) {
        MockQuotaTopUp quotaTopUp = MockPackages.quotaTopUp(id, name);
        quotaTopUpStore.add(quotaTopUp);
        return quotaTopUp;
    }

    public void addCRBN(String id, String name) {
        ChargingRuleBaseName crbn = mock(ChargingRuleBaseName.class);
        when(crbn.getId()).thenReturn(id);
        when(crbn.getName()).thenReturn(name);
        crbnRuleById.put(crbn.getId(), crbn);
        crbnRuleByName.put(crbn.getName(), crbn);
        crbns.add(crbn);
    }
    @Override
    public ServiceStore getService(){
        return serviceStore;
    }

    @Override
    public ProductOfferStore getProductOffer(){
        return productOfferStore;
    }

    @Override
    public RnCPackageStore getRnCPackage() {
        return rnCPackageStore;
    }

    @Override
    public QuotaTopUpPolicyStore<QuotaTopUp> quotaTopUp() {
        return null;
    }

    public void addTopUp(QuotaTopUp quotaTopUp, QuotaTopUp... quotaTopUps) {
        this.quotaTopUpStore.add(quotaTopUp);

        for (QuotaTopUp topUp : quotaTopUps) {
            this.quotaTopUpStore.add(topUp);
        }
    }

    @Override
    public QuotaTopUp getActiveQuotaTopUpByName(String id) {
        return null;
    }

    @Override
    public QuotaTopUp getActiveQuotaTopUpById(String id) {
        return null;
    }

    @Override
    public QuotaTopUp getQuotaTopUpById(String id) {
        return quotaTopUpStore.getById(id);
    }

    @Override
    public QuotaTopUp getQuotaTopUpByName(String name) {
        return quotaTopUpStore.getByName(name);
    }

    @Override
    public MonetaryRechargePlan getActiveMonetaryRechargePlanByName(String name) {
        return null;
    }

    @Override
    public MonetaryRechargePlan getActiveMonetaryRechargePlanById(String id) {
        return null;
    }

    @Override
    public MonetaryRechargePlan getMonetaryRechargePlanById(String id) {
        return monetaryRechargePlan().byId(id);
    }

    @Override
    public MonetaryRechargePlan getMonetaryRechargePlanByName(String name) {
        return monetaryRechargePlan().byName(name);
    }

    @Override
    public MonetaryRechargePlan getMonetaryRechargePlanByPrice(BigDecimal price) {
        return null;
    }

    @Override
    public QuotaProfile getQuotaProfile(String packageId, String quotaProfileId) {
        return null;
    }

    @Nullable
    @Override
    public DataRateCard getDataRateCard(String packageId, String rateCardId) {
        return null;
    }

    @Nullable
    @Override
    public String getPackageName(String packageID) {
        return null;
    }

    @Override
    public void readServices(){
        //No implementation needed
    }

    @Override
    public PolicyCacheDetail getDataPkgTypeWisePolicyStatistics(String pkgType) {
        return null;
    }

    @Override
    public DataSliceConfiguration getSliceConfiguration() {
        return null;
    }

    @Override
    public void reloadSliceConfiguration() throws LoadConfigurationException {
        //No implementation needed
    }

    @Override
	public RnCPackage getRnCBasePackageById(String id) {
		return rnCPackageStore.baseById(id);
	}

	@Override
	public RnCPackage getRnCBasePackageByName(String name) {
		return rnCPackageStore.baseByName(name);
	}

    @Override
    public BoDPackageStore getBoDPackage() {
        return boDPackageStore;
    }

    @Override
    public PolicyCacheDetail reloadBoDPackages(java.util.function.Predicate<BoDData> predicate) {
        return null;
    }

    @Override
    public List<PolicyDetail> getBoDPackageDetail(java.util.function.Predicate<BoDPackage> bodPkgFilter) {
        return null;
    }
}
