package com.elitecore.netvertex.core.locationmanagement.data;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MccConfiguration implements ToStringable {
    private Country country;
    private int mccCode;
    private Map<Integer, NetworkConfiguration> networkInfoMap;


    public MccConfiguration(Country country, int mccCode) {
        super();
        this.country = country;
        this.mccCode = mccCode;
        networkInfoMap = new HashMap<>();

    }

    /**
     * @return the countryId
     */
    public String getCountryId() {
        return country.getId();
    }

    /**
     * @return the countryName
     */
    public String getCountryName() {
        return country.getName();
    }

    /**
     * @return the mccCode
     */
    public int getMccCode() {
        return mccCode;
    }


    public Collection<NetworkConfiguration> getAllNetworkInfos() {
        return networkInfoMap.values();
    }


    public NetworkConfiguration getNetworkData(int mnc) {

        return networkInfoMap.get(mnc);
    }

    public void addNetworkInfo(NetworkConfiguration networkConfiguration) {
        networkInfoMap.put(networkConfiguration.getMNC(), networkConfiguration);
    }

    public Country getCountry() {
        return country;
    }


    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- MCC(Country) Information -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {

        builder.newline()
                .append("Country Id", country.getId())
                .append("Country Name", country.getName())
                .append("MCC Code", mccCode)
                .appendChildObject("Network Configuration", networkInfoMap.values());

    }
}
