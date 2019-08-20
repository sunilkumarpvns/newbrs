package com.elitecore.netvertex.gateway.radius.conf.impl;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayData;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.netvertex.core.conf.impl.*;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.util.ConfigLogger;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.mapping.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.*;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class RadiusGatewayConfigurable extends BaseConfigurationImpl{
    private static final String MODULE = "RAD-GW-CNFGRBL";
    private Map<String, RadiusGatewayConfiguration> radiusGatewayConfMap;
    private Map<String, RadiusGatewayConfiguration> radiusGatewayConfByNameMap;
    private Map<String, RadiusGatewayConfiguration> radiusGatewayConfByIpAddressMap;
    private final SessionFactory sessionFactory;
    private RadiusGatewayFactory radiusGatewayFactory;

    public RadiusGatewayConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
        super(serverContext);
        this.sessionFactory = sessionFactory;
        radiusGatewayConfMap = new HashMap<>();
        radiusGatewayConfByNameMap = new HashMap<>();
        radiusGatewayConfByIpAddressMap = new HashMap<>();
        PCCToRadiusMappingFactory pccToRadiusMappingFactory = new PCCToRadiusMappingFactory();
        RadiusToPCCMappingFactory radiusToPCCMappingFactory = new RadiusToPCCMappingFactory();
        RadiusGatewayProfileFactory radiusGatewayProfileFactory = new RadiusGatewayProfileFactory(
                new RadiusPCCRuleMappingFactory(pccToRadiusMappingFactory, Compiler.getDefaultCompiler()),
                new RadiusPacketMappingFactory(pccToRadiusMappingFactory, radiusToPCCMappingFactory), new ScriptDataFactory(), new ServiceGuideFactory());
        this.radiusGatewayFactory = new RadiusGatewayFactory(radiusGatewayProfileFactory);
    }

    @Override
    public void readConfiguration() throws LoadConfigurationException {
        getLogger().info(MODULE, "Read Radius gateway configuration started");

        Session session = null;

        try {
            session = sessionFactory.openSession();
            List<RadiusGatewayData> radiusGatewayDatas = HibernateReader.readAll(RadiusGatewayData.class, session);
            Map<String, RadiusGatewayConfiguration> tempRadiusGatewayConfMap = new HashMap<>(radiusGatewayConfMap);
            Map<String, RadiusGatewayConfiguration> tempRadiusGatewayConfByNameMap = new HashMap<>(radiusGatewayConfByNameMap);
            Map<String, RadiusGatewayConfiguration> tempRadiusGatewayConfByIpAddressMap = new HashMap<>(radiusGatewayConfByIpAddressMap);

            for (RadiusGatewayData radiusGatewayData : radiusGatewayDatas) {
                String gatewayId = radiusGatewayData.getId();
                RadiusGatewayConfigurationImpl radiusGatewayConfiguration = radiusGatewayFactory.create(radiusGatewayData);
                tempRadiusGatewayConfMap.put(gatewayId, radiusGatewayConfiguration);
                if (Strings.isNullOrBlank(radiusGatewayConfiguration.getName()) == false) {
                    tempRadiusGatewayConfByNameMap.put(radiusGatewayConfiguration.getName(), radiusGatewayConfiguration);
                }
                if(Strings.isNullOrBlank(radiusGatewayConfiguration.getIPAddress()) == false){
                    tempRadiusGatewayConfByIpAddressMap.put(radiusGatewayConfiguration.getIPAddress(), radiusGatewayConfiguration);
                }
            }
            this.radiusGatewayConfMap = tempRadiusGatewayConfMap;
            this.radiusGatewayConfByNameMap = tempRadiusGatewayConfByNameMap;
            this.radiusGatewayConfByIpAddressMap = tempRadiusGatewayConfByIpAddressMap;

            ConfigLogger.getInstance().info(MODULE, this.toString());
            getLogger().info(MODULE,"Read Radius gateway configuration completed");
        } finally {
            closeQuietly(session);
        }
    }

    public void reloadConfiguration() throws LoadConfigurationException {
        getLogger().info(MODULE,"Reload Radius gateway configuration completed");
        readConfiguration();
        getLogger().info(MODULE,"Reload Radius gateway configuration completed");
    }

    public RadiusGatewayConfiguration getRadiusGatewayConfigurationById(int id) {
        return radiusGatewayConfMap.get(id);
    }

    public RadiusGatewayConfiguration getRadiusGatewayConfigurationByIpAddress(String ipAddress) {
        return radiusGatewayConfByIpAddressMap.get(ipAddress);
    }

    public RadiusGatewayConfiguration getRadiusGatewayConfigurationByName(String name) {
        return radiusGatewayConfByNameMap.get(name);
    }

    public Set<String> getRadiusGatewayNames() {
        return radiusGatewayConfByNameMap.keySet();
    }

    public Collection<RadiusGatewayConfiguration> getRadiusGatewayConfigurations() {
        return radiusGatewayConfMap.values();
    }

    @Override
    public String toString() {

        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- Radius Gateway Configuration -- ");

        if (radiusGatewayConfMap.isEmpty()) {
            builder.appendValue("No configuration found");
        } else {
            for (Map.Entry<String, RadiusGatewayConfiguration> entry : radiusGatewayConfMap.entrySet()) {
                builder.appendHeading(" -- start of gateway configuration -- ");
                entry.getValue().toString(builder);
                builder.appendHeading(" -- end of gateway configuration -- ");
                builder.newline();
            }
        }
        return builder.toString();
    }

    public PCCToRadiusMapping getPCCToRadiusMappings(String gatewayName, String packetType) {
        RadiusGatewayConfiguration gatewayConfiguration = radiusGatewayConfByNameMap.get(gatewayName);
        PCCToRadiusMapping pccToRadiusMapping = null;

        if (gatewayConfiguration != null) {
            pccToRadiusMapping = gatewayConfiguration.getPCCToRADIUSPacketMappings().get(RadApplicationPacketType.valueOf(packetType));
        }
        return pccToRadiusMapping;
    }

    public RadiusToPCCMapping getRadiusToPCCMappings(String gatewayName, String packetType) {
        RadiusGatewayConfiguration gatewayConfiguration = radiusGatewayConfByNameMap.get(gatewayName);
        RadiusToPCCMapping radiusToPCCMapping = null;

        if (gatewayConfiguration != null) {
            radiusToPCCMapping = gatewayConfiguration.getRadiusToPCCPacketMappings().get(RadApplicationPacketType.valueOf(packetType));
        }
        return radiusToPCCMapping;
    }

}
