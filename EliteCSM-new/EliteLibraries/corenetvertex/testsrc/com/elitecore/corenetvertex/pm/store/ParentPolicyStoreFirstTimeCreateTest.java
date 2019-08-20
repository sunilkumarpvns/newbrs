package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class ParentPolicyStoreFirstTimeCreateTest {

    private static final String ID_1 = "ID1";
    private static final String NAME_1 = "NAME1";
    private ParentPolicyStore store;
    private Map<String, Package> backUpDataPackagesById;
    private Map<String, IMSPackage> backUpIMSPackagesById;
    private Map<String, QuotaTopUp> backUpQuotatopUpsById;
    private Map<String, MonetaryRechargePlan> backUpMonetaryRechargePlansById;
    private List<Package> packagesAdded;
    private List<GroupManageOrder> groupManageOrders;
    private List<String> deletedInactivePackagesIds;
    private List<IMSPackage> imsPackagesAdded;
    private List<String> deletedInactiveIMSPackageIds;
    private List<QuotaTopUp> quotaTopUpsAdded;
    private List<String> deletedInactiveQuotaTopUpIds;
    private List<MonetaryRechargePlan> monetaryRechargePlansAdded;
    private List<String> deletedInactiveMonetaryRechargePlanIds;

    @Before
    public void setUp() {
        this.backUpDataPackagesById = new HashMap<>();
        this.backUpIMSPackagesById = new HashMap<>();
        this.backUpQuotatopUpsById = new HashMap<>();
        this.backUpMonetaryRechargePlansById = new HashMap<>();
        this.packagesAdded = new ArrayList<>();
        this.groupManageOrders = new ArrayList<>();
        this.deletedInactivePackagesIds = new ArrayList<>();
        this.imsPackagesAdded = new ArrayList<>();
        this.deletedInactiveIMSPackageIds = new ArrayList<>();
        this.quotaTopUpsAdded = new ArrayList<>();
        this.deletedInactiveQuotaTopUpIds = new ArrayList<>();
        this.monetaryRechargePlansAdded = new ArrayList<>();
        this.deletedInactiveMonetaryRechargePlanIds = new ArrayList<>();
        this.store = new ParentPolicyStore(backUpDataPackagesById, backUpIMSPackagesById, backUpQuotatopUpsById, backUpMonetaryRechargePlansById);
    }

    @Test
    public void test_NotAddAnyPackage_WhenEmptyPackageIsPassed() {
        store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);

        assertTrue(store.all().isEmpty());
    }

    @Test
    public void test_AddPackage_WhenFailedPackageIsPassed() {

        PolicyProvider provider = new PolicyProvider();
        provider.addBasePolicy(ID_1, NAME_1).policyStatusFailure();
        List<Package> packagesAdded = provider.getAllPackageDatas();

        store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);

        assertFalse(store.all().isEmpty());
        assertSame(provider.getBasePackageDataById(ID_1), store.byId(ID_1));
        assertSame(provider.getBasePackageDataByName(NAME_1), store.byName(NAME_1));
    }

    @Test
    public void test_AddPackage_WhenSuccessPackageIsPassed() {
        PolicyProvider provider = new PolicyProvider();
        provider.addBasePolicy(ID_1, NAME_1).policyStatusSuccess();
        List<Package> packagesAdded = provider.getAllPackageDatas();

        store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);

        assertFalse(store.all().isEmpty());
        assertSame(provider.getBasePackageDataById(ID_1), store.byId(ID_1));
        assertSame(provider.getBasePackageDataByName(NAME_1), store.byName(NAME_1));
    }

    public class ChildStoreMethods {

        @Test
        public void base__GiveDataPolicyStore() {
            assertNotNull(store.base());
        }

        @Test
        public void addOn__GiveDataPolicyStore() {
            assertNotNull(store.addOn());
        }

        @Test
        public void emergency__GiveBasePolicyStore() {
            assertNotNull(store.emergency());
        }

        @Test
        public void promotional__GivePromotionalPolicyStore() {
            assertNotNull(store.promotional());
        }

        @Test
        public void pcc__GivePCCRuleStore() {
            assertNotNull(store.pccRule());
        }

        @Test
        public void chargingRuleBaseName__GiveChargingRuleBaseNameStore() {
            assertNotNull(store.chargingRuleBaseName());
        }

        @Test
        public void ims__GiveIMSPolicyStore() {
            assertNotNull(store.ims());
        }

        @Test
        public void quotaTopUp_giveQuotaTopUpPolicyStore() {
            assertNotNull(store.quotaTopUp());
        }
    }
}
