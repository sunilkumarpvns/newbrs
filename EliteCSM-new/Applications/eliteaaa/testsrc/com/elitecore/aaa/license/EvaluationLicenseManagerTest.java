package com.elitecore.aaa.license;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elitecore.license.base.commons.LicenseNameConstants;

public class EvaluationLicenseManagerTest {

	private EvaluationLicenseManager licenseManager;
	
	@Test
	public void licenseEvaluationDaysIsOne() {
		assertEquals(EvaluationLicenseManager.LICENSE_EVALUATION_DAYS, 1L);
	}

	@Test
	public void licenseEvaluationTPSIsTen() {
		assertEquals(EvaluationLicenseManager.EVALUATION_LICENSE_TPS, 100L);
	}

	@Before
	public void setUp() {
		licenseManager = new EvaluationLicenseManager(null, null);
	}
	
	@Test
	public void invalidatesLicenseForTPSGreaterThanTen() {
		
		Assert.assertFalse(licenseManager.validateLicense(LicenseNameConstants.SYSTEM_TPS, "102"));
	}
	
	@Test
	public void LicenseValueIsOneDayWhenLicenseKeyIsSYSTEM_EXPIRY() {
		assertEquals(licenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_EXPIRY), "1");
	}
	
	@Test
	public void LicenseValueIsOneDayWhenLicenseKeyIsSYSTEM_TPS() {
		assertEquals(licenseManager.getLicenseValue(LicenseNameConstants.SYSTEM_TPS), "100");
	}
	
	}
