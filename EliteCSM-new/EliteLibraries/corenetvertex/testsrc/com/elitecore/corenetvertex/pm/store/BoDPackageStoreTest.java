package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(HierarchicalContextRunner.class)
public class BoDPackageStoreTest {

    @Before
    public void setup() {

    }

    @After
    public void reset() {
    }

    public class EmptyStore {
        //Make sure that there is no null pointer exceptions when store is created and get operations are done
        // immediately.
        @Test
        public void allWillReturnEmptyArray() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            assertEquals(0, boDPackageStore.all().size());
        }

        @Test
        public void byIdWillReturnNull() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            assertNull(boDPackageStore.byId("hello"));
        }

        @Test
        public void byNameWillReturnNull() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            assertNull(boDPackageStore.byName("hello"));
        }
    }

    public class InitializedOnce {
        //Make sure that empty store accepts new values
        @Test
        public void allReturnsAllTheRnCPackagesCreatedWithCreateMethod() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(list, boDPackageStore.all());

        }

        @Test
        public void getByIdReturnsTheSamePackageCreatedDuringCreate() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(bodPackage, boDPackageStore.byId(bodPackage.getId()));
        }

        @Test
        public void getByNameReturnsTheSamePackageCreatedDuringCreate() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(bodPackage, boDPackageStore.byName(bodPackage.getName()));
        }
    }

    public class Reload {
        //Make sure that store replaces old values with new ones
        @Test
        public void byNameWillNotReturnOldValues() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            list.clear();
            bodPackage = createBodPackage(PolicyStatus.SUCCESS, "fail", "reason");
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(bodPackage, boDPackageStore.byName(bodPackage.getName()));
        }

        @Test
        public void byIdWillNotReturnOldValues() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            list.clear();
            bodPackage = createBodPackage(PolicyStatus.SUCCESS, "fail reason", "partial fail reason");
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(bodPackage, boDPackageStore.byId(bodPackage.getId()));
        }

        @Test
        public void allWillReturnNewPackagesCreatedAlongWithIOldPackages() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            list.clear();

            BoDPackage bodPackage1 = createBodPackage("new","package", PolicyStatus.SUCCESS, null, null);
            list.add(bodPackage1);
            boDPackageStore.create(list, new ArrayList<>());

            list.add(bodPackage);
            ReflectionAssert.assertLenientEquals(list, boDPackageStore.all());
        }

        @Test
        public void returnsNullByIdForDeletedOffersWhenCreatedagainWithDeletedOffers() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, Arrays.asList(new String[]{bodPackage.getId()}));
            Assert.assertNull(boDPackageStore.byId(bodPackage.getId()));
        }

        @Test
        public void returnsNullByNameForDeletedOffersWhenCreatedagainWithDeletedOffers() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, Arrays.asList(new String[]{bodPackage.getId()}));
            Assert.assertNull(boDPackageStore.byName(bodPackage.getName()));
        }

        @Test
        public void returnsLastKnownGoodIfItWasSuccessEarlierWhenPackageIsFailedNow() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();
            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            list.clear();

            bodPackage = createBodPackage(PolicyStatus.FAILURE, null, null);
            list.add(bodPackage);
            boDPackageStore.create(list, new ArrayList<>());

            BoDPackage expected = createBodPackage(PolicyStatus.LAST_KNOWN_GOOD, null, null);

            ReflectionAssert.assertLenientEquals(expected, boDPackageStore.byName(bodPackage.getName()));
        }

        @Test
        public void returnsLastKnownGoodIfItWasLastKnownGoodAtLastPolicyReloadAndThisTimePackageIsFailed() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();

            BoDPackage bodPackage = createBodPackage(PolicyStatus.SUCCESS, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            list.clear();
            bodPackage = createBodPackage(PolicyStatus.FAILURE, "Hello", "world");
            list.add(bodPackage);
            boDPackageStore.create(list, new ArrayList<>());

            BoDPackage expected = createBodPackage(PolicyStatus.LAST_KNOWN_GOOD, "Hello", "world");
            ReflectionAssert.assertLenientEquals(expected, boDPackageStore.byName(bodPackage.getName()));
        }

        @Test
        public void returnsCurrentOfferIfLastKnownGoodWasFailureWhenCurrentOfferFailsToReload() {
            BoDPackageStore boDPackageStore = new BoDPackageStore();

            BoDPackage bodPackage = createBodPackage(PolicyStatus.FAILURE, null, null);

            ArrayList<BoDPackage> list = new ArrayList<>();
            list.add(bodPackage);

            boDPackageStore.create(list, new ArrayList<>());

            list.clear();
            bodPackage = createBodPackage(PolicyStatus.FAILURE, "Hello", "world");
            list.add(bodPackage);
            boDPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(bodPackage, boDPackageStore.byName(bodPackage.getName()));
        }
    }

    private BoDPackage createBodPackage(String name, String id, PolicyStatus policyStatus, String failReason, String partialFailReason) {
        return new BoDPackage(name, id, "bod description", PkgMode.LIVE, PkgStatus.ACTIVE,
                30, ValidityPeriodUnit.DAY, new ArrayList<>(),
                new HashMap<>(), new ArrayList<>(), failReason, policyStatus, null, null, null, null, null);
    }

    private BoDPackage createBodPackage(PolicyStatus policyStatus, String failReason, String partialFailReason) {
        return createBodPackage("e5792hklrhl43", "Bod 1",  policyStatus, failReason, partialFailReason);
    }

}