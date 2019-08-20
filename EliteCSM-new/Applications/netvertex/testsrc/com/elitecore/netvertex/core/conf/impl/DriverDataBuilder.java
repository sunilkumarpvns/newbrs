package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.corenetvertex.sm.driver.constants.ReportingType;
import com.elitecore.corenetvertex.sm.driver.csv.CsvDriverData;

import java.util.UUID;

public class DriverDataBuilder {

    public static DriverData newDriverDataWithDefaultValues() {
        DriverData driverData = newDriverData();
        driverData.setCsvDriverData(newCSVDriverData());
        return driverData;
    }

    public static DriverData newDriverDataWithTimeBoundryDaily(){
        DriverData driverData = newDriverData();
        CsvDriverData csvDriverData = newCSVDriverData();
        csvDriverData.setTimeBoundary(1440);
        csvDriverData.setTimeBasedRollingUnit(null);
        csvDriverData.setSizeBasedRollingUnit(null);
        csvDriverData.setRecordBasedRollingUnit(null);
        driverData.setCsvDriverData(csvDriverData);
        return driverData;
    }

    public static DriverData newCSVDriverWithTimeBaseRollingUnit(){
        DriverData driverData = newDriverData();
        CsvDriverData csvDriverData = newCSVDriverData();
        csvDriverData.setTimeBasedRollingUnit(5l);
        csvDriverData.setTimeBoundary(null);
        csvDriverData.setRecordBasedRollingUnit(null);
        csvDriverData.setSizeBasedRollingUnit(null);
        driverData.setCsvDriverData(csvDriverData);
        return driverData;
    }

    public static DriverData newCSVDriverWithSizeBaseRollingUnit(){
        DriverData driverData = newDriverData();
        CsvDriverData csvDriverData = newCSVDriverData();
        csvDriverData.setSizeBasedRollingUnit(100l);
        csvDriverData.setTimeBasedRollingUnit(null);
        csvDriverData.setTimeBoundary(null);
        csvDriverData.setRecordBasedRollingUnit(null);
        driverData.setCsvDriverData(csvDriverData);
        return driverData;
    }

    public static DriverData newCSVDriverWithRecordBaseRollingUnit(){
        DriverData driverData = newDriverData();
        CsvDriverData csvDriverData = newCSVDriverData();
        csvDriverData.setRecordBasedRollingUnit(5000l);
        csvDriverData.setTimeBoundary(null);
        csvDriverData.setTimeBasedRollingUnit(null);
        csvDriverData.setSizeBasedRollingUnit(null);
        driverData.setCsvDriverData(csvDriverData);
        return driverData;
    }


    public static DriverData newCSVDriverWithAllRollingUnitsConfiguredExceptTimeBoundry(){
        DriverData driverData = newDriverData();
        CsvDriverData csvDriverData = newCSVDriverData();
        csvDriverData.setTimeBasedRollingUnit(5l);
        csvDriverData.setSizeBasedRollingUnit(100l);
        csvDriverData.setRecordBasedRollingUnit(5000l);
        driverData.setCsvDriverData(csvDriverData);
        return driverData;
    }

    public static CsvDriverData newCSVDriverData() {
        CsvDriverData csvDriverData = new CsvDriverData();
        csvDriverData.setHeader("false");
        csvDriverData.setReportingType(ReportingType.CHARGING_CDR.name());
        csvDriverData.setTimeBoundary(1);
        csvDriverData.setSizeBasedRollingUnit(1l);
        csvDriverData.setRecordBasedRollingUnit(1l);
        return csvDriverData;
    }

    private static DriverData newDriverData() {
        DriverData driverData = new DriverData();
        driverData.setId(UUID.randomUUID().toString());
        driverData.setName("CDR_Driver_Test");
        return driverData;
    }


}
