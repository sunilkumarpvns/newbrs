package com.elitecore.netvertex.service.pcrf.conf;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.pccservicepolicy.PccServicePolicyData;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.conf.impl.CSVCDRDriverConfigurationFactory;
import com.elitecore.netvertex.core.conf.impl.DBCDRDriverConfigurationFactory;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.core.util.ConfigLogger;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.impl.PccServicePolicyConfigurationImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class PccServicePolicyConfigurable extends BaseConfigurationImpl {
    private static final String MODULE = "PCRF-SERVICE-POLICY-CNF";
    private Map<String, PccServicePolicyConfiguration> pcrfServicePolicyConfMap;
    private Map<String, PccServicePolicyConfiguration> pcrfServicePolicyConfByNameMap;
    private final SessionFactory sessionFactory;

    public PccServicePolicyConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
        super(serverContext);
        this.sessionFactory = sessionFactory;
        pcrfServicePolicyConfMap = new HashMap<>();
        pcrfServicePolicyConfByNameMap = new HashMap<>();
    }

    @Override
    public void readConfiguration() throws LoadConfigurationException {
        getLogger().info(MODULE, "Read PCRF service configuration started");

        Session session = null;
        String syGateway = null;
        CSVCDRDriverConfigurationFactory csvCdrDriverConfigurationFactory = new CSVCDRDriverConfigurationFactory();
        DBCDRDriverConfigurationFactory dbCdrDriverConfigurationFactory = new DBCDRDriverConfigurationFactory();
        try {

            session = sessionFactory.openSession();
            List<PccServicePolicyData> pcrfServicePolicyDatas = HibernateReader.readAll(PccServicePolicyData.class, session);

            Map<String, PccServicePolicyConfiguration> tempPcrfServicePolicyConfMap = new HashMap<>(pcrfServicePolicyConfMap);
            Map<String, PccServicePolicyConfiguration> tempPcrfServicePolicyConfByName = new HashMap<>(pcrfServicePolicyConfByNameMap);

            for (PccServicePolicyData  pcrfServicePolicyData : pcrfServicePolicyDatas) {

                if(pcrfServicePolicyData.getStatus().equals(ServicePolicyStatus.ACTIVE.name())) {


                    DriverConfiguration policyDriver = null;
                    if (pcrfServicePolicyData.getPolicyCdrDriver() != null) {
                        if (DriverType.DB_CDR_DRIVER.name().equals(pcrfServicePolicyData.getPolicyCdrDriver().getDriverType())) {
                            policyDriver = dbCdrDriverConfigurationFactory.create(pcrfServicePolicyData.getPolicyCdrDriver());
                        } else {
                            policyDriver = csvCdrDriverConfigurationFactory.create(pcrfServicePolicyData.getPolicyCdrDriver());
                        }
                    }


                    DriverConfiguration chargingDriver = null;
                    if (pcrfServicePolicyData.getChargingCdrDriver() != null) {
                        if (DriverType.DB_CDR_DRIVER.name().equals(pcrfServicePolicyData.getChargingCdrDriver().getDriverType())) {
                            chargingDriver = dbCdrDriverConfigurationFactory.create(pcrfServicePolicyData.getChargingCdrDriver());
                        } else {
                            chargingDriver = csvCdrDriverConfigurationFactory.create(pcrfServicePolicyData.getChargingCdrDriver());
                        }
                    }


                    if (pcrfServicePolicyData.getSyGateway() != null) {
                        syGateway = pcrfServicePolicyData.getSyGateway().getName();
                    }

                    String unknownUserPkgId = null;
                    String unknownUserPkgName = null;
                    if(pcrfServicePolicyData.getUnknownUserAction().equals(UnknownUserAction.ALLOW_UNKNOWN_USER.name())){
                        if (pcrfServicePolicyData.getUnknownUserProductOffer() != null) {
                            unknownUserPkgId = pcrfServicePolicyData.getUnknownUserProductOffer().getId();
                            unknownUserPkgName = pcrfServicePolicyData.getUnknownUserProductOffer().getName();
                        }else{
                            throw new LoadConfigurationException("Error while reading unknown user package information.");
                        }
                    }

                    PccServicePolicyConfigurationImpl pcrfServicePolicyConf = new PccServicePolicyConfigurationImpl(
                            pcrfServicePolicyData.getId(), pcrfServicePolicyData.getName(), pcrfServicePolicyData.getDescription(), pcrfServicePolicyData.getRuleset(),
                            RequestAction.valueOf(pcrfServicePolicyData.getAction()), pcrfServicePolicyData.getSubscriberLookupOn(), pcrfServicePolicyData.getIdentityAttribute(),
                            UnknownUserAction.valueOf(pcrfServicePolicyData.getUnknownUserAction()), unknownUserPkgId, unknownUserPkgName,
                            syGateway, SyMode.valueOf(pcrfServicePolicyData.getSyMode()), policyDriver,
                            chargingDriver, pcrfServicePolicyData.getOrderNumber(), ServicePolicyStatus.valueOf(pcrfServicePolicyData.getStatus()));

                    tempPcrfServicePolicyConfMap.put(pcrfServicePolicyData.getId(), pcrfServicePolicyConf);

                    tempPcrfServicePolicyConfByName.put(pcrfServicePolicyConf.getName(), pcrfServicePolicyConf);
                }
            }

            this.pcrfServicePolicyConfMap = tempPcrfServicePolicyConfMap;
            this.pcrfServicePolicyConfByNameMap = tempPcrfServicePolicyConfByName;

            ConfigLogger.getInstance().info(MODULE, this.toString());
            getLogger().info(MODULE,"Read PCRF service configuration completed");

        } catch (Exception e) {
            throw new LoadConfigurationException("Error while reading PCRF service configuration. Reason: " + e.getMessage(), e);
        } finally {
            closeQuietly(session);
        }
    }

    public PccServicePolicyConfiguration getById(int id) {
        return pcrfServicePolicyConfMap.get(id);
    }

    public PccServicePolicyConfiguration getByName(String name) {
        return pcrfServicePolicyConfByNameMap.get(name);
    }

    public Set<String> getPCRFServicePolicyNames() {
        return pcrfServicePolicyConfByNameMap.keySet();
    }

    public void reloadConfiguration() throws LoadConfigurationException {
        getLogger().info(MODULE,"Reload PCRF service configuration started");
        readConfiguration();
        getLogger().info(MODULE,"Reload PCRF service configuration completed");
    }

    @Override
    public String toString() {

        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- PCRF Service Policy Configuration -- ");

        if (pcrfServicePolicyConfMap.isEmpty()) {
            builder.appendValue("No configuration found");
        } else {
            for (Map.Entry<String, PccServicePolicyConfiguration> entry : pcrfServicePolicyConfMap.entrySet()) {
                builder.appendHeading(" -- start of PCRF service policy configuration -- ");
                entry.getValue().toString(builder);
                builder.appendHeading(" -- end of PCRF service policy configuration -- ");
                builder.newline();
            }
        }

        return builder.toString();
    }
}
