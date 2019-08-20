package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class ActiveQuotaTopUpPolicyStoreTest {
    private static final String NAME_2 = "NAME2";
    private static final String ID_2 = "ID2";
    private static final String NAME_1 = "NAME1";
    private static final String ID_1 = "ID1";
    private ActiveQuotaTopUpPolicyStore<QuotaTopUp> store;

    @Before
    public void setUp() {
        this.store = new ActiveQuotaTopUpPolicyStore<>();
    }

    public class Create {

        @Test(expected = NullPointerException.class)
        public void ThrowNPE_WhenNullPackagesPassed() throws Exception {
            List<QuotaTopUp> packages = null;
            store.create(packages);
        }

        @Test
        public void NotAddAnyPackages_WhenEmptyPackageListIsPassed() {
            List<QuotaTopUp> emptyPackages = new ArrayList<>();
            store.create(emptyPackages);

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void NotAddPackages_WhenStatusIsNotActive() {

            PolicyProvider provider = new PolicyProvider();
            provider.addQuotaTopUpPolicy(ID_1, NAME_1).statusInActive();

            store.create(provider.getQuotaTopUps());

            assertTrue(store.all().isEmpty());
            assertNull(store.byId(ID_1));
            assertNull(store.byName(NAME_1));
        }

        @Test
        public void AddPackages_WhenStatusIsActive() {
            PolicyProvider provider = new PolicyProvider();
            provider.addQuotaTopUpPolicy(ID_1, NAME_1).statusActive();

            store.create(provider.getQuotaTopUps());

            assertFalse(store.all().isEmpty());
            assertSame(provider.getQuotaTopUpById(ID_1), store.byId(ID_1));
            assertSame(provider.getQuotaTopUpByName(NAME_1), store.byName(NAME_1));
        }

        @Test
        public void AddPackagesOnlyWithActiveStatus() {
            PolicyProvider provider = new PolicyProvider();
            provider.addQuotaTopUpPolicy(ID_1, NAME_1).statusActive();
            provider.addQuotaTopUpPolicy(ID_2, NAME_2).statusInActive();

            store.create(provider.getQuotaTopUps());

            assertSame(provider.getQuotaTopUpById(ID_1), store.byId(ID_1));
            assertSame(provider.getQuotaTopUpByName(NAME_1), store.byName(NAME_1));
            assertNull(store.byId(ID_2));
            assertNull(store.byName(NAME_2));
            assertNull(store.byId(ID_2));
            assertNull(store.byName(NAME_2));
        }
    }

    public class Live {

        @Test
        public void AlwaysGiveLivePackageStore() {
            assertNotNull(store.live());
        }
    }
}
