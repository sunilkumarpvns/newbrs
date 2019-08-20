package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(HierarchicalContextRunner.class)
public class RnCPackageStoreTest {

    private int counter = 1;
    private String currency="INR";

    @Before
    public void setup(){

    }

    @After
    public void reset(){
        counter=1;
    }

    public class EmptyStore {
        @Test
        public void allWillReturnEmptyArray(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();
            assertEquals(0,rnCPackageStore.all().size());
        }

        @Test
        public void byIdWillReturnNull(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();
            assertNull(rnCPackageStore.byId("hello"));
        }

        @Test
        public void byNameWillReturnNull(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();
            assertNull(rnCPackageStore.byName("name"));
        }

        @Test
        public void baseByNameWillReturnNull(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();
            assertNull(rnCPackageStore.baseByName("who"));
        }

        @Test
        public void baseByIdWillReturnNull(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();
            assertNull(rnCPackageStore.baseById("why"));
        }
    }

    public class InitializedOnce{
        @Test
        public void testsAllReturnsAllTheRnCPackagesCreatedWithCreateMethod(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(list, rnCPackageStore.all());
        }

        @Test
        public void testsGetByIdReturnsTheSamePackageCreatedDuringCreate(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(rnCPackage, rnCPackageStore.byId(rnCPackage.getId()));
        }

        @Test
        public void testsGetByNameReturnsTheSamePackageCreatedDuringCreate(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(rnCPackage, rnCPackageStore.byName(rnCPackage.getName()));
        }

        @Test
        public void testsGetBaseByIdReturnsTheSameBasePackageCreatedDuringCreate(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(rnCPackage, rnCPackageStore.baseById(rnCPackage.getId()));
        }

        @Test
        public void testsGetBaseByNameReturnsTheSameBasePackageCreatedDuringCreate(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(rnCPackage, rnCPackageStore.baseByName(rnCPackage.getName()));
        }
    }

    public class Reload{
        @Test
        public void byNameWillNotReturnOldValues(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            list.clear();
            counter=1;
            rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, "Hello", "world");
            list.add(rnCPackage);
            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(rnCPackage, rnCPackageStore.byName(rnCPackage.getName()));

        }
        @Test
        public void byIdWillNotReturnOldValues(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            list.clear();
            counter=1;
            rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, "Hello", "world");
            list.add(rnCPackage);
            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(rnCPackage, rnCPackageStore.byId(rnCPackage.getId()));


        }
        @Test
        public void baseByNameWillNotReturnOldValues(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            list.clear();
            counter=1;
            rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, "Hello", "world");
            list.add(rnCPackage);
            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(rnCPackage, rnCPackageStore.baseByName(rnCPackage.getName()));


        }
        @Test
        public void baseByIdWillNotReturnOldValues(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            list.clear();
            counter=1;
            rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, "Hello", "world");
            list.add(rnCPackage);
            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(rnCPackage, rnCPackageStore.baseById(rnCPackage.getId()));


        }
        @Test
        public void allWillReturnNewPackagesCreatedAlongWithIOldPackages(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            list.clear();

            RnCPackage rnCPackage1 = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);
            list.add(rnCPackage1);
            rnCPackageStore.create(list, new ArrayList<>());

            list.add(rnCPackage);

            ReflectionAssert.assertLenientEquals(list, rnCPackageStore.all());
        }

        @Test
        public void itReturnsNullByIdForDeletedOffersWhenCreatedagainWithDeletedOffers(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());
            rnCPackageStore.create(new ArrayList<>(), Arrays.asList(new String[]{rnCPackage.getId()}));

            Assert.assertNull(rnCPackageStore.byName(rnCPackage.getName()));
        }

        @Test
        public void itReturnsNullByNameForDeletedOffersWhenCreatedagainWithDeletedOffers(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());
            rnCPackageStore.create(new ArrayList<>(), Arrays.asList(new String[]{rnCPackage.getId()}));

            Assert.assertNull(rnCPackageStore.byId(rnCPackage.getId()));
        }
        @Test
        public void itReturnsNullBaseByIdForDeletedOffersWhenCreatedagainWithDeletedOffers(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());
            rnCPackageStore.create(new ArrayList<>(), Arrays.asList(new String[]{rnCPackage.getId()}));

            Assert.assertNull(rnCPackageStore.baseByName(rnCPackage.getName()));
        }

        @Test
        public void itReturnsNullBaseByNameForDeletedOffersWhenCreatedAgainWithDeletedOffers(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());
            rnCPackageStore.create(new ArrayList<>(), Arrays.asList(new String[]{rnCPackage.getId()}));

            Assert.assertNull(rnCPackageStore.baseById(rnCPackage.getId()));
        }

        @Test
        public void itReturnsLastKnownGoodIfItWasSuccessWhenPackageIsFailed(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.SUCCESS, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            list.clear();
            counter=1;
            rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.FAILURE, "Hello", "world");
            list.add(rnCPackage);
            rnCPackageStore.create(list, new ArrayList<>());

            counter=1;
            RnCPackage expected = createRnCPackage(RnCPkgType.BASE, PolicyStatus.LAST_KNOWN_GOOD, "Hello", "world");
            ReflectionAssert.assertLenientEquals(expected, rnCPackageStore.byName(rnCPackage.getName()));
        }

        @Test
        public void itReturnsLastKnownGoodIfItWasLastKnownGoodAtLastPolicyReloadAndThisTimePackageIsFailed(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.LAST_KNOWN_GOOD, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            list.clear();
            counter=1;
            rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.FAILURE, "Hello", "world");
            list.add(rnCPackage);
            rnCPackageStore.create(list, new ArrayList<>());

            counter=1;
            RnCPackage expected = createRnCPackage(RnCPkgType.BASE, PolicyStatus.LAST_KNOWN_GOOD, "Hello", "world");
            ReflectionAssert.assertLenientEquals(expected, rnCPackageStore.byName(rnCPackage.getName()));
        }

        @Test
        public void itReturnsCurrentOfferIfLastKnownGoodWasFailureWhenCurrentOfferFailsToReload(){
            RnCPackageStore rnCPackageStore = new RnCPackageStore();

            RnCPackage rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.FAILURE, null, null);

            ArrayList<RnCPackage> list = new ArrayList<>();
            list.add(rnCPackage);

            rnCPackageStore.create(list, new ArrayList<>());

            list.clear();
            counter=1;
            rnCPackage = createRnCPackage(RnCPkgType.BASE, PolicyStatus.FAILURE, "Hello", "world");
            list.add(rnCPackage);
            rnCPackageStore.create(list, new ArrayList<>());

            ReflectionAssert.assertLenientEquals(rnCPackage, rnCPackageStore.byName(rnCPackage.getName()));
        }
    }

    private RnCPackage createRnCPackage(RnCPkgType pkgType, PolicyStatus policyStatus, String failReason, String partialFailReason){
        RnCPackage rnCPackage = new RnCPackage("id"+counter, "name"+counter, "description",
                new ArrayList<>(), new ArrayList<>(),
                null,"tag", pkgType,
                PkgMode.TEST, PkgStatus.ACTIVE, policyStatus,
                failReason, partialFailReason, ChargingType.SESSION,currency);

        counter++;
        return rnCPackage;
    }
}
