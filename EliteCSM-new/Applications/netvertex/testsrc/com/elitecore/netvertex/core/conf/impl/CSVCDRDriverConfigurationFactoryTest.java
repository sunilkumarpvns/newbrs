package com.elitecore.netvertex.core.conf.impl;


import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.netvertex.core.driver.cdr.conf.impl.CSVDriverConfigurationImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CSVCDRDriverConfigurationFactoryTest {


    private CSVCDRDriverConfigurationFactory csvcdrDriverConfigurationFactory;

    @Before
    public void setUp() throws Exception {
        csvcdrDriverConfigurationFactory = new CSVCDRDriverConfigurationFactory();
    }

    @Test
    public void validateDefaultRollingUnitsReturnsOneInEachRollingParameterConfig() throws Exception {
        DriverData driverData = DriverDataBuilder.newDriverDataWithDefaultValues();
        CSVDriverConfigurationImpl config = csvcdrDriverConfigurationFactory.create(driverData);
        Map<RollingTypeConstant, Integer> expectedOutput = new HashMap<>();
        expectedOutput.put(RollingTypeConstant.TIME_BASED_ROLLING,1);
        expectedOutput.put(RollingTypeConstant.SIZE_BASED_ROLLING,1);
        expectedOutput.put(RollingTypeConstant.RECORD_BASED_ROLLING,1);
        Assert.assertEquals(expectedOutput,config.getRollingTypeMap());

    }

    @Test
    public void validateTimeBoundryRollingUnitSetToDaily() throws Exception {
        DriverData driverData = DriverDataBuilder.newDriverDataWithTimeBoundryDaily();
        CSVDriverConfigurationImpl config = csvcdrDriverConfigurationFactory.create(driverData);
        Map<RollingTypeConstant, Integer> expectedOutput = new HashMap<>();
        expectedOutput.put(RollingTypeConstant.TIME_BASED_ROLLING,1440);
        Assert.assertEquals(expectedOutput,config.getRollingTypeMap());

    }

    @Test
    public void validateTimeBaseRollingUnitSetToFive() throws Exception {
        DriverData driverData = DriverDataBuilder.newCSVDriverWithTimeBaseRollingUnit();
        CSVDriverConfigurationImpl config = csvcdrDriverConfigurationFactory.create(driverData);
        Map<RollingTypeConstant, Integer> expectedOutput = new HashMap<>();
        expectedOutput.put(RollingTypeConstant.TIME_BASED_ROLLING,5);
        Assert.assertEquals(expectedOutput,config.getRollingTypeMap());

    }

    @Test
    public void validateSizeBaseRollingUnitSetToHundred() throws Exception {
        DriverData driverData = DriverDataBuilder.newCSVDriverWithSizeBaseRollingUnit();
        CSVDriverConfigurationImpl config = csvcdrDriverConfigurationFactory.create(driverData);
        Map<RollingTypeConstant, Integer> expectedOutput = new HashMap<>();
        expectedOutput.put(RollingTypeConstant.SIZE_BASED_ROLLING,100);
        Assert.assertEquals(expectedOutput,config.getRollingTypeMap());

    }

    @Test
    public void validateRecordBaseRollingUnitSetToFiveThousand() throws Exception {
        DriverData driverData = DriverDataBuilder.newCSVDriverWithRecordBaseRollingUnit();
        CSVDriverConfigurationImpl config = csvcdrDriverConfigurationFactory.create(driverData);
        Map<RollingTypeConstant, Integer> expectedOutput = new HashMap<>();
        expectedOutput.put(RollingTypeConstant.RECORD_BASED_ROLLING,5000);
        Assert.assertEquals(expectedOutput,config.getRollingTypeMap());
    }

    @Test
    public void validateAllReportingTypeProvidedExceptTimeBoundry() throws Exception {
        DriverData driverData = DriverDataBuilder.newCSVDriverWithAllRollingUnitsConfiguredExceptTimeBoundry();
        CSVDriverConfigurationImpl config = csvcdrDriverConfigurationFactory.create(driverData);
        Map<RollingTypeConstant, Integer> expectedOutput = new HashMap<>();
        expectedOutput.put(RollingTypeConstant.TIME_BASED_ROLLING,5);
        expectedOutput.put(RollingTypeConstant.SIZE_BASED_ROLLING,100);
        expectedOutput.put(RollingTypeConstant.RECORD_BASED_ROLLING,5000);
        Assert.assertEquals(expectedOutput,config.getRollingTypeMap());
    }

}