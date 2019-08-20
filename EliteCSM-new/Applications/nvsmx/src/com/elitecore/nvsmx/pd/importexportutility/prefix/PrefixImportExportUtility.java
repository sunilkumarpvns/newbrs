package com.elitecore.nvsmx.pd.importexportutility.prefix;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pd.prefix.PrefixData;
import com.elitecore.corenetvertex.pd.prefix.PrefixDataExt;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.prefix.CSV;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hibernate.criterion.Order;

import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PrefixImportExportUtility {

    private static final String MODULE = "PREFIX-CTRL";
    private List<CountryData> countryList = new ArrayList<>();
    private List<NetworkData> networkList = new ArrayList<>();
    private List<OperatorData> operatorList = new ArrayList<>();
    private List<PrefixDataExt> prefixDataExts = new ArrayList<>();
    private String networkRelations;
    private static final String PREFIX = "prefix";


    public void prepareValuesForSubClass() throws Exception {
        setCountryList(CRUDOperationUtil.findAll(CountryData.class));
        setOperatorList(CRUDOperationUtil.findAll(OperatorData.class));
        setNetworkList(CRUDOperationUtil.findAll(NetworkData.class));
        prepareOperationNetworkRelation();
    }

    public List<CountryData> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryData> countryList) {
        this.countryList = countryList;
    }

    public List<NetworkData> getNetworkList() {
        return networkList;
    }

    public void setNetworkList(List<NetworkData> networkList) {
        this.networkList = networkList;
    }

    public List<OperatorData> getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(List<OperatorData> operatorList) {
        this.operatorList = operatorList;
    }

    public String getNetworkRelations() {
        return networkRelations;
    }

    public void setNetworkRelations(String networkRelations) {
        this.networkRelations = networkRelations;
    }

    private void prepareOperationNetworkRelation() {
        setNetworkBelongingToOperatorJson();
    }

    public void setNetworkBelongingToOperatorJson() {

        List<NetworkData> networkDataList = CRUDOperationUtil.findAllWhichIsNotDeleted(NetworkData.class);
        Map<String, List<Map<String, List<NetworkData>>>> networkDataMap = Maps.newHashMap();

        for (NetworkData networkData : networkDataList) {
            OperatorData operatorData = networkData.getOperatorData();
            String operatorId = operatorData.getId();

            CountryData countryData = networkData.getCountryData();
            String countryId = countryData.getId();

            Map<String, List<NetworkData>> operatorIdToNetworkData = Maps.newHashMap();
            if (operatorIdToNetworkData.containsKey(operatorId)) {
                operatorIdToNetworkData.get(operatorId).add(networkData);
            } else {
                List<NetworkData> networkDataListForOperatorID = new ArrayList<>();
                networkDataListForOperatorID.add(networkData);
                operatorIdToNetworkData.put(operatorId, networkDataListForOperatorID);
            }

            if (networkDataMap.containsKey(countryId)) {
                networkDataMap.get(countryId).add(operatorIdToNetworkData);
            } else {
                List<Map<String, List<NetworkData>>> countryIdToNetworkData = new ArrayList<>();
                countryIdToNetworkData.add(operatorIdToNetworkData);
                networkDataMap.put(countryId, countryIdToNetworkData);
            }

        }
        Gson gson = GsonFactory.defaultInstance();
        this.networkRelations = gson.toJson(networkDataMap);
    }

    public void setPrefixList(List<PrefixDataExt> prefixList) {
        this.prefixDataExts = prefixList;
    }


    public static StringWriter prefixExportData() {
        List<PrefixData> prefixDatas = CRUDOperationUtil.get(PrefixData.class, Order.asc(PREFIX));
        StringWriter stringWriter = new StringWriter();
        stringWriter.write(NVSMXCommonConstants.PREFIX_HEADER);
        for (PrefixData prefixData : prefixDatas) {
            stringWriter.write("\n");
            stringWriter.write(prefixData.getPrefix() + "," + prefixData.getCountryData().getName() + "," + prefixData.getOperatorData().getName() + "," + prefixData.getNetworkData().getName());
        }
        return stringWriter;
    }

    public List<PrefixDataExt> importPrefixExtended(InputStream in) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called importPrefixExtended()");
        }
        try {
            prepareValuesForSubClass();
            setPrefixList(CRUDOperationUtil.findAll(PrefixDataExt.class));
            ArrayList<String> countryDataName = new ArrayList<>();
            ArrayList<String> countryDataId = new ArrayList<>();
            for (CountryData countryData : countryList) {
                countryDataName.add(countryData.getName().toLowerCase());
                countryDataId.add(countryData.getId());
            }

            ArrayList<String> operatorDataName = new ArrayList<>();
            ArrayList<String> operatorDataId = new ArrayList<>();
            for (OperatorData operatorData : operatorList) {
                operatorDataName.add(operatorData.getName().toLowerCase());
                operatorDataId.add(operatorData.getId());
            }

            ArrayList<String> networkDataName = new ArrayList<>();
            ArrayList<String> networkDataId = new ArrayList<>();
            for (NetworkData networkData : networkList) {
                networkDataName.add(networkData.getName().toLowerCase());
                networkDataId.add(networkData.getId());
            }

            ArrayList<String> prefixDataId = new ArrayList<>();
            ArrayList<String> prefixDataName = new ArrayList<>();
            for (PrefixDataExt prefixDataExt : prefixDataExts) {
                prefixDataName.add(prefixDataExt.getPrefix());
                prefixDataId.add(prefixDataExt.getId());
            }
            List<PrefixDataExt> pdDataList = new ArrayList<>();
            CSV csv = new CSV(true, ',', in);
            List<String> fieldNames = null;
            if (csv.hasNext()) fieldNames = new ArrayList<>(csv.next());
            fieldNames.set(0, "name");
            while (csv.hasNext()) {
                List<String> row = csv.next();
                PrefixDataExt prefixData = new PrefixDataExt();

                if (!prefixDataName.contains(row.get(0))) {
                    prefixData.setPrefix(row.get(0));
                } else {
                    prefixData.setId(prefixDataId.get(prefixDataName.indexOf(row.get(0))));
                    prefixData.setPrefix(row.get(0));
                }
                if (!(row.get(1).trim().equals(""))) {
                    if (countryDataName.contains(row.get(1).toLowerCase())) {
                        CountryData countryData = new CountryData();
                        countryData.setId(countryDataId.get(countryDataName.indexOf(row.get(1).toLowerCase())));
                        countryData.setName(row.get(1));
                        prefixData.setCountryData(countryData);
                    } else {
                        CountryData countryData = new CountryData();
                        countryData.setName(row.get(1));
                        prefixData.setCountryData(countryData);
                    }
                }
                if (!(row.get(2).trim().equals(""))) {
                    if (operatorDataName.contains(row.get(2).toLowerCase())) {
                        OperatorData operatorData = new OperatorData();
                        operatorData.setId(operatorDataId.get(operatorDataName.indexOf(row.get(2).toLowerCase())));
                        operatorData.setName(row.get(2));
                        prefixData.setOperatorData(operatorData);
                    } else {
                        OperatorData operatorData = new OperatorData();
                        operatorData.setName(row.get(2));
                        prefixData.setOperatorData(operatorData);
                    }
                }
                if (row.size() > 3 && (!(row.get(3).trim().equals("")))) {
                    if (networkDataName.contains(row.get(3).toLowerCase())) {
                        NetworkData networkData = new NetworkData();
                        networkData.setId(networkDataId.get(networkDataName.indexOf(row.get(3).toLowerCase())));
                        networkData.setName(row.get(3));
                        prefixData.setNetworkData(networkData);
                    } else {
                        NetworkData networkData = new NetworkData();
                        networkData.setName(row.get(3));
                        prefixData.setNetworkData(networkData);
                    }
                }
                pdDataList.add(prefixData);
            }
            return pdDataList;
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Error while importing prefix");
            LogManager.getLogger().trace(MODULE, e);
        }
        return new ArrayList<>();
    }

    public static StringBuilder setErrorData(JsonObject objectPrefix, StringBuilder sb, JsonObject objectRemarks) {
        if (objectPrefix.get(PREFIX) == null) {
            sb.append(",");
        } else {
            sb.append(objectPrefix.get(PREFIX).getAsString() + ",");
        }
        if (objectPrefix.get("countryData") == null) {
            sb.append(",");
        } else {
            sb.append(objectPrefix.get("countryData").getAsJsonObject().get("name").getAsString() + ",");
        }
        if (objectPrefix.get("operatorData") == null) {
            sb.append(",");
        } else {
            sb.append(objectPrefix.get("operatorData").getAsJsonObject().get("name").getAsString() + ",");
        }
        if (objectPrefix.get("networkData") == null) {
            sb.append(",");
        } else {
            sb.append(objectPrefix.get("networkData").getAsJsonObject().get("name").getAsString() + ",");
        }
        for (JsonElement jsonElement : (objectRemarks.get("subReasons").getAsJsonArray())) {
            sb.append(jsonElement + " ");
        }
        return sb;
    }

    public static String mergeValuesAndStatus(JsonArray prefixAllDataJson, JsonArray remarks) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called mergeValuesAndStatus()");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(NVSMXCommonConstants.PREFIX_HEADER + "\n");
        for (int i = 0; i < prefixAllDataJson.size(); i++) {
            JsonObject objectPrefix = prefixAllDataJson.get(i).getAsJsonObject();
            JsonObject objectRemarks = remarks.get(i).getAsJsonObject();
            if (!objectRemarks.get("messages").getAsString().equals("SUCCESS")) {
                PrefixImportExportUtility.setErrorData(objectPrefix, sb, objectRemarks);
                sb.append("\n");
            }

        }
        return sb.toString();
    }

    public static String getTime() {
        return new SimpleDateFormat("YYMMddHHmmss").format(new Date());
    }
}
