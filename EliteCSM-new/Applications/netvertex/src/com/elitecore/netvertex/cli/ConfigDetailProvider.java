package com.elitecore.netvertex.cli;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.FileAlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.TrapAlertListenerConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.LocationInformationConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.MccConfiguration;
import com.elitecore.netvertex.core.lrn.data.LRNConfiguration;
import com.elitecore.netvertex.core.roaming.MCCMNCRoutingEntry;
import com.elitecore.netvertex.core.roaming.RoutingEntry;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadApplicationPacketType;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import com.elitecore.netvertex.service.pcrf.TACDetail;
import com.elitecore.netvertex.service.pcrf.prefix.conf.PrefixConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ConfigurationDetailProvider used for displaying configurations on CLI,
 * register other configurations detail provider here
 * <p>
 * Syntax:
 * show config
 *
 * @author chetan.sankhala
 */
public class ConfigDetailProvider extends DetailProvider {

    private static final String KEY = "config";
    private final HashMap<String, DetailProvider> detailProviderMap;
    private NetVertexServerContext netVertexServerContext;

    /*
     * used for test cases
     */
    public ConfigDetailProvider(NetVertexServerContext netVertexServerContext) {
        this.netVertexServerContext = netVertexServerContext;
        this.detailProviderMap = new HashMap<>(8);
    }

    @Override
    public String execute(String[] parameters) {

        if (parameters.length == 0 || isHelpSymbol(parameters[0])) {
            return getHelpMsg();
        }

        KeyToConfMapping keyToConfMapping = KeyToConfMapping.fromKey(parameters[0]);

        if (keyToConfMapping != null) {
            //command: show config misc-params
            if (parameters.length == 1) {
                return keyToConfMapping.getConfigurationToString(netVertexServerContext);
            }

            if (isHelpSymbol(parameters[1])) {
                return keyToConfMapping.getHelpMsg();
            }

            //command: show config dia-gateway <gatewayName>
            return keyToConfMapping.getConfigurationToString(netVertexServerContext, Arrays.copyOfRange(parameters, 1, parameters.length));
        } else if (detailProviderMap.containsKey(parameters[0])) {
            return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters, 1, parameters.length));
        }

        return " Invalid Option: " + parameters[0] + getHelpMsg();
    }

    @Override
    public String getHelpMsg() {
        StringWriter writer = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(writer);
        out.println();

        out.println(StringUtility.fillChar(" Description", 12) + " : " + getDescription());
        out.println(StringUtility.fillChar(" Usage", 12) + " : " + KEY);
        out.println();
        out.println(" Possible Options:");
        out.incrementIndentation();

        for (KeyToConfMapping keyToConfMapping : KeyToConfMapping.values()) {
            out.print(StringUtility.fillChar(keyToConfMapping.key, 30));
            out.append(" : ");
            out.println(keyToConfMapping.getDescription());
        }

        if (detailProviderMap.isEmpty() == false) {
            detailProviderMap.keySet().stream().map(provider -> provider + " : " + detailProviderMap.get(provider).getDescription()).forEach(out::println);
        }

        out.decrementIndentation();
        out.close();
        return writer.toString();
    }

    @Override
    public String getHotkeyHelp() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.print("'" + KEY + "':{'" + HELP_OPTION + "':{},'" + HELP + "':{}");

        for (KeyToConfMapping keyToConfMapping : KeyToConfMapping.values()) {
            out.print(CommonConstants.COMMA + keyToConfMapping.getHotKey(netVertexServerContext));
        }

        for (DetailProvider provider : detailProviderMap.values()) {
            out.print(CommonConstants.COMMA + provider.getHotkeyHelp());
        }
        out.print("}");
        return writer.toString();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public HashMap<String, DetailProvider> getDetailProviderMap() {
        return detailProviderMap;
    }

    @Override
    public String getDescription() {
        return "displays configuration details";
    }


    private enum KeyToConfMapping {
        SESSION_MANAGER("session-manager") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return netVertexServerContext.getServerConfiguration().getSessionManagerConfiguration().toString();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... param) {
                return getConfigurationToString(netVertexServerContext);
            }

            @Override
            public String getHotKey(NetVertexServerContext serverContext) {
                return getDefaultHotKey(key);
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key);
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Session Manager Configuration";
            }
        },
        MISC_PARAMS("misc-params") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return netVertexServerContext.getServerConfiguration().getMiscellaneousParameterConfiguration().toString();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                return getConfigurationToString(netVertexServerContext);
            }

            @Override
            public String getHotKey(NetVertexServerContext serverContext) {
                return getDefaultHotKey(key);
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key);
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Miscellaneous Configuration";
            }
        },
        DIAMETER_GATEWAY("dia-gateway") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide any of below gateway name to view detail: \n" + netVertexServerContext.getServerConfiguration().getDiameterGatewayNames().toString();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                DiameterGatewayConfiguration configuration = netVertexServerContext.getServerConfiguration().getDiameterGatewayConfByName(params[0]);

                if (configuration == null) {
                    return "Diameter gateway configuration not found for name: " + params[0];
                }
                return configuration.toString();
            }

            @Override
            public String getHotKey(NetVertexServerContext serverContext) {
                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{");

                Set<String> diameterGatewayNames = serverContext.getServerConfiguration().getDiameterGatewayNames();
                if (Collectionz.isNullOrEmpty(diameterGatewayNames) == false) {
                    for (String name : serverContext.getServerConfiguration().getDiameterGatewayNames()) {
                        hotKeyBuilder.append("'").append(name).append("'").append(":{},");
                    }
                    hotKeyBuilder.deleteCharAt(hotKeyBuilder.length() - 1);
                }

                hotKeyBuilder.append("}");
                return hotKeyBuilder.toString();
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " [gateway-name]");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Diameter Gateway Configuration";
            }
        },
        DIAMETER_GATEWAY_PACKET_MAPPING("dia-gateway-packet-mapping") {
            private static final String TYPE = "-type";
            private static final String GATEWAY_NAME = "-name";
            private Set<String> diaApplicationPacketTypes = ApplicationPacketType.getDiaApplicationPacketTypes();

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {

                return getHelpMsg() + "\nProvide any of below gateway name to view detail: \n" + netVertexServerContext.getServerConfiguration().getDiameterGatewayNames().toString() +
                        "\n" + "Provide any of below Application Packet Type to view detail for Diameter gateway Packet Mapping: \n" +
                        diaApplicationPacketTypes;
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                String type = null;
                String gatewayName = null;

                if (params.length < 4) {
                    return "Input parameter missing. Expected: 4, Actual: " + params.length + "\n" + getHelpMsg();
                }

                for (int index = 0; index < params.length; index = index + 2) {

                    if (params[index].equals(TYPE)) {
                        type = params[index + 1];
                    } else if (params[index].equals(GATEWAY_NAME)) {
                        gatewayName = params[index + 1];
                    }
                }

                if (type == null || gatewayName == null) {
                    return "Either of gateway name or packet mapping type value is not provided.\n Skipping the display of diameter gateway packet mapping Information.";
                }

                type = type.toUpperCase();

                if (diaApplicationPacketTypes.contains(type) == false) {
                    return "Provided Application Packet type is not supported.\n" + "Provide any of below Application Packet Type to view detail for Diameter gateway" +
                            "Packet Mapping:\n" +
                            diaApplicationPacketTypes;
                }

                if (ApplicationPacketType.valueOf(type).getConversionType() == ConversionType.GATEWAY_TO_PCC) {
                    DiameterToPCCMapping diameterToPCCMappingsByPacketType = netVertexServerContext.getServerConfiguration().
                            getDiameterToPCCMappingsByPacketType(gatewayName, type);

                    if (diameterToPCCMappingsByPacketType != null) {
                        return diameterToPCCMappingsByPacketType.toString();
                    } else {
                        return "Diameter to PCC Packet Mapping Information not found for gateway: " + gatewayName + " for type: " + type + "\n";
                    }

                } else if (ApplicationPacketType.valueOf(type).getConversionType() == ConversionType.PCC_TO_GATEWAY) {
                    PCCToDiameterMapping pccToDiameterMappingsByPacketType = netVertexServerContext.getServerConfiguration().
                            getPCCToDiameterMappingsByPacketType(gatewayName, type);

                    if (pccToDiameterMappingsByPacketType != null) {
                        return pccToDiameterMappingsByPacketType.toString();
                    } else {
                        return "PCC to Diameter Packet Mapping Information not found for gateway: " + gatewayName + " for type: " + type + "\n";
                    }
                }

                return null;
            }

            @Override
            public String getHotKey(NetVertexServerContext netVertexServerContext) {

                StringBuilder appPacketTypeBuilder = new StringBuilder();

                appPacketTypeBuilder.append("'" + TYPE + "':{");

                if (Collectionz.isNullOrEmpty(diaApplicationPacketTypes) == false) {
                    createHotKeyHelp(appPacketTypeBuilder, diaApplicationPacketTypes);
                }

                appPacketTypeBuilder.append("}");

                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{'" + GATEWAY_NAME + "':{");

                Set<String> diameterGatewayNames = netVertexServerContext.getServerConfiguration().getDiameterGatewayNames();

                if (Collectionz.isNullOrEmpty(diameterGatewayNames) == false) {
                    createHotKeyHelpForAppPacketType(hotKeyBuilder, diameterGatewayNames, appPacketTypeBuilder);
                }

                hotKeyBuilder.append("}");

                hotKeyBuilder.append("}");

                return hotKeyBuilder.toString();
            }


            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " " + GATEWAY_NAME + " <dia-gateway-name> " + " "
                        + TYPE + " <packet-type>");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Diameter Gateway Packet Mapping Configuration based on input provided";
            }
        },
        RADIUS_GATEWAY("rad-gateway") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide any of below gateway name to view detail: \n" + netVertexServerContext.getServerConfiguration().getRadiusGatewayNames().toString();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                RadiusGatewayConfiguration configuration = netVertexServerContext.getServerConfiguration().getRadiusGatewayConfigurationByName(params[0]);

                if (configuration == null) {
                    return "Radius gateway configuration not found for name: " + params[0];
                }
                return configuration.toString();
            }

            @Override
            public String getHotKey(NetVertexServerContext netVertexServerContext) {
                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{");

                Set<String> radiusGatewayNames = netVertexServerContext.getServerConfiguration().getRadiusGatewayNames();
                if (Collectionz.isNullOrEmpty(radiusGatewayNames) == false) {
                    for (String name : netVertexServerContext.getServerConfiguration().getRadiusGatewayNames()) {
                        hotKeyBuilder.append("'").append(name).append("'").append(":{},");
                    }
                    hotKeyBuilder.deleteCharAt(hotKeyBuilder.length() - 1);
                }

                hotKeyBuilder.append("}");
                return hotKeyBuilder.toString();
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " [gateway-name]");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Radius Gateway Configuration";
            }
        },
        RADIUS_GATEWAY_PACKET_MAPPING("rad-gateway-packet-mapping") {
            private static final String TYPE = "-type";
            private static final String GATEWAY_NAME = "-name";
            private List<String> radApplicationPacketTypes = RadApplicationPacketType.getRadApplicationPacketTypes();

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {

                return getHelpMsg() + "\nProvide any of below gateway name to view detail: \n" + netVertexServerContext.getServerConfiguration().getRadiusGatewayNames().toString() +
                        "\n" + "Provide any of below Appliction type to view detail for RADIUS gateway Packet Mapping: \n" +
                        radApplicationPacketTypes;
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                String type = null;
                String gatewayName = null;

                if (params.length < 4) {
                    return "Input parameter missing. Expected: 4, Actual: " + params.length + "\n" + getHelpMsg();
                }

                for (int index = 0; index < params.length; index = index + 2) {

                    if (params[index].equals(TYPE)) {
                        type = params[index + 1];
                    } else if (params[index].equals(GATEWAY_NAME)) {
                        gatewayName = params[index + 1];
                    }
                }

                if (type == null || gatewayName == null) {
                    return "Either of gateway name or packet mapping type value is not provided.\n Skipping the display of RADIUS gateway packet mapping Information.";
                }

                type = type.toUpperCase();
                if (RadApplicationPacketType.contains(type) == false) {
                    return "Provided Application Packet type is not supported.\n" + "Provide any of below Application Packet Type to view detail for RADIUS gateway " +
                            "Packet Mapping:\n" +
                            radApplicationPacketTypes;
                }

                if (RadApplicationPacketType.valueOf(type).getConversionType() == ConversionType.GATEWAY_TO_PCC) {
                    RadiusToPCCMapping radiusToPCCMappingsByPacketType = netVertexServerContext.getServerConfiguration().
                            getRadiusToPCCMappingsByPacketType(gatewayName, type);
                    if (radiusToPCCMappingsByPacketType != null) {
                        return radiusToPCCMappingsByPacketType.toString();
                    } else {
                        return "RADIUS to PCC Packet Mapping Information not found for gateway: " + gatewayName + " for type: " + type + "\n";
                    }
                } else if (RadApplicationPacketType.valueOf(type).getConversionType() == ConversionType.PCC_TO_GATEWAY) {
                    PCCToRadiusMapping pccToRadiusMappingsByPacketType = netVertexServerContext.getServerConfiguration().
                            getPCCToRadiusMappingsByPacketType(gatewayName, type);
                    if (pccToRadiusMappingsByPacketType != null) {
                        return pccToRadiusMappingsByPacketType.toString();
                    } else {
                        return "PCC to RADIUS Packet Mapping Information not found for gateway: " + gatewayName + " for type: " + type + "\n";
                    }
                }

                return null;
            }

            @Override
            public String getHotKey(NetVertexServerContext netVertexServerContext) {

                StringBuilder appPacketTypeBuilder = new StringBuilder();

                appPacketTypeBuilder.append("'" + TYPE + "':{");

                if (Collectionz.isNullOrEmpty(radApplicationPacketTypes) == false) {
                    createHotKeyHelp(appPacketTypeBuilder, radApplicationPacketTypes);
                }

                appPacketTypeBuilder.append("}");

                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{'" + GATEWAY_NAME + "':{");
                Set<String> radiusGatewayNames = netVertexServerContext.getServerConfiguration().getRadiusGatewayNames();

                if (Collectionz.isNullOrEmpty(radiusGatewayNames) == false) {
                    createHotKeyHelpForAppPacketType(hotKeyBuilder, radiusGatewayNames, appPacketTypeBuilder);
                }

                hotKeyBuilder.append("}}");
                return hotKeyBuilder.toString();
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " " + GATEWAY_NAME + " <rad-gateway-name> " + " "
                        + TYPE + " <packet-type>");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View RADIUS Gateway Packet Mapping Configuration based on input provided";
            }
        },

        DDF("ddf") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return netVertexServerContext.getServerConfiguration().getDDFTableData().toString();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... param1) {
                return getConfigurationToString(netVertexServerContext);
            }

            @Override
            public String getHotKey(NetVertexServerContext serverContext) {
                return getDefaultHotKey(key);
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key);
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "Display DDF Configuration";
            }
        },
        NETWORK("network") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide MCC Code to view Network Information.";
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... param1) {

                MccConfiguration mccConfiguration = netVertexServerContext.getLocationRepository().getMCCConfigurationByMCCCode(param1[0]);
                if (mccConfiguration == null) {
                    return "Network Information is not found for provided MCC Code: " + param1[0];
                }
                return mccConfiguration.toString();
            }

            @Override
            public String getHotKey(NetVertexServerContext serverContext) {
                return getDefaultHotKey(key);
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " <mcc-code> ");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Network Configuration by MCC Code";
            }
        },
        LOCATION("location") {
            private static final String KEY_MCC = "-mcc";
            private static final String KEY_MNC = "-mnc";
            private static final String KEY_LAC = "-lac";
            private static final String KEY_CI = "-ci";
            private static final String KEY_SAC = "-sac";
            private static final String KEY_RAC = "-rac";

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide required parameters to view Location Information.\n" + getHelpMsg();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                if (params.length < 8) {
                    return "Input parameter missing. Expected: 8, Actual: " + params.length;
                }

                String mcc = null;
                String mnc = null;
                String lac = null;
                String ci = null;
                String sac = null;
                String rac = null;

                for (int index = 0; index < params.length; index = index + 2) {
                    switch (params[index]) {
                        case KEY_MCC:
                            mcc = params[index + 1];
                            break;

                        case KEY_MNC:
                            mnc = params[index + 1];
                            break;

                        case KEY_LAC:
                            lac = params[index + 1];
                            break;

                        case KEY_CI:
                            ci = params[index + 1];
                            break;

                        case KEY_RAC:
                            rac = params[index + 1];
                            break;

                        case KEY_SAC:
                            sac = params[index + 1];
                            break;
                    }
                }

                if (mcc == null || mnc == null || lac == null) {
                    return "Either of MCC, MNC or LAC value is not provided. Skipping the display of Location Information.";
                }

                if (ci != null) {
                    LocationInformationConfiguration locationInformation = netVertexServerContext.getLocationRepository().getLocationInformationByCGI(mcc, mnc, lac, ci);
                    if (locationInformation != null) {
                        return locationInformation.toString();
                    } else {
                        return "Location Information is not found for provided MCC Code: " + mcc + ", MNC Code: " + mnc + ", LAC Code: " + lac + ", CGI Code: " + ci;
                    }
                } else if (rac != null) {
                    LocationInformationConfiguration locationInformation = netVertexServerContext.getLocationRepository().getLocationInformationByRAI(mcc, mnc, lac, rac);
                    if (locationInformation != null) {
                        return locationInformation.toString();
                    } else {
                        return "Location Information is not found for provided MCC Code: " + mcc + ", MNC Code: " + mnc + ", LAC Code: " + lac + ", RAC Code: " + rac;
                    }
                } else if (sac != null) {
                    LocationInformationConfiguration locationInformation = netVertexServerContext.getLocationRepository().getLocationInformationBySAI(mcc, mnc, lac, sac);
                    if (locationInformation != null) {
                        return locationInformation.toString();
                    } else {
                        return "Location Information is not found for provided MCC Code: " + mcc + ", MNC Code: " + mnc + ", LAC Code: " + lac + ", SAC Code: " + sac;
                    }
                } else {
                    return "Either of CI, RAC or SAC value is not provided. Skipping the display of Location Information.";
                }
            }

            @Override
            public String getHotKey(NetVertexServerContext netVertexServerContext) {
                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{");

                Set<String> locationConfigurationParams = new HashSet<>();
                locationConfigurationParams.addAll(Arrays.asList(KEY_MCC, KEY_MNC, KEY_LAC, KEY_CI, KEY_RAC, KEY_SAC));
                for (String param : locationConfigurationParams) {
                    hotKeyBuilder.append("'").append(param).append("'").append(":{},");
                }

                hotKeyBuilder.deleteCharAt(hotKeyBuilder.length() - 1);

                hotKeyBuilder.append("}");
                return hotKeyBuilder.toString();
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " " + KEY_MCC + " <mcc-code> "
                        + KEY_MNC + " <mnc-code> " + KEY_LAC + " <lac-code> ["
                        + KEY_CI + " <ci-value>] [" + KEY_RAC + " <rac-value>] ["
                        + KEY_SAC + " <sac-value>]");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Location Configuration based on input provided";
            }
        },
        LOCATION_BY_AREANAME("location-by-areaname") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide Area Name to view Location Information by using Area Name.";
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... param1) {
                LocationInformationConfiguration locationByAreaName = netVertexServerContext.getLocationRepository().getLocationInformationByAreaName(param1[0]);
                if (locationByAreaName != null) {
                    return locationByAreaName.toString();
                } else {
                    return "Location Information is not found for provided Area Name: " + param1[0];
                }
            }

            @Override
            public String getHotKey(NetVertexServerContext netVertexServerContext) {
                return KeyToConfMapping.getDefaultHotKey(key);
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " <areaname>");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Location Configuration by Area Name";
            }

        },

        ALERTS("alert") {
            @Override
            public String getConfigurationToString(NetVertexServerContext context) {
                return context.getServerConfiguration().getAlertListenersConfiguration().toString();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext context, String... param1) {

                AlertListenerConfiguration conf = context.getServerConfiguration().getAlertListenersConfiguration();

                FileAlertListenerConfiguration configuration = conf.getFileAlertListenerConfigurations().
                        stream().
                        filter(listener -> listener.getName().equalsIgnoreCase(param1[0]))
                        .findFirst().orElse(null);

                if (configuration != null) {
                    return configuration.toString();
                }

                TrapAlertListenerConfiguration trapConfiguration = conf.getTrapAlertListenerConfigurations().
                        stream().
                        filter(listener -> listener.getName().equalsIgnoreCase(param1[0]))
                        .findFirst().orElse(null);

                if (trapConfiguration != null) {
                    return trapConfiguration.toString();
                }

                return "Alert configuration not found for name: " + param1[0];
            }

            @Override
            public String getHotKey(NetVertexServerContext context) {

                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{");

                AlertListenerConfiguration alertListenersConfiguration = context.getServerConfiguration().getAlertListenersConfiguration();

                List<String> names = new ArrayList<>();

                alertListenersConfiguration.getFileAlertListenerConfigurations()
                        .stream()
                        .map(FileAlertListenerConfiguration::getName)
                        .collect(Collectors.toCollection(() -> names));

                alertListenersConfiguration.getTrapAlertListenerConfigurations()
                        .stream()
                        .map(TrapAlertListenerConfiguration::getName)
                        .collect(Collectors.toCollection(() -> names));

                createHotKeyHelp(hotKeyBuilder, names);

                hotKeyBuilder.append(CommonConstants.CLOSING_BRACES);

                return hotKeyBuilder.toString();
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " [alert-configuration-name]");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Alert Listener Configuration";
            }


        },
        TAC("device-by-tac") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide TAC to view Device configuration.";
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                TACDetail detail = netVertexServerContext.getDeviceManager().getTACDetail(params[0]);
                if (detail != null) {
                    return detail.toString();
                } else {
                    return "Device configuration is not found for provided TAC: " + params[0];
                }
            }

            @Override
            public String getHotKey(NetVertexServerContext netVertexServerContext) {
                return KeyToConfMapping.getDefaultHotKey(key);
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " <tac> ");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Device configuration detail by TAC";
            }
        },
        ROUTINGTABLE_BY_MCCMNC("routingtable-by-mccmnc") {

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide MCCMNC to view Routing Table configuration.";
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {

                MCCMNCRoutingConfiguration mccmncRoutingConfiguration = netVertexServerContext.getServerConfiguration().getMCCMNCRoutingConfiguration();

                MCCMNCRoutingEntry routingEntry = null;

                if (Objects.nonNull(mccmncRoutingConfiguration)) {
                    routingEntry = mccmncRoutingConfiguration.getRoutingEntryByMCCMNC(params[0]);
                }

                if ( routingEntry != null) {
                    return routingEntry.toString();
                } else {
                    return "Routing Table configuration is not found for provided MCCMNC: " + params[0];
                }

            }

            @Override
            public String getHotKey(NetVertexServerContext serverContext) {
                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{");

                MCCMNCRoutingConfiguration mccmncRoutingConfiguration = serverContext.getServerConfiguration().getMCCMNCRoutingConfiguration();
                Collection<String> mccMncs = null;

                if (Objects.nonNull(mccmncRoutingConfiguration)) {
                    mccMncs = mccmncRoutingConfiguration.getMccMncs();
                }

                if (Collectionz.isNullOrEmpty(mccMncs) == false) {
                    for (String name : mccMncs) {
                        hotKeyBuilder.append("'").append(name).append("'").append(":{},");
                    }
                    hotKeyBuilder.deleteCharAt(hotKeyBuilder.length() - 1);
                }

                hotKeyBuilder.append("}");
                return hotKeyBuilder.toString();
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " <mccmnc> ");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Routing Table configuration by MCCMNC";
            }
        },
        ROUTINGTABLE_BY_NAME("routingtable-by-name") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide name to view Routing tabel configuration.";
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                MCCMNCRoutingConfiguration mccmncRoutingConfiguration = netVertexServerContext.getServerConfiguration().getMCCMNCRoutingConfiguration();

                RoutingEntry routingEntry = null;

                if (Objects.nonNull(mccmncRoutingConfiguration)) {
                    routingEntry = mccmncRoutingConfiguration.getRoutingEntryByName(params[0]);
                }

                if ( routingEntry != null) {
                    return routingEntry.toString();
                } else {
                    return "Routing Table configuration is not found for provided name: " + params[0];
                }

            }

            @Override
            public String getHotKey(NetVertexServerContext serverContext) {
                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{");

                MCCMNCRoutingConfiguration mccmncRoutingConfiguration = serverContext.getServerConfiguration().getMCCMNCRoutingConfiguration();

                Collection<String> routingEntryNames = null;

                if (Objects.nonNull(mccmncRoutingConfiguration)) {
                    routingEntryNames = mccmncRoutingConfiguration.getRoutingEntryNames();
                }

                if (Collectionz.isNullOrEmpty(routingEntryNames) == false) {
                    for (String name : routingEntryNames) {
                        hotKeyBuilder.append("'").append(name).append("'").append(":{},");
                    }
                    hotKeyBuilder.deleteCharAt(hotKeyBuilder.length() - 1);
                }

                hotKeyBuilder.append("}");
                return hotKeyBuilder.toString();
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " <name> ");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Routing Table configuration by name";
            }
        }, SERVICE_POLICY("service-policy") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide any of below PCC service policy name to view detail: \n" + netVertexServerContext.getServerConfiguration().getPCRFServicePolicyNames().toString();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                PccServicePolicyConfiguration configuration = netVertexServerContext.getServerConfiguration().getPCRFServicePolicyConfByName(params[0]);

                if (configuration == null) {
                    return "PCC service policy configuration not found for name: " + params[0];
                }
                return configuration.toString();
            }

            @Override
            public String getHotKey(NetVertexServerContext serverContext) {
                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{");

                Set<String> pcrfServicePolicyNames = serverContext.getServerConfiguration().getPCRFServicePolicyNames();
                if (Collectionz.isNullOrEmpty(pcrfServicePolicyNames) == false) {
                    createHotKeyHelp(hotKeyBuilder, pcrfServicePolicyNames);
                }

                hotKeyBuilder.append("}");
                return hotKeyBuilder.toString();
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " [service-policy-name]");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View PCC Service Policy Configuration";
            }
        }, SERVER_GROUP("server-group") {
            @Override
            public String getConfigurationToString(NetVertexServerContext context) {
                return context.getServerConfiguration().getNetvertexServerGroupConfiguration().toString();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext context, String... param1) {
                return getConfigurationToString(context);
            }

            @Override
            public String getHotKey(NetVertexServerContext context) {
                return "'" + key + "':{}";
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key);
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View Server Group Configuration";
            }
        },
        SYSTEM_PARAMETER("system-parameter") {
            @Override
            public String getConfigurationToString(NetVertexServerContext context) {
                return context.getServerConfiguration().getSystemParameterConfiguration().toString();
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext context, String... params) {
                return getConfigurationToString(context);
            }

            @Override
            public String getHotKey(NetVertexServerContext netVertexServerContext) {
                return "'" + key + "':{}";
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key);
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View System Parameter Configuration";
            }
        }, PREFIX("prefix") {
            private static final String ANY_MATCH = "-anymatch";
            private static final String BEST_MATCH = "-bestmatch";

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "View prefix configuration information.";
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {
                if (params.length < 2) {
                    return "Input parameter missing. Expected: 2, Actual: " + params.length;
                }

                if (params.length > 2) {
                    return "Number of Input parameters exceeded. Expected: 2, Actual: " + params.length;
                }

                if (params[0].equals(BEST_MATCH)) {
                    PrefixConfiguration prefixConfiguration = netVertexServerContext.getPrefixRepository().getBestMatch(params[1]);

                    if (prefixConfiguration != null) {
                        return prefixConfiguration.toString();
                    } else {
                        return "Best match prefix configuration is not found for provided MSISDN: " + params[1];
                    }
                } else if (params[0].equals(ANY_MATCH)) {
                    List<PrefixConfiguration> prefixConfigurations = netVertexServerContext.getPrefixRepository().getAnyMatch(params[1]);

                    if (prefixConfigurations.isEmpty() == false) {
                        IndentingToStringBuilder stringBuilder = new IndentingToStringBuilder();
                        for (PrefixConfiguration prefixConfiguration : prefixConfigurations) {
                            stringBuilder.appendValue(prefixConfiguration);
                        }
                        return stringBuilder.toString();
                    } else {
                        return "Any match prefix configurations are not found for provided MSISDN: " + params[1];
                    }
                }

                return null;
            }

            @Override
            public String getHotKey(NetVertexServerContext netVertexServerContext) {
                StringBuilder hotKeyBuilder = new StringBuilder("'" + key + "':{");

                Set<String> prefixConfigurationParams = new HashSet<>();
                prefixConfigurationParams.addAll(Arrays.asList(ANY_MATCH, BEST_MATCH));

                for (String param : prefixConfigurationParams) {
                    hotKeyBuilder.append("'").append(param).append("'").append(":{},");
                }

                hotKeyBuilder.deleteCharAt(hotKeyBuilder.length() - 1);

                hotKeyBuilder.append("}");

                return hotKeyBuilder.toString();
            }

            @Override
            public String getHelpMsg() {

                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " " + ANY_MATCH + " <prefix> ");
                builder.incrementIndentation();
                builder.appendValue(" -- OR --");
                builder.decrementIndentation();
                builder.append("Usage", key + " " + BEST_MATCH + " <prefix> ");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.appendValue("View prefix configuration detail.");
                builder.incrementIndentation();
                builder.incrementIndentation();
                builder.incrementIndentation();
                builder.appendValue("Provide option " + ANY_MATCH + " to view all the possible prefix configuration details.");
                builder.appendValue("Provide option " + BEST_MATCH + " to view longest matching prefix configuration detail.");
                return builder.toString();

            }
        },LRN("lrn") {
            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext) {
                return "Provide LRN to view LRN configuration information.";
            }

            @Override
            public String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params) {

                LRNConfiguration lrnConfiguration = netVertexServerContext.getLRNConfigurationRepository().getLRNConfigurationByLRN(params[0]);

                if (lrnConfiguration != null) {
                    return lrnConfiguration.toString();
                } else {
                    return "LRN configuration is not found for provided LRN: " + params[0];
                }
            }

            @Override
            public String getHotKey(NetVertexServerContext netVertexServerContext) {
                 return getDefaultHotKey(key);
            }

            @Override
            public String getHelpMsg() {
                IndentingToStringBuilder builder = new IndentingToStringBuilder();
                builder.append("Usage", key + " <LRN> ");
                builder.append("Description", getDescription());
                return builder.toString();
            }

            @Override
            public String getDescription() {
                return "View LRN configuration detail by LRN";
            }
        };

        protected String key;
        private static Map<String, KeyToConfMapping> keyToEnum;

        static {
            keyToEnum = new HashMap<>();
            for (KeyToConfMapping keyToConfMapping : values()) {
                keyToEnum.put(keyToConfMapping.key.toLowerCase(), keyToConfMapping);
            }
        }

        KeyToConfMapping(String key) {
            this.key = key;
        }



        private static String getDefaultHotKey(String key) {
            return "'" + key + "':{}";
        }

        public abstract String getConfigurationToString(NetVertexServerContext netVertexServerContext);

        public abstract String getConfigurationToString(NetVertexServerContext netVertexServerContext, String... params);

        public static KeyToConfMapping fromKey(String key) {
            return keyToEnum.get(key.toLowerCase());
        }

        protected void createHotKeyHelp(StringBuilder hotKeyBuilder, Collection<String> names) {
            if(names.isEmpty() == false) {
                hotKeyBuilder.append(names.stream().sorted().collect(Collectors.joining("':{},'", "'", "':{}")));
            }
        }

        protected void createHotKeyHelpForAppPacketType(StringBuilder hotKeyBuilder, Collection<String> names, StringBuilder appPacketTypeBuilder) {
            if(names.isEmpty() == false) {

                hotKeyBuilder.append("'");

                for (String name : names) {

                    hotKeyBuilder.append(name + "':{");

                    hotKeyBuilder.append(appPacketTypeBuilder);

                    hotKeyBuilder.append("}");
                    hotKeyBuilder.append(",'");
                }
                hotKeyBuilder.delete(hotKeyBuilder.length()-2, hotKeyBuilder.length());
            }
        }

        public abstract String getHotKey(NetVertexServerContext netVertexServerContext);

        public abstract String getHelpMsg();

        public abstract String getDescription();
    }
}
