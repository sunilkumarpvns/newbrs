package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.conf.SyCounterQuotaProfileConf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyCounterQuotaProfileDetailFactory {

    private String serviceId;
    private int fupLevel;
    private List<SyCounterQuotaProfileConf.CouterDetail> counterDetails;

    public SyCounterQuotaProfileDetailFactory(String serviceId) {
        this.serviceId = serviceId;
        this.counterDetails = new ArrayList<SyCounterQuotaProfileConf.CouterDetail>();
    }

    public static SyCounterQuotaProfileDetailFactory create(String serviceId) {
        return new SyCounterQuotaProfileDetailFactory(serviceId);
    }

    public SyCounterQuotaProfileDetailFactory hqs() {
        fupLevel = 0;
        return this;
    }

    public SyCounterQuotaProfileDetailFactory fup1() {
        fupLevel = 1;
        return this;
    }

    public SyCounterQuotaProfileDetailFactory fup2() {
        fupLevel = 1;
        return this;
    }

    public SyCounterQuotaProfileDetailFactory levelHigherThan(SyCounterBaseQuotaProfileDetail counterBaseQuotaProfileDetail) {
        fupLevel = counterBaseQuotaProfileDetail.getFupLevel() + 1 ;
        return this;
    }

    public SyCounterQuotaProfileDetailFactory levelLowerThan(SyCounterBaseQuotaProfileDetail counterBaseQuotaProfileDetail) {
        fupLevel = counterBaseQuotaProfileDetail.getFupLevel() - 1 ;
        return this;
    }

    public SyCounterQuotaProfileDetailFactory levelSameAs(SyCounterBaseQuotaProfileDetail counterBaseQuotaProfileDetail) {
        fupLevel = counterBaseQuotaProfileDetail.getFupLevel();
        return this;
    }

    public SyCounterQuotaProfileDetailFactory optionalCounter(String key, String value) {
        counterDetails.add(new SyCounterQuotaProfileConf.CouterDetail(key, value, false));
        return this;
    }

    public SyCounterQuotaProfileDetailFactory mandatoryCounter(String key, String value) {
        counterDetails.add(new SyCounterQuotaProfileConf.CouterDetail(key, value, true));
        return this;
    }



    public SyCounterBaseQuotaProfileDetail build() {
        String quotaProfileId = UUID.randomUUID().toString();
        String packageId = UUID.randomUUID().toString();
        return new SyCounterBaseQuotaProfileDetail(quotaProfileId, quotaProfileId, packageId, serviceId, serviceId, fupLevel,
                this.counterDetails);
    }
}
