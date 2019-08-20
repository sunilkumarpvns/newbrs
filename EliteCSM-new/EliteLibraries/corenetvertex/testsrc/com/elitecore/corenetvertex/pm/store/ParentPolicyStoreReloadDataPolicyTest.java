package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.util.MockBasePackage;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

//Status_Fail
 /*      WhenFailedPackagePassed
         *             NotAddPackage_WhenPreviousPackageNotExist
         *             Add_WhenPreviousPackageExist
         *
         *
         * //Deleted
         *      WhenDeletedPackageIdPassed
         *             RemoveOldCachedPackageOnly (Should not check current provided packages)
         *
         * //Reload
         *      Create Should replace old packages when new package with same id passed
         *      Create Should retain old packages when some packages passed
         */


@RunWith(HierarchicalContextRunner.class)
public class ParentPolicyStoreReloadDataPolicyTest {

    public static final String ID_1 = "ID1";
    public static final String NAME_1 = "NAME1";
    public static final String IMS_NAME_1 = "IMS_NAME1";
    public static final String IMS_ID_1 = "IMS_ID1";
    public static final String NAME_2 = "NAME_2";
    public static final String ID_2 = "ID_2";
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
        PolicyProvider provider = new PolicyProvider();
        provider.addBasePolicy(ID_1, NAME_1).policyStatusSuccess();
        store.create(provider.getPackages(), groupManageOrders, deletedInactivePackagesIds, provider.getIMSPackages(), deletedInactiveIMSPackageIds,
                quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);
    }

    public class WhenFailedPackagePassed {

        @Test
        public void AddOldPackage_WhenPreivousPackageExistWithSameId() {

            Package oldPackage = store.byId(ID_1);
            PolicyProvider provider = new PolicyProvider();
            provider.addBasePolicy(ID_1, NAME_1).policyStatusFailure();
            store.reloadDataPolicy(provider.getPackages(), groupManageOrders, deletedInactivePackagesIds);

            assertEquals(1, store.all().size());
            assertSame(oldPackage, store.byId(ID_1));
            assertSame(oldPackage, store.byName(NAME_1));
        }

        @Test
        public void RetainOldPackage() {

            Package oldPackage = store.byId(ID_1);
            PolicyProvider provider = new PolicyProvider();
            MockBasePackage newPackage = provider.addBasePolicy(ID_2, NAME_2).policyStatusFailure();
            store.reloadDataPolicy(provider.getPackages(), groupManageOrders, deletedInactivePackagesIds);

            assertEquals(2, store.all().size());
            assertSame(oldPackage, store.byId(ID_1));
            assertSame(oldPackage, store.byName(NAME_1));
            assertSame(newPackage, store.byId(ID_2));
            assertSame(newPackage, store.byName(NAME_2));
        }
    }

    public class WhenSuccessPackagePassed {

        @Test
        public void test_ReplaceOldPackage_WhenNewPackageIsPackageWithSameId() {
            Package oldPackage = store.byId(ID_1);
            PolicyProvider provider = new PolicyProvider();

            MockBasePackage newPackageWithSameId = provider.addBasePolicy(ID_1, NAME_1).policyStatusSuccess();
            store.reloadDataPolicy(provider.getPackages(), groupManageOrders, deletedInactivePackagesIds);

            assertEquals(1, store.all().size());
            assertNotSame(oldPackage, store.byId(ID_1));
            assertNotSame(oldPackage, store.byName(NAME_1));
            assertSame(newPackageWithSameId, store.byId(ID_1));
            assertSame(newPackageWithSameId, store.byName(NAME_1));
        }

        @Test
        public void RetainOldPackage() {

            Package oldPackage = store.byId(ID_1);
            PolicyProvider provider = new PolicyProvider();
            MockBasePackage newPackage = provider.addBasePolicy(ID_2, NAME_2).policyStatusFailure();
            store.reloadDataPolicy(provider.getPackages(), groupManageOrders, deletedInactivePackagesIds);

            assertEquals(2, store.all().size());
            assertSame(oldPackage, store.byId(ID_1));
            assertSame(oldPackage, store.byName(NAME_1));
            assertSame(newPackage, store.byId(ID_2));
            assertSame(newPackage, store.byName(NAME_2));
        }
    }

    public class WhenDeletedDataPackageIdPassed {

        public static final String NAME_2 = "NAME_2";
        public static final String ID_2 = "ID_2";

        @Test
        public void RemovePackage() {
            Package oldPackage = store.byId(ID_1);
            PolicyProvider provider = new PolicyProvider();

            store.reloadDataPolicy(provider.getPackages(), groupManageOrders, Arrays.asList(ID_1));

            assertTrue(store.all().isEmpty());
            assertNull(store.byId(ID_1));
            assertNull(store.byName(NAME_1));
        }
    }
}
