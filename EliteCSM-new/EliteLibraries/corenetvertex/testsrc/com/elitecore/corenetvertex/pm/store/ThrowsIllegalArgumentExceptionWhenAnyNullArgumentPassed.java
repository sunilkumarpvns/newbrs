package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JUnitParamsRunner.class)
public class ThrowsIllegalArgumentExceptionWhenAnyNullArgumentPassed {
    private ParentPolicyStore store;

    public void setUp() {
        this.store = new ParentPolicyStore(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }


    public Object[][] dataProviderFor_NPE() {

        List<Package> packagesAdded = newList();
        List<GroupManageOrder> groupManageOrders = newList();
        List<String> deletedInactivePackagesIds = newList();
        List<IMSPackage> imsPackagesAdded = newList();
        List<String> deletedInactiveIMSPackageIds = newList();
        List<QuotaTopUp> quotaTopUpsAdded= newList();
        List<String> deletedInactiveQuotaTopUpsIds = newList();
        List<MonetaryRechargePlan> monetaryRechargePlansAdded = newList();
        List<String> deletedInactiveMonetaryRechargePlanIds = newList();

        return new Object[][]{
                {
                        null,
                        groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds, quotaTopUpsAdded,
                        deletedInactiveQuotaTopUpsIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds
                },
                {
                        packagesAdded,
                        null, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds, quotaTopUpsAdded,
                        deletedInactiveQuotaTopUpsIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds
                },
                {
                        packagesAdded,
                        groupManageOrders, null, imsPackagesAdded, deletedInactiveIMSPackageIds, quotaTopUpsAdded,
                        deletedInactiveQuotaTopUpsIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds
                },
                {
                        packagesAdded,
                        groupManageOrders, deletedInactivePackagesIds, null, deletedInactiveIMSPackageIds, quotaTopUpsAdded,
                        deletedInactiveQuotaTopUpsIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds
                },
                {
                        packagesAdded,
                        groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds, null,
                        deletedInactiveQuotaTopUpsIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds
                },
        };
    }

    @Test(expected = NullPointerException.class)
    @Parameters(method = "dataProviderFor_NPE")
    public void test_ThrowIllegalArgumentException_WhenNullPackagesPassed(List<Package> packagesAdded,
                                                                          List<GroupManageOrder> groupManageOrders,
                                                                          List<String> deletedInactivePackagesIds,
                                                                          List<IMSPackage> imsPackagesAdded,
                                                                          List<String> deletedInactiveIMSPackageIds,
                                                                          List<QuotaTopUp> quotaTopUpsAdded,
                                                                          List<String> deletedInactiveQuotaTopUpsIds,
                                                                          List<MonetaryRechargePlan> monetaryRechargePlansAdded,
                                                                          List<String> deletedInactiveMonetaryRechargePlanIds) {

        store.create(packagesAdded, groupManageOrders, deletedInactivePackagesIds, imsPackagesAdded, deletedInactiveIMSPackageIds,
                quotaTopUpsAdded, deletedInactiveQuotaTopUpsIds, monetaryRechargePlansAdded, deletedInactiveMonetaryRechargePlanIds);
    }

    public <T, V> Map<T, V> newMap() {
        return new HashMap<>();
    }

    public <T> List<T> newList() {
        return new ArrayList<>();
    }
}
