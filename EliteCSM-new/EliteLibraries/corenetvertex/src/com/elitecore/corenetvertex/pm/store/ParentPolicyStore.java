package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ParentPolicyStore implements PolicyStore<Package> {

    private static final String MODULE = "PARENT-PLC-STORE";

    @Nonnull private List<Package> packages;
    @Nonnull private Map<String, Package> byName;
    @Nonnull private Map<String, Package> byId;
    @Nonnull private List<GroupManageOrder> groupManageOrders;

    //IMS
    @Nonnull private Map<String, IMSPackage> imsById;
    @Nonnull private Map<String, QuotaTopUp> quotaTopUpById;
    @Nonnull private Map<String, MonetaryRechargePlan> monetaryRechargePlanById;
    @Nonnull private DataPolicyStore<BasePackage> basePolicyStore;
    @Nonnull private DataPolicyStore<AddOn> addOnPolicyStore;

    @Nonnull private GlobalPolicyStore<EmergencyPackage> emergencyPolicyStore;
    @Nonnull private GlobalPolicyStore<PromotionalPackage> promotionalPolicyStore;
    @Nonnull private IMSPolicyStore<IMSPackage> imsPolicyStore;
    @Nonnull private QuotaTopUpPolicyStore<QuotaTopUp> quotaTopUpPolicyStore;
    @Nonnull private MonetaryRechargePlanPolicyStore<MonetaryRechargePlan> monetaryRechargePlanPolicyStore;
    @Nonnull private PCCRuleStore pccRuleStore;
    @Nonnull private ChargingRuleBaseNameStore chargingRuleBaseNameStore;


    public ParentPolicyStore(@Nonnull Map<String, Package> backUpDataPackagesById,
                             @Nonnull Map<String, IMSPackage> backUpIMSPackagesById,
                             @Nonnull Map<String, QuotaTopUp> backUpQuotaTopUpsById,
                             @Nonnull Map<String, MonetaryRechargePlan> backUpMonetaryRechargePlansById) {
        this.basePolicyStore = new DataPolicyStore<>(createFilterFor(PkgType.BASE));
        this.addOnPolicyStore = new DataPolicyStore<>(createFilterFor(PkgType.ADDON));
        this.quotaTopUpPolicyStore = new QuotaTopUpPolicyStore<>();
        this.emergencyPolicyStore = new GlobalPolicyStore<>(createFilterFor(PkgType.EMERGENCY), createFilterForGroupManageOrder(PkgType.EMERGENCY));
        this.promotionalPolicyStore = new GlobalPolicyStore<>(createFilterFor(PkgType.PROMOTIONAL), createFilterForGroupManageOrder(PkgType.PROMOTIONAL));
        this.imsPolicyStore = new IMSPolicyStore<>();
        this.monetaryRechargePlanPolicyStore = new MonetaryRechargePlanPolicyStore<>();
        this.pccRuleStore = new PCCRuleStore();
        this.chargingRuleBaseNameStore = new ChargingRuleBaseNameStore();

        this.packages = new ArrayList<>();
        this.byId = new HashMap<>();
        this.byId.putAll(backUpDataPackagesById);
        this.byName = new HashMap<>();
        this.groupManageOrders = new ArrayList<>();
        this.imsById = new HashMap<>();
        this.quotaTopUpById = new HashMap<>();
        this.monetaryRechargePlanById = new HashMap<>();
        this.imsById.putAll(backUpIMSPackagesById);
        this.quotaTopUpById.putAll(backUpQuotaTopUpsById);
        this.monetaryRechargePlanById.putAll(backUpMonetaryRechargePlansById);
    }

    private Predicate<Package> createFilterFor(PkgType pkgType) {
        return pkg -> pkgType == pkg.getPackageType();
    }

    private Predicate<GroupManageOrder> createFilterForGroupManageOrder(PkgType pkgType) {
        return pkg -> pkgType == pkg.getPackageType();
    }

    /**
     * Packages passed here will not have DESIGN Mode, DELETED/INACTIVE Status
     *
     * @param packagesAdded, Newly Added Policies (will not have DESIGN Mode, DELETED/INACTIVE Status)
     * @param groupManageOrders, Group Wise Manage Order for Global Plans Only
     * @param deletedInactivePackagesIds used for remove old data policy cache
     * @param imsPackagesAdded, New IMS Packages
     * @param deletedInactiveIMSPackageIds used for remove old data policy cache
     */
    public void create(@Nonnull List<Package> packagesAdded,
                       @Nonnull List<GroupManageOrder> groupManageOrders,
                       @Nonnull List<String> deletedInactivePackagesIds,
                       @Nonnull List<IMSPackage> imsPackagesAdded,
                       @Nonnull List<String> deletedInactiveIMSPackageIds,
                       @Nonnull List<QuotaTopUp> quotaTopUpsAdded,
                       @Nonnull List<String> deletedInactiveQuotaTopUps,
                       @Nonnull List<MonetaryRechargePlan> monetaryRechargePlansAdded,
                       @Nonnull List<String> deletedInactivemonetaryRechargePlanIds) {
        createDataPolicy(packagesAdded, groupManageOrders, deletedInactivePackagesIds);
        createIMSPolicy(imsPackagesAdded, deletedInactiveIMSPackageIds);
        createQuotaTopUpPolicy(quotaTopUpsAdded, deletedInactiveQuotaTopUps);
        createMonetaryRechargePlanPolicy(monetaryRechargePlansAdded, deletedInactivemonetaryRechargePlanIds);
    }

    private void createDataPolicy(List<Package> packagesAdded, List<GroupManageOrder> groupManageOrders, List<String> deletedInactivePackagesIds) {
        HashMap<String, Package> newById = new HashMap<>();
        HashMap<String, Package> newByName = new HashMap<>();
        List<Package> newPackages = createAllPackagesList(packagesAdded, deletedInactivePackagesIds);

        newPackages.forEach(pkg -> {
            newById.put(pkg.getId(), pkg);
            newByName.put(pkg.getName(), pkg);
        });

        List<GroupManageOrder> newGroupManageOrders = createAllGroupManageOrderList(groupManageOrders, newById);
        DataPolicyStore<BasePackage> newBasePolicyStore = new DataPolicyStore<>(createFilterFor(PkgType.BASE));
        DataPolicyStore<AddOn> newAddOnPolicyStore = new DataPolicyStore<>(createFilterFor(PkgType.ADDON));
        GlobalPolicyStore<EmergencyPackage> newEmergencyPolicyStore = new GlobalPolicyStore<>(createFilterFor(PkgType.EMERGENCY), createFilterForGroupManageOrder(PkgType.EMERGENCY));
        GlobalPolicyStore<PromotionalPackage> newPromotionalPolicyStore = new GlobalPolicyStore<>(createFilterFor(PkgType.PROMOTIONAL), createFilterForGroupManageOrder(PkgType.PROMOTIONAL));
        PCCRuleStore newPccRuleStore = new PCCRuleStore();
        ChargingRuleBaseNameStore newChargingRuleBaseNameStore = new ChargingRuleBaseNameStore();

        newBasePolicyStore.create(newPackages);
        newAddOnPolicyStore.create(newPackages);
        newEmergencyPolicyStore.create(newPackages, newGroupManageOrders);
        newPromotionalPolicyStore.create(newPackages, newGroupManageOrders);
        newPccRuleStore.create(newPackages);
        newChargingRuleBaseNameStore.create(newPackages);

        this.packages = newPackages;
        this.byId = newById;
        this.byName = newByName;
        this.groupManageOrders = newGroupManageOrders;
        this.basePolicyStore = newBasePolicyStore;
        this.addOnPolicyStore = newAddOnPolicyStore;
        this.emergencyPolicyStore = newEmergencyPolicyStore;
        this.promotionalPolicyStore = newPromotionalPolicyStore;
        this.pccRuleStore = newPccRuleStore;
        this.chargingRuleBaseNameStore = newChargingRuleBaseNameStore;
    }

    private void createIMSPolicy(List<IMSPackage> imsPackagesAdded, List<String> deletedInactiveIMSPackageIds) {
        List<IMSPackage> newIMSPackages = createAllIMSPackages(imsPackagesAdded, deletedInactiveIMSPackageIds);
        HashMap<String, IMSPackage> newIMSById = new HashMap<>();
        newIMSPackages.forEach(pkg-> newIMSById.put(pkg.getId(), pkg));
        IMSPolicyStore<IMSPackage> newImsPolicyStore = new IMSPolicyStore<>();

        newImsPolicyStore.create(newIMSPackages);

        this.imsById = newIMSById;
        this.imsPolicyStore = newImsPolicyStore;
    }

    private void createQuotaTopUpPolicy(List<QuotaTopUp> quotaTopUpsAdded, List<String> deletedInactiveQuotaTopUps) {
        List<QuotaTopUp> newQuotaTopUps = createAllQuotaTopUps(quotaTopUpsAdded, deletedInactiveQuotaTopUps);
        HashMap<String, QuotaTopUp> newQuotaTopUpsById = new HashMap<>();
        newQuotaTopUps.forEach(pkg-> newQuotaTopUpsById.put(pkg.getId(), pkg));
        QuotaTopUpPolicyStore<QuotaTopUp> newQuotaTopUpPolicyStore = new QuotaTopUpPolicyStore<>();

        newQuotaTopUpPolicyStore.create(newQuotaTopUps);

        this.quotaTopUpById = newQuotaTopUpsById;
        this.quotaTopUpPolicyStore = newQuotaTopUpPolicyStore;
    }

    private void createMonetaryRechargePlanPolicy(List<MonetaryRechargePlan> monetaryRechargePlansAdded, List<String> deletedInactiveMonetaryRechargePlans) {
        List<MonetaryRechargePlan> newMonetaryRechargePlans = createAllMonetaryRechargePlans(monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlans);
        HashMap<String, MonetaryRechargePlan> newMonetaryRechargePlansById = new HashMap<>();
        newMonetaryRechargePlans.forEach(pkg-> newMonetaryRechargePlansById.put(pkg.getId(), pkg));
        MonetaryRechargePlanPolicyStore<MonetaryRechargePlan> newMonetrayRechargePlanPolicystore = new MonetaryRechargePlanPolicyStore<>();

        newMonetrayRechargePlanPolicystore.create(newMonetaryRechargePlans);

        this.monetaryRechargePlanById = newMonetaryRechargePlansById;
        this.monetaryRechargePlanPolicyStore = newMonetrayRechargePlanPolicystore;
    }

    private List<IMSPackage> createAllIMSPackages(List<IMSPackage> imsPackages, List<String> deletedInactiveIMSPackageIds) {

        Set<String> newPackageIds = new HashSet<>();
        List<IMSPackage> newPackages = new ArrayList<>();
        imsPackages.forEach(pkg -> {

            if (PolicyStatus.FAILURE == pkg.getStatus() && this.imsById.get(pkg.getId()) != null) {
                IMSPackage previousPackage = this.imsById.get(pkg.getId());
                if(previousPackage.getStatus() != PolicyStatus.FAILURE){
                    previousPackage.setPolicyStatus(PolicyStatus.LAST_KNOWN_GOOD);
                    previousPackage.setFailedReson(pkg.getFailReason());
                    previousPackage.setPartialFailReason(pkg.getPartialFailReason());
                    pkg = previousPackage;

                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Used last known good configuration for package(" + pkg.getName() + ")");
                    }
                }
            }

            newPackageIds.add(pkg.getId());
            newPackages.add(pkg);
        });

        //deletedInactivePackages should be not be added in new list
        this.imsById.values().stream().filter(pkg-> (!newPackageIds.contains(pkg.getId()))
                && (!deletedInactiveIMSPackageIds.contains(pkg.getId()))).forEach(newPackages::add);

        return newPackages;
    }

    private List<QuotaTopUp> createAllQuotaTopUps(List<QuotaTopUp> quotaTopUps, List<String> deletedInactiveQuotaTopUps) {

        Set<String> newPackageIds = new HashSet<>();
        List<QuotaTopUp> newPackages = new ArrayList<>();
        quotaTopUps.forEach(pkg -> {

            if (PolicyStatus.FAILURE == pkg.getStatus() && this.quotaTopUpById.get(pkg.getId()) != null) {
                QuotaTopUp previousPackage = this.quotaTopUpById.get(pkg.getId());
                if(previousPackage.getStatus() != PolicyStatus.FAILURE) {
                    previousPackage.setPolicyStatus(PolicyStatus.LAST_KNOWN_GOOD);
                    previousPackage.setFailReason(pkg.getFailReason());
                    previousPackage.setPartialFailReason(pkg.getPartialFailReason());
                    pkg = previousPackage;

                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Used last known good configuration for package(" + pkg.getName() + ")");
                    }
                }
            }

            newPackageIds.add(pkg.getId());
            newPackages.add(pkg);
        });

        //deletedInactivePackages should be not be added in new list
        this.quotaTopUpById.values().stream().filter(pkg-> (!newPackageIds.contains(pkg.getId()))
                && (!deletedInactiveQuotaTopUps.contains(pkg.getId()))).forEach(newPackages::add);

        return newPackages;
    }

    private List<MonetaryRechargePlan> createAllMonetaryRechargePlans(List<MonetaryRechargePlan> monetaryRechargePlans, List<String> deletedInactiveMonetaryRechargePlans) {

        Set<String> newPackageIds = new HashSet<>();
        List<MonetaryRechargePlan> newPackages = new ArrayList<>();
        monetaryRechargePlans.forEach(pkg -> {

            if (PolicyStatus.FAILURE == pkg.getStatus() && this.quotaTopUpById.get(pkg.getId()) != null) {
                MonetaryRechargePlan previousPackage = this.monetaryRechargePlanById.get(pkg.getId());
                if(previousPackage.getStatus() != PolicyStatus.FAILURE) {
                    previousPackage.setStatus(PolicyStatus.LAST_KNOWN_GOOD);
                    previousPackage.setFailReason(pkg.getFailReason());
                    previousPackage.setPartialFailReason(pkg.getPartialFailReason());
                    pkg = previousPackage;

                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Used last known good configuration for package(" + pkg.getName() + ")");
                    }
                }
            }

            newPackageIds.add(pkg.getId());
            newPackages.add(pkg);
        });

        //deletedInactivePackages should be not be added in new list
        this.monetaryRechargePlanById.values().stream().filter(pkg-> (!newPackageIds.contains(pkg.getId()))
                && (!deletedInactiveMonetaryRechargePlans.contains(pkg.getId()))).forEach(newPackages::add);

        return newPackages;
    }

    private List<GroupManageOrder> createAllGroupManageOrderList(List<GroupManageOrder> groupManageOrders, HashMap<String, Package> newPackagesById) {

        HashMap<String, GroupManageOrder> tempMapById = new HashMap<>();
        List<GroupManageOrder> newGroupManageOrders = new ArrayList<>();
        groupManageOrders.forEach((GroupManageOrder order) -> {
            tempMapById.put(order.getId(), order);
            newGroupManageOrders.add(order);
        });

        // Add Remaining Packages which are not deleted or marked inactive.
        this.groupManageOrders.stream().filter(order-> (tempMapById.get(order.getId()) == null)
                                            && (newPackagesById.get(order.getPackageId()) != null)).forEach(newGroupManageOrders::add);
        return newGroupManageOrders;
    }

    private List<Package> createAllPackagesList(List<Package> packagesAdded, List<String> deletedInactivePackagesIds) {

        HashMap<String, Package> newPackagesMapById = new HashMap<>();
        List<Package> newPackages = new ArrayList<>();
        packagesAdded.forEach(pkg -> {

            if (PolicyStatus.FAILURE == pkg.getStatus() && this.byId.get(pkg.getId()) != null) {
                Package previousPackage = this.byId.get(pkg.getId());
                if(previousPackage.getStatus() != PolicyStatus.FAILURE) {
                    previousPackage.setPolicyStatus(PolicyStatus.LAST_KNOWN_GOOD);
                    previousPackage.setFailReason(pkg.getFailReason());
                    previousPackage.setPartialFailReason(pkg.getPartialFailReason());
                    pkg = previousPackage;

                    if (getLogger().isWarnLogLevel()) {
                        getLogger().warn(MODULE, "Used last known good configuration for package(" + pkg.getName() + ")");
                    }
                }
            }

            newPackagesMapById.put(pkg.getId(), pkg);
            newPackages.add(pkg);
        });

        //deletedInactivePackages should be not be added in new list
        this.packages.stream().filter(pkg-> (newPackagesMapById.get(pkg.getId()) == null)
                && (deletedInactivePackagesIds.contains(pkg.getId()) == false)).forEach(newPackages::add);

        return newPackages;
    }

    public void reloadDataPolicy(@Nonnull List<Package> packagesAdded,
                                 @Nonnull List<GroupManageOrder> groupManagerOrdersAdded,
                                 @Nonnull List<String> deletedInactivePackagesIds) {
        createDataPolicy(packagesAdded, groupManagerOrdersAdded, deletedInactivePackagesIds);
    }

    public void reloadIMSPolicy(@Nonnull List<IMSPackage> imsPackagesAdded,
                                 @Nonnull List<String> deletedInactiveIMSPackageIds) {
        createIMSPolicy(imsPackagesAdded, deletedInactiveIMSPackageIds);
    }

    public void reloadTopUpPolicy(List<QuotaTopUp> quotaTopUps, List<String> deletedInactiveQuotaTopUpIds) {

        createQuotaTopUpPolicy(quotaTopUps, deletedInactiveQuotaTopUpIds);
    }

    public void reloadMonetaryRechargePlanPolicy(List<MonetaryRechargePlan> monetaryRechargePlans, List<String> deletedInactiveMonetaryRechargePlanIds) {

        createMonetaryRechargePlanPolicy(monetaryRechargePlans, deletedInactiveMonetaryRechargePlanIds);
    }

    @Override
    @Nullable public Package byId(String id) {
        return byId.get(id);
    }

    @Override
    @Nullable public Package byName(String name) {
        return byName.get(name);
    }

    @Override
    @Nonnull public List<Package> all() {
        return packages;
    }

    @Nonnull public Map<String, Package> getDataPackagesForBackUp() {
        return byId;
    }

    @Nonnull public Map<String, IMSPackage> getIMSPackagesForBackUp() {
        return imsById;
    }

    @Nonnull public Map<String, QuotaTopUp> getQuotaTopUpsForBackUp() {
        return quotaTopUpById;
    }

    @Nullable public List<Package> byName(String ...names) {

        List<String> requiredPackageNames = Arrays.asList(names);

        return packages.stream().filter(pkg -> requiredPackageNames.contains(pkg.getName())).collect(Collectors.toList());
    }

    @Nullable public List<Package> byId(String ...ids) {

        List<String> requiredPackageIds = Arrays.asList(ids);

        return packages.stream().filter(pkg -> requiredPackageIds.contains(pkg.getId())).collect(Collectors.toList());
    }

    @Nonnull public GlobalPolicyStore<EmergencyPackage> emergency() {
        return emergencyPolicyStore;
    }

    @Nonnull public DataPolicyStore<BasePackage> base() {
        return basePolicyStore;
    }

    @Nonnull public DataPolicyStore<AddOn> addOn() {
        return addOnPolicyStore;
    }

    @Nonnull public GlobalPolicyStore<PromotionalPackage> promotional() {
        return promotionalPolicyStore;
    }

    @Nonnull public PCCRuleStore pccRule() {
        return pccRuleStore;
    }

    @Nonnull public ChargingRuleBaseNameStore chargingRuleBaseName() {
        return chargingRuleBaseNameStore;
    }

    @Nonnull public IMSPolicyStore<IMSPackage> ims() {
        return imsPolicyStore;
    }


    @Nonnull public QuotaTopUpPolicyStore<QuotaTopUp> quotaTopUp() {
        return quotaTopUpPolicyStore;
    }

    @Nonnull public MonetaryRechargePlanPolicyStore<MonetaryRechargePlan> monetaryRechargePlan() {
        return monetaryRechargePlanPolicyStore;
    }
}
