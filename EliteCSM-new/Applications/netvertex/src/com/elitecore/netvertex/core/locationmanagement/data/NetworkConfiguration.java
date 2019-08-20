package com.elitecore.netvertex.core.locationmanagement.data;


import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class NetworkConfiguration implements ToStringable {

    private String networkId;
    private int mnc;
    private String operator;
    private String brand;
    private String technology;
    private String networkName;
    private Country country;
    private HashSet<LocationInformationConfiguration> locationInformationConfigurationSet;
    private Map<Long, LacConfiguration> lacDataMap;
    private HashSet<String> regionSet;
    private HashSet<String> citySet;


    public NetworkConfiguration(String networkId, int mnc, String operator,
                                String networkName, String brand, String technology, Country country) {
        this.networkId = networkId;
        this.mnc = mnc;
        this.operator = operator;
        this.networkName = networkName;
        this.brand = brand;
        this.technology = technology;
        this.country = country;
        lacDataMap = new HashMap<Long, LacConfiguration>();
        locationInformationConfigurationSet = new HashSet<LocationInformationConfiguration>();
        regionSet = new HashSet<String>();
        citySet = new HashSet<String>();


    }

    public String getBrand() {
        return brand;
    }

    public String getTechnology() {
        return technology;
    }

    public String getNetworkId() {
        return networkId;
    }

    public int getMNC() {
        return mnc;
    }

    public String getOperator() {
        return operator;
    }

    public String getNetworkName() {
        return networkName;
    }

    public String getCountryName() {
        return country.getName();
    }

    public String getGeography() {
        return country.getGeography();
    }

    public Country getCountry() {
        return country;
    }

    public void addLocationInformation(LocationInformationConfiguration locationInformationConfiguration) {
        locationInformationConfigurationSet.add(locationInformationConfiguration);
        regionSet.add(locationInformationConfiguration.getRegion());
        citySet.add(locationInformationConfiguration.getCity());

    }

    public Set<LocationInformationConfiguration> getLocationInformationConfigurationSet() {
        return (Set<LocationInformationConfiguration>) locationInformationConfigurationSet.clone();
    }


    public void addLacData(LacConfiguration lacConfiguration) {
        lacDataMap.put(lacConfiguration.getLacCode(), lacConfiguration);
    }

    public LacConfiguration getLacData(Long lacCode) {
        return lacDataMap.get(lacCode);

    }

    public Set<String> getRegionSet() {
        return (Set<String>) regionSet.clone();
    }

    public Set<String> getCitySet() {
        return (Set<String>) citySet.clone();
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Network Information -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.newline().append("Network ID", networkId).
                append("MNC", mnc)
                .append("Operator", operator)
                .append("Network name", networkName)
                .append("Brand", brand)
                .append("Technology", technology)
                .appendChildObject("LAC", lacDataMap.values())
                .appendChild("Location", locationInformationConfigurationSet.stream().map(LocationInformationConfiguration::getLocationId).collect(Collectors.toList()))
                .appendChild("Region", regionSet)
                .appendChild("City", citySet).append("Country",country.getName())
                .append("Geography",country.getGeography());
    }
}
