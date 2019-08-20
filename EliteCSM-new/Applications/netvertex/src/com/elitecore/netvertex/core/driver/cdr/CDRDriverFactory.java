package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import com.elitecore.corenetvertex.constants.DriverType;
import com.elitecore.corenetvertex.sm.driver.constants.ReportingType;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.driver.cdr.conf.CSVDriverConfiguration;
import com.elitecore.netvertex.core.driver.cdr.conf.impl.DBCDRDriverConfigurationImpl;
import com.elitecore.netvertex.core.driver.cdr.impl.UMCSVLineBuilder;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;

import java.util.HashMap;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class CDRDriverFactory {

    private static final String MODULE = "CDR-DRIVER-FACTORY";
    private Map<String, CDRDriver<ValueProviderExtImpl>> cdrDriverMap;
    private NetVertexServerContext serverContext;
    private AlertListener alertListener;

    public CDRDriverFactory(NetVertexServerContext serverContext, AlertListener alertListener) {
        this.serverContext = serverContext;
        this.alertListener = alertListener;
        cdrDriverMap = new HashMap<>();
    }

    public synchronized CDRDriver<ValueProviderExtImpl> create(DriverConfiguration driverConfiguration) throws InitializationFailedException {

        CDRDriver<ValueProviderExtImpl> cdrDriver = cdrDriverMap.get(driverConfiguration.getDriverInstanceId());
        if (cdrDriver != null) {
            return cdrDriver;
        }

        BaseCSVDriver.CSVLineBuilder<ValueProviderExtImpl> csvLineBuilder = null;

        String csvHeaderLine = null;
        String driverInstanceId = driverConfiguration.getDriverInstanceId();
        if (DriverType.CSV_DRIVER.name().equals(driverConfiguration.getDriverType())) {

            CSVDriverConfiguration csvDriverConf = (CSVDriverConfiguration) driverConfiguration;

            if (ReportingType.UM.name().equals(csvDriverConf.getReportingType())) {
                csvLineBuilder = new UMCSVLineBuilder(csvDriverConf, TimeSource.systemTimeSource());
                csvHeaderLine = new PolicyHeaderBuilder(csvDriverConf).getCSVHeaderLine();
            }

            if (ReportingType.CHARGING_CDR.name().equals(csvDriverConf.getReportingType())) {
                ChargingHeaderBuilder chargingHeaderBuilder = new ChargingHeaderBuilder(csvDriverConf);
                csvHeaderLine = chargingHeaderBuilder.getCSVHeaderLine();
                csvLineBuilder = new MsccCsvLineBuilder(csvDriverConf, TimeSource.systemTimeSource(), chargingHeaderBuilder.getColoumValueProviders());
            }

            NetvertexCSVDriver csvDriver = new NetvertexCSVDriver(serverContext.getServerHome(),
                    csvDriverConf,
                    serverContext.getTaskScheduler(),
                    csvLineBuilder,
                    csvHeaderLine,
                    PCRFKeyBaseFileParameterResolver.create(csvDriverConf.getPrefixFileName(),csvDriverConf.getDirectoryName()));
            csvDriver.init();
            cdrDriverMap.put(driverInstanceId, csvDriver);
            return csvDriver;
        } else if (DriverType.DB_CDR_DRIVER.name().equals(driverConfiguration.getDriverType())) {

            DBCDRDriverConfigurationImpl dbCDRDriverConf = (DBCDRDriverConfigurationImpl) driverConfiguration;
            DBDataSource dataSource = (DBDataSource) serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDatasource(dbCDRDriverConf.getDBDatasourceId());
            NVDBCDRDriver dbCDRDriver = new NVDBCDRDriver(dataSource, dbCDRDriverConf, serverContext.getTaskScheduler());
            dbCDRDriver.init();
            dbCDRDriver.registerAlertListener(alertListener);
            cdrDriverMap.put(driverInstanceId, dbCDRDriver);
            return dbCDRDriver;
        } else {
            throw new InitializationFailedException("Error in initializing CDR driver = " + driverConfiguration.getDriverName()
                    + " of type = " + driverConfiguration.getDriverType() + ". Reason: Unsupported Driver Type");
        }
    }


    public boolean stop() {

        if(getLogger().isInfoLogLevel()) {
           getLogger().info(MODULE, "Stopping CDR drivers" );
        }
        for (CDRDriver<ValueProviderExtImpl> cdrDriver : cdrDriverMap.values()) {
            cdrDriver.stop();
        }

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "All CDR drivers stopped" );
        }

        return true;
    }
}
