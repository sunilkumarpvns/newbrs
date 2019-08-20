package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.notification.TopUpNotificationData;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class QuotaTopUpFactoryTest {

    private QuotaNotificationSchemeFactory quotaNotificationSchemeFactory;
    private PackageFactory packageFactory;
    private QuotaTopUpFactory factory;

    @Before
    public void setUp() throws Exception {
        quotaNotificationSchemeFactory = spy(new QuotaNotificationSchemeFactory(packageFactory));
        packageFactory = new PackageFactory();
        factory = new QuotaTopUpFactory(quotaNotificationSchemeFactory, packageFactory, DeploymentMode.PCC);
    }

    public class CreateShouldCallCreateOfQuotaNotificationFactoryWhen {

        @Test
        public void notificationDataIsNull() throws Exception {
            DataTopUpData dataTopUpData = createDataTopUpPackage();
            dataTopUpData.setTopUpNotificationList(null);
            factory.create(Arrays.asList(dataTopUpData));

            verify(quotaNotificationSchemeFactory, times(1)).createTopUpQuotaNotificationScheme(eq(null), any(), any());
        }

        @Test
        public void notificationDataIsEmpty() throws Exception {
            DataTopUpData dataTopUpData = createDataTopUpPackage();
            TopUpNotificationData topUpNotificationData = new TopUpNotificationData();
            List<TopUpNotificationData> topUpNotificationDatas = Arrays.asList(topUpNotificationData);
            dataTopUpData.setTopUpNotificationList(topUpNotificationDatas);
            factory.create(Arrays.asList(dataTopUpData));

            verify(quotaNotificationSchemeFactory, times(1)).createTopUpQuotaNotificationScheme(eq(topUpNotificationDatas), any(), any());
        }

        @Test
        public void notificationDataConfigured() throws Exception {
            DataTopUpData dataTopUpData = createDataTopUpPackage();
            TopUpNotificationData topUpNotificationData = new TopUpNotificationData();
            topUpNotificationData.setThreshold(1000);
            List<TopUpNotificationData> topUpNotificationDatas = Arrays.asList(topUpNotificationData);
            dataTopUpData.setTopUpNotificationList(topUpNotificationDatas);
            factory.create(Arrays.asList(dataTopUpData));

            verify(quotaNotificationSchemeFactory, times(1)).createTopUpQuotaNotificationScheme(eq(topUpNotificationDatas), any(), any());
        }
    }

    public class FailReasonShouldBeAddedWhen {

        @Test
        public void deploymentModeIsPCRF() throws Exception{
            DeploymentMode deploymentMode = DeploymentMode.PCRF;
            QuotaTopUpFactory topUpFactory = new QuotaTopUpFactory(quotaNotificationSchemeFactory, packageFactory, deploymentMode);

            DataTopUpData dataTopUpData = createDataTopUpPackage();
            TopUpNotificationData topUpNotificationData = new TopUpNotificationData();
            topUpNotificationData.setThreshold(1000);
            List<TopUpNotificationData> topUpNotificationDatas = Arrays.asList(topUpNotificationData);
            dataTopUpData.setTopUpNotificationList(topUpNotificationDatas);
            List<QuotaTopUp> quotaTopUps = topUpFactory.create(Arrays.asList(dataTopUpData));
            String expectedstring = "[Top-Up is not compatible with deployment mode: "+deploymentMode.getValue()+"]";
            Assert.assertEquals(expectedstring, quotaTopUps.get(0).getFailReason());
        }

        @Test
        public void totalBalanceIsNotConfigurred() throws Exception {
            DataTopUpData dataTopUpData = createDataTopUpPackage();
            dataTopUpData.setId("ID");
            TopUpNotificationData topUpNotificationData = new TopUpNotificationData();
            topUpNotificationData.setThreshold(1000);
            List<TopUpNotificationData> topUpNotificationDatas = Arrays.asList(topUpNotificationData);
            dataTopUpData.setTopUpNotificationList(topUpNotificationDatas);
            dataTopUpData.setVolumeBalance(null);
            List<QuotaTopUp> quotaTopUps = factory.create(Arrays.asList(dataTopUpData));
            String expectedstring = "[Quota profile (NAME-QuotaProfile) parsing fail. Cause by:Quota Profile Detail(ID) parsing fail. " +
                    "Cause by:Volume Balance not defined for defined Volume Unit Type: TOTAL]";
            Assert.assertEquals(expectedstring, quotaTopUps.get(0).getFailReason());
        }

        @Test
        public void uploadBalanceIsNotConfigurred() throws Exception {
            DataTopUpData dataTopUpData = createDataTopUpPackage();
            dataTopUpData.setId("ID");
            TopUpNotificationData topUpNotificationData = new TopUpNotificationData();
            topUpNotificationData.setThreshold(1000);
            List<TopUpNotificationData> topUpNotificationDatas = Arrays.asList(topUpNotificationData);
            dataTopUpData.setTopUpNotificationList(topUpNotificationDatas);
            dataTopUpData.setUnitType(VolumeUnitType.UPLOAD.name());
            dataTopUpData.setVolumeBalance(null);
            List<QuotaTopUp> quotaTopUps = factory.create(Arrays.asList(dataTopUpData));
            String expectedstring = "[Quota profile (NAME-QuotaProfile) parsing fail. Cause by:Quota Profile Detail(ID) parsing fail. " +
                    "Cause by:Volume Balance not defined for defined Volume Unit Type: UPLOAD]";
            Assert.assertEquals(expectedstring, quotaTopUps.get(0).getFailReason());
        }

        @Test
        public void downloadBalanceIsNotConfigurred() throws Exception {
            DataTopUpData dataTopUpData = createDataTopUpPackage();
            dataTopUpData.setId("ID");
            TopUpNotificationData topUpNotificationData = new TopUpNotificationData();
            topUpNotificationData.setThreshold(1000);
            List<TopUpNotificationData> topUpNotificationDatas = Arrays.asList(topUpNotificationData);
            dataTopUpData.setTopUpNotificationList(topUpNotificationDatas);
            dataTopUpData.setUnitType(VolumeUnitType.DOWNLOAD.name());
            dataTopUpData.setVolumeBalance(null);
            List<QuotaTopUp> quotaTopUps = factory.create(Arrays.asList(dataTopUpData));
            String expectedstring = "[Quota profile (NAME-QuotaProfile) parsing fail. Cause by:Quota Profile Detail(ID) parsing fail. " +
                    "Cause by:Volume Balance not defined for defined Volume Unit Type: DOWNLOAD]";
            Assert.assertEquals(expectedstring, quotaTopUps.get(0).getFailReason());
        }

        @Test
        public void timeBalanceIsNotConfigurred() throws Exception {
            DataTopUpData dataTopUpData = createDataTopUpPackage();
            dataTopUpData.setId("ID");
            TopUpNotificationData topUpNotificationData = new TopUpNotificationData();
            topUpNotificationData.setThreshold(1000);
            List<TopUpNotificationData> topUpNotificationDatas = Arrays.asList(topUpNotificationData);
            dataTopUpData.setTopUpNotificationList(topUpNotificationDatas);
            dataTopUpData.setQuotaType(QuotaUsageType.TIME.name());
            dataTopUpData.setTimeBalance(null);
            List<QuotaTopUp> quotaTopUps = factory.create(Arrays.asList(dataTopUpData));
            String expectedstring = "[Quota profile (NAME-QuotaProfile) parsing fail. Cause by:Quota Profile Detail(ID) parsing fail. " +
                    "Cause by:Time Balance not defined for defined Time Unit Type: null]";
            Assert.assertEquals(expectedstring, quotaTopUps.get(0).getFailReason());
        }
    }

    private DataTopUpData createDataTopUpPackage() {
        DataTopUpData data = new DataTopUpData();
        data.setPackageMode(PkgMode.LIVE.name());
        data.setName("NAME");
        data.setQuotaType(QuotaProfileType.RnC_BASED.name());
        data.setTopupType(TopUpType.TOP_UP.name());
        data.setVolumeBalance(100l);
        data.setUnitType(VolumeUnitType.TOTAL.name());
        data.setQuotaType(QuotaUsageType.VOLUME.name());
        data.setValidityPeriod(10);
        data.setValidityPeriodUnit(ValidityPeriodUnit.MID_NIGHT);
        data.setVolumeBalanceUnit(DataUnit.MB.name());
        return data;
    }
}