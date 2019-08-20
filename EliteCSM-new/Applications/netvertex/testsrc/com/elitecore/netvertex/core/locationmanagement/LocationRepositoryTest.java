package com.elitecore.netvertex.core.locationmanagement;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.corenetvertex.sm.location.geography.GeographyData;
import com.elitecore.corenetvertex.sm.routing.network.BrandData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import com.elitecore.netvertex.core.locationmanagement.util.NetworkDataBuilder;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static com.elitecore.corenetvertex.util.HibernateSessionFactory.create;
import static org.junit.Assert.assertSame;

@RunWith(HierarchicalContextRunner.class)
public class LocationRepositoryTest {

    private static final String OPERATOR_NAME = "VODAFONE-IN";
    private static final String BRAND_NAME = "VODAFONE";
    private static final String COUNTRY_NAME = "INDIA";
    private static final String COUNTRY_CODE = "IN";
    private static final String GEOGRAPHY = "Asia";
    private HibernateSessionFactory hibernateSessionFactory;
    LocationInfoManager locationInfoManager;
    NetworkDataBuilder networkDataBuilder;


    @Rule
    public PrintMethodRule printMethod = new PrintMethodRule();

    @Before
    public void setUp() throws Exception {
        hibernateSessionFactory = createSessionFactory();
        networkDataBuilder = new NetworkDataBuilder(hibernateSessionFactory);
        prepareNetworkInformation();
        LocationInfoManager locationInfoManager = new LocationInfoManager(hibernateSessionFactory.getSessionFactory());
        locationInfoManager.init();
        this.locationInfoManager = locationInfoManager;
    }

    private void prepareNetworkInformation() {
        CountryData countryData = networkDataBuilder.saveCountryData(COUNTRY_NAME, COUNTRY_CODE);
        BrandData brandData = networkDataBuilder.saveBrandData(BRAND_NAME);
        OperatorData operatorData = networkDataBuilder.saveOperatorData(OPERATOR_NAME);
        List<CountryData> countryDataList = Collectionz.newArrayList();
        countryDataList.add(countryData);
        GeographyData geographyData = networkDataBuilder.saveGeographyData(GEOGRAPHY,countryDataList);
        countryData.setGeography(geographyData.getName());

        networkDataBuilder.createNetworkWithNetworkDetails(NetworkDetail.VODAFONE_GUJRAT.mcc, NetworkDetail.VODAFONE_GUJRAT.mnc, NetworkDetail.VODAFONE_GUJRAT.networkName, networkDataBuilder.getNetworkWithBasicInfo(countryData, brandData, operatorData));
        networkDataBuilder.createNetworkWithNetworkDetails(NetworkDetail.VODAFONE_MUMBAI.mcc, NetworkDetail.VODAFONE_MUMBAI.mnc, NetworkDetail.VODAFONE_MUMBAI.networkName, networkDataBuilder.getNetworkWithBasicInfo(countryData, brandData, operatorData));
        networkDataBuilder.createNetworkWithNetworkDetails(NetworkDetail.VODAFONE_CHENNAI.mcc, NetworkDetail.VODAFONE_CHENNAI.mnc, NetworkDetail.VODAFONE_CHENNAI.networkName, networkDataBuilder.getNetworkWithBasicInfo(countryData, brandData, operatorData));


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
    public void tearDownConnection() throws Exception {
        hibernateSessionFactory.shutdown();
    }

    public class GetNetworkInformationByMCCMNC {

        @Test
        public void shouldReturnNullIfMCCcodeNotExist() {
            Assert.assertTrue(getLocationRepository().getNetworkInformationByMCCMNC("4080") == null);
        }

        @Test
        public void shouldReturnNullIfMNCcodeNotExist() {
            Assert.assertTrue(getLocationRepository().getNetworkInformationByMCCMNC("40450") == null);
        }

        @Test
        public void shouldReturnNullIfMCCMNCDoesntExist() {
            Assert.assertTrue(getLocationRepository().getNetworkInformationByMCCMNC("408100") == null);
        }

        @Test
        public void shouldReturnValidNetworkInformationIfMCCMNCExist() {
            NetworkConfiguration vodafoneGuj = getLocationRepository().getNetworkInformationByMCCMNC(String.valueOf(NetworkDetail.VODAFONE_GUJRAT.mcc)+String.valueOf(NetworkDetail.VODAFONE_GUJRAT.mnc));
            assertSame(vodafoneGuj.getBrand(), BRAND_NAME);
            assertSame(vodafoneGuj.getOperator(), OPERATOR_NAME);
            assertSame(vodafoneGuj.getNetworkName(), NetworkDetail.VODAFONE_GUJRAT.networkName);
            assertSame(vodafoneGuj.getCountryName(), COUNTRY_NAME);
            assertSame(vodafoneGuj.getGeography(),GEOGRAPHY);

        }
    }

    private LocationRepository getLocationRepository() {
        return locationInfoManager;
    }


}