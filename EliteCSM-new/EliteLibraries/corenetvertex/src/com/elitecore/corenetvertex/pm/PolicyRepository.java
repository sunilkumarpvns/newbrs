
package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Predicate;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
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
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.store.QuotaTopUpPolicyStore;
import com.elitecore.corenetvertex.pm.store.RnCPackageStore;
import com.elitecore.corenetvertex.pm.store.ServiceStore;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public interface PolicyRepository {

	public AddOn getAddOnById(String id);
	public AddOn getAddOnByName(String name);
	public AddOn getActiveAddOnById(String id);
	List<AddOn> getActiveLiveAddOnDatas();

    MonetaryRechargePlanPolicyStore<MonetaryRechargePlan> monetaryRechargePlan();

    public BasePackage getBasePackageDataById(String id);
	public BasePackage getBasePackageDataByName(String name);
	public BasePackage getActiveBasePackageById(String id);
	public BasePackage getActiveBasePackageByName(String name);

	DataPolicyStore<BasePackage> base();

	List<BasePackage> getActiveLiveBasePkgDatas();
	List<BasePackage> getActiveAllBasePkgDatas();

	public List<Package> getPkgDatasByName(String... packageNames);
	public UserPackage getPkgDataByName(String pkgName);
	public UserPackage getPkgDataById(String pkgId);
	public List<Package> getAllPackageDatas();

	public List<IMSPackage> getIMSPackageByName(String... packageNames);
	public IMSPackage getIMSPkgById(String pkgId);
	public IMSPackage getIMSPkgByName(String pkgName);

	public List<EmergencyPackage> getEmergencyPackages();
	public EmergencyPackage getEmergencyPackagebyId(String id);
	public EmergencyPackage getEmergencyPackageByName(String name);

	public PCCRule getPccRule(String pccRuleName);
	public PCCRule getPCCRuleById(String pccRuleId);

	public ChargingRuleBaseName getChargingRuleBaseNameByName(String chargingRuleBaseName);
	public ChargingRuleBaseName getChargingRuleBaseNameById(String chargingRuleBaseNameId);

	public IMSPackage getActiveIMSPackageByName(String destArray);
	List<IMSPackage> getActiveLiveImsPkgDatas();
	List<IMSPackage> getActiveAllImsPkgDatas();

	List<ProductOffer> getActiveAllBaseProductOffers();

	List<ProductOffer> getActiveAllAddOnProductOffers();

	List<ProductOffer> getActiveLiveBaseProductOffers();

	List<ProductOffer> getActiveLiveAddOnProductOffers();

	List<QuotaTopUp> getActiveAllQuotaTopUpDatas();

	public List<QuotaTopUp> getActiveLiveQuotaTopUpDatas();

	public SubscriptionPackage getActiveAddOnByName(String id);

	List<AddOn> getActiveAllAddOnPkgDatas(Boolean syInterface);
	List<AddOn> getActiveLiveAddOnDatas(Boolean syInterface);

	public List<String> getAllDataPackageNames();
	public List<String> getAllIMSPackageNames();

	public PolicyCacheDetail reload();
	public Iterator<PolicyCacheDetail> reloadHistory();
	public List<PolicyDetail>  getPolicyDetail();

	List<PolicyDetail> getPolicyDetail(Predicate<UserPackage> dataPackageFilter, Predicate<IMSPackage> imsPackageFilter, Predicate<QuotaTopUp> quotaTopUpFilter,
									   Predicate<MonetaryRechargePlan> monetaryRechargePlanFilter);
	List<PolicyDetail> getOfferDetail(Predicate<ProductOffer> productOfferFilter);
	List<PolicyDetail> getRnCPackageDetail(java.util.function.Predicate<RnCPackage>  predicate);

	public List<PolicyDetail> getPolicyDetail(String...policyName);
	public List<PolicyDetail> getOfferDetails(String...offerName);
	public List<PolicyDetail> getRnCDetails(String...rncPackageName);
	public List<PolicyDetail> getBoDPackageDetails(String...bodPackageNames);
	public List<PromotionalPackage> getPromotionalPackages();
	public PromotionalPackage getPromotionalPackageById(String pkgId);
	public PromotionalPackage getPromotionalPackageByName(String pkgName);
	public PolicyCacheDetail reloadDataPackages(String... names);
	public PolicyCacheDetail reloadIMSPackages(String... names);
	public PolicyCacheDetail reloadProductOffers(java.util.function.Predicate<ProductOfferData>  predicate);
	public PolicyCacheDetail reloadRnCPackages(java.util.function.Predicate<RncPackageData>  predicate);
	public PolicyCacheDetail reloadQuotaTopUps(String... names);

    PolicyCacheDetail reloadMonetaryRechargePlans(String... names);

    public PolicyCacheDetail reloadByName(String... names);

    PolicyCacheDetail reloadMonetaryRechargePlansOfGroups(List<String> groupIds);

    PolicyCacheDetail reloadIMSPackagesOfGroups(List<String> groupIds);

	PolicyCacheDetail reloadQuotaTopUpsOfGroups(List<String> groupIds);

	PolicyCacheDetail reloadDataPackagesOfGroups(List<String> groupIds);

	ArrayList<EmergencyPackage> getEmergencyPackagesOfGroup(String groupId);

	ArrayList<PromotionalPackage> getPromotionalPackagesOfGroup(String groupId);

	ServiceStore getService();

	ProductOfferStore<ProductOffer> getProductOffer();

	RnCPackageStore getRnCPackage();

	public RnCPackage getRnCBasePackageById(String id);
	public RnCPackage getRnCBasePackageByName(String name);

	QuotaTopUpPolicyStore<QuotaTopUp> quotaTopUp();

	public QuotaTopUp getActiveQuotaTopUpByName(String name);

	public QuotaTopUp getActiveQuotaTopUpById(String id);

	public QuotaTopUp getQuotaTopUpById(String id);

	public QuotaTopUp getQuotaTopUpByName(String name);

	public MonetaryRechargePlan getActiveMonetaryRechargePlanByName(String name);

	public MonetaryRechargePlan getActiveMonetaryRechargePlanById(String id);

	public MonetaryRechargePlan getMonetaryRechargePlanById(String id);

	public MonetaryRechargePlan getMonetaryRechargePlanByName(String name);

	public MonetaryRechargePlan getMonetaryRechargePlanByPrice(BigDecimal price);

    QuotaProfile getQuotaProfile(String packageId, String quotaProfileId);

	@Nullable
	DataRateCard getDataRateCard(String packageId, String rateCardId);

	@Nullable
	String getPackageName(String packageID);
	void readServices();

	PolicyCacheDetail getDataPkgTypeWisePolicyStatistics(String pkgType);

	DataSliceConfiguration getSliceConfiguration();

	void reloadSliceConfiguration() throws LoadConfigurationException;

	public BoDPackageStore getBoDPackage();
	public PolicyCacheDetail reloadBoDPackages(java.util.function.Predicate<BoDData> predicate);
	public List<PolicyDetail> getBoDPackageDetail(java.util.function.Predicate<BoDPackage> bodPkgFilter);
}
