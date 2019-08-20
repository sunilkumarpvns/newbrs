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
public class PCCRuleStoreTest {

    private PCCRuleStore store;

    @Before
    public void setUp() {
        this.store = new PCCRuleStore();
    }

    public class Create {

        private static final String NAME_1 = "NAME1";
        private static final String ID_1 = "ID1";
        public static final String PCCNAME_1 = "PCCNAME1";
        public static final String PCC_1 = "PCC1";

        @Test(expected = NullPointerException.class)
        public void ThrowNPE_WhenNullPackagesPassed() throws Exception {
            List<Package> packages = null;
            store.create(packages);
        }

        @Test
        public void NotAddAnyPCCRules_WhenEmptyPackageListIsPassed() {
            List<Package> emptyPackages = new ArrayList<>();
            store.create(emptyPackages);
            assertTrue(store.all().isEmpty());
        }

        @Test
        public void NotAddPCCRules_WhenPassedPackagesDoesNotHavePCCRule() {
            PolicyProvider provider = new PolicyProvider();
            provider.addBasePolicy(ID_1, NAME_1).mockPCCRule(Collections.emptyList());

            store.create(provider.getPackages());

            assertTrue(store.all().isEmpty());
        }

        @Test
        public void AddPCCRules_WhenPassedPackagesHavePCCRule() {
            PolicyProvider provider = new PolicyProvider();
            provider.addPCCRule(PCC_1, PCCNAME_1);
            provider.addBasePolicy(ID_1, NAME_1).mockPCCRule(provider.getPccRules());

            store.create(provider.getPackages());

            assertFalse(store.all().isEmpty());
            assertSame(provider.getPCCRuleById(PCC_1), store.byId(PCC_1));
            assertSame(provider.getPCCRuleByName(PCCNAME_1), store.byName(PCCNAME_1));
        }
    }

}