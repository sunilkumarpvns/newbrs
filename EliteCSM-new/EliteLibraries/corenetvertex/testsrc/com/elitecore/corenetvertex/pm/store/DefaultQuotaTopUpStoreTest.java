package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class DefaultQuotaTopUpStoreTest {

    private  DefaultQuotaTopUpStore<QuotaTopUp> store;

    @Before
    public void setUp() {
        this.store = new DefaultQuotaTopUpStore(filterAlwaysTrue());
    }

    public class Create {

        private static final String NAME_2 = "NAME2";
        private static final String ID_2 = "ID2";
        private static final String NAME_1 = "NAME1";
        private static final String ID_1 = "ID1";

        @Test(expected = NullPointerException.class)
        public void ThrowIllegalArgumentException_WhenNullPackagesPassed() throws Exception {
            DefaultQuotaTopUpStore<QuotaTopUp>  store = new DefaultQuotaTopUpStore(filterAlwaysTrue());
            List<QuotaTopUp> packages = null;
            store.create(packages);
        }

        @Test
        public void NotAddAnyPackages_WhenEmptyPackageListIsPassed() {
            DefaultDataPolicyStore store = new DefaultDataPolicyStore(filterAlwaysTrue());
            List<QuotaTopUp> emptyPackages = new ArrayList<>();
            store.create(emptyPackages);

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void NotAddAnyPackages_WhenFilterIsFalse() {

            PolicyProvider provider = new PolicyProvider();
            provider.addQuotaTopUpPolicy(ID_1, NAME_1);
            provider.addQuotaTopUpPolicy(ID_2, NAME_2);

            DefaultQuotaTopUpStore<QuotaTopUp> store = new DefaultQuotaTopUpStore(filterAlwaysFalse());
            store.create(provider.getQuotaTopUps());

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void AddPackages_WhenFilterSatisfied() {
            PolicyProvider provider = new PolicyProvider();
            provider.addQuotaTopUpPolicy(ID_1, NAME_1);
            provider.addQuotaTopUpPolicy(ID_2, NAME_2);

            store.create(provider.getQuotaTopUps());

            assertFalse(store.all().isEmpty());
            assertSame(provider.getQuotaTopUpById(ID_1), store.byId(ID_1));
            assertSame(provider.getQuotaTopUpById(ID_2), store.byId(ID_2));
            assertSame(provider.getQuotaTopUpByName(NAME_1), store.byName(NAME_1));
            assertSame(provider.getQuotaTopUpByName(NAME_2), store.byName(NAME_2));
            ReflectionAssert.assertReflectionEquals(provider.getQuotaTopUps(), store.all());
        }


    }

}
