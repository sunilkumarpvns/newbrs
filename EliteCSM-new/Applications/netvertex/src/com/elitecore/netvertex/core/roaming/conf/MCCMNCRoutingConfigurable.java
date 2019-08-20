package com.elitecore.netvertex.core.roaming.conf;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
import com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData;
import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableData;
import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableGatewayRelData;
import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingType;
import com.elitecore.corenetvertex.sm.routing.network.BrandData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.core.roaming.*;
import com.elitecore.netvertex.core.roaming.conf.impl.MCCMNCRoutingConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class MCCMNCRoutingConfigurable extends BaseConfigurationImpl {
    private static final String MODULE = "MCCMNC-ROUTINGTABLE";
    private static final String KEY = "MCC-MNC-RT";
    private final SessionFactory sessionFactory;
    private MCCMNCRoutingConfiguration mccmncRoutingConfiguration;

    public MCCMNCRoutingConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
        super(serverContext);
        this.sessionFactory = sessionFactory;
    }

    public void readConfiguration() throws LoadConfigurationException {


        getLogger().info(MODULE, "MCC-MNC Routing Table Configuration Reading Started.");
        Session session = null;
        Map<String, MCCMNCRoutingEntry> tempRoutingEntriesByMCCMNC = new HashMap<>();
        HashMap<String, RoutingEntry> tempRoutingEntriesByName = new HashMap<>();
        List<RoutingEntry> routingEntries = new ArrayList<>();
        List<MCCMNCEntry> mccmncEntries = new ArrayList<>();
        try {
            session = sessionFactory.openSession();

            readRoutingTable(session, mccmncEntries, routingEntries);
            List<RealmBaseRoutingEntry> realmBaseRoutingEntries = new ArrayList<>();
            List<MCCMNCRoutingEntry> mccmncBaseRoutingEntries = new ArrayList<>();

            for (RoutingEntry routingEntry : routingEntries) {
                if (RoutingType.MCC_MNC_BASED == routingEntry.getType()) {
                    MCCMNCRoutingEntry mccmncRoutingEntry = (MCCMNCRoutingEntry) routingEntry;
                    tempRoutingEntriesByMCCMNC = mccmncRoutingEntry
                            .getMCCMNCGroup().getMCCMNCEntities().stream()
                            .collect(Collectors.toMap(Entry::getKey, entry -> mccmncRoutingEntry));
                    mccmncBaseRoutingEntries.add(mccmncRoutingEntry);
                } else {
                    realmBaseRoutingEntries.add((RealmBaseRoutingEntry) routingEntry);
                }
                tempRoutingEntriesByName.put(routingEntry.getName(), routingEntry);

            }

            getLogger().info(MODULE, "MCC-MNC Routing Table Configuration Reading Completed.");
        } finally {
            closeQuietly(session);
        }

        MCCMNCRoutingConfiguration mccmncRoutingConfiguration = new MCCMNCRoutingConfigurationImpl(tempRoutingEntriesByMCCMNC
                , tempRoutingEntriesByName
                , mccmncEntries.stream().collect(Collectors.toMap(MCCMNCEntry::getMCCMNC, Function.identity()))
                , routingEntries);

        this.mccmncRoutingConfiguration = mccmncRoutingConfiguration;
    }

    private void readRoutingTable(Session session, List<MCCMNCEntry> mccmncEntries, List<RoutingEntry> routingEntries) {
        List<RoutingTableGatewayRelData> routingTableGatewayRelDatas = HibernateReader.readAll(RoutingTableGatewayRelData.class, session);
        Map<String, MCCMNCGroup> tempMccMncGroups;
        HashMap<String, MCCMNCEntry> tempMCCMNCEntries = new HashMap<>();

        for (RoutingTableGatewayRelData routingTableGatewayRelData : routingTableGatewayRelDatas) {
            RoutingTableData routingTableData = routingTableGatewayRelData.getRoutingTableData();
            MccMncGroupData mccMncGroupData = routingTableData.getMccMncGroupData();
            BrandData brandData = mccMncGroupData.getBrandData();
            List<NetworkData> networkDataList = mccMncGroupData.getNetworkDatas();
            tempMCCMNCEntries = createMCCMNCEntries(networkDataList);
            String routingId = routingTableData.getId();
            String name = routingTableData.getName();
            String type = routingTableData.getType();
            String expression = routingTableData.getRealmCondition();
            String mccmcnGroupId = mccMncGroupData.getId();
            tempMccMncGroups = createMCCMNCGroup(mccMncGroupData, brandData, tempMCCMNCEntries, networkDataList);
            MCCMNCGroup mccmncGroup = tempMccMncGroups.get(mccmcnGroupId);
            if (RoutingType.MCC_MNC_BASED.name().equalsIgnoreCase(type) && mccmncGroup == null) {
                getLogger().warn(MODULE, "MCCMNCRoutingGroup not found with id = " + mccmcnGroupId + " for RoutingTable ID = " + routingId);
                continue;
            }
            boolean isRoaming = stringToBoolean("roaming enabled", routingTableData.getRoaming(), false);

            String action = routingTableData.getAction();
            RoutingActions routingAction = RoutingActions.fromRoutingActionString(action);

            HashMap<String, Integer> gatewayWeightageMap = new HashMap<String, Integer>(1, 1);

            DiameterGatewayData diameterGatewayData = routingTableGatewayRelData.getDiameterGatewayData();
            String peerName = diameterGatewayData.getHostIdentity();
            DiameterGatewayConfiguration diaGatewayConf = ((NetVertexServerContext) getServerContext()).getServerConfiguration().getDiameterGatewayConfByHostIdentity(peerName);
            if (diaGatewayConf == null) {
                getLogger().error(MODULE, "Fail to add hostIdentity in routing entry. Reason: Diameter gateway configuration not found for " + peerName);
                continue;
            }

            gatewayWeightageMap.put(diaGatewayConf.getName(), routingTableGatewayRelData.getWeightage());

            if (RoutingType.CUSTOM_REALM_BASED.name().equalsIgnoreCase(type)) {
                routingEntries.add(new RealmBaseRoutingEntry(name, expression, routingAction, gatewayWeightageMap, isRoaming));
            } else if (RoutingType.MCC_MNC_BASED.name().equalsIgnoreCase(type)) {
                routingEntries.add(new MCCMNCRoutingEntry(name, mccmncGroup, routingAction, gatewayWeightageMap, isRoaming));
            } else {
                getLogger().warn(MODULE, "Skip adding routing entry with id = " + routingId + ". Reason : Invalid routing type");
            }

        }
        mccmncEntries.addAll(tempMCCMNCEntries.values());
    }

    private HashMap<String, MCCMNCEntry> createMCCMNCEntries(List<NetworkData> networkDatas) {
        HashMap<String, MCCMNCEntry> idToMCCMNCEntries = new HashMap<>();
        for (NetworkData networkData : networkDatas) {
            BrandData brandData = networkData.getBrandData();
            CountryData countryData = networkData.getCountryData();
            OperatorData operatorData = networkData.getOperatorData();
            String mccmncID = networkData.getId();
            String mccmncOperator = operatorData.getName();
            String mccmncNetworkName = networkData.getName();
            String mccmncCountry = countryData.getName();
            String mccmncBrand = brandData.getName();
            String mccmncTechnology = networkData.getTechnology();
            String mcc = String.valueOf(networkData.getMcc());
            String mnc = String.valueOf(networkData.getMnc());
            idToMCCMNCEntries.put(mccmncID, new MCCMNCEntry(mccmncID, mcc, mnc, mccmncOperator, mccmncNetworkName, mccmncCountry, mccmncBrand, mccmncTechnology));
        }
        return idToMCCMNCEntries;
    }

    private Map<String, MCCMNCGroup> createMCCMNCGroup(MccMncGroupData mccMncGroupData, BrandData brandData, HashMap<String, MCCMNCEntry> tempMCCMNCEntries, List<NetworkData> networkDataList) {
        Map<String, MCCMNCEntry> mccmncEntityMap = new HashMap<String, MCCMNCEntry>();
        Map<String, MCCMNCGroup> mccmncGroups = new HashMap<>();
        String mccmncGroupId = mccMncGroupData.getId();
        String mccmncGroupName = mccMncGroupData.getName();
        String brand = brandData.getName();
        String mccmncGroupDesc = mccMncGroupData.getDescription();

        for (NetworkData networkData : networkDataList) {
            String mccmncId = networkData.getId();
            MCCMNCEntry mccmncEntity = tempMCCMNCEntries.get(mccmncId);
            if (mccmncEntity != null) {
                mccmncEntityMap.put(mccmncEntity.getMCCMNC(), mccmncEntity);
            } else {
                getLogger().warn(MODULE, "No MCC-MNC Entity found with id = " + mccmncEntity + " for MCCMNCGroup Name = " + mccmncGroupName + " and ID = " + mccmncGroupId);
            }

        }
        if (mccmncEntityMap.isEmpty() == false) {
            mccmncGroups.put(mccmncGroupId, new MCCMNCGroup(mccmncGroupName, brand, mccmncGroupDesc, mccmncEntityMap));
        }

        return mccmncGroups;
    }


    @Override
    public String getKey() {
        return KEY;
    }

    public void reloadConfiguration() throws LoadConfigurationException {
        getLogger().info(MODULE, "Reload configuration for MCC-MNC Routing Table is started.");
        readConfiguration();
        getLogger().info(MODULE, "Reload configuration for MCC-MNC Routing Table is completed.");
    }

    public MCCMNCRoutingConfiguration getMCCMNCRoutingConfiguration() {
        return mccmncRoutingConfiguration;
    }
}