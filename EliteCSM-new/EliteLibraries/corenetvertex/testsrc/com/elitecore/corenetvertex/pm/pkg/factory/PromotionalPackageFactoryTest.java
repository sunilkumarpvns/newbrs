package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.Collections;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pm.PkgDataBuilder;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PromotionalPackageFactoryTest {
	private PromotionalPackageFactory factory;
	private QoSProfileFactory qoSProfileFactory;
	private UsageNotificationSchemeFactory usageNotificationSchemeFactory;
	private QuotaNotificationSchemeFactory quotaNotificationSchemeFactory;
	private PackageFactory packageFactory;
	private PkgData pkgData;

	@Before
	public void setUp() throws Exception {
		packageFactory = new PackageFactory();
		UMBasedQuotaProfileFactory umBasedQuotaProfileFactory = new UMBasedQuotaProfileFactory(packageFactory);
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
		pkgData = PkgDataBuilder.newBasePackageWithDefaultValues(false);
	}

	@Test
	public void failWhenDeploymentTypeOCS() {
		DeploymentMode deploymentMode = DeploymentMode.OCS;
		factory = new PromotionalPackageFactory(qoSProfileFactory,
				usageNotificationSchemeFactory,
				packageFactory,
				deploymentMode);
		Package promotionalPackage = factory.createPromotionalPackage(pkgData, PkgStatus.ACTIVE, Collections.emptyList());
		assertEquals(PolicyStatus.FAILURE, promotionalPackage.getStatus());
		assertThat(promotionalPackage.getFailReason(), containsString("Promotional package is not supported with deployment mode: " + deploymentMode));
	}

	@Test
	public void successWhenDeploymentTypePCRF() {
		DeploymentMode deploymentMode = DeploymentMode.PCRF;
		factory = new PromotionalPackageFactory(qoSProfileFactory,
				usageNotificationSchemeFactory,
				packageFactory,
				deploymentMode);
		Package promotionalPackage = factory.createPromotionalPackage(pkgData, PkgStatus.ACTIVE, Collections.emptyList());
		assertEquals(PolicyStatus.SUCCESS, promotionalPackage.getStatus());
	}

	@Test
	public void failWhenDeploymentTypePCC() {
		DeploymentMode deploymentMode = DeploymentMode.PCC;
		factory = new PromotionalPackageFactory(qoSProfileFactory,
				usageNotificationSchemeFactory,
				packageFactory,
				deploymentMode);
		Package promotionalPackage = factory.createPromotionalPackage(pkgData, PkgStatus.ACTIVE, Collections.emptyList());
		assertEquals(PolicyStatus.FAILURE, promotionalPackage.getStatus());
		assertThat(promotionalPackage.getFailReason(), containsString("Promotional package is not supported with deployment mode: " + deploymentMode));
	}
}