package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.elitecore.corenetvertex.pm.store.PolicyStoreTestUtil.filterAlwaysFalse;
import static com.elitecore.corenetvertex.pm.store.PolicyStoreTestUtil.filterAlwaysTrue;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class GlobalPolicyStoreTest {

    private static final String ID1 = "pkgId1";
    private static final String ID2 = "pkgId2";
    private static final String ID3 = "pkgId3";
    private static final String PKG_NAME1 = "pkgName1";
    private static final String PKG_NAME3 = "pkgName3";
    private static final String PKG_NAME2 = "pkgName2";
    private static final List<String> GROUPS = Arrays.asList("GROUP1", "GROUP2", "GROUP3", "GROUP4", "GROUP5");

    @Test(expected = NullPointerException.class)
    public void test_create_ThrowNPE_WhenNullPackagesPassed() throws Exception {
        GlobalPolicyStore<EmergencyPackage> store = new GlobalPolicyStore<>(filterAlwaysTrue(), filterAlwaysTrue());

        List<Package> packages = null;
        List<GroupManageOrder> globalManageOrders = new ArrayList<>();

        store.create(packages, globalManageOrders);
    }

    @Test(expected = NullPointerException.class)
    public void test_create_ThrowNPE_WhenNullManageOrderPassed() throws Exception {
        GlobalPolicyStore<EmergencyPackage> store = new GlobalPolicyStore<>(filterAlwaysTrue(), filterAlwaysTrue());

        List<Package> packages = new ArrayList<>();
        List<GroupManageOrder> globalManageOrders = null;

        store.create(packages, globalManageOrders);
    }

    @Test
    public void test_create_NotAddPackages_WhenFilterIsNotSatisfied() {
        PolicyProvider provider = new PolicyProvider();
        provider.addEmergencyGroupManageOrder(GROUPS.get(0), ID1, 1);
        provider.addEmergencyPackage(ID1, PKG_NAME1);

        GlobalPolicyStore<EmergencyPackage> store = new GlobalPolicyStore<>(filterAlwaysFalse(), filterAlwaysTrue());
        store.create(provider.getPackages(),  provider.getGroupManageOrders());

        assertTrue(store.all().isEmpty());
    }

    @Test
    public void test_create_AddPackages_WhenFilterIsSatisfied() {

        PolicyProvider provider = new PolicyProvider();
        provider.addEmergencyPackage(ID1, PKG_NAME1);

        GlobalPolicyStore<EmergencyPackage> store = new GlobalPolicyStore<>(filterAlwaysTrue(), filterAlwaysTrue());
        store.create(provider.getPackages(), new ArrayList<>());

        assertSame(provider.getEmergencyPackagebyId(ID1), store.byId(ID1));
        assertSame(provider.getEmergencyPackageByName(PKG_NAME1), store.byName(PKG_NAME1));
    }

    /*
     * manage order =============
	 *
	 * group1 > em1
	 * group2 > em2, em1, em3
	 * grup3 > em1, em3
	 */
    @Test
    public void test_create_ShouldStorePackagesAsPerProvidedGroupManageOrder() {

        PolicyProvider provider = new PolicyProvider();
        provider.addEmergencyGroupManageOrder(GROUPS.get(0), ID1, 1);
        provider.addEmergencyGroupManageOrder(GROUPS.get(1), ID1, 2);
        provider.addEmergencyGroupManageOrder(GROUPS.get(2), ID1, 1);
        provider.addEmergencyGroupManageOrder(GROUPS.get(1), ID2, 1);
        provider.addEmergencyGroupManageOrder(GROUPS.get(1), ID3, 3);
        provider.addEmergencyGroupManageOrder(GROUPS.get(2), ID3, 2);

        provider.addEmergencyPackage(ID1, PKG_NAME1);
        provider.addEmergencyPackage(ID2, PKG_NAME2);
        provider.addEmergencyPackage(ID3, PKG_NAME3);

        GlobalPolicyStore<EmergencyPackage> store = new GlobalPolicyStore<>(filterAlwaysTrue(), filterAlwaysTrue());
        store.create(provider.getPackages(), provider.getGroupManageOrders());

        assertFalse(store.all().isEmpty());

        assertReflectionEquals(getExpectedList(provider,ID1), store.byGroupId(GROUPS.get(0)));
        assertReflectionEquals(getExpectedList(provider, ID2, ID1, ID3), store.byGroupId(GROUPS.get(1)));
        assertReflectionEquals(getExpectedList(provider, ID1, ID3), store.byGroupId(GROUPS.get(2)));
    }

    private List<EmergencyPackage> getExpectedList(PolicyProvider provider, String... ids) {
        List<EmergencyPackage> expectedList = new ArrayList<>();

        for (String id : ids) {
            expectedList.add(provider.getEmergencyPackagebyId(id));
        }

        return expectedList;
    }

    /*
        Created policy for Group0,
        Asserted for Group1
     */
    @Test
    public void test_byGroup_ShouldReturnNullWhenNoPackagesProvidedForRespectiveGroup() {
        PolicyProvider provider = new PolicyProvider();
        // entry added for Group0
        provider.addEmergencyGroupManageOrder(GROUPS.get(0), ID1, 1);
        provider.addEmergencyPackage(ID1, PKG_NAME1);

        GlobalPolicyStore<EmergencyPackage> store = new GlobalPolicyStore<>(filterAlwaysTrue(), filterAlwaysTrue());
        store.create(provider.getPackages(), provider.getGroupManageOrders());

        assertFalse(store.all().isEmpty());

        // should not found entry for Group1
        assertNull(store.byGroupId(GROUPS.get(1)));
    }
}