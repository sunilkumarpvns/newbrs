package com.elitecore.corenetvertex.pm.rnc.notification;

import java.util.ArrayList;
import java.util.List;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.rncpackage.notification.RncNotificationData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.RnCPkgBuilder;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;


import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class SessionThresholdNotificationSchemeFactoryTest {
    public static final int THRESHOLD = 20;
    private List<String> partialFailReasons = new ArrayList<>();
    private RnCFactory rnCFactory = new RnCFactory();
    private ThresholdNotificationSchemeFactory factory = new ThresholdNotificationSchemeFactory(rnCFactory);
    private ChargingType chargingType = ChargingType.SESSION;

    @Before
    public void setup(){
    }

    public class SuccessCases{
        @Test
        public void createThresholdNotificationSchemeSuccuessfulWhenValidValuesProvided(){
            RncPackageData rncPackageWithNonMonetaryRateCard = RnCPkgBuilder.rncBasePackageWithThresholdNotifications();
            ThresholdNotificationScheme actual = factory.createThresholdNotificationScheme(rncPackageWithNonMonetaryRateCard.getRncNotifications(), chargingType, partialFailReasons);
            ReflectionAssert.assertLenientEquals(createExpectedThresholdNotification(actual),actual);
            Assert.assertNotNull(actual.getThresholdEvents());
        }
    }

    public class FailCases{

        @Test
        public void createThresholdNotificationSchemeFailsWhenNoNotificationTemplateIsAttached(){
            RncPackageData rncPackageData = RnCPkgBuilder.rncBasePackageWithThresholdNotifications();
            factory.createThresholdNotificationScheme(getInvalidRncNotifications(rncPackageData), chargingType, partialFailReasons);
			assertThat(partialFailReasons.get(0), containsString("Email or SMS template is not configured"));
        }
    }
    private ThresholdNotificationScheme createExpectedThresholdNotification(ThresholdNotificationScheme thresholdNotificationScheme) {
        ThresholdNotificationScheme thresholdNotification = new ThresholdNotificationScheme(thresholdNotificationScheme.getThresholdEvents());
        return thresholdNotification;
    }

    public List<RncNotificationData> getInvalidRncNotifications(RncPackageData rncPackageData) {
        List<RncNotificationData> rncNotifications = Collectionz.newArrayList();
        RncNotificationData rncNotificationData = new RncNotificationData();
        rncNotificationData.setThreshold(THRESHOLD);

        rncNotificationData.setRncPackageData(rncPackageData);
        rncNotificationData.setRateCardData(rncPackageData.getRateCardGroupData().get(0).getPeakRateRateCard());
        rncNotifications.add(rncNotificationData);
        return rncNotifications;
    }

    @After
    public void after(){
        partialFailReasons.clear();
    }
}
