package com.elitecore.corenetvertex.pm.rnc.notification;

import java.util.ArrayList;
import java.util.List;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.rncpackage.notification.RncNotificationData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pm.RnCPkgBuilder;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.service.notification.Template;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

@RunWith(JUnitParamsRunner.class)
public class EventThresholdNotificationSchemeFactoryTest {
	private RnCFactory rnCFactory = new RnCFactory();
	private List<String> partialFailReasons = new ArrayList<>();
	private ThresholdNotificationSchemeFactory factory;
	private ChargingType chargingType = ChargingType.EVENT;

	@Before
	public void setup() {
		this.factory = new ThresholdNotificationSchemeFactory(rnCFactory);
	}

	public Object[][] dataProviderFor_test_returnNull_WhenNotificationIsNotConfigured() {
		return new Object[][]{
				{
						null
				},
				{
						Collectionz.newArrayList()
				}
		};
	}

	@Test
	@Parameters(method = "dataProviderFor_test_returnNull_WhenNotificationIsNotConfigured")
	public void test_returnNull_WhenNotificationIsNotConfigured(List<RncNotificationData> notificationData) {
		assertNull(factory.createThresholdNotificationScheme(notificationData, chargingType, Collectionz.newArrayList()));
	}

	public Object[][] dataProviderFor_partialFailReasonShouldBeAddedAsPerEmailAndSMSTemplateConfigured() {
		NotificationTemplateData emailTemplate = RnCPkgBuilder.getEmailTemplate();
		NotificationTemplateData smsTemplate = RnCPkgBuilder.getSmsTemplate();

		return new Object[][] {
				{
						null, null, "Email or SMS template is not configured"
				},
				{
						emailTemplate, null, null
				},
				{
						null, smsTemplate, null
				},
				{
						emailTemplate, smsTemplate, null
				},
		};
	}

	@Test
	@Parameters(method = "dataProviderFor_partialFailReasonShouldBeAddedAsPerEmailAndSMSTemplateConfigured")
	public void partialFailReasonShouldBeAddedAsPerEmailAndSMSTemplateConfigured(NotificationTemplateData emailTemplate,
																				 NotificationTemplateData smsTemplate,
																				 String failReason) {
		RncPackageData rncPackageData = RnCPkgBuilder.rncBasePackageWithThresholdNotifications();
		setTemplate(emailTemplate, smsTemplate, rncPackageData);

		ThresholdNotificationScheme actualScheme = factory.createThresholdNotificationScheme(rncPackageData.getRncNotifications(), chargingType, partialFailReasons);
		if (failReason == null) {
			assertNotNull(actualScheme);
		} else {
			assertThat(partialFailReasons.get(0), containsString(failReason));
			assertNull(actualScheme);
		}
	}

	private void setTemplate(NotificationTemplateData emailTemplate, NotificationTemplateData smsTemplate, RncPackageData rncPackageData) {
		rncPackageData.getRncNotifications().get(0).setEmailTemplateData(emailTemplate);
		rncPackageData.getRncNotifications().get(0).setSmsTemplateData(smsTemplate);
	}

	@Test
	public void partialFailReasonShouldBeAdded_when_MonetaryRateCardConfigured() {
		RncPackageData rncPackageData = RnCPkgBuilder.rncBasePackageWithMonetaryPeakRateCard();
		List<RncNotificationData> rncNotifications = createNotificationWithThreshold(rncPackageData, 10);
		ThresholdNotificationScheme actualScheme = factory.createThresholdNotificationScheme(rncNotifications, chargingType, partialFailReasons);
		assertNull(actualScheme);
		assertThat(partialFailReasons.get(0), containsString("Notification can not be generated for monetary rate card"));

	}


	public Object[][] dataProviderFor_partialFailReasonShouldBeAdded_when_ThresholdIsNotConfigured() {
		return new Object[][] {
				{
					0
				},
				{
					-1
				},
				{
					null
				}
		};
	}

	@Test
	@Parameters(method = "dataProviderFor_partialFailReasonShouldBeAdded_when_ThresholdIsNotConfigured")
	public void partialFailReasonShouldBeAdded_when_ThresholdIsNotConfigured(Integer threshold) {
		RncPackageData rncPackageData = RnCPkgBuilder.rncBasePackageWithNonMonetaryOffPeakRateCard();
		List<RncNotificationData> rncNotifications = createNotificationWithThreshold(rncPackageData, threshold);
		ThresholdNotificationScheme actualScheme = factory.createThresholdNotificationScheme(rncNotifications, chargingType, partialFailReasons);
		assertNull(actualScheme);
		assertThat(partialFailReasons.get(0), containsString("Threshold is not defined"));
	}

	private List<RncNotificationData> createNotificationWithThreshold(RncPackageData rncPackageData, Integer threshold) {
		List<RncNotificationData> rncNotifications = RnCPkgBuilder.getRncNotifications(rncPackageData);
		rncNotifications.get(0).setThreshold(threshold);
		return rncNotifications;
	}

	@Test
	public void partialFailReasonShouldBeAdded_when_BalanceNotConfiguredInRateCard() {
		RncPackageData rncPackageData = RnCPkgBuilder.rncBasePackageWithThresholdNotifications();
		setUp_BalanceNotConfigured(rncPackageData);
		ThresholdNotificationScheme actualScheme = factory.createThresholdNotificationScheme(rncPackageData.getRncNotifications(), chargingType, partialFailReasons);
		assertNull(actualScheme);
		assertThat(partialFailReasons.get(0), containsString("Free units are not configured"));
	}

	private void setUp_BalanceNotConfigured(RncPackageData rncPackageData) {
		rncPackageData.getRateCardGroupData().get(0).getPeakRateRateCard().getNonMonetaryRateCardData().setEvent(null);
	}

	@Test
	public void schemeShouldCreatedSuccessfully_when_BalanceConfiguredInRateCard() {
		//Rate Card Events=100 and Notification threshold=30%
		RncPackageData rncPackageData = RnCPkgBuilder.rncBasePackageWithThresholdNotifications();
		ThresholdNotificationScheme actualScheme = factory.createThresholdNotificationScheme(rncPackageData.getRncNotifications(), chargingType, partialFailReasons);
		assertNotNull(actualScheme);

		ThresholdEvent expectedEvent = createExpectedEvent(rncPackageData.getRncNotifications().get(0));
		assertTrue(partialFailReasons.size() == 0);
		assertTrue(actualScheme.getThresholdEvents().size() == 1);
		assertLenientEquals(expectedEvent, actualScheme.getThresholdEvents().get(0));
	}

	private ThresholdEvent createExpectedEvent(RncNotificationData rncNotificationData) {
		Long configuredQuota = rncNotificationData.getRateCardData().getNonMonetaryRateCardData().getEvent();
		NotificationTemplateData emailTemplateData = rncNotificationData.getEmailTemplateData();
		Template emailTemplate = null;
		if (emailTemplateData != null) {
			emailTemplate = new Template(emailTemplateData.getId(), emailTemplateData.getName(), emailTemplateData.getSubject(), emailTemplateData.getTemplateData());
		}

		NotificationTemplateData smsTemplateData = rncNotificationData.getSmsTemplateData();
		Template smsTemplate = null;
		if (smsTemplateData != null) {
			smsTemplate = new Template(smsTemplateData.getId(), smsTemplateData.getName(), smsTemplateData.getSubject(), smsTemplateData.getTemplateData());
		}

		return new ThresholdEvent(rncNotificationData.getRateCardId(), configuredQuota - (configuredQuota * rncNotificationData.getThreshold()/100), emailTemplate, smsTemplate);
	}

	@Test
	public void eventsShouldBeSortedAscendingOrderThreshold() {
		RncPackageData rncPackageData = RnCPkgBuilder.rncBasePackageWithNonMonetaryPeakRateCard();
		RnCPkgBuilder.addRncNotificationsWithThreshold(50, rncPackageData);
		RnCPkgBuilder.addRncNotificationsWithThreshold(30, rncPackageData);
		RnCPkgBuilder.addRncNotificationsWithThreshold(60, rncPackageData);

		ThresholdNotificationScheme notificationScheme = factory.createThresholdNotificationScheme(rncPackageData.getRncNotifications(), chargingType, partialFailReasons);
		assertNotNull(notificationScheme);
		assertTrue(partialFailReasons.size() == 0);
		assertTrue(notificationScheme.getThresholdEvents().size() == 3);

		int expectedThreshold1 = 40;
		int expectedThreshold2 = 50;
		int expectedThreshold3 = 70;

		assertEquals(expectedThreshold1, notificationScheme.getThresholdEvents().get(0).getThreshold());
		assertEquals(expectedThreshold2, notificationScheme.getThresholdEvents().get(1).getThreshold());
		assertEquals(expectedThreshold3, notificationScheme.getThresholdEvents().get(2).getThreshold());
	}
}
