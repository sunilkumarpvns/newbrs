package com.elitecore.netvertex.gateway.diameter.conf;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.netvertex.core.conf.impl.DiameterGatewayFactory;
import com.elitecore.netvertex.core.conf.impl.DiameterGatewayProfileFactory;
import com.elitecore.netvertex.core.conf.impl.DiameterPacketMappingFactory;
import com.elitecore.netvertex.core.conf.impl.PCCMappingFactory;
import com.elitecore.netvertex.core.conf.impl.ScriptDataFactory;
import com.elitecore.netvertex.core.conf.impl.ServiceGuideFactory;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMappingFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterMappingFactory;
import com.elitecore.netvertex.core.util.ConfigLogger;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayConfigurationImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class DiameterGatewayConfigurable extends BaseConfigurationImpl {

    private static final String MODULE = "DIA-GW-CNFGRBL";
    private Map<String, DiameterGatewayConfiguration> diameterGatewayConfMap;
    private Map<String, DiameterGatewayConfiguration> diameterGatewayConfHostIdentityMap;
    private Map<String, DiameterGatewayConfiguration> diameterGatewayConfByNameMap;
    private Map<String, DiameterGatewayConfiguration> diameterGatewayConfByConnUrlMap;
    private final SessionFactory sessionFactory;
    private DiameterGatewayFactory diameterGatewayFactory;
    private DiameterPacketMappingFactory diameterPacketMappingFactory;
    private List<DiameterGatewayData> diameterGWDatas;

    public DiameterGatewayConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
        super(serverContext);
        this.sessionFactory = sessionFactory;
        diameterGatewayConfMap = new HashMap<>();
        diameterGatewayConfHostIdentityMap = new HashMap<>();
        diameterGatewayConfByNameMap = new HashMap<>();
        diameterGatewayConfByConnUrlMap = new HashMap<>();
        PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory = new PCRFToDiameterMappingFactory();
        DiameterToPCCMappingFactory diameterToPCCMappingFactory = new DiameterToPCCMappingFactory();
        diameterPacketMappingFactory = new DiameterPacketMappingFactory(pcrfToDiameterMappingFactory, diameterToPCCMappingFactory);
        DiameterGatewayProfileFactory diameterGatewayProfileFactory = new DiameterGatewayProfileFactory(new PCCMappingFactory(pcrfToDiameterMappingFactory, Compiler.getDefaultCompiler())
                , diameterPacketMappingFactory, new ScriptDataFactory(), new ServiceGuideFactory());
        this.diameterGatewayFactory = new DiameterGatewayFactory(diameterGatewayProfileFactory);
    }

    public void init() throws InitializationFailedException {
        getLogger().info(MODULE, "Initialization of Diameter gateway configuration started");

        Map<String, DiameterGatewayConfiguration> tempDiameterGatewayConfMap = new HashMap<>(diameterGatewayConfMap);
        Map<String, DiameterGatewayConfiguration> tempDiameterGatewayHostIdentityMap = new HashMap<>(diameterGatewayConfHostIdentityMap);
        Map<String, DiameterGatewayConfiguration> tempDiameterGatewayConfByName = new HashMap<>(diameterGatewayConfByNameMap);
        Map<String, DiameterGatewayConfiguration> tempDiameterGatewayConfConnUrlMap = new HashMap<>(diameterGatewayConfByConnUrlMap);


        for (DiameterGatewayData diameterGatewayData : diameterGWDatas) {

            String gatewayId = diameterGatewayData.getId();
            DiameterGatewayConfigurationImpl diameterGatewayConf = null;
            try {
                diameterGatewayConf = diameterGatewayFactory.create(diameterGatewayData);
            } catch (LoadConfigurationException e) {
                throw new InitializationFailedException("Error while reading diameter gateway configuration. Reason: " + e.getMessage(), e);
            }

            tempDiameterGatewayConfMap.put(gatewayId, diameterGatewayConf);

            if(Strings.isNullOrBlank(diameterGatewayData.getHostIdentity()) == false){
                tempDiameterGatewayHostIdentityMap.put(diameterGatewayConf.getHostIdentity(), diameterGatewayConf);
            }

            if(Strings.isNullOrBlank(diameterGatewayConf.getHostIPAddress()) == false){
                tempDiameterGatewayConfConnUrlMap.put(diameterGatewayConf.getHostIPAddress(), diameterGatewayConf);
            }

            tempDiameterGatewayConfByName.put(diameterGatewayConf.getName(), diameterGatewayConf);
        }

        this.diameterGatewayConfByConnUrlMap = tempDiameterGatewayConfConnUrlMap;
        this.diameterGatewayConfMap = tempDiameterGatewayConfMap;
        this.diameterGatewayConfHostIdentityMap = tempDiameterGatewayHostIdentityMap;
        this.diameterGatewayConfByNameMap = tempDiameterGatewayConfByName;

        ConfigLogger.getInstance().info(MODULE, this.toString());
        getLogger().info(MODULE, "Initialization of Diameter gateway configuration completed");
    }

    @Override
    public void readConfiguration() throws LoadConfigurationException {
        getLogger().info(MODULE, "Read Diameter gateway configuration started");

        Session session = null;
        try {

            session = sessionFactory.openSession();
            this.diameterGWDatas = HibernateReader.readAll(DiameterGatewayData.class, session);

            getLogger().info(MODULE,"Read Diameter gateway configuration completed");

        } catch (Exception e) {
            throw new LoadConfigurationException("Error while reading diameter gateway configuration. Reason: " + e.getMessage(), e);
        } finally {
            closeQuietly(session);
        }

    }

    public DiameterGatewayConfiguration getById(int id) {
        return diameterGatewayConfMap.get(id);
    }

    public DiameterGatewayConfiguration getByHostIdentity(String hostIdentity) {
        return diameterGatewayConfHostIdentityMap.get(hostIdentity);
    }

    public DiameterGatewayConfiguration getByConnectionUrl(String connUrl) {
        return diameterGatewayConfByConnUrlMap.get(connUrl);
    }

    public DiameterGatewayConfiguration getByName(String name) {
        return diameterGatewayConfByNameMap.get(name);
    }

    public Set<String> getGatewayNames() {
        return diameterGatewayConfByNameMap.keySet();
    }

    public Collection<DiameterGatewayConfiguration> getAllGatewayConfiguration() {
        return diameterGatewayConfMap.values();
    }

    public void reloadConfiguration() throws LoadConfigurationException, InitializationFailedException {
        getLogger().info(MODULE,"Reload Diameter gateway configuration completed");
        readConfiguration();
        init();
        getLogger().info(MODULE,"Reload Diameter gateway configuration completed");
    }

    @Override
    public String toString() {

        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- Diameter Gateway Configuration -- ");

        if (diameterGatewayConfMap.isEmpty()) {
            builder.appendValue("No configuration found");
        } else {
            for (Map.Entry<String, DiameterGatewayConfiguration> entry : diameterGatewayConfMap.entrySet()) {
                builder.appendHeading(" -- start of gateway configuration -- ");
                entry.getValue().toString(builder);
                builder.appendHeading(" -- end of gateway configuration -- ");
                builder.newline();
            }
        }

        return builder.toString();
    }

    public PCCToDiameterMapping getPCCToDiameterMappings(String gatewayName, String packetType) {
        DiameterGatewayConfiguration gatewayConfiguration = diameterGatewayConfByNameMap.get(gatewayName);
        PCCToDiameterMapping pccToDiameterMapping = null;

        if (gatewayConfiguration != null) {
            pccToDiameterMapping = gatewayConfiguration.getPCCToDiameterPacketMappings().get(ApplicationPacketType.valueOf(packetType));
        }
        return pccToDiameterMapping;
    }

    public DiameterToPCCMapping getDiameterToPCCMappings(String gatewayName, String packetType) {
        DiameterGatewayConfiguration gatewayConfiguration = diameterGatewayConfByNameMap.get(gatewayName);
        DiameterToPCCMapping diameterToPCCMapping = null;

        if (gatewayConfiguration != null) {
            diameterToPCCMapping = gatewayConfiguration.getDiameterToPCCPacketMappings().get(ApplicationPacketType.valueOf(packetType));
        }
        return diameterToPCCMapping;
    }
}
