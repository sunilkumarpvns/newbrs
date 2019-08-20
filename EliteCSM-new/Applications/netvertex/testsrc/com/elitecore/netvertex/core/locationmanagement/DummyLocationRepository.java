package com.elitecore.netvertex.core.locationmanagement;

import com.elitecore.netvertex.core.locationmanagement.data.LocationInformationConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.MccConfiguration;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DummyLocationRepository implements LocationRepository{
    private Map<Integer, MccConfiguration> mccConfigurationMap;
    private Map<String, NetworkConfiguration> networkConfigurationByMccMnc;
    private Map<String, LocationInformationConfiguration> locationsByAreaName;
    private Map<String, LocationInformationConfiguration> locationByCGI;
    private Map<String, LocationInformationConfiguration> locationByRAC;
    private Map<String, LocationInformationConfiguration> locationBySAC;

    public static DummyLocationRepository spy() {
        return Mockito.spy(new DummyLocationRepository());
    }

    public DummyLocationRepository() {
        mccConfigurationMap = new HashMap<>();
        locationsByAreaName = new HashMap<>();
        locationByCGI = new HashMap<>();
        locationByRAC = new HashMap<>();
        locationBySAC = new HashMap<>();
        networkConfigurationByMccMnc = new HashMap<>();

    }

    public MccConfiguration spyNetworkConfWithMCCCode(int mccCode) {
        MccConfiguration mccConfiguration = mock(MccConfiguration.class);
        mccConfigurationMap.put(mccCode, mccConfiguration);
        return mccConfiguration;
    }

    public LocationInformationConfiguration spylocationConfByAreaNameWithAreaName(String areaName) {
        LocationInformationConfiguration locationConfiguration = mock(LocationInformationConfiguration.class);
        locationsByAreaName.put(areaName, locationConfiguration);
        return locationConfiguration;
    }

    public LocationInformationConfiguration spylocationConfByCGI(String ci) {
        LocationInformationConfiguration locationConfiguration = mock(LocationInformationConfiguration.class);
        locationByCGI.put(ci, locationConfiguration);
        return locationConfiguration;
    }

    public LocationInformationConfiguration spylocationConfByRAC(String rac) {
        LocationInformationConfiguration locationConfiguration = mock(LocationInformationConfiguration.class);
        locationByRAC.put(rac, locationConfiguration);
        return locationConfiguration;
    }

    public LocationInformationConfiguration spylocationConfBySAC(String sac) {
        LocationInformationConfiguration locationConfiguration = mock(LocationInformationConfiguration.class);
        locationBySAC.put(sac, locationConfiguration);
        return locationConfiguration;
    }


    public NetworkConfiguration spyNetworkConfigurationByMCCMNC(String mcc, String mnc,String country,String network,String operator,String brand,String geography){
        MccConfiguration mccConfiguration = mock(MccConfiguration.class);
        when(mccConfiguration.getCountryName()).thenReturn(country);
        NetworkConfiguration networkConfiguration = mock(NetworkConfiguration.class);
        when(networkConfiguration.getMNC()).thenReturn(Integer.parseInt(mnc));
        when(networkConfiguration.getNetworkName()).thenReturn(network);
        when(networkConfiguration.getBrand()).thenReturn(brand);
        when(networkConfiguration.getCountryName()).thenReturn(country);
        when(networkConfiguration.getGeography()).thenReturn(geography);
        mccConfiguration.addNetworkInfo(networkConfiguration);
        when(mccConfiguration.getNetworkData(Integer.parseInt(mnc))).thenReturn(networkConfiguration);
        mccConfigurationMap.put(Integer.parseInt(mcc), mccConfiguration);
        networkConfigurationByMccMnc.put(String.valueOf(mcc)+String.valueOf(mnc), networkConfiguration);
        return networkConfiguration;
    }

    @Override
    public MccConfiguration getMCCConfigurationByMCCCode(String mcc) {
        return mccConfigurationMap.get(Integer.parseInt(mcc));
    }



    @Override
    public NetworkConfiguration getNetworkInformationByMCCMNC(String mccMnc) {
        return networkConfigurationByMccMnc.get(mccMnc);
    }

    @Override
    public NetworkConfiguration getNetworkInformationById(String id) {
        return null;
    }


    @Override
    public LocationInformationConfiguration getLocationInformationByCGI(String mcc, String mnc, String lac, String ci) {
        return locationByCGI.get(ci);
    }

    @Override
    public LocationInformationConfiguration getLocationInformationBySAI(String mcc, String mnc, String lac, String sac) {
        return locationBySAC.get(sac);
    }

    @Override
    public LocationInformationConfiguration getLocationInformationByRAI(String mcc, String mnc, String lac, String rac) {
        return locationByRAC.get(rac);
    }

    @Override
    public LocationInformationConfiguration getLocationInformationByAreaName(String areaName) {
        return locationsByAreaName.get(areaName);
    }
}
