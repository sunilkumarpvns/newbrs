package com.elitecore.netvertex.core.locationmanagement;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.location.area.AreaData;
import com.elitecore.corenetvertex.sm.location.area.LacInformationData;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.geography.GeographyData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.netvertex.core.locationmanagement.data.CGIConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.Country;
import com.elitecore.netvertex.core.locationmanagement.data.LacConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.LocationInformationConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.MccConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.RAIConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.SAIConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.corenetvertex.pm.HibernateConfigurationUtil.closeQuietly;

public class LocationConfigurable {
    private static final String MODULE = "LOCN-MGR";
    private Map<Integer, MccConfiguration> countryInformationMap;
    private Map<String, NetworkConfiguration> networkInfoByMccMnc;
    private Map<String,NetworkConfiguration> networkInfoById;
    private Map<String, LocationInformationConfiguration> locationsByAreaName;
    private final SessionFactory sessionFactory;

    public LocationConfigurable(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.countryInformationMap = new HashMap<>();
        this.locationsByAreaName = new HashMap<>();
        this.networkInfoById = new HashMap<>();
    }

    public void readConfiguration() {
        List<AreaData> areaDatas;
        List<NetworkData> networkDatas;
        List<CountryData> countryDatas;
        List<GeographyData> geographyDatas;
        Map<String,Country> countryInfoByCode = new HashMap<>();
        Session session = null;
        Map<Integer, MccConfiguration> countryInformationMap;
        Map<String, LocationInformationConfiguration> locationsByAreaName;
        try {
            session = sessionFactory.openSession();
            countryDatas = HibernateReader.readAll(CountryData.class,session);
            if(Collectionz.isNullOrEmpty(countryDatas) == false){
                for(CountryData countryData : countryDatas){
                    Country country = new Country(countryData.getId(),countryData.getName(),countryData.getCode());
                    countryInfoByCode.put(country.getCode(),country);
                }
            }
            geographyDatas = HibernateReader.readAll(GeographyData.class,session);
            if(Collectionz.isNullOrEmpty(geographyDatas) == false){
                geographyDatas.forEach(geographyData -> {
                    List<CountryData> geographyWiseCountry = geographyData.getGeographyCountryRelDatas();
                    if(Collectionz.isNullOrEmpty(geographyWiseCountry) == false){
                        geographyWiseCountry.forEach(countryData -> {
                            Country country = countryInfoByCode.get(countryData.getCode());
                            if(country != null){
                                country.addGeography(geographyData.getName());
                            }
                        });
                    }
                });
            }
            areaDatas = HibernateReader.readAll(AreaData.class, session);
            networkDatas = HibernateReader.readAll(NetworkData.class, session);

            countryInformationMap = readNetworkInformation(networkDatas, countryInfoByCode);
            locationsByAreaName = readLocationInformation(areaDatas, countryInformationMap,countryInfoByCode);
        } finally {
            closeQuietly(session);
        }

        this.countryInformationMap = countryInformationMap;
        this.locationsByAreaName = locationsByAreaName;
        this.networkInfoByMccMnc = new HashMap<String, NetworkConfiguration>();
        this.networkInfoById = new HashMap<>();

        for(MccConfiguration mccConfiguration : countryInformationMap.values()) {
            for (NetworkConfiguration networkConfiguration : mccConfiguration.getAllNetworkInfos()) {
                String mccmnc = String.valueOf(mccConfiguration.getMccCode()) + String.valueOf(networkConfiguration.getMNC());

                if(networkConfiguration.getMNC() < 10) {
                    String mccmncIn5Digit = String.valueOf(mccConfiguration.getMccCode()) + "0" + networkConfiguration.getMNC();
                    String mccmncIn6Digit = String.valueOf(mccConfiguration.getMccCode()) + "00" + networkConfiguration.getMNC();
                    networkInfoByMccMnc.put(mccmncIn5Digit, networkConfiguration);
                    networkInfoByMccMnc.put(mccmncIn6Digit, networkConfiguration);
                } else if(networkConfiguration.getMNC() < 100) {
                    String mccmncIn6Digit = String.valueOf(mccConfiguration.getMccCode()) + "0" + networkConfiguration.getMNC();
                    networkInfoByMccMnc.put(mccmncIn6Digit, networkConfiguration);
                }
                networkInfoByMccMnc.put(mccmnc, networkConfiguration);
                networkInfoById.put(networkConfiguration.getNetworkId(),networkConfiguration);
            }
        }

    }

    private Map<Integer, MccConfiguration> readNetworkInformation(List<NetworkData> networkDatas, Map<String,Country> countryByCode) {
        LogManager.getLogger().info(MODULE, "Reading Network Configuration Started");
        Map<Integer, MccConfiguration> tempCountryInformationMap = new HashMap<>();

        for (NetworkData networkData : networkDatas) {
            MccConfiguration mccConfiguration = tempCountryInformationMap.get(networkData.getMcc());
            if (mccConfiguration == null) {
                mccConfiguration = createMCC(networkData,countryByCode);
                tempCountryInformationMap.put(networkData.getMcc(), mccConfiguration);
            }

            String networkId = networkData.getId();
            String brandName = networkData.getBrandData().getName();
            String operatorName = networkData.getOperatorData().getName();
            String networkName = networkData.getName();
            String technology = networkData.getTechnology();
            int mncCode = networkData.getMnc();

            NetworkConfiguration networkConfiguration = new NetworkConfiguration(networkId, mncCode, operatorName,
                    networkName, brandName, technology,mccConfiguration.getCountry());

            mccConfiguration.addNetworkInfo(networkConfiguration);
        }

        LogManager.getLogger().debug(MODULE, "Reading Network Configuration completed");
        return tempCountryInformationMap;
    }

    private Map<String, LocationInformationConfiguration> readLocationInformation(List<AreaData> areaDatas, Map<Integer, MccConfiguration> countryInformationMap,Map<String, Country> countryByCode) {
        LogManager.getLogger().info(MODULE, "start reading geographical location information");
        Map<String, LocationInformationConfiguration> tempLocationsByAreaName = new HashMap<>();
        for (AreaData areaData : areaDatas) {
            LocationInformationConfiguration locationInformation = createLocationInformation(areaData,countryByCode);
            tempLocationsByAreaName.put(locationInformation.getArea(), locationInformation);

            NetworkData networkData = areaData.getNetworkData();
            int mccCode = networkData.getMcc();
            int mncCode = networkData.getMnc();

            MccConfiguration mccConfigurationData = countryInformationMap.get(mccCode);
            if (mccConfigurationData == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
                    LogManager.getLogger().error(MODULE, "MCC data not found for mcc: " + mccCode);
                }
                continue;
            }
            NetworkConfiguration networkConfiguration = mccConfigurationData.getNetworkData(mncCode);
            if (networkConfiguration == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
                    LogManager.getLogger().error(MODULE, "Network data not found for mnc: " + mncCode);
                }
                continue;
            }
            networkConfiguration.addLocationInformation(locationInformation);
            List<LacInformationData> lacInformationDataList = areaData.getLacInformationDataList();

            List<LacConfiguration> lacConfigurations = readLACInformation(lacInformationDataList, locationInformation);
            for (LacConfiguration tempLacConfiguration : lacConfigurations) {
                networkConfiguration.addLacData(tempLacConfiguration);
            }
        }
        LogManager.getLogger().debug(MODULE, "Reading geographical location information completed");
        return tempLocationsByAreaName;
    }

    private List<LacConfiguration> readLACInformation(List<LacInformationData> lacInformationDataList,
                                                      LocationInformationConfiguration locationInformationConfiguration) {
        LogManager.getLogger().info(MODULE, "Start reading LAC information for Area: " + locationInformationConfiguration.getArea());
        List<LacConfiguration> lacInformations = new ArrayList<>();

        for (LacInformationData lacInformation : lacInformationDataList) {
            String lacInfoId = lacInformation.getId();
            long lacCode = lacInformation.getLac();
            String strCis = lacInformation.getCiList();
            String strRacs = lacInformation.getRacList();
            String strSacs = lacInformation.getSacList();
            String[] ciArray = splitStringValues(strCis);
            String[] raiArray = splitStringValues(strRacs);
            String[] saiArray = splitStringValues(strSacs);
            LacConfiguration lacConfiguration = new LacConfiguration(lacInfoId, lacCode);
            lacConfiguration.addLocationInformation(locationInformationConfiguration);
            addCI(locationInformationConfiguration, ciArray, lacConfiguration);
            addRAI(locationInformationConfiguration, raiArray, lacConfiguration);
            addSAI(locationInformationConfiguration, saiArray, lacConfiguration);
            lacInformations.add(lacConfiguration);
        }
        LogManager.getLogger().debug(MODULE, "Reading LAC information for Area: " + locationInformationConfiguration.getArea() + " completed");
        return lacInformations;
    }

    private void addCI(LocationInformationConfiguration locationInformationConfiguration, String[] ciArray, LacConfiguration lacConfiguration) {
        if (ciArray != null && ciArray.length >= 1) {
            for (int i = 0; i < ciArray.length; i++) {
                long ci = Long.parseLong(ciArray[i]);
                CGIConfiguration cgiConfiguration = new CGIConfiguration(ci, locationInformationConfiguration);
                lacConfiguration.addCgi(cgiConfiguration);
            }
        }
    }

    private void addRAI(LocationInformationConfiguration locationInformationConfiguration, String[] raiArray, LacConfiguration lacConfiguration) {
        if (raiArray != null && raiArray.length >= 1) {
            for (int i = 0; i < raiArray.length; i++) {
                long racCode = Long.parseLong(raiArray[i]);
                RAIConfiguration raiConfiguration = new RAIConfiguration(racCode, locationInformationConfiguration);
                lacConfiguration.addRai(raiConfiguration);
            }
        }
    }

    private void addSAI(LocationInformationConfiguration locationInformationConfiguration, String[] saiArray, LacConfiguration lacConfiguration) {
        if (saiArray != null && saiArray.length >= 1) {
            for (int i = 0; i < saiArray.length; i++) {
                long sacCode = Long.parseLong(saiArray[i]);
                SAIConfiguration saiConfiguration = new SAIConfiguration(sacCode, locationInformationConfiguration);
                lacConfiguration.addSai(saiConfiguration);
            }
        }
    }

    private LocationInformationConfiguration createLocationInformation(AreaData areaData, Map<String, Country> countryByCode) {
        String locationId = areaData.getId();
        String area = areaData.getName();
        CityData cityData = areaData.getCityData();
        String cityName = cityData.getName();
        RegionData regionData = cityData.getRegionData();
        String regionName = regionData.getName();
        Country country = countryByCode.get(regionData.getCountryData().getCode());
        String param1 = areaData.getParam1();
        String param2 = areaData.getParam2();
        String param3 = areaData.getParam3();
        int congestionStatus = 0;
        if (areaData.getCongestionStatus() != null) {
            congestionStatus = areaData.getCongestionStatus();
        }
        return new LocationInformationConfiguration(locationId, area, cityName, regionName, country, congestionStatus, param1, param2, param3);
    }

    private String[] splitStringValues(String strValue) {
        if (strValue == null || strValue.length() == 0) {
            return new String[0];
        }
        return strValue.trim().split("[;|,]");
    }

    private MccConfiguration createMCC(NetworkData networkData,Map<String, Country> countyByCode) {
        CountryData countryData = networkData.getCountryData();
        Country country = countyByCode.get(countryData.getCode());
        int mccCode = networkData.getMcc();
        return new MccConfiguration(country,mccCode);
    }

    public MccConfiguration getCountryInformationByMCCCode(int mcc) {
        return this.countryInformationMap.get(mcc);
    }

    public LocationInformationConfiguration getLocationInformationbyAreaName(String areaName) {
        return this.locationsByAreaName.get(areaName);
    }

    public NetworkConfiguration getNetworkByMccMnc(String mccMnc) {
        return networkInfoByMccMnc.get(mccMnc);
    }

    public NetworkConfiguration getNetworkConfigurationById(String id) {
        return networkInfoById.get(id);
    }
}
