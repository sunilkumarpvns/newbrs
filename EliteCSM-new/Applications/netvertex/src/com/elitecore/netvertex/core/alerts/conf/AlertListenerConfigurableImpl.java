package com.elitecore.netvertex.core.alerts.conf;

import java.util.ArrayList;
import java.util.List;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.SnmpRequestType;
import com.elitecore.core.serverx.alert.TrapVersion;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.alerts.AlertListenerData;
import com.elitecore.corenetvertex.sm.alerts.AlertListenerRelData;
import com.elitecore.corenetvertex.sm.alerts.AlertTypes;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.stream.Collectors.toList;

public class AlertListenerConfigurableImpl extends BaseConfigurationImpl
        implements AlertListenerConfigurable{

    private static final String MODULE = "ALRT-LSTNR-CNFIGRBL";
    public static final int DEFAULT_SNMP_PORT = 162;
    private final SessionFactory sessionFactory;
    private AlertListenerConfiguration alertListenerConfiguration;


    public AlertListenerConfigurableImpl(ServerContext serverContext, SessionFactory sessionFactory) {
        super(serverContext);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void readConfiguration() throws LoadConfigurationException {
        getLogger().info(MODULE, "Read Alert Listener Configuration started");

        Session session = sessionFactory.openSession();

        try {
            List<AlertListenerData> alertListenerDatas = HibernateReader.readAll(AlertListenerData.class, session);

            List<FileAlertListenerConfiguration> fileAlertListenerConfigurations = new ArrayList<>(2);
            List<TrapAlertListenerConfiguration> trapAlertListenerConfigurations = new ArrayList<>(2);

            for (AlertListenerData alertListenerData : alertListenerDatas) {
                getLogger().info(MODULE, "Read Alert Listener Configuration(" + alertListenerData.getName() + ") started");

                if(AlertTypes.valueOf(alertListenerData.getType()) == AlertTypes.FILE) {
                    fileAlertListenerConfigurations.add(createFileAlertListener(alertListenerData));
                } else {
                    trapAlertListenerConfigurations.add(createTrapAlertListener(alertListenerData));
                }

                getLogger().info(MODULE, "Read Alert Listener Configuration(" + alertListenerData.getName() + ") completed");
            }

            alertListenerConfiguration = new AlertListenerConfiguration(trapAlertListenerConfigurations, fileAlertListenerConfigurations);
            ConfigLogger.getInstance().info(MODULE, alertListenerConfiguration.toString());
            getLogger().info(MODULE, "Read Alert Listener Configuration completed");
        } finally {
            HibernateConfigurationUtil.closeQuietly(session);
        }
    }

    private FileAlertListenerConfiguration createFileAlertListener(AlertListenerData data) {

        List<AlertListenerRelData> relDatas = data.getAlertListenerRelDataList();

        List<AlertsConfiguration> alertConfigurations = createAlertConfigurations(relDatas);

        return new FileAlertListenerConfiguration(
                data.getId()
                , data.getFileName()
                , data.getMaxRollingUnit().intValue()
                , data.getRollingType()
                , data.getRollingUnit().intValue()
                , data.getCompRollingUnit()
                , data.getName()
        , alertConfigurations);

    }

    private List<AlertsConfiguration> createAlertConfigurations(List<AlertListenerRelData> relDatas) {
        return relDatas.stream()
                .filter(relData -> com.elitecore.corenetvertex.core.alerts.Alerts.valueOf(relData.getType()).isParentAlert() == false)  //skipping alert configuration parent entries
                .map(relData -> new AlertsConfiguration(Alerts.fromCoreNetvertexAlert(com.elitecore.corenetvertex.core.alerts.Alerts.valueOf(relData.getType())), relData.isFloodControl()))
                .collect(toList());
    }

    private TrapAlertListenerConfiguration createTrapAlertListener(AlertListenerData data) {

        String serverIp = "127.0.0.1";
        int port = DEFAULT_SNMP_PORT;
        try{
            URLData urlData = URLParser.parse(data.getTrapServer());
            serverIp = urlData.getHost();
            port = urlData.getPort();
            if(port == URLParser.UNKNOWN_PORT){
                getLogger().warn(MODULE, "Using default Trap Alert listener port: " + port
                        + ". Reason: port not configured");
            }else{
                port = DEFAULT_SNMP_PORT;
            }
        }catch(InvalidURLException e){
            getLogger().error(MODULE, "Using default Trap Alert listener hostAddress " + serverIp + ":" + port
                    + ". Reason: Error while parsing URL: " + data.getTrapServer());
            getLogger().trace(MODULE,e);
        }

        SnmpRequestType snmpRequestType = null;
        int timeout = 0;
        int retryCount = 0;

        if (data.getSnmpRequestType() != null) {
            snmpRequestType = SnmpRequestType.valueOf(data.getSnmpRequestType());
        }

        if (data.getTimeOut() != null) {
            timeout = data.getTimeOut().intValue();
        }

        if (data.getRetryCount() != null) {
            retryCount = data.getRetryCount().intValue();
        }

        return new TrapAlertListenerConfiguration(
                data.getId()
                , data.getCommunity()
                , port
                , serverIp
                , TrapVersion.valueOf(data.getTrapVersion())
                , data.getAdvanceTrap()
                , data.getName()
                , snmpRequestType
                , timeout
                , retryCount
                , createAlertConfigurations(data.getAlertListenerRelDataList()));
    }

    @Override
    public AlertListenerConfiguration getAlertListenerConfigurations() {
        return alertListenerConfiguration;
    }
}
