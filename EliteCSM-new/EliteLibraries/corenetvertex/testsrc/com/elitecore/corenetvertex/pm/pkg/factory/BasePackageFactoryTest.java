package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.Collections;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pm.PkgDataBuilder;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class BasePackageFactoryTest {

	private BasePackageFactory basePackageFactory;
	private QoSProfileFactory qoSProfileFactory;
	private UsageNotificationSchemeFactory usageNotificationSchemeFactory;
	private QuotaNotificationSchemeFactory quotaNotificationSchemeFactory;
	private PackageFactory packageFactory;

	@Before
	public void setUp() throws Exception {
		packageFactory = new PackageFactory();
		UMBasedQuotaProfileFactory umBasedQuotaProfileFactory= new UMBasedQuotaProfileFactory(packageFactory);
		SyBasedQuotaProfileFactory syBasedQuotaProfileFactory = new SyBasedQuotaProfileFactory(packageFactory);
		RatingGroupFactory ratingGroupFactory = new RatingGroupFactory(packageFactory);
		DataServiceTypeFactory dataServiceTypeFactory = new DataServiceTypeFactory(ratingGroupFactory, packageFactory);
		RncProfileFactory rncProfileFactory = new RncProfileFactory(packageFactory, ratingGroupFactory, dataServiceTypeFactory);
		DataMonetaryRateCardFactory dataMonetaryRateCardFactory = new DataMonetaryRateCardFactory(packageFactory);
		PCCRuleFactory pccRuleFactory = new PCCRuleFactory(dataServiceTypeFactory, packageFactory);
		ChargingRuleBaseNameFactory chargingRuleBaseNameFactory = new ChargingRuleBaseNameFactory(dataServiceTypeFactory, packageFactory);
		QoSProfileDetailFactory qoSProfileDetailFactory = new QoSProfileDetailFactory(pccRuleFactory, chargingRuleBaseNameFactory, packageFactory);
		qoSProfileFactory = new QoSProfileFactory(umBasedQuotaProfileFactory, syBasedQuotaProfileFactory, qoSProfileDetailFactory, packageFactory, rncProfileFactory, dataMonetaryRateCardFactory);
		usageNotificationSchemeFactory = new UsageNotificationSchemeFactory(packageFactory);
		quotaNotificationSchemeFactory = new QuotaNotificationSchemeFactory(packageFactory);
	}

	public class DeploymentModeIsPCC {

		private PkgData basePackageData;
		private DeploymentMode deploymentMode = DeploymentMode.PCC;

		@Before
		public void setUp() {
			basePackageData = PkgDataBuilder.newBasePackageWithRnCProfileAndPCCProfile(false);
			basePackageFactory = new BasePackageFactory(qoSProfileFactory, usageNotificationSchemeFactory,
					packageFactory, quotaNotificationSchemeFactory, deploymentMode);
		}

		public class FailWhenQuotaProfileTypeIsNotRnCBased {

			@Test
			public void QuotaProfileTypeIsUMBased() {
				basePackageData.setQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED);
				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("Quota profile type: "
						+ QuotaProfileType.USAGE_METERING_BASED
						+ " is not compatible with deployment mode: " + deploymentMode));
			}

			@Test
			public void QuotaProfileTypeIsSyBased() {
				basePackageData.setQuotaProfileType(QuotaProfileType.SY_COUNTER_BASED);
				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("Quota profile type: "
						+ QuotaProfileType.SY_COUNTER_BASED
						+ " is not compatible with deployment mode: " + deploymentMode));
			}
		}

		public class FailWhenQuotaProfileIsRnCBasedAnd {

			@Before
			public void setUp() {
				basePackageData.setQuotaProfileType(QuotaProfileType.RnC_BASED);
			}

			@Test
			public void QuotaConfigured_And_QoSDetailsNotConfigured() {
				removeQoSDetailsFromPackage(basePackageData);

				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("QoS detail is not configured"));
			}

			@Test
			public void QuotaNotConfigured_And_QoSDetailsConfigured() {

				removeRnCProfilePCCPRofile(basePackageData);

				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("RnC Quota Profile/Rate Card is not attached with PCC profile"));
			}

			@Test
			public void RateCardQuotaConfigured_And_QoSDetailsNotConfigured() {
				PkgData basePackageData = PkgDataBuilder.newBasePackageWithRateCardAndPCCProfile(false);
				basePackageData.setQuotaProfileType(QuotaProfileType.RnC_BASED);
				removeQoSDetailsFromPackage(basePackageData);
				BasePackage basePackage = createPackage(basePackageData);
				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("QoS detail is not configured"));
			}

			@Test
			public void RateCardQuotaNotConfigured_And_QoSDetailsConfigured() {
				PkgData basePackageData = PkgDataBuilder.newBasePackageWithRateCardAndPCCProfile(false);
				basePackageData.setQuotaProfileType(QuotaProfileType.RnC_BASED);

				removeRateCardFromQoS(basePackageData);

				BasePackage basePackage = createPackage(basePackageData);
				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("RnC Quota Profile/Rate Card is not attached with PCC profile"));
			}

			@Test
			public void QuotaNotConfigured_And_QoSDetailsNotConfigured() {
				removeQoSDetailsFromPackage(basePackageData);
				removeRnCProfilePCCPRofile(basePackageData);
				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("QoS detail is not configured"));
				assertThat(basePackage.getFailReason(), containsString("RnC Quota Profile/Rate Card is not attached with PCC profile"));
			}

			@Test
			public void RateCardNotConfigured_And_QoSDetailsNotConfigured() {
				PkgData basePackageData = PkgDataBuilder.newBasePackageWithRateCardAndPCCProfile(false);
				basePackageData.setQuotaProfileType(QuotaProfileType.RnC_BASED);

				removeRateCardFromQoS(basePackageData);
				removeRateCardFromQoS(basePackageData);

				BasePackage basePackage = createPackage(basePackageData);
				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("RnC Quota Profile/Rate Card is not attached with PCC profile"));
			}
		}

		public class SuccessWhenQuotaProfileTypeIsRnCBasedAnd {
			@Before
			public void setUp() {
				basePackageData.setQuotaProfileType(QuotaProfileType.RnC_BASED);
			}

			@Test
			public void Quota_and_QoSDetailsConfigured() {
				BasePackage basePackage = createPackage(basePackageData);
				assertEquals(PolicyStatus.SUCCESS, basePackage.getStatus());
			}
		}
	}

	public class DeploymentTypePCRF {

		private PkgData basePackageData;
		private DeploymentMode deploymentMode = DeploymentMode.PCRF;

		@Before
		public void setUp() {
			basePackageData = PkgDataBuilder.newBasePackageWithDefaultValues(false);
			basePackageFactory = new BasePackageFactory(qoSProfileFactory, usageNotificationSchemeFactory,
					packageFactory, quotaNotificationSchemeFactory, deploymentMode);
		}

		public class FailWhen {
			@Test
			public void QuotaProfileTypeIsRnCBased() {
				basePackageData.setQuotaProfileType(QuotaProfileType.RnC_BASED);
				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("Quota profile type: "
						+ QuotaProfileType.RnC_BASED
						+ " is not compatible with deployment mode: " + deploymentMode));
			}

			@Test
			public void qosDetailsNotConfigured() {
				basePackageData.setQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED);

				removeQoSDetailsFromPackage(basePackageData);

				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("QoS detail is not configured"));
			}
		}

		public class SuccessWhenQuotaProfileTypeIsUMBasedAnd {
			@Before
			public void setUp() {
				basePackageData.setQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED);
			}

			@Test
			public void QoSDetailConfigured() {
				BasePackage basePackage = createPackage(basePackageData);
				assertEquals(PolicyStatus.SUCCESS, basePackage.getStatus());
			}
		}
	}

	public class DeploymentTypeOCS {

		private PkgData basePackageData;
		private DeploymentMode deploymentMode = DeploymentMode.OCS;

		@Before
		public void setUp() {
			basePackageData = PkgDataBuilder.newBasePackageWithRnCProfileAndPCCProfile(false);
			basePackageFactory = new BasePackageFactory(qoSProfileFactory, usageNotificationSchemeFactory,
					packageFactory, quotaNotificationSchemeFactory, deploymentMode);
		}

		public class FailWhen {
			@Test
			public void QuotaProfileTypeIsUMBased() {
				basePackageData.setQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED);
				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("Quota profile type: "
						+ QuotaProfileType.USAGE_METERING_BASED
						+ " is not compatible with deployment mode: " + deploymentMode));
			}

			@Test
			public void QuotaProfileTypeIsSyBased() {
				basePackageData.setQuotaProfileType(QuotaProfileType.SY_COUNTER_BASED);
				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("Quota profile type: "
						+ QuotaProfileType.SY_COUNTER_BASED
						+ " is not compatible with deployment mode: " + deploymentMode));
			}

			@Test
			public void qosDetailsConfigured() {
				basePackageData.setQuotaProfileType(QuotaProfileType.RnC_BASED);

				BasePackage basePackage = createPackage(basePackageData);

				assertEquals(PolicyStatus.FAILURE, basePackage.getStatus());
				assertThat(basePackage.getFailReason(), containsString("QoS detail is configured. Deployment mode is " + deploymentMode));
			}
		}

		public class SuccessWhenQuotaProfileTypeIsUMBasedAnd {
			@Before
			public void setUp() {
				basePackageData.setQuotaProfileType(QuotaProfileType.RnC_BASED);
			}

			@Test
			public void QoSDetailConfigured() {
				removeQoSDetailsFromPackage(basePackageData);
				BasePackage basePackage = createPackage(basePackageData);
				assertEquals(PolicyStatus.SUCCESS, basePackage.getStatus());
			}
		}
	}

	private void removeRateCardFromQoS(PkgData basePackageData) {
		for (QosProfileData qoSProfileData : basePackageData.getQosProfiles()) {
			qoSProfileData.setRateCardData(null);
		}
	}
	private void removeQoSDetailsFromPackage(PkgData basePackageData) {
		for (QosProfileData qoSProfileData : basePackageData.getQosProfiles()) {
			qoSProfileData.setQosProfileDetailDataList(Collections.emptyList());
		}
	}
	private void removeRnCProfilePCCPRofile(PkgData basePackageData) {
		for (QosProfileData qoSProfileData : basePackageData.getQosProfiles()) {
			qoSProfileData.setRncProfileData(null);
		}
	}

	private BasePackage createPackage(PkgData basePackageData) {
		return basePackageFactory.createBasePackage(basePackageData, PkgStatus.ACTIVE, Collections.emptyList());
	}
}
