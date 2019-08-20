package com.elitecore.netvertex.pm;

import com.elitecore.commons.base.Predicate;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.sliceconfig.DataSliceConfiguration;
import com.elitecore.corenetvertex.pm.store.BoDPackageStore;
import com.elitecore.corenetvertex.pm.store.DataPolicyStore;
import com.elitecore.corenetvertex.pm.store.MonetaryRechargePlanPolicyStore;
import com.elitecore.corenetvertex.pm.store.ParentPolicyStore;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.store.QuotaTopUpPolicyStore;
import com.elitecore.corenetvertex.pm.store.RnCPackageStore;
import com.elitecore.corenetvertex.pm.store.ServiceStore;
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import com.elitecore.netvertex.pm.util.MockAddOnPackage;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.pm.util.MockPackages;
import com.elitecore.netvertex.pm.util.MockProductOffer;
import com.elitecore.netvertex.pm.util.MockPromotionalPackage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.NotNullPredicate;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class DummyPolicyRepository implements PolicyRepository {

	private final Map<String, PCCRule> pccRuleMap;
	private final Map<String, PCCRule> pccRuleNameMap;
	private final Map<String, PCCRule> pccRuleByMonitoringKeyMap;
	private final Map<String,DataServiceType> serviceTypeMap;
	private Map<String, BasePackage> basePackages;
	private ServiceStore serviceStore;
	private ProductOfferStore productOfferStore;
	private RnCPackageStore rnCPackageStore;
	private DataSliceConfiguration dataSliceConfiguration;
	private BoDPackageStore boDPackageStore;


	private ParentPolicyStore parentPolicyStore;




	public DummyPolicyRepository() {
		pccRuleMap = new LinkedHashMap<String, PCCRule>();
		pccRuleNameMap = new LinkedHashMap<String, PCCRule>();
		pccRuleByMonitoringKeyMap = new LinkedHashMap<String, PCCRule>();
		serviceTypeMap = new LinkedHashMap<String, DataServiceType>();
		basePackages = new LinkedHashMap<String, BasePackage>();

		this.serviceStore = new ServiceStore();
		this.productOfferStore = new ProductOfferStore();
		this.boDPackageStore = new BoDPackageStore();

		parentPolicyStore = new ParentPolicyStore(emptyMap(), emptyMap(), emptyMap(), emptyMap());
		rnCPackageStore = new RnCPackageStore();
		dataSliceConfiguration = new DataSliceConfiguration();
		setDataSliceConfiguration();
	}


	public void addPCCRules(Collection<PCCRule> pccRules){

	}

	public DummyPolicyRepository addBasePackage(BasePackage basePackage) {
		parentPolicyStore.reloadDataPolicy(Arrays.asList(basePackage)
				, emptyList()
				, emptyList());

		return this;
	}

	public DummyPolicyRepository addAddOn(AddOn addOn) {
		parentPolicyStore.reloadDataPolicy(Arrays.asList(addOn)
				, emptyList()
				, emptyList());

		return this;
	}

	public MockBasePackage mockBasePackage() {
		MockBasePackage aPackage = MockPackages.basePackage(UUID.randomUUID().toString(), "name:" + UUID.randomUUID().toString());
		addBasePackage(aPackage);
		return aPackage;
	}

	public MockAddOnPackage mockAddOnPackage() {
		MockAddOnPackage aPackage = MockPackages.addOn(UUID.randomUUID().toString(), "name:" + UUID.randomUUID().toString());
		addAddOn(aPackage);
		return aPackage;
	}

	public MockQuotaTopUp mockQuotaTopUp() {
		MockQuotaTopUp mockQuotaTopUp = MockQuotaTopUp.create(UUID.randomUUID().toString(), "name:" + UUID.randomUUID().toString());
		addQuotaTopUp(mockQuotaTopUp );
		return mockQuotaTopUp;
	}

	public MockQuotaTopUp mockQuotaTopUp(List<String> applicablePCCProfiles) {
		MockQuotaTopUp mockQuotaTopUp = MockQuotaTopUp.create(UUID.randomUUID().toString(), "name:--" + UUID.randomUUID().toString(),applicablePCCProfiles);
		addQuotaTopUp(mockQuotaTopUp );
		return mockQuotaTopUp;
	}

	public MockPromotionalPackage mockPromotionalPackage() {
		MockPromotionalPackage aPackage = MockPackages.promotionalPackage(UUID.randomUUID().toString(), "name:" + UUID.randomUUID().toString());
		addPromotionalPackage(aPackage);
		return aPackage;
	}

	private DummyPolicyRepository addPromotionalPackage(MockPromotionalPackage aPackage) {
		parentPolicyStore.reloadDataPolicy(Arrays.asList(aPackage)
				, emptyList()
				, emptyList());

		return this;
	}

	public DummyPolicyRepository addProductOffer(MockProductOffer productOffer) {
		productOfferStore.create(Arrays.asList(productOffer), emptyList());
		return this;
	}

	public void removeProductOffer(MockProductOffer... productOffer) {
		productOfferStore.create(emptyList(), Arrays.asList(productOffer));
	}

	public void addRnCPackages(List<RnCPackage> rnCPackages) {
		rnCPackageStore.create(rnCPackages, emptyList());
	}

	public DummyPolicyRepository addQuotaTopUp(MockQuotaTopUp topup) {
		parentPolicyStore.reloadTopUpPolicy(Arrays.asList(topup)
				, emptyList());
		return this;
	}


    public static class PolicyRepositoryBuilder{

		DummyPolicyRepository mockablePolicyRepository = new DummyPolicyRepository();

		public PolicyRepositoryBuilder withServiceTypes(List<DataServiceType> dataServiceTypes){

			if(dataServiceTypes == null || dataServiceTypes.isEmpty()){
				return this;
			}

			CollectionUtils.filter(dataServiceTypes, NotNullPredicate.INSTANCE);

			for(DataServiceType dataServiceType : dataServiceTypes){
				mockablePolicyRepository.serviceTypeMap.put(dataServiceType.getDataServiceTypeID(), dataServiceType);
			}
			return this;
		}

		public PolicyRepositoryBuilder withPCCRules(Collection<PCCRule> pccRules){

			if(pccRules == null || pccRules.isEmpty()){
				return this;
			}

			CollectionUtils.filter(pccRules, NotNullPredicate.INSTANCE);

			for(PCCRule PCCRule : pccRules){
				mockablePolicyRepository.pccRuleMap.put(PCCRule.getId(), PCCRule);
				mockablePolicyRepository.pccRuleNameMap.put(PCCRule.getName(), PCCRule);
				mockablePolicyRepository.pccRuleByMonitoringKeyMap.put(PCCRule.getMonitoringKey(), PCCRule);

			}
			return this;
		}



		public DummyPolicyRepository build(){
			return mockablePolicyRepository;
		}

		public PolicyRepositoryBuilder withBasePackage(BasePackage object) {
			mockablePolicyRepository.basePackages.put(object.getName(), object);
			return this;
		}
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
	public List<PolicyDetail> getOfferDetail(Predicate<ProductOffer> productOfferFilter) {
		return null;
	}

	@Override
	public List<PolicyDetail> getRnCPackageDetail(java.util.function.Predicate<RnCPackage>  predicate) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AddOn getAddOnById(String id) {
		return parentPolicyStore.addOn().byId(id);
	}


	@Override
	public AddOn getAddOnByName(String name) {
		return parentPolicyStore.addOn().byName(name);
	}


	@Override
	public com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage getBasePackageDataById(String id) {
		return parentPolicyStore.base().byId(id);
	}


	@Override
	public com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage getBasePackageDataByName(String name) {
		return parentPolicyStore.base().byName(name);
	}


	@Override
	public List<Package> getPkgDatasByName(String... packageNames) {
		return Arrays.stream(packageNames).map(parentPolicyStore::byName).filter(Objects::nonNull).collect(Collectors.toList());
	}


	@Override
	public List<IMSPackage> getIMSPackageByName(String... packageNames) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public UserPackage getPkgDataByName(String pkgName) {
		// TODO Auto-generated method stub
		return parentPolicyStore.base().byName(pkgName);
	}


	@Override
	public UserPackage getPkgDataById(String pkgId) {
		return parentPolicyStore.byId(pkgId);
	}


	@Override
	public List<Package> getAllPackageDatas() {
		return parentPolicyStore.all();
	}


	@Override
	public List<String> getAllDataPackageNames() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> getAllIMSPackageNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMSPackage getIMSPkgById(String pkgId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IMSPackage getIMSPkgByName(String pkgName) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public PolicyCacheDetail reload() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage getActiveBasePackageById(String id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage getActiveBasePackageByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataPolicyStore<com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage> base() {
		return null;
	}


	@Override
	public List<EmergencyPackage> getEmergencyPackages() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public PCCRule getPccRule(String pccRuleName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public PCCRule getPCCRuleById(String pccRuleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChargingRuleBaseName getChargingRuleBaseNameByName(String chargingRuleBaseName){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChargingRuleBaseName getChargingRuleBaseNameById(String chargingRuleBaseNameId){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AddOn getActiveAddOnById(String id) {
		return parentPolicyStore.addOn().active().byId(id);
	}


	@Override
	public SubscriptionPackage getActiveAddOnByName(String id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IMSPackage getActiveIMSPackageByName(String destArray) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public List<com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage> getActiveLiveBasePkgDatas() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<AddOn> getActiveLiveAddOnDatas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MonetaryRechargePlanPolicyStore<MonetaryRechargePlan> monetaryRechargePlan() {
		return null;
	}


	@Override
	public List<IMSPackage> getActiveLiveImsPkgDatas() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage> getActiveAllBasePkgDatas() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<IMSPackage> getActiveAllImsPkgDatas() {
		// TODO Auto-generated method stub
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
	public List<AddOn> getActiveAllAddOnPkgDatas(Boolean syInterface) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<AddOn> getActiveLiveAddOnDatas(Boolean syInterface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EmergencyPackage getEmergencyPackagebyId(String id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public EmergencyPackage getEmergencyPackageByName(String name) {
		// TODO Auto-generated method stub
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
	public PolicyCacheDetail reloadDataPackages(String... parameters) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public PolicyCacheDetail reloadIMSPackages(String... parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PolicyCacheDetail reloadProductOffers(java.util.function.Predicate<ProductOfferData>  predicate) {
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
		return null;
	}

	@Override
	public ArrayList<EmergencyPackage> getEmergencyPackagesOfGroup(String groupId) {
		return null;
	}

	@Override
	public ArrayList<PromotionalPackage> getPromotionalPackagesOfGroup(String groupId) {
		return null;
	}

	@Override
	public ServiceStore getService(){
		return serviceStore;
	}

	@Override
	public ProductOfferStore getProductOffer() {
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
		return parentPolicyStore.quotaTopUp().byId(id);
	}

	@Override
	public QuotaTopUp getQuotaTopUpByName(String name) {
		return parentPolicyStore.quotaTopUp().byName(name);
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
		return null;
	}

	@Override
	public MonetaryRechargePlan getMonetaryRechargePlanByName(String name) {
		return null;
	}

	@Override
	public MonetaryRechargePlan getMonetaryRechargePlanByPrice(BigDecimal price) {
		return null;
	}

	@Override
    public QuotaProfile getQuotaProfile(String packageId, String quotaProfileId) {
        UserPackage userPackage = getPkgDataById(packageId);
        if (userPackage != null) {
            return userPackage.getQuotaProfile(quotaProfileId);
        }


        QuotaTopUp quotaTopUp = quotaTopUp().byId(packageId);
        if (quotaTopUp != null) {
            return quotaTopUp.getQuotaProfile(quotaProfileId);
        }
        return null;
    }

    @Nullable
    @Override
    public DataRateCard getDataRateCard(String packageId, String rateCardId) {
        return null;
    }

    @Nullable
    @Override
    public String getPackageName(String packageId) {
        UserPackage userPackage = getPkgDataById(packageId);
        if (userPackage != null) {
            return userPackage.getName();
        }

        QuotaTopUp quotaTopUp = quotaTopUp().byId(packageId);
        if (quotaTopUp != null) {
            return quotaTopUp.getName();
        }

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
		return dataSliceConfiguration;
	}

	private void setDataSliceConfiguration() {
		dataSliceConfiguration.setMonetaryReservation(Long.valueOf(10));
		dataSliceConfiguration.setVolumeMinimumSlice(Long.valueOf(10));
		dataSliceConfiguration.setVolumeMaximumSlice(Long.valueOf(400));
		dataSliceConfiguration.setVolumeMinimumSliceUnit(DataUnit.MB);
		dataSliceConfiguration.setVolumeMaximumSliceUnit(DataUnit.MB);
		dataSliceConfiguration.setTimeMinimumSlice(Long.valueOf(10));
		dataSliceConfiguration.setTimeMaximumSlice(Long.valueOf(1));
		dataSliceConfiguration.setTimeMinimumSliceUnit(TimeUnit.SECOND);
		dataSliceConfiguration.setTimeMaximumSliceUnit(TimeUnit.HOUR);
		dataSliceConfiguration.setVolumeSlicePercentage(10);
		dataSliceConfiguration.setTimeSlicePercentage(10);
		dataSliceConfiguration.setVolumeSliceThreshold(10);
		dataSliceConfiguration.setTimeSliceThreshold(10);

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
