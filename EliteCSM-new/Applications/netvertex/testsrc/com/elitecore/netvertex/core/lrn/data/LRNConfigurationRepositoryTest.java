package com.elitecore.netvertex.core.lrn.data;

import com.elitecore.corenetvertex.pd.lrn.LrnData;
import com.elitecore.corenetvertex.sm.routing.network.BrandData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import com.elitecore.netvertex.core.locationmanagement.LocationInfoManager;
import com.elitecore.netvertex.core.locationmanagement.LocationRepository;
import com.elitecore.netvertex.core.locationmanagement.util.NetworkDataBuilder;
import com.elitecore.netvertex.core.lrn.LRNConfigurable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static com.elitecore.corenetvertex.util.HibernateSessionFactory.create;
import static org.junit.Assert.assertTrue;


public class LRNConfigurationRepositoryTest {

    private static final String OPERATOR_NAME = "VODAFONE-IN";
    private static final String BRAND_NAME = "VODAFONE";
    private static final String COUNTRY_NAME = "INDIA";
    private static final String COUNTRY_CODE = "IN";
    private static final  String VALID_LRN = "2728";
    private HibernateSessionFactory hibernateSessionFactory;
    LocationRepository locationRepository;
    NetworkDataBuilder networkDataBuilder;
    private LRNConfigurationRepository lrnConfigurationRepository;
    NetworkData vodafoneGujrat;


    @Before
    public void setUp() throws Exception {
        hibernateSessionFactory = createSessionFactory();
        networkDataBuilder = new NetworkDataBuilder(hibernateSessionFactory);
        prepareNetworkInformation();
        LocationInfoManager locationInfo = new LocationInfoManager(hibernateSessionFactory.getSessionFactory());
        locationInfo.init();
        locationRepository =locationInfo;
        this.locationRepository = locationInfo;
        LRNConfigurable lrnConfigurable = new LRNConfigurable(hibernateSessionFactory.getSessionFactory(), locationRepository);
        lrnConfigurable.read();
        lrnConfigurationRepository = lrnConfigurable;

    }

    private void prepareNetworkInformation() {
        CountryData countryData = networkDataBuilder.saveCountryData(COUNTRY_NAME, COUNTRY_CODE);
        BrandData brandData = networkDataBuilder.saveBrandData(BRAND_NAME);
        OperatorData operatorData = networkDataBuilder.saveOperatorData(OPERATOR_NAME);

        vodafoneGujrat = networkDataBuilder.createNetworkWithNetworkDetails(NetworkDetail.VODAFONE_GUJRAT.mcc, NetworkDetail.VODAFONE_GUJRAT.mnc, NetworkDetail.VODAFONE_GUJRAT.networkName, networkDataBuilder.getNetworkWithBasicInfo(countryData, brandData, operatorData));
        NetworkData vodafoneMumbai = networkDataBuilder.createNetworkWithNetworkDetails(NetworkDetail.VODAFONE_MUMBAI.mcc, NetworkDetail.VODAFONE_MUMBAI.mnc, NetworkDetail.VODAFONE_MUMBAI.networkName, networkDataBuilder.getNetworkWithBasicInfo(countryData, brandData, operatorData));
        NetworkData vodafoneChennai =networkDataBuilder.createNetworkWithNetworkDetails(NetworkDetail.VODAFONE_CHENNAI.mcc, NetworkDetail.VODAFONE_CHENNAI.mnc, NetworkDetail.VODAFONE_CHENNAI.networkName, networkDataBuilder.getNetworkWithBasicInfo(countryData, brandData, operatorData));
        LrnData lrnData1 = new LrnData();
        lrnData1.setLrn(VALID_LRN);
        lrnData1.setNetworkData(vodafoneGujrat);
        lrnData1.setOperatorData(vodafoneGujrat.getOperatorData());
        hibernateSessionFactory.save(lrnData1);
   }

    private HibernateSessionFactory createSessionFactory() throws IOException {
        String sid = UUID.randomUUID().toString();
        String connectionURL = "jdbc:h2:mem:" + sid;
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", connectionURL);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        return create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
    }

    enum NetworkDetail {
        VODAFONE_GUJRAT(404, 5, "VODAFONE-GUJ"),
        VODAFONE_MUMBAI(404, 20, "VODAFONE-MUM"),
        VODAFONE_CHENNAI(404, 84, "VODAFONE-CH");
        private final int mcc;
        private final int mnc;
        private final String networkName;


        NetworkDetail(int mcc, int mnc, String networkName) {
            this.mcc = mcc;
            this.mnc = mnc;
            this.networkName = networkName;
        }
    }
    
    @After
    public void tearDown() throws Exception {
        hibernateSessionFactory.shutdown();
    }

    @Test
    public void getLRNConfigurationByLRNreturnNullIfNullKeyIsPassed() {
        assertTrue(Objects.isNull(lrnConfigurationRepository.getLRNConfigurationByLRN(null)));
    }

    @Test
    public void getLRNConfigurationByLRNreturnNullIfInValidLRNValusIsPassed(){
        String invalidLRN = "11111";
        assertTrue(Objects.isNull(lrnConfigurationRepository.getLRNConfigurationByLRN(invalidLRN)));
    }
    @Test
    public void getLRNConfigurationByLRNreturnValidLRNConfigurationIfvalidLRNValusIsPassed(){
        LRNConfiguration vodafoneLRN = lrnConfigurationRepository.getLRNConfigurationByLRN(VALID_LRN);
        assertTrue(VALID_LRN.equalsIgnoreCase(vodafoneLRN.getLrn()));
        assertTrue(vodafoneLRN.getNetworkConfiguration().getNetworkName().equalsIgnoreCase(NetworkDetail.VODAFONE_GUJRAT.networkName));
        assertTrue(vodafoneLRN.getNetworkConfiguration().getCountryName().equalsIgnoreCase(vodafoneGujrat.getCountryData().getName()));
        assertTrue(vodafoneLRN.getNetworkConfiguration().getOperator().equalsIgnoreCase(vodafoneGujrat.getOperatorData().getName()));
    }
}