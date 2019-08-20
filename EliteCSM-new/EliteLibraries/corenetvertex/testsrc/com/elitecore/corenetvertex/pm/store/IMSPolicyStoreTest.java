package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.util.IMSPackageStore;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Null;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.corenetvertex.pm.store.PolicyStoreTestUtil.filterAlwaysFalse;
import static com.elitecore.corenetvertex.pm.store.PolicyStoreTestUtil.filterAlwaysTrue;
import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class IMSPolicyStoreTest {

    private static final String NAME_2 = "NAME2";
    private static final String ID_2 = "ID2";
    private static final String NAME_1 = "NAME1";
    private static final String ID_1 = "ID1";

    private IMSPolicyStore<IMSPackage> store;

    @Before
    public void setUp() {
        this.store = new IMSPolicyStore<>();
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
        public void AddPackages_WhenPackageWithActiveStatus() {
            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_2, NAME_2).statusActive();

            store.create(provider.getIMSPackages());

            assertSame(provider.getIMSPkgById(ID_2), store.byId(ID_2));
            assertSame(provider.getIMSPkgByName(ID_2), store.byName(ID_2));
        }

        @Test
        public void AddPackages_WhenPackageWithActiveInStatus() {
            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_2, NAME_2).statusInActive();

            store.create(provider.getIMSPackages());

            assertSame(provider.getIMSPkgById(ID_2), store.byId(ID_2));
            assertSame(provider.getIMSPkgByName(ID_2), store.byName(ID_2));
        }
    }

    public class Active {

        @Test
        public void GiveActivePackageStore() {
            PolicyProvider provider = new PolicyProvider();
            provider.addIMSPolicy(ID_1, NAME_1);

            store.create(provider.getIMSPackages());

            assertNotNull(store.active());
        }
    }

}