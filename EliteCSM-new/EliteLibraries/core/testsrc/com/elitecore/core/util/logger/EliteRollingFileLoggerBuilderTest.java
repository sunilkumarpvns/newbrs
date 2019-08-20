package com.elitecore.core.util.logger;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class EliteRollingFileLoggerBuilderTest {

	private static final String ANY_NON_NULL_STRING = "ANY_STRING";
	private static final int TIME_BASED_ROLLING_TYPE = 1;
	private static final int SIZE_BASED_ROLLING_TYPE = 2;
	
	private static final int TIME_BASED_ROLLING_EVERY_MINUTE = 3;
	private static final int TIME_BASED_ROLLING_EVERY_HOUR = 4;
	private static final int TIME_BASED_ROLLING_EVERY_DAY = 5;

	public EliteRollingFileLogger logger;
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	public Object[] rollingTypes() {
		return $(
				$(TIME_BASED_ROLLING_TYPE),
				$(SIZE_BASED_ROLLING_TYPE)
		);
	}
	
	public Object[] timeBasedRollingUnits() {
		return $(
				$(TIME_BASED_ROLLING_EVERY_MINUTE),
				$(TIME_BASED_ROLLING_EVERY_HOUR),
				$(TIME_BASED_ROLLING_EVERY_DAY)
		);
	}
	
	@Test
	public void test_ConstantForTimeBasedRolling_MustBeOne() {
		assertEquals(TIME_BASED_ROLLING_TYPE, EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE);
	}
	
	@Test
	public void test_ConstantForSizeBasedRolling_MustBeTwo() {
		assertEquals(SIZE_BASED_ROLLING_TYPE, EliteRollingFileLogger.SIZE_BASED_ROLLING_TYPE);
	}
	
	@Test
	public void test_ConstantForTimeBasedMinuteWiseRolling_MustBeThree() {
		assertEquals(TIME_BASED_ROLLING_EVERY_MINUTE, EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_MINUTE);
	}
	
	@Test
	public void test_ConstantForTimeBasedHourWiseRolling_MustBeFour() {
		assertEquals(TIME_BASED_ROLLING_EVERY_HOUR, EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_HOUR);
	}
	
	@Test
	public void test_ConstantForTimeBasedDayWiseRolling_MustBeFive() {
		assertEquals(TIME_BASED_ROLLING_EVERY_DAY, EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_DAY);
	}
	
	
	/*
	 *  Tests for Builder
	 */
	
	@Test
	public void testBuilderConstructor_ShouldThrowNPE_IfServerInstanceNameIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("serverInstanceName is null");
		new EliteRollingFileLogger.Builder(null, ANY_NON_NULL_STRING);
	}
	
	@Test
	public void testBuilderConstructor_ShouldThrowNPE_IfTargetFileNameIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("targetFileName is null");
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, null);
	}
	
	@Test
	@Parameters(value = {
			"0",
			"-1",
			"-2",
			"-100",
			"3",
			"4",
			"100"
	})
	public void testBuilder_RollingType_ShouldThrowIllegalArgumentException_IfRollingTypeIsNotOneOfTimeBasedOrSizeBased(int rollingType) {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("rollingType should be one of Time Based (1) or Size Based (2)");
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.rollingType(rollingType);
	}
	
	@Test
	@Parameters(method = "rollingTypes")
	public void testBuilder_RollingType_ShouldAcceptPassedRollingType_IfRollingTypeIsOneOfTimeBasedOrSizeBased(int rollingType) {
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.rollingType(rollingType);
	}
	
	@Test
	@Parameters(value = {
			"0",
			"-1",
			"-2",
			"-100"
	})
	public void testBuilder_RollingUnit_ShouldThrowIllegalArgumentException_IfRollingUnitIsNotGreaterThanZero(int rollingUnit) {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid value: " + rollingUnit + " for rollingUnit."
				+ " Value should be greater than zero");
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.rollingUnit(rollingUnit);
	}
	
	@Test
	@Parameters(value = {
			"1",
			"10",
			"100"
	})
	public void testBuilder_RollingUnit_ShouldAcceptPassedRollingUnit_IfRollingUnitIsGreaterThanZero(int rollingUnit) {
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.rollingUnit(rollingUnit);
	}
	
	@Test
	@Parameters(value = {
			"0",
			"-1",
			"-2",
			"-100"
	})
	public void testBuilder_MaxRolledUnits_ShouldThrowIllegalArgumentException_IfRollingTypeIsNotGreaterThanZero(int maxRolledUnits) {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid value: " + maxRolledUnits + " for maxRolledUnits."
				+ " Value should be greater than zero");
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.maxRolledUnits(maxRolledUnits);
	}
	
	@Test
	@Parameters(value = {
			"1",
			"10",
			"100"
	})
	public void testBuilder_MaxRolledUnits_ShouldAcceptPassedMaxRolledUnits_IfMaxRolledUnitsIsGreaterThanZero(int maxRolledUnits) {
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.maxRolledUnits(maxRolledUnits);
	}
	
	@Test
	@Parameters({
		"true",
		"false"
	})
	public void testBuilder_CompressRolledUnits_ShouldAcceptValuePassed(boolean compressRolledUnits) {
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.compressRolledUnits(compressRolledUnits);
	}
	
	@Test
	@Parameters(method = "dataFor_testBuilder_SysLogParameters_ShouldThrowNPE_IfEitherOfSysLogHostIPOrSysLogFacilityIsNull")
	public void testBuilder_SysLogParameters_ShouldThrowNPE_IfEitherOfSysLogHostIPOrSysLogFacilityIsNull(String sysLogHostIP, String sysLogFacility, String expectedExceptionMessage) {
		exception.expect(NullPointerException.class);
		exception.expectMessage(expectedExceptionMessage);
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.sysLogParameters(sysLogHostIP, sysLogFacility);
	}
	
	public Object[] dataFor_testBuilder_SysLogParameters_ShouldThrowNPE_IfEitherOfSysLogHostIPOrSysLogFacilityIsNull() {
		return $(
				//sysLogHostIP			//sysLogFacility		// exceptionMessage
				$(null,					null,					"syslogHostIP is null"),
				$(null,					ANY_NON_NULL_STRING,	"syslogHostIP is null"),
				$(ANY_NON_NULL_STRING,	null,					"syslogFacility is null")
		);
	}

	@Test
	@Parameters(method = "dataFor_testBuilder_SyslogParameters_ShouldAcceptValuesPassed_IfBothSyslogHostIPAndSyslogFacilityAreNonNull")
	public void testBuilder_SyslogParameters_ShouldAcceptValuesPassed_IfBothSyslogHostIPAndSyslogFacilityAreNonNull(String syslogHostIP, String syslogFacility) {
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.sysLogParameters(syslogHostIP, syslogFacility);
	}
	
	public Object[] dataFor_testBuilder_SyslogParameters_ShouldAcceptValuesPassed_IfBothSyslogHostIPAndSyslogFacilityAreNonNull() {
		return $(
				$("",						""),
				$(ANY_NON_NULL_STRING,		""),
				$("",						ANY_NON_NULL_STRING),
				$(ANY_NON_NULL_STRING,		ANY_NON_NULL_STRING)
		);
	}
	
	/*@Test
	@Parameters({
		"true",
		"false"
	})
	public void testBuilder_ImmediateFlush_ShouldAcceptValuePassed(boolean immediateFlush) {
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.immediateFlush(immediateFlush);
	}*/
	
	@Test
	public void testBuilder_Build_ShouldReturnCreatedLogger_WhenAllMandatoryParametersArePassedInConstructor() {
		logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING,
				ANY_NON_NULL_STRING).build();
		assertNotNull(logger);
	}	
	
	
	@Test
	@Parameters({
		"1",
		"2",
		"6",
		"7",
		"100"
	})
	public void testBuilder_Build_ShouldThrowIllegalStateException_IfRollingTypeIsTimeBasedAndRollingUnitIsNotAsExpected(int rollingUnit) {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Invalid value: " + rollingUnit + " of rollingUnit"
				+ " for rollingType: TIME_BASED_ROLLING");
		
		new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.rollingType(EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE)
		.rollingUnit(rollingUnit)
		.build();
	}
	
	
	@Test
	@Parameters(method = "timeBasedRollingUnits")
	public void testBuilder_Build_ShouldCreateLogger_IfRollingTypeIsTimeBasedAndRollingUnitIsAsExpected(int rollingUnit) {
		logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.rollingType(EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE)
		.rollingUnit(rollingUnit)
		.build();
		assertNotNull("logger should not be null", logger);
	}
	
	
	@Test
	@Parameters({
		"1",
		"2",
		"3",
		"4",
		"5",
		"100"
	})
	public void testBuilder_Build_ShouldCreateLogger_IfRollingTypeIsSizeBasedAndRollingUnitIsGreaterThanZero(int rollingUnit) {
		logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.rollingType(EliteRollingFileLogger.SIZE_BASED_ROLLING_TYPE)
		.rollingUnit(rollingUnit)
		.build();
		assertNotNull("logger should not be null", logger);
	}
	
	@Test
	public void testBuilder_Build_ShouldCreateLoggerWithTimeBasedRolling_WhenNoValueWasProvidedUsingBuilder() {
		logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.build();
		assertEquals(TIME_BASED_ROLLING_TYPE, logger.getRollingType());
	}
	
	@Test
	public void testBuilder_Build_ShouldCreateLoggerWithTimeBasedDayWiseRollingUnit_WhenNoValueWasProvidedUsingBuilder() {
		logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.build();
		assertEquals(5, logger.getRollingUnit());
	}
	
	@Test
	public void testBuilder_Build_ShouldCreateLoggerWithMaxRolledUnitsAsOne_WhenNoValueWasProvidedUsingBuilder() {
		logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.build();
		assertEquals(1, logger.getMaxRolledUnits());
	}
	
	@Test
	public void testBuilder_Build_ShouldCreateLoggerWithCompressionOfRolledUnitsEnabled_WhenNoValueWasProvidedUsingBuilder() {
		logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.build();
		assertTrue(logger.isCompressRolledUnit());
	}
	
	/*@Test
	public void testBuilder_Build_ShouldCreateLoggerWithImmediateFlushEnabled_WhenNoValueWasProvidedUsingBuilder() {
		EliteRollingFileLogger logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.build();
		assertTrue(logger.isImmediateFlush());
	}*/
	
	@Test
	@Parameters(method = "rollingTypes")
	public void testBuilder_Build_ShouldCreateLoggerWithValueOfRollingType_ConfiguredUsingBuilder(int rollingType) {
		logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.rollingType(rollingType)
		.build();
		
		assertEquals(rollingType, logger.getRollingType());
	}
	
	@Test
	@Parameters(method = "timeBasedRollingUnits")
	public void testBuilder_Build_ShouldCreateLoggerWithValueOfTimeBasedRollingUnits_ConfiguredUsingBuilder_WhenRollingTypeIsTimeBased(int rollingUnit) {
		logger = new EliteRollingFileLogger.Builder(ANY_NON_NULL_STRING, ANY_NON_NULL_STRING)
		.rollingType(TIME_BASED_ROLLING_TYPE)
		.rollingUnit(rollingUnit)
		.build();
		
		assertEquals(rollingUnit, logger.getRollingUnit());
	}


	@After
	public void closeLogger (){
		if(logger != null) {
			logger.close();
		}
	}
	
	/*
	 * End of tests for Builder
	 */
}
