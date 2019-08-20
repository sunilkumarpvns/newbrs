package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class ChargingRuleBaseNameStoreTest {

    private ChargingRuleBaseNameStore store;

    @Before
    public void setUp() {
        this.store = new ChargingRuleBaseNameStore();
    }

    public class Create {

        private static final String NAME_1 = "NAME1";
        private static final String ID_1 = "ID1";
        public static final String CRBNNAME_1 = "CRBNNAME1";
        public static final String CRBN_1 = "CRBN1";

        @Test(expected = NullPointerException.class)
        public void ThrowNPE_WhenNullPackagesPassed() throws Exception {
            List<Package> packages = null;
            store.create(packages);
        }

        @Test
        public void NotAddAnyCRBN_WhenEmptyPackageListIsPassed() {
            List<Package> emptyPackages = new ArrayList<>();
            store.create(emptyPackages);
            assertTrue(store.all().isEmpty());
        }

        @Test
        public void NotAddCRBN_WhenPassedPackagesDoesNotHaveCRBN() {
            PolicyProvider provider = new PolicyProvider();
            provider.addBasePolicy(ID_1, NAME_1).mockChargingRuleBaseName(Collections.emptyList());

            store.create(provider.getPackages());

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void AddCRBN_WhenPassedPackagesHaveCRBN() {
            PolicyProvider provider = new PolicyProvider();
            provider.addCRBN(CRBN_1, CRBNNAME_1);
            provider.addBasePolicy(ID_1, NAME_1).mockChargingRuleBaseName(provider.getChargingRuleBaseNames());

            store.create(provider.getPackages());

            assertFalse(store.all().isEmpty());
            assertSame(provider.getChargingRuleBaseNameById(CRBN_1), store.byId(CRBN_1));
            assertSame(provider.getChargingRuleBaseNameByName(CRBNNAME_1), store.byName(CRBNNAME_1));
        }
    }


}