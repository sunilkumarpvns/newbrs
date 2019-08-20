package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import com.elitecore.corenetvertex.pm.service.Service;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class ReloadServiceData {
    private PolicyManager policyManager;
    private PackageFactory packageFactory;
    private PackageHibernateSessionFactory hibernateSessionFactory = mock(PackageHibernateSessionFactory.class);
    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    private RnCFactory rnCFactory = new RnCFactory();
    private RnCPackageFactory rnCPackageFactory = new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory, new ThresholdNotificationSchemeFactory(rnCFactory));
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
	private DeploymentMode deploymentMode = DeploymentMode.PCC;
    private DummyDataReader dummyDataReader;


    @Before
    public void setUp() throws Exception {
        generatePolicyBackUpFile();
        when(hibernateSessionFactory.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        packageFactory = new PackageFactory();
        policyManager = new PolicyManager();
        dummyDataReader = new DummyDataReader();
    }



    public class WhenTableEmpty{
        @Before
        public void init() throws InitializationFailedException{
            initPolicyManager();
        }

        @Test
        public void getAllOnServiceReturnsEmptyList() throws InitializationFailedException{
			assertEquals(0,policyManager.getService().all().size());
        }

        @Test
        public void byIdReturnsNullValue() throws InitializationFailedException{
			assertNull(policyManager.getService().byId("empty"));
        }

        @Test
        public void byNameReturnsNullValue() throws InitializationFailedException{
			assertNull(policyManager.getService().byName("invalid"));
        }
    }

    private void initPolicyManager() throws InitializationFailedException {
        policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), dummyDataReader, packageFactory, rnCPackageFactory, deploymentMode);
    }

	public class InitWhenTableNotEmpty{
        @Test
        public void allReturnsSameListAsStoredInDB() throws InitializationFailedException{
            ServiceData serviceData = new ServiceData();
            serviceData.setId("1");
            serviceData.setName("name1");
            addDefaultValues(serviceData);
            Service service = new Service(serviceData.getId(), serviceData.getName(), PkgStatus.fromVal(serviceData.getStatus()));
            dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
			initPolicyManager();

			ReflectionAssert.assertLenientEquals(service,policyManager.getService().all().get(0));

        }

        @Test
        public void byIdReturnsSameObjectAsStoredInDB() throws InitializationFailedException{
            ServiceData serviceData = new ServiceData();
            serviceData.setId("1");
            serviceData.setName("name1");
            addDefaultValues(serviceData);
            Service service = new Service(serviceData.getId(), serviceData.getName(), PkgStatus.fromVal(serviceData.getStatus()));
            dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
			initPolicyManager();

			ReflectionAssert.assertLenientEquals(service, policyManager.getService().byId("1"));

        }

        @Test
        public void byNameReturnsSameObjectAsStoredInDB() throws InitializationFailedException{
            ServiceData serviceData = new ServiceData();
            serviceData.setId("hello");
            serviceData.setName("name1");
            addDefaultValues(serviceData);
            Service service = new Service(serviceData.getId(), serviceData.getName(), PkgStatus.fromVal(serviceData.getStatus()));
            dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
			initPolicyManager();

			ReflectionAssert.assertLenientEquals(service, policyManager.getService().byName("name1"));

        }
    }

    public class Reload{


        public class DataDeleted{
            @Test
            public void allReturnsEmptyLit() throws InitializationFailedException{
                ServiceData serviceData = new ServiceData();
                serviceData.setId("hello");
                serviceData.setName("name1");
                dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
				initPolicyManager();

                dummyDataReader.setReadList(ServiceData.class, new ArrayList());
                policyManager.reload();

                ReflectionAssert.assertLenientEquals(new ArrayList<Service>(), policyManager.getService().all());
            }

            @Test
            public void byIdReturnsNullForOldId() throws InitializationFailedException{
                ServiceData serviceData = new ServiceData();
                serviceData.setId("hello");
                serviceData.setName("name1");
                dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
				initPolicyManager();

                dummyDataReader.setReadList(ServiceData.class, new ArrayList());
                policyManager.reload();

                assertNull(policyManager.getService().byId("hello"));

            }

            @Test
            public void byNameReturnsNullForOldName() throws InitializationFailedException{
                ServiceData serviceData = new ServiceData();
                serviceData.setId("hello");
                serviceData.setName("name1");
                dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
				initPolicyManager();

                dummyDataReader.setReadList(ServiceData.class, new ArrayList());
                policyManager.reload();

                assertNull(policyManager.getService().byName("name1"));

            }
        }

        public class NewDataAdded{
            @Test
            public void byIdReturnsNullForOldId() throws InitializationFailedException{
                ServiceData serviceData = new ServiceData();
                serviceData.setId("hello");
                serviceData.setName("name1");
                dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
				initPolicyManager();

				mockNewDataBehaviour();
                policyManager.reload();

                assertNull(policyManager.getService().byId("hello"));

            }

            @Test
            public void byNameReturnsNullForOldName() throws InitializationFailedException{
                ServiceData serviceData = new ServiceData();
                serviceData.setId("hello");
                serviceData.setName("name1");
                dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
				initPolicyManager();

				mockNewDataBehaviour();
                policyManager.reload();

                assertNull(policyManager.getService().byName("name1"));

            }
            @Test
            public void allReturnsNewValuesFromDatabase() throws InitializationFailedException{
                ServiceData serviceData = new ServiceData();
                serviceData.setId("hello");
                serviceData.setName("name1");
                addDefaultValues(serviceData);
                dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
				initPolicyManager();

				List<Service> newList = mockNewDataBehaviour();
                policyManager.reload();

                ReflectionAssert.assertLenientEquals(newList, policyManager.getService().all());

            }

            @Test
            public void byNameForValidValueReturnsValidObjectStoredInDb() throws InitializationFailedException{
                ServiceData serviceData = new ServiceData();
                serviceData.setId("hello");
                serviceData.setName("name1");
                addDefaultValues(serviceData);
                dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
				initPolicyManager();

				List<Service> newList = mockNewDataBehaviour();
                policyManager.reload();

                ReflectionAssert.assertLenientEquals(newList.get(0), policyManager.getService().byName("name2"));

            }

            @Test
            public void byIdForValidValueReturnsValidObjectStoredInDb() throws InitializationFailedException{
                ServiceData serviceData = new ServiceData();
                serviceData.setId("hello");
                serviceData.setName("name1");
                addDefaultValues(serviceData);
                dummyDataReader.setReadList(ServiceData.class, createServiceDataList(serviceData));
				initPolicyManager();

				List<Service> newList = mockNewDataBehaviour();

                policyManager.reload();

                ReflectionAssert.assertLenientEquals(newList.get(0), policyManager.getService().byId("world"));

            }
        }
    }

    private List<Service> mockNewDataBehaviour(){

        List<Service> newList = new ArrayList();
        List<ServiceData> newDataList = new ArrayList();

        ServiceData serviceData = new ServiceData();
        serviceData.setId("world");
        serviceData.setName("name2");
        addDefaultValues(serviceData);
        newDataList.add(serviceData);
        Service service = new Service(serviceData.getId(), serviceData.getName(), PkgStatus.fromVal(serviceData.getStatus()));
        newList.add(service);

        serviceData = new ServiceData();
        serviceData.setId("sample");
        serviceData.setName("name3");
        addDefaultValues(serviceData);
        newDataList.add(serviceData);
        service = new Service(serviceData.getId(), serviceData.getName(), PkgStatus.fromVal(serviceData.getStatus()));
        newList.add(service);

        dummyDataReader.setReadList(ServiceData.class, newDataList);

        return newList;
    }

    public void addDefaultValues(ServiceData serviceData){
        serviceData.setGroupNames("GROUP_1");
    }

    @After
    public void tearDown() throws Exception {
        hibernateSessionFactory.shutdown();
        folder.delete();
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }

    private List<ServiceData> createServiceDataList(ServiceData serviceData) {
        List<ServiceData> list = new ArrayList<>(2);
        list.add(serviceData);
        return list;
    }
}
