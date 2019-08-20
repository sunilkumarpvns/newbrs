package com.elitecore.netvertex.core.locationmanagement.util;

import com.elitecore.corenetvertex.sm.location.geography.GeographyData;
import com.elitecore.corenetvertex.sm.routing.network.BrandData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;

import java.util.List;
import java.util.UUID;

public class NetworkDataBuilder {


    HibernateSessionFactory hibernateSessionFactory;

    public NetworkDataBuilder(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    private CountryData getCountryData(String countryName, String countryCode) {
        CountryData india = new CountryData();
        india.setId(UUID.randomUUID().toString());
        india.setCode(countryCode);
        india.setName(countryName);
        return india;
    }


    private BrandData getBrandData(String brandName) {
        BrandData brandData = new BrandData();
        brandData.setId(UUID.randomUUID().toString());
        brandData.setName(brandName);
        return brandData;
    }

    private OperatorData getOperatorData(String name) {
        OperatorData operatorData = new OperatorData();
        operatorData.setId(UUID.randomUUID().toString());
        operatorData.setName(name);
        return operatorData;
    }

    public NetworkData getNetworkWithBasicInfo(CountryData countryData, BrandData brandData, OperatorData operatorData) {
        NetworkData networkData = new NetworkData();
        networkData.setId(UUID.randomUUID().toString());
        networkData.setCountryData(countryData);
        networkData.setBrandData(brandData);
        networkData.setOperatorData(operatorData);
        networkData.setTechnology("GSM");
        return networkData;
    }


    public CountryData saveCountryData(String countryName, String countryCode) {
        CountryData countryData = getCountryData(countryName, countryCode);
        hibernateSessionFactory.save(countryData);
        return countryData;
    }

    public GeographyData saveGeographyData(String geographyName, List<CountryData> countryDataList){
        GeographyData geographyData = getGeographyData(geographyName, countryDataList);
        hibernateSessionFactory.save(geographyData);
        return geographyData;
    }

    private GeographyData getGeographyData(String geographyName, List<CountryData> countryDataList) {
        GeographyData asia = new GeographyData();
        asia.setId(UUID.randomUUID().toString());
        asia.setName(geographyName);
        asia.setGeographyCountryRelDatas(countryDataList);
        return asia;
    }

    public BrandData saveBrandData(String brandName) {
        BrandData brandData = getBrandData(brandName);
        hibernateSessionFactory.save(brandData);
        return brandData;
    }

    public OperatorData saveOperatorData(String operatorName) {
        OperatorData operatorData = getOperatorData(operatorName);
        hibernateSessionFactory.save(operatorData);
        return operatorData;
    }

    public NetworkData createNetworkWithNetworkDetails(int mcc, int mnc, String networkname, NetworkData network) {
        network.setName(networkname);
        network.setMcc(mcc);
        network.setMnc(mnc);
        hibernateSessionFactory.save(network);
        return network;
    }
}
