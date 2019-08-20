package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.util.MockBasePackage;
import com.elitecore.corenetvertex.pm.util.MockIMSPackage;
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(HierarchicalContextRunner.class)
public class ParentPolicyStoreBackUpScenario {

    public static final String ID_1 = "ID1";
    public static final String NAME_1 = "NAME1";
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
    }

    public class DataPolicy {

        @Before
        public void setUp() {
            PolicyProvider provider = new PolicyProvider();
            provider.addBasePolicy(ID_1, NAME_1).policyStatusSuccess();
            backUpDataPackagesById.put(ID_1, provider.getBasePackageDataById(ID_1));
            store = new ParentPolicyStore(backUpDataPackagesById, backUpIMSPackagesById, backUpQuotatopUpsById, backUpMonetaryRechargePlansById);
            store.create(provider.getPackages(), groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                    quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);
        }

        public class WhenFailedPackagePassed {

            @Test
            public void AddBackUpPackage_WhenBackUpPackageExistWithSameId() {

                Package oldPackage = store.byId(ID_1);
                PolicyProvider provider = new PolicyProvider();
                provider.addBasePolicy(ID_1, NAME_1).policyStatusFailure();
                store.create(provider.getPackages(), groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                        quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);

                assertEquals(1, store.all().size());
                assertSame(oldPackage, store.byId(ID_1));
                assertSame(oldPackage, store.byName(NAME_1));
            }

        }

        public class WhenSuccessPackagePassed {

            @Test
            public void AddNewPackage() {

                PolicyProvider provider = new PolicyProvider();
                MockBasePackage newPackage = provider.addBasePolicy(ID_1, NAME_1).policyStatusSuccess();
                store.create(provider.getPackages(), groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                        quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);

                assertEquals(1, store.all().size());
                assertSame(newPackage, store.byId(ID_1));
                assertSame(newPackage, store.byName(NAME_1));
            }
        }
    }

    public class IMSPolicy {

        @Before
        public void setUp() {
            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_1, NAME_1).policyStatusSuccess();
            backUpIMSPackagesById.put(ID_1, provider.getIMSPkgById(ID_1));
            store = new ParentPolicyStore(backUpDataPackagesById, backUpIMSPackagesById, backUpQuotatopUpsById, backUpMonetaryRechargePlansById);
            store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, provider.getIMSPackages(), deletedInactiveIMSPackageIds,
                    quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);
        }

        public class WhenFailedPackagePassed {

            @Test
            public void AddBackUpPackage_WhenBackUpPackageExistWithSameId() {

                IMSPackage oldPackage = store.ims().byId(ID_1);
                PolicyProvider provider = new PolicyProvider();
                provider.addIMSPolicy(ID_1, NAME_1).policyStatusFailure();
                store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, provider.getIMSPackages(), deletedInactiveIMSPackageIds,
                        quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);

                assertEquals(1, store.ims().all().size());
                assertSame(oldPackage, store.ims().byId(ID_1));
                assertSame(oldPackage, store.ims().byName(NAME_1));
            }
        }

        public class WhenSuccessPackagePassed {

            @Test
            public void AddNewPackage() {

                PolicyProvider provider = new PolicyProvider();
                MockIMSPackage newPackage = provider.addIMSPolicy(ID_1, NAME_1).policyStatusSuccess();
                store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, provider.getIMSPackages(), deletedInactiveIMSPackageIds,
                        quotaTopUpsAdded, deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);

                assertEquals(1, store.ims().all().size());
                assertSame(newPackage, store.ims().byId(ID_1));
                assertSame(newPackage, store.ims().byName(NAME_1));
            }
        }
    }

    public class QuotaTopUpPolicy {

        @Before
        public void setUp() {
            PolicyProvider provider = new PolicyProvider();
            provider.addQuotaTopUpPolicy(ID_1, NAME_1).policyStatusSuccess();
            backUpQuotatopUpsById.put(ID_1, provider.getQuotaTopUpById(ID_1));
            store = new ParentPolicyStore(backUpDataPackagesById, backUpIMSPackagesById, backUpQuotatopUpsById, backUpMonetaryRechargePlansById);
            store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                    provider.getQuotaTopUps(), deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);
        }

        public class WhenFailedPackagePassed {

            @Test
            public void AddBackUpPackage_WhenBackUpPackageExistWithSameId() {

                QuotaTopUp oldPackage = store.quotaTopUp().byId(ID_1);
                PolicyProvider provider = new PolicyProvider();
                provider.addQuotaTopUpPolicy(ID_1, NAME_1).policyStatusFailure();
                store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                        provider.getQuotaTopUps(), deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);

                assertEquals(1, store.quotaTopUp().all().size());
                assertSame(oldPackage, store.quotaTopUp().byId(ID_1));
                assertSame(oldPackage, store.quotaTopUp().byName(NAME_1));
            }
        }

        public class WhenSuccessPackagePassed {

            @Test
            public void AddNewPackage() {

                PolicyProvider provider = new PolicyProvider();
                MockQuotaTopUp newPackage = provider.addQuotaTopUpPolicy(ID_1, NAME_1).policyStatusSuccess();
                store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                        provider.getQuotaTopUps(), deletedInactiveQuotaTopUpIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);

                assertEquals(1, store.quotaTopUp().all().size());
                assertSame(newPackage, store.quotaTopUp().byId(ID_1));
                assertSame(newPackage, store.quotaTopUp().byName(NAME_1));
            }
        }
    }


}
