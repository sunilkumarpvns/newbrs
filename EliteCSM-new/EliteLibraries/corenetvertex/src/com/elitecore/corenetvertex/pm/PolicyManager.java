package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.EntityType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.data.PackageType;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.monetaryrechargeplan.MonetaryRechargePlanData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.bod.BoDFactory;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.bod.BoDPackageFactory;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.offer.AddOnProductOfferFactory;
import com.elitecore.corenetvertex.pm.offer.BaseProductOfferFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates;
import com.elitecore.corenetvertex.pm.pkg.StaffGroupIMSPackageDataPredicate;
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
import com.elitecore.corenetvertex.pm.pkg.factory.AddOnFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.BasePackageFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.ChargingRuleBaseNameFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.DataMonetaryRateCardFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.DataPackageFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.DataServiceTypeFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.EmergencyPackageFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.GroupManageOrderFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.IMSBasePackageFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.IMSPackageFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.IMSServiceTableFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.MonetaryRechargePlanFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.PCCAttributeTableEntryFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.PCCRuleFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.PromotionalPackageFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.QoSProfileDetailFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.QoSProfileFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.QuotaNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.QuotaTopUpFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.RatingGroupFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.RncProfileFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.SyBasedQuotaProfileFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.UMBasedQuotaProfileFactory;
import com.elitecore.corenetvertex.pm.pkg.factory.UsageNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.corenetvertex.pm.sliceconfig.DataSliceConfiguration;
import com.elitecore.corenetvertex.pm.sliceconfig.SliceConfigurable;
import com.elitecore.corenetvertex.pm.store.BoDPackageStore;
import com.elitecore.corenetvertex.pm.store.ChargingRuleBaseNameStore;
import com.elitecore.corenetvertex.pm.store.DataPolicyStore;
import com.elitecore.corenetvertex.pm.store.GlobalPolicyStore;
import com.elitecore.corenetvertex.pm.store.IMSPolicyStore;
import com.elitecore.corenetvertex.pm.store.MonetaryRechargePlanPolicyStore;
import com.elitecore.corenetvertex.pm.store.ParentPolicyStore;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.store.QuotaTopUpPolicyStore;
import com.elitecore.corenetvertex.pm.store.RnCPackageStore;
import com.elitecore.corenetvertex.pm.store.ServiceStore;
import com.elitecore.corenetvertex.pm.util.PkgGroupOrderDataPredicates;
import com.elitecore.corenetvertex.pm.util.ProductOfferPredicates;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.LeakyBucket;
import com.elitecore.corenetvertex.util.SerializationUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.elitecore.commons.base.Collectionz.newArrayList;
import static com.elitecore.commons.logging.LogManager.getLogger;

public class PolicyManager implements PolicyRepository {

    private static final String MODULE = "PLC-MGR";
    private static final String SYSTEM_FOLDER_NAME = "system";
    private static final String POLICY_BKP_FILE_NAME = "policies.bkp";
    private static final String RNC_PKG_BKP_FILE_NAME = "rnc_packages.bkp";
    private static final String PRODUCT_OFFER_BKP_FILE_NAME = "product_offers.bkp";
    private static final String BOD_PKG_BKP_FILE_NAME = "bod_packages.bkp";

    private static final Predicate<UserPackage> QUOTA_POLICY_FILTER = pkg -> QuotaProfileType.SY_COUNTER_BASED != pkg.getQuotaProfileType();
    private static final Predicate<ResourceData> INACTIVE_DELETED_RESOURCE_FILTER = pkg -> (CommonConstants.STATUS_DELETED.equals(pkg.getStatus())
            || PkgStatus.INACTIVE.val.equalsIgnoreCase(pkg.getStatus()));
    public static final String POLICY_RELOADED_SUCCESSFULLY = "Policy reloaded successfully";
    public static final String FAIL_TO_RELOAD_POLICY_REASON = "Fail to reload policy. Reason: ";
    public static final String NOT_FOUND = "Not found";

    private static PolicyManager policyManager;

    private String policyBackupFilePath;
    private String rncPackagesBackupFilePath;
    private String productOfferBackupFilePath;
	private DeploymentMode deploymentMode;

    private ParentPolicyStore policyStore;
    private ServiceStore serviceStore;
    private ProductOfferStore<ProductOffer> productOfferStore;
    private RnCPackageStore rnCPackageStore;
    private SessionFactory sessionFactory;
    private PackageFactory packageFactory;
    private DataPackageFactory dataPackageFactory;
    private IMSPackageFactory imsPackageFactory;
    private QuotaTopUpFactory quotaTopUpFactory;
    private MonetaryRechargePlanFactory monetaryRechargePlanFactory;
    private BaseProductOfferFactory baseProductOfferFactory;
    private AddOnProductOfferFactory addOnProductOfferFactory;
    private RnCPackageFactory rnCPackageFactory;
    private SliceConfigurable sliceConfigurable;
    private Map<String, PolicyCacheDetail> pkgTypeWisePolicyStatistics = Maps.newLinkedHashMap();

    private LeakyBucket<PolicyCacheDetail> listHistory;

    private LeakyBucket<Long> policyReloadTime = new LeakyBucket<>(10);
    private DataReader hibernateDataReader;

    private BoDPackageFactory boDPackageFactory;
    private BoDPackageStore boDPackageStore;
    private String bodPackagesBackupFilePath;


    public PolicyManager() {
        //NO NEED TO IMPLEMENT
    }

    public static PolicyManager getInstance() {
        return policyManager;
    }

    static {
        policyManager = new PolicyManager();
    }

    @VisibleForTesting
    public static void setInstance(PolicyManager instance) {
        PolicyManager.policyManager = instance;
    }

    @VisibleForTesting
    public static void release() {
        PolicyManager.policyManager = null;
    }

    private void setPackageFactories() {
        UMBasedQuotaProfileFactory umBasedQuotaProfileFactory = new UMBasedQuotaProfileFactory(packageFactory);
        SyBasedQuotaProfileFactory syBasedQuotaProfileFactory = new SyBasedQuotaProfileFactory(packageFactory);
        RatingGroupFactory ratingGroupFactory = new RatingGroupFactory(packageFactory);
        DataServiceTypeFactory dataServiceTypeFactory = new DataServiceTypeFactory(ratingGroupFactory, packageFactory);
        RncProfileFactory rncProfileFactory = new RncProfileFactory(packageFactory, ratingGroupFactory, dataServiceTypeFactory);
        DataMonetaryRateCardFactory dataMonetaryRateCardFactory = new DataMonetaryRateCardFactory(packageFactory);
        PCCRuleFactory pccRuleFactory = new PCCRuleFactory(dataServiceTypeFactory, packageFactory);
        ChargingRuleBaseNameFactory chargingRuleBaseNameFactory = new ChargingRuleBaseNameFactory(dataServiceTypeFactory, packageFactory);
        QoSProfileDetailFactory qoSProfileDetailFactory = new QoSProfileDetailFactory(pccRuleFactory, chargingRuleBaseNameFactory, packageFactory);
        QoSProfileFactory qoSProfileFactory = new QoSProfileFactory(umBasedQuotaProfileFactory, syBasedQuotaProfileFactory, qoSProfileDetailFactory, packageFactory, rncProfileFactory, dataMonetaryRateCardFactory);
        UsageNotificationSchemeFactory usageNotificationSchemeFactory = new UsageNotificationSchemeFactory(packageFactory);
        QuotaNotificationSchemeFactory quotaNotificationSchemeFactory = new QuotaNotificationSchemeFactory(packageFactory);
        AddOnFactory addOnFactory = new AddOnFactory(qoSProfileFactory, usageNotificationSchemeFactory,
				packageFactory, quotaNotificationSchemeFactory, deploymentMode);
        BasePackageFactory basePackageFactory = new BasePackageFactory(qoSProfileFactory, usageNotificationSchemeFactory,
				packageFactory, quotaNotificationSchemeFactory, deploymentMode);
        EmergencyPackageFactory emergencyPackageFactory = new EmergencyPackageFactory(qoSProfileFactory, packageFactory);
        PromotionalPackageFactory promotionalPackageFactory = new PromotionalPackageFactory(qoSProfileFactory,
				usageNotificationSchemeFactory, packageFactory, deploymentMode);

        PCCAttributeTableEntryFactory pccAttributeTableEntryFactory = new PCCAttributeTableEntryFactory(packageFactory);
        IMSServiceTableFactory imsServiceTableFactory = new IMSServiceTableFactory(packageFactory, pccAttributeTableEntryFactory);
        IMSBasePackageFactory imsBasePackageFactory = new IMSBasePackageFactory(packageFactory, imsServiceTableFactory);

        this.dataPackageFactory = new DataPackageFactory(addOnFactory, basePackageFactory, emergencyPackageFactory, promotionalPackageFactory, packageFactory);
        this.imsPackageFactory = new IMSPackageFactory(imsBasePackageFactory);
        this.quotaTopUpFactory = new QuotaTopUpFactory(quotaNotificationSchemeFactory, packageFactory,deploymentMode);
        this.monetaryRechargePlanFactory = new MonetaryRechargePlanFactory();
        this.baseProductOfferFactory = new BaseProductOfferFactory(this);
        this.addOnProductOfferFactory = new AddOnProductOfferFactory(this);
    }
    public synchronized void init(String serverHome,
                                  SessionFactory sessionFactory,
                                  DataReader hibernateDataReader,
                                  PackageFactory packageFactory,
                                  RnCPackageFactory rnCPackageFactory,
                                  DeploymentMode deploymentMode) throws InitializationFailedException {
        this.packageFactory = packageFactory;
        this.rnCPackageFactory = rnCPackageFactory;
        this.boDPackageFactory = new BoDPackageFactory(new BoDFactory(),  deploymentMode);
        this.policyBackupFilePath = serverHome + File.separator + SYSTEM_FOLDER_NAME + File.separator + POLICY_BKP_FILE_NAME;
        this.rncPackagesBackupFilePath = serverHome + File.separator + SYSTEM_FOLDER_NAME + File.separator + RNC_PKG_BKP_FILE_NAME;
        this.productOfferBackupFilePath = serverHome + File.separator + SYSTEM_FOLDER_NAME + File.separator + PRODUCT_OFFER_BKP_FILE_NAME;
        this.bodPackagesBackupFilePath = serverHome + File.separator + SYSTEM_FOLDER_NAME + File.separator + BOD_PKG_BKP_FILE_NAME;
        this.deploymentMode = deploymentMode;
		this.listHistory = new LeakyBucket<>(10);
        setPackageFactories();
        this.sessionFactory = sessionFactory;
        this.serviceStore = new ServiceStore();
        this.productOfferStore = new ProductOfferStore<>();
        this.rnCPackageStore = new RnCPackageStore();
        this.hibernateDataReader = hibernateDataReader;
        this.sliceConfigurable = new SliceConfigurable(this.sessionFactory, this.hibernateDataReader);
        this.boDPackageStore = new BoDPackageStore();

        getLogger().info(MODULE, "Initializing Policy Manager");
        readServices();
        getLogger().info(MODULE, "Service reloaded successfully");

        try {
            this.sliceConfigurable.readSliceConfiguration();
        } catch (LoadConfigurationException e) {
            getLogger().error(MODULE, "Fail to read slice configuration. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        Map<String, Package> backUpDataPolicy = Maps.newHashMap();
        Map<String, IMSPackage> backUpIMSPolicy = Maps.newHashMap();
        Map<String, QuotaTopUp> backUpQuotaTopUpPolicy = Maps.newHashMap();
        Map<String, MonetaryRechargePlan> backUpMonetaryRechargePlanPolicy = Maps.newHashMap();

        PolicyBackUpData backUpDataPackage = readBackUpPackages();
        boolean useBackUp = true;
        if (isDeploymentModeIsChanged(deploymentMode, backUpDataPackage)) {
            useBackUp = false;
        }

        if (useBackUp && backUpDataPackage != null) {
            backUpDataPolicy = backUpDataPackage.getPackageById();
            backUpIMSPolicy = backUpDataPackage.getImsPackageById();
        }

        this.policyStore = new ParentPolicyStore(backUpDataPolicy, backUpIMSPolicy, backUpQuotaTopUpPolicy, backUpMonetaryRechargePlanPolicy);

        List<RnCPackage> rnCPackages = new ArrayList<>();
        if (useBackUp) {
            rnCPackages = readBackUpRnCPackages();
        }

        rnCPackageStore.create(rnCPackages, new ArrayList<>());
        readConfiguredPackages();

        reloadRnCPackages(rncPackageData-> true);

        //----------------------
        List<BoDPackage> backupBoDPackages = new ArrayList<>();
        if(useBackUp){
            backupBoDPackages = readBackUpBoDPackages();
        }

        boDPackageStore.create(backupBoDPackages, new ArrayList<>());

        reloadBoDPackages(boDData -> true);

        //---------------------

		List<ProductOffer> backUpProductOffers = new ArrayList<>();
		if (useBackUp) {
			backUpProductOffers = readBackUpProductOffers();
		}
        productOfferStore.create(backUpProductOffers, new ArrayList<>());
        reloadProductOffers(productOfferData -> true);

        writePackagesForBackup();

        writeRnCPackagesForBackup();

        writeProductOffersForBackup();

        getLogger().info(MODULE, "Policy manager initialized successfully");
    }

	private boolean isDeploymentModeIsChanged(DeploymentMode deploymentMode, PolicyBackUpData backUpDataPackage) {
		return backUpDataPackage != null && backUpDataPackage.getDeploymentMode() != deploymentMode;
	}

	private void writePackagesForBackup() {

        getLogger().info(MODULE, "Policy backup generation started");

        try {
            SerializationUtil.serialize(new PolicyBackUpData(policyStore.getDataPackagesForBackUp(),
                    policyStore.getIMSPackagesForBackUp(), policyStore.getQuotaTopUpsForBackUp(), deploymentMode), policyBackupFilePath);
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while writing to Policy backup file. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
        getLogger().info(MODULE, "Policy backup generation completed");
    }

    private void writeRnCPackagesForBackup() {

        getLogger().info(MODULE, "RnC Package backup generation started");

        try {
            SerializationUtil.serialize(rnCPackageStore.all(), rncPackagesBackupFilePath);
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while writing to RnC Package backup file. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
        getLogger().info(MODULE, "RnC Package backup generation completed");
    }

    private void writeProductOffersForBackup() {

        getLogger().info(MODULE, "Product Offer backup generation started");

        try {
            SerializationUtil.serialize(productOfferStore.all(), productOfferBackupFilePath);
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while writing to Product Offer backup file. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
        getLogger().info(MODULE, "Product Offer backup generation completed");
    }

    @Nullable
    private PolicyBackUpData readBackUpPackages() {

        getLogger().info(MODULE, "Reading of backUp packages started");

        File backupFile = new File(policyBackupFilePath);
        if (backupFile.exists() == false || backupFile.isDirectory()) {
            getLogger().info(MODULE, "Policy backup file not available.");
            return null;
        }

        try {
            return (PolicyBackUpData) SerializationUtil.deserialize(backupFile.getAbsolutePath());
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while reading policy backup file. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        getLogger().info(MODULE, "Reading of backUp packages completed");
        return null;
    }

    @Nullable
    private List<RnCPackage> readBackUpRnCPackages() {

        List<RnCPackage> backedUpPackages = new ArrayList<>();
        getLogger().info(MODULE, "Reading of backUp RnC packages started");

        File backupFile = new File(rncPackagesBackupFilePath);
        if (backupFile.exists() == false || backupFile.isDirectory()) {
            getLogger().info(MODULE, "RnC package backup file not available.");
            return backedUpPackages;
        }

        try {
            backedUpPackages.addAll((List<RnCPackage>) SerializationUtil.deserialize(backupFile.getAbsolutePath()));
            return backedUpPackages;
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while reading rnc package backup file. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        getLogger().info(MODULE, "Reading of backUp rnc packages completed");
        return backedUpPackages;
    }

    private List<ProductOffer> readBackUpProductOffers() {

        List<ProductOffer> backedUpPackages = new ArrayList<>();
        getLogger().info(MODULE, "Reading of backUp Product Offer started");

        File backupFile = new File(productOfferBackupFilePath);
        if (backupFile.exists() == false || backupFile.isDirectory()) {
            getLogger().info(MODULE, "Product Offer backup file not available.");
            return backedUpPackages;
        }

        try {
            backedUpPackages.addAll((List<ProductOffer>) SerializationUtil.deserialize(backupFile.getAbsolutePath()));
            return backedUpPackages;
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while reading Product Offer backup file. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        getLogger().info(MODULE, "Reading of backUp Product Offer  completed");
        return backedUpPackages;
    }

    private void readConfiguredPackages() throws InitializationFailedException {

        Session session = null;
        try {
            getLogger().info(MODULE, "Reading from hibernate started");

            session = sessionFactory.openSession();
            List<PkgData> pkgDatas = hibernateDataReader.readAll(PkgData.class, session);
            List<IMSPkgData> imsPkgDatas = hibernateDataReader.readAll(IMSPkgData.class, session);
            List<DataTopUpData> dataTopUpDatas = hibernateDataReader.readAll(DataTopUpData.class, session);
            List<MonetaryRechargePlanData> monetaryRechargePlanDatas = hibernateDataReader.readAll(MonetaryRechargePlanData.class, session);

            List<RatingGroupData> ratingGroupDatas = hibernateDataReader.readAll(RatingGroupData.class, session);
            List<PkgGroupOrderData> pkgGroupOrderDatas = hibernateDataReader.readAll(PkgGroupOrderData.class, session);

            getLogger().info(MODULE, "Reading from hibernate completed");

            // Collect Deleted And Inactive Packages to remove existing cache
            List<String> deletedOrInActiveDataPackages = pkgDatas.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(ResourceData::getId)
                    .collect(Collectors.toList());

            Collectionz.filter(pkgDatas, DataPolicyFilter.instance());

            // Collect Deleted And Inactive Packages to remove existing cache
            List<String> deletedOrInActiveImsPackages = imsPkgDatas.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(IMSPkgData::getId)
                    .collect(Collectors.toList());

            List<String> deletedOrInActiveQuotaTopUps = dataTopUpDatas.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(DataTopUpData::getId)
                    .collect(Collectors.toList());

            List<String> deletedInactivemonetaryRechargePlans = monetaryRechargePlanDatas.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(MonetaryRechargePlanData::getId)
                    .collect(Collectors.toList());

            Collectionz.filter(imsPkgDatas, IMSPolicyFilter.instance());

            Collectionz.filter(dataTopUpDatas, QuotaTopUpPolicyFilter.instance());

            List<Package> createdPackages = dataPackageFactory.create(pkgDatas, ratingGroupDatas);

            List<GroupManageOrder> groupManageOrders = GroupManageOrderFactory.create(pkgGroupOrderDatas);

            List<IMSPackage> imsPackages = imsPackageFactory.create(imsPkgDatas);

            List<QuotaTopUp> quotaTopUps = quotaTopUpFactory.create(dataTopUpDatas);

            List<MonetaryRechargePlan> monetaryRechargePlans = monetaryRechargePlanFactory.create(monetaryRechargePlanDatas);

            populatePurgedDataPackagesInDeletedIds(createdPackages, deletedOrInActiveDataPackages, policyStore.all());
            populatePurgedImsPackagesInDeletedIds(imsPackages, deletedOrInActiveImsPackages, policyStore.ims().all());
            populatePurgedQuotaTopUpPackagesInDeletedIds(quotaTopUps, deletedOrInActiveQuotaTopUps, policyStore.quotaTopUp().all());
            populatePurgedMonetaryRechargePlansInDeletedIds(monetaryRechargePlans, deletedInactivemonetaryRechargePlans, policyStore.monetaryRechargePlan().all());

            this.policyStore.create(createdPackages, groupManageOrders, deletedOrInActiveDataPackages, imsPackages, deletedOrInActiveImsPackages,
                    quotaTopUps, deletedOrInActiveQuotaTopUps, monetaryRechargePlans, deletedInactivemonetaryRechargePlans);

        } catch (Exception e) {
            throw new InitializationFailedException("Error while initialising Policy Manager. Reason: " + e.getMessage(), e);
        } finally {
            closeQuietly(session);
        }
    }

    private void populatePurgedDataRnCPackagesInDeletedIds(List<RncPackageData> packagesFromNewCache, List<String> deletedOrInActiveDataPackages, List<RnCPackage> cachedPackages) {
        Set<String> packageIdsFromNewCache = packagesFromNewCache.stream().map(RncPackageData::getId).collect(Collectors.toSet());

        cachedPackages.stream()
                .filter(pkg -> packageIdsFromNewCache.contains(pkg.getId()) == false)
                .forEach(pkg-> deletedOrInActiveDataPackages.add(pkg.getId()));
    }

    ///TODO Combine below three methods into 1, use ID list only
    private void populatePurgedQuotaTopUpPackagesInDeletedIds(List<QuotaTopUp> packagesFromNewCache, List<String> deletedOrInActiveQuotaTopUps, List<QuotaTopUp> cachedPackages) {
        Set<String> packageIdsFromNewCache = packagesFromNewCache.stream().map(QuotaTopUp::getId).collect(Collectors.toSet());
        cachedPackages.stream()
                .filter(pkg -> packageIdsFromNewCache.contains(pkg.getId()) == false)   // If not exist in newPackages
                .forEach(pkg-> deletedOrInActiveQuotaTopUps.add(pkg.getId()));  // add in deletedIds list
    }

    private void populatePurgedMonetaryRechargePlansInDeletedIds(List<MonetaryRechargePlan> packagesFromNewCache, List<String> deletedOrInActiveMonetaryRechargePlans, List<MonetaryRechargePlan> cachedPackages) {
        Set<String> packageIdsFromNewCache = packagesFromNewCache.stream().map(MonetaryRechargePlan::getId).collect(Collectors.toSet());
        cachedPackages.stream()
                .filter(pkg -> packageIdsFromNewCache.contains(pkg.getId()) == false)   // If not exist in newPackages
                .forEach(pkg-> deletedOrInActiveMonetaryRechargePlans.add(pkg.getId()));  // add in deletedIds list
    }

    private void populatePurgedImsPackagesInDeletedIds(List<IMSPackage> packagesFromNewCache, List<String> deletedOrInActiveImsPackages, List<IMSPackage> cachedPackages) {
        Set<String> packageIdsFromNewCache = packagesFromNewCache.stream().map(IMSPackage::getId).collect(Collectors.toSet());
        cachedPackages.stream()
                .filter(pkg -> packageIdsFromNewCache.contains(pkg.getId()) == false)
                .forEach(pkg-> deletedOrInActiveImsPackages.add(pkg.getId()));
    }


    private void populatePurgedDataPackagesInDeletedIds(List<Package> packagesFromNewCache, List<String> deletedOrInActiveDataPackages, List<Package> cachedPackages) {
        Set<String> packageIdsFromNewCache = packagesFromNewCache.stream().map(Package::getId).collect(Collectors.toSet());

        cachedPackages.stream()
                .filter(pkg -> packageIdsFromNewCache.contains(pkg.getId()) == false)
                .forEach(pkg-> deletedOrInActiveDataPackages.add(pkg.getId()));
    }

    public void readServices(){
        Session session = null;
        try {
            getLogger().info(MODULE, "Reading from hibernate started");

            session = sessionFactory.openSession();

            List<ServiceData> serviceData = hibernateDataReader.readAll(ServiceData.class, session);

            List<Service> activeServices = new ArrayList<>(serviceData.size());

            serviceData.stream()
                    .filter(service -> (CommonConstants.STATUS_ACTIVE.equals(service.getStatus())))
                    .forEach(data -> activeServices.add(new Service(data.getId(), data.getName(), PkgStatus.fromVal(data.getStatus()))));

            this.serviceStore.create(activeServices);

        } catch (HibernateException e) {
            getLogger().error(MODULE, "Fail to read service details. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        } finally {
            closeQuietly(session);
        }
    }

    private void closeQuietly(Session session) {

        if (session == null) {
            return;
        }

        if (session.isOpen() == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Previous session is already closed.");
            }
            return;
        }

        try {
            session.close();
        } catch (Exception ex) {
            getLogger().error(MODULE, "Error while closing previous session. Reason:" + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }
    }

    @Override
    public AddOn getAddOnById(String id) {
        return addOn().byId(id);
    }

    @Override
    public AddOn getAddOnByName(String name) {
        return addOn().byName(name);
    }

    @Override
    public AddOn getActiveAddOnById(String id) {
        return addOn().active().byId(id);
    }

    @Override
    public List<AddOn> getActiveLiveAddOnDatas() {
        return addOn().active().all();
    }

    private DataPolicyStore<AddOn> addOn() {
        return policyStore.addOn();
    }

    @Override
    public QuotaTopUpPolicyStore<QuotaTopUp> quotaTopUp() {
        return policyStore.quotaTopUp();
    }

    @Override
    public MonetaryRechargePlanPolicyStore<MonetaryRechargePlan> monetaryRechargePlan() {
        return policyStore.monetaryRechargePlan();
    }

    @Override
    public BasePackage getBasePackageDataById(String id) {
        return base().byId(id);
    }

    @Override
    public BasePackage getBasePackageDataByName(String name) {
        return base().byName(name);
    }

    @Override
    public BasePackage getActiveBasePackageById(String id) {
        return base().active().byId(id);
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
    public BasePackage getActiveBasePackageByName(String name) {
        return base().active().byName(name);
    }

    @Override
    public DataPolicyStore<BasePackage> base() {
        return policyStore.base();
    }

    @Override
    public List<BasePackage> getActiveLiveBasePkgDatas() {
        return base().active().live().all();
    }

    @Override
    public List<BasePackage> getActiveAllBasePkgDatas() {
        return base().active().all();
    }

    @Override
    public List<Package> getPkgDatasByName(String... packageNames) {
        return policyStore.byName(packageNames);
    }

    @Override
    public UserPackage getPkgDataByName(String pkgName) {
        return policyStore.byName(pkgName);
    }

    @Override
    public UserPackage getPkgDataById(String pkgId) {
        return policyStore.byId(pkgId);
    }

    @Override
    public List<Package> getAllPackageDatas() {
        return policyStore.all();
    }

    @Override
    public DataSliceConfiguration getSliceConfiguration() { return sliceConfigurable.getConfiguration(); }

    @Override
    public void reloadSliceConfiguration() throws LoadConfigurationException {
        getLogger().info(MODULE, "Reloading Slice Configuration");
        sliceConfigurable.readSliceConfiguration();
        getLogger().info(MODULE, "Slice Configuration reloaded successfully");
    }

    @Override
    public List<IMSPackage> getIMSPackageByName(String... packageNames) {

        List<IMSPackage> imsPackages = newArrayList();

        for (String packageName : packageNames) {
            IMSPackage imsPackage = ims().byName(packageName);
            if (imsPackage != null) {
                imsPackages.add(imsPackage);
            }
        }

        return imsPackages;
    }

    @Override
    public IMSPackage getIMSPkgById(String pkgId) {
        return ims().byId(pkgId);
    }

    private IMSPolicyStore<IMSPackage> ims() {
        return policyStore.ims();
    }

    @Override
    public IMSPackage getIMSPkgByName(String pkgName) {
        return ims().byName(pkgName);
    }

    @Override
    public List<EmergencyPackage> getEmergencyPackages() {
        return emergency().all();
    }

    private GlobalPolicyStore<EmergencyPackage> emergency() {
        return policyStore.emergency();
    }

    @Override
    public EmergencyPackage getEmergencyPackagebyId(String id) {
        return emergency().byId(id);
    }

    @Override
    public EmergencyPackage getEmergencyPackageByName(String name) {
        return emergency().byName(name);
    }

    @Override
    public PCCRule getPccRule(String pccRuleName) {
        return policyStore.pccRule().byName(pccRuleName);
    }

    @Override
    public PCCRule getPCCRuleById(String pccRuleId) {
        return policyStore.pccRule().byId(pccRuleId);
    }

    @Override
    public ChargingRuleBaseName getChargingRuleBaseNameByName(String chargingRuleBaseName) {
        return chargingRuleBaseNameStore().byName(chargingRuleBaseName);
    }

    private ChargingRuleBaseNameStore chargingRuleBaseNameStore() {
        return policyStore.chargingRuleBaseName();
    }

    @Override
    public ChargingRuleBaseName getChargingRuleBaseNameById(String chargingRuleBaseNameId) {
        return chargingRuleBaseNameStore().byId(chargingRuleBaseNameId);
    }

    @Override
    public IMSPackage getActiveIMSPackageByName(String name) {
        return ims().active().byName(name);
    }

    @Override
    public List<IMSPackage> getActiveLiveImsPkgDatas() {
        return ims().active().live().all();
    }

    @Override
    public List<QuotaTopUp> getActiveLiveQuotaTopUpDatas() {
        return quotaTopUp().active().live().all();
    }

    @Override
    public List<IMSPackage> getActiveAllImsPkgDatas() {
        return ims().active().all();
    }

    @Override
    public List<ProductOffer> getActiveAllBaseProductOffers() {
        return getProductOffer().base().active().all();
    }

    @Override
    public List<ProductOffer> getActiveLiveBaseProductOffers() {
        return getProductOffer().base().active().live().all();
    }

    @Override
    public List<ProductOffer> getActiveAllAddOnProductOffers() {
        return getProductOffer().addOn().active().all();
    }

    @Override
    public List<ProductOffer> getActiveLiveAddOnProductOffers() {
        return getProductOffer().addOn().active().live().all();
    }

    @Override
    public List<QuotaTopUp> getActiveAllQuotaTopUpDatas() {
        return quotaTopUp().active().all();
    }


    @Override
    public SubscriptionPackage getActiveAddOnByName(String name) {
        return addOn().active().byName(name);
    }

    @Override
    public List<AddOn> getActiveAllAddOnPkgDatas(Boolean syInterface) {

        if (syInterface) {
            return addOn().active().all();
        }

        //give all policy except SY_BASED
        return addOn().active().all()
                .stream()
                .filter(QUOTA_POLICY_FILTER)
                .collect(Collectors.toList());
    }

    @Override
    public List<AddOn> getActiveLiveAddOnDatas(Boolean syInterface) {

        if (syInterface) {
            return addOn().active().live().all();
        }

        //give all policy except SY_BASED
        return addOn().active().live().all()
                .stream()
                .filter(QUOTA_POLICY_FILTER)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllDataPackageNames() {
        return policyStore.all().stream().map(Package::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllIMSPackageNames() {
        return ims().all().stream().map(IMSPackage::getName).collect(Collectors.toList());
    }

    @Override
    public synchronized PolicyCacheDetail reload() {
        getLogger().info(MODULE, "Reloading Policy Manager");

        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();
        try {

            readServices();
            getLogger().info(MODULE, "Service reloaded successfully");
            readConfiguredPackages();

            //TODO use filters here
            for (BasePackage basePkg : base().all()) {

                if (basePkg.getStatus() == PolicyStatus.SUCCESS) {
                    successCounter++;
                    successPolicyList.add(new PolicyDetail(basePkg.getId(), basePkg.getName(), PolicyStatus.SUCCESS, PackageType.BASE_DATA_PACKAGE.getSubType(), EntityType.DATA.getValue(), basePkg.getMode(), basePkg.getFailReason()));
                } else if (basePkg.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                    partialSuccessCounter++;
                    partialSuccessPolicyList.add(new PolicyDetail(basePkg.getId(), basePkg.getName(), PolicyStatus.PARTIAL_SUCCESS, PackageType.BASE_DATA_PACKAGE.getSubType(), EntityType.DATA.getValue(), basePkg.getMode(), basePkg.getPartialFailReason()));
                } else if (basePkg.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                    lastKnownGoodCounter++;
                    lastKnownGoodPolicyList.add(new PolicyDetail(basePkg.getId(), basePkg.getName(), PolicyStatus.LAST_KNOWN_GOOD, PackageType.BASE_DATA_PACKAGE.getSubType(), EntityType.DATA.getValue(), basePkg.getMode(), basePkg.getPartialFailReason()));
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail(basePkg.getId(), basePkg.getName(), PolicyStatus.FAILURE, PackageType.BASE_DATA_PACKAGE.getSubType(), EntityType.DATA.getValue(), basePkg
                            .getMode(), basePkg.getFailReason()));
                }
            }

            for (AddOn addOn : addOn().all()) {

                if (addOn.getStatus() == PolicyStatus.SUCCESS) {
                    successCounter++;
                    successPolicyList.add(new PolicyDetail(addOn.getId(), addOn.getName(), PolicyStatus.SUCCESS, PackageType.ADD_ON_DATA_PACKAGE.getSubType(), EntityType.DATA.getValue(), addOn.getMode(), addOn.getFailReason()));
                } else if (addOn.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                    partialSuccessCounter++;
                    partialSuccessPolicyList.add(new PolicyDetail(addOn.getId(), addOn.getName(), PolicyStatus.PARTIAL_SUCCESS, PackageType.ADD_ON_DATA_PACKAGE.getSubType(), EntityType.DATA.getValue(),
                            addOn.getMode(), addOn.getPartialFailReason()));
                } else if (addOn.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                    lastKnownGoodCounter++;
                    lastKnownGoodPolicyList.add(new PolicyDetail(addOn.getId(), addOn.getName(), PolicyStatus.LAST_KNOWN_GOOD, PackageType.ADD_ON_DATA_PACKAGE.getSubType(), EntityType.DATA.getValue(),
                            addOn.getMode(), addOn.getPartialFailReason()));
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail(addOn.getId(), addOn.getName(), PolicyStatus.FAILURE, PackageType.ADD_ON_DATA_PACKAGE.getSubType(), EntityType.DATA.getValue(),
                            addOn.getMode(), addOn.getFailReason()));
                }
            }

            //TODO restructure to scale code according to PkgType
            for (EmergencyPackage emergencyPkgData : getEmergencyPackages()) {

                if (emergencyPkgData.getStatus() == PolicyStatus.SUCCESS) {
                    successCounter++;
                    successPolicyList.add(new PolicyDetail(emergencyPkgData.getId(), emergencyPkgData.getName(), PolicyStatus.SUCCESS,
                            PackageType.EMERGENCY.getSubType(), EntityType.DATA.getValue(), emergencyPkgData.getMode(), emergencyPkgData.getFailReason()));
                } else if (emergencyPkgData.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                    partialSuccessCounter++;
                    partialSuccessPolicyList.add(new PolicyDetail(emergencyPkgData.getId(), emergencyPkgData.getName(), PolicyStatus.PARTIAL_SUCCESS,
                            PackageType.EMERGENCY.getSubType(), EntityType.DATA.getValue(), emergencyPkgData.getMode(), emergencyPkgData.getPartialFailReason()));
                } else if (emergencyPkgData.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                    lastKnownGoodCounter++;
                    lastKnownGoodPolicyList.add(new PolicyDetail(emergencyPkgData.getId(), emergencyPkgData.getName(), PolicyStatus.LAST_KNOWN_GOOD,
                            PackageType.EMERGENCY.getSubType(), EntityType.DATA.getValue(), emergencyPkgData.getMode(), emergencyPkgData.getPartialFailReason()));
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail(emergencyPkgData.getId(), emergencyPkgData.getName(), PolicyStatus.FAILURE,
                            PackageType.EMERGENCY.getSubType(), EntityType.DATA.getValue(), emergencyPkgData.getMode(), emergencyPkgData.getFailReason()));
                }
            }

            for (PromotionalPackage promotionalPackage : getPromotionalPackages()) {

                if (promotionalPackage.getStatus() == PolicyStatus.SUCCESS) {
                    successCounter++;
                    successPolicyList.add(new PolicyDetail(promotionalPackage.getId(), promotionalPackage.getName(), PolicyStatus.SUCCESS,
                            PackageType.PROMOTIONAL.getSubType(), EntityType.DATA.getValue(), promotionalPackage.getMode(), promotionalPackage.getFailReason()));
                } else if (promotionalPackage.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                    partialSuccessCounter++;
                    partialSuccessPolicyList.add(new PolicyDetail(promotionalPackage.getId(), promotionalPackage.getName(), PolicyStatus.PARTIAL_SUCCESS,
                            PackageType.PROMOTIONAL.getSubType(), EntityType.DATA.getValue(), promotionalPackage.getMode(), promotionalPackage.getPartialFailReason()));
                } else if (promotionalPackage.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                    lastKnownGoodCounter++;
                    lastKnownGoodPolicyList.add(new PolicyDetail(promotionalPackage.getId(), promotionalPackage.getName(), PolicyStatus.LAST_KNOWN_GOOD,
                            PackageType.PROMOTIONAL.getSubType(), EntityType.DATA.getValue(), promotionalPackage.getMode(), promotionalPackage.getPartialFailReason()));
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail(promotionalPackage.getId(), promotionalPackage.getName(), PolicyStatus.FAILURE,
                            PackageType.PROMOTIONAL.getSubType(), EntityType.DATA.getValue(), promotionalPackage.getMode(), promotionalPackage.getFailReason()));
                }
            }

            for (IMSPackage imsPkgData : ims().all()) {

                if (imsPkgData.getStatus() == PolicyStatus.SUCCESS) {
                    successCounter++;
                    successPolicyList.add(new PolicyDetail(imsPkgData.getId(), imsPkgData.getName(), PolicyStatus.SUCCESS,
                            EntityType.IMS.name(),EntityType.IMS.getValue(),
                            imsPkgData.getMode(), imsPkgData.getFailReason()));
                } else if (imsPkgData.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                    partialSuccessCounter++;
                    partialSuccessPolicyList.add(new PolicyDetail(imsPkgData.getId(), imsPkgData.getName(), PolicyStatus.PARTIAL_SUCCESS,
                            EntityType.IMS.name(),EntityType.IMS.getValue(),
                            imsPkgData.getMode(), imsPkgData.getPartialFailReason()));
                } else if (imsPkgData.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                    lastKnownGoodCounter++;
                    lastKnownGoodPolicyList.add(new PolicyDetail(imsPkgData.getId(), imsPkgData.getName(), PolicyStatus.LAST_KNOWN_GOOD,
                            EntityType.IMS.name(),EntityType.IMS.getValue(),
                            imsPkgData.getMode(), imsPkgData.getPartialFailReason()));
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail(imsPkgData.getId(), imsPkgData.getName(), PolicyStatus.FAILURE, EntityType.IMS.name(),EntityType.IMS.getValue(), imsPkgData
                            .getMode(), imsPkgData.getFailReason()));
                }
            }

            for (MonetaryRechargePlan monetaryRechargePlan : monetaryRechargePlan().all()) {

                if (monetaryRechargePlan.getStatus() == PolicyStatus.SUCCESS) {
                    successCounter++;
                    successPolicyList.add(new PolicyDetail(monetaryRechargePlan.getId(), monetaryRechargePlan.getName(), PolicyStatus.SUCCESS,
                            EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(),
                            monetaryRechargePlan.getMode(), monetaryRechargePlan.getFailReason()));
                } else if (monetaryRechargePlan.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                    partialSuccessCounter++;
                    partialSuccessPolicyList.add(new PolicyDetail(monetaryRechargePlan.getId(), monetaryRechargePlan.getName(), PolicyStatus.PARTIAL_SUCCESS,
                            EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(),
                            monetaryRechargePlan.getMode(), monetaryRechargePlan.getPartialFailReason()));
                } else if (monetaryRechargePlan.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                    lastKnownGoodCounter++;
                    lastKnownGoodPolicyList.add(new PolicyDetail(monetaryRechargePlan.getId(), monetaryRechargePlan.getName(), PolicyStatus.LAST_KNOWN_GOOD,
                            EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(),
                            monetaryRechargePlan.getMode(), monetaryRechargePlan.getPartialFailReason()));
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail(monetaryRechargePlan.getId(), monetaryRechargePlan.getName(), PolicyStatus.FAILURE, EntityType.MONETARYRECHARGEPLAN.name(),
                            EntityType.MONETARYRECHARGEPLAN.getValue(), monetaryRechargePlan
                            .getMode(), monetaryRechargePlan.getFailReason()));
                }
            }

            for (QuotaTopUp quotaTopUp : quotaTopUp().all()) {

                if (quotaTopUp.getStatus() == PolicyStatus.SUCCESS) {
                    successCounter++;
                    successPolicyList.add(new PolicyDetail(quotaTopUp.getId(), quotaTopUp.getName(), PolicyStatus.SUCCESS,
                            quotaTopUp.getPackageType().name(), EntityType.TOPUP.getValue(),
                            quotaTopUp.getMode(), quotaTopUp.getFailReason()));
                } else if (quotaTopUp.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                    partialSuccessCounter++;
                    partialSuccessPolicyList.add(new PolicyDetail(quotaTopUp.getId(), quotaTopUp.getName(), PolicyStatus.PARTIAL_SUCCESS,
                            quotaTopUp.getPackageType().name(), EntityType.TOPUP.getValue(),
                            quotaTopUp.getMode(), quotaTopUp.getPartialFailReason()));
                } else if (quotaTopUp.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                    lastKnownGoodCounter++;
                    lastKnownGoodPolicyList.add(new PolicyDetail(quotaTopUp.getId(), quotaTopUp.getName(), PolicyStatus.LAST_KNOWN_GOOD,
                            quotaTopUp.getPackageType().name(), EntityType.TOPUP.getValue(),
                            quotaTopUp.getMode(), quotaTopUp.getPartialFailReason()));
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail(quotaTopUp.getId(), quotaTopUp.getName(), PolicyStatus.FAILURE, quotaTopUp.getPackageType().name(), EntityType.TOPUP.getValue(), quotaTopUp
                            .getMode(), quotaTopUp.getFailReason()));
                }
            }


            PolicyCacheDetail rncCacheDetail = reloadRnCPackages(rncPackageData->true);

            successCounter+=rncCacheDetail.getSuccessCounter();
            successPolicyList.addAll(rncCacheDetail.getSuccessPolicyList());
            partialSuccessCounter+=rncCacheDetail.getPartialSuccessCounter();
            partialSuccessPolicyList.addAll(rncCacheDetail.getPartialSuccessPolicyList());
            failureCounter+=rncCacheDetail.getFailureCounter();
            failPolicyList.addAll(rncCacheDetail.getFailurePolicyList());
            lastKnownGoodCounter+=rncCacheDetail.getLastKnownGoodCounter();
            lastKnownGoodPolicyList.addAll(rncCacheDetail.getLastKnownGoodPolicyList());


            PolicyCacheDetail bodCacheDetail = reloadBoDPackages(bodData->true);
            successCounter+=bodCacheDetail.getSuccessCounter();
            successPolicyList.addAll(bodCacheDetail.getSuccessPolicyList());
            partialSuccessCounter+=bodCacheDetail.getPartialSuccessCounter();
            partialSuccessPolicyList.addAll(bodCacheDetail.getPartialSuccessPolicyList());
            failureCounter+=bodCacheDetail.getFailureCounter();
            failPolicyList.addAll(bodCacheDetail.getFailurePolicyList());
            lastKnownGoodCounter+=bodCacheDetail.getLastKnownGoodCounter();
            lastKnownGoodPolicyList.addAll(bodCacheDetail.getLastKnownGoodPolicyList());


            PolicyCacheDetail offerCacheDetail = reloadProductOffers(productOfferData->true);

            successCounter+=offerCacheDetail.getSuccessCounter();
            successPolicyList.addAll(offerCacheDetail.getSuccessPolicyList());
            partialSuccessCounter+=offerCacheDetail.getPartialSuccessCounter();
            partialSuccessPolicyList.addAll(offerCacheDetail.getPartialSuccessPolicyList());
            failureCounter+=offerCacheDetail.getFailureCounter();
            failPolicyList.addAll(offerCacheDetail.getFailurePolicyList());
            lastKnownGoodCounter+=offerCacheDetail.getLastKnownGoodCounter();
            lastKnownGoodPolicyList.addAll(offerCacheDetail.getLastKnownGoodPolicyList());

            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                    withFailurePolicyList(failPolicyList).
                    withPartialSuccessPolicyList(partialSuccessPolicyList).
                    withPartialSuccessCounter(partialSuccessCounter).
                    withSuccessPolicyList(successPolicyList).
                    withSuccessCounter(successCounter).
                    withLastKnownGoodCounter(lastKnownGoodCounter).
                    withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();

            writePackagesForBackup();
            getLogger().info(MODULE, POLICY_RELOADED_SUCCESSFULLY);
            updatePolicyReloadTime();

            writeRnCPackagesForBackup();
            writeProductOffersForBackup();

        } catch (Exception ex) {
            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withRemark(ex.getMessage()).build();
            getLogger().error(MODULE, FAIL_TO_RELOAD_POLICY_REASON + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }

        // Last 10 elements to be displayed
        listHistory.add(policyCacheDetail);

        return policyCacheDetail;
    }

    @Override
    public synchronized Iterator<PolicyCacheDetail> reloadHistory() {
        return listHistory.iterator();
    }

    @Override
    public synchronized List<PolicyDetail> getPolicyDetail() {
        List<PolicyDetail> policyDetails =  getPolicyDetail(Predicates.<UserPackage>alwaysTrue(), Predicates.<IMSPackage>alwaysTrue()
                , Predicates.<QuotaTopUp>alwaysTrue(), Predicates.<MonetaryRechargePlan>alwaysTrue());
        policyDetails.addAll(getOfferDetail(Predicates.<ProductOffer>alwaysTrue()));
        policyDetails.addAll(getRnCPackageDetail(rnCPackage -> true));
        policyDetails.addAll(getBoDPackageDetail(boDPackage -> true));
        return policyDetails;
    }

    @Override
    public List<PolicyDetail> getPolicyDetail(com.elitecore.commons.base.Predicate<UserPackage> dataPackageFilter, com.elitecore.commons.base.Predicate<IMSPackage> imsPackageFilter, com.elitecore.commons.base.Predicate<QuotaTopUp> quotaTopUpFilter,
                                              com.elitecore.commons.base.Predicate<MonetaryRechargePlan> monetaryRechargePlanFilter) {
        ArrayList<PolicyDetail> policyDetails = new ArrayList<>();

        for (UserPackage pkg : policyStore.all()) {

            if (dataPackageFilter.apply(pkg) == false) {
                continue;
            }
            String remarks = createRemarks(pkg);
            if (PkgType.ADDON == pkg.getPackageType()) {
                policyDetails.add(new PolicyDetail(pkg.getId(), pkg.getName(), pkg.getStatus(), PackageType.ADD_ON_DATA_PACKAGE.getSubType(),
                        EntityType.DATA.getValue(),pkg.getMode(), remarks));
            } else if (PkgType.BASE == pkg.getPackageType()) {
                policyDetails.add(new PolicyDetail(pkg.getId(), pkg.getName(), pkg.getStatus(), PackageType.BASE_DATA_PACKAGE.getSubType(),
                        EntityType.DATA.getValue(),pkg.getMode(), remarks));
            } else if (PkgType.EMERGENCY == pkg.getPackageType()) {
                policyDetails.add(new PolicyDetail(pkg.getId(), pkg.getName(), pkg.getStatus(), PackageType.EMERGENCY.getSubType(),
                        EntityType.EMERGENCY.getValue(),pkg.getMode(), remarks));
            } else if (PkgType.PROMOTIONAL == pkg.getPackageType()) {
                policyDetails.add(new PolicyDetail(pkg.getId(), pkg.getName(), pkg.getStatus(), PackageType.PROMOTIONAL.getSubType(),
                        EntityType.PROMOTIONAL.getValue(),pkg.getMode(), remarks));
            }
        }

        for (IMSPackage imsPackage : policyStore.ims().all()) {

            if (imsPackageFilter.apply(imsPackage) == false) {
                continue;
            }
            policyDetails.add(new PolicyDetail(imsPackage.getId(), imsPackage.getName(), imsPackage.getStatus(), EntityType.IMS.name(),EntityType.IMS.getValue(), imsPackage
                    .getMode(), imsPackage.getFailReason()));
        }

        for (QuotaTopUp quotaTopUp : policyStore.quotaTopUp().all()) {

            if (quotaTopUpFilter.apply(quotaTopUp) == false) {
                continue;
            }
            policyDetails.add(new PolicyDetail(quotaTopUp.getId(), quotaTopUp.getName(), quotaTopUp.getStatus(), quotaTopUp.getPackageType().getVal(),EntityType.TOPUP.getValue(), quotaTopUp
                    .getMode(), quotaTopUp.getFailReason()));
        }

        for (MonetaryRechargePlan monetaryRechargePlan : policyStore.monetaryRechargePlan().all()) {

            if (monetaryRechargePlanFilter.apply(monetaryRechargePlan) == false) {
                continue;
            }
            policyDetails.add(new PolicyDetail(monetaryRechargePlan.getId(), monetaryRechargePlan.getName(), monetaryRechargePlan.getStatus(), EntityType.MONETARYRECHARGEPLAN.getValue(),EntityType.MONETARYRECHARGEPLAN.getValue(), monetaryRechargePlan
                    .getMode(), monetaryRechargePlan.getFailReason()));
        }

        Collections.sort(policyDetails, Collections.reverseOrder());

        return policyDetails;
    }

    @Override
    public List<PolicyDetail> getRnCPackageDetail(java.util.function.Predicate<RnCPackage>  rnCPackageFilter) {
        ArrayList<PolicyDetail> offerDetails = new ArrayList();

        for (RnCPackage offer : rnCPackageStore.all()) {
            if (rnCPackageFilter.test(offer) == false) {
                continue;
            }
            String remarks = createRemarks(offer);
            offerDetails.add(new PolicyDetail(offer.getId(), offer.getName(), offer.getPolicyStatus(), offer.getPkgType().getVal(),EntityType.RNC.getValue(), offer.getPackageMode(),remarks));
        }
        Collections.sort(offerDetails, Collections.reverseOrder());

        return offerDetails;
    }

    @Override
    public List<PolicyDetail> getOfferDetail(com.elitecore.commons.base.Predicate<ProductOffer> productOfferFilter) {
        ArrayList<PolicyDetail> offerDetails = new ArrayList();

        for (ProductOffer offer : productOfferStore.all()) {
            if (productOfferFilter.apply(offer) == false) {
                continue;
            }
            String remarks = createRemarks(offer);
            offerDetails.add(new PolicyDetail(offer.getId(), offer.getName(), offer.getPolicyStatus(), offer.getType().name(),EntityType.OFFER.getValue(), offer.getPackageMode(),remarks));
        }
        Collections.sort(offerDetails, Collections.reverseOrder());

        return offerDetails;
    }

    private String createRemarks(UserPackage pkg) {
        StringBuilder remarkBuilder = new StringBuilder();
        if (Strings.isNullOrBlank(pkg.getFailReason()) == false) {
            remarkBuilder.append("Fail Reasons: " + pkg.getFailReason());
        }

        if (Strings.isNullOrBlank(pkg.getPartialFailReason()) == false) {
            if (Strings.isNullOrBlank(pkg.getFailReason()) == false) {
                remarkBuilder.append(", ");
            }
            remarkBuilder.append("Partial Fail Reasons: " + pkg.getPartialFailReason());
        }

        if (remarkBuilder.length() < 1) {
            return null;
        }

        return remarkBuilder.toString();
    }

    private String createRemarks(RnCPackage pkg) {
        StringBuilder remarkBuilder = new StringBuilder();
        if (Strings.isNullOrBlank(pkg.getFailReason()) == false) {
            remarkBuilder.append("Fail Reasons: " + pkg.getFailReason());
        }

        if (Strings.isNullOrBlank(pkg.getPartialFailReason()) == false) {
            if (Strings.isNullOrBlank(pkg.getFailReason()) == false) {
                remarkBuilder.append(", ");
            }
            remarkBuilder.append("Partial Fail Reasons: " + pkg.getPartialFailReason());
        }

        if (remarkBuilder.length() < 1) {
            return null;
        }

        return remarkBuilder.toString();
    }

    private String createRemarks(ProductOffer offer) {

        if((PolicyStatus.FAILURE == offer.getPolicyStatus() || PolicyStatus.LAST_KNOWN_GOOD == offer.getPolicyStatus()) && Objects.nonNull(offer.getFailReason())){
            return offer.getFailReason().toString();
        }

        if(PolicyStatus.PARTIAL_SUCCESS == offer.getPolicyStatus() && Objects.nonNull(offer.getPartialFailReason())){
            return offer.getPartialFailReason().toString();
        }

        return null;
    }

    @Override
    public synchronized List<PolicyDetail> getPolicyDetail(String... packageNames) {

        List<PolicyDetail> policyDetails = new ArrayList<>(packageNames.length);

        for (String packageName : packageNames) {

            UserPackage pkg = getPkgDataByName(packageName);
            if (pkg != null) {


                ///TODO CHETAN Use enum restructure
                if (PkgType.ADDON == pkg.getPackageType()) {
                    policyDetails.add(new PolicyDetail(pkg.getId(), pkg.getName(), pkg.getStatus(), PackageType.ADD_ON_DATA_PACKAGE.name(),EntityType.DATA.getValue(), pkg
                            .getMode(), createRemarks(pkg)));
                } else if (PkgType.BASE == pkg.getPackageType()) {
                    policyDetails.add(new PolicyDetail(pkg.getId(), pkg.getName(), pkg.getStatus(), PackageType.BASE_DATA_PACKAGE.name(),EntityType.DATA.getValue(), pkg
                            .getMode(), createRemarks(pkg)));
                } else if (PkgType.EMERGENCY == pkg.getPackageType()) {
                    policyDetails.add(new PolicyDetail(pkg.getId(), pkg.getName(), pkg.getStatus(), PackageType.EMERGENCY.name(),EntityType.DATA.getValue(), pkg
                            .getMode(), createRemarks(pkg)));
                } else if (PkgType.PROMOTIONAL == pkg.getPackageType()) {
                    policyDetails.add(new PolicyDetail(pkg.getId(), pkg.getName(), pkg.getStatus(), PackageType.PROMOTIONAL.name(),EntityType.DATA.getValue(), pkg
                            .getMode(), createRemarks(pkg)));
                }
            } else if(getQuotaTopUpByName(packageName) != null) {
                QuotaTopUp quotaTopUp = getQuotaTopUpByName(packageName);
                policyDetails.add(new PolicyDetail(quotaTopUp.getId(), quotaTopUp.getName(), quotaTopUp.getStatus(), TopUpType.TOP_UP.name(),EntityType.DATA.getValue(), quotaTopUp
                        .getMode(), quotaTopUp.getFailReason()));
            } else if (getMonetaryRechargePlanByName(packageName) != null) {
                MonetaryRechargePlan monetaryRechargePlan = getMonetaryRechargePlanByName(packageName);
                policyDetails.add(new PolicyDetail(monetaryRechargePlan.getId(), monetaryRechargePlan.getName(), monetaryRechargePlan.getStatus(), EntityType.MONETARYRECHARGEPLAN.getValue(),
                        EntityType.MONETARYRECHARGEPLAN.getValue(), monetaryRechargePlan.getMode(), monetaryRechargePlan.getFailReason()));
            } else if (Objects.nonNull(getBoDPackage()) && Objects.nonNull(getBoDPackage().byName(packageName))) {
                BoDPackage boDPackage = getBoDPackage().byName(packageName);
                policyDetails.add(new PolicyDetail(boDPackage.getId(), boDPackage.getName(), boDPackage.getPolicyStatus(), EntityType.BOD.getValue(),
                        EntityType.BOD.getValue(), boDPackage.getPackageMode(), createRemarks(boDPackage)));
            } else if (Objects.nonNull(getRnCBasePackageByName(packageName))) {
                RnCPackage rnCPackage = getRnCBasePackageByName(packageName);
                policyDetails.add(new PolicyDetail(rnCPackage.getId(), rnCPackage.getName(), rnCPackage.getPolicyStatus(), rnCPackage.getPkgType().getVal(),
                        EntityType.RNC.getValue(), rnCPackage.getPackageMode(), createRemarks(rnCPackage)));
            } else if (Objects.nonNull(getProductOffer()) && Objects.nonNull(getProductOffer().byName(packageName))) {
                ProductOffer productOffer = getProductOffer().byName(packageName);
                policyDetails.add(new PolicyDetail(productOffer.getId(), productOffer.getName(), productOffer.getPolicyStatus(), productOffer.getType().name(),
                        EntityType.OFFER.getValue(), productOffer.getPackageMode(), createRemarks(productOffer)));
            }
            else {
                IMSPackage imsPackage = getIMSPkgByName(packageName);
                if (imsPackage != null) {
                    policyDetails.add(new PolicyDetail(imsPackage.getId(), imsPackage.getName(), imsPackage.getStatus(), EntityType.IMS.name(),EntityType.IMS.getValue(), imsPackage
                            .getMode(), imsPackage.getFailReason()));
                }
            }
        }
        return policyDetails;

    }

    @Override
    public synchronized List<PolicyDetail> getOfferDetails(String... offerNames) {

        List<PolicyDetail> policyDetails = new ArrayList<>(offerNames.length);

        for (String offerName : offerNames) {

            ProductOffer productOffer = getProductOffer().byName(offerName);
            if (productOffer != null) {

                String remarks = createRemarks(productOffer);
                if (PkgType.ADDON == productOffer.getType() || PkgType.BASE == productOffer.getType()) {
                    policyDetails.add(new PolicyDetail(productOffer.getId(), productOffer.getName(), productOffer.getPolicyStatus(), productOffer.getType().name(), EntityType.OFFER.getValue(), productOffer.getPackageMode(), remarks));
                }
            }
        }
        return policyDetails;

    }

    @Override
    public synchronized List<PolicyDetail> getRnCDetails(String... rncPackageNames) {

        List<PolicyDetail> policyDetails = new ArrayList<>(rncPackageNames.length);

        for (String rncPackageName : rncPackageNames) {

            RnCPackage rnCPackage = getRnCPackage().byName(rncPackageName);
            if (rnCPackage != null) {

                String remarks = createRemarks(rnCPackage);
                if (RnCPkgType.BASE == rnCPackage.getPkgType() || RnCPkgType.MONETARY_ADDON == rnCPackage.getPkgType() || RnCPkgType.NON_MONETARY_ADDON == rnCPackage.getPkgType()) {
                    policyDetails.add(new PolicyDetail(rnCPackage.getId(), rnCPackage.getName(), rnCPackage.getPolicyStatus(), rnCPackage.getPkgType().getVal(), EntityType.RNC.getValue(), rnCPackage.getPackageMode(), remarks));
                }
            }
        }
        return policyDetails;

    }

    @Override
    public List<PolicyDetail> getBoDPackageDetails(String... bodPackageNames) {
        List<PolicyDetail> policyDetails = new ArrayList<>(bodPackageNames.length);

        for (String bodPackageName : bodPackageNames) {

            BoDPackage bodPackage = getBoDPackage().byName(bodPackageName);
            if (bodPackage != null) {

                String remarks = createRemarks(bodPackage);
                policyDetails.add(new PolicyDetail(bodPackage.getId(), bodPackage.getName(), bodPackage.getPolicyStatus(), null, EntityType.BOD.getValue(), bodPackage.getPackageMode(), remarks));
            }
        }
        return policyDetails;
    }

    @Override
    public List<PromotionalPackage> getPromotionalPackages() {
        return promotional().all();
    }

    @Override
    public PromotionalPackage getPromotionalPackageById(String pkgId) {
        return promotional().byId(pkgId);
    }

    private GlobalPolicyStore<PromotionalPackage> promotional() {
        return policyStore.promotional();
    }

    @Override
    public PromotionalPackage getPromotionalPackageByName(String pkgName) {
        return promotional().byName(pkgName);
    }

    @Override
    public synchronized PolicyCacheDetail reloadDataPackages(String... names) {

        getLogger().info(MODULE, "Reloading Policy Manager");

        PolicyCacheDetail policyCacheDetail = null;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();
        try {

            Session session = null;
            try {
                session = sessionFactory.openSession();
                List<PkgData> newPkgDatas = hibernateDataReader.read(PkgData.class, session, names);

                // Collect Deleted And Inactive Packages to remove existing cache
                List<String> deletedOrInActiveDataPackages = newPkgDatas.stream()
                        .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                        .map(ResourceData::getId)
                        .collect(Collectors.toList());
                List<RatingGroupData> ratingGroupDatas = hibernateDataReader.readAll(RatingGroupData.class, session);

                List<String> requestedNamesList = Arrays.asList(names);
                List<Package> currentCachedPackages = this.policyStore.all().stream()
                        .filter(pkg -> requestedNamesList.contains(pkg.getName()))
                        .collect(Collectors.toList());

                processReloadDataPackages(newPkgDatas, deletedOrInActiveDataPackages, currentCachedPackages, ratingGroupDatas, Collectionz.newArrayList());
            } finally {
                closeQuietly(session);
            }

            if (names != null && names.length > 0) {
                for (String parameter : names) {

                    if (Strings.isNullOrBlank(parameter)) {
                        continue;
                    }

                    Package pakage = policyStore.byName(parameter);

                    if (pakage != null) {

                        PackageType packageType = getPackageType(pakage);

                        if (pakage.getStatus() == PolicyStatus.SUCCESS) {
                            successCounter++;
                            successPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.SUCCESS
                                    , packageType.getSubType(),EntityType.DATA.getValue(), pakage.getMode(), pakage.getFailReason()));
                        } else if (pakage.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                            partialSuccessCounter++;
                            partialSuccessPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.PARTIAL_SUCCESS
                                    , packageType.getSubType(),EntityType.DATA.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                        } else if (pakage.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                            lastKnownGoodCounter++;
                            lastKnownGoodPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.LAST_KNOWN_GOOD
                                    , packageType.getSubType(),EntityType.DATA.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                        } else {
                            failureCounter++;
                            failPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.FAILURE, packageType.getSubType(),EntityType.DATA.getValue(), pakage
                                    .getMode(), pakage.getFailReason()));
                        }
                    } else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail("", parameter, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.getSubType(),EntityType.DATA.getValue(), PkgMode.DESIGN,
                                NOT_FOUND));
                    }
                }
            }

            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                    withFailurePolicyList(failPolicyList).
                    withPartialSuccessPolicyList(partialSuccessPolicyList).
                    withPartialSuccessCounter(partialSuccessCounter).
                    withSuccessPolicyList(successPolicyList).
                    withSuccessCounter(successCounter).
                    withLastKnownGoodCounter(lastKnownGoodCounter).
                    withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();

            writePackagesForBackup();
            getLogger().info(MODULE, "Policy reloaded successfully");

        } catch (Exception ex) {
            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withRemark(ex.getMessage()).build();
            getLogger().error(MODULE, "Fail to reload policy. Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }

        // Last 10 elements to be displayed
        listHistory.add(policyCacheDetail);

        return policyCacheDetail;
    }

    private PackageType getPackageType(Package pakage) {

        //TODO CHETAN remove PackageType enum and enhance PkgType
        if (PkgType.BASE.name().equals(pakage.getType())) {
            return PackageType.BASE_DATA_PACKAGE;
        }
        if (PkgType.ADDON.name().equals(pakage.getType())) {
            return PackageType.ADD_ON_DATA_PACKAGE;
        }
        if (PkgType.EMERGENCY.name().equals(pakage.getType())) {
            return PackageType.EMERGENCY;
        }
        if (PkgType.PROMOTIONAL.name().equals(pakage.getType())) {
            return PackageType.PROMOTIONAL;
        }

        return PackageType.UNKNOWN;
    }

    private static class DataPolicyFilter implements com.elitecore.commons.base.Predicate<PkgData> {

        private static final DataPolicyFilter FILTER = new DataPolicyFilter();

        @Override
        public boolean apply(PkgData input) {
            return (PkgMode.DESIGN.name().equalsIgnoreCase(input.getPackageMode())
                    || CommonConstants.STATUS_DELETED.equals(input.getStatus())
                    || PkgStatus.INACTIVE.val.equalsIgnoreCase(input.getStatus())) == false;
        }

        public static DataPolicyFilter instance() {
            return FILTER;
        }
    }

    private static class IMSPolicyFilter implements com.elitecore.commons.base.Predicate<IMSPkgData> {

        private static final IMSPolicyFilter FILTER = new IMSPolicyFilter();

        @Override
        public boolean apply(IMSPkgData input) {
            return (PkgMode.DESIGN.name().equalsIgnoreCase(input.getPackageMode())
                    || CommonConstants.STATUS_DELETED.equals(input.getStatus())
                    || PkgStatus.INACTIVE.val.equalsIgnoreCase(input.getStatus())) == false;
        }

        public static IMSPolicyFilter instance() {
            return FILTER;
        }
    }

    private static class QuotaTopUpPolicyFilter implements com.elitecore.commons.base.Predicate<DataTopUpData> {

        private static final QuotaTopUpPolicyFilter FILTER = new QuotaTopUpPolicyFilter();

        @Override
        public boolean apply(DataTopUpData input) {
            return (PkgMode.DESIGN.name().equalsIgnoreCase(input.getPackageMode())
                    || CommonConstants.STATUS_DELETED.equals(input.getStatus())
                    || PkgStatus.INACTIVE.val.equalsIgnoreCase(input.getStatus())) == false;
        }

        public static QuotaTopUpPolicyFilter instance() {
            return FILTER;
        }
    }

    private static class MonetaryRechargePlanPolicyFilter implements com.elitecore.commons.base.Predicate<MonetaryRechargePlanData> {

        private static final MonetaryRechargePlanPolicyFilter FILTER = new MonetaryRechargePlanPolicyFilter();

        @Override
        public boolean apply(MonetaryRechargePlanData input) {
            return (PkgMode.DESIGN.name().equalsIgnoreCase(input.getPackageMode())
                    || CommonConstants.STATUS_DELETED.equals(input.getStatus())
                    || PkgStatus.INACTIVE.val.equalsIgnoreCase(input.getStatus())) == false;
        }

        public static MonetaryRechargePlanPolicyFilter instance() {
            return FILTER;
        }
    }

    @Override
    public synchronized PolicyCacheDetail reloadIMSPackages(String... names) {

        getLogger().info(MODULE, "Reloading Policy Manager");

        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();
        try {

            ///FIXME COPY FROM 6.8.2 -- harsh patel
            Session session = null;
            List<IMSPkgData> newIMSPkgDatas;
            try {
                session = sessionFactory.openSession();
                newIMSPkgDatas = hibernateDataReader.read(IMSPkgData.class, session, names);
                // Collect Deleted And Inactive Packages to remove existing cache
                List<String> deletedOrInActiveImsPackages = newIMSPkgDatas.stream()
                        .filter(pkg -> ResourceDataStatusFilter.getInstance().apply(pkg))
                        .map(ResourceData::getId)
                        .collect(Collectors.toList());

                List<String> requestedNamesList = Arrays.asList(names);

                List<IMSPackage> currentCachedPackages = policyStore.ims().all().stream()
                        .filter(pkg -> requestedNamesList.contains(pkg.getName()))
                        .collect(Collectors.toList());

                processReloadIMSPackages(newIMSPkgDatas, deletedOrInActiveImsPackages, currentCachedPackages);

            } finally {
                closeQuietly(session);
            }

            if (names != null && names.length > 0) {
                for (String name : names) {

                    if (Strings.isNullOrBlank(name)) {

                        continue;
                    }

                    IMSPackage pakage = ims().byName(name);

                    if (pakage != null) {

                        if (pakage.getStatus() == PolicyStatus.SUCCESS) {
                            successCounter++;
                            successPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.SUCCESS
                                    , EntityType.IMS.name(),EntityType.IMS.getValue(), pakage.getMode(), pakage.getFailReason()));
                        } else if (pakage.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                            partialSuccessCounter++;
                            partialSuccessPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.PARTIAL_SUCCESS
                                    , EntityType.IMS.name(),EntityType.IMS.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                        } else if (pakage.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                            lastKnownGoodCounter++;
                            lastKnownGoodPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.LAST_KNOWN_GOOD
                                    , EntityType.IMS.name(),EntityType.IMS.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                        } else {
                            failureCounter++;
                            failPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.FAILURE, EntityType.IMS.name(),EntityType.IMS.getValue(), pakage
                                    .getMode(), pakage.getFailReason()));
                        }
                    } else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail("", name, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.getSubType(),EntityType.IMS.getValue(), PkgMode.DESIGN, "Not found"));
                    }
                }
            }

            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                    withFailurePolicyList(failPolicyList).
                    withPartialSuccessPolicyList(partialSuccessPolicyList).
                    withPartialSuccessCounter(partialSuccessCounter).
                    withSuccessPolicyList(successPolicyList).
                    withSuccessCounter(successCounter).
                    withLastKnownGoodCounter(lastKnownGoodCounter).
                    withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();

            writePackagesForBackup();
            getLogger().info(MODULE, "Policy reloaded successfully");

        } catch (Exception ex) {
            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withRemark(ex.getMessage()).build();
            getLogger().error(MODULE, "Fail to reload policy. Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }

        // Last 10 elements to be displayed
        listHistory.add(policyCacheDetail);

        return policyCacheDetail;
    }


    private void processReloadDataPackages(List<PkgData> newPkgDatas, List<String> deletedOrInActiveImsPackages, List<Package> currentCachedPackages,
                                           List<RatingGroupData> ratingGroupDatas, List<GroupManageOrder> groupManageOrdersAdded) {

        // filter DESIGN, DELETED and INACTIVE packages
        Collectionz.filter(newPkgDatas, DataPolicyFilter.instance());

        List<Package> cacheForNewPackages = dataPackageFactory.create(newPkgDatas, ratingGroupDatas);
        populatePurgedDataPackagesInDeletedIds(cacheForNewPackages, deletedOrInActiveImsPackages, currentCachedPackages);

        this.policyStore.reloadDataPolicy(cacheForNewPackages, groupManageOrdersAdded, deletedOrInActiveImsPackages);
    }

    private void processReloadIMSPackages(List<IMSPkgData> newIMSPkgDatas, List<String> deletedOrInActiveImsPackages, List<IMSPackage> currentCachedPackages) {

        // filter DESIGN, DELETED and INACTIVE packages
        Collectionz.filter(newIMSPkgDatas, IMSPolicyFilter.instance());

        List<IMSPackage> cacheForNewPackages = imsPackageFactory.create(newIMSPkgDatas);
        populatePurgedImsPackagesInDeletedIds(cacheForNewPackages, deletedOrInActiveImsPackages, currentCachedPackages);

        this.policyStore.reloadIMSPolicy(cacheForNewPackages, deletedOrInActiveImsPackages);
    }

    private void processReloadQuotaTopUpPackages(List<DataTopUpData> newDataTopUpDatas, List<String> deletedOrInActiveImsPackages, List<QuotaTopUp> currentCachedPackages) {
        // filter DESIGN, DELETED and INACTIVE packages
        Collectionz.filter(newDataTopUpDatas, QuotaTopUpPolicyFilter.instance());
        List<QuotaTopUp> cacheForNewPackages = quotaTopUpFactory.create(newDataTopUpDatas);
        populatePurgedQuotaTopUpPackagesInDeletedIds(cacheForNewPackages, deletedOrInActiveImsPackages, currentCachedPackages);

        this.policyStore.reloadTopUpPolicy(cacheForNewPackages, deletedOrInActiveImsPackages);
    }

    private void processReloadMonetaryRechargePlans(List<MonetaryRechargePlanData> newMonetaryRechargePlanDatas, List<String> deletedOrInActiveMonetaryRechargePlans, List<MonetaryRechargePlan> currentCachedPackages) {
        // filter DESIGN, DELETED and INACTIVE packages
        Collectionz.filter(newMonetaryRechargePlanDatas, MonetaryRechargePlanPolicyFilter.instance());
        List<MonetaryRechargePlan> cacheForNewPackages = monetaryRechargePlanFactory.create(newMonetaryRechargePlanDatas);
        populatePurgedMonetaryRechargePlansInDeletedIds(cacheForNewPackages, deletedOrInActiveMonetaryRechargePlans, currentCachedPackages);

        this.policyStore.reloadMonetaryRechargePlanPolicy(cacheForNewPackages, deletedOrInActiveMonetaryRechargePlans);
    }



    @Override
    public PolicyCacheDetail reloadQuotaTopUps(String... names) {
        getLogger().info(MODULE, "Reloading Policy Manager");

        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();
        try {

            ///FIXME COPY FROM 6.8.2 -- harsh patel
            Session session = null;
            try {

                session = sessionFactory.openSession();
                List<DataTopUpData> newQuotaTopUps = hibernateDataReader.read(DataTopUpData.class, session, names);

                // Collect Deleted And Inactive Packages to remove existing cache
                List<String> deletedOrInActiveQuotaTopUps = newQuotaTopUps.stream()
                        .filter(pkg -> ResourceDataStatusFilter.getInstance().apply(pkg))
                        .map(ResourceData::getId)
                        .collect(Collectors.toList());

                List<String> requestedNamesList = Arrays.asList(names);

                List<QuotaTopUp> currentCachedPackages = this.policyStore.quotaTopUp().all().stream()
                        .filter(pkg -> requestedNamesList.contains(pkg.getName()))
                        .collect(Collectors.toList());

                processReloadQuotaTopUpPackages(newQuotaTopUps, deletedOrInActiveQuotaTopUps, currentCachedPackages);
            } finally {
                closeQuietly(session);
            }

            if (names != null && names.length > 0) {
                for (String name : names) {

                    if (Strings.isNullOrBlank(name)) {
                        continue;
                    }

                    QuotaTopUp pakage = quotaTopUp().byName(name);

                    if (pakage != null) {
                        TopUpType packageType = pakage.getPackageType();
                        if (pakage.getStatus() == PolicyStatus.SUCCESS) {
                            successCounter++;
                            successPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.SUCCESS
                                    , packageType.name(), EntityType.TOPUP.getValue(), pakage.getMode(), pakage.getFailReason()));
                        } else if (pakage.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                            partialSuccessCounter++;
                            partialSuccessPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.PARTIAL_SUCCESS
                                    , packageType.name(), EntityType.TOPUP.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                        } else if (pakage.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                            lastKnownGoodCounter++;
                            lastKnownGoodPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.LAST_KNOWN_GOOD
                                    , packageType.name(), EntityType.TOPUP.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                        } else {
                            failureCounter++;
                            failPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.FAILURE, packageType.name(), EntityType.TOPUP.getValue(),
                                    pakage.getMode(), pakage.getFailReason()));
                        }
                    } else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail("", name, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.getSubType(), EntityType.TOPUP.getValue(), PkgMode.DESIGN, "Not found"));
                    }
                }
            }

            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                    withFailurePolicyList(failPolicyList).
                    withPartialSuccessPolicyList(partialSuccessPolicyList).
                    withPartialSuccessCounter(partialSuccessCounter).
                    withSuccessPolicyList(successPolicyList).
                    withSuccessCounter(successCounter).
                    withLastKnownGoodCounter(lastKnownGoodCounter).
                    withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();

            writePackagesForBackup();
            getLogger().info(MODULE, "Policy reloaded successfully");

        } catch (Exception ex) {
            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withRemark(ex.getMessage()).build();
            getLogger().error(MODULE, "Fail to reload policy. Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }

        // Last 10 elements to be displayed
        listHistory.add(policyCacheDetail);

        return policyCacheDetail;
    }


    @Override
    public PolicyCacheDetail reloadMonetaryRechargePlans(String... names) {
        getLogger().info(MODULE, "Reloading Policy Manager");

        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();
        try {
            Session session = null;
            List<MonetaryRechargePlanData> newMonetaryRechargePlanDatas;
            try {
                session = sessionFactory.openSession();
                newMonetaryRechargePlanDatas = hibernateDataReader.read(MonetaryRechargePlanData.class, session, names);

                List<String> deletedOrInActiveMonetaryRechargePlans = newMonetaryRechargePlanDatas.stream()
                        .filter(pkg -> ResourceDataStatusFilter.getInstance().apply(pkg))
                        .map(ResourceData::getId)
                        .collect(Collectors.toList());

                List<String> requestedNamesList = Arrays.asList(names);

                List<MonetaryRechargePlan> currentCachedPackages = policyStore.monetaryRechargePlan().all().stream()
                        .filter(pkg -> requestedNamesList.contains(pkg.getName()))
                        .collect(Collectors.toList());

                processReloadMonetaryRechargePlans(newMonetaryRechargePlanDatas, deletedOrInActiveMonetaryRechargePlans, currentCachedPackages);

            } finally {
                closeQuietly(session);
            }

            if (names != null && names.length > 0) {
                for (String name : names) {

                    if (Strings.isNullOrBlank(name)) {
                        continue;
                    }

                    MonetaryRechargePlan pakage = monetaryRechargePlan().byName(name);

                    if (pakage != null) {

                        if (pakage.getStatus() == PolicyStatus.SUCCESS) {
                            successCounter++;
                            successPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.SUCCESS
                                    , EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(), pakage.getMode(), pakage.getFailReason()));
                        } else if (pakage.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                            partialSuccessCounter++;
                            partialSuccessPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.PARTIAL_SUCCESS
                                    , EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                        } else if (pakage.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                            lastKnownGoodCounter++;
                            lastKnownGoodPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.LAST_KNOWN_GOOD
                                    , EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                        } else {
                            failureCounter++;
                            failPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.FAILURE, EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(), pakage
                                    .getMode(), pakage.getFailReason()));
                        }
                    } else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail("", name, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.getSubType(),EntityType.MONETARYRECHARGEPLAN.getValue(), PkgMode.DESIGN, "Not found"));
                    }
                }
            }

            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                    withFailurePolicyList(failPolicyList).
                    withPartialSuccessPolicyList(partialSuccessPolicyList).
                    withPartialSuccessCounter(partialSuccessCounter).
                    withSuccessPolicyList(successPolicyList).
                    withSuccessCounter(successCounter).
                    withLastKnownGoodCounter(lastKnownGoodCounter).
                    withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();

            writePackagesForBackup();
            getLogger().info(MODULE, "Policy reloaded successfully");

        } catch (Exception ex) {
            policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withRemark(ex.getMessage()).build();
            getLogger().error(MODULE, "Fail to reload policy. Reason: " + ex.getMessage());
            getLogger().trace(MODULE, ex);
        }

        // Last 10 elements to be displayed
        listHistory.add(policyCacheDetail);

        return policyCacheDetail;
    }

    @Override
    public synchronized PolicyCacheDetail reloadByName(String... names) {
        if(getLogger().isInfoLogLevel()){
            getLogger().info(MODULE, "Reloading Policy Manager by calling reloadByName");
        }
        List<String> dataPackageNames = null;
        List<String> imsPackageNames = null;
        List<String> quotaTopUpPackageNames = null;
        List<String> productOffersNames = null;
        List<String> monetaryRechargePlanNames = null;

        AtomicInteger failureCounter= new AtomicInteger(0);
        AtomicInteger successCounter= new AtomicInteger(0);
        AtomicInteger partialSuccessCounter= new AtomicInteger(0);
        AtomicInteger lastKnownGoodCounter= new AtomicInteger(0);
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();

        for (String name : names) {

            UserPackage pakage = this.policyStore.byName(name);

            if (pakage != null) {
                if (dataPackageNames == null) {
                    dataPackageNames = new ArrayList<>();
                }

                dataPackageNames.add(pakage.getName());
            } else if (ims().byName(name) != null){

                IMSPackage imsPackage = ims().byName(name);

                if (imsPackage != null) {
                    if (imsPackageNames == null) {
                        imsPackageNames = new ArrayList<>();
                    }
                    imsPackageNames.add(imsPackage.getName());
                }

            } else if (quotaTopUp().byName(name) != null) {
                QuotaTopUp quotaTopUp = quotaTopUp().byName(name);

                if (quotaTopUpPackageNames == null) {
                    quotaTopUpPackageNames = new ArrayList<>();
                }
                quotaTopUpPackageNames.add(quotaTopUp.getName());

            } else if (monetaryRechargePlan().byName(name) != null){

                MonetaryRechargePlan monetaryRechargePlan = monetaryRechargePlan().byName(name);

                if (monetaryRechargePlan != null) {
                    if (monetaryRechargePlanNames == null) {
                        monetaryRechargePlanNames = new ArrayList<>();
                    }
                    monetaryRechargePlanNames.add(monetaryRechargePlan.getName());
                }

            } else {
                ///FIXME HOW TO HANLE NEW CREATED POLICY OR UNKNOWN POLICY
            }

            if(productOfferStore.byName(name)!=null){
                if (productOffersNames == null) {
                    productOffersNames = new ArrayList<>();
                }
                productOffersNames.add(name);
            }
        }

        StringBuilder reloadFailRemarkBuilder = new StringBuilder();
        if (dataPackageNames != null) {
            String[] nameArray = new String[dataPackageNames.size()];

            for (int index = 0; index < dataPackageNames.size(); index++) {
                nameArray[index] = dataPackageNames.get(index);
            }
            populateCounters(reloadDataPackages(nameArray), reloadFailRemarkBuilder, failureCounter, partialSuccessCounter, successCounter, lastKnownGoodCounter, failPolicyList, partialSuccessPolicyList, successPolicyList, lastKnownGoodPolicyList);
        }

        if (imsPackageNames != null) {

            String[] nameArray = new String[dataPackageNames.size()];

            for (int index = 0; index < imsPackageNames.size(); index++) {
                nameArray[index] = imsPackageNames.get(index);
            }

            populateCounters(reloadIMSPackages(nameArray), reloadFailRemarkBuilder, failureCounter, partialSuccessCounter, successCounter, lastKnownGoodCounter, failPolicyList, partialSuccessPolicyList, successPolicyList, lastKnownGoodPolicyList);
        }

        if (quotaTopUpPackageNames != null) {
            String[] nameArray = new String[dataPackageNames.size()];

            for (int index = 0; index < quotaTopUpPackageNames.size(); index++) {
                nameArray[index] = quotaTopUpPackageNames.get(index);
            }

            populateCounters(reloadQuotaTopUps(nameArray), reloadFailRemarkBuilder, failureCounter, partialSuccessCounter, successCounter, lastKnownGoodCounter, failPolicyList, partialSuccessPolicyList, successPolicyList, lastKnownGoodPolicyList);
        }

        if (monetaryRechargePlanNames != null) {

            String[] nameArray = new String[dataPackageNames.size()];

            for (int index = 0; index < monetaryRechargePlanNames.size(); index++) {
                nameArray[index] = monetaryRechargePlanNames.get(index);
            }

            populateCounters(reloadMonetaryRechargePlans(nameArray), reloadFailRemarkBuilder, failureCounter, partialSuccessCounter, successCounter, lastKnownGoodCounter, failPolicyList, partialSuccessPolicyList, successPolicyList, lastKnownGoodPolicyList);
        }

        if (productOffersNames != null) {
            populateCounters(reloadProductOffers(ProductOfferPredicates.createNameFilter(productOffersNames.toArray(new String[productOffersNames.size()]))),
                    reloadFailRemarkBuilder, failureCounter, partialSuccessCounter, successCounter, lastKnownGoodCounter, failPolicyList, partialSuccessPolicyList, successPolicyList, lastKnownGoodPolicyList);
        }


        if(reloadFailRemarkBuilder.length() > 0){
            return new PolicyCacheDetail.PolicyCacheDetailBuilder().withRemark(reloadFailRemarkBuilder.toString()).build();
        }
        return new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter.get()).
                withFailurePolicyList(failPolicyList).
                withPartialSuccessPolicyList(partialSuccessPolicyList).
                withPartialSuccessCounter(partialSuccessCounter.get()).
                withSuccessPolicyList(successPolicyList).
                withSuccessCounter(successCounter.get()).
                withLastKnownGoodCounter(lastKnownGoodCounter.get()).
                withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();
    }

    private void populateCounters(PolicyCacheDetail policyCacheDetail,
                                  StringBuilder reloadFailRemarkBuilder,
                                  AtomicInteger failureCounter,
                                  AtomicInteger partialSuccessCounter,
                                  AtomicInteger successCounter,
                                  AtomicInteger lastKnownGoodCounter,
                                  List<PolicyDetail> failPolicyList,
                                  List<PolicyDetail> partialSuccessPolicyList,
                                  List<PolicyDetail> successPolicyList,
                                  List<PolicyDetail> lastKnownGoodPolicyList) {
        if (Strings.isNullOrBlank(policyCacheDetail.getRemark()) == false) {

            if (reloadFailRemarkBuilder.length() != 0) {
                reloadFailRemarkBuilder.append(", ");
            }
            reloadFailRemarkBuilder.append(policyCacheDetail.getRemark());

        } else {
            if (policyCacheDetail.getFailureCounter() > 0) {
                failureCounter.set(failureCounter.get() + policyCacheDetail.getFailureCounter());
                failPolicyList.addAll(policyCacheDetail.getFailurePolicyList());
            }
            if (policyCacheDetail.getPartialSuccessCounter() > 0) {
                partialSuccessCounter.set(partialSuccessCounter.get() + policyCacheDetail.getPartialSuccessCounter());
                partialSuccessPolicyList.addAll(policyCacheDetail.getPartialSuccessPolicyList());
            }
            if (policyCacheDetail.getSuccessCounter() > 0) {
                successCounter.set(successCounter.get() + policyCacheDetail.getSuccessCounter());
                successPolicyList.addAll(policyCacheDetail.getSuccessPolicyList());
            }
            if (policyCacheDetail.getLastKnownGoodCounter() > 0) {
                lastKnownGoodCounter.set(lastKnownGoodCounter.get() + policyCacheDetail.getLastKnownGoodCounter());
                lastKnownGoodPolicyList.addAll(policyCacheDetail.getLastKnownGoodPolicyList());
            }
        }
    }

    @Override
    public synchronized PolicyCacheDetail reloadIMSPackagesOfGroups(List<String> groupIds) {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(MODULE,"Reloading IMS packages");
        }
        Session session = null;
        try {

            session = sessionFactory.openSession();
            List<IMSPkgData> newIMSPkgDatas = hibernateDataReader.readAll(IMSPkgData.class, session);

            Collectionz.filter(newIMSPkgDatas, StaffGroupIMSPackageDataPredicate.create(groupIds));

            // Collect Deleted And Inactive Packages to remove existing cache
            List<String> deletedOrInActiveIMSPackages = newIMSPkgDatas.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(ResourceData::getId)
                    .collect(Collectors.toList());

            List<IMSPackage> currentCachedPackages = this.policyStore.ims().all().stream()
                    .filter(pkg -> Collections.disjoint(groupIds, pkg.getGroupIds()) == false)
                    .collect(Collectors.toList());

            processReloadIMSPackages(newIMSPkgDatas, deletedOrInActiveIMSPackages, currentCachedPackages);

            List<String> pkgNames = newIMSPkgDatas.stream().map(IMSPkgData::getName).collect(Collectors.toList());
            PolicyCacheDetail policyCacheDetail = buildPolicyCacheDetailAfterReloadIMS(pkgNames);
            writePackagesForBackup();
            getLogger().info(MODULE, "IMS Package reloaded successfully");

            listHistory.add(policyCacheDetail);
            return policyCacheDetail;

        } finally {

            closeQuietly(session);
        }
    }

    @Override
    public PolicyCacheDetail reloadQuotaTopUpsOfGroups(List<String> groupIds) {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(MODULE,"Reloading Quota TopUp packages");
        }
        Session session = null;
        try {

            session = sessionFactory.openSession();
            List<DataTopUpData> newDataTopUpDatas = hibernateDataReader.readAll(DataTopUpData.class, session);
            Collectionz.filter(newDataTopUpDatas, ResourceDataPredicates.createStaffBelongingPredicate(groupIds));

            // Collect Deleted And Inactive Packages to remove existing cache
            List<String> deletedOrInActiveQuotaTopUps = newDataTopUpDatas.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(ResourceData::getId)
                    .collect(Collectors.toList());

            List<QuotaTopUp> currentCachedPackages = policyStore.quotaTopUp().all().stream()
                    .filter(pkg -> Collections.disjoint(groupIds, pkg.getGroupIds()) == false)
                    .collect(Collectors.toList());

            processReloadQuotaTopUpPackages(newDataTopUpDatas, deletedOrInActiveQuotaTopUps, currentCachedPackages);

            List<String> pkgNames = newDataTopUpDatas.stream().map(DataTopUpData::getName).collect(Collectors.toList());

            PolicyCacheDetail policyCacheDetail = buildPolicyCacheDetailAfterReloadQuotaTopUp(pkgNames);

            writePackagesForBackup();

            getLogger().info(MODULE, "Quota TopUp Package reloaded successfully");

            listHistory.add(policyCacheDetail);
            return policyCacheDetail;

        } finally {

            closeQuietly(session);
        }
    }

    @Override
    public synchronized PolicyCacheDetail reloadMonetaryRechargePlansOfGroups(List<String> groupIds) {

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(MODULE,"Reloading Monetary Recharge Plans");
        }
        Session session = null;
        try {

            session = sessionFactory.openSession();
            List<MonetaryRechargePlanData> newMonetaryRechargePlanDatas = hibernateDataReader.readAll(MonetaryRechargePlanData.class, session);

            Collectionz.filter(newMonetaryRechargePlanDatas, ResourceDataPredicates.createStaffBelongingPredicate(groupIds));

            List<String> deletedOrInActiveMonetaryRechargePlans = newMonetaryRechargePlanDatas.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(ResourceData::getId)
                    .collect(Collectors.toList());

            List<MonetaryRechargePlan> currentCachedPackages = this.policyStore.monetaryRechargePlan().all().stream()
                    .filter(pkg -> Collections.disjoint(groupIds, pkg.getGroupIds()) == false)
                    .collect(Collectors.toList());

            processReloadMonetaryRechargePlans(newMonetaryRechargePlanDatas, deletedOrInActiveMonetaryRechargePlans, currentCachedPackages);

            List<String> pkgNames = newMonetaryRechargePlanDatas.stream().map(MonetaryRechargePlanData::getName).collect(Collectors.toList());
            PolicyCacheDetail policyCacheDetail = buildPolicyCacheDetailAfterReloadMonetaryRechargePlan(pkgNames);
            writePackagesForBackup();
            getLogger().info(MODULE, "Monetary Recharge Plan reloaded successfully");

            listHistory.add(policyCacheDetail);
            return policyCacheDetail;

        } finally {

            closeQuietly(session);
        }
    }

    @Override
    public synchronized PolicyCacheDetail reloadProductOffers(Predicate<ProductOfferData> predicate){
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading product offers");
        }

        Session session = null;
        try {
            session = sessionFactory.openSession();
            List<ProductOfferData> newOffers = hibernateDataReader.readAll(ProductOfferData.class, session);

            // Collect Deleted And Inactive Offers to remove existing cache
            List<String> deletedOrInActiveProductOffers = newOffers.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(ResourceData::getId)
                    .collect(Collectors.toList());

            //Collect purged offer entities
            deletedOrInActiveProductOffers.addAll(getPurgedOffers(newOffers));

            newOffers = newOffers.stream()
                    .filter(offerData -> ResourceDataStatusFilter.getInstance().apply(offerData)
                            && predicate.test(offerData)
                            && PkgMode.DESIGN.name().equalsIgnoreCase(offerData.getPackageMode()) == false).collect(Collectors.toList());

            List<ProductOfferData> addOnOffers = Collectionz.newArrayList();
            List<ProductOfferData> baseOffers = Collectionz.newArrayList();
            newOffers.forEach(offer-> {
                if(PkgType.BASE.name().equalsIgnoreCase(offer.getType())){
                    baseOffers.add(offer);
                } else if(PkgType.ADDON.name().equalsIgnoreCase(offer.getType())){
                    addOnOffers.add(offer);
                }
            });
            List<ProductOffer> productOfferList = new ArrayList<>();

            addOnOffers.forEach(offer->productOfferList.add(addOnProductOfferFactory.createProductOffer(offer)));
            this.productOfferStore.create(productOfferList, deletedOrInActiveProductOffers);

            baseOffers.forEach(offer->productOfferList.add(baseProductOfferFactory.createProductOffer(offer)));
            this.productOfferStore.create(productOfferList, deletedOrInActiveProductOffers);

            List<String> offerNames = newOffers.stream().map(ProductOfferData::getName).collect(Collectors.toList());
            PolicyCacheDetail policyCacheDetail = buildPolicyCacheDetailAfterReloadOffer(offerNames);

            listHistory.add(policyCacheDetail);
            writeProductOffersForBackup();
            getLogger().info(MODULE, "Offer reloaded successfully");
            return policyCacheDetail;

        } finally {

            closeQuietly(session);
        }
    }

    private List<String> getPurgedOffers(List<ProductOfferData> offerData){
        Map<String, ProductOfferData> offerDataMap = new HashMap<>();
        offerData.forEach(offer->offerDataMap.put(offer.getId(),offer));

        List<String> purgedObjects = new ArrayList<>();

        productOfferStore.all().forEach(storedOffer->{
            if(offerDataMap.get(storedOffer.getId())==null){
                purgedObjects.add(storedOffer.getId());
            }
        });

        return purgedObjects;
    }

    @Override
    public synchronized PolicyCacheDetail reloadRnCPackages(Predicate<RncPackageData> predicate){
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading rnc packages");
        }

        Session session = null;
        try {
            session = sessionFactory.openSession();
            List<RncPackageData> newPackages = hibernateDataReader.readAll(RncPackageData.class, session);

            // Collect Deleted And Inactive Offers to remove existing cache
            List<String> deletedOrInActiveRnCPackages = newPackages.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(ResourceData::getId)
                    .collect(Collectors.toList());

            //Collect purged RnC packages
            populatePurgedDataRnCPackagesInDeletedIds(newPackages, deletedOrInActiveRnCPackages, rnCPackageStore.all());

            newPackages = newPackages.stream()
                    .filter(offerData -> ResourceDataStatusFilter.getInstance().apply(offerData)
                            && predicate.test(offerData)
                            && PkgMode.DESIGN.name().equalsIgnoreCase(offerData.getMode()) == false).collect(Collectors.toList());

            List<RnCPackage> rnCPackageList = new ArrayList<>();

            newPackages.forEach(offer->{
                RnCPackage rnCPackage = rnCPackageFactory.create(offer);
                rnCPackage.init();
                rnCPackageList.add(rnCPackage);
            });

            this.rnCPackageStore.create(rnCPackageList, deletedOrInActiveRnCPackages);

            List<String> offerNames = newPackages.stream().map(RncPackageData::getName).collect(Collectors.toList());
            PolicyCacheDetail policyCacheDetail = buildPolicyCacheForRncPackages(offerNames);

            writeRnCPackagesForBackup();
            listHistory.add(policyCacheDetail);
            getLogger().info(MODULE, "RnC packages reloaded successfully");
            return policyCacheDetail;

        } finally {
            closeQuietly(session);
        }
    }

    @Override
    public synchronized PolicyCacheDetail reloadDataPackagesOfGroups(List<String> groupIds) {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading data packages");
        }

        Session session = null;
        try {
            session = sessionFactory.openSession();
            List<PkgData> newPkgDatas = hibernateDataReader.readAll(PkgData.class, session);
            List<PkgGroupOrderData> newPkgGroupOrderDatas = hibernateDataReader.readAll(PkgGroupOrderData.class, session);

            Collectionz.filter(newPkgDatas, GroupFilter.create(groupIds));

            // Collect Deleted And Inactive Packages to remove existing cache
            List<String> deletedOrInActiveDataPackages = newPkgDatas.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(ResourceData::getId)
                    .collect(Collectors.toList());

            Collectionz.filter(newPkgGroupOrderDatas, PkgGroupOrderDataPredicates.createGroupFilter(groupIds));
            List<RatingGroupData> ratingGroupDatas = hibernateDataReader.readAll(RatingGroupData.class, session);
            List<GroupManageOrder> groupManageOrdersAdded = GroupManageOrderFactory.create(newPkgGroupOrderDatas);

            // fetch old cache packages with provided groups
            List<Package> currentCachedPackages = policyStore.all().stream()
                    .filter(pkg -> Collections.disjoint(groupIds, pkg.getGroupIds())==false)
                    .collect(Collectors.toList());

            processReloadDataPackages(newPkgDatas, deletedOrInActiveDataPackages, currentCachedPackages, ratingGroupDatas, groupManageOrdersAdded);

            List<String> pkgNames = newPkgDatas.stream().map(PkgData::getName).collect(Collectors.toList());
            PolicyCacheDetail policyCacheDetail = buildPolicyCacheDetailAfterReloadData(pkgNames);
            Map<String, List<PkgData>> pkgTypeToNames = newPkgDatas.stream().collect(Collectors.groupingBy(PkgData::getType));
            for (PkgType pkgType: PkgType.values()) {
                List<String> packageNames = Collectionz.newArrayList();
                if(Collectionz.isNullOrEmpty(pkgTypeToNames.get(pkgType.name())) == false) {
                    packageNames = pkgTypeToNames.get(pkgType.name()).stream().map(PkgData::getName).collect(Collectors.toList());
                }
                pkgTypeWisePolicyStatistics.put(pkgType.name(), buildPolicyCacheDetailAfterReloadData(packageNames));
            }

            writePackagesForBackup();
            listHistory.add(policyCacheDetail);
            getLogger().info(MODULE, "Policy reloaded successfully");
            return policyCacheDetail;

        } finally {
            closeQuietly(session);
        }
    }

    @Override
    public PolicyCacheDetail getDataPkgTypeWisePolicyStatistics(String pkgType) {
        if(pkgTypeWisePolicyStatistics.get(pkgType) == null){
            return null;
        }
        return pkgTypeWisePolicyStatistics.get(pkgType);
    }

    private PolicyCacheDetail buildPolicyCacheDetailAfterReloadIMS(List<String> imsPackageNames) {
        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();

        for (String parameter : imsPackageNames) {

            if (Strings.isNullOrBlank(parameter) == false) {

                IMSPackage pakage = ims().byName(parameter);

                if (pakage != null) {
                    if (pakage.getStatus() == PolicyStatus.SUCCESS) {
                        successCounter++;
                        successPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.SUCCESS
                                , EntityType.IMS.name(),EntityType.IMS.getValue(), pakage.getMode(), pakage.getFailReason()));
                    } else if (pakage.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                        partialSuccessCounter++;
                        partialSuccessPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.PARTIAL_SUCCESS
                                , EntityType.IMS.name(),EntityType.IMS.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                    } else if (pakage.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                        lastKnownGoodCounter++;
                        lastKnownGoodPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.LAST_KNOWN_GOOD
                                , EntityType.IMS.name(),EntityType.IMS.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                    } else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.FAILURE, EntityType.IMS.name(),EntityType.IMS.getValue(), pakage
                                .getMode(), pakage.getFailReason()));
                    }
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail("",parameter, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.name(),EntityType.IMS.getValue(), PkgMode.DESIGN, "Not found"));
                }
            }
        }


        policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                withFailurePolicyList(failPolicyList).
                withPartialSuccessPolicyList(partialSuccessPolicyList).
                withPartialSuccessCounter(partialSuccessCounter).
                withSuccessPolicyList(successPolicyList).
                withSuccessCounter(successCounter).
                withLastKnownGoodCounter(lastKnownGoodCounter).
                withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();
        return policyCacheDetail;
    }

    private PolicyCacheDetail buildPolicyCacheDetailAfterReloadMonetaryRechargePlan(List<String> monetaryRechargePlanNames) {
        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();

        for (String parameter : monetaryRechargePlanNames) {

            if (Strings.isNullOrBlank(parameter) == false) {

                MonetaryRechargePlan pakage = monetaryRechargePlan().byName(parameter);

                if (pakage != null) {
                    if (pakage.getStatus() == PolicyStatus.SUCCESS) {
                        successCounter++;
                        successPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.SUCCESS
                                , EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(), pakage.getMode(), pakage.getFailReason()));
                    } else if (pakage.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                        partialSuccessCounter++;
                        partialSuccessPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.PARTIAL_SUCCESS
                                , EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                    } else if (pakage.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                        lastKnownGoodCounter++;
                        lastKnownGoodPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.LAST_KNOWN_GOOD
                                , EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                    } else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.FAILURE, EntityType.MONETARYRECHARGEPLAN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(), pakage
                                .getMode(), pakage.getFailReason()));
                    }
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail("",parameter, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.name(),EntityType.MONETARYRECHARGEPLAN.getValue(), PkgMode.DESIGN, "Not found"));
                }
            }
        }


        policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                withFailurePolicyList(failPolicyList).
                withPartialSuccessPolicyList(partialSuccessPolicyList).
                withPartialSuccessCounter(partialSuccessCounter).
                withSuccessPolicyList(successPolicyList).
                withSuccessCounter(successCounter).
                withLastKnownGoodCounter(lastKnownGoodCounter).
                withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();
        return policyCacheDetail;
    }

    private PolicyCacheDetail buildPolicyCacheDetailAfterReloadQuotaTopUp(List<String> quotaTopUpNames) {
        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();

        for (String quotaTopUpName : quotaTopUpNames) {

            if (Strings.isNullOrBlank(quotaTopUpName) == false) {

                QuotaTopUp pakage = quotaTopUp().byName(quotaTopUpName);

                if (pakage != null) {

                    TopUpType packageType = pakage.getPackageType();
                    if (pakage.getStatus() == PolicyStatus.SUCCESS) {
                        successCounter++;
                        successPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.SUCCESS
                                , packageType.name(),EntityType.TOPUP.getValue(), pakage.getMode(), pakage.getFailReason()));
                    } else if (pakage.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                        partialSuccessCounter++;
                        partialSuccessPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.PARTIAL_SUCCESS
                                , packageType.name(),EntityType.TOPUP.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                    }
                    else if (pakage.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                        lastKnownGoodCounter++;
                        lastKnownGoodPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.LAST_KNOWN_GOOD
                                , packageType.name(),EntityType.TOPUP.getValue(), pakage.getMode(), pakage.getPartialFailReason()));
                    } else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.FAILURE, packageType.name(),EntityType.TOPUP.getValue(),
                                pakage.getMode(),
                                pakage.getFailReason()));
                    }
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail("", quotaTopUpName, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.getSubType(),EntityType.TOPUP.getValue(), PkgMode.DESIGN, "Not found"));
                }
            }
        }


        policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                withFailurePolicyList(failPolicyList).
                withPartialSuccessPolicyList(partialSuccessPolicyList).
                withPartialSuccessCounter(partialSuccessCounter).
                withSuccessPolicyList(successPolicyList).
                withSuccessCounter(successCounter).
                withLastKnownGoodCounter(lastKnownGoodCounter).
                withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();
        return policyCacheDetail;
    }

    private PolicyCacheDetail buildPolicyCacheDetailAfterReloadOffer(List<String> offerNames) {
        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList<>();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList<>();
        List<PolicyDetail> successPolicyList = new ArrayList<>();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList<>();

        for (String offerName : offerNames) {

            if (Strings.isNullOrBlank(offerName) == false) {

                ProductOffer offerData = productOfferStore.byName(offerName);

                if (offerData != null) {

                    PkgType packageType = offerData.getType();
                    if (offerData.getPolicyStatus() == PolicyStatus.SUCCESS) {
                        successCounter++;
                        successPolicyList.add(new PolicyDetail(offerData.getId(), offerData.getName(), offerData.getPolicyStatus()
                                , packageType.val,EntityType.OFFER.getValue(), offerData.getPackageMode(), Objects.nonNull(offerData.getFailReason())?offerData.getFailReason().toString():null));
                    } else if(offerData.getPolicyStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                        partialSuccessCounter++;
                        partialSuccessPolicyList.add(new PolicyDetail(offerData.getId(), offerData.getName(), offerData.getPolicyStatus(), packageType.val,EntityType.OFFER.getValue(),
                                offerData.getPackageMode(),
                                Objects.nonNull(offerData.getPartialFailReason())?offerData.getPartialFailReason().toString():null));
                    } else if(offerData.getPolicyStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                        lastKnownGoodCounter++;

                        String remark = offerData.getFailReason()==null?null:offerData.getFailReason().toString();

                        if(Strings.isNullOrBlank(remark)){
                            remark = offerData.getPartialFailReason()==null?null:offerData.getPartialFailReason().toString();
                        }

                        lastKnownGoodPolicyList.add(new PolicyDetail(offerData.getId(), offerData.getName(), offerData.getPolicyStatus(), packageType.name(),EntityType.OFFER.getValue(),
                                offerData.getPackageMode(),remark));
                    }  else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail(offerData.getId(), offerData.getName(), PolicyStatus.FAILURE, packageType.val,EntityType.OFFER.getValue(),
                                offerData.getPackageMode(),
                                Objects.nonNull(offerData.getFailReason())?offerData.getFailReason().toString():null));
                    }
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail("", offerName, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.getSubType(),EntityType.OFFER.getValue(), PkgMode.DESIGN, "Not found"));
                }
            }
        }


        policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                withFailurePolicyList(failPolicyList).
                withPartialSuccessPolicyList(partialSuccessPolicyList).
                withPartialSuccessCounter(partialSuccessCounter).
                withSuccessPolicyList(successPolicyList).
                withSuccessCounter(successCounter).
                withLastKnownGoodCounter(lastKnownGoodCounter).
                withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();
        return policyCacheDetail;
    }

    private PolicyCacheDetail buildPolicyCacheForRncPackages(List<String> packageNames) {
        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = new ArrayList();
        List<PolicyDetail> partialSuccessPolicyList = new ArrayList();
        List<PolicyDetail> successPolicyList = new ArrayList();
        List<PolicyDetail> lastKnownGoodPolicyList = new ArrayList();

        for (String packageName : packageNames) {

            if (Strings.isNullOrBlank(packageName) == false) {

                RnCPackage rncPkg = rnCPackageStore.byName(packageName);

                if (rncPkg != null) {

                    RnCPkgType packageType = rncPkg.getPkgType();
                    if (rncPkg.getPolicyStatus() == PolicyStatus.SUCCESS) {
                        successCounter++;
                        successPolicyList.add(new PolicyDetail(rncPkg.getId(), rncPkg.getName(), PolicyStatus.SUCCESS
                                , packageType.getVal(),EntityType.RNC.getValue(), rncPkg.getPackageMode(), rncPkg.getFailReason()));
                    } else if(rncPkg.getPolicyStatus() == PolicyStatus.PARTIAL_SUCCESS) {

                        partialSuccessCounter++;
                        partialSuccessPolicyList.add(new PolicyDetail(rncPkg.getId(), rncPkg.getName(), PolicyStatus.PARTIAL_SUCCESS, packageType.getVal(),EntityType.RNC.getValue(),
                                rncPkg.getPackageMode(),
                                rncPkg.getPartialFailReason()));

                    } else if(rncPkg.getPolicyStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                        lastKnownGoodCounter++;

                        String remark = rncPkg.getFailReason();

                        if(Strings.isNullOrBlank(remark)){
                            remark = rncPkg.getPartialFailReason();
                        }

                        lastKnownGoodPolicyList.add(new PolicyDetail(rncPkg.getId(), rncPkg.getName(), rncPkg.getPolicyStatus(), packageType.getVal(),EntityType.RNC.getValue(),
                                rncPkg.getPackageMode(),remark));
                    } else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail(rncPkg.getId(), rncPkg.getName(), PolicyStatus.FAILURE, packageType.getVal(),EntityType.RNC.getValue(),
                                rncPkg.getPackageMode(),
                                rncPkg.getFailReason()));
                    }
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail("", packageName, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.getSubType(),EntityType.RNC.getValue(), PkgMode.DESIGN, "Not found"));
                }
            }
        }


        policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                withFailurePolicyList(failPolicyList).
                withPartialSuccessPolicyList(partialSuccessPolicyList).
                withPartialSuccessCounter(partialSuccessCounter).
                withSuccessPolicyList(successPolicyList).
                withSuccessCounter(successCounter).
                withLastKnownGoodCounter(lastKnownGoodCounter).
                withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();
        return policyCacheDetail;
    }

    private PolicyCacheDetail buildPolicyCacheDetailAfterReloadData(List<String> pkgNames) {
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = Collectionz.newArrayList();
        List<PolicyDetail> partialSuccessPolicyList = Collectionz.newArrayList();
        List<PolicyDetail> successPolicyList = Collectionz.newArrayList();
        List<PolicyDetail> lastKnownGoodPolicyList = Collectionz.newArrayList();
        PackageType packageType = null;
        for (String name : pkgNames) {

            if (Strings.isNullOrEmpty(name)) {
                continue;
            }

            Package pakage = this.policyStore.byName(name);

            if (pakage != null) {

                packageType = getPackageType(pakage);

                String entityType = (packageType.name().equals(EntityType.EMERGENCY.name())) ? EntityType.EMERGENCY.getValue() : (packageType.name().equals(EntityType.PROMOTIONAL.name())) ? EntityType.PROMOTIONAL.getValue() : EntityType.DATA.getValue();

                if (pakage.getStatus() == PolicyStatus.SUCCESS) {
                    successCounter++;
                    successPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.SUCCESS
                            , packageType.getSubType(),entityType, pakage.getMode(), pakage.getFailReason()));
                } else if (pakage.getStatus() == PolicyStatus.PARTIAL_SUCCESS) {
                    partialSuccessCounter++;
                    partialSuccessPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.PARTIAL_SUCCESS
                            , packageType.getSubType(), entityType, pakage.getMode(), pakage.getPartialFailReason()));
                } else if (pakage.getStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                    lastKnownGoodCounter++;
                    lastKnownGoodPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.LAST_KNOWN_GOOD
                            , packageType.getSubType(), entityType, pakage.getMode(), pakage.getPartialFailReason()));
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail(pakage.getId(), pakage.getName(), PolicyStatus.FAILURE, packageType.getSubType(),entityType, pakage
                            .getMode(), pakage.getFailReason()));
                }
            } else {
                failureCounter++;
                failPolicyList.add(new PolicyDetail("",name, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.getSubType(),EntityType.DATA.getValue(), PkgMode.DESIGN, "Not found"));
            }
        }

        return new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                withFailurePolicyList(failPolicyList).
                withPartialSuccessPolicyList(partialSuccessPolicyList).
                withPartialSuccessCounter(partialSuccessCounter).
                withSuccessPolicyList(successPolicyList).
                withSuccessCounter(successCounter).
                withLastKnownGoodCounter(lastKnownGoodCounter).
                withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();
    }

    private static class GroupFilter implements com.elitecore.commons.base.Predicate<PkgData> {

        private final List<String> groupIds;

        private GroupFilter(List<String> groupIds) {
            this.groupIds = groupIds;
        }

        @Override
        public boolean apply(PkgData pkgData) {
            if (Strings.isNullOrBlank(pkgData.getGroups())) {
                return false;
            }

            List<String> pkgGroupIds = CommonConstants.COMMA_SPLITTER.split(pkgData.getGroups());

            for (String inputGroupId : groupIds) {
                if (pkgGroupIds.contains(inputGroupId)) {
                    return true;
                }
            }
            return false;
        }

        public static GroupFilter create(List<String> groupIds) {
            return new GroupFilter(groupIds);
        }
    }

    @Override
    public ArrayList<EmergencyPackage> getEmergencyPackagesOfGroup(String groupId) {
        return (ArrayList<EmergencyPackage>) emergency().byGroupId(groupId);
    }

    @Override
    public ArrayList<PromotionalPackage> getPromotionalPackagesOfGroup(String groupId) {
        return (ArrayList<PromotionalPackage>) promotional().byGroupId(groupId);
    }


    @Override
    public ServiceStore getService(){
        return serviceStore;
    }

    @Override
    public ProductOfferStore<ProductOffer> getProductOffer(){
        return productOfferStore;
    }

    @Override
    public RnCPackageStore getRnCPackage() {
        return rnCPackageStore;
    }

    @Override
    public QuotaTopUp getActiveQuotaTopUpByName(String name) {
        return quotaTopUp().active().byName(name);
    }

    @Override
    public QuotaTopUp getActiveQuotaTopUpById(String id) {
        return quotaTopUp().active().byId(id);
    }

    @Override
    public QuotaTopUp getQuotaTopUpById(String id) {
        return quotaTopUp().byId(id);
    }

    @Override
    public QuotaTopUp getQuotaTopUpByName(String name) {
        return quotaTopUp().byName(name);
    }

    @Override
    public MonetaryRechargePlan getActiveMonetaryRechargePlanByName(String name) {
        return monetaryRechargePlan().active().byName(name);
    }

    @Override
    public MonetaryRechargePlan getActiveMonetaryRechargePlanById(String id) {
        return monetaryRechargePlan().active().byId(id);
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
        List<MonetaryRechargePlan> monetaryRechargePlans = monetaryRechargePlan().active().all();

        for (MonetaryRechargePlan monetaryRechargePlan : monetaryRechargePlans) {
            if (monetaryRechargePlan.getPrice().equals(price)) {
                return monetaryRechargePlan;
            }
        }

        return null;
    }

    public void stop() {

        writePackagesForBackup();

        getLogger().info(MODULE, "Closing hibernate session factroy");

        if (sessionFactory != null) {
            sessionFactory.close();
        }

        getLogger().info(MODULE, "Hibernate session factroy closed successfully");
    }

    public void updatePolicyReloadTime() {
        policyReloadTime.add(System.currentTimeMillis());
    }

    @Override
    public @Nullable  QuotaProfile getQuotaProfile(String packageId, String quotaProfileId) {
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

    @Override
    public @Nullable DataRateCard getDataRateCard(String packageId, String rateCardId) {
        UserPackage userPackage = getPkgDataById(packageId);
        if (userPackage != null) {
            return userPackage.getDataRateCard(rateCardId);
        }
        return null;
    }

    @Override
    public @Nullable String getPackageName(String packageId) {
        UserPackage userPackage = getPkgDataById(packageId);
        if (userPackage != null) {
            return userPackage.getName();
        }

        QuotaTopUp quotaTopUp = quotaTopUp().byId(packageId);
        if (quotaTopUp != null) {
            return quotaTopUp.getName();
        }

        RnCPackage rnCPackage = rnCPackageStore.byId(packageId);
        if (rnCPackage != null) {
            return rnCPackage.getName();
        }

        return null;
    }

    @Override
    public BoDPackageStore getBoDPackage() {
        return boDPackageStore;
    }

    @Override
    public synchronized PolicyCacheDetail reloadBoDPackages(Predicate<BoDData> predicate){
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading BoD packages");
        }

        Session session = null;
        try {
            session = sessionFactory.openSession();
            List<BoDData> newPackages = hibernateDataReader.readAll(BoDData.class, session);

            //Collect Deleted And Inactive BoD Packages to remove existing cache
            List<String> deletedOrInActiveBoDPackages = newPackages.stream()
                    .filter(INACTIVE_DELETED_RESOURCE_FILTER)
                    .map(ResourceData::getId)
                    .collect(Collectors.toList());

            //Collect purged BoD packages
            populatePurgedBoDPackagesInDeletedIds(newPackages, deletedOrInActiveBoDPackages, boDPackageStore.all());

            newPackages = newPackages.stream()
                    .filter(bodData -> ResourceDataStatusFilter.getInstance().apply(bodData)
                            && predicate.test(bodData)
                            && PkgMode.DESIGN.name().equalsIgnoreCase(bodData.getPackageMode()) == false)
                                .collect(Collectors.toList());

            List<BoDPackage> bodPackages = newArrayList();

            newPackages.forEach(bodPkg->{
                BoDPackage bodPackage = boDPackageFactory.create(bodPkg);
                bodPackages.add(bodPackage);
            });

            this.boDPackageStore.create(bodPackages, deletedOrInActiveBoDPackages);

            List<String> bodNames = newPackages.stream().map(BoDData::getName).collect(Collectors.toList());
            PolicyCacheDetail policyCacheDetail = buildPolicyCacheForBoDPackages(bodNames);

            writeBoDPackagesForBackup();

            listHistory.add(policyCacheDetail);
            getLogger().info(MODULE, "BoD packages reloaded successfully");
            return policyCacheDetail;

        } finally {
            closeQuietly(session);
        }
    }

    private void populatePurgedBoDPackagesInDeletedIds(List<BoDData> packagesFromNewCache
            , List<String> deletedOrInActiveBoDPackages, List<BoDPackage> cachedPackages) {
        Set<String> packageIdsFromNewCache = packagesFromNewCache.stream().map(BoDData::getId).collect(Collectors.toSet());

        cachedPackages.stream()
                .filter(pkg -> packageIdsFromNewCache.contains(pkg.getId()) == false)
                .forEach(pkg-> deletedOrInActiveBoDPackages.add(pkg.getId()));
    }

    private void writeBoDPackagesForBackup() {

        getLogger().info(MODULE, "BoD Package backup generation started");

        try {
            SerializationUtil.serialize(boDPackageStore.all(), bodPackagesBackupFilePath);
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while writing to BoD Package backup file. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
        getLogger().info(MODULE, "BoD Package backup generation completed.");
    }

    @Nullable
    private List<BoDPackage> readBackUpBoDPackages() {

        List<BoDPackage> backedUpPackages = newArrayList();
        getLogger().info(MODULE, "Reading of backUp BoD packages started");

        File backupFile = new File(bodPackagesBackupFilePath);
        if (backupFile.exists() == false || backupFile.isDirectory()) {
            getLogger().info(MODULE, "BoD package backup file not available.");
            return backedUpPackages;
        }

        try {
            backedUpPackages.addAll((List<BoDPackage>) SerializationUtil.deserialize(backupFile.getAbsolutePath()));
            return backedUpPackages;
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while reading BoD package backup file. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

        getLogger().info(MODULE, "Reading of backUp BoD packages completed");
        return backedUpPackages;
    }

    private PolicyCacheDetail buildPolicyCacheForBoDPackages(List<String> packageNames) {
        PolicyCacheDetail policyCacheDetail;
        int failureCounter = 0;
        int successCounter = 0;
        int partialSuccessCounter = 0;
        int lastKnownGoodCounter = 0;
        List<PolicyDetail> failPolicyList = newArrayList();
        List<PolicyDetail> partialSuccessPolicyList = newArrayList();
        List<PolicyDetail> successPolicyList = newArrayList();
        List<PolicyDetail> lastKnownGoodPolicyList = newArrayList();

        for (String packageName : packageNames) {

            if (Strings.isNullOrBlank(packageName) == false) {

                BoDPackage boDPackage = boDPackageStore.byName(packageName);

                if (boDPackage != null) {
                    if (boDPackage.getPolicyStatus() == PolicyStatus.SUCCESS) {
                        successCounter++;
                        successPolicyList.add(new PolicyDetail(boDPackage.getId(), boDPackage.getName(), PolicyStatus.SUCCESS
                                , null, EntityType.BOD.getValue(), boDPackage.getPackageMode(), boDPackage.getFailReason()));
                    } else if(boDPackage.getPolicyStatus() == PolicyStatus.LAST_KNOWN_GOOD) {
                        lastKnownGoodCounter++;

                        String remark = boDPackage.getFailReason();

                        lastKnownGoodPolicyList.add(new PolicyDetail(boDPackage.getId(), boDPackage.getName(), boDPackage.getPolicyStatus(), EntityType.BOD.getValue(), EntityType.BOD.getValue(),
                                boDPackage.getPackageMode(),remark));
                    } else {
                        failureCounter++;
                        failPolicyList.add(new PolicyDetail(boDPackage.getId(), boDPackage.getName(), PolicyStatus.FAILURE, EntityType.BOD.getValue(), EntityType.BOD.getValue(),
                                boDPackage.getPackageMode(),
                                boDPackage.getFailReason()));
                    }
                } else {
                    failureCounter++;
                    failPolicyList.add(new PolicyDetail("", packageName, PolicyStatus.UNKNOWN, PackageType.UNKNOWN.getSubType(), EntityType.BOD.getValue(), PkgMode.DESIGN, "Not found"));
                }
            }
        }


        policyCacheDetail = new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).
                withFailurePolicyList(failPolicyList).
                withPartialSuccessPolicyList(partialSuccessPolicyList).
                withPartialSuccessCounter(partialSuccessCounter).
                withSuccessPolicyList(successPolicyList).
                withSuccessCounter(successCounter).
                withLastKnownGoodCounter(lastKnownGoodCounter).
                withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();
        return policyCacheDetail;
    }

    @Override
    public List<PolicyDetail> getBoDPackageDetail(Predicate<BoDPackage>  bodPkgFilter) {
        ArrayList<PolicyDetail> bodDetails = newArrayList();

        for (BoDPackage boDPackage : boDPackageStore.all()) {
            if (bodPkgFilter.test(boDPackage) == false) {
                continue;
            }
            String remarks = createRemarks(boDPackage);
            bodDetails.add(new PolicyDetail(boDPackage.getId(), boDPackage.getName(), boDPackage.getPolicyStatus(), EntityType.BOD.getValue(),
                    EntityType.BOD.getValue(), boDPackage.getPackageMode(),remarks));
        }
        Collections.sort(bodDetails, Collections.reverseOrder());
        return bodDetails;
    }

    private String createRemarks(BoDPackage boDPackage) {
        StringBuilder remarkBuilder = new StringBuilder();
        if (Strings.isNullOrBlank(boDPackage.getFailReason()) == false) {
            remarkBuilder.append("Fail Reasons: " + boDPackage.getFailReason());
        }
        if (remarkBuilder.length() < 1) {
            return null;
        }
        return remarkBuilder.toString();
    }

}