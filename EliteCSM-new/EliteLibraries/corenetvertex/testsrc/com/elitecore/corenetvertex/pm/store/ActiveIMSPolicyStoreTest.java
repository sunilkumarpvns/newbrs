package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class ActiveIMSPolicyStoreTest {
    private static final String NAME_2 = "NAME2";
    private static final String ID_2 = "ID2";
    private static final String NAME_1 = "NAME1";
    private static final String ID_1 = "ID1";
    private ActiveIMSPolicyStore<IMSPackage> store;

    @Before
    public void setUp() {
        this.store = new ActiveIMSPolicyStore();
    }

    public class Create {

        @Test(expected = NullPointerException.class)
        public void ThrowNPE_WhenNullPackagesPassed() throws Exception {
            List<IMSPackage> packages = null;
            store.create(packages);
        }

        @Test
        public void NotAddAnyPackages_WhenEmptyPackageListIsPassed() {
            List<IMSPackage> emptyPackages = new ArrayList<>();
            store.create(emptyPackages);

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void NotAddPackages_WhenStatusIsNotActive() {

            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_1, NAME_1).statusInActive();

            store.create(provider.getIMSPackages());

            assertTrue(store.all().isEmpty());
            assertNull(store.byId(ID_1));
            assertNull(store.byName(NAME_1));
        }

        @Test
        public void AddPackages_WhenStatusIsActive() {
            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_1, NAME_1).statusActive();

            store.create(provider.getIMSPackages());

            assertFalse(store.all().isEmpty());
            assertSame(provider.getIMSPkgById(ID_1), store.byId(ID_1));
            assertSame(provider.getIMSPkgByName(NAME_1), store.byName(NAME_1));
        }

        @Test
        public void AddPackagesOnlyWithActiveStatus() {
            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_1, NAME_1).statusActive();
            provider.addIMSPolicy(ID_2, NAME_2).statusInActive();

            store.create(provider.getIMSPackages());

            assertSame(provider.getIMSPkgById(ID_1), store.byId(ID_1));
            assertSame(provider.getIMSPkgByName(NAME_1), store.byName(NAME_1));
            assertNull(store.byId(ID_2));
            assertNull(store.byName(NAME_2));
        }
    }

    public class Live {

        @Test
        public void GiveLivePackageStore() {
            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_1, NAME_1).statusInActive();

            store.create(provider.getIMSPackages());

            assertNotNull(store.live());
        }
    }
}