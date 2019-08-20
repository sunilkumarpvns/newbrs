package com.elitecore.netvertex.gateway.radius;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.sm.dictonary.AttributeData;
import com.elitecore.corenetvertex.sm.dictonary.AttributeValueData;
import com.elitecore.corenetvertex.spr.DummyDBDataSource;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.DictionaryParseException;
import com.elitecore.coreradius.commons.util.ILicenseValidator;
import com.elitecore.coreradius.commons.util.dictionary.DictionaryModel;
import com.elitecore.netvertex.gateway.diameter.DiameterDictionaryDataBuilder;
import com.elitecore.netvertex.gateway.radius.dictionary.RadiusDictionaryLoader;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.elitecore.netvertex.gateway.radius.RadiusDictionaryDataBuilder.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RadiusDictionaryLoaderTest {
    private DictionaryModel expectedDictionaryModel;
    private RadiusDictionaryLoader radiusDictionaryLoader;

    private SessionFactory sessionFactory;
    private HibernateSessionFactory hibernateSessionFactory;
    private File testFile;

    @Mock private SessionFactory mockSessionFactory;
    @Mock private ServerContext serverContext;
    @Mock private Dictionary dictionary;
    @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String DS_NAME = "test-DB";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        String ssid = UUID.randomUUID().toString();
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:h2:mem:" + ssid, "", "", 1, 5000, 3000);
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + ssid);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
        sessionFactory = hibernateSessionFactory.getSessionFactory();

        saveDictionaryConfiguration();
        setUpServerHome();
    }

    private void setUpServerHome() throws IOException {
        this.temporaryFolder.create();
        temporaryFolder.newFolder("dictionary", "radius");
        testFile = temporaryFolder.newFile("dictionary/radius/test.xml");
        Mockito.when(serverContext.getServerHome()).thenReturn(temporaryFolder.getRoot().getAbsolutePath());
    }

    public void saveDictionaryConfiguration() throws DictionaryParseException {

        hibernateSessionFactory.save(RadiusDictionaryDataBuilder.getVendorInformation());
        hibernateSessionFactory.save(RadiusDictionaryDataBuilder.getOtherVendorInformation());
        List<AttributeData> attributeDatas = getAttributeData();
        expectedDictionaryModel = new DictionaryModel(RadiusDictionaryDataBuilder.buildIdToVendorInformation(attributeDatas));

        for (AttributeData attributeData : attributeDatas) {
            LogManager.getLogger().debug(RadiusDictionaryLoaderTest.class.getSimpleName(), attributeData.toString());

            saveChildAttributes(attributeData);
        }

        hibernateSessionFactory.save(DiameterDictionaryDataBuilder.getStringAttribute());

        radiusDictionaryLoader = new RadiusDictionaryLoader(sessionFactory, serverContext);
    }

    private void saveChildAttributes(AttributeData attributeData) {
        List<AttributeData> childAttributes = attributeData.getChildAttributes();
        if (Objects.nonNull(childAttributes)) {
            attributeData.setChildAttributes(null);
            Set<AttributeValueData> attributeSupportedValues = attributeData.getAttributeSupportedValues();
            attributeData.setAttributeSupportedValues(null);
            hibernateSessionFactory.save(attributeData);
            if(attributeSupportedValues != null){
                for(AttributeValueData supportedValue: attributeSupportedValues){
                    supportedValue.setAttributeData(attributeData);
                    hibernateSessionFactory.save(supportedValue);
                }
            }

            for (AttributeData chilAttributeData : childAttributes) {
                if (Objects.nonNull(chilAttributeData.getChildAttributes())) {
                    saveChildAttributes(chilAttributeData);
                } else {
                    hibernateSessionFactory.save(chilAttributeData);
                }
            }
        } else {
            hibernateSessionFactory.save(attributeData);
        }
    }

    @Test
    public void createNonEmptyXMLFileOnDictionaryLoad() throws LoadConfigurationException {
        radiusDictionaryLoader.load(dictionary);

        Assert.assertTrue(testFile.length() > 0);
    }
    @Test
    public void loadDictionaryWhenDictionaryReadSuccessfullyFromDB() throws LoadConfigurationException, DictionaryParseException {
        radiusDictionaryLoader.load(dictionary);

        DictionaryModel dictionaryModel = getDictionaryModel();

        ReflectionAssert.assertLenientEquals(expectedDictionaryModel, dictionaryModel);
    }

    private DictionaryModel getDictionaryModel() throws DictionaryParseException {
        ArgumentCaptor<ILicenseValidator> validatorArgumentCaptortor = ArgumentCaptor.forClass(ILicenseValidator.class);
        ArgumentCaptor<DictionaryModel> dictionaryModelArgumentCaptor = ArgumentCaptor.forClass(DictionaryModel.class);
        verify(dictionary).load(dictionaryModelArgumentCaptor.capture(), validatorArgumentCaptortor.capture());
        return dictionaryModelArgumentCaptor.getValue();
    }

    @Test
    public void readDictionaryConfigurationFromBackupWhenDBReadingFail() throws LoadConfigurationException, DictionaryParseException {
        radiusDictionaryLoader.load(dictionary);
        DictionaryModel dictionaryModel = getDictionaryModel();

        radiusDictionaryLoader = new RadiusDictionaryLoader(mockSessionFactory, serverContext);
        when(mockSessionFactory.openSession()).thenThrow(new HibernateException("From "+ RadiusDictionaryLoader.class));

        radiusDictionaryLoader.load(dictionary);

        Assert.assertNotNull(expectedDictionaryModel.getIdToVendorInformation());
        ReflectionAssert.assertLenientEquals(expectedDictionaryModel, dictionaryModel);
    }

    @Test
    public void loadDictionaryWhenDictionaryTypeIsDiameter() throws LoadConfigurationException {
        radiusDictionaryLoader.load(dictionary);

        Assert.assertFalse(expectedDictionaryModel.getIdToVendorInformation().get(new Long(RadiusDictionaryDataBuilder.VENDOR_ID)).getAttributeModels().stream().anyMatch(attributeData -> attributeData.getName().equals("Attrib8")));
    }

    @Test
    public void loadDictionaryWhenTypeIsNull() throws LoadConfigurationException {
        radiusDictionaryLoader.load(dictionary);

        Assert.assertTrue(expectedDictionaryModel.getIdToVendorInformation().get(new Long(RadiusDictionaryDataBuilder.VENDOR_ID)).getAttributeModels().stream().anyMatch(attributeData -> Objects.nonNull(attributeData.getType())));
    }

    @Test
    public void loadDictionaryForMultipleVendors() throws LoadConfigurationException {
        radiusDictionaryLoader.load(dictionary);

        Assert.assertFalse(expectedDictionaryModel.getIdToVendorInformation().get(new Long(RadiusDictionaryDataBuilder.VENDOR_ID)).getAttributeModels().stream().anyMatch(attributeData -> String.valueOf(attributeData.getId()).equals("33")));
    }

    @Test
    public void loadDictionaryAlongWithAttributeSupportedValues() throws LoadConfigurationException, DictionaryParseException {
        radiusDictionaryLoader.load(dictionary);

        DictionaryModel dictionaryModel = getDictionaryModel();

        Assert.assertTrue(dictionaryModel.getIdToVendorInformation().get(new Long(RadiusDictionaryDataBuilder.VENDOR_ID)).getAttributeModels().stream().anyMatch(attributeData -> attributeData.getSupportedValues().stream().anyMatch(supportedValue -> supportedValue.getName().equals("test"))));
    }

    public static List<AttributeData> getAttributeData() {
        List<AttributeData> attributeData = new ArrayList<>();

        attributeData.add(getStringAttribute());

        attributeData.add(getAttributeDataForOtherVendor());

        attributeData.add(getGroupedAttribute());
        getChildAttribute().forEach(attributeData::add);
        getSubChildAttribute().forEach(attributeData::add);

        attributeData.add(getNullAttribute());

        attributeData.add(getSupportedValues());

        return attributeData;
    }

    @After
    public void tearDown() throws Exception {
        temporaryFolder.delete();
        hibernateSessionFactory.shutdown();
    }
}
