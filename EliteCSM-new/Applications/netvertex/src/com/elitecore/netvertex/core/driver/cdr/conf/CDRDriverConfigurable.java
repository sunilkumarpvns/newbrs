package com.elitecore.netvertex.core.driver.cdr.conf;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.constants.DriverType;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.netvertex.core.conf.impl.CSVCDRDriverConfigurationFactory;
import com.elitecore.netvertex.core.conf.impl.DBCDRDriverConfigurationFactory;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class CDRDriverConfigurable extends BaseConfigurationImpl {

    private static final String MODULE = "DRIVER-CNF";
    private Map<String, DriverConfiguration> driverConfMap;
    private Map<String, DriverConfiguration> driverConfByNameMap;
    private final SessionFactory sessionFactory;

    public CDRDriverConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
        super(serverContext);
        this.sessionFactory = sessionFactory;
        driverConfMap = new HashMap<>();
        driverConfByNameMap = new HashMap<>();
    }

    @Override
    public void readConfiguration() throws LoadConfigurationException {

        Session session = null;
        CSVCDRDriverConfigurationFactory csvCdrDriverConfigurationFactory = new CSVCDRDriverConfigurationFactory();
        DBCDRDriverConfigurationFactory dbCdrDriverConfigurationFactory = new DBCDRDriverConfigurationFactory();
        try {

            session = sessionFactory.openSession();
            List<DriverData> driverDatas = HibernateReader.readAll(DriverData.class, session);

            for(DriverData driverData : driverDatas) {
                try {
                    DriverConfiguration policyDriver;
                    if (DriverType.DB_CDR_DRIVER.name().equals(driverData.getDriverType())) {
                        policyDriver = dbCdrDriverConfigurationFactory.create(driverData);
                    } else {
                        policyDriver = csvCdrDriverConfigurationFactory.create(driverData);
                    }

                    driverConfMap.put(policyDriver.getDriverInstanceId(), policyDriver);
                    driverConfByNameMap.put(policyDriver.getDriverName(), policyDriver);
                } catch(LoadConfigurationException ex) {
                    LogManager.getLogger().error(MODULE, "Error while reading driver configuration with id:" + driverData.getId()  + "(" + driverData.getName() + "). Reason:" + ex.getMessage());
                    LogManager.getLogger().trace(MODULE, ex);
                }


            }

        } catch (Exception e) {
            throw new LoadConfigurationException("Error while reading driver configuration. Reason: " + e.getMessage(), e);
        } finally {
            closeQuietly(session);
        }


    }

    public DriverConfiguration getDriverConfigurationByName(String name) {
        return driverConfByNameMap.get(name);
    }

    public DriverConfiguration getDriverConfigurationById(String id) {
        return driverConfMap.get(id);
    }
}
