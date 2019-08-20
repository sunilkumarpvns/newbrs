package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pm.service.Service;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(HierarchicalContextRunner.class)
public class ServiceStoreTest {

    private List serviceList = new ArrayList();

    @Before
    public void setup(){
        serviceList.clear();
    }

    public class EmptyStore {

        @Test
        public void allWillReturnEmptyArray(){
            ServiceStore serviceStore = new ServiceStore();
            assertEquals(0,serviceStore.all().size());
        }

        @Test
        public void byIdWillReturnNull(){
            ServiceStore serviceStore = new ServiceStore();
            assertNull(serviceStore.byId("hello"));
        }

        @Test
        public void byNameWillReturnNull(){
            ServiceStore serviceStore = new ServiceStore();
            assertNull(serviceStore.byName("name"));
        }

    }

    public class InitializedOnce{
        @Test
        public void allMustReturnSameListWhchAdded(){
            Service serviceData = new Service("1", "name", PkgStatus.ACTIVE);
            serviceList.add(serviceData);

            ServiceStore serviceStore = new ServiceStore();
            serviceStore.create(serviceList);

            ReflectionAssert.assertLenientEquals(serviceStore.all(), serviceList);

        }

        @Test
        public void getbyIdMustReturnTheSameAddedObject(){
            Service serviceData = new Service("good", "name", PkgStatus.ACTIVE);
            serviceList.add(serviceData);

            ServiceStore serviceStore = new ServiceStore();
            serviceStore.create(serviceList);

            ReflectionAssert.assertLenientEquals(serviceStore.byId("good"), serviceData);
        }



        @Test
        public void getbyNameMustReturnTheSameAddedObject(){
            Service serviceData = new Service("good","name", PkgStatus.ACTIVE);
            serviceList.add(serviceData);

            ServiceStore serviceStore = new ServiceStore();
            serviceStore.create(serviceList);

            ReflectionAssert.assertLenientEquals(serviceStore.byId("name"), serviceData);

        }
    }

    public class Reload{
        @Test
        public void byNameAndIdWillNotReturnOldValues(){
            Service serviceData = new Service("1", "name", PkgStatus.ACTIVE);
            serviceList.add(serviceData);

            ServiceStore serviceStore = new ServiceStore();
            serviceStore.create(serviceList);

            serviceList = new ArrayList();

            serviceData = new Service("2", "name2", PkgStatus.ACTIVE);
            serviceList.add(serviceData);

            serviceStore.create(serviceList);

            assertNull(serviceStore.byId("1"));
            assertNull(serviceStore.byName("name"));
        }
        @Test
        public void allWillReturnNewListWhichWasAdded(){
            Service serviceData = new Service("1", "name", PkgStatus.ACTIVE);
            serviceList.add(serviceData);

            ServiceStore serviceStore = new ServiceStore();
            serviceStore.create(serviceList);

            serviceList = new ArrayList();

            serviceData = new Service("3", "name3", PkgStatus.ACTIVE);
            serviceList.add(serviceData);

            serviceStore.create(serviceList);

            ReflectionAssert.assertLenientEquals(serviceStore.all(), serviceList);
        }
    }
}
