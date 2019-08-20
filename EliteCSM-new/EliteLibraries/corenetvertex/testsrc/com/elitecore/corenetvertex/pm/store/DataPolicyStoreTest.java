package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.corenetvertex.pm.store.PolicyStoreTestUtil.filterAlwaysFalse;
import static com.elitecore.corenetvertex.pm.store.PolicyStoreTestUtil.filterAlwaysTrue;
import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class DataPolicyStoreTest {

    private static final String NAME_2 = "NAME2";
    private static final String ID_2 = "ID2";
    private static final String NAME_1 = "NAME1";
    private static final String ID_1 = "ID1";
    private DataPolicyStore<BasePackage> store;

    @Before
    public void setUp() {
         this.store = new DataPolicyStore<>(filterAlwaysTrue());
    }

    public class Create {


        @Test(expected = NullPointerException.class)
        public void ThrowNPE_WhenNullPackagesPassed() throws Exception {
            List<Package> packages = null;
            store.create(packages);
        }

        @Test
        public void NotAddAnyPackages_WhenEmptyPackageListIsPassed() {
            List<Package> emptyPackages = new ArrayList<>();
            store.create(emptyPackages);

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void NotAddAnyPackages_WhenFilterIsFalse() {

            PolicyProvider provider = new PolicyProvider();
            provider.addBasePolicy(ID_1, NAME_1);
            provider.addBasePolicy(ID_2, NAME_2);

            DataPolicyStore<BasePackage> store = new DataPolicyStore<>(filterAlwaysFalse());
            store.create(provider.getPackages());

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void AddPackages_WhenFilterSatisfied() {
            PolicyProvider provider = new PolicyProvider();
            provider.addBasePolicy(ID_1, NAME_1);
            provider.addBasePolicy(ID_2, NAME_2);

            // filter provided for base package type only
            DataPolicyStore<BasePackage> store = new DataPolicyStore<>(pkg-> PkgType.BASE == pkg.getPackageType());
            store.create(provider.getPackages());

            assertFalse(store.all().isEmpty());
            assertSame(provider.getBasePackageDataById(ID_1), store.byId(ID_1));
            assertSame(provider.getBasePackageDataById(ID_2), store.byId(ID_2));
            assertSame(provider.getBasePackageDataByName(NAME_1), store.byName(NAME_1));
            assertSame(provider.getBasePackageDataByName(NAME_2), store.byName(NAME_2));
            ReflectionAssert.assertReflectionEquals(provider.getPackages(), store.all());
        }
    }

    public class Active {

        @Test
        public void GiveActivePackageStore() {
            PolicyProvider provider = new PolicyProvider();
            provider.addBasePolicy(ID_1, NAME_1);

            store.create(provider.getPackages());

            assertNotNull(store.active());
        }
    }

}