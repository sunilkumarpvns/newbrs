package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
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
public class DefaultIMSPolicyStoreTest {

    private  DefaultIMSStore<IMSPackage> store;

    @Before
    public void setUp() {
        this.store = new DefaultIMSStore(filterAlwaysTrue());
    }

    public class Create {

        private static final String NAME_2 = "NAME2";
        private static final String ID_2 = "ID2";
        private static final String NAME_1 = "NAME1";
        private static final String ID_1 = "ID1";

        @Test(expected = NullPointerException.class)
        public void ThrowIllegalArgumentException_WhenNullPackagesPassed() throws Exception {
            DefaultIMSStore<IMSPackage>  store = new DefaultIMSStore(filterAlwaysTrue());
            List<IMSPackage> packages = null;
            store.create(packages);
        }

        @Test
        public void NotAddAnyPackages_WhenEmptyPackageListIsPassed() {
            DefaultDataPolicyStore store = new DefaultDataPolicyStore(filterAlwaysTrue());
            List<IMSPackage> emptyPackages = new ArrayList<>();
            store.create(emptyPackages);

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void NotAddAnyPackages_WhenFilterIsFalse() {

            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_1, NAME_1);
            provider.addIMSPolicy(ID_2, NAME_2);

            DefaultIMSStore<IMSPackage> store = new DefaultIMSStore(filterAlwaysFalse());
            store.create(provider.getIMSPackages());

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void AddPackages_WhenFilterSatisfied() {
            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_1, NAME_1);
            provider.addIMSPolicy(ID_2, NAME_2);

            store.create(provider.getIMSPackages());

            assertFalse(store.all().isEmpty());
            assertSame(provider.getIMSPkgById(ID_1), store.byId(ID_1));
            assertSame(provider.getIMSPkgById(ID_2), store.byId(ID_2));
            assertSame(provider.getIMSPkgByName(NAME_1), store.byName(NAME_1));
            assertSame(provider.getIMSPkgByName(NAME_2), store.byName(NAME_2));
            ReflectionAssert.assertReflectionEquals(provider.getIMSPackages(), store.all());
        }


    }

}