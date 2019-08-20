package com.elitecore.netvertex.gateway.diameter;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.sm.dictonary.AttributeData;
import com.elitecore.corenetvertex.sm.dictonary.AttributeValueData;
import com.elitecore.corenetvertex.spr.DummyDBDataSource;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import com.elitecore.coreradius.commons.util.DictionaryParseException;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.ILicenseValidator;
import com.elitecore.diameterapi.diameter.common.util.dictionary.DiameterDictionaryModel;
import com.elitecore.netvertex.gateway.diameter.dictionary.DiameterDictionaryLoader;
import com.elitecore.netvertex.gateway.radius.RadiusDictionaryDataBuilder;
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

import static com.elitecore.netvertex.gateway.diameter.DiameterDictionaryDataBuilder.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiameterDictionaryLoaderTest {

    private DiameterDictionaryModel expectedDiameterDictionaryModel;
    private DiameterDictionaryLoader diameterDictionaryLoader;

    private SessionFactory sessionFactory;
    private HibernateSessionFactory hibernateSessionFactory;
    private File testFile;

    @Mock private SessionFactory mockSessionFactory;
    @Mock private ServerContext serverContext;
    @Mock private DiameterDictionary diameterDictionary;
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
        temporaryFolder.newFolder("dictionary", "diameter");
        testFile = temporaryFolder.newFile("dictionary/diameter/test.xml");
        Mockito.when(serverContext.getServerHome()).thenReturn(temporaryFolder.getRoot().getAbsolutePath());
    }

    public void saveDictionaryConfiguration() throws DictionaryParseException {

        hibernateSessionFactory.save(DiameterDictionaryDataBuilder.getVendorInformation());
        for (AttributeData attributeData : getAttributeData()) {
            LogManager.getLogger().debug(DiameterDictionaryLoaderTest.class.getSimpleName(), attributeData.toString());

            Set<AttributeValueData> attributeSupportedValues = attributeData.getAttributeSupportedValues();
            attributeData.setAttributeSupportedValues(null);
            hibernateSessionFactory.save(attributeData);
            if(attributeSupportedValues != null){
                for(AttributeValueData supportedValue: attributeSupportedValues){
                    supportedValue.setAttributeData(attributeData);
                    hibernateSessionFactory.save(supportedValue);
        }
            }
        }

        hibernateSessionFactory.save(RadiusDictionaryDataBuilder.getStringAttribute());

        expectedDiameterDictionaryModel = DiameterDictionaryDataBuilder.buildDiameterDictionaryModel(getAttributeData());
        diameterDictionaryLoader = new DiameterDictionaryLoader(sessionFactory, serverContext);
    }

    @Test
    public void createNonEmptyXMLFileOnDictionaryLoad() throws LoadConfigurationException {
        diameterDictionaryLoader.load(diameterDictionary);

        assertTrue(testFile.length() > 0);
    }

    @Test
    public void loadDictionaryWhenDictionaryReadSuccessfullyFromDB() throws Exception {
        diameterDictionaryLoader.load(diameterDictionary);

        DiameterDictionaryModel diameterDictionaryModel = getDiameterDictionaryModel();

        ReflectionAssert.assertLenientEquals(expectedDiameterDictionaryModel, diameterDictionaryModel);
    }

    private DiameterDictionaryModel getDiameterDictionaryModel() throws Exception {
        ArgumentCaptor<ILicenseValidator> validatorArgumentCaptortor = ArgumentCaptor.forClass(ILicenseValidator.class);
        ArgumentCaptor<DiameterDictionaryModel> diameterDictionaryModelArgumentCaptor = ArgumentCaptor.forClass(DiameterDictionaryModel.class);
        verify(diameterDictionary).load(diameterDictionaryModelArgumentCaptor.capture(), validatorArgumentCaptortor.capture());
        return diameterDictionaryModelArgumentCaptor.getValue();
    }

    @Test
    public void readDictionaryConfigurationFromBackupWhenDBReadingFail() throws Exception {
        diameterDictionaryLoader.load(diameterDictionary);
        DiameterDictionaryModel dictionaryModel = getDiameterDictionaryModel();

        diameterDictionaryLoader = new DiameterDictionaryLoader(mockSessionFactory, serverContext);
        when(mockSessionFactory.openSession()).thenThrow(new HibernateException("From"+ DiameterDictionaryLoader.class));

        diameterDictionaryLoader.load(diameterDictionary);

        Assert.assertNotNull(expectedDiameterDictionaryModel.getIdtoVendorInformation());
        ReflectionAssert.assertLenientEquals(expectedDiameterDictionaryModel, dictionaryModel);
    }

    @Test
    public void loadDictionaryWhenDictionaryTypeIsRadius() throws LoadConfigurationException {
        diameterDictionaryLoader.load(diameterDictionary);

        Assert.assertFalse(expectedDiameterDictionaryModel.getIdtoVendorInformation().get(DiameterDictionaryDataBuilder.VENDOR_ID).getAttributeData().stream().anyMatch(attributeData -> attributeData.getName().equals("Attrib5")));
    }

    @Test
    public void loadDictionaryWhenTypeIsNull() throws LoadConfigurationException {
        diameterDictionaryLoader.load(diameterDictionary);

        assertTrue(expectedDiameterDictionaryModel.getIdtoVendorInformation().get(DiameterDictionaryDataBuilder.VENDOR_ID).getAttributeData().stream().anyMatch(attributeData -> Objects.nonNull(attributeData.getType())));
    }

    @Test
    public void loadDictionaryWithAttributeSupportedValues() throws Exception {
        diameterDictionaryLoader.load(diameterDictionary);

        DiameterDictionaryModel diameterDictionaryModel = getDiameterDictionaryModel();

        assertTrue(diameterDictionaryModel.getIdtoVendorInformation().get(DiameterDictionaryDataBuilder.VENDOR_ID).getAttributeData().stream().anyMatch(attributeData -> attributeData.getIdToSupportedValues().size() != 0 ? attributeData.getIdToSupportedValues().get("22").equals("test") : true));
    }
    @Test
    public void uniqueVendorIdsOnLoadDictionary() throws Exception {
        diameterDictionaryLoader.load(diameterDictionary);

        DiameterDictionaryModel diameterDictionaryModel = getDiameterDictionaryModel();

        assertTrue(expectedDiameterDictionaryModel.getVendorIds().size() == diameterDictionaryModel.getVendorIds().size());
    }

    public static List<AttributeData> getAttributeData() {
        List<AttributeData> attributeData = new ArrayList<>();

        attributeData.add(getNullAttribute());

        attributeData.add(getStringAttribute());

        attributeData.add(getGroupedAttribute());

        attributeData.add(getAttributeSupportedValues());

        return attributeData;
    }

    @After
    public void tearDown() throws Exception {
        temporaryFolder.delete();
        hibernateSessionFactory.shutdown();;
    }
}
