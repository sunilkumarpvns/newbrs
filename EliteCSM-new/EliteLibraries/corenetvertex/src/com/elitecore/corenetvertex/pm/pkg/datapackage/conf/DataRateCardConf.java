package com.elitecore.corenetvertex.pm.pkg.datapackage.conf;

import java.util.List;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;

public class DataRateCardConf {
    private String id;
    private String name;
    private String keyOne;
    private String keyTwo;
    private Uom pulseUom;
    private Uom rateUom;
    private List<RateCardVersion> dataRateCardVersions;

    public DataRateCardConf(String id, String name,
                            String keyOne,
                            String keyTwo,
                            List<RateCardVersion> dataRateCardVersions, Uom pulseUom, Uom rateUom) {
        this.id = id;
        this.name = name;
        this.keyOne = keyOne;
        this.keyTwo = keyTwo;
        this.pulseUom = pulseUom;
        this.rateUom = rateUom;
        this.dataRateCardVersions = dataRateCardVersions;
    }

    public String getName() {
        return name;
    }

    public String getKeyOne() {
        return keyOne;
    }

    public String getKeyTwo() {
        return keyTwo;
    }

    public List<RateCardVersion> getDataRateCardVersions() {
        return dataRateCardVersions;
    }

    public String getId() {
        return id;
    }

    public Uom getPulseUom() {
        return pulseUom;
    }

    public Uom getRateUom() {
        return rateUom;
    }
}
